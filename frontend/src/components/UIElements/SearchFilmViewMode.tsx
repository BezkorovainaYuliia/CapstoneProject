import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import axios from "axios";
import { NO_IMAGE_POSTER, type FilmDTO} from "../Types.ts";
import SaveIcon from "../icons/SaveIcon.tsx";

export default function SearchFilmViewMode() {
    const { id } = useParams<{ id: string }>();
    console.log("Search Film View Mode");
    console.log(id);
    const navigateTo = useNavigate();
    const [movie, setMovie] = useState<FilmDTO | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [saving, setSaving] = useState(false);

    useEffect(() => {
        if (!id) return;
        setLoading(true);
        axios
            .get<FilmDTO>(`/api/search/${id}`, { withCredentials: true })
            .then(res =>
            {
                setMovie(res.data)
                console.log("Get BY Id api")
                console.log(res.data)
            })
            .catch(err => {
                console.error(err);
                setError("Error fetching movie details");
            })
            .finally(() => setLoading(false));
    }, [id]);

    const mapMovieToFilm = (movie: FilmDTO): FilmDTO => {
        return {
            title: movie.title,
            releaseDate: movie.releaseDate,
            rate: movie.rate,
            casts: movie.casts || "",
            genre: movie.genre || "",
            duration: movie.duration,
            poster: movie.poster !== "N/A" ? movie.poster : "",
            description: movie.description || "",
        };
    };

    const handleAdd = async () => {
        if (!movie) return;
        setSaving(true);
        try {
            const film: FilmDTO = mapMovieToFilm(movie);
            await axios.post("/api/films", film, { withCredentials: true });
            navigateTo("/films");
        } catch (err) {
            console.error("Error adding film:", err);
            setError("Error saving film");
        } finally {
            setSaving(false);
        }
    };

    if (loading) return <p>Loading...</p>;
    if (error) return <p className="text-red-600">{error}</p>;
    if (!movie) return <p>No movie found</p>;

    return (
        <div className="max-w-md mx-auto p-4 bg-white rounded shadow">
            <img
                src={movie.poster !== "N/A" ? movie.poster : NO_IMAGE_POSTER}
                alt={movie.title}
                className="w-full h-64 object-cover rounded mb-4"
            />

            <h1 className="text-2xl font-bold mb-2">{movie.title}</h1>
            <p><strong>Release Date:</strong> {movie.releaseDate || "-"}</p>
            <p><strong>Rate:</strong> {movie.rate ?? "-"}</p>
            <p><strong>Casts:</strong> {movie.casts || "-"}</p>
            <p><strong>Genre:</strong> {movie.genre || "-"}</p>
            <p><strong>Duration:</strong> {movie.duration || "-"}</p>
            <p><strong>Description:</strong> {movie.description || "No description"}</p>

            <div className="flex justify-end mt-4">
                <button
                    onClick={handleAdd}
                    disabled={saving}
                    className="flex items-center gap-2 text-white bg-green-600 hover:bg-green-700 font-medium rounded-lg text-sm px-5 py-2.5"
                >
                    <SaveIcon />
                    {saving ? "Saving..." : "Add"}
                </button>
            </div>
        </div>
    );
}
