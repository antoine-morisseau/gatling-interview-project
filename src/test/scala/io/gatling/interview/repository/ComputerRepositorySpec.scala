package io.gatling.interview.repository

import cats.effect.{IO, Resource}
import cats.effect.testing.scalatest.AsyncIOSpec
import io.gatling.interview.api.ComputerNotFoundError
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers
import io.gatling.interview.model.Computer
import org.scalactic.source
import org.scalatest.compatible

import java.io.File
import java.nio.file.{Files, Path, StandardCopyOption}
import java.time.{LocalDate, Month}
import java.util.UUID
import scala.concurrent.Future

class ComputerRepositorySpec extends AsyncFlatSpec with AsyncIOSpec with Matchers {

  "ComputerRepository#fetchAll" should "retrieve all computers" in {
    val expectedComputers = Seq(
      Computer(id = 1, name = "MacBook Pro 15.4 inch", introduced = None, discontinued = None),
      Computer(
        id = 2,
        name = "CM-5",
        introduced = Some(LocalDate.of(1991, Month.JANUARY, 1)),
        discontinued = None
      ),
      Computer(
        id = 3,
        name = "Apple IIee",
        introduced = Some(LocalDate.of(2006, Month.JANUARY, 10)),
        discontinued = Some(LocalDate.of(2010, Month.JANUARY, 10))
      )
    )

    temporaryFileResource("computers/computers.json")
      .use { computersFilePath =>
        val repository = new ComputerRepository[IO](computersFilePath)
        repository.fetchAll()
      }
      .asserting { fetchedComputers =>
        fetchedComputers shouldBe expectedComputers
      }
  }

  "ComputerRepository#fetchAll" should "fail if the JSON file is invalid" in {
    temporaryFileResource("computers/computers-invalid.json")
      .use { computersFilePath =>
        val repository = new ComputerRepository[IO](computersFilePath)
        repository.fetchAll()
      }
      .assertThrows[Exception]
  }

  "ComputerRepository#fetch" should "retrieve a computer" in {
    val expectedComputer =
      Computer(id = 1, name = "MacBook Pro 15.4 inch", introduced = None, discontinued = None)

    temporaryFileResource("computers/computers.json")
      .use { computersFilePath =>
        val repository = new ComputerRepository[IO](computersFilePath)
        repository.fetch(expectedComputer.id)
      }
      .asserting { fetchedComputer =>
        fetchedComputer shouldBe expectedComputer
      }
  }

  "ComputerRepository#fetch" should "fail if the id doesn't exist" in {
    temporaryFileResource("computers/computers.json")
      .use { computersFilePath =>
        val repository = new ComputerRepository[IO](computersFilePath)
        repository.fetch(-1)
      }
      .assertThrows[ComputerNotFoundError]
  }

  "ComputerRepository#insert" should "insert a computer" in {
    val computer =
      Computer(id = 90, name = "MacBook Pro 15.4 inch", introduced = None, discontinued = None)

    temporaryFileResource("computers/computers.json").use { computersFilePath =>
      val repository = new ComputerRepository[IO](computersFilePath)
      repository.insert(computer)
    }.assertNoException
  }

  private def temporaryFileResource(path: String): Resource[IO, Path] =
    Resource(
      for {
        inputStream <- IO.delay(getClass.getClassLoader.getResourceAsStream(path))
        file <- IO.delay(File.createTempFile(UUID.randomUUID().toString, "tmp"))
        path = file.toPath
        _ <- IO.delay(file.deleteOnExit())
        _ <- IO.delay(Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING))
      } yield (path, IO.delay(file.delete()).as(()))
    )
}
