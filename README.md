# ⚓ Battleship — Real-Time Multiplayer Web Game

A multi-user Battleship game where a human player battles a computer opponent driven by configurable firing strategies. Any number of browsers can watch and play the same battles at once, and every change is pushed to all of them in real time — no page reloads.

Built with **Java 21** and **Spring Boot**, following an **MVP (Passive View)** architecture and classic object-oriented design patterns.

🔗 **Live demo:** https://batallanaval-fjgv.onrender.com/

> Hosted on Render's free tier: if the app has been idle for 15 minutes, the first request takes about a minute to wake it up.

> The user interface is in Spanish.

---

## Features

- **Operations room (dashboard)** — wins, losses, campaign success rate, and a live list of every battle with its status and the remaining integrity of both fleets.
- **Battle setup** — choose the location, the opponent's strategy, the ocean size (6–12 cells per side) and the fleet composition, with full input validation.
- **Fleet deployment** — place each ship horizontally or vertically; the domain enforces the rules (stay inside the board, no overlapping, no adjacent ships — not even diagonally).
- **Combat** — atomic rounds (human shot → computer response), a chronological list of sunk enemy ships, and the remaining enemy fleet revealed on defeat.
- **Real-time sync** — every action is instantly reflected in all other browsers viewing the same battle or the dashboard.

### Computer strategies

| Strategy | Behavior |
|---|---|
| **Sequential** | Sweeps the board left to right, bottom to top. |
| **Spiral** | Starts at a central cell and fires along an expanding spiral. |
| **Hunt & Target** | Fires randomly until it scores a hit, then attacks neighboring cells — following the ship's orientation once known — until it sinks it. |

---

## Tech Stack

| Layer | Technologies |
|---|---|
| Backend | Java 21, Spring Boot, Maven |
| Frontend | HTML, CSS, vanilla JavaScript (ES modules) |
| Real-time | Server-Sent Events (SSE) |
| Deployment | Docker, Render |

---

## Architecture

The application follows **Model–View–Presenter (Passive View)**. Views only render what they receive and forward user events; presenters translate those events into calls to the domain model and send back a list of UI commands. All business rules live in the model, which has no dependency on Spring or the web layer.

```
Browser (View)  ──HTTP──▶  Presenter  ──▶  Facade  ──▶  Domain model
      ▲                        │                            │
      └──────── SSE ◀──────────┴──────── Observer ◀─────────┘
```

### Design patterns and principles

| Pattern / principle | Implementation |
|---|---|
| **Facade** (Singleton) | `Fachada` is the single entry point from the presentation layer into the domain. |
| **State** | `EstadoBatalla` → `EnPreparacion`, `EnCombate`, `Ganada`, `Perdida`. Each state decides which actions a battle accepts, so the `Batalla` class contains no state conditionals. |
| **Strategy** | `Tactica` → `Secuencial`, `Espiral`, `CazaYAtaque`. Adding a new strategy only requires a new subclass — no changes to existing code (Open/Closed). |
| **Observer** | `Batalla` and `Fachada` are observable; presenters subscribe and push updates to the browser over SSE. |
| **Template Method** | `PresentadorConBatalla` holds the shared SSE and subscription logic; subclasses define how to react to changes. |
| **Information Expert** | Each class owns its own rules: `Oceano` validates placement and shots, `Barco` knows when it is sunk, `Batalla` validates its configuration and runs rounds. |

### Key engineering decisions

- **Concurrency:** battle operations are `synchronized` per battle, so each round executes atomically — if two browsers fire simultaneously, the first request completes the full round before the second is processed.
- **Stateless strategies:** the Hunt & Target mode is derived from the board state (unsunk hits) rather than stored, so a single strategy instance safely serves every battle.
- **Error handling:** business rule violations throw a domain exception that a global handler maps to a user-facing message, keeping presenters free of validation logic.
- **No database by design:** state is held in memory and seeded on startup with sample battles in every state, keeping the demo self-contained.

---

## Project Structure

```
batallanaval/
├── src/main/java/uy/edu/ort/batallanaval/
│   ├── modelo/          # Domain: Batalla, Oceano, Celda, Barco, TipoBarco
│   │   ├── estados/     # State pattern: battle lifecycle
│   │   └── tacticas/    # Strategy pattern: computer firing strategies
│   ├── servicios/       # In-memory repositories
│   ├── fachada/         # Facade (Singleton + Observable)
│   ├── presenters/      # REST controllers acting as presenters
│   ├── dtos/            # Data sent to the views
│   ├── observador/      # Observer infrastructure
│   ├── excepciones/     # Domain exception and global handler
│   └── utils/           # SSE connection and seed data
└── src/main/resources/static/
    ├── index.html        # Operations room
    ├── nuevaBatalla.html # Battle setup
    ├── despliegue.html   # Fleet deployment
    └── batalla.html      # Combat
```

---

## Getting Started

**Prerequisite:** Java 21.

```bash
cd batallanaval
./mvnw spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080). Ship types, strategies and one sample battle per state are loaded on startup.

To see real-time sync in action, open the same battle in a regular window and in an incognito window.

### Run with Docker

```bash
cd batallanaval
docker build -t battleship .
docker run -p 8080:8080 battleship
```

