package org.example.backend.service;

import org.example.backend.exceptions.ElementNotFoundExceptions;
import org.example.backend.model.Film;
import org.example.backend.model.FilmDTO;
import org.example.backend.repository.FilmsRepository;
import org.springframework.stereotype.Service;

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
                filmDTO.release_date(),
                filmDTO.rate(),
                filmDTO.casts(),
                filmDTO.genre(),
                filmDTO.duration()
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
        if (filmDTO.release_date() != null) {
            existingFilm = existingFilm.withRelease_date(filmDTO.release_date());
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

}
