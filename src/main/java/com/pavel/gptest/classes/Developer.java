package com.pavel.gptest.classes;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Generated()
@Getter
@Entity
public class Developer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    long id;
    @Setter
    String name;
    @Setter
    String email;

    public Developer(long id, String name, String email) throws IllegalArgumentException {
        if (!validateName(name))
            throw new IllegalArgumentException(String.format("Name %s is invalid", name));
        if (!validateEmail(email))
            throw new IllegalArgumentException(String.format("E-mail %s is invalid", email));
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public Developer() {

    }

    public static boolean validateName(String name) {
        return (name.length() >= 2 && name.length() <= 50) && (name.matches("^[a-zA-Z].*"));
    }

    public static boolean validateEmail(String email) {
        Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE); // taken from StackOverflow :)
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Developer developer = (Developer) o;
        return id == developer.id && Objects.equals(name, developer.name) && Objects.equals(email, developer.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email);
    }

    @Override
    public String toString() {
        return "Developer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
