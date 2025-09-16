import type { Film } from "./Types.ts";
import { NO_IMAGE_POSTER } from "./Types.ts";
import { Link } from "react-router-dom";

type FilmCardProps = {
    film: Film;
};

export default function FilmCard({ film }: Readonly<FilmCardProps>) {
    return (
        <Link
            key={film.id}
            to={`/films/${film.id}`}
            className="film-card border border-gray-200 rounded-2xl shadow
                       overflow-hidden flex flex-col cursor-pointer
                       transform transition duration-300 hover:scale-105 hover:shadow-2xl
                       text-left focus:outline-none focus:ring-2 focus:ring-blue-500"
        >
            {/* Poster */}
            <div className="overflow-hidden">
                <img
                    src={film.poster || NO_IMAGE_POSTER}
                    alt={film.title}
                    className="w-full h-64 object-cover transition-transform duration-300 hover:scale-110"
                />
            </div>

            {/* Title + Rate */}
            <div className="p-4">
                <h2 className="text-lg font-semibold mb-1 truncate">{film.title}</h2>
                <p className="text-sm text-gray-600">⭐ {film.rate}</p>
            </div>
        </Link>
    );
}
