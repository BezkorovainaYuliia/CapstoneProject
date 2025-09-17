
import {useNavigate} from "react-router-dom";
import type {Film} from "../Types.ts";
import EditIcon from "../icons/EditIcon.tsx";
import DeleteIcon from "../icons/DeleteIcon.tsx";
import axios from "axios";
import { NO_IMAGE_POSTER } from "../Types.ts";

interface ViewModeProps {
    film: Film;
}

export default function ViewMode({film}: Readonly<ViewModeProps>) {
    const navigateTo = useNavigate();

    const handleDelete = () => {
        if (!film.id) return;
        axios
            .delete(`/api/films/${film.id}`, { withCredentials: true })
            .then(() => {
                navigateTo("/films");
            })
            .catch((err) => console.error("Error deleting film:", err));
    };

    return (
        <div className="max-w-md mx-auto p-4 bg-white rounded shadow">

                <img
                    src={film.poster || NO_IMAGE_POSTER}
                    alt={film.title}
                    className="w-full h-64 object-cover rounded mb-4"
                />

            <h1 className="text-2xl font-bold mb-2">{film.title}</h1>
            <p>
                <strong>Release Date:</strong> {film.releaseDate || "-"}
            </p>
            <p>
                <strong>Rate:</strong> {film.rate ?? "-"}
            </p>
            <p>
                <strong>Casts:</strong> {film.casts || "-"}
            </p>
            <p>
                <strong>Genre:</strong> {film.genre || "-"}
            </p>
            <p>
                <strong>Duration:</strong> {film.duration ? `${film.duration} min` : "-"}
            </p>
            <p>
                <strong>Description:</strong> {film.description || "No description"}
            </p>

            <div className="flex justify-end gap-2 mt-4">
                <button
                    onClick={() => navigateTo(`/films/edit/${film.id}`)}
                    className="text-white bg-blue-700 hover:bg-blue-800 font-medium rounded-lg text-sm px-5 py-2.5"
                >
                    <EditIcon />
                </button>
                <button
                    onClick={handleDelete}
                    className="text-white bg-red-600 hover:bg-red-700 font-medium rounded-lg text-sm px-5 py-2.5"
                >
                    <DeleteIcon />
                </button>
            </div>
        </div>
    );
}