package nl.pinkroccade.familychallenge.service;

import nl.pinkroccade.familychallenge.domain.Person;
import nl.pinkroccade.familychallenge.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Service to detect if a person matches the three-kid family pattern.
 *
 * <p><b>Pattern requirements:</b></p>
 * <ul>
 *   <li>Person has a partner</li>
 *   <li>Person has exactly 3 children</li>
 *   <li>All 3 children list the same partner as either parent1 or parent2</li>
 *   <li>At least one of those children is under 18 years old</li>
 * </ul>
 */
@Service
public class PatternMatchingService {
    
    private final PersonRepository repository;
    
    public PatternMatchingService(PersonRepository repository) {
        this.repository = repository;
    }
    
    /**
     * Finds all people who match the three-kid family pattern.
     * Time complexity: O(n * k) where n = total people, k = children per person (max 3)
     * 
     * @return List of people who match the pattern
     */
    public List<Person> findMatches() {
        return repository.findAll().stream()
                .filter(this::matchesPattern)
                .toList();
    }
    
    /**
     * Checks if a person matches the pattern.
     * 
     * @param person The person to check
     * @return true if person matches the pattern
     */
    private boolean matchesPattern(Person person) {
        // Must have a partner
        if (person.getPartnerId() == null) {
            return false;
        }
        
        // Partner must exist in repository
        Long partnerId = person.getPartnerId();
        if (repository.findById(partnerId).isEmpty()) {
            return false;
        }
        
        // Must have exactly 3 children
        Set<Long> childrenIds = person.getChildrenIds();
        if (childrenIds == null || childrenIds.size() != 3) {
            return false;
        }
        boolean hasChildUnder18 = false;
        
        // Check all 3 children
        for (Long childId : childrenIds) {
            Person child = repository.findById(childId).orElse(null);
            
            if (child == null) {
                return false; // Child doesn't exist
            }
            
            // Child must list this person AND the partner as parents
            if (!isParentOf(person.getId(), child) || !isParentOf(partnerId, child)) {
                return false;
            }
            
            // Check if at least one child is under 18
            if (!hasChildUnder18 && isUnder18(child)) {
                hasChildUnder18 = true;
            }
        }
        return hasChildUnder18;
    }
    
    /**
     * Checks if personId is listed as parent1 or parent2 of the child.
     */
    private boolean isParentOf(Long personId, Person child) {
        return personId.equals(child.getParent1Id()) || personId.equals(child.getParent2Id());
    }
    
    /**
     * Checks if a person is under 18 years old.
     */
    private boolean isUnder18(Person person) {
        LocalDate now = LocalDate.now();
        LocalDate birthDate = person.getBirthDate();
        Objects.requireNonNull(birthDate, "Birth date required");
        if (birthDate.isAfter(now)) {
            throw new IllegalArgumentException("Birth date cannot be in future");
        }
        return Period.between(birthDate, now).getYears() < 18;
    }
}

