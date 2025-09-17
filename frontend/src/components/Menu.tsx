import { useNavigate } from "react-router-dom";
import "./Menu.css";
import VideoCamIcon from "./icons/VideoCamIcon.tsx";
import LogoutIcon from "./icons/LogoutIcon.tsx";
import PlusIcon from "./icons/PlusIcon.tsx";
import HomeIcon from "./icons/HomeIcon.tsx";
import ListIcon from "./icons/ListIcon.tsx";

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
        { path: "/", label: <HomeIcon />, auth: false },
        { path: "/films", label: <ListIcon />, auth: true },
        { path: "/films/add", label: <PlusIcon />, auth: true},
    ];

    return (
        <nav className="navbar flex items-center justify-between">
            {/* Left: Logo + Links */}
            <div className="flex items-center space-x-8">
                {/* Logo */}
                <VideoCamIcon />

                {/* Links */}
                <div className="flex space-x-6">
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
            </div>

            {/* Right: User and Logout */}
            <div className="flex items-center space-x-4">
                {user && (
                    <>
                        <span>Welcome, {user}</span>
                        <img
                            src="https://i.pravatar.cc/40"
                            alt={user}
                            className="w-8 h-8 rounded-full"
                        />
                        <button onClick={logout} className="logout-button">
                            <LogoutIcon />
                        </button>
                    </>
                )}
            </div>
        </nav>
    );
}
