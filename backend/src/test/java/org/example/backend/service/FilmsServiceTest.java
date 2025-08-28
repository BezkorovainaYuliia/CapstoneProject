package org.example.backend.service;

import org.example.backend.model.Film;
import org.example.backend.repository.FilmsRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class FilmsServiceTest {
    @Mock
    private FilmsRepository filmsRepository;

    @InjectMocks
    private FilmsService filmsService;

    public FilmsServiceTest() {
        openMocks(this);
    }

    @Test
    void getAllFilms_returnsListOfFilms() {
        Film film = new Film("1", "Inception",
                LocalDate.of(2010, 7, 16),
                8.8,
                "Leonardo DiCaprio, Joseph Gordon-Levitt",
                "Sci-Fi",
                148);

        when(filmsRepository.findAll()).thenReturn(List.of(film));

        List<Film> films = filmsService.getAllFilms();

        assertEquals(1, films.size());
        assertEquals("Inception", films.get(0).title());

    }

}