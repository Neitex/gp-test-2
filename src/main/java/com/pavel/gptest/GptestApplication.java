package com.pavel.gptest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pavel.gptest.classes.Developer;
import com.pavel.gptest.classes.DeveloperSearchTerm;
import com.pavel.gptest.exceptions.DeveloperException;
import com.pavel.gptest.repositories.DevelopersRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Iterator;

@SpringBootApplication
public class GptestApplication {

    public static void main(String[] args) {
        SpringApplication.run(GptestApplication.class, args);
    }

}

@RestController
@RequestMapping()
class RESTController {

    final DevelopersRepository developersRepository;

    public RESTController(DevelopersRepository developersRepository) {
        this.developersRepository = developersRepository;
    }

    @GetMapping(value = "/developer", produces = {MediaType.APPLICATION_JSON_VALUE})
    public Developer getDeveloper(@RequestParam(required = false) String id, @RequestParam(required = false) String email) throws DeveloperException.DeveloperNotFoundException {
        DeveloperSearchTerm term = DeveloperSearchTerm.getTerm(id, email);
        Developer developer;
        switch (term) {
            case ID_ONLY: {
                long convertedId = Long.parseLong(id);
                developer = developersRepository.findDeveloperById(convertedId);
            }
            break;
            case EMAIL_ONLY: {
                developer = developersRepository.findDeveloperByEmail(email).orElseThrow(() -> new DeveloperException.DeveloperNotFoundException(email));
            }
            break;
            case ID_AND_EMAIL: {
                developer = developersRepository.findDeveloperByIdAndEmail(Long.parseLong(id), email);
            }
            break;
            case NONE:
                throw new IllegalArgumentException("No search terms were provided");
            default:
                throw new IllegalStateException("Impossible 'term' was given");
        }
        if (developer == null) throw new DeveloperException.DeveloperNotFoundException(term, Long.parseLong(id), email);
        return developer;
    }

    @PutMapping(value = "/developer", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public Developer createDeveloper(@RequestBody Developer developer) throws IllegalArgumentException, DeveloperException.EmailNotUnique {
        if (developersRepository.existsByEmail(developer.getEmail()))
            throw new DeveloperException.EmailNotUnique(developer.getEmail());
        developersRepository.save(developer);
        return developer;
    }

    @DeleteMapping(value = "/developer")
    public void deleteDeveloper(@RequestParam String id) {
        developersRepository.deleteById(Long.parseLong(id));
    }

    @PatchMapping(value = "/developer")
    public Developer editDeveloper(@RequestParam String id, @RequestBody String string) throws IOException, DeveloperException.DeveloperNotFoundException, DeveloperException.EmailNotUnique {
        long parsedId = Long.parseLong(id);
        JsonNode object = (new ObjectMapper()).readValue(string, JsonNode.class);
        if (!developersRepository.existsById(parsedId))
            throw new DeveloperException.DeveloperNotFoundException(parsedId);
        if (object.size() > 2) throw new IllegalArgumentException("Too many fields in JSON payload");
        for (Iterator<String> it = object.fieldNames(); it.hasNext(); ) {
            String key = it.next();
            switch (key) {
                case "name":
                    developersRepository.updateDeveloperName(Long.parseLong(id), object.get(key).textValue());
                    break;
                case "email":
                    if (developersRepository.existsByEmail(object.get(key).textValue()))
                        throw new DeveloperException.EmailNotUnique(object.get(key).textValue());
                    developersRepository.updateDeveloperEmail(parsedId, object.get(key).textValue());
                    break;
                default:
                    throw new IllegalArgumentException(String.format("Unknown key %s encountered", object.get(key).textValue()));
            }
        }
        return developersRepository.findDeveloperById(parsedId);
    }

    @ExceptionHandler({DeveloperException.DeveloperNotFoundException.class})
    public ResponseEntity<String> handleException(DeveloperException.DeveloperNotFoundException e) {
        return new ResponseEntity<>(e.getReason(), null, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({NumberFormatException.class})
    public ResponseEntity<String> handleException(NumberFormatException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<String> handleException(IllegalArgumentException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({DeveloperException.EmailNotUnique.class})
    public ResponseEntity<String> handleException(DeveloperException.EmailNotUnique e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }
}
