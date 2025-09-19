package org.example.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.With;

@With
public record MovieDetails(
        @JsonProperty("imdbID")
        String imdbID,
        @JsonProperty("Title")
        String title,
        @JsonProperty("Year")
        String year,
        @JsonProperty("imdbRating")
        String imdbRating,
        @JsonProperty("Released")
        String released,
        @JsonProperty("Runtime")
        String runtime,  //duration
        @JsonProperty("Genre")
        String genre, //list of genre
        @JsonProperty("Actors")
        String actors,
        @JsonProperty("Plot")
        String plot,  // description
        @JsonProperty("Poster")
        String poster
) {}

