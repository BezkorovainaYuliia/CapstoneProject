import {useState } from "react";
import axios from "axios";
import type { Film } from "./Types.ts";
import FilmForm from "./UIElements/FilmForm.tsx";
import {useNavigate} from "react-router-dom";



export default function AddFilm() {
    const [film, setFilm] = useState<Film>({
        id: "",
        title: "",
        release_date: "",
        rate: 0,
        casts: "",
        genre: "",
        duration: 0,
    });
    const navigator = useNavigate();

    const handleSubmit = () => {
        axios.post("/api/films", film, { withCredentials: true})
            .then(() => {
                handleCancel();
            })
            .catch(err => console.error("Error saving film:", err));
    };

    const handleCancel = () => {
        setFilm({
            id: "",
            title: "",
            release_date: "",
            rate: 0,
            casts: "",
            genre: "",
            duration: 0,
        });
        navigator("/");
    }

    return (
        <div>

        <FilmForm
            film={film}
            onChange={setFilm}
            onSubmit={handleSubmit}
            onCancel={handleCancel}
        />
        </div>
    );
}
