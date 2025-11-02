package nl.pinkroccade.familychallenge.mapper;

import nl.pinkroccade.familychallenge.domain.Person;
import nl.pinkroccade.familychallenge.dto.PersonReferenceDTO;
import nl.pinkroccade.familychallenge.dto.PersonRequestDTO;
import nl.pinkroccade.familychallenge.dto.PersonResponseDTO;
import nl.pinkroccade.familychallenge.repository.PersonRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Maps between Person domain objects and DTOs.
 */
public class PersonMapper {

    /**
     * Converts PersonRequestDTO to Person domain object.
     */
    public static Person toDomain(PersonRequestDTO dto) {
        Person person = new Person();
        person.setId(dto.id());
        person.setName(dto.name());
        person.setBirthDate(dto.birthDate());
        person.setParent1Id(dto.parent1() != null ? dto.parent1().id() : null);
        person.setParent2Id(dto.parent2() != null ? dto.parent2().id() : null);
        person.setPartnerId(dto.partner() != null ? dto.partner().id() : null);

        if (dto.children() != null) {
            Set<Long> childrenIds = dto.children().stream()
                    .map(PersonReferenceDTO::id)
                    .collect(Collectors.toSet());
            person.setChildrenIds(childrenIds);
        }

        return person;
    }

    /**
     * Converts Person domain object to PersonResponseDTO.
     * Resolves references to other entities via repository.
     */
    public static PersonResponseDTO toResponseDTO(Person person, PersonRepository repository) {
        return new PersonResponseDTO(
                person.getId(),
                person.getName(),
                person.getBirthDate(),
                toReference(person.getParent1Id(), repository),
                toReference(person.getParent2Id(), repository),
                toReference(person.getPartnerId(), repository),
                toReferenceList(person.getChildrenIds())
        );
    }

    private static PersonReferenceDTO toReference(Long id, PersonRepository repository) {
        if (id == null) {
            return null;
        }
        // Return reference with just ID - name/details fetched if needed
        return new PersonReferenceDTO(id);
    }

    private static List<PersonReferenceDTO> toReferenceList(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return ids.stream()
                .map(PersonReferenceDTO::new)
                .toList();
    }
}

