# ECommerce App

A native Android e-commerce application developed using **Java, XML, Android Studio, and Firebase**.

The application provides a complete customer shopping experience including authentication, product browsing, categories, cart management, wishlist, checkout, address management, orders, notifications, related products, and invoice generation.

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Application Package](#application-package)
- [Project Structure](#project-structure)
- [Requirements](#requirements)
- [Getting Started](#getting-started)
- [Firebase Configuration](#firebase-configuration)
- [Cloud Firestore Setup](#cloud-firestore-setup)
- [Firestore Seed Data](#firestore-seed-data)
- [Firestore Collections Created Automatically](#firestore-collections-created-automatically)
- [Google Sign-In Configuration](#google-sign-in-configuration)
- [Firebase Cloud Messaging](#firebase-cloud-messaging)
- [Firebase Storage](#firebase-storage)
- [Running the Application](#running-the-application)
- [Troubleshooting](#troubleshooting)
- [Security Notes](#security-notes)
- [Project Status](#project-status)
- [Author](#author)

---

# Overview

The ECommerce App is a customer-facing Android shopping application.

The application uses Firebase as its cloud backend and Cloud Firestore as its primary application database.

The project is organized so that another developer can clone the repository, create their own Firebase project, configure the Android application, create a Firestore database, add the initial catalog seed data, and run the application.

---

# Features

## Authentication

- Email/password registration
- Email/password login
- Forgot password
- Google Sign-In
- Firebase Authentication integration
- User profile management

## Product Features

- Product listing
- Product categories
- New products
- Popular products
- Product details
- Related products
- Product images
- Stock management
- Product filtering

## Cart

- Add products to cart
- Change product quantities
- Remove products
- Stock-aware quantity handling
- Cart total calculation
- Checkout

## Wishlist

- Add products to wishlist
- Remove products from wishlist
- Wishlist listing
- Firebase synchronization

## Address Management

- Add address
- Edit address
- Delete address
- Select checkout address
- Store customer addresses in Firestore

## Orders

- Place orders
- Order history
- Order details
- Order item details
- Order status
- Order tracking
- Invoice generation

## Notifications

- Firebase Cloud Messaging
- Push notification handling
- Notification service
- Notification helper

---

# Technology Stack

| Technology | Purpose |
|---|---|
| Java | Android application development |
| XML | Android UI |
| Android Studio | Development IDE |
| Gradle | Build system |
| Firebase Authentication | Authentication |
| Cloud Firestore | Cloud database |
| Firebase Cloud Messaging | Push notifications |
| Firebase Storage | Image/file storage |
| Google Sign-In | Authentication |
| Gson | Data parsing |
| Glide | Image loading |
| PayHere | Payment integration |

---

# Application Package

The Android application ID is:

```text
com.imeshperera.ecomapp
```

This exact package name should be used when registering the Android application in Firebase.

---

# Project Structure

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
├── firebase/
│   └── firestore_seed.json
│
├── build.gradle
├── settings.gradle
├── gradle.properties
├── gradlew
├── gradlew.bat
└── README.md
```

---

# Requirements

Before running the application, install:

- Android Studio
- Android SDK
- A compatible JDK for the project's Gradle/Android Gradle Plugin version
- Git
- A Firebase account

For optional automated Firestore seeding using the Firebase Admin SDK:

- Node.js
- npm

---

# Getting Started

## 1. Clone the Repository

```bash
git clone https://github.com/ImeshPerera/ECommerce_App.git
```

Enter the project:

```bash
cd ECommerce_App
```

Open the project in Android Studio.

Allow Android Studio to complete Gradle synchronization before running the application.

---

# Firebase Configuration

The application uses Firebase for:

- Firebase Authentication
- Cloud Firestore
- Firebase Cloud Messaging
- Firebase Storage
- Google Sign-In

A developer should normally create and use their own Firebase project rather than using another developer's Firebase credentials.

---

# Cloud Firestore Setup

## 1. Create a Firebase Project

Open:

https://console.firebase.google.com/

Create a new Firebase project.

For example:

```text
ECommerce_App
```

The Firebase project ID does not need to match the original project.

---

## 2. Register the Android Application

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
Package name:
com.imeshperera.ecomapp
```

If Firebase requests SHA certificates, add the SHA-1/SHA-256 certificates for the Android signing environment being used.

For local development, signing information can be obtained with:

```powershell
.\gradlew signingReport
```

Look for the `SHA1` and `SHA-256` values under the appropriate build variant.

---

## 3. Download `google-services.json`

After registering the Android application, Firebase provides:

```text
google-services.json
```

Place it in:

```text
app/google-services.json
```

If the repository contains a placeholder configuration, replace it with the configuration generated for your own Firebase project.

The configuration must belong to:

```text
com.imeshperera.ecomapp
```

---

# Firestore Database

In Firebase Console:

```text
Build
    ↓
Firestore Database
    ↓
Create database
```

Create:

```text
Database ID:
(default)
```

Select:

```text
Firestore Native
```

The original development project used:

```text
Database ID: (default)
Mode: Firestore Native
Edition: Standard
Location: nam5
```

For a new Firebase project, choose an appropriate Firestore location for your project.

---

# Firestore Seed Data

The repository contains a simple seed file:

```text
firestore_seed.json
```

The purpose of this file is to provide the **initial catalog data** required by the Android application.

You do not need to recreate the entire Firestore database manually.

Only these two collections require initial data:

```text
Category
New Products
```

The other user-related collections are created automatically by the application when users perform the relevant actions.

---

# Seed File Overview

The seed file contains data formatted specifically to match the Android application's Firestore collection names and model classes.

---

## 1. Category Collection

Collection name:

```text
Category
```

The initial categories are:

- Shoes
- Clothing
- Watches
- Bags
- Electronics

Fields:

```text
name
type
img_url
```

Example:

```json
{
  "name": "Shoes",
  "type": "shoes",
  "img_url": "https://..."
}
```

---

## 2. New Products Collection

Collection name:

```text
New Products
```

Products are assigned using:

```text
cat: "new"
```

or:

```text
cat: "popular"
```

This matches the product queries used by the application.

Fields:

```text
name
brand
type
cat
price
rate
stock
img_url
detail
```

Example:

```json
{
  "name": "Nike Air Max 270",
  "brand": "Nike",
  "type": "shoes",
  "cat": "new",
  "price": "150",
  "rate": "4.8",
  "stock": 25,
  "img_url": "https://...",
  "detail": "Sample product description"
}
```

The complete seed data is available in:

```text
firestore_seed.json
```

---

# How to Add the Seed Data

The seed file is included in the repository so developers can use it as the source for the initial Firestore catalog.

The only initial collections that need to be populated are:

```text
Category
New Products
```

You can use either of the following methods.

## Method 1 - Firebase Console

Open:

```text
Firebase Console
    ↓
Firestore Database
    ↓
Data
```

Create:

```text
Category
```

and add the category documents/fields from:

```text
firestore_seed.json
```

Then create:

```text
New Products
```

and add the product documents/fields from the same seed file.

This is the simplest method for a small development dataset.

## Method 2 - Firebase Admin SDK

For automated setup, the seed JSON can be loaded by a Node.js script using the Firebase Admin SDK.

The basic Firestore operation is:

```javascript
firestore
    .collection("Category")
    .doc("cat_shoes")
    .set({
        name: "Shoes",
        type: "shoes",
        img_url: "https://..."
    });
```

For products:

```javascript
firestore
    .collection("New Products")
    .doc("prod_1")
    .set({
        name: "Nike Air Max 270",
        brand: "Nike",
        type: "shoes",
        cat: "new",
        price: "150",
        rate: "4.8",
        stock: 25,
        img_url: "https://...",
        detail: "Sample product description"
    });
```

A complete automated seed script can be added to the repository later if required.

---

# Firestore Collections Created Automatically

You do **not** need to manually create every Firestore collection.

The application creates and updates user-related data automatically when the relevant feature is used.

| Firestore Collection / Path | Seed Required? | Created / Updated By |
|---|---|---|
| `Category` | Yes | Initial seed data |
| `New Products` | Yes | Initial seed data |
| `users` | No | User registration / profile update |
| `users/{uid}/addresses` | No | Add/Edit Address |
| `AddToCart/{uid}/User` | No | Add to Cart |
| `wishlist/{uid}/items` | No | Wishlist |
| `PlacedOrders` | No | Checkout / order placement |

Firestore creates the collection/document path when the application performs its first write.

---

# Runtime Firestore Flow

```text
Application starts
       │
       ├── Category
       │      └── Initial seed data
       │
       └── New Products
              └── Initial seed data

                     ↓

              User interacts
                     │
        ┌────────────┼────────────┐
        │            │            │
     Register    Add Address   Add to Cart
        │            │            │
        ▼            ▼            ▼
      users       addresses     AddToCart
        │
        ├─────────────────┐
        │                 │
     Wishlist          Checkout
        │                 │
        ▼                 ▼
    wishlist          PlacedOrders
```

---

# How Runtime Collections Are Created

## User Registration

When a user registers, the application creates or updates:

```text
users/{uid}
```

The user document contains the profile/account information required by the application.

---

## Add Address

When a user saves a delivery address:

```text
users/{uid}/addresses/{addressId}
```

is created.

---

## Add to Cart

When a user adds a product to the shopping cart:

```text
AddToCart/{uid}/User
```

is created or updated.

---

## Wishlist

When a user taps the heart/favorite button:

```text
wishlist/{uid}/items
```

is created or updated.

---

## Place Order

When a user completes checkout:

```text
PlacedOrders/{orderId}
```

is created.

---

# Firebase Authentication

Enable Firebase Authentication if you want to test registration and login.

In Firebase Console:

```text
Build
    ↓
Authentication
    ↓
Sign-in method
```

Enable:

```text
Email/Password
```

For Google Sign-In, also enable:

```text
Google
```

---

# Google Sign-In Configuration

Google Sign-In requires the Android application to be correctly registered in Firebase.

Verify:

```text
Package:
com.imeshperera.ecomapp
```

Also configure the appropriate SHA-1/SHA-256 certificates.

For local development:

```powershell
.\gradlew signingReport
```

After changing Firebase Android configuration, download a fresh:

```text
google-services.json
```

and replace:

```text
app/google-services.json
```

Then synchronize and rebuild the Android project.

---

# Firebase Cloud Messaging

The project includes Firebase Cloud Messaging support for push notifications.

The application contains:

```text
app/src/main/java/com/imeshperera/ecomapp/services/MyFirebaseMessagingService.java
```

To use FCM with your own Firebase project:

1. Configure the Android application.
2. Add your own `google-services.json`.
3. Enable Firebase Cloud Messaging.
4. Build and run the application on a real Android device.
5. Test notification delivery.

---

# Firebase Storage

Firebase Storage can be enabled from:

```text
Firebase Console
    ↓
Build
    ↓
Storage
```

The application uses image URLs for product images.

When using your own Firebase project, configure Storage and its security rules according to the application's requirements.

you can add your own images to related storage and update seed links matching accordingly 

---

# Firestore Security Rules

Firestore Security Rules control access to database data.

Before using the application in production, configure rules based on:

- Authentication state
- User UID
- Collection access
- Application authorization requirements

Do not use unrestricted production rules such as:

```text
allow read, write: if true;
```

The rules should be tested before deployment.

If `firestore.rules` is added to the repository later, it should be treated as the source-controlled version of the Firestore rules.

---

# Firestore Indexes

If Firestore reports that a query requires an index, create the required index through Firebase Console.

For a source-controlled setup, maintain:

```text
firestore.indexes.json
```

and deploy using the Firebase CLI when appropriate:

```bash
firebase deploy--only firestore:indexes
```

---

# Running the Application

After completing the Firebase setup:

1. Open the project in Android Studio.
2. Confirm the correct JDK is configured.
3. Synchronize Gradle.
4. Configure `app/google-services.json`.
5. Create the Firestore `(default)` database.
6. Add the `Category` seed data.
7. Add the `New Products` seed data.
8. Enable Firebase Authentication.
9. Configure Google Sign-In if required.
10. Configure FCM if notifications are required.
11. Connect an Android device or start an emulator.
12. Run the application.

---

# First-Time Setup Summary

For a new developer:

```text
Clone Repository
       ↓
Create Firebase Project
       ↓
Register Android App
       ↓
Package:
com.imeshperera.ecomapp
       ↓
Download google-services.json
       ↓
Place in:
app/google-services.json
       ↓
Create Firestore
Database: (default)
       ↓
Add Category Seed Data
       ↓
Add New Products Seed Data
       ↓
Enable Authentication
       ↓
Configure Google Sign-In
       ↓
Run Application
       ↓
Application automatically creates:
Users
Addresses
Cart
Wishlist
Orders
```

---

# Troubleshooting

## Google Sign-In Failed

Check:

- Firebase Android package name
- SHA-1 certificate
- SHA-256 certificate where applicable
- Google Sign-In provider
- `google-services.json`
- Firebase project
- Android application ID

Run:

```powershell
.\gradlew signingReport
```

and compare the SHA values with Firebase Console.

---

## Firebase Connection Problems

Check:

```text
app/google-services.json
```

Confirm that it belongs to:

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

Check:

- Firebase Authentication
- Firestore Security Rules
- Logged-in user UID
- Collection name
- Document path
- Firebase project configured in `google-services.json`

Do not make Firestore completely public as a permanent solution.

---

## Products Are Not Appearing

Check that the collection name is exactly:

```text
New Products
```

Check that product documents contain:

```text
name
brand
type
cat
price
rate
stock
img_url
detail
```

Also verify that:

```text
cat
```

contains either:

```text
new
```

or:

```text
popular
```

---

## Categories Are Not Appearing

Check that the collection name is exactly:

```text
Category
```

and that documents contain:

```text
name
type
img_url
```

---

# Security Notes

## Firebase Configuration

Each developer should use their own Firebase project and their own:

```text
app/google-services.json
```

Do not commit private Firebase Admin SDK credentials.

---

## Never Commit Private Credentials

Do not commit files such as:

```text
service-account.json
firebase-adminsdk-*.json
private API keys
private signing keys
keystores
passwords
```

to a public GitHub repository.

A Firebase Admin SDK service-account key provides privileged access and must remain private.

---

## Firestore Data

The seed file is intended for development/demo catalog data.

Do not place real customer information in:

```text
firebase/firestore_seed.json
```

Do not include:

- Real customer names
- Real phone numbers
- Real addresses
- Real FCM tokens
- Real payment information
- Production customer data
- Private credentials

---

# Project Status

The project is being developed incrementally through phase-based Git branches.

Current development phase:

```text
Phase 5
```

Completed phases include:

### Phase 1

- Bottom navigation
- User profile
- Password reset

### Phase 2

- Quantity / stock handling
- Wishlist

### Phase 3

- Shipping
- Invoice
- Order history
- Order tracking

### Phase 4

- Address management
- Checkout improvements
- Google Sign-In
- Notifications
- Related products
- Order details
- Firebase integration improvements

### Phase 5

Current active development phase.

Features and database structures may continue to change during development.

---

# Firebase Resources

- Firebase Console: https://console.firebase.google.com/
- Firebase Android Setup: https://firebase.google.com/docs/android/setup
- Firebase Authentication: https://firebase.google.com/docs/auth
- Cloud Firestore: https://firebase.google.com/docs/firestore
- Firebase Cloud Messaging: https://firebase.google.com/docs/cloud-messaging
- Firebase Storage: https://firebase.google.com/docs/storage
- Firebase Admin SDK: https://firebase.google.com/docs/admin/setup

---

# Author

**Imesh Dilshan Perera**

GitHub:

https://github.com/ImeshPerera

Project Repository:

https://github.com/ImeshPerera/ECommerce_App

---

# License

This project is primarily intended as a software engineering and educational project.

If you plan to redistribute, commercialize, or substantially reuse the project, add an appropriate license to the repository.
