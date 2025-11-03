package nl.pinkroccade.familychallenge.service.strategy.children;

import nl.pinkroccade.familychallenge.domain.Person;
import nl.pinkroccade.familychallenge.repository.PersonRepository;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Inclusive child count validation strategy.
 *
 * <p><b>CHOSEN INTERPRETATION (ADR-04 #2 - confirmed):</b> Person must have exactly 3 children,
 * all listing the same partner as parent. The PARTNER can have additional children with
 * OTHER people (blended family support).</p>
 *
 * <p>This interpretation focuses only on the person being evaluated having exactly 3 children,
 * without restricting the partner's other relationships. Supports real-world blended families.</p>
 *
 * <p><b>Rationale:</b> The requirement "has exactly 3 children" refers to the person being evaluated,
 * not their partner. The assignment never constrains the partner's total child count.</p>
 */
@Component("inclusiveChildCountStrategy")
public class InclusiveChildCountStrategy implements ChildCountStrategy {

    @Override
    public ValidationResult validateChildren(Person person, Long partnerId, PersonRepository repository) {
        // DECISION: ADR-04 #2 (confirmed) - Inclusive interpretation
        // Person must have exactly 3 children, all with the same partner
        // Partner CAN have additional children with other people

        Set<Long> childrenIds = person.getChildrenIds();
        if (childrenIds == null || childrenIds.size() != 3) {
            return new ValidationResult(false, Set.of());
        }

        Set<Long> validChildren = new HashSet<>();

        // Check all 3 children list both person and partner as parents
        for (Long childId : childrenIds) {
            Person child = repository.findById(childId).orElse(null);

            if (child == null) {
                return new ValidationResult(false, Set.of());
            }

            // Child must list this person AND the partner as parents
            if (!isParentOf(person.getId(), child) || !isParentOf(partnerId, child)) {
                return new ValidationResult(false, Set.of());
            }

            validChildren.add(childId);
        }

        // INCLUSIVE: We don't check if partner has additional children
        // Partner can have children with other people - that's OK

        return new ValidationResult(true, validChildren);
    }

    /**
     * Checks if personId is listed as parent1 or parent2 of the child.
     */
    private boolean isParentOf(Long personId, Person child) {
        return personId.equals(child.getParent1Id()) || personId.equals(child.getParent2Id());
    }
}

