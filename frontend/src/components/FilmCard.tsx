import type { Film } from "./Types.ts";
import axios from "axios";
import {useNavigate} from "react-router-dom";
import DeleteIcon from "./icons/DeleteIcon.tsx";
import EditIcon from "./icons/EditIcon.tsx";
import InfoIcon from "./icons/InfoIcon.tsx";

type FilmCardProps = {
    film: Film;
    onDelete: (id: string) => void;
};

export default function FilmCard({ film, onDelete }: Readonly<FilmCardProps>) {
    const navigate = useNavigate();

    function deleteHandler(filmId: string) {
        console.log("Deleting film ID:", filmId, { withCredentials: true });
            axios
                .delete(`/api/films/${filmId}`)
                .then(() => {
                    onDelete(filmId);})
                .catch((err) => {
                    console.error(err);
                });
        }

    return (
        <div
            key={film.id}
            className="film-card border-4
            border-gray-100 p-4 rounded shadow hover:shadow-lg
            transition-shadow flex flex-col justify-between"
        >
            <div>
                <h1>{film.title}</h1>
                <h6>Release Date: {film.release_date ? film.release_date : "-"}</h6>
                <h6>Rate: {film.rate}</h6>
                <h6>Casts: {film.casts}</h6>
                <h6>Genre: {film.genre}</h6>
                <h6>Duration: {film.duration} minutes</h6>
            </div>

            <div className="flex justify-end gap-2 mt-4">

                    <button
                        className="bg-blue-600 text-white px-3 py-1 rounded hover:bg-blue-700"
                        onClick={() => navigate(`/films/edit/${film.id}`)}
                    >
                        <EditIcon />
                    </button>


                <button className="bg-green-600 text-white px-3 py-1 rounded hover:bg-green-700">
                    <InfoIcon/>

                </button>
                <button
                    className="bg-gray-700 text-white px-3 py-1 rounded hover:bg-gray-800 flex items-center gap-2"
                    onClick={() => deleteHandler(film.id)}
                >
                   <DeleteIcon/>

                </button>
            </div>
        </div>

    );
}
