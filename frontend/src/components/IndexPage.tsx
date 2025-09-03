import { useState } from "react";

const images = [
    { id: "amazon", src: "https://m.media-amazon.com/images/G/01/AdProductsWebsite/images/campaigns/primeVideo/PV_titleScreens_16Shows._TTW_.jpeg", alt: "Amazon" },
    { id: "disney", src: "https://res.cloudinary.com/jerrick/image/upload/v1748932078/683e95ee16c5ed001d3964af.jpg", alt: "Disney+" },
    { id: "netflix", src: "https://raw.githubusercontent.com/thatanjan/netflix-clone-yt/youtube/media//banner.jpg", alt: "Netflix" },
];

export default function IndexPage() {
    const [current, setCurrent] = useState(0);

    const prevSlide = () => {
        setCurrent((prev) => (prev === 0 ? images.length - 1 : prev - 1));
    };

    const nextSlide = () => {
        setCurrent((prev) => (prev === images.length - 1 ? 0 : prev + 1));
    };

    return (
        <div className="max-w-screen-xl mx-auto mt-6 relative">
            {/* Images */}
            <div className="relative h-56 md:h-96 overflow-hidden rounded-lg">
                {images.map((img, i) => (
                    <img
                        key={img.id}
                        src={img.src}
                        alt={img.alt}
                        className={`absolute w-full h-full object-cover transition-opacity duration-700 ease-in-out 
              ${i === current ? "opacity-100" : "opacity-0"}`}
                    />
                ))}
            </div>

            {/* Prev Button */}
            <button
                onClick={prevSlide}
                className="absolute top-1/2 left-4 -translate-y-1/2 bg-black/30 hover:bg-black/50 text-white rounded-full p-2"
            >
                <svg className="w-6 h-6" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 6 10">
                    <path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M5 1 1 5l4 4" />
                </svg>
            </button>

            {/* Next Button */}
            <button
                onClick={nextSlide}
                className="absolute top-1/2 right-4 -translate-y-1/2 bg-black/30 hover:bg-black/50 text-white rounded-full p-2"
            >
                <svg className="w-6 h-6" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 6 10">
                    <path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="m1 9 4-4-4-4" />
                </svg>
            </button>

            {/* Indicators */}
            <div className="flex justify-center mt-4 space-x-2">
                {images.map((img, i) => (
                    <button
                        key={img.id + "-indicator"}
                        onClick={() => setCurrent(i)}
                        className={`w-3 h-3 rounded-full ${current === i ? "bg-blue-600" : "bg-gray-300"}`}
                    />
                ))}
            </div>


            <div className="flex justify-center mt-6">
                <button
                    className="flex items-center gap-2 px-6 py-3 text-lg font-semibold text-white bg-blue-700 rounded-lg shadow-lg hover:bg-blue-800 transition focus:outline-none"
                >
                    Start the Journey
                    <svg
                        className="w-6 h-6 text-white"  aria-hidden="true"
                        xmlns="http://www.w3.org/2000/svg"  width="24"
                        height="24"  fill="currentColor"
                        viewBox="0 0 24 24"
                    >
                        <path
                            fillRule="evenodd"
                            d="M12.006 2a9.847 9.847 0 0 0-6.484 2.44 10.32 10.32 0 0 0-3.393 6.17 10.48 10.48 0 0 0 1.317 6.955 10.045 10.045 0 0 0 5.4 4.418c.504.095.683-.223.683-.494 0-.245-.01-1.052-.014-1.908-2.78.62-3.366-1.21-3.366-1.21a2.711 2.711 0 0 0-1.11-1.5c-.907-.637.07-.621.07-.621.317.044.62.163.885.346.266.183.487.426.647.71.135.253.318.476.538.655a2.079 2.079 0 0 0 2.37.196c.045-.52.27-1.006.635-1.37-2.219-.259-4.554-1.138-4.554-5.07a4.022 4.022 0 0 1 1.031-2.75 3.77 3.77 0 0 1 .096-2.713s.839-.275 2.749 1.05a9.26 9.26 0 0 1 5.004 0c1.906-1.325 2.74-1.05 2.74-1.05.37.858.406 1.828.101 2.713a4.017 4.017 0 0 1 1.029 2.75c0 3.939-2.339 4.805-4.564 5.058a2.471 2.471 0 0 1 .679 1.897c0 1.372-.012 2.477-.012 2.814 0 .272.18.592.687.492a10.05 10.05 0 0 0 5.388-4.421 10.473 10.473 0 0 0 1.313-6.948 10.32 10.32 0 0 0-3.39-6.165A9.847 9.847 0 0 0 12.007 2Z"
                            clipRule="evenodd"
                        />
                    </svg>
                </button>
            </div>

        </div>
    );
}
