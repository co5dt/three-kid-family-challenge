package nl.pinkroccade.familychallenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Three-Kid Family Challenge.
 *
 * <p>This application processes person records and identifies families matching the following pattern:</p>
 * <ul>
 *   <li>Has a partner</li>
 *   <li>Has exactly 3 children with that same partner</li>
 *   <li>At least one child is under 18 years old</li>
 * </ul>
 */
@SpringBootApplication
public class FamilyChallengeApplication {

    public static void main(String[] args) {
        SpringApplication.run(FamilyChallengeApplication.class, args);
    }
}

