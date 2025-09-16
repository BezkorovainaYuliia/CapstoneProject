import { useNavigate } from "react-router-dom";
import "./Menu.css";
import VideoCamIcon from "./icons/VideoCamIcon.tsx";
import LogoutIcon from "./icons/LogoutIcon.tsx";

interface NaviBarProps {
    user: string | null;
    onUserChange: (user: string | null) => void;
}

export default function Menu({ user, onUserChange }: Readonly<NaviBarProps>) {
    const nav = useNavigate();

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
        { path: "/films/add", label: "Add Films", auth: true},
        { path: "/calendar", label: "Calendar", auth: true  },
    ];

    return (
        <nav className="navbar">
            {/* Logo */}
            <div className="flex items-center space-x-8">
                <VideoCamIcon/>
                {/* Links */}
                <div className="nav-links">
                    {links.map((link) =>
                        link.auth && !user ? null : (
                            <button
                                key={link.path}
                                onClick={() => nav(link.path)}
                                className="nav-button"
                            >
                                {link.label}
                            </button>
                        )
                    )}
                </div>
            {/* Правий блок: користувач або логін */}
            <div>
                {user && (

                    <div className="flex items-center space-x-4">
                        <span>Welcome, {user}
                            <img
                                src="https://i.pravatar.cc/40"
                                alt={user ?? "User avatar"}
                                className="w-8 h-8 rounded-full"
                            />
                        </span>
                        <button
                            onClick={logout}
                            className="logout-button"
                        >
                            <LogoutIcon/>
                        </button>
                    </div>
                ) }
            </div>
            </div>
        </nav>
    );
}
