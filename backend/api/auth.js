import { authenticate } from '../lib/auth.js';

export default async function handler(req, res) {
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'POST, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization');

  if (req.method === 'OPTIONS') return res.status(200).end();

  if (req.method !== 'POST') {
    return res.status(405).json({ success: false, error: 'Method not allowed' });
  }

  try {
    const user = await authenticate(req);
    return res.status(200).json({
      success: true,
      userId: user.localId,
      email: user.email,
      name: user.displayName || user.email
    });
  } catch (err) {
    return res.status(401).json({ success: false, error: err.message });
  }
}
