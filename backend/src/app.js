import express from "express";
import cors from "cors";
import routes from "./routes/index.routes.js";
import { errorHandler, notFoundHandler } from "./middleware/error.middleware.js";

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

app.use(notFoundHandler);
app.use(errorHandler);

export default app;
