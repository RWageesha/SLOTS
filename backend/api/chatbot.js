import OpenAI from 'openai';
import { getSupabase } from '../lib/db.js';
import { authenticate } from '../lib/auth.js';

const openai = new OpenAI({ apiKey: process.env.OPENAI_API_KEY });

export default async function handler(req, res) {
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'POST, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization');

  if (req.method === 'OPTIONS') return res.status(200).end();

  if (req.method !== 'POST') {
    return res.status(405).json({ success: false, error: 'Method not allowed' });
  }

  let user;
  try {
    user = await authenticate(req);
  } catch (err) {
    return res.status(401).json({ success: false, error: err.message });
  }

  const { message } = req.body;
  if (!message || typeof message !== 'string') {
    return res.status(400).json({ success: false, error: 'message is required' });
  }

  const supabase = getSupabase();
  const userId = user.localId;

  try {
    // Gather context: today's pending tasks and this month's expenses
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const [tasksResult, transactionsResult] = await Promise.all([
      supabase
        .from('tasks')
        .select('title, category, priority, deadline, status')
        .eq('user_id', userId)
        .eq('status', 'PENDING')
        .order('deadline', { ascending: true })
        .limit(10),
      supabase
        .from('transactions')
        .select('type, amount, category, description')
        .eq('user_id', userId)
        .gte('date', new Date(today.getFullYear(), today.getMonth(), 1).toISOString())
        .order('date', { ascending: false })
        .limit(20)
    ]);

    const pendingTasks = tasksResult.data || [];
    const transactions = transactionsResult.data || [];

    const totalIncome = transactions
      .filter(t => t.type === 'INCOME')
      .reduce((sum, t) => sum + parseFloat(t.amount), 0);
    const totalExpenses = transactions
      .filter(t => t.type === 'EXPENSE')
      .reduce((sum, t) => sum + parseFloat(t.amount), 0);

    const contextSummary = `
Current user context:
- Pending tasks: ${pendingTasks.length > 0 ? pendingTasks.map(t => `"${t.title}" (${t.category}, ${t.priority})`).join(', ') : 'None'}
- This month's income: $${totalIncome.toFixed(2)}
- This month's expenses: $${totalExpenses.toFixed(2)}
- Current balance: $${(totalIncome - totalExpenses).toFixed(2)}
`.trim();

    const systemPrompt = `You are SLOTS Assistant, a helpful AI for the Smart Life Organizing and Tracking System app. 
You help users manage their tasks, budget, and debts. Be concise, practical and supportive.
${contextSummary}

If the user asks to add, create, or manage tasks/transactions/debts, acknowledge their request and let them know you can provide guidance, but direct database modifications should be done through the app UI.
Always respond in a friendly, helpful manner.`;

    const completion = await openai.chat.completions.create({
      model: 'gpt-3.5-turbo',
      messages: [
        { role: 'system', content: systemPrompt },
        { role: 'user', content: message }
      ],
      max_tokens: 500,
      temperature: 0.7
    });

    const aiResponse = completion.choices[0]?.message?.content || 'I could not generate a response.';

    return res.status(200).json({
      response: aiResponse,
      context: {
        pendingTasksCount: pendingTasks.length,
        monthlyBalance: totalIncome - totalExpenses
      }
    });

  } catch (err) {
    console.error('Chatbot API error:', err);
    return res.status(500).json({ success: false, error: 'Failed to process request' });
  }
}
