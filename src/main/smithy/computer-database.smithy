$version: "2"

namespace io.gatling.interview.api

use alloy#dateFormat
use alloy#simpleRestJson

@simpleRestJson
service ComputerDatabaseEndpoints {
  version: "1.0.0",
  operations: [ListComputers, GetComputer, CreateComputer]
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

@http(method: "POST", uri: "/computers", code: 201)
operation CreateComputer {
  input: CreateComputerInput,
  output: Computer,
  errors: [InvalidDateFormatError]
}

@input
structure GetComputerInput {
  @required
  @httpLabel
  id: Long
}

@input
structure CreateComputerInput {
  @required
  name: String,

  @dateFormat
  introduced: String,

  @dateFormat
  discontinued: String
}

@error("client")
@httpError(404)
structure ComputerNotFoundError {
  @required
  message: String,

  @required
  i18nCode: String = "COMPUTER_NOT_FOUND"
}

@error("client")
@httpError(400)
structure InvalidDateFormatError {
  @required
  message: String = "Date need to be of yyyy-MM-dd format"
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

  @dateFormat
  introduced: String,

  @dateFormat
  discontinued: String,
}
