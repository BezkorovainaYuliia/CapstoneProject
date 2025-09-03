// FilmForm.tsx
import type { Film } from "./Types.ts";

type Props = {
    film: Film;
    onChange: (updated: Film) => void;
    onSubmit: () => void;
    onCancel?: () => void;
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
                                     onChange,
                                     onSubmit,
                                     onCancel,
                                 }: Readonly<Props>) {
    return (
        <form
            onSubmit={(e) => {
                e.preventDefault();
                onSubmit();
            }}
            className="max-w-md mx-auto p-4 bg-white rounded shadow"
        >
            {/* Title */}
            <div className="relative z-0 w-full mb-5 group">
                <input
                    type="text"
                    id="title"
                    value={film.title}
                    onChange={(e) => onChange({ ...film, title: e.target.value })}
                    className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent
             border-0 border-b-2 border-gray-300 appearance-none
             focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                    placeholder=" "
                    required
                />
                <label
                    htmlFor="title"
                    className="absolute text-sm text-gray-500 duration-300 transform
             -translate-y-6 scale-75 top-3 -z-10 origin-[0]
             peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0
             peer-focus:scale-75 peer-focus:-translate-y-6 peer-focus:text-blue-600"
                >
                    Title
                </label>
            </div>

            {/* Release Date */}
            <div className="relative z-0 w-full mb-5 group">
                <input
                    type="date"
                    id="release_date"
                    value={toInputDate(film.release_date)}
                    onChange={(e) =>
                        onChange({ ...film, release_date: toBackendDate(e.target.value) })
                    }
                    className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent
             border-0 border-b-2 border-gray-300 appearance-none
             focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                    required
                />
                <label
                    htmlFor="release_date"
                    className="absolute text-sm text-gray-500 duration-300 transform
             -translate-y-6 scale-75 top-3 -z-10 origin-[0]
             peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0
             peer-focus:scale-75 peer-focus:-translate-y-6 peer-focus:text-blue-600"
                >
                    Release Date
                </label>
            </div>

            {/* Rate */}
            <div className="relative z-0 w-full mb-5 group">
                <input
                    type="number"
                    id="rate"
                    value={film.rate || ""}
                    onChange={(e) => onChange({ ...film, rate: Number(e.target.value) })}
                    className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent
             border-0 border-b-2 border-gray-300 appearance-none
             focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                    min={0}
                    max={10}
                    step={0.1}
                />
                <label
                    htmlFor="rate"
                    className="absolute text-sm text-gray-500 duration-300 transform
             -translate-y-6 scale-75 top-3 -z-10 origin-[0]
             peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0
             peer-focus:scale-75 peer-focus:-translate-y-6 peer-focus:text-blue-600"
                >
                    Rate
                </label>
            </div>

            {/* Casts */}
            <div className="relative z-0 w-full mb-5 group">
                <input
                    type="text"
                    id="casts"
                    value={film.casts || ""}
                    onChange={(e) => onChange({ ...film, casts: e.target.value })}
                    className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent
             border-0 border-b-2 border-gray-300 appearance-none
             focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                    placeholder=" "
                />
                <label
                    htmlFor="casts"
                    className="absolute text-sm text-gray-500 duration-300 transform
             -translate-y-6 scale-75 top-3 -z-10 origin-[0]
             peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0
             peer-focus:scale-75 peer-focus:-translate-y-6 peer-focus:text-blue-600"
                >
                    Casts
                </label>
            </div>

            {/* Genre */}
            <div className="relative z-0 w-full mb-5 group">
                <select
                    id="genre"
                    value={film.genre || ""}
                    onChange={(e) => onChange({ ...film, genre: e.target.value })}
                    className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent
             border-0 border-b-2 border-gray-300 appearance-none
             focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                    required
                >
                    <option value="" disabled hidden></option>
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
                <label
                    htmlFor="genre"
                    className="absolute text-sm text-gray-500 duration-300 transform
             -translate-y-6 scale-75 top-3 -z-10 origin-[0]
             peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0
             peer-focus:scale-75 peer-focus:-translate-y-6 peer-focus:text-blue-600"
                >
                    Genre
                </label>
            </div>

            {/* Duration */}
            <div className="relative z-0 w-full mb-5 group">
                <input
                    type="number"
                    id="duration"
                    value={film.duration || ""}
                    onChange={(e) => onChange({ ...film, duration: Number(e.target.value) })}
                    className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent
             border-0 border-b-2 border-gray-300 appearance-none
             focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                    placeholder=" "
                    min={0}
                />
                <label
                    htmlFor="duration"
                    className="absolute text-sm text-gray-500 duration-300 transform
             -translate-y-6 scale-75 top-3 -z-10 origin-[0]
             peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0
             peer-focus:scale-75 peer-focus:-translate-y-6 peer-focus:text-blue-600"
                >
                    Duration (minutes)
                </label>
            </div>

            {/* Buttons */}
            <div className="flex justify-end gap-2">


                {/*Save*/}
                <button
                    type="submit"
                    className="text-white bg-blue-700 hover:bg-blue-800 focus:ring-4
             focus:outline-none focus:ring-blue-300 font-medium rounded-lg
             text-sm px-5 py-2.5 text-center"
                >
                    <svg className="w-6 h-6 text-gray-800 dark:text-white" aria-hidden="true"
                         xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24">
                        <path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2"
                              d="M11 16h2m6.707-9.293-2.414-2.414A1 1 0 0 0 16.586 4H5a1 1 0 0 0-1 1v14a1 1 0 0 0 1 1h14a1 1 0 0 0 1-1V7.414a1 1 0 0 0-.293-.707ZM16 20v-6a1 1 0 0 0-1-1H9a1 1 0 0 0-1 1v6h8ZM9 4h6v3a1 1 0 0 1-1 1h-4a1 1 0 0 1-1-1V4Z"/>
                    </svg>
                </button>

                {onCancel && (
                    <button
                        type="button"
                        onClick={onCancel}
                        className="text-gray-700 bg-gray-200 hover:bg-gray-300 focus:ring-4
             focus:outline-none focus:ring-gray-400 font-medium rounded-lg
             text-sm px-5 py-2.5 text-center"
                    >
                        {/*Cancel*/}
                        <svg className="w-6 h-6 text-gray-800 dark:text-white" aria-hidden="true"
                             xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24">
                            <path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2"
                                  d="M6 18 17.94 6M18 18 6.06 6"/>
                        </svg>
                    </button>
                )}

            </div>
        </form>
    );
}
