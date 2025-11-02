package nl.pinkroccade.familychallenge.config;

import nl.pinkroccade.familychallenge.service.strategy.age.AgeValidationStrategy;
import nl.pinkroccade.familychallenge.service.strategy.children.ChildCountStrategy;
import nl.pinkroccade.familychallenge.service.strategy.cleanup.DataCleanupStrategy;
import nl.pinkroccade.familychallenge.service.strategy.partner.PartnerValidationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class FamilyChallengeConfiguration {

    private final FamilyChallengeProperties properties;

    public FamilyChallengeConfiguration(FamilyChallengeProperties properties) {
        this.properties = properties;
    }

    @Bean
    public PartnerValidationStrategy partnerValidationStrategy(List<PartnerValidationStrategy> strategies) {

        return strategies.stream()
                .filter(s -> s.getClass().getName().equals(properties.getPartnerValidation()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No PartnerValidationStrategy found for configured class: "
                                + properties.getPartnerValidation()
                                + ". Available strategies: "
                                + strategies.stream().map(s -> s.getClass().getName()).toList()));
    }

    @Bean
    public ChildCountStrategy childCountStrategy(List<ChildCountStrategy> strategies) {

        return strategies.stream()
                .filter(s -> s.getClass().getName().equals(properties.getChildCount()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No ChildCountStrategy found for configured class: "
                                + properties.getChildCount()
                                + ". Available strategies: "
                                + strategies.stream().map(s -> s.getClass().getName()).toList()));
    }

    @Bean
    public AgeValidationStrategy ageValidationStrategy(List<AgeValidationStrategy> strategies) {

        return strategies.stream()
                .filter(s -> s.getClass().getName().equals(properties.getAgeValidation()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No AgeValidationStrategy found for configured class: "
                                + properties.getAgeValidation()
                                + ". Available strategies: "
                                + strategies.stream().map(s -> s.getClass().getName()).toList()));
    }

    @Bean
    public DataCleanupStrategy dataCleanupStrategy(List<DataCleanupStrategy> strategies) {

        return strategies.stream()
                .filter(s -> s.getClass().getName().equals(properties.getCascadeDelete()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No DataCleanupStrategy found for configured class: "
                                + properties.getCascadeDelete()
                                + ". Available strategies: "
                                + strategies.stream().map(s -> s.getClass().getName()).toList()));
    }
}

