import AddFilmForm from "./components/AddFilmForm.tsx";
import GetAllFilms from "./components/GetAllFilms.tsx";
import {Route, Routes} from "react-router-dom";
import {EditComponent} from "./components/EditComponent.tsx";


function App() {

    return (
        <>
            <div className="p-6">
                <h1 className="text-2xl font-bold mb-4">Add Film</h1>
                <AddFilmForm  />

            </div>

                <Routes>
                    <Route path="/" element={<GetAllFilms />} />
                    <Route path="/edit/:id" element={<EditComponent />} />
                </Routes>



        </>
    );
}

export default App;
