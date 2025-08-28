package org.example.backend.controller;

import org.example.backend.repository.FilmsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
}