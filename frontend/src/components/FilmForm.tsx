import type {Film} from "./Types.ts";
import { useState } from "react";
import axios from "axios";

export default function FilmForm() {
    const [film, setFilm] = useState<Film>({
        id: "",
        title: "",
        release_date: "",
        rate: 0,
        casts: "",
        genre: "",
        duration: 0,
    });

    const [alerts, setAlerts] = useState<string>("Film isn't saved successfully!");

    // converting yyyy-MM-dd (input) → dd-MM-yyyy (backend)
    const toBackendDate = (value: string) => {
        if (!value) return "";
        const [yyyy, mm, dd] = value.split("-");
        return `${dd}-${mm}-${yyyy}`;
    };

    // converting dd-MM-yyyy (backend) → yyyy-MM-dd (input)
    const toInputDate = (value: string) => {
        if (!value) return "";
        const [dd, mm, yyyy] = value.split("-");
        return `${yyyy}-${mm}-${dd}`;
    };

    const handleChange = (updatedFilm: Film) => {
        setFilm(updatedFilm);
    };

    const handleSubmit = () => {
        console.log("Submitting film:", film);

        axios.post("/api/films", film)
            .then((res) => {
                console.log("Film saved:", res.data);
                setAlerts("Film saved successfully!");
                setFilm({     ///DTO
                    id: "",
                    title: "",
                    release_date: "",
                    rate: 0,
                    casts: "",
                    genre: "",
                    duration: 0,
                });
            })
            .catch((err) => console.error("Error saving film:", err));
    };

    return (
        <form
            onSubmit={(e) => {
                e.preventDefault();
                handleSubmit();
            }}
            className="max-w-md mx-auto p-4 bg-white rounded shadow"
        >
            <div className="mb-4">
                <label className="block text-gray-700 font-bold mb-2" htmlFor="title">
                    Title
                </label>
                <input
                    type="text"
                    id="title"
                    value={film.title}
                    onChange={(e) => handleChange({ ...film, title: e.target.value })}
                    className="w-full px-3 py-2 border rounded"
                    required
                />
            </div>

            <div className="mb-4">
                <label className="block text-gray-700 font-bold mb-2" htmlFor="release_date">
                    Release Date
                </label>
                <input
                    type="date"
                    id="release_date"
                    value={toInputDate(film.release_date)}
                    onChange={(e) =>
                        handleChange({ ...film, release_date: toBackendDate(e.target.value) })
                    }
                    className="w-full px-3 py-2 border rounded"
                />
            </div>

            <div className="mb-4">
                <label className="block text-gray-700 font-bold mb-2" htmlFor="rate">
                    Rate
                </label>
                <input
                    type="number"
                    id="rate"
                    value={film.rate || ""}
                    onChange={(e) =>
                        handleChange({ ...film, rate: Number(e.target.value) })
                    }
                    className="w-full px-3 py-2 border rounded"
                    min={0}
                    max={10}
                    step={0.1}
                />
            </div>

            <div className="mb-4">
                <label className="block text-gray-700 font-bold mb-2" htmlFor="casts">
                    Casts
                </label>
                <input
                    type="text"
                    id="casts"
                    value={film.casts || ""}
                    onChange={(e) =>
                        handleChange({ ...film, casts: e.target.value })
                    }
                    className="w-full px-3 py-2 border rounded"
                />
            </div>

            <div className="mb-4">
                <label className="block text-gray-700 font-bold mb-2" htmlFor="genre">
                    Genre
                </label>
                <select
                    id="genre"
                    value={film.genre || ""}
                    onChange={(e) => handleChange({ ...film, genre: e.target.value })}
                    className="w-full px-3 py-2 border rounded"
                    required
                >
                    <option value="">-- Select genre --</option>
                    <option value="ACTION">Action</option>
                    <option value="COMEDY">Comedy</option>
                    <option value="DRAMA">Drama</option>
                    <option value="HORROR">Horror</option>
                    <option value="ROMANCE">Romance</option>
                    <option value="SCI_FI">Sci-Fi</option>
                    <option value="DOCUMENTARY">Documentary</option>
                    <option value="THRILLER">Thriller</option>
                    <option value="ANIMATION">Animation</option>
                    <option value="FANTASY">Fantasy</option>
                </select>
            </div>

            <div className="mb-4">
                <label className="block text-gray-700 font-bold mb-2" htmlFor="duration">
                    Duration (minutes)
                </label>
                <input
                    type="number"
                    id="duration"
                    value={film.duration || ""}
                    onChange={(e) =>
                        handleChange({ ...film, duration: Number(e.target.value) })
                    }
                    className="w-full px-3 py-2 border rounded"
                    min={0}
                />
            </div>

            <button
                type="submit"
                className="w-full bg-blue-500 text-white font-bold py-2 px-4 rounded hover:bg-blue-700">
                Submit
            </button>
            <label className="block mt-2 text-center text-gray-700 font-semibold">
                {alerts}
            </label>
        </form>
    );
}
