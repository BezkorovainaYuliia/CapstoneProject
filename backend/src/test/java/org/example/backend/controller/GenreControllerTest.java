package org.example.backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GenreControllerTest {

    @Autowired
    GenreController controller;

    @Test
    void contextLoads() {
        assertNotNull(controller);
    }

    @Test
    void getGenres_shouldReturnAllGenres() {
        var genres = controller.getGenres();
        assertEquals(11, genres.size());
        assertTrue(genres.contains("ACTION"));
        assertTrue(genres.contains("COMEDY"));
        assertTrue(genres.contains("DRAMA"));
        assertTrue(genres.contains("HORROR"));
        assertTrue(genres.contains("SCI_FI"));
    }

    @Test
    void getGenres_shouldReturnGenreNames() {
        var genres = controller.getGenres();
        for (var genre : genres) {
            assertDoesNotThrow(() -> Enum.valueOf(org.example.backend.model.GENRE.class, genre));
        }
    }
}
