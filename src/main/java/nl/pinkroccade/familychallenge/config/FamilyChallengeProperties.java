package nl.pinkroccade.familychallenge.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "family-challenge")
public class FamilyChallengeProperties {

    private String partnerValidation;
    private String childCount;
    private String ageValidation;
    private String cascadeDelete;

    public String getPartnerValidation() {
        return partnerValidation;
    }

    public void setPartnerValidation(String partnerValidation) {
        this.partnerValidation = partnerValidation;
    }

    public String getChildCount() {
        return childCount;
    }

    public void setChildCount(String childCount) {
        this.childCount = childCount;
    }

    public String getAgeValidation() {
        return ageValidation;
    }

    public void setAgeValidation(String ageValidation) {
        this.ageValidation = ageValidation;
    }

    public String getCascadeDelete() {
        return cascadeDelete;
    }

    public void setCascadeDelete(String cascadeDelete) {
        this.cascadeDelete = cascadeDelete;
    }
}

