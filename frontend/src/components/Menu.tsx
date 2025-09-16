import { useNavigate } from "react-router-dom";
import BellIcon from "./icons/BellIcon.tsx";
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
            </div>

            <div className="flex items-center space-x-4">
                {/* Пошук */}
                <div className="relative">
                    <span className="absolute inset-y-0 left-0 flex items-center pl-3">
                        <svg
                            xmlns="http://www.w3.org/2000/svg"
                            className="w-4 h-4 text-gray-400"
                            fill="none"
                            viewBox="0 0 24 24"
                            stroke="currentColor"
                        >
                            <path
                                strokeLinecap="round"
                                strokeLinejoin="round"
                                strokeWidth={2}
                                d="M21 21l-4.35-4.35M17 11a6 6 0 11-12 0 6 6 0 0112 0z"
                            />
                        </svg>
                    </span>
                    <input
                        type="text"
                        placeholder="Search"
                        className="bg-[#1e293b] text-sm text-gray-300 rounded-md pl-9 pr-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                    />
                </div>
                {/* Дзвіночок */}
                <button className="p-2 rounded-full hover:bg-[#1e293b]">
                    <BellIcon/>
                </button>
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
        </nav>
    );
}
