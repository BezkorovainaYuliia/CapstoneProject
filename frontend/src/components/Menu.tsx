import { useNavigate } from "react-router-dom";

interface NaviBarProps {
    user: string | null;
    onUserChange: (user: string | null) => void;
}

export default function Menu({ user, onUserChange }: Readonly<NaviBarProps>) {
    const nav = useNavigate();

    function login() {
        const host =
            window.location.host === "localhost:5173"
                ? "http://localhost:8080"
                : window.location.origin;
        window.location.href = host + "/oauth2/authorization/github";
    }

    function logout() {
        const host =
            window.location.host === "localhost:5173"
                ? "http://localhost:8080"
                : window.location.origin;
        window.location.href = host + "/logout";
        onUserChange(null);
    }

    const links = [
        { path: "/", label: "Home", auth: false },
        { path: "/films", label: "Films", auth: true },
        { path: "/films/add", label: "Add Films", auth: true }
    ];

    return (
        <nav className="bg-blue-700 text-white p-4 flex justify-between items-center">
            {/* Логотип */}
            <button
                onClick={() => nav("/")}
                className="text-xl font-bold hover:underline"
            >
                MyApp
            </button>

            {/* Навігаційні посилання */}
            <div className="space-x-4">
                {links.map((link) =>
                    link.auth && !user ? null : (
                        <button
                            key={link.path}
                            onClick={() => nav(link.path)}
                            className="hover:underline"
                        >
                            {link.label}
                        </button>
                    )
                )}
            </div>

            {/* Правий блок: користувач або логін */}
            <div>
                {user ? (
                    <div className="flex items-center space-x-4">
                        <span>Welcome, {user}</span>
                        <button
                            onClick={logout}
                            className="bg-red-500 px-3 py-1 rounded hover:bg-red-600"
                        >
                            Logout
                        </button>
                    </div>
                ) : (
                    <button
                        onClick={login}
                        className="bg-green-500 px-3 py-1 rounded hover:bg-green-600"
                    >
                        Start the Journey
                    </button>
                )}
            </div>
        </nav>
    );
}
