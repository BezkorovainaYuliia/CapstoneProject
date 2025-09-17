package org.example.backend.service;

import org.example.backend.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


@Service
public class ClientApiService {
    private final RestClient restClient;
    @Value("${apikey}")
    private String apiKey2;
    @Value("${baseUri}")
    private String baseUri2;

    public ClientApiService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl(baseUri2)
                .build();
    }

    public SearchResponse getListOfChosenFilmByName(String title){
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("apikey", apiKey2)
                        .queryParam("type", "movie")
                        .queryParam("s", title)
                        .build())
                .retrieve()
                .body(SearchResponse.class);
    }

    public FilmDTO getMovieById(String imdbId) {

        MovieDetails movieDetails = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("apikey", apiKey2)
                        .queryParam("i", imdbId)
                        .build())
                .retrieve()
                .body(MovieDetails.class);


            //convert to Integer
            Integer duration = safeParseDuration(movieDetails.runtime());
            //convert to LocalDate
            LocalDate releaseDate = parseReleased(movieDetails.released());
            //convert to GENRE
            GENRE genre = fromString(movieDetails.genre());

        return new FilmDTO(
                movieDetails.title(), //title
                releaseDate, //releaseDate
                Double.valueOf(movieDetails.rated()), //rate Double
                movieDetails.actors(), //casts
                genre, // GENRE enum
                duration,// duration
                movieDetails.poster(), //poster
                movieDetails.plot() //description
        );
    }

    //convert Duration to Integer
    private static Integer safeParseDuration(String runtime){
        if (runtime == null) {
            return 0;
        }

        String digits = runtime.replaceAll("\\D+", "");
        if (digits.isEmpty()) {
            return 0;
        }
            return Integer.parseInt(digits);
    }

    //convert LocalDate
    private static LocalDate parseReleased(String released) {
        if (released == null || released.isBlank()) {
            throw new IllegalArgumentException("Released date cannot be null or blank");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
        return LocalDate.parse(released, formatter);
    }

    //convert to enum GENRE
    private static GENRE fromString(String genres) {
        if (genres == null || genres.isBlank()) {
            return null;
        }

        String[] parts = genres.split(",");
        for (String part : parts) {
            String normalized = part.trim().toUpperCase().replace("-", "_").replace(" ", "_");
            try {
                return GENRE.valueOf(normalized);
            } catch (IllegalArgumentException e) {
                //
            }
        }
        return null;
    }
}
