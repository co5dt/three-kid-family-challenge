package nl.pinkroccade.familychallenge.service.strategy.cleanup;

import nl.pinkroccade.familychallenge.domain.Person;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * No-op delete cleanup strategy.
 *
 * <p><b>Alternative interpretation from ADR-04:</b> References are left as-is when
 * entities are deleted. Forward references remain valid.</p>
 *
 * <p>This allows forward references to persist, which may be useful if deleted entities
 * might be re-added later, or if the system should preserve relationship declarations
 * regardless of current existence.</p>
 */
@Component("noOpDeleteStrategy")
public class NoOpDeleteStrategy implements DataCleanupStrategy {

    @Override
    public void cleanupReferences(Person person, Set<Long> ignoredIds) {
        // ALTERNATIVE: ADR-04 #6 - No cleanup
        // References are left as-is (forward references remain)
        // Do nothing
    }
}


