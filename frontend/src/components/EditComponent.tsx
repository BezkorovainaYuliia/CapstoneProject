import type { Film } from "./Types.ts";
import { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import axios from "axios";
import FilmEditForm from "./FilmEditForm.tsx"; // твій компонент з форми

export const EditComponent = () => {
    const { id } = useParams();
    const [film, setFilm] = useState<Film | null>(null);

    useEffect(() => {
        if (id) {
            axios
                .get(`/api/films/${id}`)
                .then((res) => setFilm(res.data))
                .catch((err) => console.error(err));
        }
    }, [id]);

    if (!film) return <div>Loading...</div>;
    return <FilmEditForm />;
};
