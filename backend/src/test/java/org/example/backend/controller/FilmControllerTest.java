package org.example.backend.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.backend.model.Film;
import org.example.backend.model.FilmDTO;
import org.example.backend.model.GENRE;
import org.example.backend.repository.FilmsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureMockRestServiceServer

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
                148
        );

        System.out.println(filmDTO);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); //!!!!! Support for Java 8 date/time types
        String filmJson = objectMapper.writeValueAsString(filmDTO);

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/films")
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

}