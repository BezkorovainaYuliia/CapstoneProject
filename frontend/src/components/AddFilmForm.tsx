import { useState } from "react";
import axios from "axios";
import type { Film } from "./Types.ts";
import FilmForm from "./FilmForm";

export default function AddFilmForm() {
    const [film, setFilm] = useState<Film>({
        id: "",
        title: "",
        release_date: "",
        rate: 0,
        casts: "",
        genre: "",
        duration: 0,
    });

    const handleSubmit = () => {
        axios.post("/api/films", film)
            .then(() => {
                setFilm({
                    id: "",
                    title: "",
                    release_date: "",
                    rate: 0,
                    casts: "",
                    genre: "",
                    duration: 0,
                });
            })
            .catch(err => console.error("Error saving film:", err));
    };

    return (
        <FilmForm
            film={film}
            onChange={setFilm}
            onSubmit={handleSubmit}
        />
    );
}
