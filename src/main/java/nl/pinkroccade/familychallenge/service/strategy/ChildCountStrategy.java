package nl.pinkroccade.familychallenge.service.strategy;

import nl.pinkroccade.familychallenge.domain.Person;
import nl.pinkroccade.familychallenge.repository.PersonRepository;

import java.util.Set;

/**
 * Strategy for validating child count in pattern matching.
 * 
 * <p><b>AMBIGUITY #2 from ADR-04:</b> Partner's Additional Children in Blended Families</p>
 * <p>Question: The person must have exactly 3 children, and all 3 must list the same partner 
 * as parent. But what if the PARTNER has additional children with OTHER people?</p>
 * 
 * <p>Implementations:</p>
 * <ul>
 *   <li><b>Exclusive:</b> The partner must ONLY have these 3 children (no children with other partners)</li>
 *   <li><b>Inclusive:</b> The partner can have additional children with other people (supports blended families)</li>
 * </ul>
 * 
 * @see nl.pinkroccade.familychallenge.service.strategy.ExclusiveChildCountStrategy
 * @see nl.pinkroccade.familychallenge.service.strategy.InclusiveChildCountStrategy
 */
public interface ChildCountStrategy {
    
    /**
     * Result of child count validation.
     */
    record ValidationResult(boolean valid, Set<Long> validChildrenIds) {}
    
    /**
     * Validates if a person's children meet the pattern requirements.
     * 
     * @param person the person to check (must have exactly 3 children)
     * @param partnerId the partner ID to check children against
     * @param repository the repository to look up children and partner
     * @return validation result with valid flag and set of children IDs that meet criteria
     */
    ValidationResult validateChildren(Person person, Long partnerId, PersonRepository repository);
}

