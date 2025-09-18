import { useState } from "react";
import axios from "axios";
import type { SearchResponse, Movie } from "./Types.ts";

export default function SearchFilm() {
    const [searchFilmByTitle, setSearchFilmByTitle] = useState<string>("");
    const [films, setFilms] = useState<Movie[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const handleSearch = async () => {
        if (!searchFilmByTitle.trim()) return;

        try {
            setLoading(true);
            setError(null);

            const response = await axios.get<SearchResponse>("/api/search", {
                params: { title: searchFilmByTitle },
                withCredentials: true,
            });

            if (response.data.Response === "True") {
                setFilms(response.data.Search);
            } else {
                setFilms([]);
                setError("Film not found");
            }
        } catch (err) {
            console.error(err);
            setError("Error by API");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="p-4 max-w-lg mx-auto">
            <div className="flex gap-2">
                <input
                    type="text"
                    placeholder="SearchFilm by title"
                    value={searchFilmByTitle}
                    onChange={(e) => setSearchFilmByTitle(e.target.value)}
                    onKeyDown={(e) => e.key === "Enter" && handleSearch()}
                    className="flex-1 px-3 py-2 border rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                <button
                    onClick={handleSearch}
                    className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition"
                >
                    SearchFilm
                </button>
            </div>

            <div className="mt-4">
                {loading && <p>Loading...</p>}
                {error && <p className="text-red-600">{error}</p>}

                {films.length > 0 && (
                    <ul className="mt-2 space-y-2">
                        {films.map((film) => (
                            <li key={film.imdbID} className="flex items-center gap-3">
                                <img
                                    src={film.Poster !== "N/A" ? film.Poster : "/placeholder.png"}
                                    alt={film.Title}
                                    className="w-12 h-16 object-cover rounded"
                                />
                                <span>
                  <strong>{film.Title}</strong> ({film.Year})
                </span>
                            </li>
                        ))}
                    </ul>
                )}
            </div>
        </div>
    );
}
