import app from "./src/app.js";
import dotenv from "dotenv";
import { connectDB } from "./src/config/db.js";

dotenv.config();

const PORT = process.env.PORT || 3000;

connectDB();

app.listen(PORT, () => {
  console.log(`Server started on http://localhost:${PORT}`);
});