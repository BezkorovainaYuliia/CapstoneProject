import FilmForm from "./components/FilmForm.tsx";
import GetAllFilms from "./components/GetAllFilms.tsx";


function App() {

    return (
        <div className="p-6">
            <h1 className="text-2xl font-bold mb-4">Add Film</h1>
            <FilmForm  />
            <GetAllFilms/>
        </div>
    );
}

export default App;
