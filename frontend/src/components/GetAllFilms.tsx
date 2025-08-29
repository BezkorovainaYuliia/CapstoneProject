import axios from "axios";
import {useEffect, useState} from "react";

type Film = {
    id: string;
    title: string;
    release_date: string;
    rate: number;
    casts: string;
    genre: string;
    duration: number;
}
export default function GetAllFilms() {
    const [films, setFilms] = useState<Film[]>([]);

    function loadFilms() {
        axios
            .get<Film[]>("/api/films")
            .then((res) => setFilms(res.data))
            .catch((err) => {
                console.error(err);
            });
    }
    
    useEffect(() => {
        loadFilms();
    }, []);

    return (
        <div className="text-black text-3xl font-bold text-center">
        <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 sm:gap-8">
            <h1 className="col-span-full text-2xl font-bold mb-4">Films</h1>
            {films.map((film) => (
                <div key={film.id} className="film-card
                border-4 border-gray-100 p-4 rounded
                shadow hover:shadow-lg
                transition-shadow">
                    <h1 className="text-lg font-sans">{film.title}</h1>
                    <h6>Release Date: {film.release_date
                        ? film.release_date
                        : "-"}</h6>
                    <h6>Rate: {film.rate}</h6>
                    <h6>Casts: {film.casts}</h6>
                    <h6>Genre: {film.genre}</h6>
                    <h6>Duration: {film.duration} minutes</h6>
                </div>
            ))}
        </div>
        </div>

    );
}