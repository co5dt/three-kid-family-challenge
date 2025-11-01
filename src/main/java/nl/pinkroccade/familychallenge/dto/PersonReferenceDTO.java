package nl.pinkroccade.familychallenge.dto;

/**
 * Simple reference to a person by ID.
 * Used in JSON payloads to represent relationships.
 */
public record PersonReferenceDTO(Long id) {
    
    public PersonReferenceDTO {
        if (id == null) {
            throw new IllegalArgumentException("Person reference ID cannot be null");
        }
    }
}
