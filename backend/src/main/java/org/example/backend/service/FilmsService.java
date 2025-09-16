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
                filmDTO.poster(),
                filmDTO.description()
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
        if (filmDTO.poster() != null) {
            existingFilm = existingFilm.withPoster(filmDTO.poster());
        }
        if (filmDTO.description() != null) {
            existingFilm = existingFilm.withDescription(filmDTO.description());
        }

        return filmsRepository.save(existingFilm);
    }

    public List<Film> getFilmsByFilter(Integer year, String genre, Double rate) {
        LocalDate start = null;
        LocalDate end = null;

        if (year != null) {
            validateYear(year);
            start = LocalDate.of(year, 1, 1);
            end = LocalDate.of(year, 12, 31);
        }

        GENRE filmGenre = parseGenre(genre);
        validateRate(rate);

        return queryFilms(start, end, filmGenre, rate);
    }

    private void validateYear(Integer year) {
        int currentYear = LocalDate.now().getYear();
        if (year < 1888 || year > currentYear) {
            throw new IllegalArgumentException("Year must be between 1888 and the current year: " + year);
        }
    }

    private GENRE parseGenre(String genre) {
        if (genre == null) return null;
        try {
            return GENRE.valueOf(genre);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid genre: " + genre);
        }
    }

    private void validateRate(Double rate) {
        if (rate != null && (rate < 0.0 || rate > 10.0)) {
            throw new IllegalArgumentException("Rate must be between 0.0 and 10.0: " + rate);
        }
    }

    private List<Film> queryFilms(LocalDate start, LocalDate end, GENRE genre, Double rate) {

        if (start != null && genre != null && rate != null) {
            return filmsRepository.findFilmsByGenreAndRateAfterAndReleaseDateBetween(genre, rate, start, end);
        }

        if (start != null && genre != null) return filmsRepository.findFilmsByGenreAndReleaseDateBetween(genre, start, end);
        if (start != null && rate != null) return filmsRepository.findFilmsByRateAfterAndReleaseDateBetween(rate, start, end);
        if (genre != null && rate != null) return filmsRepository.findFilmsByGenreAndRateAfter(genre, rate);

        if (start != null) return filmsRepository.findFilmsByReleaseDateBetween(start, end);
        if (genre != null) return filmsRepository.findFilmsByGenre(genre);
        if (rate != null) return filmsRepository.findFilmsByRateAfter(rate);

        return filmsRepository.findAll();
    }


    public List<String> getHomepageImages() {
        LocalDate now = LocalDate.now();
        LocalDate start = now.minusMonths(1);
        LocalDate end = now.plusMonths(1);

        List<Film> films = filmsRepository.findFilmsByReleaseDateBetween(start, end);

        return films.stream()
                .map(Film::poster)
                .toList();
    }
}
