const express = require('express');
const cors = require('cors');
const admin = require('firebase-admin');
require('dotenv').config();

const app = express();
app.use(cors());
app.use(express.json());

const multer = require('multer');
const { createClient } = require('@supabase/supabase-js');

// Initialize Supabase Client
const supabaseUrl = process.env.SUPABASE_URL || '';
const supabaseKey = process.env.SUPABASE_KEY || '';
const supabase = createClient(supabaseUrl || 'https://placeholder.supabase.co', supabaseKey || 'placeholder_key');

// Configure Multer for memory storage
const upload = multer({ storage: multer.memoryStorage() });

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
  "The library opens at 9 AM and closes at 8 PM."
];

const { OpenAI } = require('openai');

const actualKey = process.env.GOOGLE_API_KEY || process.env.OPENAI_API_KEY || process.env.AI_API_KEY || '';
const isAiEnabled = actualKey.length > 10;

const openai = new OpenAI({
  apiKey: actualKey || 'dummy_key',
  baseURL: "https://generativelanguage.googleapis.com/v1beta/openai/",
});

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
          model: "gemini-1.5-flash",
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

// Supabase Image Upload Endpoint
app.post('/api/upload', upload.single('image'), async (req, res) => {
  try {
    if (!req.file) {
      return res.status(400).json({ success: false, message: 'No image provided' });
    }

    const file = req.file;
    const fileName = `${Date.now()}_${file.originalname}`;
    
    // Upload image to Supabase Storage (assuming a bucket named 'uploads')
    const { data, error } = await supabase.storage
      .from('uploads')
      .upload(fileName, file.buffer, {
        contentType: file.mimetype,
        upsert: false
      });

    if (error) {
      console.error("Supabase Upload Error:", error);
      return res.status(500).json({ success: false, message: 'Failed to upload image' });
    }

    // Get public URL
    const { data: { publicUrl } } = supabase.storage
      .from('uploads')
      .getPublicUrl(fileName);

    res.json({
      success: true,
      message: 'Image uploaded successfully',
      imageUrl: publicUrl
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
