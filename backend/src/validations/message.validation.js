import { z } from "zod";

export const sendMessageSchema = z.object({
  body: z.object({
    conversationId: z.number().int().positive().optional(),
    conversation_id: z.number().int().positive().optional(),
    content: z.string().trim().min(1, "Content is required"),
    messageType: z.enum(["text", "image", "file", "audio"]).optional(),
    message_type: z.enum(["text", "image", "file", "audio"]).optional(),
    replyTo: z.number().int().positive().optional().nullable(),
    reply_to: z.number().int().positive().optional().nullable(),
  }).refine((data) => data.conversationId || data.conversation_id, {
    message: "conversationId is required",
    path: ["conversationId"],
  })
});

export const markMessagesSeenSchema = z.object({
  body: z.object({
    conversationId: z.number().int().positive().optional(),
    conversation_id: z.number().int().positive().optional(),
  }).refine((data) => data.conversationId || data.conversation_id, {
    message: "conversationId is required",
    path: ["conversationId"],
  })
});

export const updateTypingStatusSchema = z.object({
  body: z.object({
    conversationId: z.number().int().positive().optional(),
    conversation_id: z.number().int().positive().optional(),
    isTyping: z.boolean().optional(),
    is_typing: z.boolean().optional(),
  }).refine((data) => data.conversationId || data.conversation_id, {
    message: "conversationId is required",
    path: ["conversationId"],
  })
});

export const reactToMessageSchema = z.object({
  body: z.object({
    messageId: z.number().int().positive().optional(),
    message_id: z.number().int().positive().optional(),
    reaction: z.string().optional().nullable(),
  }).refine((data) => data.messageId || data.message_id, {
    message: "messageId is required",
    path: ["messageId"],
  })
});

export const editMessageSchema = z.object({
  body: z.object({
    content: z.string().trim().min(1, "Content is required"),
  })
});
