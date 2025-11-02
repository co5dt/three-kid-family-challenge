package nl.pinkroccade.familychallenge.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

/**
 * Incoming POST request payload for person data.
 * <p>
 * Represents the JSON structure received from clients.
 * All relationship fields are optional as records may be incomplete.
 */
public record PersonRequestDTO(
        @NotNull(message = "Person ID is required")
        Long id,
        String name,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate birthDate,
        PersonReferenceDTO parent1,
        PersonReferenceDTO parent2,
        PersonReferenceDTO partner,
        List<PersonReferenceDTO> children
) {
}

