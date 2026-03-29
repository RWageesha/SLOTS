import { getSupabase } from '../lib/db.js';
import { authenticate } from '../lib/auth.js';

export default async function handler(req, res) {
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, DELETE, OPTIONS');
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
      const { month } = req.query;

      let query = supabase
        .from('transactions')
        .select('*')
        .eq('user_id', userId)
        .order('date', { ascending: false });

      if (month) {
        const startDate = new Date(`${month}-01T00:00:00Z`);
        const endDate = new Date(startDate);
        endDate.setMonth(endDate.getMonth() + 1);
        query = query
          .gte('date', startDate.toISOString())
          .lt('date', endDate.toISOString());
      }

      const { data, error } = await query;
      if (error) throw error;

      // If requesting summary
      if (req.query.summary === 'true') {
        const income = data
          .filter(t => t.type === 'INCOME')
          .reduce((sum, t) => sum + parseFloat(t.amount), 0);
        const expenses = data
          .filter(t => t.type === 'EXPENSE')
          .reduce((sum, t) => sum + parseFloat(t.amount), 0);
        return res.status(200).json({
          totalIncome: income,
          totalExpenses: expenses,
          balance: income - expenses,
          month: month || 'all'
        });
      }

      return res.status(200).json({ transactions: data, total: data.length });
    }

    if (req.method === 'POST') {
      const { type, amount, category, description, date } = req.body;

      if (!type || !amount || !category) {
        return res.status(400).json({ success: false, error: 'type, amount, and category are required' });
      }

      if (!['INCOME', 'EXPENSE'].includes(type)) {
        return res.status(400).json({ success: false, error: 'type must be INCOME or EXPENSE' });
      }

      const { data, error } = await supabase
        .from('transactions')
        .insert([{
          user_id: userId,
          type,
          amount: parseFloat(amount),
          category,
          description: description || '',
          date: date ? new Date(date).toISOString() : new Date().toISOString()
        }])
        .select()
        .single();

      if (error) throw error;
      return res.status(201).json({ success: true, data });
    }

    if (req.method === 'DELETE') {
      const txId = req.query.id;
      if (!txId) {
        return res.status(400).json({ success: false, error: 'Transaction ID required' });
      }

      const { error } = await supabase
        .from('transactions')
        .delete()
        .eq('id', txId)
        .eq('user_id', userId);

      if (error) throw error;
      return res.status(200).json({ success: true, message: 'Transaction deleted' });
    }

    return res.status(405).json({ success: false, error: 'Method not allowed' });

  } catch (err) {
    console.error('Transactions API error:', err);
    return res.status(500).json({ success: false, error: err.message });
  }
}
