import express from "express";
import {
  getProfile,
  getUsers,
  updateProfile,
  deleteUser,
} from "./user.controller.js";

import { verifyToken } from "../../middleware/auth.middleware.js";

const router = express.Router();

router.get("/me", verifyToken, getProfile);
router.get("/users", verifyToken, getUsers);
router.put("/me", verifyToken, updateProfile);
router.delete("/me", verifyToken, deleteUser);

export default router;
