import axios from "axios";
import { useEffect, useState } from "react";
import type { Film } from "./Types.ts";
import FilmCard from "./FilmCard.tsx";

export default function Dashboard() {
    const [films, setFilms] = useState<Film[]>([]);

    function loadFilms() {
        axios
            .get<Film[]>("/api/films", { withCredentials: true })
            .then((res) => setFilms(res.data))
            .catch((err) => console.error(err));
    }

    useEffect(() => {
        loadFilms();
    }, []);

    return (
        <div className="text-3xl font-bold text-center">
            <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 sm:gap-8">
                <h1 className="col-span-full text-2xl font-bold mb-4">Films</h1>
                {films.map((film) => (
                    <FilmCard key={film.id} film={film}/>
                ))}
            </div>
        </div>
    );
}
