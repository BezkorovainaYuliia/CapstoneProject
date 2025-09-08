import { Navigate } from "react-router-dom";

type ProtectedRouteProps = {
    user: string | null | undefined;
    children: React.ReactNode;
};

export default function ProtectedRoute({ user, children }: Readonly<ProtectedRouteProps>) {
    if (!user) {
        return <Navigate to="/" replace />;
    }

    return <>{children}</>;
}