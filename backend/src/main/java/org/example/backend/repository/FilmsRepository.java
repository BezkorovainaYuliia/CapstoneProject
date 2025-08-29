package org.example.backend.repository;

import org.example.backend.model.Film;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilmsRepository extends MongoRepository<Film, String> {
}
