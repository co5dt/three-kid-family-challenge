package nl.pinkroccade.familychallenge.service.strategy.partner;

import nl.pinkroccade.familychallenge.domain.Person;
import nl.pinkroccade.familychallenge.repository.PersonRepository;
import org.springframework.stereotype.Component;

/**
 * Existence-based partner validation strategy.
 *
 * <p><b>ALTERNATIVE INTERPRETATION (ADR-04 #3 - NOT chosen):</b> The referenced partner must
 * exist in the system for the relationship to count as valid.</p>
 *
 * <p>This ensures stronger data integrity by requiring both parties to exist
 * before considering the relationship valid for pattern matching.</p>
 *
 * <p><b>Note:</b> This strategy remains available for reference but is not the chosen interpretation.</p>
 */
@Component("existenceBasedPartnerValidation")
public class ExistenceBasedPartnerValidation implements PartnerValidationStrategy {

    @Override
    public boolean hasValidPartner(Person person, PersonRepository repository) {
        // ALTERNATIVE: ADR-04 #3 (NOT chosen) - Existence-based interpretation
        // Partner must exist in repository
        Long partnerId = person.getPartnerId();
        if (partnerId == null) {
            return false;
        }
        return repository.findById(partnerId).isPresent();
    }
}


