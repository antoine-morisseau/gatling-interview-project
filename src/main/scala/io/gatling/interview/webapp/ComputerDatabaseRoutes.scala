package io.gatling.interview.webapp

import cats.effect._
import cats.implicits.{toBifunctorOps, toTraverseOps}
import io.gatling.interview.api._
import io.gatling.interview.repository.ComputerRepository
import io.gatling.interview.{api, model}

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util.Try

object ComputerDatabaseRoutes {
  private val localDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  private def localDateToString(value: LocalDate): String = localDateFormatter.format(value)
  private def stringToLocalDate(value: String): Either[InvalidDateFormatError, LocalDate] = Try(
    LocalDate.parse(value, localDateFormatter)
  ).toEither.leftMap(_ => InvalidDateFormatError())

  private def transformComputer(value: model.Computer): api.Computer = api.Computer(
    id = value.id,
    name = value.name,
    introduced = value.introduced.map(localDateToString),
    discontinued = value.discontinued.map(localDateToString)
  )
}

class ComputerDatabaseRoutes(repository: ComputerRepository[IO])
    extends ComputerDatabaseEndpoints[IO] {
  import ComputerDatabaseRoutes._

  override def listComputers(): IO[ComputersOutput] = for {
    result <- repository.fetchAll()
  } yield ComputersOutput(result.map(transformComputer))

  override def getComputer(id: Long): IO[Computer] = for {
    computer <- repository.fetch(id)
  } yield transformComputer(computer)

  override def createComputer(
      name: String,
      introduced: Option[String],
      discontinued: Option[String]
  ): IO[Computer] = for {
    introducedLocalDate <- IO.fromEither(introduced.traverse(stringToLocalDate))
    discontinuedLocalDate <- IO.fromEither(discontinued.traverse(stringToLocalDate))
    allComputers <- repository.fetchAll()
    nextId = allComputers.maxByOption(_.id).map(_.id + 1L).getOrElse(1L)
    computer = model.Computer(nextId, name, introducedLocalDate, discontinuedLocalDate)
    _ <- repository.insert(computer)
  } yield transformComputer(computer)
}
