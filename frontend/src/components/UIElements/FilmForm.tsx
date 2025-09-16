import type { Film } from "../Types.ts";
import SaveIcon from "../icons/SaveIcon.tsx";
import CancelIcon from "../icons/CancelIcon.tsx";
import { useEffect, useState } from "react";
import axios from "axios";
import ViewMode from "./ViewMode.tsx";

type Props = {
    film: Film;
    onSubmit: (updatedFilm: Film) => void;
    onCancel?: () => void;
    readonly?: boolean;
};

const toBackendDate = (value: string) => {
    if (!value) return "";
    const [yyyy, mm, dd] = value.split("-");
    return `${dd}-${mm}-${yyyy}`;
};

const toInputDate = (value: string) => {
    if (!value) return "";
    const [dd, mm, yyyy] = value.split("-");
    return `${yyyy}-${mm}-${dd}`;
};

export default function FilmForm({
                                     film,
                                     onSubmit,
                                     onCancel,
                                     readonly = false,
                                 }: Readonly<Props>) {
    const [formData, setFormData] = useState<Film>(film);
    const [genres, setGenres] = useState<string[]>([]);
    const [isEditing, setIsEditing] = useState(false);

    useEffect(() => {
        setFormData(film);
    }, [film]);

    useEffect(() => {
        if (!readonly || isEditing) {
            axios
                .get("/api/genres", { withCredentials: true })
                .then((res) => setGenres(res.data))
                .catch((err) => console.error("Error fetching genres:", err));
        }
    }, [readonly, isEditing]);

    const handleChange = (updated: Partial<Film>) => {
        setFormData((prev) => ({ ...prev, ...updated }));
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        onSubmit(formData);
        setIsEditing(false);
    };

    const handleCancelEdit = () => {
        onCancel?.();
        setFormData(film);
        setIsEditing(false);
    };

    // 🔹 VIEW MODE
    if (readonly && !isEditing) {
        return <ViewMode film={film} />;
    }

    // 🔹 EDIT / ADD MODE
    return (
        <form onSubmit={handleSubmit} className="max-w-md mx-auto p-4 bg-white rounded shadow">
            {/* Title */}
            <div className="relative z-0 w-full mb-5 group">
                <input
                    type="text"
                    id="title"
                    value={formData.title}
                    onChange={(e) => handleChange({ title: e.target.value })}
                    className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                    placeholder=" "
                    required
                />
                <label
                    htmlFor="title"
                    className="absolute text-sm text-gray-500 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6 peer-focus:text-blue-600"
                >
                    Title
                </label>
            </div>

            {/* Release Date */}
            <div className="relative z-0 w-full mb-5 group">
                <input
                    type="date"
                    id="releaseDate"
                    value={toInputDate(formData.releaseDate)}
                    onChange={(e) => handleChange({ releaseDate: toBackendDate(e.target.value) })}
                    className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                    required
                />
                <label
                    htmlFor="releaseDate"
                    className="absolute text-sm text-gray-500 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6 peer-focus:text-blue-600"
                >
                    Release Date
                </label>
            </div>

            {/* Rate */}
            <div className="relative z-0 w-full mb-5 group">
                <input
                    type="number"
                    id="rate"
                    value={formData.rate || ""}
                    onChange={(e) => handleChange({ rate: Number(e.target.value) })}
                    className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                    min={0.1}
                    max={10}
                    step={0.1}
                    required
                />
                <label
                    htmlFor="rate"
                    className="absolute text-sm text-gray-500 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6 peer-focus:text-blue-600"
                >
                    Rate
                </label>
            </div>

            {/* Casts */}
            <div className="relative z-0 w-full mb-5 group">
                <input
                    type="text"
                    id="casts"
                    value={formData.casts || " "}
                    onChange={(e) => handleChange({ casts: e.target.value })}
                    className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                    placeholder=" "
                    required
                />
                <label
                    htmlFor="casts"
                    className="absolute text-sm text-gray-500 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6 peer-focus:text-blue-600"
                >
                    Casts
                </label>
            </div>

            {/* Genre */}
            <div className="relative z-0 w-full mb-5 group">
                <select
                    id="genre"
                    value={formData.genre || ""}
                    onChange={(e) => handleChange({ genre: e.target.value })}
                    className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                    required
                >
                    <option value="" disabled hidden />
                    {genres.map((g) => (
                        <option key={g} value={g}>
                            {g.replace("_", " ")}
                        </option>
                    ))}
                </select>
                <label
                    htmlFor="genre"
                    className="absolute text-sm text-gray-500 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6 peer-focus:text-blue-600"
                >
                    Genre
                </label>
            </div>

            {/* Duration */}
            <div className="relative z-0 w-full mb-5 group">
                <input
                    type="number"
                    id="duration"
                    value={formData.duration || ""}
                    onChange={(e) => handleChange({ duration: Number(e.target.value) })}
                    className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                    placeholder="0"
                    min={0}
                    max={200}
                />
                <label
                    htmlFor="duration"
                    className="absolute text-sm text-gray-500 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6 peer-focus:text-blue-600"
                >
                    Duration (minutes)
                </label>
            </div>

            {/* Poster URL */}
            <div className="relative z-0 w-full mb-5 group">
                <input
                    type="url"
                    id="posterUrl"
                    value={formData.poster}
                    onChange={(e) => handleChange({ poster: e.target.value })}
                    className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                />
                <label
                    htmlFor="posterUrl"
                    className="absolute text-sm text-gray-500 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6 peer-focus:text-blue-600"
                >
                    Poster URL
                </label>
            </div>
            {/* Description */}
            <div className="relative z-0 w-full mb-5 group">
                <input
                    type="text"
                    id="description"
                    value={formData.description || " "}
                    onChange={(e) => handleChange({ description: e.target.value })}
                    className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                    placeholder=" "
                    required
                />
                <label
                    htmlFor="casts"
                    className="absolute text-sm text-gray-500 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6 peer-focus:text-blue-600"
                >
                    Description
                </label>
            </div>

            {/* Buttons */}
            <div className="flex justify-end gap-2">
                <button
                    type="submit"
                    className="text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center"
                >
                    <SaveIcon />
                </button>
                <button
                    type="button"
                    onClick={handleCancelEdit}
                    className="text-gray-700 bg-gray-200 hover:bg-gray-300 focus:ring-4 focus:outline-none focus:ring-gray-400 font-medium rounded-lg text-sm px-5 py-2.5 text-center"
                >
                    <CancelIcon />
                </button>
            </div>
        </form>
    );
}
