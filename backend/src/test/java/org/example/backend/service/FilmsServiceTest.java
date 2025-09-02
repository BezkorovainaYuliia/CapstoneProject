package org.example.backend.service;

import org.example.backend.exceptions.ElementNotFoundExceptions;
import org.example.backend.model.Film;
import org.example.backend.model.FilmDTO;
import org.example.backend.model.GENRE;
import org.example.backend.repository.FilmsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    @Test
    void deleteFilmById_existingFilm_deletesSuccessfully() {
        String filmId = "123";

        when(filmsRepository.existsById(filmId)).thenReturn(true);

        filmsService.deleteFilmById(filmId);

        verify(filmsRepository).deleteById(filmId);
    }

    @Test
    void deleteFilmById_nonExistingFilm_throwsException() {
        String filmId = "999";

        when(filmsRepository.existsById(filmId)).thenReturn(false);

        ElementNotFoundExceptions ex = assertThrows(ElementNotFoundExceptions.class,
                () -> filmsService.deleteFilmById(filmId));

        assertEquals("Film not found: 999", ex.getMessage());
        verify(filmsRepository, never()).deleteById(anyString());
    }


    @Test
    void getFilmById_existingFilm_returnsFilm() {
        String filmId = "123";
        Film film = new Film(filmId, "Inception", LocalDate.of(2010, 7, 16),
                8.8, "Leonardo DiCaprio", GENRE.SCI_FI, 148);

        when(filmsRepository.findById(filmId)).thenReturn(Optional.of(film));

        Film result = filmsService.getFilmById(filmId);

        assertNotNull(result);
        assertEquals(filmId, result.id());
        assertEquals("Inception", result.title());

        verify(filmsRepository, times(1)).findById(filmId);
    }

    @Test
    void getFilmById_nonExistingFilm_throwsException() {
        String filmId = "999";

        when(filmsRepository.findById(filmId)).thenReturn(Optional.empty());

        ElementNotFoundExceptions ex = assertThrows(ElementNotFoundExceptions.class,
                () -> filmsService.getFilmById(filmId));

        assertEquals("Film not found: 999", ex.getMessage());

        verify(filmsRepository, times(1)).findById(filmId);
    }

    @Test
    void updateFilm_existingFilm_updatesAndReturnsFilm() {
        String filmId = "123";
        Film existingFilm = new Film(filmId, "Inception", LocalDate.of(2010, 7, 16),
                8.8, "Leonardo DiCaprio", GENRE.SCI_FI, 148);

        FilmDTO filmDTO = new FilmDTO(
                "Inception Updated",
                LocalDate.of(2010, 7, 16),
                9.0,
                "Leonardo DiCaprio, Joseph Gordon-Levitt",
                GENRE.SCI_FI,
                150
        );

        when(filmsRepository.findById(filmId)).thenReturn(Optional.of(existingFilm));

        Film updatedFilm = new Film(
                filmId,
                filmDTO.title(),
                filmDTO.release_date(),
                filmDTO.rate(),
                filmDTO.casts(),
                filmDTO.genre(),
                filmDTO.duration()
        );

        when(filmsRepository.save(any(Film.class))).thenReturn(updatedFilm);

        Film result = filmsService.updateFilm(filmId, filmDTO);

        assertNotNull(result);
        assertEquals(filmId, result.id());
        assertEquals("Inception Updated", result.title());
        assertEquals(9.0, result.rate());
        assertEquals(150, result.duration());

        verify(filmsRepository).findById(filmId);
        verify(filmsRepository).save(any(Film.class));
    }

    @Test
    void updateFilm_nonExistingFilm_throwsException() {
        String filmId = "999";
        FilmDTO filmDTO = new FilmDTO(
                "Inception Updated",
                LocalDate.of(2010, 7, 16),
                9.0,
                "Leonardo DiCaprio, Joseph Gordon-Levitt",
                GENRE.SCI_FI,
                150
        );
        when(filmsRepository.findById(filmId)).thenReturn(Optional.empty());
        ElementNotFoundExceptions ex = assertThrows(ElementNotFoundExceptions.class,
                () -> filmsService.updateFilm(filmId, filmDTO));
        assertEquals("Film not found: 999", ex.getMessage());
        verify(filmsRepository).findById(filmId);
        verify(filmsRepository, never()).save(any(Film.class));
    }

    @Test
    void updateFilm_existingFilmWithPartialDTO_updatesAndReturnsFilm() {
        String filmId = "123";
        Film existingFilm = new Film(filmId, "Inception", LocalDate.of(2010, 7, 16),
                8.8, "Leonardo DiCaprio", GENRE.SCI_FI, 148);

        FilmDTO filmDTO = new FilmDTO(
                null,
                null,
                9.0,
                null,
                null,
                150
        );

        when(filmsRepository.findById(filmId)).thenReturn(Optional.of(existingFilm));

        Film updatedFilm = new Film(
                filmId,
                "Inception",
                LocalDate.of(2010, 7, 16),
                9.0,
                "Leonardo DiCaprio",
                GENRE.SCI_FI,
                150
        );

        when(filmsRepository.save(any(Film.class))).thenReturn(updatedFilm);

        Film result = filmsService.updateFilm(filmId, filmDTO);

        assertNotNull(result);
        assertEquals(filmId, result.id());
        assertEquals("Inception", result.title());
        assertEquals(9.0, result.rate());
        assertEquals(150, result.duration());

        verify(filmsRepository).findById(filmId);
        verify(filmsRepository).save(any(Film.class));
    }

    @Test
    void updateFilm_existingFilmWithBlankTitleInDTO_doesNotUpdateTitle() {
        String filmId = "123";
        Film existingFilm = new Film(filmId, "Inception", LocalDate.of(2010, 7, 16),
                8.8, "Leonardo DiCaprio", GENRE.SCI_FI, 148);

        FilmDTO filmDTO = new FilmDTO(
                "   ",
                null,
                9.0,
                null,
                null,
                150
        );

        when(filmsRepository.findById(filmId)).thenReturn(Optional.of(existingFilm));

        Film updatedFilm = new Film(
                filmId,
                "Inception",
                LocalDate.of(2010, 7, 16),
                9.0,
                "Leonardo DiCaprio",
                GENRE.SCI_FI,
                150
        );

        when(filmsRepository.save(any(Film.class))).thenReturn(updatedFilm);

        Film result = filmsService.updateFilm(filmId, filmDTO);

        assertNotNull(result);
        assertEquals(filmId, result.id());
        assertEquals("Inception", result.title()); // Title should remain unchanged
        assertEquals(9.0, result.rate());
        assertEquals(150, result.duration());

        verify(filmsRepository).findById(filmId);
        verify(filmsRepository).save(any(Film.class));
    }

    @Test
    void getAllFilms_whenNoFilms_returnsEmptyList() {
        when(filmsRepository.findAll()).thenReturn(List.of());

        List<Film> films = filmsService.getAllFilms();

        assertNotNull(films);
        assertTrue(films.isEmpty());
    }

    @Test
    void getAllFilms_whenRepositoryThrowsException_propagatesException() {
        when(filmsRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> filmsService.getAllFilms());
        assertEquals("Database error", ex.getMessage());
    }

    @Test
    void addFilm_whenRepositoryThrowsException_propagatesException() {
        FilmDTO filmDTO = new FilmDTO(
                "Inception",
                LocalDate.of(2010, 7, 16),
                8.8,
                "Leonardo DiCaprio",
                GENRE.SCI_FI,
                148
        );

        when(idService.generateId()).thenReturn("123");
        when(filmsRepository.save(any(Film.class))).thenThrow(new RuntimeException("Database error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> filmsService.addFilm(filmDTO));
        assertEquals("Database error", ex.getMessage());
    }

    @Test
    void deleteFilmById_whenRepositoryThrowsException_propagatesException() {
        String filmId = "123";

        when(filmsRepository.existsById(filmId)).thenReturn(true);
        doThrow(new RuntimeException("Database error")).when(filmsRepository).deleteById(filmId);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> filmsService.deleteFilmById(filmId));
        assertEquals("Database error", ex.getMessage());
    }

    @Test
    void updateFilm_whenRepositoryThrowsException_propagatesException() {
        String filmId = "123";
        Film existingFilm = new Film(filmId, "Inception", LocalDate.of(2010, 7, 16),
                8.8, "Leonardo DiCaprio", GENRE.SCI_FI, 148);

        FilmDTO filmDTO = new FilmDTO(
                "Inception Updated",
                LocalDate.of(2010, 7, 16),
                9.0,
                "Leonardo DiCaprio, Joseph Gordon-Levitt",
                GENRE.SCI_FI,
                150
        );

        when(filmsRepository.findById(filmId)).thenReturn(Optional.of(existingFilm));
        when(filmsRepository.save(any(Film.class))).thenThrow(new RuntimeException("Database error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> filmsService.updateFilm(filmId, filmDTO));
        assertEquals("Database error", ex.getMessage());
    }

    @Test
    void getFilmById_whenRepositoryThrowsException_propagatesException() {
        String filmId = "123";

        when(filmsRepository.findById(filmId)).thenThrow(new RuntimeException("Database error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> filmsService.getFilmById(filmId));
        assertEquals("Database error", ex.getMessage());
    }

    @Test
    void addFilm_whenRateIsNull_addsFilmSuccessfully() {
        // given
        FilmDTO filmDTO = new FilmDTO(
                "Inception",
                LocalDate.of(2010, 7, 16),
                null,
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
        assertThat(result.rate()).isNull();

        verify(filmsRepository, times(1)).save(any(Film.class));
        verify(idService, times(1)).generateId();
    }

    @Test
    void addFilm_whenDurationIsNull_addsFilmSuccessfully() {
        // given
        FilmDTO filmDTO = new FilmDTO(
                "Inception",
                LocalDate.of(2010, 7, 16),
                8.8,
                "Leonardo DiCaprio",
                GENRE.SCI_FI,
                null
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
        assertThat(result.duration()).isNull();

        verify(filmsRepository, times(1)).save(any(Film.class));
        verify(idService, times(1)).generateId();
    }

    @Test
    void updateFilm_whenRateAndDurationAreNull_updatesFilmSuccessfully() {
        String filmId = "123";
        Film existingFilm = new Film(filmId, "Inception", LocalDate.of(2010, 7, 16),
                8.8, "Leonardo DiCaprio", GENRE.SCI_FI, 148);

        FilmDTO filmDTO = new FilmDTO(
                "Inception Updated",
                LocalDate.of(2010, 7, 16),
                null,
                "Leonardo DiCaprio, Joseph Gordon-Levitt",
                GENRE.SCI_FI,
                null
        );

        when(filmsRepository.findById(filmId)).thenReturn(Optional.of(existingFilm));

        Film updatedFilm = new Film(
                filmId,
                filmDTO.title(),
                filmDTO.release_date(),
                existingFilm.rate(), // rate remains unchanged
                filmDTO.casts(),
                filmDTO.genre(),
                existingFilm.duration() // duration remains unchanged
        );

        when(filmsRepository.save(any(Film.class))).thenReturn(updatedFilm);

        Film result = filmsService.updateFilm(filmId, filmDTO);

        assertNotNull(result);
        assertEquals(filmId, result.id());
        assertEquals("Inception Updated", result.title());
        assertEquals(8.8, result.rate()); // rate should remain unchanged
        assertEquals(148, result.duration()); // duration should remain unchanged

        verify(filmsRepository).findById(filmId);
        verify(filmsRepository).save(any(Film.class));
    }

    @Test
    void updateFilm_whenAllFieldsInDTOAreNull_doesNotUpdateAnyField() {
        String filmId = "123";
        Film existingFilm = new Film(filmId, "Inception", LocalDate.of(2010, 7, 16),
                8.8, "Leonardo DiCaprio", GENRE.SCI_FI, 148);

        FilmDTO filmDTO = new FilmDTO(
                null,
                null,
                null,
                null,
                null,
                null
        );

        when(filmsRepository.findById(filmId)).thenReturn(Optional.of(existingFilm));
        when(filmsRepository.save(any(Film.class))).thenReturn(existingFilm);

        Film result = filmsService.updateFilm(filmId, filmDTO);

        assertNotNull(result);
        assertEquals(filmId, result.id());
        assertEquals("Inception", result.title());
        assertEquals(LocalDate.of(2010, 7, 16), result.release_date());
        assertEquals(8.8, result.rate());
        assertEquals("Leonardo DiCaprio", result.casts());
        assertEquals(GENRE.SCI_FI, result.genre());
        assertEquals(148, result.duration());

        verify(filmsRepository).findById(filmId);
        verify(filmsRepository).save(any(Film.class));
    }

}