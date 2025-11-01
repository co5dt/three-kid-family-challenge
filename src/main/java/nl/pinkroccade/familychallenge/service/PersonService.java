package nl.pinkroccade.familychallenge.service;

import nl.pinkroccade.familychallenge.dto.PersonRequestDTO;
import nl.pinkroccade.familychallenge.dto.PersonResponseDTO;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class PersonService {
    public List<PersonResponseDTO> processPerson(PersonRequestDTO request) {
        return Collections.emptyList();
    }
}
