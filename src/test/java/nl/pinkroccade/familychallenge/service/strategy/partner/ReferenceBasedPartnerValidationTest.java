package nl.pinkroccade.familychallenge.service.strategy.partner;

import nl.pinkroccade.familychallenge.domain.Person;
import nl.pinkroccade.familychallenge.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ReferenceBasedPartnerValidation} strategy (ADR-04 #3).
 * <p>Reference-based approach: Partner is valid if partnerId is non-null.</p>
 */
@ExtendWith(MockitoExtension.class)
class ReferenceBasedPartnerValidationTest {

    @Mock
    private PersonRepository repository;

    private ReferenceBasedPartnerValidation strategy;

    @BeforeEach
    void setUp() {
        strategy = new ReferenceBasedPartnerValidation();
    }

    @Test
    void nullPartnerShouldReturnFalse() {
        Person person = new Person(1L).withPartnerId(null);
        boolean result = strategy.hasValidPartner(person, repository);
        assertThat(result).isFalse();
    }

    @Test
    void partnerReferenceExistsShouldReturnTrue() {
        Person person = new Person(1L).withPartnerId(999L);
        boolean result = strategy.hasValidPartner(person, repository);
        assertThat(result).isTrue();
    }

    @Test
    void partnerExistsShouldReturnTrue() {
        Person person = new Person(1L).withPartnerId(2L);
        boolean result = strategy.hasValidPartner(person, repository);
        assertThat(result).isTrue();
    }
}

