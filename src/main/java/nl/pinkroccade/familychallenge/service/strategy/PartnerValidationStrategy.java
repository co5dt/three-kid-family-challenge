package nl.pinkroccade.familychallenge.service.strategy;

import nl.pinkroccade.familychallenge.domain.Person;
import nl.pinkroccade.familychallenge.repository.PersonRepository;

/**
 * Strategy for validating partner relationships in pattern matching.
 * 
 * <p><b>AMBIGUITY #3 from ADR-04:</b> Partner Reference vs. Partner Existence</p>
 * <p>Question: If a person references a partner ID that has never been POSTed, 
 * does that count as "has a partner" for pattern matching?</p>
 * 
 * <p>Implementations:</p>
 * <ul>
 *   <li><b>Reference-based:</b> Having a non-null partner field satisfies "has a partner"</li>
 *   <li><b>Existence-based:</b> The referenced partner must exist in the repository</li>
 * </ul>
 * 
 * @see nl.pinkroccade.familychallenge.service.strategy.ReferenceBasedPartnerValidation
 * @see nl.pinkroccade.familychallenge.service.strategy.ExistenceBasedPartnerValidation
 */
public interface PartnerValidationStrategy {
    
    /**
     * Validates if a person has a valid partner for pattern matching purposes.
     * 
     * @param person the person to check
     * @param repository the repository to look up referenced partners
     * @return true if the person has a valid partner according to this strategy
     */
    boolean hasValidPartner(Person person, PersonRepository repository);
}

