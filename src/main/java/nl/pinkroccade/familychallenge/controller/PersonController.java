package nl.pinkroccade.familychallenge.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
