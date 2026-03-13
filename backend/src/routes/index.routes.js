import express from "express";
import authRoutes from "../modules/auth/auth.routes.js";

const router = express.Router();

// API root
router.get("/", (req, res) => {
    res.json({
        message: "API working"
    });
});

// auth routes
router.use("/auth", authRoutes);

export default router;