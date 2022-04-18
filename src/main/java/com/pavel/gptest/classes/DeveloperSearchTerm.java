package com.pavel.gptest.classes;

public enum DeveloperSearchTerm {
    ID_ONLY, EMAIL_ONLY, ID_AND_EMAIL, NONE;

    public static DeveloperSearchTerm getTerm(String id, String email) {
        if (id != null && email == null)
            return ID_ONLY;
        else if (id == null && email != null)
            return EMAIL_ONLY;
        else if (id != null)
            return ID_AND_EMAIL;
        else return NONE;
    }
}
