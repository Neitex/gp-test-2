package com.pavel.gptest;

import com.pavel.gptest.classes.Developer;
import com.pavel.gptest.exceptions.DeveloperException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RESTControllerTest {

    @Nested
    @SpringBootTest
    @AutoConfigureTestDatabase
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    class getDeveloperTests {
        @Autowired
        RESTController controller;

        @Test
        void getWithIdWithEmail() throws DeveloperException.DeveloperNotFoundException {
            controller.developersRepository.save(new Developer(1, "John Doe", "uwu@example.com"));
            assertEquals(new Developer(1, "John Doe", "uwu@example.com"), controller.getDeveloper("1", "uwu@example.com"));
        }

        @Test
        void getWithIdWithoutEmail() throws DeveloperException.DeveloperNotFoundException {
            controller.developersRepository.save(new Developer(1, "John Doe", "uwu@example.com"));
            assertEquals(new Developer(1, "John Doe", "uwu@example.com"), controller.getDeveloper("1", null));
        }

        @Test
        void getWithoutIdWithEmail() throws DeveloperException.DeveloperNotFoundException {
            controller.developersRepository.save(new Developer(1, "John Doe", "uwu@example.com"));
            assertEquals(new Developer(1, "John Doe", "uwu@example.com"), controller.getDeveloper(null, "uwu@example.com"));
        }

        @Test
        void getWithoutIdWithoutEmail() {
            assertThrows(IllegalArgumentException.class, () -> controller.getDeveloper(null, null));
        }

        @Test
        void getInvalidIdWithoutEmail() {
            assertThrows(NumberFormatException.class, () -> controller.getDeveloper("1.2", null));
        }

        @Test
        void getUnknownIdWithoutEmail() {
            assertThrows(DeveloperException.DeveloperNotFoundException.class, () -> controller.getDeveloper("55", null));
        }

        @Test
        void getUnknownIdUnknownEmail() {
            assertThrows(DeveloperException.DeveloperNotFoundException.class, () -> controller.getDeveloper("55", "uwu3@example.com"));
        }

        @Test
        void getWithoutIdUnknownEmail() {
            assertThrows(DeveloperException.DeveloperNotFoundException.class, () -> controller.getDeveloper(null, "uwu3@example.com"));
        }
    }

    @Nested
    @SpringBootTest
    @AutoConfigureTestDatabase
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    class createDeveloperTests {
        @Autowired
        RESTController controller;

        @AfterEach
        void cleanUp() {
            controller.developersRepository.deleteAll();
        }

        @Test
        void createDeveloperValidFields() throws DeveloperException.EmailNotUnique {
            Developer dev = new Developer(1, "John Doe", "uwu@example.com");
            assertEquals(dev, controller.createDeveloper(dev));
        }

        @Test
        void createDeveloperValidFieldsEnsureExists() throws DeveloperException.DeveloperNotFoundException, DeveloperException.EmailNotUnique {
            Developer dev = new Developer(1, "John Doe", "uwu@example.com");
            assertEquals(dev, controller.createDeveloper(dev));
            assertEquals(dev, controller.getDeveloper(String.valueOf(dev.getId()), dev.getEmail()));
        }

        @Test
        void createDeveloperWithSameEmail() throws IllegalArgumentException, DeveloperException.EmailNotUnique {
            Developer dev = new Developer(1, "John Doe", "uwu@example.com");
            assertEquals(dev, controller.createDeveloper(dev));
            assertThrows(DeveloperException.EmailNotUnique.class, () -> controller.createDeveloper(dev));
        }

    }

    @Nested
    @SpringBootTest
    @AutoConfigureTestDatabase
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    class deleteDevelopers {
        @Autowired
        RESTController controller;

        @Test
        void createAndDeleteDeveloper() throws DeveloperException.DeveloperNotFoundException, DeveloperException.EmailNotUnique {
            Developer dev = new Developer(1, "John Doe", "uwu@example.com");
            controller.createDeveloper(dev);
            controller.deleteDeveloper("1");
            assertEquals(0, controller.developersRepository.count());
        }
    }

    @Nested
    @SpringBootTest
    @AutoConfigureTestDatabase
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    class updateDeveloperTest {
        @Autowired
        RESTController restController;

        @Test
        void updateExistingUser() throws DeveloperException.DeveloperNotFoundException, IOException, DeveloperException.EmailNotUnique {
            Developer testDev = new Developer(1, "John Doe", "uwu@example.com");
            Developer resultDev1 = new Developer(1, "John", "uwu@example.com");
            Developer resultDev2 = new Developer(1, "John Doom", "uwu2@example.com");
            restController.developersRepository.save(testDev);
            assertEquals(resultDev1, restController.editDeveloper("1", "{\"name\":\"John\"}"));
            assertEquals(resultDev1, restController.getDeveloper("1", null));
            assertEquals(resultDev2, restController.editDeveloper("1", "{\"name\":\"John Doom\",\"email\":\"uwu2@example.com\"}"));
            assertEquals(resultDev2, restController.getDeveloper("1", null));
        }

        @Test
        void updateNonExistentUser() {
            assertThrows(DeveloperException.DeveloperNotFoundException.class, () -> restController.editDeveloper("0", "{}"));
        }

        @Test
        void updateExistingUserExistingEmail() {
            Developer testDev = new Developer(1, "John Doe", "uwu@example.com");
            Developer testDev2 = new Developer(1, "John", "uwu2@example.com");
            restController.developersRepository.save(testDev);
            restController.developersRepository.save(testDev2);
            assertThrows(DeveloperException.EmailNotUnique.class, () -> restController.editDeveloper("1", "{\"email\":\"uwu2@example.com\"}"));
        }
    }

    @Nested
    class DeveloperClassTest {
        @Test
        void createDeveloperInvalidName() {
            assertThrows(IllegalArgumentException.class, () -> {
                new Developer(1, "2", "uwu@example.com"); //invalid name
            });
        }

        @Test
        void createDeveloperInvalidFields() {
            assertThrows(IllegalArgumentException.class, () -> {
                new Developer(1, "2", "john"); //invalid name and email
            });
        }

        @Test
        void createDeveloperInvalidEmail() {
            assertThrows(IllegalArgumentException.class, () -> {
                new Developer(1, "John Doe", "uwu"); //invalid name
            });
        }
    }
}
