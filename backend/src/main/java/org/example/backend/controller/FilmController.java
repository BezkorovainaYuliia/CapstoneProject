package org.example.backend.controller;

import org.example.backend.model.*;
import org.example.backend.service.ClientApiService;
import org.example.backend.service.FilmsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class FilmController {

    private final FilmsService filmsService;
    private final ClientApiService clientApiService;

    public FilmController(FilmsService filmsService,
                          ClientApiService clientApiService) {
        this.filmsService = filmsService;
        this.clientApiService = clientApiService;
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


    @GetMapping("/films/filter")
    public ResponseEntity<List<Film>> getFilmsByFilter(@RequestParam(required = false) Integer year,
                                                       @RequestParam(required = false) String genre,
                                                       @RequestParam(required = false) Double rate) {
      List<Film> films = filmsService.getFilmsByFilter(year, genre, rate);
      return ResponseEntity.ok(films);
    }

    @GetMapping("/homepage_images")
    public ResponseEntity<List<String>> getHomepageImages() {
        List<String> images = filmsService.getHomepageImages();
        return ResponseEntity.ok(images);
    }

    @GetMapping("/search")
    public ResponseEntity<SearchResponse> getListOfInfo(@RequestParam String title) {
        SearchResponse response = clientApiService.getListOfChosenFilmByName(title);
                return ResponseEntity.ok(response);
    }

    @GetMapping("/search/{imdbID}")
    public ResponseEntity<FilmDTO> getFilmByImdbID(@PathVariable String imdbID) {
        FilmDTO filmFromClientApi = clientApiService.getMovieById(imdbID);
        return ResponseEntity.ok(filmFromClientApi);
    }

}
