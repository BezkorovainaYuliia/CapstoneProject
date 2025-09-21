import { Link } from "react-router-dom";
import { type Movie, NO_IMAGE_POSTER } from "../Types.ts";
type Props = { filmFromClientApi: Movie; };

export default function SearchFilmCard({ filmFromClientApi }: Readonly<Props>) {

    if (!filmFromClientApi.imdbID) return "Problem with no filmFromClientApi";

    return (
        <Link
            key =  {filmFromClientApi.imdbID}
            to={`/films/search/${filmFromClientApi.imdbID}`}
              className="film-card border border-gray-200 rounded-2xl shadow overflow-hidden flex flex-col cursor-pointer transform transition duration-300 hover:scale-105 hover:shadow-2xl text-left focus:outline-none focus:ring-2 focus:ring-blue-500" >
            {/* Poster */}
            <div className="overflow-hidden">
                <img src={filmFromClientApi.Poster !== "N/A" ? filmFromClientApi.Poster : NO_IMAGE_POSTER}
                     alt={filmFromClientApi.Title}
                     className="w-full h-64 object-cover transition-transform duration-300 hover:scale-110" />
            </div>
            {/* Title + Year */}
            <div className="p-4">
                <h2 className="text-lg font-semibold mb-1 truncate">{filmFromClientApi.Title}</h2>
                <p className="text-sm text-gray-600">
                    {filmFromClientApi.Year}</p>
            </div>
        </Link>
    );
}