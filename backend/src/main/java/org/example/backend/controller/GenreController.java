package org.example.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.example.backend.model.GENRE;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/genres")
public class GenreController {

    @GetMapping
    public List<String> getGenres() {
        return Arrays.stream(GENRE.values())
                .map(Enum::name)
                .toList();
    }
}
