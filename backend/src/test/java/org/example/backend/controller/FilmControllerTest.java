package org.example.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.backend.model.Film;
import org.example.backend.model.FilmDTO;
import org.example.backend.model.GENRE;
import org.example.backend.repository.FilmsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureMockRestServiceServer
@WithMockUser(username = "testuser")
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private FilmsRepository filmsRepository;


    @BeforeEach
    void cleanDb() {
        filmsRepository.deleteAll();
    }

    @Test
    void getFilms_returnsOk() throws Exception {
        mockMvc.perform(get("/api/films"))
                .andExpect(status().isOk());
    }

    @Test
    void addFilm_shouldPersistInDb() throws Exception {
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

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); //!!!!! Support for Java 8 date/time types
        String filmJson = objectMapper.writeValueAsString(filmDTO);

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/films")
                       // .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isCreated())
                .andExpect(content().json(filmJson));


        // then
        Optional<Film> saved = filmsRepository.findAll().stream().findFirst();
        assertThat(saved).isPresent();
        assertThat(saved.get().title()).isEqualTo("Inception");
        assertThat(saved.get().genre()).isEqualTo(GENRE.SCI_FI);
    }

    @Test
    void deleteFilm_existingId_returnsNoContent() throws Exception {
        Film film = new Film(
                "123",
                "Inception",
                LocalDate.of(2010, 7, 16),
                8.8,
                "Leonardo DiCaprio",
                GENRE.SCI_FI,
                148,
                "https://example.com/inception.jpg"
        );
        filmsRepository.save(film);
        // when + then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/films/{id}", "123")
                        //.with(csrf())
                )
                .andExpect(status().isNoContent());
        boolean exists = filmsRepository.existsById("123");
        assertFalse(exists);
    }

    @Test
    void deleteFilm_nonExistingId_returnsNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/films/{id}", "999")
                        //.with(csrf())
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void getFilmById_existingId_returnsFilm() throws Exception {

        Film film = new Film("123", "Inception", LocalDate.of(2010,7,16),
                8.8, "Leonardo DiCaprio", GENRE.SCI_FI, 148, "https://example.com/inception.jpg");
        filmsRepository.save(film);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/films/{id}", "123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.title").value("Inception"));
    }

    @Test
    void getFilmById_nonExistingId_returnsNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/films/{id}", "999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateFilm_existingId_returnsUpdatedFilm() throws Exception {
        // given
        Film film = new Film("123", "Inception", LocalDate.of(2010,7,16),
                8.8, "Leonardo DiCaprio", GENRE.SCI_FI, 148, "https://example.com/inception.jpg");
        filmsRepository.save(film);

        FilmDTO updatedFilmDTO = new FilmDTO(
                "Inception Updated",
                LocalDate.of(2010, 7, 16),
                9.0,
                "Leonardo DiCaprio",
                GENRE.SCI_FI,
                150,
                "https://example.com/inception_updated.jpg"
        );

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String updatedFilmJson = objectMapper.writeValueAsString(updatedFilmDTO);

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.put("/api/films/{id}", "123")
                        //.with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedFilmJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.title").value("Inception Updated"))
                .andExpect(jsonPath("$.rate").value(9.0))
                .andExpect(jsonPath("$.duration").value(150));
    }


    @Test
    void updateFilm_nonExistingId_returnsNotFound() throws Exception {
        FilmDTO updatedFilmDTO = new FilmDTO(
                "Inception Updated",
                LocalDate.of(2010, 7, 16),
                9.0,
                "Leonardo DiCaprio",
                GENRE.SCI_FI,
                150,
                "https://example.com/inception_updated.jpg"
        );

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String updatedFilmJson = objectMapper.writeValueAsString(updatedFilmDTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/films/{id}", "999")
                       // .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedFilmJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void addFilm_withNullTitle_returnsBadRequest() throws Exception {
        FilmDTO filmDTO = new FilmDTO(
                null,
                LocalDate.of(2010, 7, 16),
                8.8,
                "Leonardo DiCaprio",
                GENRE.SCI_FI,
                148,
                "https://example.com/inception.jpg"
        );

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String filmJson = objectMapper.writeValueAsString(filmDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/films")
                       // .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addFilm_withBlankTitle_returnsBadRequest() throws Exception {
        FilmDTO filmDTO = new FilmDTO(
                "   ",
                LocalDate.of(2010, 7, 16),
                8.8,
                "Leonardo DiCaprio",
                GENRE.SCI_FI,
                148,
                "https://example.com/inception.jpg"
        );

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String filmJson = objectMapper.writeValueAsString(filmDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/films")
                        //.with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getFilms_whenNoFilms_returnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/films").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getFilms_whenFilmsExist_returnsFilmList() throws Exception {
        // given
        Film film1 = new Film("1", "Inception", LocalDate.of(2010,7,16),
                8.8, "Leonardo DiCaprio", GENRE.SCI_FI, 148, "https://example.com/inception.jpg");
        Film film2 = new Film("2", "The Dark Knight", LocalDate.of(2008,7,18),
                9.0, "Christian Bale", GENRE.ACTION, 152, "https://example.com/dark_knight.jpg");
        filmsRepository.save(film1);
        filmsRepository.save(film2);

        // when + then
        mockMvc.perform(get("/api/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Inception"))
                .andExpect(jsonPath("$[1].title").value("The Dark Knight"));
    }

    @Test
    void getFilmsByFilter_withYear_withGenre_withRate_returnsFilteredFilms() throws Exception {
        // given
        Film film1 = new Film("1", "Inception", LocalDate.of(2010,7,16),
                8.8, "Leonardo DiCaprio", GENRE.SCI_FI, 148, "https://example.com/inception.jpg");
        Film film2 = new Film("2", "The Dark Knight", LocalDate.of(2008,7,18),
                9.0, "Christian Bale", GENRE.ACTION, 152, "https://example.com/dark_knight.jpg");
        filmsRepository.save(film1);
        filmsRepository.save(film2);

        // when + then
        mockMvc.perform(get("/api/films/filter")
                        .param("year", "2010"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Inception"));

        mockMvc.perform(get("/api/films/filter")
                        .param("genre", "ACTION"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("The Dark Knight"));

        mockMvc.perform(get("/api/films/filter")
                        .param("rate", "9.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("The Dark Knight"));
    }

    @Test
    void getFilmsByFilter_withNoParams_returnsAllFilms() throws Exception {
        // given
        Film film1 = new Film("1", "Inception", LocalDate.of(2010,7,16),
                8.8, "Leonardo DiCaprio", GENRE.SCI_FI, 148, "https://example.com/inception.jpg");
        Film film2 = new Film("2", "The Dark Knight", LocalDate.of(2008,7,18),
                9.0, "Christian Bale", GENRE.ACTION, 152, "https://example.com/dark_knight.jpg");
        filmsRepository.save(film1);
        filmsRepository.save(film2);

        // when + then
        mockMvc.perform(get("/api/films/filter"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithAnonymousUser
    void getFilms_withoutAuthentication_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/films").with(request -> {
                    request.setRemoteUser(null);
                    return request;
                }))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void getFilms_withoutAuthentication2_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/films"))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void filterFilms_withInvalidGenre_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/films/filter")
                        .param("genre", "INVALID_GENRE"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void filterFilms_withInvalidYear_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/films/filter")
                        .param("year", "1800"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/films/filter")
                        .param("year", "3000"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void filterFilms_withInvalidRate_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/films/filter")
                        .param("rate", "-1.0"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/films/filter")
                        .param("rate", "11.0"))
                .andExpect(status().isBadRequest());
    }
}