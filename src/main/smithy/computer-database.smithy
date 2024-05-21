$version: "2"

namespace io.gatling.interview.api

use alloy#simpleRestJson

@simpleRestJson
service ComputerDatabaseEndpoints {
  version: "1.0.0",
  operations: [ListComputers, GetComputer]
}

@readonly
@http(method: "GET", uri: "/computers", code: 200)
operation ListComputers {
  output: ComputersOutput
}

@readonly
@http(method: "GET", uri: "/computers/{id}", code: 200)
operation GetComputer {
  input: GetComputerInput,
  output: Computer,
  errors: [ComputerNotFoundError]
}

@input
structure GetComputerInput {
  @required
  @httpLabel
  id: Long
}

@error("client")
@httpError(404)
structure ComputerNotFoundError {
  @required
  message: String,

  @required
  i18nCode: String = "COMPUTER_NOT_FOUND"
}

structure ComputersOutput {
  @required
  computers: Computers
}

list Computers {
  member: Computer
}

structure Computer {
  @required
  id: Long,

  @required
  name: String,

  introduced: String,

  discontinued: String,
}
