package org.example.backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.With;

import java.time.LocalDate;

@With
public record FilmDTO(
        String title,
        //!!!! i need this format to parse the date from the request body
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
        LocalDate release_date,
        Double rate,
        String casts,
        GENRE genre,
        Integer duration,
        String poster) {
}
