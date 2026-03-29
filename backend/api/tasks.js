import { getSupabase } from '../lib/db.js';
import { authenticate } from '../lib/auth.js';

export default async function handler(req, res) {
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization');

  if (req.method === 'OPTIONS') {
    return res.status(200).end();
  }

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
        .from('tasks')
        .select('*')
        .eq('user_id', userId)
        .order('created_at', { ascending: false });

      if (error) throw error;
      return res.status(200).json({ tasks: data, total: data.length });
    }

    if (req.method === 'POST') {
      const { title, description, category, priority, deadline, status } = req.body;

      if (!title || !category) {
        return res.status(400).json({ success: false, error: 'title and category are required' });
      }

      const { data, error } = await supabase
        .from('tasks')
        .insert([{
          user_id: userId,
          title,
          description: description || '',
          category,
          priority: priority || 'MEDIUM',
          deadline: deadline ? new Date(deadline).toISOString() : null,
          status: status || 'PENDING'
        }])
        .select()
        .single();

      if (error) throw error;
      return res.status(201).json({ success: true, data });
    }

    if (req.method === 'PUT') {
      const taskId = req.query.id;
      if (!taskId) {
        return res.status(400).json({ success: false, error: 'Task ID required' });
      }

      const { title, description, category, priority, deadline, status } = req.body;

      const { data, error } = await supabase
        .from('tasks')
        .update({
          title,
          description,
          category,
          priority,
          deadline: deadline ? new Date(deadline).toISOString() : null,
          status,
          updated_at: new Date().toISOString()
        })
        .eq('id', taskId)
        .eq('user_id', userId)
        .select()
        .single();

      if (error) throw error;
      return res.status(200).json({ success: true, data });
    }

    if (req.method === 'DELETE') {
      const taskId = req.query.id;
      if (!taskId) {
        return res.status(400).json({ success: false, error: 'Task ID required' });
      }

      const { error } = await supabase
        .from('tasks')
        .delete()
        .eq('id', taskId)
        .eq('user_id', userId);

      if (error) throw error;
      return res.status(200).json({ success: true, message: 'Task deleted' });
    }

    return res.status(405).json({ success: false, error: 'Method not allowed' });

  } catch (err) {
    console.error('Tasks API error:', err);
    return res.status(500).json({ success: false, error: err.message });
  }
}
