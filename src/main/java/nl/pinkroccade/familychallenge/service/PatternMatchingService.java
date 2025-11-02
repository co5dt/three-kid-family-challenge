package nl.pinkroccade.familychallenge.service;

import nl.pinkroccade.familychallenge.domain.Person;
import nl.pinkroccade.familychallenge.repository.PersonRepository;
import nl.pinkroccade.familychallenge.service.strategy.age.AgeValidationStrategy;
import nl.pinkroccade.familychallenge.service.strategy.children.ChildCountStrategy;
import nl.pinkroccade.familychallenge.service.strategy.partner.PartnerValidationStrategy;
import org.springframework.stereotype.Service;

import java.util.List;
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
 * 
 * <p><b>Strategy-based Design (ADR-04):</b></p>
 * <p>This service uses pluggable strategies to handle requirement ambiguities.
 * Strategies can be configured in application.properties without code changes.</p>
 */
@Service
public class PatternMatchingService {
    
    private final PersonRepository repository;
    private final PartnerValidationStrategy partnerValidationStrategy;
    private final ChildCountStrategy childCountStrategy;
    private final AgeValidationStrategy ageValidationStrategy;
    
    public PatternMatchingService(
            PersonRepository repository,
            PartnerValidationStrategy partnerValidationStrategy,
            ChildCountStrategy childCountStrategy,
            AgeValidationStrategy ageValidationStrategy) {
        this.repository = repository;
        this.partnerValidationStrategy = partnerValidationStrategy;
        this.childCountStrategy = childCountStrategy;
        this.ageValidationStrategy = ageValidationStrategy;
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
     * Checks if a person matches the pattern using configured strategies.
     * 
     * @param person The person to check
     * @return true if person matches the pattern
     */
    private boolean matchesPattern(Person person) {
        // ASSUMPTION: ADR-04 #3 - Delegated to PartnerValidationStrategy
        // Must have a valid partner according to configured strategy
        if (!partnerValidationStrategy.hasValidPartner(person, repository)) {
            return false;
        }
        
        Long partnerId = person.getPartnerId();
        
        // ASSUMPTION: ADR-04 #2 - Delegated to ChildCountStrategy
        // Must have valid children according to configured strategy
        ChildCountStrategy.ValidationResult childValidation = 
                childCountStrategy.validateChildren(person, partnerId, repository);
        
        if (!childValidation.valid()) {
            return false;
        }
        
        Set<Long> validChildrenIds = childValidation.validChildrenIds();
        
        // ASSUMPTION: ADR-04 #1 - Delegated to AgeValidationStrategy
        // At least one child must be under 18 according to configured strategy
        for (Long childId : validChildrenIds) {
            Person child = repository.findById(childId).orElse(null);
            if (child != null && ageValidationStrategy.isUnder18(child)) {
                return true; // Found at least one child under 18
            }
        }
        
        return false; // No child under 18 found
    }
}

