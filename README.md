::: {align="center"}
# MediPulse Assist

### AI-Powered Medical Triage & Nearby Hospital Finder

**Describe a medical concern. Receive structured, general triage
guidance and discover nearby hospitals on an interactive map.**

[![React](https://img.shields.io/badge/Frontend-React%20%2B%20Vite-61DAFB?logo=react&logoColor=black)](https://react.dev/)
[![Java](https://img.shields.io/badge/Backend-Java%20%2B%20Spring%20Boot-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Maps](https://img.shields.io/badge/Maps-OpenStreetMap-7EBC6F?logo=openstreetmap&logoColor=white)](https://www.openstreetmap.org/)
[![AI](https://img.shields.io/badge/AI-Groq%20API-orange)](https://groq.com/)
[![Status](https://img.shields.io/badge/Project-In%20Development-yellow)](#-project-status)

[Overview](#-overview) · [Features](#-key-features) ·
[Architecture](#-how-it-works) · [Setup](#-getting-started) ·
[API](#-api-reference) · [Roadmap](#-roadmap)
:::

------------------------------------------------------------------------

> \[!CAUTION\] **This project is not an emergency service, medical
> device, diagnostic tool, or substitute for a qualified healthcare
> professional.**
>
> AI-generated guidance can be incomplete or incorrect. Do not use this
> application to decide whether emergency care is necessary or to delay
> seeking help. If someone may be experiencing a medical emergency,
> contact the local emergency number or go to the nearest emergency
> department immediately.

## 📌 Overview

**MediPulse Assist** is a full-stack web application concept designed to
bring two useful capabilities into one place:

1.  **AI-assisted general triage guidance** --- a user describes a
    medical concern in plain language and receives a structured response
    containing an urgency label, a suggested hospital department,
    general immediate-action guidance, and a disclaimer.
2.  **Nearby hospital discovery** --- the browser's location (with the
    user's permission) is used to search for nearby hospitals through
    OpenStreetMap data. Results are sorted by straight-line distance and
    shown on an interactive map.

The aim is to make relevant information easier to find during a
stressful situation. The application does **not** diagnose conditions,
prescribe medicines, or determine that it is safe to remain at home.

### 🎯 Project goals

-   Provide a simple, responsive interface for describing a medical
    concern.
-   Return structured AI-generated information rather than an
    unstructured chat response.
-   Find nearby hospital map features using OpenStreetMap's Overpass
    API.
-   Display the user's location and nearby hospital markers with
    Leaflet.
-   Provide a direct link to open directions in Google Maps.
-   Keep API credentials on the backend rather than exposing them in the
    browser.

## ✨ Key Features

  -----------------------------------------------------------------------
  Feature                             Description
  ----------------------------------- -----------------------------------
  Symptom / concern input             Enter a plain-language description
                                      of the medical concern.

  AI triage guidance                  Receive an urgency category
                                      (`HIGH`, `MODERATE`, or `LOW`),
                                      suggested specialty,
                                      immediate-action list, and
                                      disclaimer.

  Browser geolocation                 Request the user's coordinates
                                      after permission is granted.

  Nearby hospital search              Query OpenStreetMap's Overpass API
                                      within a configurable radius
                                      (planned default: 5 km).

  Distance sorting                    Calculate approximate straight-line
                                      distance with the Haversine
                                      formula.

  Interactive map                     Display the user's position and
                                      nearby hospital markers using React
                                      Leaflet.

  Hospital selection                  Selecting a hospital card focuses
                                      the map on its marker.

  Google Maps directions              Open a directions URL for a
                                      selected hospital.

  Responsive workspace                Present guidance and hospital
                                      results alongside the map, adapting
                                      to smaller screens.
  -----------------------------------------------------------------------

## 🧭 Application Experience

The initial application is planned around two routes:

  -----------------------------------------------------------------------
  Route                   Page                    Purpose
  ----------------------- ----------------------- -----------------------
  `/`                     Landing page            Explain the application
                                                  and provide a call to
                                                  action to begin.

  `/assist`               Triage workspace        Request location,
                                                  accept the user's
                                                  description, show
                                                  guidance and nearby
                                                  hospitals, and display
                                                  the map.
  -----------------------------------------------------------------------

### Triage workspace

The workspace is designed as a split layout:

-   **Guidance and results panel:** urgency label, recommended
    specialty, immediate-action list, disclaimer, and hospital cards.
-   **Map panel:** user's location and hospital markers.
-   **Hospital interaction:** selecting a hospital card pans or zooms
    the map to that hospital; the directions link opens Google Maps.

## 🏗️ How It Works

The frontend sends one request to the Spring Boot backend. The backend
starts the AI guidance request and the hospital lookup concurrently,
then combines the results into one response.

``` mermaid
flowchart TD
    A[User opens /assist] --> B[Browser requests location permission]
    B --> C[User enters medical concern]
    C --> D[React sends one POST request]
    B --> D
    D --> E[Spring Boot orchestration endpoint]
    E --> F[AI triage service]
    E --> G[Overpass hospital search]
    F --> H[Structured triage guidance]
    G --> I[Hospital results and distance sorting]
    H --> J[Combine response DTO]
    I --> J
    J --> K[React displays guidance, hospital cards and map]
    K --> L[User selects a hospital]
    L --> M[Map focuses on selected marker]
    L --> N[Open Google Maps directions]
```

### Backend orchestration

The planned endpoint is:

`POST /api/emergency/assist`

The controller coordinates two independent tasks using Java
`CompletableFuture`:

-   **AI task:** sends the user's description to the configured AI
    provider and parses the structured response.
-   **Hospital task:** queries Overpass for nearby hospital features,
    calculates distances, and sorts the results.

The controller waits for both tasks, combines them into an
`EmergencyResponse`, and returns the result to the frontend.

### Hospital search and distance

-   The browser supplies latitude and longitude after permission is
    granted.
-   The backend sends a radius-based query to the Overpass API.
-   Hospital results are sorted using the Haversine distance
    calculation.
-   The initial design returns up to five nearest results.
-   The displayed distance is an approximation in a straight line. It is
    **not** driving distance, walking distance, or estimated travel
    time.

## 🧰 Technology Stack

### Frontend

  Technology            Responsibility
  --------------------- -----------------------------------------------
  React                 Component-based user interface
  Vite                  Frontend development server and build tooling
  React Router          Navigation between `/` and `/assist`
  React Leaflet         Interactive map components
  Leaflet               Map rendering
  OpenStreetMap tiles   Map tile data
  Lucide React          Interface icons

### Backend

  -----------------------------------------------------------------------
  Technology / Service                Responsibility
  ----------------------------------- -----------------------------------
  Java                                Backend programming language

  Spring Boot                         REST API and application structure

  Spring `RestClient` (or equivalent  Calls external APIs
  HTTP client)                        

  `CompletableFuture`                 Runs AI and hospital lookup tasks
                                      concurrently

  Jackson                             Parses structured JSON responses

  Haversine formula                   Calculates approximate
                                      straight-line distance
  -----------------------------------------------------------------------

### External services

  -----------------------------------------------------------------------
  Service                             Purpose
  ----------------------------------- -----------------------------------
  Groq API (OpenAI-compatible chat    AI-generated structured triage
  completions interface)              guidance

  OpenStreetMap Overpass API          Nearby hospital map-feature search

  OpenStreetMap tile service          Interactive map tiles

  Google Maps directions URL          External navigation hand-off
  -----------------------------------------------------------------------

> **Provider configuration note:** The project discussion explored both
> OpenAI and Groq-compatible APIs. The later implementation direction
> uses Groq. Keep the AI provider, model name, and endpoint configurable
> through backend settings, and verify the provider's current model
> availability and limits before deployment.

## 🗂️ Suggested Repository Structure

The exact structure may evolve as implementation progresses. A suggested
monorepo layout is:

``` text
medipulse-assist/
│
├── frontend/
│   ├── public/
│   ├── src/
│   │   ├── components/
│   │   │   └── HospitalMap.jsx
│   │   ├── pages/
│   │   │   ├── LandingPage.jsx
│   │   │   └── TriagePage.jsx
│   │   ├── App.jsx
│   │   ├── App.css
│   │   └── main.jsx
│   ├── package.json
│   └── vite.config.js
│
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── .../
│   │   │   │       ├── controller/
│   │   │   │       ├── dto/
│   │   │   │       └── service/
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   ├── pom.xml
│   └── .env.example
│
├── .gitignore
└── README.md
```

## 🚀 Getting Started

> The commands below describe the planned local setup. Adjust paths and
> configuration names to match the implementation in your branch.

### Prerequisites

Install the following before starting:

-   Java (version compatible with the Spring Boot version used by the
    backend)
-   Maven (or use the Maven Wrapper if included)
-   Node.js and npm
-   A Groq API key
-   A modern browser with geolocation support

### 1. Clone the repository

``` bash
git clone https://github.com/<your-username>/<your-repository>.git
cd <your-repository>
```

Replace the URL and folder name with your actual GitHub repository
details.

### 2. Start the backend

Move into the backend directory:

``` bash
cd backend
```

Create a local environment configuration. For example:

``` env
GROQ_API_KEY=your_groq_api_key
GROQ_MODEL=your_supported_model
```

Configure the backend to read these values. The API key must remain on
the server and must never be placed in frontend environment variables or
committed to Git.

Run the application:

``` bash
./mvnw spring-boot:run
```

On Windows PowerShell, if the Maven Wrapper is present:

``` powershell
.\mvnw.cmd spring-boot:run
```

If the project does not include the wrapper, use:

``` bash
mvn spring-boot:run
```

The backend is expected to run on `http://localhost:8080` unless
configured otherwise.

### 3. Start the frontend

Open a second terminal and move into the frontend directory:

``` bash
cd frontend
npm install
npm run dev
```

Vite commonly serves the development app at:

`http://localhost:5173`

If the frontend uses a configurable API base URL, set it to the local
Spring Boot backend according to the variable name defined by the
implementation.

### 4. Allow location access

Open the frontend in your browser and navigate to the triage workspace.
Allow location access when prompted. Geolocation generally requires a
secure context (HTTPS) or `localhost`.

### 5. Try the workflow

1.  Open the landing page.
2.  Navigate to the triage workspace.
3.  Allow browser location access.
4.  Enter a medical concern (do not use real personal medical
    information while testing).
5.  Submit the request.
6.  Review the returned general guidance and nearby hospital results.
7.  Select a hospital card to focus the map.
8.  Use the directions link to open Google Maps.

## 🔌 API Reference

### `POST /api/emergency/assist`

Accepts the user's description and coordinates, then returns the AI
guidance and nearby hospital results.

#### Request body

``` json
{
  "condition": "Example description for development testing",
  "latitude": 22.5726,
  "longitude": 88.3639,
  "radiusMeters": 5000
}
```

  -----------------------------------------------------------------------
  Field                   Type                    Description
  ----------------------- ----------------------- -----------------------
  `condition`             `string`                User-provided
                                                  description of the
                                                  concern

  `latitude`              `number`                Browser latitude

  `longitude`             `number`                Browser longitude

  `radiusMeters`          `integer`               Hospital search radius
                                                  in metres; planned
                                                  default is 5000
  -----------------------------------------------------------------------

#### Response shape

The response structure follows the DTO design discussed for the project:

``` json
{
  "guidance": {
    "urgencyLevel": "MODERATE",
    "recommendedSpecialty": "Emergency Medicine",
    "immediateActions": [
      "Example general safety guidance",
      "Seek assessment from a qualified healthcare professional"
    ],
    "disclaimer": "This information is general guidance and is not a medical diagnosis."
  },
  "nearestHospitals": [
    {
      "id": "example-osm-id",
      "name": "Example Hospital",
      "address": "Example address",
      "latitude": 22.5726,
      "longitude": 88.3639,
      "distanceKm": 1.25
    }
  ]
}
```

> This is an illustrative contract, not a guarantee of the final
> serialized response. Confirm the exact `NearbyPlaceResponse` fields
> and JSON names against the implemented DTO before relying on this
> example.

### Planned backend DTOs

  -----------------------------------------------------------------------
  DTO                                 Responsibility
  ----------------------------------- -----------------------------------
  `EmergencyRequest`                  Holds the concern description,
                                      coordinates, and search radius

  `TriageGuidance`                    Holds urgency level, recommended
                                      specialty, immediate actions, and
                                      disclaimer

  `NearbyPlaceResponse`               Represents one nearby hospital
                                      result

  `EmergencyResponse`                 Combines triage guidance and the
                                      nearest hospital list
  -----------------------------------------------------------------------

### Suggested service responsibilities

  -----------------------------------------------------------------------
  Class / layer                       Responsibility
  ----------------------------------- -----------------------------------
  `EmergencyController`               Exposes the assist endpoint and
                                      coordinates the workflow

  `GroqTriageService`                 Calls the configured AI provider
                                      and parses structured guidance

  `OverpassService`                   Queries OpenStreetMap Overpass and
                                      maps hospital results

  Distance utility                    Calculates Haversine distance and
                                      supports result sorting
  -----------------------------------------------------------------------

## 🔐 Configuration & Secrets

Keep credentials in backend environment variables or a local, ignored
configuration file.

Example `.env.example`:

``` env
GROQ_API_KEY=replace_me
GROQ_MODEL=replace_with_a_current_supported_model
SERVER_PORT=8080
```

-   Never commit real API keys, passwords, tokens, or private
    credentials.
-   Add `.env` and other local secret files to `.gitignore`.
-   Do not use a `VITE_`-prefixed variable for a secret API key; Vite
    exposes such variables to browser code.
-   Validate incoming coordinates, radius values, and description length
    on the backend.
-   Configure CORS for the frontend origin used in development and
    production.

## 🧪 Testing Checklist

Use this as a living checklist while implementing the project:

-   [ ] Landing page loads and navigation to `/assist` works.
-   [ ] The triage page handles geolocation permission being granted.
-   [ ] The page explains what to do if geolocation is unavailable or
    denied.
-   [ ] Empty descriptions are rejected by the frontend and backend.
-   [ ] Invalid coordinates and unreasonable search radii are rejected.
-   [ ] AI provider errors are handled without exposing secrets.
-   [ ] Malformed or unexpected AI output is handled safely.
-   [ ] Overpass timeouts or unavailable responses are handled
    gracefully.
-   [ ] An empty hospital result is displayed clearly.
-   [ ] Hospital results are sorted by calculated distance.
-   [ ] Selecting a hospital focuses the correct map marker.
-   [ ] The directions URL uses the selected hospital's coordinates.
-   [ ] The layout remains usable on mobile and desktop.
-   [ ] Medical safety messaging remains visible and understandable.

## 🛡️ Safety, Privacy & Limitations

Because the application handles potentially sensitive medical context
and location information, safety and privacy are core requirements.

### Medical safety

-   The application must not claim to diagnose a disease or condition.
-   AI output must be presented as general information, not professional
    medical advice.
-   The urgency label is an AI-generated aid and must not be treated as
    a clinically validated decision.
-   The interface must clearly advise users to contact local emergency
    services when an emergency may be occurring.
-   Guidance should be reviewed for safety, clarity, and appropriateness
    before public deployment.
-   The app must never imply that a `LOW` label guarantees that a
    condition is harmless.

### Location and data privacy

-   Request geolocation only when needed and explain why it is required.
-   Do not collect or retain location or medical descriptions beyond
    what the feature needs.
-   Avoid logging raw symptom descriptions, exact coordinates, or API
    credentials.
-   Use HTTPS in deployed environments.
-   Document what data is sent to external AI and map services.

### Data and service limitations

-   OpenStreetMap data may be incomplete, outdated, or inconsistently
    tagged.
-   Overpass API availability and rate limits can affect search
    reliability.
-   Straight-line distance does not represent actual travel time or road
    distance.
-   Google Maps directions are an external navigation hand-off, not a
    route calculation performed by this application.
-   AI responses can be inaccurate, incomplete, or inconsistent.

## 🗺️ Roadmap

The following phases describe the intended development sequence. Update
the checkboxes as work is completed.

-   [ ] **Phase 1 --- Project foundation**
    -   [ ] Create frontend and backend projects.
    -   [ ] Configure local environment variables and CORS.
    -   [ ] Establish the initial folder structure.
-   [ ] **Phase 2 --- AI triage integration**
    -   [ ] Implement the AI provider client.
    -   [ ] Define and validate structured triage output.
    -   [ ] Add error handling and safety disclaimers.
-   [ ] **Phase 3 --- Nearby hospital service**
    -   [ ] Integrate the Overpass API.
    -   [ ] Parse hospital features and available metadata.
    -   [ ] Implement Haversine distance calculation and sorting.
-   [ ] **Phase 4 --- Frontend experience**
    -   [ ] Build the landing page.
    -   [ ] Build the triage workspace.
    -   [ ] Add browser geolocation handling.
    -   [ ] Add the interactive map and hospital selection.
-   [ ] **Phase 5 --- Integration and quality**
    -   [ ] Connect the frontend to the combined backend endpoint.
    -   [ ] Test success, empty-result, and failure scenarios.
    -   [ ] Improve responsive layout and accessibility.
-   [ ] **Phase 6 --- Deployment readiness**
    -   [ ] Configure production environment variables.
    -   [ ] Enable HTTPS and production CORS settings.
    -   [ ] Review privacy, security, and medical safety messaging.
    -   [ ] Deploy and document the live application.

## 🔮 Possible Future Enhancements

These are ideas for later consideration, not commitments for the initial
release:

-   More robust hospital filtering and facility metadata.
-   Route distance and travel-time estimates using a suitable routing
    service.
-   Additional map filters and improved accessibility.
-   Carefully reviewed multilingual interface and guidance.
-   Optional user accounts or saved preferences, only with a clear
    privacy design.
-   Automated tests, monitoring, and deployment pipelines.

## 🤝 Contributing

Contributions, suggestions, and issue reports are welcome.

1.  Fork the repository.
2.  Create a feature branch: `git checkout -b feature/your-feature`.
3.  Make focused changes and add or update tests.
4.  Run the relevant frontend and backend checks.
5.  Commit your changes with a clear message.
6.  Open a pull request describing the change and any relevant
    screenshots.

For significant changes, open an issue first to discuss the proposal.

## 📄 License

No license has been selected for this repository yet. Add a `LICENSE`
file before describing the project as open source or granting reuse
permissions.

## 🙌 Acknowledgements

-   [OpenStreetMap](https://www.openstreetmap.org/) contributors for map
    data.
-   [Overpass API](https://overpass-api.de/) for querying OpenStreetMap
    data.
-   [Leaflet](https://leafletjs.com/) and [React
    Leaflet](https://react-leaflet.js.org/) for interactive mapping.
-   [Spring Boot](https://spring.io/projects/spring-boot) for the
    backend framework.
-   [Groq](https://groq.com/) for the AI inference API.

------------------------------------------------------------------------

::: {align="center"}
**Built as a learning-focused full-stack project.**

If you find the project useful, consider giving the repository a ⭐.
:::
