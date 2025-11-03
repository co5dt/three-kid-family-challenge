package nl.pinkroccade.familychallenge.service.strategy.partner;

import nl.pinkroccade.familychallenge.domain.Person;
import nl.pinkroccade.familychallenge.repository.PersonRepository;
import org.springframework.stereotype.Component;

/**
 * Reference-based partner validation strategy.
 *
 * <p><b>CHOSEN INTERPRETATION (ADR-04 #3 - confirmed):</b> Having a partner reference satisfies
 * the criterion, regardless of whether that partner entity exists in the repository.</p>
 *
 * <p>This allows forward references and treats partner relationships as declarations
 * of intent rather than requiring bidirectional existence.</p>
 *
 * <p><b>Response:</b> "Yes" - partner ID present but not POSTed does count as "has a partner".</p>
 */
@Component("referenceBasedPartnerValidation")
public class ReferenceBasedPartnerValidation implements PartnerValidationStrategy {

    @Override
    public boolean hasValidPartner(Person person, PersonRepository repository) {
        // DECISION: ADR-04 #3 (confirmed) - Reference-based interpretation
        // Having a non-null partner field satisfies "has a partner"
        return person.getPartnerId() != null;
    }
}


