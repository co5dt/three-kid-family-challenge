package nl.pinkroccade.familychallenge.service.strategy.partner;

import nl.pinkroccade.familychallenge.domain.Person;
import nl.pinkroccade.familychallenge.repository.PersonRepository;
import org.springframework.stereotype.Component;

/**
 * Reference-based partner validation strategy.
 *
 * <p><b>Current assumption from ADR-04:</b> Having a partner reference satisfies
 * the criterion, regardless of whether that partner entity exists in the repository.</p>
 *
 * <p>This allows forward references and treats partner relationships as declarations
 * of intent rather than requiring bidirectional existence.</p>
 */
@Component("referenceBasedPartnerValidation")
public class ReferenceBasedPartnerValidation implements PartnerValidationStrategy {

    @Override
    public boolean hasValidPartner(Person person, PersonRepository repository) {
        // ASSUMPTION: ADR-04 #3 - Reference-based interpretation
        // Having a non-null partner field satisfies "has a partner"
        return person.getPartnerId() != null;
    }
}


