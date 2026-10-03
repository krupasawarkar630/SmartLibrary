<div align="center">
  <img src="https://img.icons8.com/color/144/000000/library.png" alt="Library Logo" width="100"/>
  <h1>📚 Smart Library Management System</h1>
  <p><strong>A Premium, AI-Powered Digital Library Experience for Modern Institutions</strong></p>
  
  [![Android](https://img.icons8.com/color/30/000000/android-os.png)](https://developer.android.com/)
  [![Java](https://img.icons8.com/color/30/000000/java-coffee-cup-logo.png)](https://www.java.com/)
  [![Firebase](https://img.icons8.com/color/30/000000/firebase.png)](https://firebase.google.com/)
  [![NodeJS](https://img.icons8.com/color/30/000000/nodejs.png)](https://nodejs.org/)
  [![OpenAI](https://img.icons8.com/color/30/000000/chatgpt.png)](https://openai.com/)
</div>

<br/>

Welcome to the **Smart Library Management System** — a seamless, editorial-style interface designed with the "Ivory Library" theme. It provides an elegant experience for discovering, borrowing, and tracking books, integrated directly with a **Real-Time AI Assistant** for intelligent recommendations.

---

## 📸 App Screenshots

*(Note: Replace these placeholder image paths with actual screenshots of your app)*

<div align="center">
  <img src="https://via.placeholder.com/250x500.png?text=Login+Screen" width="22%" />
  &nbsp;&nbsp;
  <img src="https://via.placeholder.com/250x500.png?text=Home+Dashboard" width="22%" />
  &nbsp;&nbsp;
  <img src="https://via.placeholder.com/250x500.png?text=Book+Details" width="22%" />
  &nbsp;&nbsp;
  <img src="https://via.placeholder.com/250x500.png?text=AI+Assistant" width="22%" />
</div>

---

## ✨ Key Features

- 🎨 **Premium UI/UX ("Ivory Library")**: Designed with a clean bookstore aesthetic, utilizing Warm Ivory backgrounds, Deep Teal accents, and modern typography.
- 🤖 **Gemini AI Assistant**: Direct Retrofit integration with the Google Gemini 1.5 Flash API for context-aware book recommendations and chat.
- 🔐 **Firebase Ecosystem**: 
  - **Firebase Auth** (Email/Password & Google Sign-In via Play Services)
  - **Firestore & Realtime Database** for synchronized book tracking and user profiles.
  - **Firebase Cloud Messaging (FCM)** for push notifications.
- 📷 **ZXing QR Code Scanner**: Native embedded QR code scanning to handle book checkouts and rapid catalog searches.
- 🗺️ **Google Maps Integration**: Play Services Location and Maps API for navigating to physical library branches.
- 📚 **Smart Book Tracking**: Manage borrowed books, view availability in real-time, and track pending requests directly from the home dashboard.

---

## 🏗 Architecture Diagram

Our architecture leverages a direct connection to the Gemini AI API for intelligent chat, while using Firebase for secure database syncing and Google Play Services for Maps/Auth.

```mermaid
graph TD
    subgraph Client ["Client Side (Android)"]
        A[Android App <br> Java + XML]
    end
    
    subgraph Database ["Firebase Backend"]
        C(Firebase Auth)
        D(Firestore & Realtime DB)
        M(Firebase Messaging)
    end
    
    subgraph Services ["External APIs"]
        E[Google Gemini 1.5 Flash API]
        F[Google Maps API]
    end

    A -->|SDK| C
    A -->|SDK| D
    A -->|SDK| M
    A -->|Retrofit HTTP| E
    A -->|Play Services| F
    
    classDef client fill:#176B67,stroke:#176B67,stroke-width:2px,color:#fff;
    classDef ai fill:#3D8068,stroke:#3D8068,stroke-width:2px,color:#fff;
    classDef db fill:#C58A3A,stroke:#C58A3A,stroke-width:2px,color:#fff;
    
    class A client;
    class E,F ai;
    class C,D,M db;
```

---

## 🛠 Technology Stack

### Mobile Frontend
- **Language**: Java (JDK 17)
- **UI Toolkit**: XML, Material Components, ConstraintLayout, ViewBinding
- **Image Loading**: Glide
- **QR Code Scanning**: ZXing (Zebra Crossing) Android Embedded
- **Maps**: Google Play Services Maps & Location

### Cloud & Database (Firebase)
- **Database**: Firebase Firestore & Firebase Realtime Database
- **Authentication**: Firebase Auth & Google Sign-In
- **Notifications**: Firebase Cloud Messaging (FCM)

### AI Integration
- **Networking**: Retrofit 2 & Gson Converter
- **Intelligence**: Google Gemini 1.5 Flash API (Direct REST Integration)

---

## 🚀 Getting Started

### 1. Android Application Setup
1. Clone the repository and open the project in **Android Studio**.
2. Go to [Firebase Console](https://console.firebase.google.com/), create a project, and register this Android app (`com.example.smartlibrary`).
3. Download the `google-services.json` file and place it in the `app/` directory.
4. Enable **Authentication** (Email & Google), **Firestore**, and **Realtime Database** in Firebase.
5. Provide your Google Maps API Key in `local.properties`:
   ```properties
   MAPS_API_KEY=your_maps_api_key_here
   ```

### 2. AI & Environment Variables Setup
The AI assistant runs directly in the app via Retrofit, but you must supply your own Google Gemini API key.

1. Open `app/src/main/java/com/example/smartlibrary/ai/AiRepository.java`.
2. Find the line: `private static final String API_KEY = "YOUR_GEMINI_API_KEY_HERE";`
3. Replace `"YOUR_GEMINI_API_KEY_HERE"` with your actual API key from [Google AI Studio](https://aistudio.google.com/).

> **⚠️ Security Warning:** Do NOT commit your API key to GitHub. It has been purposefully omitted from the repository.

*(Optional Backend Setup)*
If you are planning to use the node `backend/` folder (e.g. for Supabase or legacy features):
1. Navigate to the `backend/` directory.
2. Copy `.env.example` to a new file named `.env`.
3. Fill in your credentials:
```bash
cp .env.example .env
```

Click **Run** in Android Studio to build the app onto your device or emulator!

---

## 🎨 Theme Guidelines ("Ivory Library")

The application strictly follows a custom design system to maintain a professional, academic, and calm environment.

- **Primary Background**: Warm Ivory `#F7F5F0`
- **Card Background**: Crisp White `#FFFFFF`
- **Primary Text**: Deep Forest `#202625`
- **Secondary Text**: Muted Teal `#66706E`
- **Primary Accent**: Deep Teal `#176B67` (Buttons, Active Nav)
- **Secondary Accent**: Warm Gold `#C58A3A` (Highlights, Warnings)
- **Success State**: Sage Green `#3D8068`

---

## 🤝 Contributing
Contributions, issues, and feature requests are welcome! Feel free to check the [issues page](https://github.com/krupasawarkar630/SmartLibrary/issues).

## 📄 License
This project is licensed under the MIT License.
