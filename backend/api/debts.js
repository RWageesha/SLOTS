import { getSupabase } from '../lib/db.js';
import { authenticate } from '../lib/auth.js';

export default async function handler(req, res) {
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization');

  if (req.method === 'OPTIONS') return res.status(200).end();

  let user;
  try {
    user = await authenticate(req);
  } catch (err) {
    return res.status(401).json({ success: false, error: err.message });
  }

  const supabase = getSupabase();
  const userId = user.localId;

  try {
    if (req.method === 'GET') {
      const { data, error } = await supabase
        .from('debts')
        .select('*')
        .eq('user_id', userId)
        .order('created_at', { ascending: false });

      if (error) throw error;
      return res.status(200).json({ debts: data, total: data.length });
    }

    if (req.method === 'POST') {
      const { person_name, amount, type, description } = req.body;

      if (!person_name || !amount || !type) {
        return res.status(400).json({ success: false, error: 'person_name, amount, and type are required' });
      }

      if (!['BORROWED', 'LENT'].includes(type)) {
        return res.status(400).json({ success: false, error: 'type must be BORROWED or LENT' });
      }

      const { data, error } = await supabase
        .from('debts')
        .insert([{
          user_id: userId,
          person_name,
          amount: parseFloat(amount),
          type,
          description: description || '',
          status: 'PENDING'
        }])
        .select()
        .single();

      if (error) throw error;
      return res.status(201).json({ success: true, data });
    }

    if (req.method === 'PUT') {
      const debtId = req.query.id;
      if (!debtId) {
        return res.status(400).json({ success: false, error: 'Debt ID required' });
      }

      const updates = {};
      const allowedFields = ['person_name', 'amount', 'type', 'description', 'status'];
      for (const field of allowedFields) {
        if (req.body[field] !== undefined) {
          updates[field] = req.body[field];
        }
      }

      const { data, error } = await supabase
        .from('debts')
        .update(updates)
        .eq('id', debtId)
        .eq('user_id', userId)
        .select()
        .single();

      if (error) throw error;
      return res.status(200).json({ success: true, data });
    }

    if (req.method === 'DELETE') {
      const debtId = req.query.id;
      if (!debtId) {
        return res.status(400).json({ success: false, error: 'Debt ID required' });
      }

      const { error } = await supabase
        .from('debts')
        .delete()
        .eq('id', debtId)
        .eq('user_id', userId);

      if (error) throw error;
      return res.status(200).json({ success: true, message: 'Debt deleted' });
    }

    return res.status(405).json({ success: false, error: 'Method not allowed' });

  } catch (err) {
    console.error('Debts API error:', err);
    return res.status(500).json({ success: false, error: err.message });
  }
}
