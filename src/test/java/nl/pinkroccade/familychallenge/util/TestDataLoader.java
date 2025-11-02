package nl.pinkroccade.familychallenge.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Loads test scenarios from JSON files in {@code src/test/resources/test-scenarios/}.
 *
 * <p>Auto-discovers all .json files in the test-scenarios directory.</p>
 */
public class TestDataLoader {

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private static final String SCENARIOS_PATH = "classpath:test-scenarios/*.json";

    public static JsonNode load(String filename) throws IOException {
        Resource resource = new PathMatchingResourcePatternResolver()
                .getResource("classpath:test-scenarios/" + filename);
        return mapper.readTree(resource.getInputStream());
    }

    public static Stream<Arguments> allTestScenarios() {
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver()
                    .getResources(SCENARIOS_PATH);
            return Arrays.stream(resources)
                    .map(Resource::getFilename)
                    .filter(Objects::nonNull)
                    .sorted()
                    .map(TestDataLoader::toArguments);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load test scenarios", e);
        }
    }

    private static Arguments toArguments(String filename) {
        try {
            JsonNode data = load(filename);
            boolean expectedMatch = data.has("expectedMatch") && data.get("expectedMatch").asBoolean();
            String description = data.has("description") ? data.get("description").asText() : filename;
            return Arguments.of(filename, description, data, expectedMatch);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load test file: " + filename, e);
        }
    }
}
