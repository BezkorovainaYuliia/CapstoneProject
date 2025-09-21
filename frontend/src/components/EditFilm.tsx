import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import type { Film } from "./Types.ts";
import FilmForm from "./UIElements/FilmForm.tsx";

export default function EditFilm() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [film, setFilm] = useState<Film>({ id: "",
        title: "",
        releaseDate: "",
        rate: 0,
        casts: "",
        genre: "",
        duration: 0,
        poster: "",
        description : ""});

    useEffect(() => {
        console.log(id);
        if (id) {
            axios.get(`/api/films/${id}`, { withCredentials: true })
                .then(res => {
                    setFilm(res.data);
                })
                .catch(err => console.error(err));
        }
    }, [id]);

    if (!film) return <div>Loading...</div>;

    const handleSubmit = (updatedFilm: Film) => {
        axios.put(`/api/films/${updatedFilm.id}`, updatedFilm, { withCredentials: true })
            .then(res => {
                console.log(res.data);
                navigate("/films");})
            .catch(err => console.error("Error updating filmFromClientApi:", err));
    };

    return (
        <FilmForm
            film={film}
            onSubmit={handleSubmit}
            onCancel={() => navigate("/films")}
            readonly={false}
        />
    );
}
