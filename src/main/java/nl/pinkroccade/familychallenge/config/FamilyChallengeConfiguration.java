package nl.pinkroccade.familychallenge.config;

import nl.pinkroccade.familychallenge.service.strategy.age.AgeValidationStrategy;
import nl.pinkroccade.familychallenge.service.strategy.children.ChildCountStrategy;
import nl.pinkroccade.familychallenge.service.strategy.cleanup.DataCleanupStrategy;
import nl.pinkroccade.familychallenge.service.strategy.partner.PartnerValidationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

/**
 * Configuration for strategy selection (ADR-04).
 * 
 * <p>Selects active strategy implementations from application.properties.
 * All strategies are auto-discovered as {@code @Component} beans, then filtered
 * by FQCN to select the configured one. Selected strategies are marked {@code @Primary}
 * to resolve bean ambiguity during injection.</p>
 */
@Configuration
public class FamilyChallengeConfiguration {

    private final FamilyChallengeProperties properties;

    public FamilyChallengeConfiguration(FamilyChallengeProperties properties) {
        this.properties = properties;
    }

    /**
     * Selects the configured {@link PartnerValidationStrategy} from application.properties.
     * 
     * @param strategies all available partner validation strategies
     * @return the selected strategy
     */
    @Bean
    @Primary
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

    /**
     * Selects the configured {@link ChildCountStrategy} from application.properties.
     * 
     * @param strategies all available child count strategies
     * @return the selected strategy
     */
    @Bean
    @Primary
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

    /**
     * Selects the configured {@link AgeValidationStrategy} from application.properties.
     * 
     * @param strategies all available age validation strategies
     * @return the selected strategy
     */
    @Bean
    @Primary
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

    /**
     * Selects the configured {@link DataCleanupStrategy} from application.properties.
     * 
     * @param strategies all available data cleanup strategies
     * @return the selected strategy
     */
    @Bean
    @Primary
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

