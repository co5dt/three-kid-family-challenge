package nl.pinkroccade.familychallenge.config;

import nl.pinkroccade.familychallenge.service.strategy.age.AgeValidationStrategy;
import nl.pinkroccade.familychallenge.service.strategy.children.ChildCountStrategy;
import nl.pinkroccade.familychallenge.service.strategy.cleanup.DataCleanupStrategy;
import nl.pinkroccade.familychallenge.service.strategy.partner.PartnerValidationStrategy;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for the Family Challenge application.
 *
 * <p>These properties allow runtime configuration of ambiguous requirements
 * documented in ADR-04. Change these values in application.properties to switch
 * between different interpretations without code changes.</p>
 *
 * <p>Properties prefix: {@code family-challenge}</p>
 */
@Component
@ConfigurationProperties(prefix = "family-challenge")
public class FamilyChallengeProperties {

    private String partnerValidation;
    private String childCount;
    private String ageValidation;
    private String cascadeDelete;

    /**
     * @return FQCN of {@link PartnerValidationStrategy} to use
     */
    public String getPartnerValidation() {
        return partnerValidation;
    }

    public void setPartnerValidation(String partnerValidation) {
        this.partnerValidation = partnerValidation;
    }

    /**
     * @return FQCN of {@link ChildCountStrategy} to use
     */
    public String getChildCount() {
        return childCount;
    }

    public void setChildCount(String childCount) {
        this.childCount = childCount;
    }

    /**
     * @return FQCN of {@link AgeValidationStrategy} to use
     */
    public String getAgeValidation() {
        return ageValidation;
    }

    public void setAgeValidation(String ageValidation) {
        this.ageValidation = ageValidation;
    }

    /**
     * @return FQCN of {@link DataCleanupStrategy} to use
     */
    public String getCascadeDelete() {
        return cascadeDelete;
    }

    public void setCascadeDelete(String cascadeDelete) {
        this.cascadeDelete = cascadeDelete;
    }
}

