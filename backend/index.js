const express = require('express');
const cors = require('cors');
const admin = require('firebase-admin');
require('dotenv').config();

const app = express();
app.use(cors());
app.use(express.json());

// Initialize Firebase Admin (Mock initialized for demo if credentials missing)
try {
  admin.initializeApp({
    credential: admin.credential.applicationDefault()
  });
} catch (e) {
  console.log("Firebase Admin not fully configured. Using mock verification.");
}

const mockResponses = [
  "Here are some psychology books available in your library...",
  "You currently have 3 books issued. They are due next week.",
  "That book is a great introduction to the subject.",
  "The library opens at 9 AM and closes at 8 PM."
];

const { OpenAI } = require('openai');

const openai = new OpenAI({
  apiKey: process.env.OPENAI_API_KEY || process.env.AI_API_KEY || 'dummy_key',
  baseURL: process.env.OPENAI_BASE_URL || undefined,
});

const actualKey = process.env.OPENAI_API_KEY || process.env.AI_API_KEY || '';
const isAiEnabled = actualKey.length > 10;

// AI Mock Endpoint
app.post('/api/ai/chat', async (req, res) => {
  try {
    const authHeader = req.headers.authorization;
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return res.status(401).json({ success: false, message: 'Unauthorized' });
    }

    const token = authHeader.split('Bearer ')[1];
    
    // In production, verify token:
    // const decodedToken = await admin.auth().verifyIdToken(token);
    // const userId = decodedToken.uid;
    
    const { message, conversationId } = req.body;
    let reply = "";

    if (isAiEnabled) {
      try {
        const aiResponse = await openai.chat.completions.create({
          model: process.env.AI_MODEL || "gpt-3.5-turbo",
          messages: [
            { role: "system", content: "You are the AI Library Assistant for a Library Management System. Help users with books, recommendations, summaries, issued books, due dates. Use only verified data if querying personal data. Be helpful, concise, and accurate." },
            { role: "user", content: message }
          ],
          max_tokens: parseInt(process.env.AI_MAX_TOKENS || "256"),
          temperature: parseFloat(process.env.AI_TEMPERATURE || "0.7")
        });
        reply = aiResponse.choices[0].message.content;
      } catch (aiErr) {
        console.error("AI Error:", aiErr);
        reply = "Sorry, I am having trouble connecting to the AI brain right now.";
      }
    } else {
      // Mock Response
      if (message.toLowerCase().includes("psychology")) {
        reply = "We have several psychology-related books. Some popular ones include 'Thinking, Fast and Slow' and 'Man's Search for Meaning'.";
      } else if (message.toLowerCase().includes("issued") || message.toLowerCase().includes("have")) {
        reply = "According to your records, you have 'Ikigai' and 'Atomic Habits' issued. They are due on the 15th.";
      } else {
        reply = "I'm your AI Library Assistant. " + mockResponses[Math.floor(Math.random() * mockResponses.length)];
      }
      await new Promise(r => setTimeout(r, 1000));
    }

    res.json({
      success: true,
      message: reply,
      conversationId: conversationId || 'conv_' + Date.now()
    });

  } catch (error) {
    console.error(error);
    res.status(500).json({ success: false, message: 'Internal server error' });
  }
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Backend server running on port ${PORT}`);
});
