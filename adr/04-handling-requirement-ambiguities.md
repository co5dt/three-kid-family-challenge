# ADR 04: Handling Requirement Ambiguities Through Configurable Design

**Status:** Accepted

---

## Context

During implementation of the Three-Kid Family Challenge, several requirement ambiguities emerged that significantly impact system behavior. Since clarifications are not immediately available, implementation must proceed while maintaining the ability to easily adjust behavior once answers are received.

The challenge is to make architectural decisions that:
1. Allow delivery of working software now
2. Minimize refactoring when requirements are clarified
3. Document assumptions clearly for future maintainers

---

## Decision

The system implements a **configurable, strategy-based architecture** that isolates requirement interpretation points, making them easy to modify without affecting the broader system. Each ambiguous requirement will be:

1. **Documented** with the current assumption
2. **Isolated** in a dedicated method or strategy class
3. **Tested** with scenarios covering different interpretations
4. **Marked** with clear comments indicating potential change points

### Implementation Strategy

#### 1. Use Strategy Pattern for Matching Logic
Create pluggable strategies for pattern matching rules that can be swapped based on requirement clarifications.

#### 2. Centralize Validation Rules
Place all data validation and interpretation logic in dedicated validator classes, separate from business logic.

#### 3. Configuration-Driven Behavior
Where feasible, use application properties to toggle behaviors (strict vs. flexible interpretations).

#### 4. Comprehensive Test Scenarios
Maintain test cases for both interpretations of ambiguous requirements, enabling quick verification when switching approaches.

---

## Requirement Ambiguities and Current Assumptions

### 1. Data Validation Requirements

**Question:** Are `name` and `birthDate` required fields, or can they be null?

**Context:** The assignment states "you might not know there [sic] name or birth date yet," implying these fields can be missing.

**Impact:**
- If `birthDate` can be null, how should age be calculated for "at least one child under 18"?
- Should a child with null `birthDate` be assumed under 18, over 18, or excluded from matching?

**Current Assumption:**  
✓ Fields CAN be null  
✓ Children with null `birthDate` are treated as "unknown age" and DON'T satisfy the "under 18" criterion  
✓ Pattern matching proceeds even with partial data

---

### 2. Partner's Additional Children in Blended Families

**Question:** The requirement states "has exactly 3 children and all 3 have that same partner listed as mother or father." This is clear for the person being evaluated. But what if the PARTNER has additional children with OTHER people?

**Scenario:**
```
Person A has children: [Child1, Child2, Child3] (exactly 3) ✓
Person A's partner: Person B ✓
Children 1, 2, 3 all list both A and B as parents ✓

BUT: Person B also has Child4 with Person C (different partner)
```

Does Person A match the pattern?

**Context:** The requirement is clear that Person A must have exactly 3 children. The ambiguity is whether Partner B is allowed to have additional children from other relationships.

**Interpretations:**
- **Exclusive:** The partner must ONLY have these 3 children (no children with other partners) - stricter "nuclear family" interpretation
- **Inclusive:** The partner can have additional children with other people (supports blended families) - focuses only on Person A's perspective

**Current Assumption:**  
✓ **Exclusive interpretation** - the partner should only have these 3 children, matching a traditional nuclear family structure

---

### 3. Partner Reference vs. Partner Existence

**Question:** If a person references a partner ID that has never been POSTed, does that count as "has a partner" for pattern matching?

**Scenario:**
```json
{
    "id": 10,
    "partner": {"id": 999}
}
```
Person 999 has never been POSTed to the system.

**Context:** Assignment explicitly allows forward references: "IDs may reference parents/partner/children that have never been seen before."

**Interpretations:**
- **Reference-based:** Having a non-null partner field satisfies "has a partner"
- **Existence-based:** The referenced partner must exist in the system

**Current Assumption:**  
✓ **Reference-based** - having a partner reference satisfies the criterion, regardless of whether that partner entity exists

---

### 4. Bidirectional Integrity Timing

**Question:** How should pattern matching handle incomplete bidirectional relationships when checking "immediately" after POST?

**Scenario:**
```
POST Person A: children = [1, 2, 3], partner = B
Pattern check runs immediately
Child 1 hasn't been POSTed yet (doesn't list A as parent)
```

**Context:**
- Assignment requires checking "immediately" after each POST
- Assignment requires "bidirectional integrity - if A says B is a child, B must list A as a parent (enforce or autorepair)"

**Sub-questions:**
- Should pattern matching wait until bidirectional relationships are complete?
- If auto-repair is applied, does the pattern check use pre-repair or post-repair state?
- How should the system verify "all 3 children have that same partner as parent" if children don't exist yet?

**Current Assumption:**  
✓ Pattern matching uses **post-repair state**  
✓ Bidirectional relationships are auto-repaired BEFORE pattern evaluation  
✓ Pattern matching considers forward references (children not yet POSTed count toward the total)

---

### 5. Partner Relationship Bidirectionality

**Question:** Are partner relationships required to be bidirectional, similar to parent-child relationships?

**Scenario:**
```
Person A: partner = {id: B}
Person B: partner = null (or partner = {id: C})
```

**Context:** Assignment explicitly requires bidirectional integrity for parent-child relationships but doesn't mention partners.

**Interpretations:**
- **Symmetric:** Partners should be bidirectional (if A lists B as partner, B should list A)
- **Asymmetric:** Partner references are independent declarations

**Current Assumption:**  
✓ **Symmetric** - partner relationships should be bidirectional and are auto-repaired similarly to parent-child relationships

---

### 6. DELETE Operation Cascade Behavior

**Question 6a:** When deleting IDs, should we remove those IDs from other people's partner/children/parent fields?

**Scenario:**
```
DELETE [42]

Person 10: partner = {id: 42}  // Should this become null?
Person 20: children = [{id: 42}, {id: 43}]  // Should child 42 be removed?
```

**Question 6b:** When a POST contains a reference to an ignored ID, should we store it as-is or convert it to null?

**Scenario:**
```
DELETE [42] // 42 is now ignored forever

Later POST:
{
    "id": 100,
    "partner": {"id": 42}
}
```

**Context:** Assignment states deleted IDs should be "silently ignore[d]...for both storage and matching" but doesn't specify cascade behavior.

**Current Assumption:**  
✓ **6a:** References to deleted IDs are removed/nullified during cleanup (cascade delete references)  
✓ **6b:** References to ignored IDs in new POSTs are stored as null

---
### Code Markers

Use these comments to mark assumption-dependent code:
```java
// ASSUMPTION: [Question #X from ADR-04] - [Brief description]
// Alternative: [What might change]
```
