package com.pavel.gptest.exceptions;

import com.pavel.gptest.classes.DeveloperSearchTerm;

public class DeveloperException {
    public static class DeveloperNotFoundException extends Exception {
        enum DeveloperNotFoundKeys {
            ID, EMAIL, ID_AND_EMAIL
        }

        DeveloperNotFoundKeys reason;
        long id;
        String email;


        public DeveloperNotFoundException(long id) {
            super();
            this.id = id;
            this.reason = DeveloperNotFoundKeys.ID;
        }

        public DeveloperNotFoundException(String email) {
            super();
            this.email = email;
            this.reason = DeveloperNotFoundKeys.EMAIL;
        }

        public DeveloperNotFoundException(DeveloperSearchTerm term, Long id, String email) {
            super();
            switch (term) {
                case ID_ONLY:
                    this.id = id;
                    this.reason = DeveloperNotFoundKeys.ID;
                    break;
                case EMAIL_ONLY:
                    this.email = email;
                    this.reason = DeveloperNotFoundKeys.EMAIL;
                    break;
                case ID_AND_EMAIL:
                    this.id = id;
                    this.email = email;
                    this.reason = DeveloperNotFoundKeys.ID_AND_EMAIL;
                    break;
            }
        }

        public String getReason() {
            switch (reason) {
                case ID:
                    return String.format("Developer with ID %s was not found", id);
                case EMAIL:
                    return String.format("Developer with e-mail %s was not found", email);
                case ID_AND_EMAIL:
                    return String.format("Developer with ID %s and e-mail %s was not found", id, email);
            }
            return "";
        }
    }

    public static class EmailNotUnique extends Exception {
        public EmailNotUnique(String email) {
            super(String.format("Developer with e-mail %s already exists", email));
        }
    }
}
