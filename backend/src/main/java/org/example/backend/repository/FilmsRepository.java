package org.example.backend.repository;

import org.example.backend.model.Film;
import org.example.backend.model.GENRE;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


import java.time.LocalDate;
import java.util.List;

@Repository
public interface FilmsRepository extends MongoRepository<Film, String> {

    List<Film> findFilmsByRateAfter(Double rateAfter);

    List<Film> findFilmsByGenre(GENRE genre);

    List<Film> findFilmsByReleaseDateBetween(LocalDate start, LocalDate end);

    List<Film>  findFilmsByRateAfterAndReleaseDateBetween(Double rateAfter, LocalDate start, LocalDate end);

    List<Film> findFilmsByGenreAndReleaseDateBetween(GENRE genre, LocalDate start, LocalDate end);

    List<Film> findFilmsByGenreAndRateAfter(GENRE genre, Double rateAfter);

    List<Film> findFilmsByGenreAndRateAfterAndReleaseDateBetween(GENRE genre, Double rateAfter, LocalDate start, LocalDate end);

}
