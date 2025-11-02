package nl.pinkroccade.familychallenge.repository;

import nl.pinkroccade.familychallenge.domain.Person;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Repository for managing Person entities.
 * 
 * <p>This repository handles pure data storage operations. Business logic
 * such as reference cleanup should be handled by the service layer.</p>
 */
public interface PersonRepository {
    
    Person save(Person person);
    
    Optional<Person> findById(Long id);
    
    Collection<Person> findAll();

    void deleteByIds(List<Long> ids);
    
    boolean isIgnored(Long id);
    
    Set<Long> getIgnoredIds();
}
