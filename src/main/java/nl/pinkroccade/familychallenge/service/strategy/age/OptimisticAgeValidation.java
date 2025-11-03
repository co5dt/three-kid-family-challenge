package nl.pinkroccade.familychallenge.service.strategy.age;

import nl.pinkroccade.familychallenge.domain.Person;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

/**
 * Optimistic age validation strategy.
 *
 * <p><b>ALTERNATIVE INTERPRETATION (ADR-04 #4 - NOT chosen):</b> Children with null birthDate
 * are assumed to be under 18 (optimistic assumption).</p>
 *
 * <p>This lenient approach allows pattern matching to succeed even with incomplete data,
 * assuming missing birth dates indicate younger family members.</p>
 *
 * <p><b>Note:</b> This strategy remains available for reference but is not the chosen interpretation.</p>
 */
@Component("optimisticAgeValidation")
public class OptimisticAgeValidation implements AgeValidationStrategy {

    @Override
    public boolean isUnder18(Person person) {
        // ALTERNATIVE: ADR-04 #4 (NOT chosen) - Optimistic interpretation
        // Children with null birthDate are assumed to be under 18

        LocalDate birthDate = person.getBirthDate();
        if (birthDate == null) {
            return true; // Unknown age - assume under 18
        }

        LocalDate now = LocalDate.now();
        if (birthDate.isAfter(now)) {
            // Birth date in future is invalid - but be optimistic
            return true;
        }

        return Period.between(birthDate, now).getYears() < 18;
    }
}


