package org.example.backend.repository;

import org.example.backend.model.Film;
import org.example.backend.model.GENRE;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FilmsRepository extends MongoRepository<Film, String> {
    List<Film> getFilmsByGenre(GENRE genre);

    List<Film> getFilmsByRate(Double rate);

    List<Film> getFilmsByReleaseDateIsBetween(LocalDate start, LocalDate end);
}
