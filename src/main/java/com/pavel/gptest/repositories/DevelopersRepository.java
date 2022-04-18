package com.pavel.gptest.repositories;

import com.pavel.gptest.classes.Developer;
import com.pavel.gptest.exceptions.DeveloperException;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface DevelopersRepository extends CrudRepository<Developer, Long> {
    Developer findDeveloperById(long id) throws DeveloperException.DeveloperNotFoundException;

    Optional<Developer> findDeveloperByEmail(String email) throws DeveloperException.DeveloperNotFoundException;

    Developer findDeveloperByIdAndEmail(long id, String email) throws DeveloperException.DeveloperNotFoundException;

    Boolean existsByEmail(Object email);

    Boolean existsById(long id);

    @Query("update Developer u set u.name = ?2 where u.id = ?1")
    @Modifying
    @Transactional
    void updateDeveloperName(long id, String name);

    @Query("update Developer u set u.email = ?2 where u.id = ?1")
    @Modifying
    @Transactional
    void updateDeveloperEmail(long id, String email);
}
