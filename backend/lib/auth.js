/**
 * Verifies a Firebase ID token by calling Firebase Admin REST API.
 * Returns the decoded user info on success, or throws on failure.
 */
export async function verifyFirebaseToken(token) {
  if (!token) {
    throw new Error('No token provided');
  }

  const projectId = process.env.FIREBASE_PROJECT_ID;
  if (!projectId) {
    throw new Error('FIREBASE_PROJECT_ID not configured');
  }

  const url = `https://identitytoolkit.googleapis.com/v1/accounts:lookup?key=${process.env.FIREBASE_WEB_API_KEY}`;

  const response = await fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ idToken: token })
  });

  if (!response.ok) {
    throw new Error('Token verification failed');
  }

  const data = await response.json();
  if (!data.users || data.users.length === 0) {
    throw new Error('User not found');
  }

  return data.users[0];
}

/**
 * Extracts Bearer token from Authorization header
 */
export function extractToken(req) {
  const authHeader = req.headers['authorization'] || req.headers['Authorization'];
  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    return null;
  }
  return authHeader.substring(7);
}

/**
 * Middleware helper to authenticate request
 */
export async function authenticate(req) {
  const token = extractToken(req);
  if (!token) {
    throw new Error('Unauthorized: No token provided');
  }
  const user = await verifyFirebaseToken(token);
  return user;
}
