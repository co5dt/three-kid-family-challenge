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
 * <p><b>Pattern requirements (strategy-dependent):</b></p>
 * <ul>
 *   <li>Person has a valid partner (interpretation depends on {@link PartnerValidationStrategy})</li>
 *   <li>Person has exactly 3 children with partner (interpretation depends on {@link ChildCountStrategy})</li>
 *   <li>All 3 children list both person and partner as parents</li>
 *   <li>At least one child is under 18 (interpretation depends on {@link AgeValidationStrategy})</li>
 * </ul>
 *
 * <p><b>Strategy-based Design (ADR-04):</b></p>
 * <p>Uses three pluggable strategies to handle requirement ambiguities.
 * Strategies are configured in application.properties.</p>
 */
@Service
public class PatternMatchingService {

    private final PersonRepository          repository;
    private final PartnerValidationStrategy partnerValidationStrategy;
    private final ChildCountStrategy        childCountStrategy;
    private final AgeValidationStrategy     ageValidationStrategy;

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
     * Time complexity: O(n * k) where n = total people, k = children per person
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
        // DECISION: ADR-04 #3 (confirmed) - Delegated to PartnerValidationStrategy
        // Must have a valid partner according to configured strategy
        if (!partnerValidationStrategy.hasValidPartner(person, repository)) {
            return false;
        }

        Long partnerId = person.getPartnerId();

        // DECISION: ADR-04 #2 (confirmed) - Delegated to ChildCountStrategy
        // Must have valid children according to configured strategy
        ChildCountStrategy.ValidationResult childValidation =
                childCountStrategy.validateChildren(person, partnerId, repository);

        if (!childValidation.valid()) {
            return false;
        }

        Set<Long> validChildrenIds = childValidation.validChildrenIds();

        // DECISION: ADR-04 #4 (OTI - chosen) - Delegated to AgeValidationStrategy
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

