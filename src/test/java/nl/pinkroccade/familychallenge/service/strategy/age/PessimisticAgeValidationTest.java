package nl.pinkroccade.familychallenge.service.strategy.age;

import nl.pinkroccade.familychallenge.domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link PessimisticAgeValidation} strategy (ADR-04 #1).
 * <p>Pessimistic approach: Missing data means person is NOT under 18.</p>
 */
class PessimisticAgeValidationTest {
    
    private PessimisticAgeValidation strategy;
    
    @BeforeEach
    void setUp() {
        strategy = new PessimisticAgeValidation();
    }
    
    @Test
    void nullBirthDateShouldReturnFalse() {
        Person person = new Person(1L).withBirthDate(null);
        boolean result = strategy.isUnder18(person);
        assertThat(result).isFalse();
    }
    
    @Test
    void under18ShouldReturnTrue() {
        LocalDate birthDate = LocalDate.now().minusYears(10);
        Person person = new Person(1L).withBirthDate(birthDate);
        boolean result = strategy.isUnder18(person);
        assertThat(result).isTrue();
    }
    
    @Test
    void exactly18ShouldReturnFalse() {
        LocalDate birthDate = LocalDate.now().minusYears(18);
        Person person = new Person(1L).withBirthDate(birthDate);
        boolean result = strategy.isUnder18(person);
        assertThat(result).isFalse();
    }
    
    @Test
    void over18ShouldReturnFalse() {
        LocalDate birthDate = LocalDate.now().minusYears(25);
        Person person = new Person(1L).withBirthDate(birthDate);
        boolean result = strategy.isUnder18(person);
        assertThat(result).isFalse();
    }
    
    @Test
    void futureBirthDateShouldReturnFalse() {
        LocalDate birthDate = LocalDate.now().plusDays(1);
        Person person = new Person(1L).withBirthDate(birthDate);
        boolean result = strategy.isUnder18(person);
        assertThat(result).isFalse();
    }
}

