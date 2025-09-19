package org.example.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SearchResponse(
        @JsonProperty("Search")
        List<Search> search,
        @JsonProperty("totalResults")
        String totalResults,
        @JsonProperty("Response")
        String response) {
}
