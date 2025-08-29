package org.example.backend.service;

import org.example.backend.model.Film;
import org.example.backend.model.FilmDTO;
import org.example.backend.model.GENRE;
import org.example.backend.repository.FilmsRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilmsServiceTest {
    @Mock
    private FilmsRepository filmsRepository;

    @InjectMocks
    private FilmsService filmsService;

    @Mock
    private IdService idService;

    @Test
    void getAllFilms_returnsListOfFilms() {
        Film film = new Film("1", "Inception",
                LocalDate.of(2010, 7, 16),
                8.8,
                "Leonardo DiCaprio, Joseph Gordon-Levitt",
                GENRE.SCI_FI,
                148);

        when(filmsRepository.findAll()).thenReturn(List.of(film));

        List<Film> films = filmsService.getAllFilms();

        assertEquals(1, films.size());
        assertEquals("Inception", films.getFirst().title());

    }

    @Test
    void addFilm_shouldSaveAndReturnFilm() {
        // given
        FilmDTO filmDTO = new FilmDTO(
                "Inception",
                LocalDate.of(2010, 7, 16),
                8.8,
                "Leonardo DiCaprio",
                GENRE.SCI_FI,
                148
        );

        when(idService.generateId()).thenReturn("123");

        Film savedFilm = new Film(
                "123",
                filmDTO.title(),
                filmDTO.release_date(),
                filmDTO.rate(),
                filmDTO.casts(),
                filmDTO.genre(),
                filmDTO.duration()
        );

        when(filmsRepository.save(any(Film.class))).thenReturn(savedFilm);

        // when
        Film result = filmsService.addFilm(filmDTO);

        // then
        assertThat(result.id()).isEqualTo("123");
        assertThat(result.title()).isEqualTo("Inception");
        assertThat(result.genre()).isEqualTo(GENRE.SCI_FI);

        verify(filmsRepository, times(1)).save(any(Film.class));
        verify(idService, times(1)).generateId();
    }

    @Test
    void addFilm_shouldThrowException_whenTitleIsNull() {
        FilmDTO filmDTO = new FilmDTO(
                null,
                LocalDate.of(2010, 7, 16),
                8.8,
                "Leonardo DiCaprio",
                GENRE.SCI_FI,
                148
        );

        assertThrows(NullPointerException.class, () -> filmsService.addFilm(filmDTO));
        verifyNoInteractions(filmsRepository);
    }

    @Test
    void addFilm_shouldThrowException_whenTitleIsBlank() {
        FilmDTO filmDTO = new FilmDTO(
                "   ",
                LocalDate.of(2010, 7, 16),
                8.8,
                "Leonardo DiCaprio",
                GENRE.SCI_FI,
                148
        );

        assertThrows(NullPointerException.class, () -> filmsService.addFilm(filmDTO));
        verifyNoInteractions(filmsRepository);
    }

}