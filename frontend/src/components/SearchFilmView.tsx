import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import axios from "axios";
import FilmForm from "./UIElements/FilmForm.tsx";
import type { Film } from "./Types.ts";

export default function SearchFilmView() {
    const { id } = useParams();
    const [film, setFilm] = useState<Film | null>(null);

    useEffect(() => {
        axios.get(`/api/search/${id}`, { withCredentials: true })
            .then(res => setFilm(res.data))
            .catch(err => console.error(err));
    }, [id]);

    if (!film) return <p>Loading...</p>;

    return <FilmForm film={film} onSubmit={() => {}} readonly />;
}