import {useState} from "react";

type Props = {
    images: { id: string; src: string; alt: string }[];
}

export default function Carousel(images: Readonly<Props>) {
    const [current, setCurrent] = useState(0);

    if (images.images.length === 0) {
        return <div>No images available</div>;
    }
    const prevSlide = () => {
        setCurrent((prev) => (prev === 0 ? images.images.length - 1 : prev - 1));
    };

    const nextSlide = () => {
        setCurrent((prev) => (prev === images.images.length - 1 ? 0 : prev + 1));
    };

    return (
        <div className="max-w-screen-xl mx-auto mt-6 relative">
            {/* Images */}
            <div className="relative h-56 md:h-96 overflow-hidden rounded-lg">
                {images.images.map((img, i) => (
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
                {images.images.map((img, i) => (
                    <button
                        key={img.id + "-indicator"}
                        onClick={() => setCurrent(i)}
                        className={`w-3 h-3 rounded-full ${current === i ? "bg-blue-600" : "bg-gray-300"}`}
                    />
                ))}
            </div>
        </div>
    );
}

