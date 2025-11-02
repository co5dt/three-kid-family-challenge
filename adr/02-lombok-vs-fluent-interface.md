# ADR 002: Lombok vs Manual Fluent Interface

## Status

Accepted

## Requirements:

- Clean, readable code
- Easy to understand for reviewers
- No IDE plugin dependencies
- Support for Records (DTOs already use them)
- Minimal external dependencies

## Options Considered

1. Lombok
2. Manual Fluent Interface
3. Standard Getters/Setters (Current)

## Comparison

| Criteria                  | Lombok       | Fluent Manual | Standard   |
|---------------------------|--------------|---------------|------------|
| **No external deps**      | ❌            | ✅             | ✅          |
| **IDE independent**       | ❌            | ✅             | ✅          |
| **Compile-time magic**    | ❌ High       | ✅ None        | ✅ None     |
| **Debuggability**         | ⚠️ Generated | ✅ Clear       | ✅ Clear    |
| **Code clarity**          | ⚠️ Hidden    | ✅ Explicit    | ✅ Explicit |
| **Challenge appropriate** | ❌            | ✅             | ⚠️         |
| **Method chaining**       | ✅ @Builder   | ✅ Built-in    | ❌          |
| **Works with Records**    | ⚠️ Limited   | ✅             | ✅          |

## Decision

Selected: Option 2 - Manual Fluent Interface (for domain objects only)

1. No Hidden Magic: This is a code challenge where reviewers need to see actual code, not annotations that generate
   hidden methods.
2. Zero Dependencies: Lombok adds:
    - Build dependency
    - IDE plugin requirement
    - Compile-time annotation processing overhead
    - Maintenance burden
3. Challenge Context: For a 2-hour coding challenge, adding Lombok is overkill. The Person domain class is ~150 lines -
   manageable without code generation.
4. Fluent API Value: Method chaining improves test readability:
5. Records for DTOs: Our DTOs already use Java Records (immutable, concise) - no Lombok needed there.
