package org.example.backend.service;

import org.example.backend.model.Film;
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
}
