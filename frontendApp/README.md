# BookingApp Frontend (Android)

This folder contains the **Android mobile application** for the BookingApp system.  
The app is built in **Android Studio** using **Java** and communicates with the backend via **TCP sockets**.

## Overview

The Android app serves as the **user interface** for the BookingApp system:

- **Client role:**
  - Browse available hotels.
  - Apply filters (location, dates, price, rating) for a better exploration experience.
  - View hotel details (capacity, reviews, pricing).
  - Make, cancel, and manage reservations.

- **Manager role:**
  - Manage hotel/property listings.
  - Update details such as pricing, capacity, and availability.
  - View and monitor reservations made by clients.

The app mirrors the same roles as the backend **Console client**, but in a **mobile-friendly interface**.

## Architecture

- **Frontend (this app):** Android app providing the interface for Clients and Managers.
- **Backend:** Java TCP server (see `../backend/BookingApp`).
- **Communication:** The app establishes a TCP connection to the backend’s **Master** node and sends/receives messages according to the protocol.

## Notes

- The app depends on a **running backend** (Reducer → Workers → Master).  
- If the backend is not running, the app will not be able to fetch or send booking data.  
- All business logic is handled in the backend; the frontend only provides the **UI and interaction layer**.
