package nl.pinkroccade.familychallenge.service.strategy.cleanup;

import nl.pinkroccade.familychallenge.domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link CascadeDeleteStrategy} (ADR-04 #6).
 * <p>Cascade approach: Remove references to ignored persons.</p>
 */
class CascadeDeleteStrategyTest {
    
    private CascadeDeleteStrategy strategy;
    
    @BeforeEach
    void setUp() {
        strategy = new CascadeDeleteStrategy();
    }
    
    @Test
    void partnerIgnoredShouldRemoveReference() {
        Person person = new Person(1L).withPartnerId(999L);
        Set<Long> ignoredIds = Set.of(999L);
        strategy.cleanupReferences(person, ignoredIds);
        assertThat(person.getPartnerId()).isNull();
    }
    
    @Test
    void partnerNotIgnoredShouldKeepReference() {
        Person person = new Person(1L).withPartnerId(2L);
        Set<Long> ignoredIds = Set.of(999L);
        strategy.cleanupReferences(person, ignoredIds);
        assertThat(person.getPartnerId()).isEqualTo(2L);
    }
    
    @Test
    void parent1IgnoredShouldRemoveReference() {
        Person person = new Person(1L).withParent1Id(999L).withParent2Id(3L);
        Set<Long> ignoredIds = Set.of(999L);
        strategy.cleanupReferences(person, ignoredIds);
        assertThat(person.getParent1Id()).isNull();
        assertThat(person.getParent2Id()).isEqualTo(3L);
    }
    
    @Test
    void parent2IgnoredShouldRemoveReference() {
        Person person = new Person(1L).withParent1Id(2L).withParent2Id(999L);
        Set<Long> ignoredIds = Set.of(999L);
        strategy.cleanupReferences(person, ignoredIds);
        assertThat(person.getParent1Id()).isEqualTo(2L);
        assertThat(person.getParent2Id()).isNull();
    }
    
    @Test
    void someChildrenIgnoredShouldRemoveIgnoredChildren() {
        Person person = new Person(1L).withChildrenIds(Set.of(10L, 11L, 12L, 13L));
        Set<Long> ignoredIds = Set.of(11L, 13L);
        strategy.cleanupReferences(person, ignoredIds);
        assertThat(person.getChildrenIds()).containsExactlyInAnyOrder(10L, 12L);
    }
    
    @Test
    void noChildrenShouldHandleGracefully() {
        Person person = new Person(1L).withChildrenIds(Set.of());
        Set<Long> ignoredIds = Set.of(999L);
        strategy.cleanupReferences(person, ignoredIds);
        assertThat(person.getChildrenIds()).isEmpty();
    }
}

