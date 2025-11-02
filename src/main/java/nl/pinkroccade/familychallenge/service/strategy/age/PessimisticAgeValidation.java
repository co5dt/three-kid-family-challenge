package nl.pinkroccade.familychallenge.service.strategy.age;

import nl.pinkroccade.familychallenge.domain.Person;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

/**
 * Pessimistic age validation strategy.
 *
 * <p><b>Current assumption from ADR-04:</b> Children with null birthDate are treated
 * as "unknown age" and don't satisfy the "under 18" criterion.</p>
 *
 * <p>This conservative approach requires explicit proof of age to match the pattern.</p>
 */
@Component("pessimisticAgeValidation")
public class PessimisticAgeValidation implements AgeValidationStrategy {

    @Override
    public boolean isUnder18(Person person) {
        // ASSUMPTION: ADR-04 #1 - Pessimistic interpretation
        // Children with null birthDate don't satisfy the "under 18" criterion
        LocalDate birthDate = person.getBirthDate();
        if (birthDate == null) {
            return false; // Unknown age - don't assume under 18
        }

        LocalDate now = LocalDate.now();
        if (birthDate.isAfter(now)) {
            // Birth date in future is invalid - treat as unknown age
            return false;
        }

        return Period.between(birthDate, now).getYears() < 18;
    }
}


