import type { Film } from "./Types.ts";
import axios from "axios";
import {useNavigate} from "react-router-dom";

type FilmCardProps = {
    film: Film;
    onDelete: (id: string) => void;
};

export default function FilmCard({ film, onDelete }: Readonly<FilmCardProps>) {
    const navigate = useNavigate();

    function deleteHandler(filmId: string) {
        console.log("Deleting film ID:", filmId);
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
            className="film-card border-4 border-gray-100 p-4 rounded shadow hover:shadow-lg transition-shadow flex flex-col justify-between"
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
                        onClick={() => navigate(`/edit/${film.id}`)}
                    >
                        <svg className="w-6 h-6 text-gray-800 dark:text-white" aria-hidden="true"
                             xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24">
                            <path stroke="currentColor"
                                  d="m14.304 4.844 2.852 2.852M7 7H4a1 1 0 0 0-1 1v10a1 1 0 0 0 1 1h11a1 1 0 0 0 1-1v-4.5m2.409-9.91a2.017 2.017 0 0 1 0 2.853l-6.844 6.844L8 14l.713-3.565 6.844-6.844a2.015 2.015 0 0 1 2.852 0Z"/>
                        </svg>
                    </button>


                <button className="bg-green-600 text-white px-3 py-1 rounded hover:bg-green-700">
                    <svg className="w-6 h-6 text-gray-800 dark:text-white" aria-hidden="true"
                         xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24">
                        <path stroke="currentColor"
                              d="M10 11h2v5m-2 0h4m-2.592-8.5h.01M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z"/>
                    </svg>

                </button>
                <button
                    className="bg-gray-700 text-white px-3 py-1 rounded hover:bg-gray-800 flex items-center gap-2"
                    onClick={() => deleteHandler(film.id)}
                >
                    <svg className="w-6 h-6 text-gray-800 dark:text-white" aria-hidden="true"
                         xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24">
                        <path stroke="currentColor"
                              d="M5 7h14m-9 3v8m4-8v8M10 3h4a1 1 0 0 1 1 1v3H9V4a1 1 0 0 1 1-1ZM6 7h12v13a1 1 0 0 1-1 1H7a1 1 0 0 1-1-1V7Z"/>
                    </svg>

                </button>
            </div>
        </div>

    );
}
