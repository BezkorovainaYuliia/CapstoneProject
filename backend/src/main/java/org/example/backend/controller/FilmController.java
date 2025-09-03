package org.example.backend.controller;

import org.example.backend.model.Film;
import org.example.backend.model.FilmDTO;
import org.example.backend.service.FilmsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class FilmController {

    private final FilmsService filmsService;

    public FilmController(FilmsService filmsService) {
        this.filmsService = filmsService;
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        return filmsService.getAllFilms();
    }

    @PostMapping("/films")
    public ResponseEntity<Film> addFilm(@RequestBody FilmDTO filmDTO) {
        return new ResponseEntity<>(filmsService.addFilm(filmDTO), HttpStatus.CREATED);
    }

    @DeleteMapping("/films/{id}")
    public ResponseEntity<Void> deleteFilm(@PathVariable String id) {
        filmsService.deleteFilmById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/films/{id}")
    public ResponseEntity<Film> getFilmById(@PathVariable String id) {
        Film film = filmsService.getFilmById(id);
        return ResponseEntity.ok(film);
    }

    @PutMapping("/films/{id}")
    public ResponseEntity<Film> updateFilm(@PathVariable String id, @RequestBody FilmDTO filmDTO) {
        Film updatedFilm = filmsService.updateFilm(id, filmDTO);
        return ResponseEntity.ok(updatedFilm);
    }
}
