import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

// Get __dirname equivalent in ES Modules
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// The path to your Firebase Service Account JSON file
const jsonPath = path.join(__dirname, 'chat-app-291fb-firebase-adminsdk-fbsvc-736534174a.json');
const envPath = path.join(__dirname, '.env');

try {
  // 1. Read the JSON file
  const rawData = fs.readFileSync(jsonPath, 'utf8');

  // 2. Parse and then stringify to minify it into a single line
  const minifiedJson = JSON.stringify(JSON.parse(rawData));

  // 3. Append to .env file wrapped in single quotes
  const envContent = `\nFIREBASE_SERVICE_ACCOUNT='${minifiedJson}'\n`;
  fs.appendFileSync(envPath, envContent);

  console.log('✅ Successfully added FIREBASE_SERVICE_ACCOUNT to .env!');
} catch (error) {
  console.error('❌ Error:', error.message);
}