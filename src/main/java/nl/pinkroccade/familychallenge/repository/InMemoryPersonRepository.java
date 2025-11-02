package nl.pinkroccade.familychallenge.repository;

import nl.pinkroccade.familychallenge.domain.Person;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of PersonRepository using ConcurrentHashMap.
 *
 * <p>Thread-safe for concurrent access. Data is not persisted.</p>
 * <p>This repository handles pure data storage operations. Reference cleanup
 * is the responsibility of the service layer.</p>
 */
@Repository
public class InMemoryPersonRepository implements PersonRepository {

    private final ConcurrentHashMap<Long, Person> store      = new ConcurrentHashMap<>();
    private final Set<Long>                       ignoredIds = ConcurrentHashMap.newKeySet();

    @Override
    public Person save(Person person) {
        if (isIgnored(person.getId())) {
            return null; // Silently ignore
        }
        store.put(person.getId(), person);
        return person;
    }

    @Override
    public Optional<Person> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Collection<Person> findAll() {
        return store.values();
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        ids.forEach(id -> {
            store.remove(id);
            ignoredIds.add(id);
        });
    }

    @Override
    public boolean isIgnored(Long id) {
        return ignoredIds.contains(id);
    }

    @Override
    public Set<Long> getIgnoredIds() {
        return Set.copyOf(ignoredIds);
    }

    public void clear() {
        store.clear();
        ignoredIds.clear();
    }
}

