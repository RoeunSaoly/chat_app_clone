import express from "express";
import cors from "cors";
import routes from "./routes/index.routes.js";

const app = express();

// Middleware
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Test route
app.get("/", (req, res) => {
    res.json({
        message: "Server is running"
    });
});

// api route
app.use("/api", routes)

export default app;