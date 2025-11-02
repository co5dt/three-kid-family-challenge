package nl.pinkroccade.familychallenge.service.strategy.partner;

import nl.pinkroccade.familychallenge.domain.Person;
import nl.pinkroccade.familychallenge.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link ExistenceBasedPartnerValidation} strategy (ADR-04 #3).
 * <p>Existence-based approach: Partner is valid only if partnerId is non-null AND partner actually exists in the repository.</p>
 */
@ExtendWith(MockitoExtension.class)
class ExistenceBasedPartnerValidationTest {
    
    @Mock
    private PersonRepository repository;
    
    private ExistenceBasedPartnerValidation strategy;
    
    @BeforeEach
    void setUp() {
        strategy = new ExistenceBasedPartnerValidation();
    }
    
    @Test
    void nullPartnerShouldReturnFalse() {
        Person person = new Person(1L).withPartnerId(null);
        boolean result = strategy.hasValidPartner(person, repository);
        assertThat(result).isFalse();
    }
    
    @Test
    void partnerDoesNotExistShouldReturnFalse() {
        Person person = new Person(1L).withPartnerId(999L);
        when(repository.findById(999L)).thenReturn(Optional.empty());
        boolean result = strategy.hasValidPartner(person, repository);
        assertThat(result).isFalse();
    }
    
    @Test
    void partnerExistsShouldReturnTrue() {
        Person person = new Person(1L).withPartnerId(2L);
        Person partner = new Person(2L);
        when(repository.findById(2L)).thenReturn(Optional.of(partner));
        boolean result = strategy.hasValidPartner(person, repository);
        assertThat(result).isTrue();
    }
}

