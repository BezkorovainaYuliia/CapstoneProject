import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import type { Film } from "./Types.ts";
import FilmForm from "./UIElements/FilmForm.tsx";

export default function EditFilm() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [film, setFilm] = useState<Film | null>(null);

    useEffect(() => {
        console.log(id);
        if (id) {
            axios.get(`/api/films/${id}`, { withCredentials: true }).then(res => setFilm(res.data));
        }
    }, [id]);

    if (!film) return <div>Loading...</div>;

    const handleSubmit = () => {
        axios.put(`/api/films/${film.id}`, film, { withCredentials: true })
            .then(() => navigate("/films"))
            .catch(err => console.error("Error updating film:", err));
    };

    return (
        <FilmForm
            film={film}
            onChange={setFilm}
            onSubmit={handleSubmit}
            onCancel={() => navigate("/")}
        />
    );
}
