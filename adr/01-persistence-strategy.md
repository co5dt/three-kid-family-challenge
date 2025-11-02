# ADR 001: Persistence Strategy for Three-Kid Family Challenge

## Status

Accepted

## Functional Requirements:

- Store person records with bidirectional relationships (parent-child, partner)
- Support upsert operations (same ID = update)
- Immediate pattern matching after each insert/update
- All filtering must happen in Java (no SQL WHERE clauses)
- Phase 2: DELETE with permanent ignore-list

## Non-Functional Requirements:

- Performance: Must respond immediately after each POST
- Testability: Need unit & integration tests
- Simplicity: 2-hour challenge timeframe
- Memory constraints: Unknown dataset size

## Options Considered

- HashMap
- H2 In-Memory Database
- de.flapdoodle
- TestContainers + PostgreSQL/Mongo

| Criteria                    | HashMap | H2   | Postgres+TC | MongoDB |
|-----------------------------|---------|------|-------------|---------|
| **Speed**                   | +++++   | +++  | ++          | +++     |
| **Simplicity**              | +++++   | +++  | ++          | +++     |
| **Graph fit**               | +++++   | ++   | ++          | ++++    |
| **Enforces Java filtering** | +++++   | ++   | ++          | +++     |
| **Test ease**               | +++++   | ++++ | ++          | ++++    |
| **2-hour feasible**         | +++++   | +++  | +           | +++     |
| **Production ready**        | +       | ++++ | +++++       | ++++    |

## Decision

**Selected: Option 1 - In-Memory HashMap**

### Rationale

1. **Matches Challenge Nature**: This is a coding challenge, not a production system. The requirement explicitly
   states "all filtering in Java" which strongly suggests they want to see algorithm skills, not database query
   optimization.
2. **Performance**: HashMap provides O(1) access by ID, enabling the fastest possible pattern matching implementation.
   Since pattern checks happen after *every* POST request, this is critical.
3. **Graph Structure**: The domain is inherently a graph. HashMap with ID references (`Long parent1Id`) is perfect for
   graph traversal without ORM complexity.
4. **Time Constraint**: Can implement full solution including tests in 2 hours. Database setup would consume significant
   time.
5. **Bidirectional Integrity**: Manual enforcement in Java is straightforward with HashMap and demonstrates programming
   skill.
6. **No Persistence Requirement**: The challenge never mentions restarting the service or data durability. It's focused
   on algorithmic correctness and performance.
