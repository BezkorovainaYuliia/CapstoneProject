export type Film = {
    id: string;
    title: string;
    releaseDate: string;
    rate: number;
    casts: string;
    genre: string;
    duration: number;
    poster: string;
    description : string;
}

export const NO_IMAGE_POSTER = "https://upload.wikimedia.org/wikipedia/commons/c/c2/No_image_poster.png"