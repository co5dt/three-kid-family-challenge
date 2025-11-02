package nl.pinkroccade.familychallenge.service.strategy.cleanup;

import nl.pinkroccade.familychallenge.domain.Person;

import java.util.Set;

/**
 * Strategy for cleaning up references when persons are deleted.
 * 
 * <p><b>AMBIGUITY #6 from ADR-04:</b> DELETE Operation Cascade Behavior</p>
 * <p>Question 6a: When deleting IDs, should we remove those IDs from other 
 * people's partner/children/parent fields?</p>
 * <p>Question 6b: When a POST contains a reference to an ignored ID, should we 
 * store it as-is or convert it to null?</p>
 * 
 * <p>Implementations:</p>
 * <ul>
 *   <li><b>Cascade:</b> References to deleted IDs are removed/nullified during cleanup</li>
 *   <li><b>NoOp:</b> References are left as-is (forward references remain)</li>
 * </ul>
 * 
 * @see CascadeDeleteStrategy
 * @see NoOpDeleteStrategy
 */
public interface DataCleanupStrategy {
    
    /**
     * Cleans up a person's references to ensure they don't point to ignored IDs.
     * 
     * @param person the person whose references should be cleaned
     * @param ignoredIds the set of IDs that have been deleted/ignored
     */
    void cleanupReferences(Person person, Set<Long> ignoredIds);
}

