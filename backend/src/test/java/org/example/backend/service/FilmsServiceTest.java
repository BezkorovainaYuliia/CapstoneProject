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
                148,
                "https://example.com/inception.jpg");

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
                148,
                "https://example.com/inception.jpg"
        );

        when(idService.generateId()).thenReturn("123");

        Film savedFilm = new Film(
                "123",
                filmDTO.title(),
                filmDTO.releaseDate(),
                filmDTO.rate(),
                filmDTO.casts(),
                filmDTO.genre(),
                filmDTO.duration(),
                filmDTO.poster()
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
                148,
                "https://example.com/inception.jpg"
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
                148,
                "https://example.com/inception.jpg"
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
                8.8, "Leonardo DiCaprio", GENRE.SCI_FI, 148, "https://example.com/inception.jpg");

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
                8.8, "Leonardo DiCaprio", GENRE.SCI_FI, 148, "https://example.com/inception.jpg");

        FilmDTO filmDTO = new FilmDTO(
                "Inception Updated",
                LocalDate.of(2010, 7, 16),
                9.0,
                "Leonardo DiCaprio, Joseph Gordon-Levitt",
                GENRE.SCI_FI,
                150,
                "https://example.com/inception-updated.jpg"
        );

        when(filmsRepository.findById(filmId)).thenReturn(Optional.of(existingFilm));

        Film updatedFilm = new Film(
                filmId,
                filmDTO.title(),
                filmDTO.releaseDate(),
                filmDTO.rate(),
                filmDTO.casts(),
                filmDTO.genre(),
                filmDTO.duration(),
                filmDTO.poster()
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
                150,
                "https://example.com/inception-updated.jpg"
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
                8.8, "Leonardo DiCaprio", GENRE.SCI_FI, 148, "https://example.com/inception.jpg");

        FilmDTO filmDTO = new FilmDTO(
                null,
                null,
                9.0,
                null,
                null,
                150,
                null
        );

        when(filmsRepository.findById(filmId)).thenReturn(Optional.of(existingFilm));

        Film updatedFilm = new Film(
                filmId,
                "Inception",
                LocalDate.of(2010, 7, 16),
                9.0,
                "Leonardo DiCaprio",
                GENRE.SCI_FI,
                150,
                "https://example.com/inception.jpg"
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
                8.8, "Leonardo DiCaprio", GENRE.SCI_FI, 148, "https://example.com/inception.jpg");

        FilmDTO filmDTO = new FilmDTO(
                "   ",
                null,
                9.0,
                null,
                null,
                150,
                null
        );

        when(filmsRepository.findById(filmId)).thenReturn(Optional.of(existingFilm));

        Film updatedFilm = new Film(
                filmId,
                "Inception",
                LocalDate.of(2010, 7, 16),
                9.0,
                "Leonardo DiCaprio",
                GENRE.SCI_FI,
                150,
                "https://example.com/inception.jpg"
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
                148,
                "https://example.com/inception.jpg"
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
                8.8, "Leonardo DiCaprio", GENRE.SCI_FI, 148, "https://example.com/inception.jpg");

        FilmDTO filmDTO = new FilmDTO(
                "Inception Updated",
                LocalDate.of(2010, 7, 16),
                9.0,
                "Leonardo DiCaprio, Joseph Gordon-Levitt",
                GENRE.SCI_FI,
                150,
                "https://example.com/inception-updated.jpg"
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
                148,
                "https://example.com/inception.jpg"
        );

        when(idService.generateId()).thenReturn("123");

        Film savedFilm = new Film(
                "123",
                filmDTO.title(),
                filmDTO.releaseDate(),
                filmDTO.rate(),
                filmDTO.casts(),
                filmDTO.genre(),
                filmDTO.duration(),
                filmDTO.poster()
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
                null,
                "https://example.com/inception.jpg"
        );

        when(idService.generateId()).thenReturn("123");

        Film savedFilm = new Film(
                "123",
                filmDTO.title(),
                filmDTO.releaseDate(),
                filmDTO.rate(),
                filmDTO.casts(),
                filmDTO.genre(),
                filmDTO.duration(),
                filmDTO.poster()
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
                8.8, "Leonardo DiCaprio", GENRE.SCI_FI, 148, "https://example.com/inception.jpg");

        FilmDTO filmDTO = new FilmDTO(
                "Inception Updated",
                LocalDate.of(2010, 7, 16),
                null,
                "Leonardo DiCaprio, Joseph Gordon-Levitt",
                GENRE.SCI_FI,
                null,
                "https://example.com/inception-updated.jpg"
        );

        when(filmsRepository.findById(filmId)).thenReturn(Optional.of(existingFilm));

        Film updatedFilm = new Film(
                filmId,
                filmDTO.title(),
                filmDTO.releaseDate(),
                existingFilm.rate(), // !!! rate remains unchanged
                filmDTO.casts(),
                filmDTO.genre(),
                existingFilm.duration(), // !!! duration remains unchanged
                filmDTO.poster()
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
                8.8, "Leonardo DiCaprio", GENRE.SCI_FI, 148, "https://example.com/inception.jpg");

        FilmDTO filmDTO = new FilmDTO(
                null,
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
        assertEquals(LocalDate.of(2010, 7, 16), result.releaseDate());
        assertEquals(8.8, result.rate());
        assertEquals("Leonardo DiCaprio", result.casts());
        assertEquals(GENRE.SCI_FI, result.genre());
        assertEquals(148, result.duration());

        verify(filmsRepository).findById(filmId);
        verify(filmsRepository).save(any(Film.class));
    }

    @Test
    void addFilm_whenGenreIsNull_addsFilmSuccessfully() {
        // given
        FilmDTO filmDTO = new FilmDTO(
                "Inception",
                LocalDate.of(2010, 7, 16),
                8.8,
                "Leonardo DiCaprio",
                null,
                148,
                "https://example.com/inception.jpg"
        );

        when(idService.generateId()).thenReturn("123");

        Film savedFilm = new Film(
                "123",
                filmDTO.title(),
                filmDTO.releaseDate(),
                filmDTO.rate(),
                filmDTO.casts(),
                filmDTO.genre(),
                filmDTO.duration(),
                filmDTO.poster()
        );

        when(filmsRepository.save(any(Film.class))).thenReturn(savedFilm);

        // when
        Film result = filmsService.addFilm(filmDTO);

        // then
        assertThat(result.id()).isEqualTo("123");
        assertThat(result.title()).isEqualTo("Inception");
        assertThat(result.genre()).isNull();

        verify(filmsRepository, times(1)).save(any(Film.class));
        verify(idService, times(1)).generateId();
    }

    @Test
    void updateFilm_whenGenreIsNull_updatesFilmSuccessfully() {
        String filmId = "123";
        Film existingFilm = new Film(filmId, "Inception", LocalDate.of(2010, 7, 16),
                8.8, "Leonardo DiCaprio", GENRE.SCI_FI, 148, "https://example.com/inception.jpg");

        FilmDTO filmDTO = new FilmDTO(
                "Inception Updated",
                LocalDate.of(2010, 7, 16),
                9.0,
                "Leonardo DiCaprio, Joseph Gordon-Levitt",
                null,
                150,
                "https://example.com/inception-updated.jpg"
        );

        when(filmsRepository.findById(filmId)).thenReturn(Optional.of(existingFilm));

        Film updatedFilm = new Film(
                filmId,
                filmDTO.title(),
                filmDTO.releaseDate(),
                filmDTO.rate(),
                filmDTO.casts(),
                existingFilm.genre(), // !!! genre remains unchanged
                filmDTO.duration(),
                filmDTO.poster()
        );

        when(filmsRepository.save(any(Film.class))).thenReturn(updatedFilm);

        Film result = filmsService.updateFilm(filmId, filmDTO);

        assertNotNull(result);
        assertEquals(filmId, result.id());
        assertEquals("Inception Updated", result.title());
        assertEquals(GENRE.SCI_FI, result.genre()); // genre should remain unchanged

        verify(filmsRepository).findById(filmId);
        verify(filmsRepository).save(any(Film.class));
    }

    @Test
    void addFilm_whenCastsIsNull_addsFilmSuccessfully() {
        // given
        FilmDTO filmDTO = new FilmDTO(
                "Inception",
                LocalDate.of(2010, 7, 16),
                8.8,
                null,
                GENRE.SCI_FI,
                148,
                "https://example.com/inception.jpg"
        );

        when(idService.generateId()).thenReturn("123");

        Film savedFilm = new Film(
                "123",
                filmDTO.title(),
                filmDTO.releaseDate(),
                filmDTO.rate(),
                filmDTO.casts(),
                filmDTO.genre(),
                filmDTO.duration(),
                filmDTO.poster()
        );

        when(filmsRepository.save(any(Film.class))).thenReturn(savedFilm);

        // when
        Film result = filmsService.addFilm(filmDTO);

        // then
        assertThat(result.id()).isEqualTo("123");
        assertThat(result.title()).isEqualTo("Inception");
        assertThat(result.casts()).isNull();

        verify(filmsRepository, times(1)).save(any(Film.class));
        verify(idService, times(1)).generateId();
    }

    @Test
    void updateFilm_whenCastsIsNull_updatesFilmSuccessfully() {
        String filmId = "123";
        Film existingFilm = new Film(filmId, "Inception", LocalDate.of(2010, 7, 16),
                8.8, "Leonardo DiCaprio", GENRE.SCI_FI, 148, "https://example.com/inception.jpg");
        FilmDTO filmDTO = new FilmDTO(
                "Inception Updated",
                LocalDate.of(2010, 7, 16),
                9.0,
                null,
                GENRE.SCI_FI,
                150,
                "https://example.com/inception-updated.jpg"
        );
        when(filmsRepository.findById(filmId)).thenReturn(Optional.of(existingFilm));
        Film updatedFilm = new Film(
                filmId,
                filmDTO.title(),
                filmDTO.releaseDate(),
                filmDTO.rate(),
                existingFilm.casts(), // !!! casts remains unchanged
                filmDTO.genre(),
                filmDTO.duration(),
                filmDTO.poster()
        );
        when(filmsRepository.save(any(Film.class))).thenReturn(updatedFilm);
        Film result = filmsService.updateFilm(filmId, filmDTO);
        assertNotNull(result);
        assertEquals(filmId, result.id());
        assertEquals("Inception Updated", result.title());
        assertEquals("Leonardo DiCaprio", result.casts()); // casts should remain unchanged
        verify(filmsRepository).findById(filmId);
        verify(filmsRepository).save(any(Film.class));
    }

    @Test
    void addFilm_whenPosterIsNull_addsFilmSuccessfully() {
        // given
        FilmDTO filmDTO = new FilmDTO(
                "Inception",
                LocalDate.of(2010, 7, 16),
                8.8,
                "Leonardo DiCaprio",
                GENRE.SCI_FI,
                148,
                null
        );

        when(idService.generateId()).thenReturn("123");

        Film savedFilm = new Film(
                "123",
                filmDTO.title(),
                filmDTO.releaseDate(),
                filmDTO.rate(),
                filmDTO.casts(),
                filmDTO.genre(),
                filmDTO.duration(),
                filmDTO.poster()
        );

        when(filmsRepository.save(any(Film.class))).thenReturn(savedFilm);

        // when
        Film result = filmsService.addFilm(filmDTO);

        // then
        assertThat(result.id()).isEqualTo("123");
        assertThat(result.title()).isEqualTo("Inception");
        assertThat(result.poster()).isNull();

        verify(filmsRepository, times(1)).save(any(Film.class));
        verify(idService, times(1)).generateId();
    }

    @Test
    void updateFilm_whenPosterIsNull_updatesFilmSuccessfully() {
        String filmId = "123";
        Film existingFilm = new Film(filmId, "Inception", LocalDate.of(2010, 7, 16),
                8.8, "Leonardo DiCaprio", GENRE.SCI_FI, 148, "https://example.com/inception.jpg");
        FilmDTO filmDTO = new FilmDTO(
                "Inception Updated",
                LocalDate.of(2010, 7, 16),
                9.0,
                "Leonardo DiCaprio, Joseph Gordon-Levitt",
                GENRE.SCI_FI,
                150,
                null
        );
        when(filmsRepository.findById(filmId)).thenReturn(Optional.of(existingFilm));
        Film updatedFilm = new Film(
                filmId,
                filmDTO.title(),
                filmDTO.releaseDate(),
                filmDTO.rate(),
                filmDTO.casts(),
                filmDTO.genre(),
                filmDTO.duration(),
                existingFilm.poster() // !!! poster remains unchanged
        );
        when(filmsRepository.save(any(Film.class))).thenReturn(updatedFilm);
        Film result = filmsService.updateFilm(filmId, filmDTO);
        assertNotNull(result);
        assertEquals(filmId, result.id());
        assertEquals("Inception Updated", result.title());
        assertEquals("https://example.com/inception.jpg", result.poster()); // poster should remain unchanged
        verify(filmsRepository).findById(filmId);
        verify(filmsRepository).save(any(Film.class));
    }

    @Test
    void addFilm_whenReleaseDateIsNull_addsFilmSuccessfully() {
        // given
        FilmDTO filmDTO = new FilmDTO(
                "Inception",
                null,
                8.8,
                "Leonardo DiCaprio",
                GENRE.SCI_FI,
                148,
                "https://example.com/inception.jpg"
        );

        when(idService.generateId()).thenReturn("123");

        Film savedFilm = new Film(
                "123",
                filmDTO.title(),
                filmDTO.releaseDate(),
                filmDTO.rate(),
                filmDTO.casts(),
                filmDTO.genre(),
                filmDTO.duration(),
                filmDTO.poster()
        );

        when(filmsRepository.save(any(Film.class))).thenReturn(savedFilm);

        // when
        Film result = filmsService.addFilm(filmDTO);

        // then
        assertThat(result.id()).isEqualTo("123");
        assertThat(result.title()).isEqualTo("Inception");
        assertThat(result.releaseDate()).isNull();

        verify(filmsRepository, times(1)).save(any(Film.class));
        verify(idService, times(1)).generateId();
    }
    @Test
    void updateFilm_whenReleaseDateIsNull_updatesFilmSuccessfully() {
        String filmId = "123";
        Film existingFilm = new Film(filmId, "Inception", LocalDate.of(2010, 7, 16),
                8.8, "Leonardo DiCaprio", GENRE.SCI_FI, 148, "https://example.com/inception.jpg");
        FilmDTO filmDTO = new FilmDTO(
                "Inception Updated",
                null,
                9.0,
                "Leonardo DiCaprio, Joseph Gordon-Levitt",
                GENRE.SCI_FI,
                150,
                "https://example.com/inception-updated.jpg"
        );
        when(filmsRepository.findById(filmId)).thenReturn(Optional.of(existingFilm));
        Film updatedFilm = new Film(
                filmId,
                filmDTO.title(),
                existingFilm.releaseDate(), // !!! release date remains unchanged
                filmDTO.rate(),
                filmDTO.casts(),
                filmDTO.genre(),
                filmDTO.duration(),
                filmDTO.poster()
        );
        when(filmsRepository.save(any(Film.class))).thenReturn(updatedFilm);
        Film result = filmsService.updateFilm(filmId, filmDTO);
        assertNotNull(result);
        assertEquals(filmId, result.id());
        assertEquals("Inception Updated", result.title());
        assertEquals(LocalDate.of(2010, 7, 16), result.releaseDate()); // release date should remain unchanged
        verify(filmsRepository).findById(filmId);
        verify(filmsRepository).save(any(Film.class));
    }

    @Test
    void addFilm_whenAllOptionalFieldsAreNull_addsFilmSuccessfully() {
        // given
        FilmDTO filmDTO = new FilmDTO(
                "Inception",
                null,
                null,
                null,
                null,
                null,
                null
        );

        when(idService.generateId()).thenReturn("123");

        Film savedFilm = new Film(
                "123",
                filmDTO.title(),
                filmDTO.releaseDate(),
                filmDTO.rate(),
                filmDTO.casts(),
                filmDTO.genre(),
                filmDTO.duration(),
                filmDTO.poster()
        );

        when(filmsRepository.save(any(Film.class))).thenReturn(savedFilm);

        // when
        Film result = filmsService.addFilm(filmDTO);

        // then
        assertThat(result.id()).isEqualTo("123");
        assertThat(result.title()).isEqualTo("Inception");
        assertThat(result.releaseDate()).isNull();
        assertThat(result.rate()).isNull();
        assertThat(result.casts()).isNull();
        assertThat(result.genre()).isNull();
        assertThat(result.duration()).isNull();
        assertThat(result.poster()).isNull();

        verify(filmsRepository, times(1)).save(any(Film.class));
        verify(idService, times(1)).generateId();
    }

    @Test
    void filterFilmsByGenre_existingGenre_returnsFilteredFilms() {
        GENRE genre = GENRE.SCI_FI;
        Film film1 = new Film("1", "Inception", LocalDate.of(2010, 7, 16),
                8.8, "Leonardo DiCaprio", GENRE.SCI_FI, 148, "https://example.com/inception.jpg");
        Film film2 = new Film("2", "The Matrix", LocalDate.of(1999, 3, 31),
                8.7, "Keanu Reeves", GENRE.SCI_FI, 136, "https://example.com/matrix.jpg");

        when(filmsRepository.findFilmsByGenre(genre)).thenReturn(List.of(film1, film2));

        List<Film> result = filmsService.getFilmsByFilter(null, genre.name(), null);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(film -> film.genre() == GENRE.SCI_FI));

        verify(filmsRepository).findFilmsByGenre(genre);
    }

    @Test
    void  filterFilmsByGenre_emptyGenre_returnsEmptyFilms() {
        GENRE genre = GENRE.SCI_FI;

        when(filmsRepository.findFilmsByGenre(genre)).thenReturn(List.of());

        List<Film> result = filmsService.getFilmsByFilter(null, genre.name(), null);

        assertNotNull(result);
        assertEquals(0, result.size());

        verify(filmsRepository).findFilmsByGenre(genre);
    }

    @Test
    void filterFilmsByYear_existingYear_returnsFilteredFilms() {
        int year = 2010;
        Film film1 = new Film("1", "Inception", LocalDate.of(2010, 7, 16),
                8.8, "Leonardo DiCaprio", GENRE.SCI_FI, 148, "https://example.com/inception.jpg");
        Film film2 = new Film("2", "Toy Story 3", LocalDate.of(2010, 6, 18),
                8.3, "Tom Hanks", GENRE.ANIMATION, 103, "https://example.com/toystory3.jpg");

        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);

        when(filmsRepository.findFilmsByReleaseDateBetween(start, end)).thenReturn(List.of(film1, film2));

        List<Film> result = filmsService.getFilmsByFilter(year, null, null);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(film -> film.releaseDate().getYear() == year));

        verify(filmsRepository).findFilmsByReleaseDateBetween(start, end);
    }

    @Test
    void filterFilmsByYear_noFilmsInYear_returnsEmptyList() {
        int year = 2022;
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);

        when(filmsRepository.findFilmsByReleaseDateBetween(start, end)).thenReturn(List.of());

        List<Film> result = filmsService.getFilmsByFilter(year, null, null);

        assertNotNull(result);
        assertEquals(0, result.size());

        verify(filmsRepository).findFilmsByReleaseDateBetween(start, end);
    }

    @Test
    void filterFilmByRate_existingRate_returnsFilteredFilms() {
        double rate = 8.5;
                Film film = new Film("2", "Toy Story 3", LocalDate.of(2010, 6, 18),
                8.5, "Tom Hanks", GENRE.ANIMATION, 103, "https://example.com/toystory3.jpg");

        when(filmsRepository.findFilmsByRateAfter(rate)).thenReturn(List.of(film));

        List<Film> result = filmsService.getFilmsByFilter(null, null, rate);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.stream().allMatch(film2 -> film2.rate() >= rate));

        verify(filmsRepository).findFilmsByRateAfter(rate);
    }

    @Test
    void filterFilmByRate_noFilmsWithRateAboveThreshold_returnsEmptyList() {
        double rate = 9.5;

        when(filmsRepository.findFilmsByRateAfter(rate)).thenReturn(List.of());

        List<Film> result = filmsService.getFilmsByFilter(null, null, rate);

        assertNotNull(result);
        assertEquals(0, result.size());

        verify(filmsRepository).findFilmsByRateAfter(rate);
    }

    @Test
    void filterFilmsByAllCriteria_returnsFilteredFilms() {
        int year = 2010;
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        GENRE genre = GENRE.SCI_FI;
        double rate = 8.5;
        Film film1 = new Film("1", "Inception", LocalDate.of(2010, 7, 16),
                8.5, "Leonardo DiCaprio", GENRE.SCI_FI, 148, "https://example.com/inception.jpg");

        when(filmsRepository.findFilmsByGenreAndRateAfterAndReleaseDateBetween(genre, rate, start, end)).thenReturn(List.of(film1));

        List<Film> result = filmsService.getFilmsByFilter(year, genre.name(), rate);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.stream().allMatch(film -> film.releaseDate().getYear() == year));
        assertTrue(result.stream().allMatch(film -> film.genre() == genre));
        assertTrue(result.stream().allMatch(film -> film.rate() >= rate));

        verify(filmsRepository).findFilmsByGenreAndRateAfterAndReleaseDateBetween(genre, rate, start, end);

    }


    @Test
    void filterFilmsByInvalidGenre_throwsException() throws IllegalArgumentException {
        String invalidGenre = "INVALID_GENRE";

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> filmsService.getFilmsByFilter(null, invalidGenre, null));
        assertEquals("Invalid genre: INVALID_GENRE", ex.getMessage());

    verify(filmsRepository, never()).findFilmsByGenreAndRateAfterAndReleaseDateBetween(any(GENRE.class),
            isNull(Double.class),
            isNull(LocalDate.class),
            isNull(LocalDate.class));
    }

    @Test
    void filterFilmsByInvalidRate_throwsException() {
        double invalidRate = -1.0;

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> filmsService.getFilmsByFilter(null, null, invalidRate));
        assertEquals("Rate must be between 0.0 and 10.0: -1.0", ex.getMessage());

        verify(filmsRepository, never()).findFilmsByRateAfter(invalidRate);
    }

    @Test
    void filterFilmsByInvalidYear_throwsException() {
        int invalidYear = 1800;
        LocalDate start = LocalDate.of(invalidYear, 7, 16);
        LocalDate end = LocalDate.of(invalidYear, 12, 31);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> filmsService.getFilmsByFilter(invalidYear, null, null));
        assertEquals("Year must be between 1888 and the current year: 1800", ex.getMessage());

        verify(filmsRepository, never()).findFilmsByReleaseDateBetween(start, end);
    }

    @Test
    void filterFilmsByYearInFuture_throwsException() {
        int invalidYear = LocalDate.now().getYear() + 1;
        LocalDate start = LocalDate.of(invalidYear, 7, 16);
        LocalDate end = LocalDate.of(invalidYear, 12, 31);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> filmsService.getFilmsByFilter(invalidYear, null, null));
        assertEquals("Year must be between 1888 and the current year: " + invalidYear, ex.getMessage());

        verify(filmsRepository, never()).findFilmsByReleaseDateBetween(start, end);
    }

    @Test
    void filterFilmsByRate_whenRateIsBigerAlsThan10_throwsException() {
        double invalidRate = 11.0;

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> filmsService.getFilmsByFilter(null, null, invalidRate));
        assertEquals("Rate must be between 0.0 and 10.0: 11.0", ex.getMessage());

        verify(filmsRepository, never()).findFilmsByRateAfter(invalidRate);

    }

    @Test
    void filterFilmsByRate_whenRateIsSmallerThan10_throwsException() {
        double invalidRate = -0.1;

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> filmsService.getFilmsByFilter(null, null, invalidRate));
        assertEquals("Rate must be between 0.0 and 10.0: -0.1", ex.getMessage());

        verify(filmsRepository, never()).findFilmsByRateAfter(invalidRate);

    }

    @Test
    void getFilmById_whenFilmIdIsNull_throwsException() {
        ElementNotFoundExceptions ex = assertThrows(ElementNotFoundExceptions.class, () -> filmsService.getFilmById(null));
        assertEquals("Film not found: null", ex.getMessage());
        verify(filmsRepository, never()).findById(anyString());
    }

    @Test
    void getFilmByRateAfterAndGenre_returnsFilteredFilms() {
        double rate = 8.0;
        GENRE genre = GENRE.DRAMA;
        Film film1 = new Film("1", "The Shawshank Redemption", LocalDate.of(1994, 9, 22),
                9.3, "Tim Robbins", GENRE.DRAMA, 142, "https://example.com/shawshank.jpg");
        Film film2 = new Film("2", "Forrest Gump", LocalDate.of(1994, 7, 6),
                8.8, "Tom Hanks", GENRE.DRAMA, 142, "https://example.com/forrestgump.jpg");

        when(filmsRepository.findFilmsByGenreAndRateAfter(genre, rate)).thenReturn(List.of(film1, film2));

        List<Film> result = filmsService.getFilmsByFilter(null, genre.name(), rate);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(film -> film.rate() > rate && film.genre() == genre));

        verify(filmsRepository).findFilmsByGenreAndRateAfter(genre, rate);
    }

    @Test
    void getFilmByRateAfterAndGenre_noFilmsMatchCriteria_returnsEmptyList() {
        double rate = 9.5;
        GENRE genre = GENRE.DRAMA;

        when(filmsRepository.findFilmsByGenreAndRateAfter(genre, rate)).thenReturn(List.of());

        List<Film> result = filmsService.getFilmsByFilter(null, genre.name(), rate);

        assertNotNull(result);
        assertEquals(0, result.size());

        verify(filmsRepository).findFilmsByGenreAndRateAfter(genre, rate);
    }

    @Test
    void getFilmByRateAfterAndYear_returnsFilteredFilms() {
        double rate = 8.0;
        int year = 1994;
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        Film film1 = new Film("1", "The Shawshank Redemption", LocalDate.of(1994, 9, 22),
                9.3, "Tim Robbins", GENRE.DRAMA, 142, "https://example.com/shawshank.jpg");
        Film film2 = new Film("2", "Forrest Gump", LocalDate.of(1994, 7, 6),
                8.8, "Tom Hanks", GENRE.DRAMA, 142, "https://example.com/forrestgump.jpg");

        when(filmsRepository.findFilmsByRateAfterAndReleaseDateBetween(rate, start, end))
                .thenReturn(List.of(film1, film2));

        List<Film> result = filmsService.getFilmsByFilter(year, null, rate);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(film -> film.rate() > rate && film.releaseDate().getYear() == year));

        verify(filmsRepository).findFilmsByRateAfterAndReleaseDateBetween(rate, start, end);
    }

    @Test
    void getFilmByRateAfterAndYear_noFilmsMatchCriteria_returnsEmptyList() {
        double rate = 9.5;
        int year = 1994;
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);

        when(filmsRepository.findFilmsByRateAfterAndReleaseDateBetween(rate, start, end))
                .thenReturn(List.of());

        List<Film> result = filmsService.getFilmsByFilter(year, null, rate);

        assertNotNull(result);
        assertEquals(0, result.size());

        verify(filmsRepository).findFilmsByRateAfterAndReleaseDateBetween(rate, start, end);
    }

    @Test
    void updateFilm_newPoster_returnUpdatedFilm(){
        String filmId = "123";
        Film existingFilm = new Film(filmId, "Inception", LocalDate.of(2010, 7, 16),
                8.8, "Leonardo DiCaprio", GENRE.SCI_FI, 148, "https://example.com/inception.jpg");

        FilmDTO filmDTO = new FilmDTO(
                null,
                null,
                9.0,
                null,
                null,
                150,
                null
        );

        when(filmsRepository.findById(filmId)).thenReturn(Optional.of(existingFilm));

        Film updatedFilm = new Film(
                filmId,
                "Inception",
                LocalDate.of(2010, 7, 16),
                9.0,
                "Leonardo DiCaprio",
                GENRE.SCI_FI,
                150,
                "https://example.com/inception2.jpg"
        );

        when(filmsRepository.save(any(Film.class))).thenReturn(updatedFilm);

        Film result = filmsService.updateFilm(filmId, filmDTO);

        assertNotNull(result);
        assertEquals(filmId, result.id());
        assertEquals("https://example.com/inception2.jpg", result.poster());
        assertEquals(9.0, result.rate());
        assertEquals(150, result.duration());

        verify(filmsRepository).findById(filmId);
        verify(filmsRepository).save(any(Film.class));
    }

    @Test
    void getFilmsByGenreAndYear_returnFilms(){
        GENRE genre = GENRE.DRAMA;
        int year = 1994;
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        Film film1 = new Film("1", "The Shawshank Redemption", LocalDate.of(1994, 9, 22),
                9.3, "Tim Robbins", GENRE.DRAMA, 142, "https://example.com/shawshank.jpg");
        Film film2 = new Film("2", "Forrest Gump", LocalDate.of(1994, 7, 6),
                8.8, "Tom Hanks", GENRE.DRAMA, 142, "https://example.com/forrestgump.jpg");

        when(filmsRepository.findFilmsByGenreAndReleaseDateBetween(genre, start, end))
                .thenReturn(List.of(film1, film2));

        List<Film> result = filmsService.getFilmsByFilter(year, genre.name(), null);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(film -> film.genre().equals(genre) && film.releaseDate().getYear() == year));

        verify(filmsRepository).findFilmsByGenreAndReleaseDateBetween(genre, start, end);
    }

    @Test
    void getHomepageImages_noRecentFilms_returnsEmptyList() {
        LocalDate now = LocalDate.now();
        LocalDate start = now.minusMonths(1);
        LocalDate end = now.plusMonths(1);

        when(filmsRepository.findFilmsByReleaseDateBetween(start, end))
                .thenReturn(List.of());

        List<String> posters = filmsService.getHomepageImages();

        assertNotNull(posters);
        assertTrue(posters.isEmpty());
    }

    @Test
    void getHomepageImages_returnsOnlyRecentFilms() {
        LocalDate now = LocalDate.now();

        Film recentFilm = new Film("1", "Recent Movie", now.minusDays(10),
                8.0, "Actor 1", GENRE.DRAMA, 120, "poster1.jpg");


        LocalDate start = now.minusMonths(1);
        LocalDate end = now.plusMonths(1);

        when(filmsRepository.findFilmsByReleaseDateBetween(start, end))
                .thenReturn(List.of(recentFilm));

        List<String> posters = filmsService.getHomepageImages();

        assertEquals(1, posters.size());
        assertEquals("poster1.jpg", posters.getFirst());
    }

}