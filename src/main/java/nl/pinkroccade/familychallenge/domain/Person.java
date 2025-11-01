package nl.pinkroccade.familychallenge.domain;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

/**
 * Represents a person in the family relationship graph.
 *
 * <p>All relationships are stored as ID references to other {@code Person} entities,
 * creating a bidirectional graph structure where:</p>
 * <ul>
 *   <li><b>parent1/parent2</b>: backward-looking (who raised this person)</li>
 *   <li><b>partner</b>: horizontal (current spouse/partner)</li>
 *   <li><b>children</b>: forward-looking (offspring)</li>
 * </ul>
 */
public class Person {

    private Long      id;
    private String    name;
    private LocalDate birthDate;
    private Long      parent1Id;
    private Long      parent2Id;
    private Long      partnerId;
    private Set<Long> childrenIds;

    public Person() {
        this.childrenIds = new HashSet<>();
    }

    public Person(Long id) {
        this.id = id;
        this.childrenIds = new HashSet<>();
    }

    public Person(Long id, String name, LocalDate birthDate, Long parent1Id, Long parent2Id, Long partnerId,
                  Set<Long> childrenIds) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.parent1Id = parent1Id;
        this.parent2Id = parent2Id;
        this.partnerId = partnerId;
        this.childrenIds = childrenIds != null ? new HashSet<>(childrenIds) : new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Person withId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Person withName(String name) {
        this.name = name;
        return this;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Person withBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    public Long getParent1Id() {
        return parent1Id;
    }

    public void setParent1Id(Long parent1Id) {
        this.parent1Id = parent1Id;
    }

    public Person withParent1Id(Long parent1Id) {
        this.parent1Id = parent1Id;
        return this;
    }

    public Long getParent2Id() {
        return parent2Id;
    }

    public void setParent2Id(Long parent2Id) {
        this.parent2Id = parent2Id;
    }

    public Person withParent2Id(Long parent2Id) {
        this.parent2Id = parent2Id;
        return this;
    }

    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }

    public Person withPartnerId(Long partnerId) {
        this.partnerId = partnerId;
        return this;
    }

    public Set<Long> getChildrenIds() {
        return childrenIds;
    }

    public void setChildrenIds(Set<Long> childrenIds) {
        this.childrenIds = childrenIds != null ? new HashSet<>(childrenIds) : new HashSet<>();
    }

    public Person withChildrenIds(Set<Long> childrenIds) {
        this.childrenIds = childrenIds != null ? new HashSet<>(childrenIds) : new HashSet<>();
        return this;
    }

    public void addChild(Long childId) {
        if (childId != null) {
            this.childrenIds.add(childId);
        }
    }

    public void removeChild(Long childId) {
        this.childrenIds.remove(childId);
    }

    public Person withChild(Long childId) {
        if (childId != null) {
            this.childrenIds.add(childId);
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Person)) {
            return false;
        }
        return id != null && id.equals(((Person) o).id);
    }

    @Override
    public int hashCode() {
        /* Remark: https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
         * If Person had a natural identifier (e.g. social security number), use it here.
         */
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Person.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("birthDate=" + birthDate)
                .add("parent1Id=" + parent1Id)
                .add("parent2Id=" + parent2Id)
                .add("partnerId=" + partnerId)
                .add("childrenIds=" + childrenIds)
                .toString();
    }
}

