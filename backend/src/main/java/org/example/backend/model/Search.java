package org.example.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.With;

@With
public record Search(@JsonProperty("Title")
                     String title,
                     @JsonProperty("Year")
                     String year,
                     @JsonProperty("imdbID")
                     String imdbID,
                     @JsonProperty("Type")
                     String type,
                     @JsonProperty("Poster")
                     String poster) {
}
