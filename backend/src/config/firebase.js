import admin from 'firebase-admin';

// Initialize Firebase Admin SDK
// Ideally, the service account key should be loaded from a JSON file or environment variables.
// For example, using an environment variable FIREBASE_SERVICE_ACCOUNT which contains stringified JSON.
try {
  // If you have a service account key file, you can initialize it like this:
  // import serviceAccount from '../../firebase-service-account.json' assert { type: "json" };
  // admin.initializeApp({
  //   credential: admin.credential.cert(serviceAccount)
  // });

  // Fallback: Initialize with default credentials if running in GCP or if GOOGLE_APPLICATION_CREDENTIALS is set
  if (process.env.GOOGLE_APPLICATION_CREDENTIALS || process.env.FIREBASE_SERVICE_ACCOUNT) {
    let credential;
    if (process.env.FIREBASE_SERVICE_ACCOUNT) {
      const serviceAccount = JSON.parse(process.env.FIREBASE_SERVICE_ACCOUNT);
      credential = admin.credential.cert(serviceAccount);
    } else {
      credential = admin.credential.applicationDefault();
    }
    
    admin.initializeApp({
      credential,
    });
    console.log('Firebase Admin initialized successfully');
  } else {
    // Initialize without credentials for development (will not be able to send actual notifications)
    console.warn('Firebase Admin initialized without credentials. Push notifications will not be sent.');
    admin.initializeApp();
  }
} catch (error) {
  console.error('Firebase Admin initialization error:', error);
}

export default admin;
