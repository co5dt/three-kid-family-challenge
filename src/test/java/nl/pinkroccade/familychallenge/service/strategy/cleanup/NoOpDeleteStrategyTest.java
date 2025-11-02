package nl.pinkroccade.familychallenge.service.strategy.cleanup;

import nl.pinkroccade.familychallenge.domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link NoOpDeleteStrategy} (ADR-04 #6).
 * <p>NoOp approach: Keep all references, even to ignored persons.</p>
 */
class NoOpDeleteStrategyTest {
    
    private NoOpDeleteStrategy strategy;
    
    @BeforeEach
    void setUp() {
        strategy = new NoOpDeleteStrategy();
    }
    
    @Test
    void ignoredReferencesShouldKeepAll() {
        Person person = new Person(1L)
                .withPartnerId(999L)
                .withParent1Id(998L)
                .withParent2Id(997L)
                .withChildrenIds(Set.of(996L, 995L));
        Set<Long> ignoredIds = Set.of(999L, 998L, 997L, 996L, 995L);
        
        strategy.cleanupReferences(person, ignoredIds);
        assertThat(person.getPartnerId()).isEqualTo(999L);
        assertThat(person.getParent1Id()).isEqualTo(998L);
        assertThat(person.getParent2Id()).isEqualTo(997L);
        assertThat(person.getChildrenIds()).containsExactlyInAnyOrder(996L, 995L);
    }
}

