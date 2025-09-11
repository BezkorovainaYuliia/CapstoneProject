package org.example.backend.service;

import org.example.backend.exceptions.ElementNotFoundExceptions;
import org.example.backend.model.Film;
import org.example.backend.model.FilmDTO;
import org.example.backend.model.GENRE;
import org.example.backend.repository.FilmsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class FilmsService {

    private final FilmsRepository filmsRepository;
    private final IdService idService;

    public FilmsService(FilmsRepository filmsRepository, IdService idService) {
        this.filmsRepository = filmsRepository;
        this.idService = idService;
    }

    public List<Film> getAllFilms() {
        return filmsRepository.findAll();
    }

    public Film addFilm(FilmDTO filmDTO) {

        if (filmDTO.title() == null || filmDTO.title().isBlank()) {
            throw new NullPointerException("Title cannot be null or blank.");
        }

        Film newFilm = new Film(
                idService.generateId(),
                filmDTO.title(),
                filmDTO.releaseDate(),
                filmDTO.rate(),
                filmDTO.casts(),
                filmDTO.genre(),
                filmDTO.duration(),
                filmDTO.poster()
        );
        return filmsRepository.save(newFilm);
    }

    public void deleteFilmById(String id) {
        if (!filmsRepository.existsById(id)) {
            throw new ElementNotFoundExceptions("Film not found: " + id);
        }
        filmsRepository.deleteById(id);
    }

    public Film getFilmById(String id) {
        return filmsRepository.findById(id)
                .orElseThrow(() -> new ElementNotFoundExceptions("Film not found: " + id));
    }

    public Film updateFilm(String id, FilmDTO filmDTO) {
        Film existingFilm = filmsRepository.findById(id)
                .orElseThrow(() -> new ElementNotFoundExceptions("Film not found: " + id));

        if (filmDTO.title() != null && !filmDTO.title().isBlank()) {
            existingFilm = existingFilm.withTitle(filmDTO.title());
        }
        if (filmDTO.releaseDate() != null) {
            existingFilm = existingFilm.withReleaseDate(filmDTO.releaseDate());
        }
        if (filmDTO.rate() != null) {
            existingFilm = existingFilm.withRate(filmDTO.rate());
        }
        if (filmDTO.casts() != null) {
            existingFilm = existingFilm.withCasts(filmDTO.casts());
        }
        if (filmDTO.genre() != null) {
            existingFilm = existingFilm.withGenre(filmDTO.genre());
        }
        if (filmDTO.duration() != null) {
            existingFilm = existingFilm.withDuration(filmDTO.duration());
        }

        return filmsRepository.save(existingFilm);
    }

    public List<Film> getFilmsByFilter(Integer year, String genre, Double rate) {

        if (genre != null) {
            try {
                GENRE.valueOf(genre);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid genre: " + genre);
            }
        }

        if (year != null && (year < 1888 || year > LocalDate.now().getYear())) {
            throw new IllegalArgumentException("Year must be between 1888 and the current year: " + year);
        }

        if (rate != null && (rate < 0.0 || rate > 10.0)) {
            throw new IllegalArgumentException("Rate must be between 0.0 and 10.0: " + rate);
        }


        List<Film> films = filmsRepository.findAll();

        if (rate != null) {
            films = filmsRepository.getFilmsByRate(rate);
        }
        if (genre != null) {
            films = filmsRepository.getFilmsByGenre(GENRE.valueOf(genre));
        }
        if (year != null) {
            films = films.stream()
                    .filter(f -> f.releaseDate().getYear() == year)
                    .toList();
        }
        return films;
    }

    public List<String> getHomepageImages() {
        LocalDate now = LocalDate.now();
        LocalDate start = now.minusMonths(1);
        LocalDate end = now.plusMonths(1);

        List<Film> films = filmsRepository.getFilmsByReleaseDateIsBetween(start, end);

        return films.stream()
                .map(Film::poster)
                .toList();
    }
}
