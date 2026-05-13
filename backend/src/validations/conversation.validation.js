import { z } from "zod";

export const createPrivateConversationSchema = z.object({
  body: z.object({
    userId: z.number().int().positive().optional(),
    user_id: z.number().int().positive().optional(),
    memberId: z.number().int().positive().optional(),
    member_id: z.number().int().positive().optional(),
  }).refine((data) => data.userId || data.user_id || data.memberId || data.member_id, {
    message: "A different userId is required",
    path: ["userId"],
  })
});

export const createGroupConversationSchema = z.object({
  body: z.object({
    name: z.string().trim().min(1, "Name is required"),
    avatar: z.string().url().optional().nullable(),
    memberIds: z.array(z.number().int().positive()).optional(),
    member_ids: z.array(z.number().int().positive()).optional(),
  }).refine((data) => (data.memberIds && data.memberIds.length > 0) || (data.member_ids && data.member_ids.length > 0), {
    message: "member_ids must be a non-empty array",
    path: ["member_ids"],
  })
});
