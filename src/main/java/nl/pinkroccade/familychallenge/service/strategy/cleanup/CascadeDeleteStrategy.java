package nl.pinkroccade.familychallenge.service.strategy.cleanup;

import nl.pinkroccade.familychallenge.domain.Person;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Cascade delete cleanup strategy.
 * 
 * <p><b>Current assumption from ADR-04:</b> References to deleted IDs are removed/nullified 
 * during cleanup (cascade delete references).</p>
 * 
 * <p>This ensures data integrity by preventing references to non-existent entities.</p>
 */
@Component("cascadeDeleteStrategy")
public class CascadeDeleteStrategy implements DataCleanupStrategy {
    
    @Override
    public void cleanupReferences(Person person, Set<Long> ignoredIds) {
        // ASSUMPTION: ADR-04 #6 - Cascade delete references
        
        // Clean up partner reference
        if (person.getPartnerId() != null && ignoredIds.contains(person.getPartnerId())) {
            person.setPartnerId(null);
        }
        
        // Clean up parent references
        if (person.getParent1Id() != null && ignoredIds.contains(person.getParent1Id())) {
            person.setParent1Id(null);
        }
        
        if (person.getParent2Id() != null && ignoredIds.contains(person.getParent2Id())) {
            person.setParent2Id(null);
        }
        
        // Clean up children references
        if (person.getChildrenIds() != null && !person.getChildrenIds().isEmpty()) {
            Set<Long> cleanedChildren = person.getChildrenIds().stream()
                    .filter(childId -> !ignoredIds.contains(childId))
                    .collect(Collectors.toSet());
            person.setChildrenIds(cleanedChildren);
        }
    }
}


