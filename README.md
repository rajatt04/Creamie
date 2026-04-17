# 🍨 Creamie Wallpaper App

A modern, fast, and beautiful **Jetpack Compose showcase app** for downloading and setting wallpapers, demonstrating Android UI development with declarative patterns and clean architecture.

Creamie highlights the best practices of **Kotlin + Jetpack Compose + Material3** by implementing a sleek Glassmorphism UI, robust network caching, pagination, and reusable design patterns.

---

## 🚀 Features

- **Built entirely with Jetpack Compose** (no legacy XML)
- **Material Design 3 & Glassmorphism** theming for a premium look
- **Infinite Scrolling** with Paging 3
- **Dynamic Colors** using Palette API to extract colors from images
- **Offline Caching** backed by Room Database
- **Background Operations** with WorkManager for downloading and setting wallpapers
- **Home Screen Widgets** built with Glance AppWidget
- Clean Architecture with **Hilt Dependency Injection** and **ViewModel-driven UI**
- **Sleek Animations** using Compose Animation and Lottie

---

## 📱 App Screenshots

## App Screenshots

Below are some screenshots of the Creamie application showcasing its features and user interface:

| Screenshot 1      | Screenshot 2      | Screenshot 3      |Screenshot 4       |
|-------------------|-------------------|-------------------|-------------------|
| ![Image1](https://github.com/rajatt04/Student-Management-System/blob/sms-main/Projects/Creamie/Screenshot_2026_0417_195415.jpg?raw=true) | ![Image2](https://github.com/rajatt04/Student-Management-System/blob/sms-main/Projects/Creamie/Screenshot_20260417_195433.jpg?raw=true) | ![Image3](https://github.com/rajatt04/Student-Management-System/blob/sms-main/Projects/Creamie/Screenshot_20260417_195438.jpg?raw=true) |![Image4](https://github.com/rajatt04/Student-Management-System/blob/sms-main/Projects/Creamie/Screenshot_20260417_195454.jpg?raw=true) |

| Screenshot 5      | Screenshot 6      | Screenshot 7      |Screenshot 8       |
|-------------------|-------------------|-------------------|-------------------|
| ![Image5](https://github.com/rajatt04/Student-Management-System/blob/sms-main/Projects/Creamie/Screenshot_20260417_195425.jpg?raw=true) | ![Image6](https://github.com/rajatt04/Student-Management-System/blob/sms-main/Projects/Creamie/Screenshot_20260417_195451.jpg?raw=true) | ![Image7](https://github.com/rajatt04/Student-Management-System/blob/sms-main/Projects/Creamie/Screenshot_20260417_195532.jpg?raw=true) |![Image8](https://github.com/rajatt04/rajatt04.github.io/blob/main/images/Creamie/Screenshot_2026_0318_101134.jpg?raw=true) |

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin
- **UI & Animations**: Jetpack Compose, Compose Material3, Coil (Image Loading), Lottie, Glance (Widgets)
- **Architecture**: MVI/MVVM, State Hoisting, Clean Architecture
- **Dependency Injection**: Dagger Hilt
- **Network & Data**: Retrofit, OkHttp, Paging 3
- **Local Storage**: Room Database, Preferences DataStore
- **Concurrency & Background Tasks**: Kotlin Coroutines, Flow, WorkManager
- **Testing**: JUnit, MockK, Turbine
- **Debugging**: LeakCanary

---

## 📦 Getting Started

To get a local copy up and running, follow these simple steps:

1. **Clone the repo**
   ```bash
   git clone https://github.com/rajatt04/Creamie.git
   ```

2. **Setup Pexels API Key**
   - Create a file named `local.properties` in the root directory if it doesn't exist.
   - Add your Pexels API key to the file:
     ```properties
     PEXELS_API_KEY="your_api_key_here"
     ```

3. **Open in Android Studio**
   - Open the project in the latest stable version of Android Studio with Compose support.

4. **Build & Run**
   - Sync the Gradle project and run it on an emulator or device running Android 8.0 (API 26+) or higher.

---

## 🤝 Contributing

Contributions and suggestions are warmly welcome!
Simply fork the repo, create a new branch, and submit a pull request.

---

## 📄 License

This project is licensed under the MIT License — see the `LICENSE` file for details.

---

## 🎉 Why “Creamie”?
Because this app is as smooth, delightful, and modern as your favorite ice cream flavor. Jetpack Compose never felt this sweet! 🍦
