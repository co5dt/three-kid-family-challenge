package nl.pinkroccade.familychallenge.service.strategy;

import nl.pinkroccade.familychallenge.domain.Person;

/**
 * Strategy for validating age of persons in pattern matching.
 * 
 * <p><b>AMBIGUITY #1 from ADR-04:</b> Data Validation Requirements</p>
 * <p>Question: If birthDate can be null, how should age be calculated for 
 * "at least one child under 18"? Should a child with null birthDate be assumed 
 * under 18, over 18, or excluded from matching?</p>
 * 
 * <p>Implementations:</p>
 * <ul>
 *   <li><b>Pessimistic:</b> Children with null birthDate are treated as "unknown age" 
 *       and don't satisfy the "under 18" criterion</li>
 *   <li><b>Optimistic:</b> Children with null birthDate are assumed to be under 18</li>
 * </ul>
 * 
 * @see nl.pinkroccade.familychallenge.service.strategy.PessimisticAgeValidation
 * @see nl.pinkroccade.familychallenge.service.strategy.OptimisticAgeValidation
 */
public interface AgeValidationStrategy {
    
    /**
     * Checks if a person is under 18 years old.
     * 
     * @param person the person to check
     * @return true if the person is under 18 according to this strategy
     */
    boolean isUnder18(Person person);
}

