import express from "express";
import {
  getProfile,
  getUsers,
  updateProfile,
  deleteUser,
} from "./user.controller.js";

import { verifyToken } from "../../middleware/auth.middleware.js";

const router = express.Router();

router.get("/", verifyToken, getUsers);
router.get("/profile", verifyToken, getProfile);
router.put("/profile", verifyToken, updateProfile);
router.get("/me", verifyToken, getProfile);
router.put("/me", verifyToken, updateProfile);
router.delete("/me", verifyToken, deleteUser);

export default router;
