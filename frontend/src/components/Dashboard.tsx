import axios from "axios";
import { useEffect, useState } from "react";
import type { Film } from "./Types.ts";
import FilmCard from "./FilmCard.tsx";
import FilterIcon from "./icons/FilterIcon.tsx";
import RedoIcon from "./icons/RedoIcon.tsx";

export default function Dashboard() {
    const [films, setFilms] = useState<Film[]>([]);
    const [genres, setGenres] = useState<string[]>([]);
    const [year, setYear] = useState<string>("");
    const [genre, setGenre] = useState<string>("");
    const [rate, setRate] = useState<string>("");

    const isValidYear = (value: string) => {
        if (!/^\d*$/.test(value)) return false;
        if (value.length === 4) {
            const y = parseInt(value, 10);
            return y >= 1888;
        }
        return true;
    };

    const isValidRate = (value: string) => {
        if (!/^\d+(\.\d+)?$/.test(value)) return false;
        const r = parseFloat(value);
        return r >= 0 && r <= 10;
    };


    const loadFilms = () => {
        axios
            .get<Film[]>("/api/films", { withCredentials: true })
            .then((res) => setFilms(res.data))
            .catch((err) => console.error(err));
    };


    useEffect(() => {
        axios
            .get("/api/genres", { withCredentials: true })
            .then((res) => setGenres(res.data))
            .catch((err) => console.error("Error fetching genres:", err));
    }, []);


    const applyFilters = () => {
        const params: Record<string, string> = {};
        if (year) params.year = year;
        if (genre) params.genre = genre;
        if (rate) params.rate = rate;

        axios
            .get<Film[]>("/api/films/filter", { params, withCredentials: true })
            .then((res) => setFilms(res.data))
            .catch((err) => console.error(err));
    };

    useEffect(() => {
        loadFilms();
    }, []);

    return (
        <div className="text-3xl font-bold text-center">
            <h1 className="text-sm font-bold mb-4">Films</h1>

            {/* Filters */}
            <div className="flex justify-center gap-4 mb-6 flex-wrap">
                <input
                    type="text"
                    placeholder="Year"
                    value={year}
                    onChange={(e) => {
                        const val = e.target.value;
                        if (val === "" || isValidYear(val)) setYear(val);
                    }}
                    className="sm:text-sm border-0 border-b-2 border-gray-300 rounded px-2 py-1 w-24"
                />

                <select
                    value={genre}
                    onChange={(e) => setGenre(e.target.value)}
                    className="sm:text-sm border-0 border-b-2 border-gray-300 rounded px-2 py-1 w-48"
                >
                    <option value="">All Genres</option>
                    {genres.map((g) => (
                        <option key={g} value={g}>
                            {g.replace("_", " ")}
                        </option>
                    ))}
                </select>

                <input
                    type="text"
                    placeholder="Rate"
                    value={rate}
                    onChange={(e) => {
                        const val = e.target.value;
                        if (val === "" || isValidRate(val)) setRate(val);
                    }}
                    className="sm:text-sm border-0 border-b-2 border-gray-300 rounded px-2 py-1 w-24"
                />

                <button
                    onClick={applyFilters}
                    className="bg-blue-500 text-white px-4 py-1 rounded"
                >
                    <FilterIcon />
                </button>
                <button
                    onClick={() => {
                        setYear("");
                        setGenre("");
                        setRate("");
                        loadFilms();
                    }}
                    className="bg-gray-300 text-black px-4 py-1 rounded"
                >
                    <RedoIcon />
                </button>
            </div>


            <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 sm:gap-8">
                {films.map((film) => (
                    <FilmCard key={film.id} film={film} />
                ))}
            </div>
        </div>
    );
}
