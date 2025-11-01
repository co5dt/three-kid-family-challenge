package nl.pinkroccade.familychallenge.repository;

import nl.pinkroccade.familychallenge.domain.Person;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing Person entities.
 */
public interface PersonRepository {
    
    Person save(Person person);
    
    Optional<Person> findById(Long id);
    
    Collection<Person> findAll();
    
    void deleteByIds(List<Long> ids);
    
    boolean isIgnored(Long id);
}
