import admin from '../config/firebase.js';
import { pool } from '../config/db.js';

/**
 * Send a push notification to a user using FCM
 * @param {number} userId - The recipient user ID
 * @param {Object} payload - Notification payload { title, body, data }
 */
export const sendPushNotification = async (userId, payload) => {
  try {
    // 1. Get user's FCM token from database
    const result = await pool.query(
      'SELECT fcm_token FROM users WHERE id = $1',
      [userId]
    );

    if (result.rows.length === 0 || !result.rows[0].fcm_token) {
      console.log(`No FCM token found for user ${userId}`);
      return false;
    }

    const fcmToken = result.rows[0].fcm_token;

    // 2. Prepare message
    const message = {
      notification: {
        title: payload.title,
        body: payload.body,
      },
      data: payload.data || {},
      token: fcmToken,
    };

    // 3. Send message via Firebase Admin
    if (admin.apps.length > 0 && admin.app().options.credential) {
      const response = await admin.messaging().send(message);
      console.log(`Successfully sent push notification to user ${userId}:`, response);
      return true;
    } else {
      console.log('Firebase Admin is not configured with credentials. Skipping push notification.');
      return false;
    }
  } catch (error) {
    console.error(`Error sending push notification to user ${userId}:`, error);
    return false;
  }
};
