import express from "express";
import {
  getProfile,
  searchUsers,
  updateProfile,
  deleteUser,
} from "../controllers/user.controller.js";
import authMiddleware from "../middleware/auth.middleware.js";

const router = express.Router();

router.use(authMiddleware);

router.get("/", searchUsers);
router.get("/profile", getProfile);
router.put("/profile", updateProfile);
router.get("/me", getProfile);
router.put("/me", updateProfile);
router.delete("/me", deleteUser);

export default router;
