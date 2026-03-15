import express from "express";
import authRoutes from "../modules/auth/auth.routes.js";
import * as authController from "../controllers/authController.js";
const router = express.Router();

// API root
router.get("/", (req, res) => {
    res.json({
        message: "API working"
    });
});

// auth routes
router.use("/auth", authRoutes);

router.post("/login", authController.login);

export default router;