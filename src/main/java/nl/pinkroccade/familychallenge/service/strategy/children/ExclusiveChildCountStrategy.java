package nl.pinkroccade.familychallenge.service.strategy.children;

import nl.pinkroccade.familychallenge.domain.Person;
import nl.pinkroccade.familychallenge.repository.PersonRepository;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Exclusive child count validation strategy.
 *
 * <p><b>ALTERNATIVE INTERPRETATION (ADR-04 #2 - NOT chosen):</b> Person must have exactly 3 children,
 * all listing the same partner as parent. Additionally, the PARTNER must ONLY have
 * these 3 children (no children with other partners).</p>
 *
 * <p>This "nuclear family" interpretation does not support blended families where
 * the partner might have children from previous relationships.</p>
 *
 * <p><b>Note:</b> This strategy remains available for reference but is not the chosen interpretation.
 * See ADR-04 Feedback section for details.</p>
 */
@Component("exclusiveChildCountStrategy")
public class ExclusiveChildCountStrategy implements ChildCountStrategy {

    @Override
    public ValidationResult validateChildren(Person person, Long partnerId, PersonRepository repository) {
        // ALTERNATIVE: ADR-04 #2 (NOT chosen) - Exclusive interpretation
        // Person must have exactly 3 children, all with the same partner
        // AND partner must only have these same 3 children

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

        // EXCLUSIVE check: Partner must ONLY have these 3 children
        Person partner = repository.findById(partnerId).orElse(null);
        if (partner != null && partner.getChildrenIds() != null) {
            Set<Long> partnerChildren = partner.getChildrenIds();
            if (partnerChildren.size() != 3 || !partnerChildren.equals(childrenIds)) {
                // Partner has different children or more than 3 children
                return new ValidationResult(false, Set.of());
            }
        }

        return new ValidationResult(true, validChildren);
    }

    /**
     * Checks if personId is listed as parent1 or parent2 of the child.
     */
    private boolean isParentOf(Long personId, Person child) {
        return personId.equals(child.getParent1Id()) || personId.equals(child.getParent2Id());
    }
}

