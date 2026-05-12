import { ZodError } from "zod";
import { AppError } from "./error.middleware.js";

export const validateRequest = (schema) => {
  return (req, res, next) => {
    try {
      schema.parse({
        body: req.body,
        query: req.query,
        params: req.params,
      });
      next();
    } catch (error) {
      if (error instanceof ZodError) {
        const errors = error.errors.map((e) => `${e.path.join(".")}: ${e.message}`).join(", ");
        return next(new AppError(`Validation failed: ${errors}`, 400));
      }
      next(error);
    }
  };
};
