package org.example.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.backend.model.Film;
import org.example.backend.model.FilmDTO;
import org.example.backend.model.GENRE;
import org.example.backend.repository.FilmsRepository;
import org.example.backend.service.ClientApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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

    @Autowired
    private MockRestServiceServer mockRestServiceServer;
    @Autowired
    private ClientApiService clientApiService;


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
                "https://example.com/inception.jpg",
                "This film is about ..."
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
                "https://example.com/inception.jpg",
                "This film is about ..."
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
                8.8, "Leonardo DiCaprio", GENRE.SCI_FI, 148, "https://example.com/inception.jpg",
                "This film is about ...");
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
                8.8, "Leonardo DiCaprio", GENRE.SCI_FI, 148, "https://example.com/inception.jpg",
                "This film is about ...");
        filmsRepository.save(film);

        FilmDTO updatedFilmDTO = new FilmDTO(
                "Inception Updated",
                LocalDate.of(2010, 7, 16),
                9.0,
                "Leonardo DiCaprio",
                GENRE.SCI_FI,
                150,
                "https://example.com/inception_updated.jpg",
                "This film is about ..."
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
                "https://example.com/inception_updated.jpg",
                "This film is about ..."
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
                "https://example.com/inception.jpg",
                "This film is about ..."
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
                "https://example.com/inception.jpg",
                "This film is about ..."
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
                8.8, "Leonardo DiCaprio", GENRE.SCI_FI, 148, "https://example.com/inception.jpg",
                "This film is about ...");
        Film film2 = new Film("2", "The Dark Knight", LocalDate.of(2008,7,18),
                9.0, "Christian Bale", GENRE.ACTION, 152, "https://example.com/dark_knight.jpg",
                "This film is about ...");
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
                8.8, "Leonardo DiCaprio", GENRE.SCI_FI, 148, "https://example.com/inception.jpg",
                "This film is about ...");
        Film film2 = new Film("2", "The Dark Knight", LocalDate.of(2008,7,18),
                9.0, "Christian Bale", GENRE.ACTION, 152, "https://example.com/dark_knight.jpg",
                "This film is about ...");
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
                        .param("rate", "8.8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("The Dark Knight"));
    }

    @Test
    void getFilmsByFilter_withNoParams_returnsAllFilms() throws Exception {
        // given
        Film film1 = new Film("1", "Inception", LocalDate.of(2010,7,16),
                8.8, "Leonardo DiCaprio", GENRE.SCI_FI, 148, "https://example.com/inception.jpg",
                "This film is about ...");
        Film film2 = new Film("2", "The Dark Knight", LocalDate.of(2008,7,18),
                9.0, "Christian Bale", GENRE.ACTION, 152, "https://example.com/dark_knight.jpg",
                "This film is about ...");
        filmsRepository.save(film1);
        filmsRepository.save(film2);

        // when + then
        mockMvc.perform(get("/api/films/filter"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getFilmsByFilter_withNoMatchingFilms_returnsEmptyList() throws Exception {
        // given
        Film film1 = new Film("1", "Inception", LocalDate.of(2010,7,16),
                8.8, "Leonardo DiCaprio", GENRE.SCI_FI, 148, "https://example.com/inception.jpg",
                "This film is about ...");
        Film film2 = new Film("2", "The Dark Knight", LocalDate.of(2008,7,18),
                9.0, "Christian Bale", GENRE.ACTION, 152, "https://example.com/dark_knight.jpg",
                "This film is about ...");
        filmsRepository.save(film1);
        filmsRepository.save(film2);

        // when + then
        mockMvc.perform(get("/api/films/filter")
                        .param("year", "2000"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        mockMvc.perform(get("/api/films/filter")
                        .param("genre", "COMEDY"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        mockMvc.perform(get("/api/films/filter")
                        .param("rate", "9.2"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
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

    @Test
    void getHomepageImages_returnsImageUrls() throws Exception {
        // given
        Film film1 = new Film("1", "Recent Movie 1", LocalDate.now().minusDays(10),
                7.5, "Actor 1", GENRE.DRAMA, 120, "https://example.com/recent1.jpg",
                "This film is about ...");
        Film film2 = new Film("2", "Recent Movie 2", LocalDate.now().plusDays(10),
                8.0, "Actor 2", GENRE.COMEDY, 110, "https://example.com/recent2.jpg",
                "This film is about ...");
        Film film3 = new Film("3", "Old Movie", LocalDate.now().minusMonths(3),
                6.5, "Actor 3", GENRE.HORROR, 130, "https://example.com/old.jpg",
                "This film is about ...");

        filmsRepository.save(film1);
        filmsRepository.save(film2);
        filmsRepository.save(film3);

        // when + then
        mockMvc.perform(get("/api/homepage_images"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0]").value("https://example.com/recent1.jpg"))
                .andExpect(jsonPath("$[1]").value("https://example.com/recent2.jpg"));
    }

    @Test
    void getHomepageImages_whenNoRecentFilms_returnsEmptyList() throws Exception {
        // given
        Film oldFilm1 = new Film("1", "Old Movie 1", LocalDate.now().minusMonths(3),
                6.5, "Actor 1", GENRE.DRAMA, 120, "https://example.com/old1.jpg",
                "This film is about ...");
        Film oldFilm2 = new Film("2", "Old Movie 2", LocalDate.now().minusMonths(4),
                7.0, "Actor 2", GENRE.COMEDY, 110, "https://example.com/old2.jpg",
                "This film is about ...");
        filmsRepository.save(oldFilm1);
        filmsRepository.save(oldFilm2);

        // when + then
        mockMvc.perform(get("/api/homepage_images"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getHomepageImages_whenNoFilms_returnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/homepage_images"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getFilmsByTitleFromApiClient() throws Exception {
        mockRestServiceServer
                .expect(requestTo("?apikey=key&type=movie&s=Matrix"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                    {
                        "Search": [
                            {
                                "Title": "The Matrix",
                                "Year": "1999",
                                "imdbID": "tt0133093",
                                "Type": "movie",
                                "Poster": "https://m.media-amazon.com/images/M/MV5BN2NmN2VhMTQtMDNiOS00NDlhLTliMjgtODE2ZTY0ODQyNDRhXkEyXkFqcGc@._V1_SX300.jpg"
                            },
                            {
                                "Title": "The Matrix Reloaded",
                                "Year": "2003",
                                "imdbID": "tt0234215",
                                "Type": "movie",
                                "Poster": "https://m.media-amazon.com/images/M/MV5BNjAxYjkxNjktYTU0YS00NjFhLWIyMDEtMzEzMTJjMzRkMzQ1XkEyXkFqcGc@._V1_SX300.jpg"
                            }
                        ],
                        "totalResults": "137",
                        "Response": "True"
                    }
                    """, MediaType.APPLICATION_JSON));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/search")
                        .param("title", "Matrix"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("""
                    {
                        "Search": [
                            {
                                "Title": "The Matrix",
                                "Year": "1999",
                                "imdbID": "tt0133093",
                                "Type": "movie",
                                "Poster": "https://m.media-amazon.com/images/M/MV5BN2NmN2VhMTQtMDNiOS00NDlhLTliMjgtODE2ZTY0ODQyNDRhXkEyXkFqcGc@._V1_SX300.jpg"
                            },
                            {
                                "Title": "The Matrix Reloaded",
                                "Year": "2003",
                                "imdbID": "tt0234215",
                                "Type": "movie",
                                "Poster": "https://m.media-amazon.com/images/M/MV5BNjAxYjkxNjktYTU0YS00NjFhLWIyMDEtMzEzMTJjMzRkMzQ1XkEyXkFqcGc@._V1_SX300.jpg"
                            }
                        ],
                        "totalResults": "137",
                        "Response": "True"
                    }
                    """));
    }

    @Test
    void getFilmsByTitleFromApiClient_whenNoFilm_returnsEmptyList() throws Exception {

        mockRestServiceServer
                .expect(requestTo("?apikey=key&type=movie&s=NonExistingMovie"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                    {
                        "Response": "False",
                        "Error": "Movie not found!"
                    }
                    """, MediaType.APPLICATION_JSON));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/search")
                        .param("title", "NonExistingMovie"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.Search").isEmpty());
    }

    @Test
    void getFilmsByIdFromApiClient_whenFilm_returnsFilms() throws Exception {
        String imdbID = "tt0133093";
        mockRestServiceServer
                .expect(requestTo("?apikey=key&i=tt0133093"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                            "Title": "The Matrix",
                            "Year": "1999",
                            "Released": "31 Mar 1999",
                            "Runtime": "136 min",
                            "Genre": "Action, Sci-Fi",
                            "Actors": "Keanu Reeves, Laurence Fishburne, Carrie-Anne Moss",
                            "Plot": "When a beautiful stranger leads computer hacker Neo to a forbidding underworld, he discovers the shocking truth--the life he knows is the elaborate deception of an evil cyber-intelligence.",
                            "Poster": "https://m.media-amazon.com/images/M/MV5BN2NmN2VhMTQtMDNiOS00NDlhLTliMjgtODE2ZTY0ODQyNDRhXkEyXkFqcGc@._V1_SX300.jpg",
                            "imdbRating": "8.7",
                            "imdbID": "tt0133093",
                            "Type": "movie"
                        }
    """, MediaType.APPLICATION_JSON));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/search/{imdbID}", imdbID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("The Matrix"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.releaseDate").value("31-03-1999"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.genre").value("ACTION"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.duration").value("136"));

    }

    @Test
    void getFilmsByIdFromApiClient_whenDurationIsWrong_returnsFilm() throws Exception {
        String imdbID = "tt0133093";
        mockRestServiceServer
                .expect(requestTo("?apikey=key&i=tt0133093"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                            "Title": "The Matrix",
                            "Year": "1999",
                            "Released": "31 Mar 1999",
                            "Runtime": "N/A",
                            "Genre": "Action, Sci-Fi",
                            "Actors": "Keanu Reeves, Laurence Fishburne, Carrie-Anne Moss",
                            "Plot": "When a beautiful stranger leads computer hacker Neo to a forbidding underworld, he discovers the shocking truth--the life he knows is the elaborate deception of an evil cyber-intelligence.",
                            "Poster": "https://m.media-amazon.com/images/M/MV5BN2NmN2VhMTQtMDNiOS00NDlhLTliMjgtODE2ZTY0ODQyNDRhXkEyXkFqcGc@._V1_SX300.jpg",
                            "imdbRating": "8.7",
                            "imdbID": "tt0133093",
                            "Type": "movie"
                                         }
    """, MediaType.APPLICATION_JSON));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/search/{imdbID}", imdbID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("The Matrix"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.releaseDate").value("31-03-1999"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.duration").value("0"));
    }


    @ParameterizedTest
    @ValueSource(strings = {
            "\"Genre\": \"Doc, SciFi\",",
            "\"Genre\": \"\",",
            ""
    })
    void getFilmsByIdFromApiClient_whenGenreInvalidOrMissing_returnsFilmWithoutGenre(String genreField) throws Exception {
        String imdbID = "tt0133093";

        String response = """
            {
                "Title": "The Matrix",
                "Year": "1999",
                %s
                "Released": "31 Mar 1999",
                "Actors": "Keanu Reeves, Laurence Fishburne, Carrie-Anne Moss",
                "Plot": "When a beautiful stranger leads computer hacker Neo to a forbidding underworld, he discovers the shocking truth--the life he knows is the elaborate deception of an evil cyber-intelligence.",
                "Poster": "https://m.media-amazon.com/images/M/MV5BN2NmN2VhMTQtMDNiOS00NDlhLTliMjgtODE2ZTY0ODQyNDRhXkEyXkFqcGc@._V1_SX300.jpg",
                "imdbRating": "8.7",
                "imdbID": "tt0133093",
                "Type": "movie"
            }
            """.formatted(genreField);

        mockRestServiceServer
                .expect(requestTo("?apikey=key&i=tt0133093"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/search/{imdbID}", imdbID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("The Matrix"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.genre").doesNotExist());
    }

    @Test
    void getFilmsByIdFromApiClient_whenReleasedIsWrong_throwsException() {
        String imdbID = "tt0133093";
        mockRestServiceServer
                .expect(requestTo("?apikey=key&i=tt0133093"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{ \"Released\": \"N/A\" }", MediaType.APPLICATION_JSON));

        assertThrows(DateTimeParseException.class,
                () -> clientApiService.getMovieById(imdbID));
    }

    @Test
    void getFilmsByIdFromApiClient_whenReleasedIsNull_throwsException() {
        String imdbID = "tt0133093";
        mockRestServiceServer
                .expect(requestTo("?apikey=key&i=tt0133093"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{ \"Released\": \"\" }", MediaType.APPLICATION_JSON));

        assertThrows(IllegalArgumentException.class,
                () -> clientApiService.getMovieById(imdbID));
    }

    @Test
    void getFilmsByIdFromApiClient_whenReleasedIsEmpty_throwsException() {
        String imdbID = "tt0133093";
        mockRestServiceServer
                .expect(requestTo("?apikey=key&i=tt0133093"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{ }", MediaType.APPLICATION_JSON));

        assertThrows(IllegalArgumentException.class,
                () -> clientApiService.getMovieById(imdbID));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "\"imdbRating\": \"N/A\",",
            "\"imdbRating\": \"\",",
            ""
    })
    void getFilmsByIdFromApiClient_whenIMDBRatingInvalidOrMissing_returnsFilmWithoutGenre(String ratedField) throws Exception {
        String imdbID = "tt0133093";

        String response = """
            {
                "Title": "The Matrix",
                "Year": "1999",
                "Gerne": "Action",
                "Released": "31 Mar 1999",
                "Actors": "Keanu Reeves, Laurence Fishburne, Carrie-Anne Moss",
                "Plot": "When a beautiful stranger leads computer hacker Neo to a forbidding underworld, he discovers the shocking truth--the life he knows is the elaborate deception of an evil cyber-intelligence.",
                "Poster": "https://m.media-amazon.com/images/M/MV5BN2NmN2VhMTQtMDNiOS00NDlhLTliMjgtODE2ZTY0ODQyNDRhXkEyXkFqcGc@._V1_SX300.jpg",
                %s
                "imdbID": "tt0133093",
                "Type": "movie"
            }
            """.formatted(ratedField);

        mockRestServiceServer
                .expect(requestTo("?apikey=key&i=tt0133093"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/search/{imdbID}", imdbID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("The Matrix"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.imdbRating").doesNotExist());
    }

}