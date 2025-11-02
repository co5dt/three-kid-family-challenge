package nl.pinkroccade.familychallenge.service;

import nl.pinkroccade.familychallenge.domain.Person;
import nl.pinkroccade.familychallenge.dto.PersonRequestDTO;
import nl.pinkroccade.familychallenge.dto.PersonResponseDTO;
import nl.pinkroccade.familychallenge.mapper.PersonMapper;
import nl.pinkroccade.familychallenge.repository.PersonRepository;
import nl.pinkroccade.familychallenge.service.strategy.cleanup.DataCleanupStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Service for processing person records and managing relationships.
 * Ensures bidirectional integrity of relationships.
 * 
 * <p><b>Strategy-based Design (ADR-04):</b></p>
 * <p>Uses {@link DataCleanupStrategy} to handle reference cleanup when processing 
 * persons with references to deleted/ignored IDs.</p>
 */
@Service
public class PersonService {
    
    private static final Logger log = LoggerFactory.getLogger(PersonService.class);
    
    private final PersonRepository repository;
    private final PatternMatchingService patternMatchingService;
    private final DataCleanupStrategy dataCleanupStrategy;
    
    public PersonService(
            PersonRepository repository, 
            PatternMatchingService patternMatchingService,
            DataCleanupStrategy dataCleanupStrategy) {
        this.repository = repository;
        this.patternMatchingService = patternMatchingService;
        this.dataCleanupStrategy = dataCleanupStrategy;
    }
    
    /**
     * Processes a person record: saves it, repairs bidirectional integrity, and finds matches.
     * 
     * @param request The person data from the request
     * @return List of people matching the pattern (may be empty)
     */
    public List<PersonResponseDTO> processPerson(PersonRequestDTO request) {
        if (repository.isIgnored(request.id())) {
            log.debug("Person ID {} is ignored, skipping", request.id());
            return findAndConvertMatches();
        }

        Person person = PersonMapper.toDomain(request);
        
        // ASSUMPTION: ADR-04 #6 - Delegated to DataCleanupStrategy
        // Clean up any references to ignored IDs before saving
        dataCleanupStrategy.cleanupReferences(person, repository.getIgnoredIds());
        
        // Save the person
        Person saved = repository.save(person);
        if (saved == null) {
            log.warn("Failed to save person ID {}", request.id());
            return List.of();
        }
        
        // ASSUMPTION: ADR-04 #5 - Partner relationships are bidirectional
        repairBidirectionalIntegrity(saved);

        return findAndConvertMatches();
    }
    
    /**
     * Repairs bidirectional integrity for a person's relationships.
     * If A says B is child, ensure B lists A as parent.
     * If A says B is partner, ensure B lists A as partner.
     */
    private void repairBidirectionalIntegrity(Person person) {
        Long personId = person.getId();
        
        // Repair parent-child relationships
        if (person.getParent1Id() != null) {
            addChildToParent(person.getParent1Id(), personId);
        }
        if (person.getParent2Id() != null) {
            addChildToParent(person.getParent2Id(), personId);
        }
        
        // Repair child relationships (add person as parent to children)
        if (person.getChildrenIds() != null) {
            for (Long childId : person.getChildrenIds()) {
                addParentToChild(childId, personId);
            }
        }
        
        // Repair partner relationship (bidirectional)
        if (person.getPartnerId() != null) {
            repository.findById(person.getPartnerId()).ifPresent(partner -> {
                if (!personId.equals(partner.getPartnerId())) {
                    partner.setPartnerId(personId);
                    repository.save(partner);
                }
            });
        }
    }
    
    private void addChildToParent(Long parentId, Long childId) {
        repository.findById(parentId).ifPresent(parent -> {
            if (!parent.getChildrenIds().contains(childId)) {
                parent.addChild(childId);
                repository.save(parent);
            }
        });
    }
    
    private void addParentToChild(Long childId, Long parentId) {
        repository.findById(childId).ifPresent(child -> {
            boolean needsSave = false;
            
            // Add as parent1 if empty, otherwise as parent2 if empty
            if (child.getParent1Id() == null) {
                child.setParent1Id(parentId);
                needsSave = true;
            } else if (child.getParent2Id() == null && !parentId.equals(child.getParent1Id())) {
                child.setParent2Id(parentId);
                needsSave = true;
            }
            
            if (needsSave) {
                repository.save(child);
            }
        });
    }
    
    /**
     * Deletes persons by their IDs and cleans up references to them.
     * 
     * @param ids the IDs of persons to delete
     */
    public void deletePersons(List<Long> ids) {
        // First delete from repository (removes from store and marks as ignored)
        repository.deleteByIds(ids);
        
        // ASSUMPTION: ADR-04 #6 - Delegated to DataCleanupStrategy
        // Clean up references in all remaining persons
        Set<Long> idsToCleanup = Set.copyOf(ids);
        repository.findAll().forEach(person -> {
            dataCleanupStrategy.cleanupReferences(person, idsToCleanup);
            repository.save(person);
        });
    }
    
    private List<PersonResponseDTO> findAndConvertMatches() {
        List<Person> matches = patternMatchingService.findMatches();
        return matches.stream()
                .map(person -> PersonMapper.toResponseDTO(person, repository))
                .toList();
    }
}
