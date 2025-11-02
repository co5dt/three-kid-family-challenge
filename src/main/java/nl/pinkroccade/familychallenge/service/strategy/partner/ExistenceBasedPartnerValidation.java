package nl.pinkroccade.familychallenge.service.strategy.partner;

import nl.pinkroccade.familychallenge.domain.Person;
import nl.pinkroccade.familychallenge.repository.PersonRepository;
import org.springframework.stereotype.Component;

/**
 * Existence-based partner validation strategy.
 *
 * <p><b>Alternative interpretation from ADR-04:</b> The referenced partner must
 * exist in the system for the relationship to count as valid.</p>
 *
 * <p>This ensures stronger data integrity by requiring both parties to exist
 * before considering the relationship valid for pattern matching.</p>
 */
@Component("existenceBasedPartnerValidation")
public class ExistenceBasedPartnerValidation implements PartnerValidationStrategy {

    @Override
    public boolean hasValidPartner(Person person, PersonRepository repository) {
        // ALTERNATIVE: ADR-04 #3 - Existence-based interpretation
        // Partner must exist in repository
        Long partnerId = person.getPartnerId();
        if (partnerId == null) {
            return false;
        }
        return repository.findById(partnerId).isPresent();
    }
}


