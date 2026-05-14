📱 About
Hasta-Shilpa is a fully functional Android application designed to empower bamboo and cane artisans across the Western Ghats region of India. The app bridges the gap between traditional craft skills and modern urban market demands.
Artisans get access to:

Real-time design trends from urban buyers
Step-by-step construction blueprints with precise measurements
Smart pricing guidance based on materials and labour
AI-powered design suggestions via Google Gemini
A simulated marketplace to list and preview products
Material tracking with persistent local storage


✨ Features
🏠 Home Screen

Scrollable product feed with 16+ bamboo & cane products
Category filters — Furniture, Baskets, Lighting, Decor, Bamboo
Real product images via Unsplash API
Trending banner with blueprint deep-link
Wishlist and cart interactions

📐 Blueprint Viewer

10 detailed product blueprints with real photos
Pinch-to-zoom on blueprint images
Precise cm/mm dimensions for each design
Step-by-step construction instructions (8 steps each)
Download blueprint as PDF
Save blueprints for offline viewing

🤖 AI Design Suggester (Google Gemini)

Generate 3 unique design variations for any product type
Powered by Gemini 2.0 Flash API
Each card shows unique contextual image via Unsplash
Returns dimensions, materials, estimated time, price range and market tips
Pinch-to-zoom on AI-generated design images
Quick-pick chips for common product types

₹ Price Suggester

Calculate fair selling price from material cost, hours worked and overhead
Full profit margin breakdown
Wired to PricerViewModel with StateFlow

📦 Material Tracker

Log batch details — product, bamboo poles, cane strips, date
Persistent storage via Room Database
Batch history with running totals
Export batch report as PDF
Edit and delete batches

🛒 Marketplace

Create product listings with title, photo, price, description
Category chips for product classification
Live buyer preview card
Wired to MarketplaceViewModel

📊 Progress Dashboard

Monthly earnings hero card
Weekly stats — batches, poles, strips, earnings
Product breakdown table
Efficiency metrics

🔐 Authentication

Email + Password login and registration
Phone number login and registration
Google Sign-In (UI ready)
Guest / Browse mode
SHA-256 password hashing — credentials stored in Room DB
Session persistence via SharedPreferences
Profile dialog showing user details from Room DB
Logout with full session clear

🌐 Offline Support

Blueprint and feed cards cached for offline viewing
Offline banner when network is unavailable
OfflineCache utility with file-based caching


🛠️ Tech Stack
LayerTechnologyLanguageKotlin 2.1.0UI FrameworkJetpack Compose + Material 3ArchitectureMVVM + Repository PatternNavigationNavigation ComposeLocal DatabaseRoom (Entity, DAO, Database)PreferencesSharedPreferences + EncryptedSharedPreferences (AES-256)Image LoadingCoil 2.6.0Image SourceUnsplash APIAIGoogle Gemini 2.0 Flash APIHTTP ClientHttpURLConnection (no extra dependency)JSON Parsingorg.json.JSONObject / JSONArrayPDF GenerationAndroid PdfDocument APIMin SDKAndroid 8.0 (API 25)Target SDKAndroid 15 (API 35)

🚀 Getting Started
Prerequisites

Android Studio Hedgehog or newer
Android device or emulator running API 25+
Unsplash Developer account (free) — unsplash.com/developers
Google Gemini API key (free) — aistudio.google.com

Setup
Clone the repository
https://github.com/nishant-kumar0708/HastaShilpaApp.git
cd HastaShilpaApp

Create local.properties in the project root (same level as build.gradle.kts)

sdk.dir=YOUR_ANDROID_SDK_PATH
UNSPLASH_ACCESS_KEY=your_unsplash_access_key_here
GEMINI_API_KEY=your_gemini_api_key_here

Sync Gradle

File → Sync Project with Gradle Files

Run the app

Run → Run 'app'

> ⚠️ Note for contributors: The `local.properties` file is not included in this repo 
> (gitignored for security). If you clone this project, create your own 
> `local.properties` and add your own free API keys from the links above.
> The original developer's keys are stored locally and the app works fully.


📁 Project Structure
app/src/main/java/com/hastashilpa/app/
├── MainActivity.kt              # Home screen, product card, bottom nav
├── data/
│   ├── BatchDatabase.kt         # Room DB — batches + users tables
│   ├── AuthRepository.kt        # Login, register, SHA-256 hashing
│   └── AppPreferences.kt        # SharedPreferences wrapper
├── navigation/
│   └── AppNavGraph.kt           # NavHost with 9 routes
├── ui/screens/
│   ├── AuthScreen.kt            # Login + Register UI
│   ├── Screens.kt               # Blueprint, Tracker, Pricer, Marketplace
│   ├── GenAiDesignScreen.kt     # AI Design Suggester
│   ├── OnboardingScreen.kt      # 3-page first launch flow
│   └── ProgressDashboardScreen.kt
├── viewmodel/
│   ├── AuthViewModel.kt         # Auth state, login, logout
│   ├── GenAiViewModel.kt        # Gemini API calls, JSON parsing
│   ├── TrackerViewModel.kt      # Batch CRUD via Room
│   ├── PricerViewModel.kt       # Price calculation
│   └── MarketplaceViewModel.kt  # Listing state
└── util/
    ├── UnsplashImageFetcher.kt  # Unsplash API with in-memory cache
    ├── UnsplashImage.kt         # Reusable image composable
    ├── ProductImageUrls.kt      # Search query builder per product
    ├── PdfExporter.kt           # Blueprint + batch PDF export
    ├── OfflineCache.kt          # File-based offline cache
    ├── NetworkUtils.kt          # Connectivity check
    └── SecurePrefs.kt           # AES-256 encrypted preferences

🔑 API Keys
This project uses two external APIs. Keys are stored locally and never committed to Git.
APIPurposeFree TierUnsplashProduct & blueprint images50 requests/hourGoogle GeminiAI design suggestions15 requests/minute

📸 Screenshots
Home ScreenBlueprint ViewerAI Design SuggesterProduct feed with real images10 blueprints with zoomGemini-powered 3 designs

🌿 About the Mission
Hasta-Shilpa (हस्तशिल्प) means "handicraft" in Sanskrit. This app is built to empower the bamboo and cane artisan communities of the Western Ghats — Wayanad, Coorg, Sirsi, Agumbe and surrounding regions — by giving them access to modern design tools, fair pricing guidance and urban market insights, all working offline on a mid-range Android device.

📄 License
MIT License — feel free to use, modify and distribute.

👨‍💻 Developer
Built with ❤️ for Indian handicraft artisans.

"Bridging traditional bamboo craft with modern urban markets."
