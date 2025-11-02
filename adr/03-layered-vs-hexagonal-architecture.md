# ADR 003: Layered vs. Hexagonal Architecture

## Status

Accepted

## Requirements:

- Clear separation of concerns
- Easy to understand for challenge reviewers
- Testable components
- Completable in 2-hour timeframe
- Support for swappable persistence (in-memory → JPA)

## Options Considered

1. Layered Architecture (Traditional 3-tier)
2. Hexagonal Architecture (Ports & Adapters)
3. Clean Architecture (Uncle Bob)

## Comparison

| Criteria                  | Layered | Hexagonal | Clean |
|---------------------------|---------|-----------|-------|
| **Simplicity**            | +++++   | +++       | ++    |
| **Setup time**            | +++++   | +++       | +     |
| **Challenge appropriate** | +++++   | +++       | ++    |
| **Test isolation**        | ++++    | +++++     | +++++ |
| **Dependency control**    | +++     | +++++     | +++++ |
| **Overengineering risk**  | +       | ++++      | +++++ |
| **Spring Boot standard**  | +++++   | +++       | ++    |

## Decision

Selected: Option 1 - Layered Architecture

1. **Challenge Context**: For a 2-hour coding challenge, reviewers expect straightforward code. Controller → Service →
   Repository is universally understood.
2. **Time Efficiency**: No need for ports/adapters abstractions or use case classes. Can focus on business logic instead
   of architecture ceremony.
3. **Sufficient Abstraction**: Repository interface already enables swappable persistence. PatternMatchingService is
   decoupled from data access.
4. **No Complex Domain**: The business logic (pattern matching) is simple enough that hexagonal ports or use case
   objects would be overkill.
5. **Standard Practice**: Layered is the default Spring Boot approach. Deviation needs justification - which this
   challenge doesn't require.
