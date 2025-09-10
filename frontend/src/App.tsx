import {Route, Routes, useNavigate} from "react-router-dom";
import Dashboard from "./components/Dashboard.tsx";
import {useEffect, useState} from "react";
import axios from "axios";
import Carousel from "./components/UIElements/Carousel.tsx";
import GitHubIcon from "./components/icons/GitHubIcon.tsx";
import Menu from "./components/Menu.tsx";
import AddFilm from "./components/AddFilm.tsx";
import EditFilm from "./components/EditFilm.tsx";
import ProtectedRoute from "./components/ProtectedRoutes.tsx";
import ViewFilm from "./components/FilmView.tsx";

const images = [
    { id: "amazon", src: "https://m.media-amazon.com/images/G/01/AdProductsWebsite/images/campaigns/primeVideo/PV_titleScreens_16Shows._TTW_.jpeg", alt: "Amazon" },
    { id: "disney", src: "https://res.cloudinary.com/jerrick/image/upload/v1748932078/683e95ee16c5ed001d3964af.jpg", alt: "Disney+" },
    { id: "netflix", src: "https://raw.githubusercontent.com/thatanjan/netflix-clone-yt/youtube/media//banner.jpg", alt: "Netflix" },
];

function App() {
    const [user, setUser] = useState<string | null | undefined>(undefined);
    const [loading, setLoading] = useState(true);
    const nav = useNavigate();


    function login() {
        const host = window.location.host === 'localhost:5173' ? 'http://localhost:8080' : window.location.origin

        window.open(host + '/oauth2/authorization/github', '_self')
    }

    useEffect(() => {
        axios.get("/api/auth/me", { withCredentials: true })
            .then(response => {
                if (response.data) {
                    setUser(response.data);
                } else {
                    setUser(null);
                }
            })
            .catch(() => {
                setUser(null);
                setLoading(false);
            })
            .finally(() => {
                setLoading(false);
            });
    }, [nav]);

    if (loading) {
        return <div className="p-4 text-center">Loading...</div>;
    }

    return (
        <>  {!user &&

            <div>
                {/* Carousel of images */}
                <Carousel images={images}/>

                {/* Hauptinhalt der Seite */}

                <div className="flex justify-center mt-6">
                    <button
                        className="flex items-center gap-2 px-6 py-3 text-lg font-semibold text-white bg-blue-700 rounded-lg shadow-lg hover:bg-blue-800 transition focus:outline-none"
                        onClick={login}
                    >
                        Start the Journey
                        <GitHubIcon/>
                    </button>
                </div>
            </div>
        }
            {user &&
            <div>
                <Menu user={user} onUserChange={setUser} />
                <Routes>
                    {/* Dashboard */}
                    <Route path="/" element={
                        <ProtectedRoute user={user}>
                            <Dashboard />
                        </ProtectedRoute>
                    } />

                    <Route path="/films" element={
                        <ProtectedRoute user={user}>
                            <Dashboard />
                        </ProtectedRoute>
                    } />

                    {/* Add Film */}
                    <Route path="/films/add" element={
                        <ProtectedRoute user={user}>
                            <AddFilm />
                        </ProtectedRoute>
                    } />

                    {/* Edit Film */}
                    <Route path="/films/edit/:id" element={
                        <ProtectedRoute user={user}>
                            <EditFilm />
                        </ProtectedRoute>
                    } />
                    {/* View Film */}
                    <Route path="/films/:id" element={
                        <ProtectedRoute user={user}>
                            <ViewFilm />
                        </ProtectedRoute>
                    } />

                </Routes>
            </div>
            }
        </>
    );
}
export default App;