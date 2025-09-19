import { Link } from "react-router-dom";
import { type Movie, NO_IMAGE_POSTER } from "../Types.ts";
type Props = { film: Movie; };

export default function SearchFilmCard({ film }: Readonly<Props>) {

    if (!film.imdbID) return "Problem with no film";

    return (
        <Link
            key =  {film.imdbID}
            to={`/films/search/${film.imdbID}`}
              className="film-card border border-gray-200 rounded-2xl shadow overflow-hidden flex flex-col cursor-pointer transform transition duration-300 hover:scale-105 hover:shadow-2xl text-left focus:outline-none focus:ring-2 focus:ring-blue-500" >
            {/* Poster */}
            <div className="overflow-hidden">
                <img src={film.poster !== "N/A" ? film.poster : NO_IMAGE_POSTER}
                     alt={film.title}
                     className="w-full h-64 object-cover transition-transform duration-300 hover:scale-110" />
            </div>
            {/* Title + Year */}
            <div className="p-4">
                <h2 className="text-lg font-semibold mb-1 truncate">{film.title}</h2>
                <p className="text-sm text-gray-600">
                    {film.year}</p>
            </div>
        </Link>
    );
}