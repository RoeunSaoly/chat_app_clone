import express from "express";
import {
  getProfile,
  updateProfile,
  deleteUser,
} from "./user.controller.js";

import { verifyToken } from "../../middleware/auth.middleware.js";

const router = express.Router();

router.get("/me", verifyToken, getProfile);
router.put("/me", verifyToken, updateProfile);
router.delete("/me", verifyToken, deleteUser);

export default router;