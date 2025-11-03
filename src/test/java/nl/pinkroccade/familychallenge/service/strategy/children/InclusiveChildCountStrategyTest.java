package nl.pinkroccade.familychallenge.service.strategy.children;

import nl.pinkroccade.familychallenge.domain.Person;
import nl.pinkroccade.familychallenge.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link InclusiveChildCountStrategy} (ADR-04 #2).
 * <p>Inclusive approach: Partner CAN have additional children with other people.</p>
 */
@ExtendWith(MockitoExtension.class)
class InclusiveChildCountStrategyTest {

    @Mock
    private PersonRepository repository;

    private InclusiveChildCountStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new InclusiveChildCountStrategy();
    }

    @Test
    void personHas3ChildrenPartnerHasOnlyThese3ShouldBeValid() {
        Person person = new Person(1L).withPartnerId(2L).withChildrenIds(Set.of(10L, 11L, 12L));
        Person child1 = new Person(10L).withParent1Id(1L).withParent2Id(2L);
        Person child2 = new Person(11L).withParent1Id(1L).withParent2Id(2L);
        Person child3 = new Person(12L).withParent1Id(1L).withParent2Id(2L);

        when(repository.findById(10L)).thenReturn(Optional.of(child1));
        when(repository.findById(11L)).thenReturn(Optional.of(child2));
        when(repository.findById(12L)).thenReturn(Optional.of(child3));

        ChildCountStrategy.ValidationResult result = strategy.validateChildren(person, 2L, repository);

        assertThat(result.valid()).isTrue();
        assertThat(result.validChildrenIds()).containsExactlyInAnyOrder(10L, 11L, 12L);
    }

    @Test
    void personHas3ChildrenPartnerHas4thChildWithOtherShouldBeValid() {
        Person person = new Person(1L).withPartnerId(2L).withChildrenIds(Set.of(10L, 11L, 12L));
        // Partner has 4th child with someone else, but inclusive mode doesn't care
        Person child1 = new Person(10L).withParent1Id(1L).withParent2Id(2L);
        Person child2 = new Person(11L).withParent1Id(1L).withParent2Id(2L);
        Person child3 = new Person(12L).withParent1Id(1L).withParent2Id(2L);

        when(repository.findById(10L)).thenReturn(Optional.of(child1));
        when(repository.findById(11L)).thenReturn(Optional.of(child2));
        when(repository.findById(12L)).thenReturn(Optional.of(child3));

        ChildCountStrategy.ValidationResult result = strategy.validateChildren(person, 2L, repository);

        // Inclusive mode doesn't care about partner's additional children
        assertThat(result.valid()).isTrue();
        assertThat(result.validChildrenIds()).containsExactlyInAnyOrder(10L, 11L, 12L);
    }

    @Test
    void personHas2ChildrenShouldNotBeValid() {
        Person person = new Person(1L).withPartnerId(2L).withChildrenIds(Set.of(10L, 11L));
        ChildCountStrategy.ValidationResult result = strategy.validateChildren(person, 2L, repository);
        assertThat(result.valid()).isFalse();
    }

    @Test
    void personHas4ChildrenShouldNotBeValid() {
        Person person = new Person(1L).withPartnerId(2L).withChildrenIds(Set.of(10L, 11L, 12L, 13L));
        ChildCountStrategy.ValidationResult result = strategy.validateChildren(person, 2L, repository);
        assertThat(result.valid()).isFalse();
    }
}

