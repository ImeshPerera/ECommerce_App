# ECommerce App

A native Android e-commerce application developed with **Java and XML** using Android Studio. The application provides a complete customer shopping flow including authentication, product browsing, categories, cart management, wishlist, checkout, address management, order history, order details, notifications, and invoice generation.

The project uses **Firebase** as the cloud backend and integrates services such as **Cloud Firestore, Firebase Authentication, Firebase Cloud Messaging, Firebase Storage, and Google Sign-In**.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Application Package](#application-package)
- [Project Structure](#project-structure)
- [Requirements](#requirements)
- [Getting Started](#getting-started)
- [Firebase Configuration](#firebase-configuration)
  - [Create a Firebase Project](#1-create-a-firebase-project)
  - [Register the Android App](#2-register-the-android-app)
  - [Configure google-servicesjson](#3-configure-google-servicesjson)
  - [Enable Firebase Authentication](#4-enable-firebase-authentication)
  - [Configure Google Sign-In](#5-configure-google-sign-in)
  - [Create Cloud Firestore](#6-create-cloud-firestore)
  - [Firestore Collections](#7-firestore-collections)
  - [Firestore Security Rules](#8-firestore-security-rules)
  - [Firestore Indexes](#9-firestore-indexes)
  - [Import Existing Firestore Data](#10-import-existing-firestore-data)
  - [Firebase Cloud Messaging](#11-firebase-cloud-messaging)
  - [Firebase Storage](#12-firebase-storage)
- [Firestore Backup / Export](#firestore-backup--export)
- [Running the Application](#running-the-application)
- [Development Workflow](#development-workflow)
- [Git Branching Strategy](#git-branching-strategy)
- [Troubleshooting](#troubleshooting)
- [Security Notes](#security-notes)
- [Project Status](#project-status)
- [Author](#author)

---

# Project Overview

The ECommerce App is a customer-facing Android shopping application designed to demonstrate a complete modern mobile e-commerce workflow.

The application connects an Android client to Firebase services for authentication, cloud database operations, media storage, and push notifications.

### Main application flow

```text
User
 │
 ├── Register / Login
 │       │
 │       └── Firebase Authentication
 │
 ├── Browse Products
 │       ├── Categories
 │       ├── Search / Filtering
 │       └── Related Products
 │
 ├── Product Details
 │       ├── Add to Cart
 │       └── Add to Wishlist
 │
 ├── Cart
 │       │
 │       └── Checkout
 │              ├── Address
 │              ├── Order
 │              └── Invoice
 │
 ├── Orders
 │       ├── Order History
 │       ├── Order Details
 │       └── Tracking / Status
 │
 └── Notifications
         │
         └── Firebase Cloud Messaging
```

---

# Features

## Authentication

- Email/password registration
- Email/password login
- Forgot password functionality
- Google Sign-In
- Firebase Authentication integration
- User profile information

## Product Management

- Product listing
- Product categories
- New products
- Product details
- Related products
- Product images
- Product quantity / stock handling
- Product filtering

## Shopping Cart

- Add products to cart
- Update quantities
- Remove products
- Stock-aware quantity handling
- Cart total calculation
- Checkout flow

## Wishlist

- Add products to wishlist
- Remove products from wishlist
- Wishlist product listing
- Wishlist synchronization with Firebase

## Address Management

- Add delivery address
- Edit address
- Delete address
- Select address during checkout
- Store customer addresses in Firestore

## Orders

- Place orders
- Order history
- Order details
- Order item details
- Order status
- Order tracking information
- Invoice generation

## Notifications

- Firebase Cloud Messaging
- Push notification handling
- Notification helper
- Notification service integration

## Additional Features

- Related product recommendations
- Profile management
- Bottom navigation
- Loading / UI feedback
- Invoice generation
- Firebase cloud data synchronization

---

# Technology Stack

| Technology | Purpose |
|---|---|
| Java | Android application development |
| XML | Android UI layouts |
| Android Studio | Development IDE |
| Gradle | Build and dependency management |
| Firebase Authentication | User authentication |
| Cloud Firestore | Cloud database |
| Firebase Cloud Messaging | Push notifications |
| Firebase Storage | Media/file storage |
| Google Sign-In | Google authentication |
| Gson | JSON/data parsing |
| Glide | Image loading |
| PayHere | Payment integration |
| Material Components | Android UI components |

---

# Application Package

The Android application package/application ID is:

```text
com.imeshperera.ecomapp
```

This package name must be used when registering the Android application inside Firebase.

---

# Project Structure

The project follows a conventional Android application structure:

```text
ECommerceApp/
│
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/imeshperera/ecomapp/
│   │       │       ├── activities/
│   │       │       ├── adapters/
│   │       │       ├── fragments/
│   │       │       ├── models/
│   │       │       ├── services/
│   │       │       └── utils/
│   │       │
│   │       ├── res/
│   │       │   ├── drawable/
│   │       │   ├── layout/
│   │       │   ├── menu/
│   │       │   ├── values/
│   │       │   └── xml/
│   │       │
│   │       └── AndroidManifest.xml
│   │
│   ├── build.gradle
│   └── google-services.json
│
├── gradle/
├── build.gradle
├── settings.gradle
├── gradle.properties
├── gradlew
├── gradlew.bat
└── README.md
```

---

# Requirements

Before running the project, install:

- Android Studio
- Android SDK
- JDK version compatible with the project's Gradle/Android Gradle Plugin configuration
- Git
- A Firebase account

For Firebase CLI operations, Node.js and npm are also required.

---

# Getting Started

## 1. Clone the Repository

```bash
git clone https://github.com/ImeshPerera/ECommerce_App.git
```

Move into the project:

```bash
cd ECommerce_App
```

Open the project in Android Studio.

---

## 2. Allow Gradle to Sync

Open the project and allow Android Studio to:

- Download Gradle dependencies
- Sync the project
- Index the source code
- Download required Android SDK components

Resolve any SDK or JDK requirement reported by Android Studio before continuing.

---

# Firebase Configuration

Firebase configuration is required before the application can fully communicate with the backend.

The original development Firebase project used:

```text
Firebase Project ID:
ecommerce-app-44f11

Firestore Database:
(default)

Firestore Mode:
Firestore Native

Firestore Location:
nam5

Edition:
Standard
```

A developer cloning this repository should normally create and use **their own Firebase project**.

---

# 1. Create a Firebase Project

Open:

https://console.firebase.google.com/

Create a new Firebase project.

Example:

```text
Project name:
ECommerce App - Development
```

The Firebase project ID can be different from the original project.

---

# 2. Register the Android App

Inside Firebase:

```text
Project Settings
    ↓
General
    ↓
Your apps
    ↓
Add app
    ↓
Android
```

Use:

```text
Android package name:
com.imeshperera.ecomapp
```

If Firebase asks for a SHA-1 certificate, add the SHA-1 certificate belonging to the Android environment being used.

### Why SHA-1 matters

Google Sign-In requires the Android application's signing certificate to be registered correctly.

For local debug builds, you can obtain the debug SHA-1 using Android Studio's Gradle tasks or:

```bash
./gradlew signingReport
```

On Windows:

```powershell
.\gradlew signingReport
```

Look for the `SHA1` value under the debug variant.

---

# 3. Configure `google-services.json`

After registering the Android application, Firebase provides:

```text
google-services.json
```

Download the file and place it here:

```text
app/google-services.json
```

The repository may contain a placeholder configuration. Replace it with the configuration generated by **your own Firebase project**.

The file must correspond to:

```text
com.imeshperera.ecomapp
```

### Important

Do not take a `google-services.json` file from another developer's Firebase project and expect it to connect to your own Firebase project.

Each Firebase project generates its own Android configuration.

---

# 4. Enable Firebase Authentication

Open:

```text
Firebase Console
    ↓
Build
    ↓
Authentication
```

Enable the authentication providers required by the application.

At minimum, configure:

```text
Email/Password
```

For Google Sign-In, also configure:

```text
Google
```

Make sure the Android application SHA-1/SHA-256 certificates are correctly registered when required.

---

# 5. Configure Google Sign-In

Google Sign-In depends on both Firebase Authentication and the Android application configuration.

Verify:

```text
Firebase Project
    ↓
Project Settings
    ↓
Your Android App
```

Confirm:

- Package name is correct
- SHA-1 is registered
- SHA-256 is registered where required
- Google authentication provider is enabled

After changing Firebase configuration, download a fresh:

```text
google-services.json
```

and replace:

```text
app/google-services.json
```

Then rebuild the application.

---

# 6. Create Cloud Firestore

Open:

```text
Firebase Console
    ↓
Build
    ↓
Firestore Database
```

Create a database.

Use:

```text
Database ID:
(default)
```

Select:

```text
Firestore Native
```

The original project uses:

```text
Location:
nam5
```

For a new project, select an appropriate location for your environment. Firestore database locations cannot simply be changed after database creation, so choose carefully.

---

# 7. Firestore Collections

The current development database contains the following collection groups:

```text
AddToCart
Category
New Products
PlacedOrders
users
wishlist
```

The database structure may evolve during future development.

### Example logical structure

```text
users
 └── user document
      ├── profile information
      └── account information

Category
 └── category documents

New Products
 └── product documents

AddToCart
 └── cart documents

wishlist
 └── wishlist documents

PlacedOrders
 └── order documents
```

Always refer to the current application models and Firestore access code as the source of truth for the latest field structure.

---

# 8. Firestore Security Rules

Firestore Security Rules control access to database documents.

For a production-ready project, rules should be maintained as source-controlled configuration.

Recommended file:

```text
firestore.rules
```

Example Firebase CLI deployment:

```bash
firebase deploy --only firestore:rules
```

### Important

Do not use unrestricted rules such as:

```text
allow read, write: if true;
```

for a production application.

Rules should be based on authentication and the application's authorization requirements.

If the repository does not yet contain a finalized `firestore.rules`, configure the rules in Firebase Console and export/maintain them as part of the project's backend configuration before production deployment.

---

# 9. Firestore Indexes

If Firestore queries require composite indexes, maintain them in:

```text
firestore.indexes.json
```

Deploy indexes using:

```bash
firebase deploy --only firestore:indexes
```

Firestore index configuration is separate from the database data export.

---

# 10. Import Existing Firestore Data

A development Firestore export was created from the original project.

## Export information

```text
Date:
2026-08-11

Database:
(default)

Collection groups:
All Collections

Documents:
24

Size:
14.68 KB
```

The export was stored in Google Cloud Storage under:

```text
ecommerce-app-44f11.appspot.com/
2026-08-11T11:44:34_33519/
```

### Important

The Firestore export is **not stored in this Git repository**.

It is maintained separately in Google Cloud Storage.

The export may contain application/user/order data and should therefore only be shared with authorized developers.

---

## Importing the Existing Database

If you have authorized access to the Firestore export, create your destination Firebase project first.

### Step 1 — Create Firebase Project

Create a Firebase project from:

https://console.firebase.google.com/

### Step 2 — Create Firestore

Create:

```text
Database ID:
(default)
```

using Firestore Native mode.

### Step 3 — Ensure Required Billing/Permissions

Managed Firestore import/export requires the appropriate Google Cloud billing and IAM permissions.

The destination project must be able to access the Cloud Storage location containing the export.

### Step 4 — Open Import/Export

In Google Cloud Console:

```text
Firestore
    ↓
Databases
    ↓
(default)
    ↓
Import/Export
```

Select:

```text
Import
```

### Step 5 — Select the Export Metadata

Select the Firestore export's:

```text
.overall_export_metadata
```

file.

Start the import job.

Firestore will recreate the exported documents and collections in the destination database.

---

## Important Firestore Import Notes

The Firestore export is not a traditional SQL dump.

It is a Google-managed Firestore export stored in Cloud Storage.

Conceptually:

```text
MySQL
    ↓
database.sql
```

whereas Firestore uses:

```text
Firestore
    ↓
Google Cloud Storage
    ↓
Firestore export files
```

The export contains Firestore data, but it does not automatically transfer the entire Firebase project configuration.

It does not replace:

- `google-services.json`
- Firebase Authentication configuration
- Google Sign-In configuration
- Firestore Security Rules
- Firestore index definitions
- Firebase Storage configuration
- Firebase Cloud Messaging configuration
- Google Cloud service-account credentials

---

# Firestore Backup / Export

For development and disaster recovery, Firestore data can be exported through:

```text
Google Cloud Console
    ↓
Firestore
    ↓
(default)
    ↓
Import/Export
    ↓
Export
```

Recommended export:

```text
Export entire database
Export current state of database
```

Select a Google Cloud Storage bucket as the destination.

The project currently has a development export containing:

```text
24 documents
14.68 KB
```

Regular exports are recommended before major database changes.

---

# 11. Firebase Cloud Messaging

The application includes Firebase Cloud Messaging integration for push notifications.

The project contains a Firebase messaging service:

```text
app/src/main/java/com/imeshperera/ecomapp/services/MyFirebaseMessagingService.java
```

and notification handling utilities.

For a new Firebase project:

1. Configure the Android application.
2. Add the correct `google-services.json`.
3. Ensure Firebase Cloud Messaging is enabled.
4. Build and run the application.
5. Test notification delivery on a real Android device.

Some notification functionality may require additional backend/server-side logic depending on how notifications are triggered.

---

# 12. Firebase Storage

Firebase Storage is used for cloud-hosted application media where applicable.

Enable it through:

```text
Firebase Console
    ↓
Build
    ↓
Storage
```

Configure appropriate Storage Security Rules.

Do not use unrestricted production rules such as:

```text
allow read, write: if true;
```

unless this is intentionally a temporary development environment.

---

# Firebase CLI Setup

Install the Firebase CLI:

```bash
npm install -g firebase-tools
```

Login:

```bash
firebase login
```

Verify:

```bash
firebase projects:list
```

Initialize Firebase configuration when required:

```bash
firebase init
```

Select the services required by the project.

Associate the local directory with a Firebase project:

```bash
firebase use --add
```

Then select the Firebase project created for development.

---

# Recommended Firebase Repository Files

For a maintainable project, Firebase backend configuration can be maintained alongside the Android source:

```text
ECommerceApp/
│
├── app/
│   └── google-services.json
│
├── firebase.json
├── .firebaserc
├── firestore.rules
├── firestore.indexes.json
└── README.md
```

The database export itself should normally remain outside the Git repository when it contains real user or transactional data.

---

# Running the Application

After Firebase configuration:

1. Open the project in Android Studio.
2. Confirm the correct JDK is configured.
3. Allow Gradle synchronization to complete.
4. Confirm `app/google-services.json` belongs to the Firebase project.
5. Verify Firebase Authentication configuration.
6. Verify Firestore configuration.
7. Verify Google Sign-In configuration.
8. Connect an Android device or start an emulator.
9. Build and run the application.

---

# Build from Command Line

On Windows:

```powershell
.\gradlew assembleDebug
```

Install the debug APK to a connected device:

```powershell
.\gradlew installDebug
```

Run tests where available:

```powershell
.\gradlew test
```

---

# Development Workflow

This project uses Git branches for phase-based development.

The stable branch is:

```text
main
```

Development is performed on phase branches:

```text
upgrade/phase1
upgrade/phase2
upgrade/phase3
upgrade/phase4
upgrade/phase5
...
```

## Start a New Phase

Always start from the latest `main`:

```powershell
git switch main
git pull --ff-only origin main
```

Create the next phase:

```powershell
git switch -c upgrade/phase5
```

Push the new branch:

```powershell
git push -u origin upgrade/phase5
```

---

# Commit Workflow

During a phase, make as many commits as required.

Example:

```powershell
git status
git add .
git commit -m "phase 5: implement product search"
git push
```

Then continue development:

```powershell
git add .
git commit -m "phase 5: improve search filtering"
git push
```

There is no requirement to have only one commit per phase.

---

# Pull Request Workflow

When a phase is complete:

```text
upgrade/phase5
       ↓
Pull Request
       ↓
main
```

After the Pull Request is reviewed and merged:

1. Delete the remote phase branch if appropriate.
2. Return to local `main`.
3. Pull the latest `main`.
4. Create the next phase branch.

Example:

```powershell
git switch main
git pull --ff-only origin main
git switch -c upgrade/phase6
```

### Branch rule

```text
main
    = stable / integrated code

upgrade/phaseX
    = active development
```

Avoid developing directly on `main`.

---

# Troubleshooting

## Google Sign-In Failed

Check:

- Firebase Android package name
- SHA-1 certificate
- SHA-256 certificate where applicable
- Google Sign-In provider
- `google-services.json`
- Firebase project ID
- Rebuild after changing Firebase configuration

For local development, obtain signing information with:

```powershell
.\gradlew signingReport
```

---

## Firebase Connection Problems

Check:

```text
app/google-services.json
```

Confirm that the package name matches:

```text
com.imeshperera.ecomapp
```

Then:

```text
File
 → Sync Project with Gradle Files
```

and rebuild the application.

---

## Firestore Permission Denied

A `PERMISSION_DENIED` error normally indicates a Firestore Security Rules or authentication problem.

Check:

1. User authentication state
2. Firebase Authentication provider
3. Firestore Security Rules
4. User UID
5. Collection/document path
6. Firebase project selected by `google-services.json`

Do not solve permission problems by permanently changing production rules to unrestricted access.

---

## Firestore Index Error

If Firestore reports a missing index:

1. Check the error message.
2. Identify the required query/index.
3. Create the index in Firebase Console or add it to `firestore.indexes.json`.
4. Deploy the index if using Firebase CLI.

---

## Gradle / Android Studio Problems

Try:

```text
File
 → Sync Project with Gradle Files
```

If required, clean and rebuild:

```text
Build
 → Clean Project
Build
 → Rebuild Project
```

Avoid deleting project files or Gradle configuration unless the actual error requires it.

---

# Security Notes

## Never Commit Private Credentials

Do not commit:

```text
service-account.json
firebase-adminsdk-*.json
*.pem
*.p12
private API keys
private signing keys
```

to GitHub.

Never upload Android signing keystores or passwords to a public repository.

---

## Firestore Data

The development Firestore export may contain sensitive application data such as:

- User accounts
- User profile information
- Orders
- Addresses
- Cart data
- Wishlist data

Do not publish a real production/development database export to a public Git repository.

Use sanitized sample data when a public demonstration dataset is required.

---

# Project Status

The project is being developed incrementally through phase-based Git branches.

Current development branch:

```text
upgrade/phase5
```

Completed phases include:

```text
Phase 1
- Bottom navigation
- User profile
- Password reset

Phase 2
- Quantity / stock handling
- Wishlist

Phase 3
- Shipping
- Invoice
- Order history
- Order tracking

Phase 4
- Address management
- Checkout improvements
- Google Sign-In
- Notifications
- Related products
- Order details
- Firebase integration improvements
```

Phase 5 is the current active development phase.

Features may continue to change during development.

---

# Useful Firebase Resources

- Firebase Console: https://console.firebase.google.com/
- Firebase Android Setup: https://firebase.google.com/docs/android/setup
- Firebase Authentication: https://firebase.google.com/docs/auth
- Cloud Firestore: https://firebase.google.com/docs/firestore
- Firestore Export/Import: https://firebase.google.com/docs/firestore/manage-data/export-import
- Firestore Data Migration: https://firebase.google.com/docs/firestore/manage-data/move-data
- Firebase Cloud Messaging: https://firebase.google.com/docs/cloud-messaging
- Firebase Storage: https://firebase.google.com/docs/storage
- Firebase CLI: https://firebase.google.com/docs/cli

---

# Author

**Imesh Dilshan Perera**

GitHub:

https://github.com/ImeshPerera

Project repository:

https://github.com/ImeshPerera/ECommerce_App

---

# License

This project is intended primarily as a software engineering / educational development project.

If you plan to redistribute, commercialize, or substantially reuse the project, review and add an appropriate open-source license to the repository.
