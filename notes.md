# Personal Notes during coding

## On `ComputerRepository.fetch()`

The definition return a `F[Computer]` but 
I was thinking of modifying it to return a `F[Option[Computer]]`
and managed the `None` state either in the `ComputerDatabaseRoutes`
or in a newly created `ComputerDatabaseService` that will manage
the business logic of not finding the requested resource.

I was also trying to embed the resource parsing within a for yield with no success.
So i fallback to the existing codebase.