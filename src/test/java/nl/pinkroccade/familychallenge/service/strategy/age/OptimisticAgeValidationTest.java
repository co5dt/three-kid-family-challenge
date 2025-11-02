package nl.pinkroccade.familychallenge.service.strategy.age;

import nl.pinkroccade.familychallenge.domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link OptimisticAgeValidation} strategy (ADR-04 #1).
 * <p>Optimistic approach: Missing data means person IS under 18.</p>
 */
class OptimisticAgeValidationTest {
    
    private OptimisticAgeValidation strategy;
    
    @BeforeEach
    void setUp() {
        strategy = new OptimisticAgeValidation();
    }
    
    @Test
    void nullBirthDateShouldReturnTrue() {
        Person person = new Person(1L).withBirthDate(null);
        boolean result = strategy.isUnder18(person);
        assertThat(result).isTrue();
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
    void futureBirthDateShouldReturnTrue() {
        LocalDate birthDate = LocalDate.now().plusDays(1);
        Person person = new Person(1L).withBirthDate(birthDate);
        boolean result = strategy.isUnder18(person);
        assertThat(result).isTrue();
    }
}

