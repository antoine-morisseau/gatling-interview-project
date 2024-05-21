# Personal Notes during coding

## On `ComputerRepository.fetch()`

The definition return a `F[Computer]` but 
I was thinking of modifying it to return a `F[Option[Computer]]`
and managed the `None` state either in the `ComputerDatabaseRoutes`
or in a newly created `ComputerDatabaseService` that will manage
the business logic of not finding the requested resource by raising an error.

I was also trying to embed the resource manipulation within a for yield with no success.
So i fallback to the existing codebase.

## On `ComputerRepository.insert()`

The unit test doesn't validate if i do a `fetch()` right after the `insert()`.
It doesn't write the given resource in the file even if i use a `.unsafeRunSync()` call on the `insert()`.

I'm curious to understand if it comes from the unit test behavior of managing IOs or something else.

Questions : 

- Was there a better way to insert data with jsoniter alone ?
- Can't we have a custom date validation directly in smithy4s ? 
  - OpenAPI allow the `date` format (https://spec.openapis.org/oas/v3.0.3#data-types) but smithy4s doesn't and the alloy @dateformat doesn't seem to validate the input
- Which stack of tracing/logging is used ?
- Where the logs are set and is there a convention/format ?
- How the security of endpoints is managed ?
