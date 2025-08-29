package org.example.backend.controller;

import org.example.backend.model.Film;
import org.example.backend.service.FilmsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
