package nl.pinkroccade.familychallenge.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.pinkroccade.familychallenge.dto.PersonRequestDTO;
import nl.pinkroccade.familychallenge.repository.InMemoryPersonRepository;
import nl.pinkroccade.familychallenge.util.TestDataLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "family-challenge.partner-validation=nl.pinkroccade.familychallenge.service.strategy.partner.ReferenceBasedPartnerValidation",
        "family-challenge.child-count=nl.pinkroccade.familychallenge.service.strategy.children.ExclusiveChildCountStrategy",
        "family-challenge.age-validation=nl.pinkroccade.familychallenge.service.strategy.age.PessimisticAgeValidation",
        "family-challenge.cascade-delete=nl.pinkroccade.familychallenge.service.strategy.cleanup.CascadeDeleteStrategy"
})
class PersonControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InMemoryPersonRepository repository;

    @BeforeEach
    void setUp() {
        repository.clear();
    }

    @Test
    void postPersonHappyPathShouldReturnMatches() throws Exception {
        JsonNode scenario = TestDataLoader.load("happy-path-match.json");
        JsonNode requests = scenario.get("requests");

        // Process all person requests
        for (JsonNode request : requests) {
            PersonRequestDTO dto = objectMapper.treeToValue(request, PersonRequestDTO.class);

            mockMvc.perform(post("/api/v1/people")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)));
        }

        // Last request should return matches
        PersonRequestDTO lastRequest = objectMapper.treeToValue(
                requests.get(requests.size() - 1),
                PersonRequestDTO.class
        );

        mockMvc.perform(post("/api/v1/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lastRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void postPersonNoMatchShouldReturn444() throws Exception {
        JsonNode scenario = TestDataLoader.load("no-partner.json");
        JsonNode requests = scenario.get("requests");

        for (JsonNode request : requests) {
            PersonRequestDTO dto = objectMapper.treeToValue(request, PersonRequestDTO.class);

            ResultActions result = mockMvc.perform(post("/api/v1/people")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)));

            // Last request should return 444 (no matches)
            if (request == requests.get(requests.size() - 1)) {
                result.andExpect(status().is(444));
            }
        }
    }

    @Test
    void postPersonAllChildrenOver18ShouldReturn444() throws Exception {
        JsonNode scenario = TestDataLoader.load("all-children-over-18.json");
        JsonNode requests = scenario.get("requests");

        for (JsonNode request : requests) {
            PersonRequestDTO dto = objectMapper.treeToValue(request, PersonRequestDTO.class);

            mockMvc.perform(post("/api/v1/people")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)));
        }

        // Re-post last person to trigger matching - should return 444 (no child under 18)
        PersonRequestDTO lastRequest = objectMapper.treeToValue(
                requests.get(requests.size() - 1),
                PersonRequestDTO.class
        );

        mockMvc.perform(post("/api/v1/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lastRequest)))
                .andExpect(status().is(444));
    }

    @Test
    void postPersonWrongChildCountShouldReturn444() throws Exception {
        JsonNode scenario = TestDataLoader.load("wrong-child-count.json");
        JsonNode zeroChildrenScenario = scenario.get("scenarios").get("zeroChildren");

        for (JsonNode request : zeroChildrenScenario) {
            PersonRequestDTO dto = objectMapper.treeToValue(request, PersonRequestDTO.class);

            mockMvc.perform(post("/api/v1/people")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)));
        }

        // Re-post to trigger matching - should return 444 (needs exactly 3 children)
        PersonRequestDTO lastRequest = objectMapper.treeToValue(
                zeroChildrenScenario.get(zeroChildrenScenario.size() - 1),
                PersonRequestDTO.class
        );

        mockMvc.perform(post("/api/v1/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lastRequest)))
                .andExpect(status().is(444));
    }

    @Test
    void postPersonPartialDataShouldHandleGracefully() throws Exception {
        JsonNode scenario = TestDataLoader.load("partial-data.json");
        JsonNode requests = scenario.get("requests");

        for (JsonNode request : requests) {
            PersonRequestDTO dto = objectMapper.treeToValue(request, PersonRequestDTO.class);

            mockMvc.perform(post("/api/v1/people")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)));
        }

        // Re-post last person to trigger matching - should return 444 (only 2 children, one without birthDate)
        PersonRequestDTO lastRequest = objectMapper.treeToValue(
                requests.get(requests.size() - 1),
                PersonRequestDTO.class
        );

        mockMvc.perform(post("/api/v1/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lastRequest)))
                .andExpect(status().is(444));
    }

    @Test
    void postPersonMixedPartnersShouldReturn444() throws Exception {
        JsonNode scenario = TestDataLoader.load("mixed-partners.json");
        JsonNode requests = scenario.get("requests");

        for (JsonNode request : requests) {
            PersonRequestDTO dto = objectMapper.treeToValue(request, PersonRequestDTO.class);

            mockMvc.perform(post("/api/v1/people")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)));
        }

        // Re-post to trigger matching - should return 444 (children have different partners)
        PersonRequestDTO lastRequest = objectMapper.treeToValue(
                requests.get(requests.size() - 1),
                PersonRequestDTO.class
        );

        mockMvc.perform(post("/api/v1/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lastRequest)))
                .andExpect(status().is(444));
    }

    @Test
    void postPersonWithoutIdShouldReturn400() throws Exception {
        String invalidJson = """
                {
                    "name": "Test Person",
                    "birthDate": "2000-01-01"
                }
                """;

        mockMvc.perform(post("/api/v1/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}

