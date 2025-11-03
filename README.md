# Three-Kid Family Challenge

REST API for pattern matching in family relationships. Identifies families where a person has a partner, exactly 3 children with that partner, and at least one child under 18.

## Requirements

- Java 21
- Maven 3.x

## Quick Start

```bash
# Run the application
mvn spring-boot:run

# Run tests
mvn test
```

The API starts on `http://localhost:8080`

## API Endpoints

- `POST /api/v1/people` - Add or update a person (returns matching persons or 444)
- `DELETE /api/v1/people` - Delete persons by IDs (body: JSON array of IDs)

## Configuration

Strategy implementations can be configured in `application.properties`:

```properties
family-challenge.partner-validation=...  # How to validate partner existence
family-challenge.child-count=...          # How to count children (inclusive/exclusive)
family-challenge.age-validation=...       # How to handle null birthDates
family-challenge.cascade-delete=...       # How to handle deleted ID references
```

## Key Assumptions

- **Data fields**: Name and birthDate can be null (partial data allowed)
- **Partner validation**: Reference-based (partner ID counts even if not POSTed)
- **Child counting**: Inclusive (partner can have additional children with others)
- **Age validation**: Pessimistic (null birthDate doesn't satisfy "under 18")
- **Relationships**: Auto-repaired to maintain bidirectional integrity
- **Storage**: In-memory (data lost on restart)

See `adr/04-handling-requirement-ambiguities.md` for detailed architectural decisions.

## Project Structure

```
src/main/java/nl/pinkroccade/familychallenge/
├── controller/     # REST endpoints
├── service/        # Business logic with strategy pattern
├── domain/         # Person entity
├── repository/     # In-memory storage
└── config/         # Strategy configuration
```

