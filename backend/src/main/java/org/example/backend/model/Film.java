package org.example.backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.With;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@With
@Document(collection = "dbFilms")
public record Film(
        String id,
        String title,
        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDate release_date,
        double rate,
        String casts,
        String genre,
        int duration) {
}
