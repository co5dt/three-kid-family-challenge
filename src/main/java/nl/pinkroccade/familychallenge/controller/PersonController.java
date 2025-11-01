package nl.pinkroccade.familychallenge.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import nl.pinkroccade.familychallenge.dto.PersonRequestDTO;
import nl.pinkroccade.familychallenge.dto.PersonResponseDTO;
import nl.pinkroccade.familychallenge.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for person-related operations.
 *
 * <p>Endpoints:</p>
 * <ul>
 *   <li><b>POST /api/v1/people</b>: Add or update a person and return matching persons.</li>
 *   <li><b>DELETE /api/v1/people</b>: Delete persons and add them to the ignore list.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/people")
public class PersonController {

    private static final Logger log = LoggerFactory.getLogger(PersonController.class);

    /* Unofficial client error specific to nginx */
    private static final int HTTP_444_NO_RESPONSE = 444;

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    /**
     * Processes a person record.
     * <p>
     * Returns:
     * <ul>
     *   <li>HTTP 200 with matching persons if at least one person matches the pattern</li>
     *   <li>HTTP 444 if no one matches</li>
     * </ul>
     * </p>
     *
     * @param request the person data
     * @param response the HTTP response (used for setting custom status 444)
     * @return the list of matching persons, or {@code null} if no matches are found
     */
    @PostMapping
    public ResponseEntity<List<PersonResponseDTO>> addPerson(
            @Valid @RequestBody PersonRequestDTO request,
            HttpServletResponse response) {
        log.info("POST /api/v1/people - ID: {}", request.id());

        List<PersonResponseDTO> matches = personService.processPerson(request);

        if (matches.isEmpty()) {
            log.debug("No matches found - returning HTTP 444");
            response.setStatus(HTTP_444_NO_RESPONSE);
            return null;
        }

        log.debug("Found {} matches - returning HTTP 200", matches.size());
        return ResponseEntity.ok(matches);
    }
}
