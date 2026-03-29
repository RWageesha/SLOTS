# SLOTS – Smart Life Organizing and Tracking System

A full-stack Android application for personal productivity, budget management, and AI-powered assistance.

---

## Features

- **📋 Task Management** – Create, categorize, prioritize, and track tasks with deadlines
- **💰 Budget Tracking** – Log income and expenses with pie-chart visualization (MPAndroidChart)
- **🤝 Debt Tracker** – Track money you've borrowed or lent
- **🤖 AI Chatbot** – OpenAI-powered assistant with awareness of your tasks and finances
- **📱 Home Widget** – Quick-glance widget showing pending tasks and budget
- **🔐 Firebase Auth** – Email/password and Google Sign-In
- **🌙 Dark Mode** – Full Material3 DayNight theming
- **🔄 Offline-First** – Room DB for local storage; optional backend sync via Supabase

---

## Tech Stack

| Layer | Technology |
|---|---|
| Android | Kotlin, MVVM, Jetpack (Room, ViewModel, LiveData, Navigation, Hilt, WorkManager) |
| Backend | Node.js Vercel Serverless Functions |
| Database | Supabase (PostgreSQL) |
| Auth | Firebase Auth (Email + Google Sign-In) |
| AI | OpenAI GPT-3.5-turbo API |
| Charts | MPAndroidChart |

---

## Project Structure

```
SLOTS/
├── app/                          # Android application module
│   └── src/main/
│       ├── java/com/slots/app/
│       │   ├── data/
│       │   │   ├── local/        # Room DB (entities, DAOs, SlotsDatabase)
│       │   │   ├── remote/       # Retrofit API services, API models
│       │   │   └── repository/   # Repositories bridging local + remote
│       │   ├── domain/
│       │   │   ├── model/        # Pure Kotlin domain models
│       │   │   └── usecase/      # Use cases (business logic)
│       │   ├── ui/
│       │   │   ├── auth/         # LoginActivity + AuthViewModel
│       │   │   ├── dashboard/    # DashboardFragment + ViewModel
│       │   │   ├── tasks/        # Tasks list, Add/Edit, Adapter
│       │   │   ├── budget/       # Budget, Transactions, Debts
│       │   │   └── chatbot/      # AI chat interface
│       │   ├── di/               # Hilt DI modules
│       │   ├── widget/           # Home screen widget
│       │   └── SlotsApplication.kt
│       └── res/                  # Layouts, navigation, menus, values
├── backend/                      # Vercel serverless functions
│   ├── api/                      # Endpoint handlers
│   └── lib/                      # Shared utilities (db, auth)
├── database/                     # PostgreSQL schema and seed data
└── README.md
```

---

## Setup Instructions

### 1. Firebase Setup

1. Create a project at [Firebase Console](https://console.firebase.google.com)
2. Add an Android app with package name `com.slots.app`
3. Enable **Authentication** → Email/Password and Google Sign-In
4. Download `google-services.json` and place it in `app/`
5. Add your Web Client ID to `app/src/main/res/values/strings.xml`:
   ```xml
   <string name="default_web_client_id">YOUR_WEB_CLIENT_ID.apps.googleusercontent.com</string>
   ```

### 2. Supabase Setup

1. Create a project at [Supabase](https://supabase.com)
2. Run the SQL from `database/schema.sql` in the Supabase SQL editor
3. (Optional) Run `database/seed.sql` for sample data
4. Copy your **Project URL** and **anon key** from Settings → API

### 3. OpenAI Setup

1. Get an API key from [OpenAI Platform](https://platform.openai.com)

### 4. Backend Deployment (Vercel)

```bash
cd backend
npm install
vercel login
vercel env add SUPABASE_URL
vercel env add SUPABASE_ANON_KEY
vercel env add OPENAI_API_KEY
vercel env add FIREBASE_PROJECT_ID
vercel env add FIREBASE_WEB_API_KEY
vercel --prod
```

### 5. Android App Configuration

1. Open the project in Android Studio (Electric Eel or later)
2. In `app/build.gradle`, update `BASE_URL` to your Vercel deployment URL:
   ```groovy
   buildConfigField "String", "BASE_URL", '"https://your-project.vercel.app/"'
   buildConfigField "String", "OPENAI_API_KEY", '"sk-your-openai-key"'
   ```
   > **Note:** For production, use a backend proxy for OpenAI calls instead of embedding the key.
3. Sync Gradle and build the project

---

## Architecture Overview

```
UI Layer (Fragments/Activities)
    ↓
ViewModels (StateFlow / LiveData)
    ↓
Use Cases (Business Logic)
    ↓
Repositories (Single Source of Truth)
    ↙         ↘
Room DB      Retrofit API
(Local)      (Remote/Vercel)
```

### Dependency Injection

Hilt is used throughout. Key modules:
- `DatabaseModule` – provides `SlotsDatabase` and all DAOs
- `NetworkModule` – provides Retrofit instances for backend + OpenAI
- `AppModule` – provides repositories and use case bundles

---

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/tasks` | List all user tasks |
| POST | `/api/tasks` | Create a task |
| PUT | `/api/tasks?id={id}` | Update a task |
| DELETE | `/api/tasks?id={id}` | Delete a task |
| GET | `/api/transactions` | List transactions (filter by `?month=YYYY-MM`) |
| POST | `/api/transactions` | Create a transaction |
| DELETE | `/api/transactions?id={id}` | Delete a transaction |
| GET | `/api/debts` | List all debts |
| POST | `/api/debts` | Create a debt |
| PUT | `/api/debts?id={id}` | Update debt (e.g., settle) |
| DELETE | `/api/debts?id={id}` | Delete a debt |
| POST | `/api/chatbot` | Send message to AI assistant |
| POST | `/api/auth/verify` | Verify Firebase token |

All endpoints require `Authorization: Bearer <firebase_id_token>`.

---

## Security Notes

- Never commit real API keys or `google-services.json` to version control (covered by `.gitignore`)
- OpenAI API key in `BuildConfig` is for development; use a backend proxy in production
- Backend uses Firebase token verification for all authenticated endpoints
- Row Level Security (RLS) is enabled on all Supabase tables

---

## License

MIT License – see LICENSE file for details.
