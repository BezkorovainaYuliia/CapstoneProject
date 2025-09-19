import { useState } from "react";
import axios from "axios";
import { type SearchResponse, type Movie } from "./Types.ts";
import SearchFilmCard from "./UIElements/SearchFilmCard.tsx";

export default function Search() {
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
                console.log(response.data.Search);
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

    return ( <div className="p-4 max-w-4xl mx-auto">
        <div className="flex gap-2 mb-4">
            <input type="text"
                   placeholder="Search by title"
                   value={searchFilmByTitle}
                   onChange={(e) => setSearchFilmByTitle(e.target.value)}
                   onKeyDown={(e) => e.key === "Enter" && handleSearch()}
                   className="flex-1 px-3 py-2 border rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
            <button onClick={handleSearch}
                    className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition" >
                Search </button> </div>
        {loading && <p>Loading...</p>}
        {error && <p className="text-red-600">{error}</p>}
        {films.length > 0 && (
            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
                {films .slice()
                    .sort((a, b) => Number(b.Year) - Number(a.Year))
                    .map((film) => (
                        <SearchFilmCard
                            key={film.imdbID}
                            film={film} />
                    ))}
            </div> )}
    </div> );
}
