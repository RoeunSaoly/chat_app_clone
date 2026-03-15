import jwt from "jsonwebtoken";

export const verifyToken = (req, res, next) => {

    const authHeader = req.headers.authorization;

    // Check if Authorization header exists
    if (!authHeader) {
        return res.status(401).json({ message: "Access denied. No token provided." });
    }

    // Expect format: Bearer TOKEN
    if (!authHeader.startsWith("Bearer ")) {
        return res.status(401).json({ message: "Invalid authorization format." });
    }

    const token = authHeader.split(" ")[1];

    try {

        const decoded = jwt.verify(token, process.env.JWT_SECRET);

        req.user = decoded; // attach decoded payload to request

        next();

    } catch (error) {

        return res.status(401).json({
            message: "Invalid or expired token"
        });

    }
};