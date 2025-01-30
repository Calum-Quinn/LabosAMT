# Report

## Exercice 1 - Entity mappings

* Describe precisely from the perspective of SQL statements sent to the database, the difference between:
    * `ActorRepositoryTest.testUpdateActor`   
    * `ActorRepositoryTest.testUpdateActorWithoutFlushAndClear`
    * `ActorRepositoryTest.testUpdateActorWithoutClear`
 
For all three functions, the corresponding SQL commands contain an `INSERT` into the actor table. This is the insertion of the updated actor.

The second test, without the clear, does not have the `SELECT` command which gets the specified actor. 

The last test, without the clear or the flush, does not contain either the `SELECT` command or the `UPDATE` command which sets new values for the selected actor.

* Explain the behavior differences and why it occurs.

The entity manager's `flush` command forces force the data to be persisted in the database, therefore the result is the command in the output as it is being run immediately on the database.

The em's `clear` command clears the context. This means that the managed entities are detached which results in non flushed entities not being persisted.

*Hints: run the tests using the debugger, look at the SQL statements in the log.*

## Exercice 2 - Querying

**Report** on the query language that you prefer and why.

The preferred query language for us is SQL.

The main reason this is the case is that it removes the necessity for an extra layer of abstraction.

As JPQL interacts with an entity model instead of directly with the database, it can by slightly less efficient when it comes to the queries.
Although it can be better for abstraction and productivity, the extra layer of abstractions those tools providesintroduces expected behaviours at times, that can slow down, and increase verbosity. 

The Criteria API is not as legible as the two others and therefore it feels less easily usable in situations including multiple people on the same project.
But it may be at times the best solution for programmatic usages, or to create a domain specific (or plain custom) abstraction layer.
