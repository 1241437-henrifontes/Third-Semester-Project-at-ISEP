# Railway Freight Management System

An integrated railway freight logistics and warehouse management system developed as a **3rd-semester project** at **ISEP** for the course LAPR3, spanning Java application development, Oracle database design, and embedded C/Assembly programming.

## Overview

This system simulates a European railway freight transport network (focused on Portugal/Belgium), combining warehouse inventory management, train scheduling, route optimization, and network analysis. It integrates three disciplines into a single cohesive application:

- **Java** — Main application with layered architecture
- **Oracle SQL** — Database schema, queries, and PL/SQL functions
- **C & RISC-V Assembly** — Embedded railway controller running on a simulated RISC-V processor

## Features

### Warehouse Management
- Item, SKU, and box inventory tracking
- Customer order management with priority-based allocation
- Picking route optimization with trolley weight capacity constraints
- Returns processing with quarantine, restock, and discard workflows (FIFO/FEFO)

### Railway Operations
- Train assembly from locomotives and compatible wagons
- Route planning and travel time estimation with a power-to-weight ratio
- Train scheduling with crossing detection on single-track segments

### Network Analysis
- **Graph algorithms** — Custom implementation of BFS, DFS, Dijkstra, Bellman-Ford, Prim's MST, and Edmonds-Karp max flow
- **Spatial indexing** — KD-Tree for efficient station proximity and range searches
- **Station analytics** — Betweenness centrality, harmonic closeness, degree/strength distribution, hub scores
- **Risk-aware pathfinding** — Negative cycle detection for hazardous material transport
- **Max flow analysis** — Railway network capacity using Edmonds-Karp algorithm

### Embedded System (ARQCP)
- RISC-V assembly routines for sensor data processing
- Display board and light signal control
- Train departure ordering and assignment logic
- Cross-compiled with `riscv32-buildroot-linux-gnu-gcc` and emulated via QEMU

## Tech Stack

|    Category    |                    Technology                    |
|:--------------:|:------------------------------------------------:|
|  **Language**  |        Java 25 (preview features enabled)        |
|   **Build**    |                   Apache Maven                   |
|  **Database**  |        Oracle Database (JDBC with ojdbc8)        |
|  **Testing**   |  JUnit 5, Mockito, JaCoCo, PIT mutation testing  |
|     **CI**     |                  GitHub Actions                  |
|  **Embedded**  |        C, RISC-V Assembly, QEMU emulation        |
|    **Data**    |      CSV datasets (560+ European stations)       |

## Architecture

The Java application follows a **layered architecture**:

```
UI (Console Menus) → Controllers → Services → Repositories → CSV / Oracle DB
                                     ↓
                               Domain Model (Graphs, Trees, Entities)
```

- **Singleton repositories** load and cache data at bootstrap from CSV files and Oracle
- **Custom graph library** (`MapGraph`, `MatrixGraph`) implements all algorithms from scratch
- **AVL/BST trees** for timezone-based station indexing
- **KD-Tree** for spatial indexing of geographic coordinates

## Project Structure

```
├── src/main/java/          # Java application
│   ├── Main.java           # Entry point
│   ├── Bootstrap.java      # Data initialization
│   ├── Model/              # Domain entities, graphs, trees
│   ├── Controllers/        # Application controllers
│   ├── Services/           # Business logic
│   ├── Repositories/       # Data access layer
│   └── UI/                 # Console menu system
├── src/test/java/          # Unit tests (per user story)
├── database/               # Oracle SQL schema and seed data
├── ARQCP/                  # C and RISC-V Assembly module
├── train_station_dataset/  # CSV data files
├── documentation/          # UML diagrams and reports
└── Scrum/                  # Sprint reports and burndown charts
```

## How to Run

### Prerequisites
- JDK 25 with `--enable-preview`
- Apache Maven 3.x
- Oracle Database (optional, for full DB integration)

### Build & Test
```bash
mvn compile                  # Compile
mvn test                     # Run tests
mvn verify                   # Run tests with JaCoCo coverage
mvn package                  # Package JAR
```

### Run
```bash
mvn exec:java -Dexec.mainClass="Main"
```

### Embedded Module (ARQCP)
```bash
cd ARQCP/sprint3
make                         # Cross-compile for RISC-V
make run                     # Run under QEMU
```

## Testing

- Unit tests cover all 15 user stories
- JaCoCo generates code coverage reports at `target/site/jacoco/`
- PIT mutation testing available via `mvn org.pitest:pitest-maven:mutationCoverage`

## Academic Context

|                    |                                                                                    |
|:------------------:|:----------------------------------------------------------------------------------:|
|     **Course**     |                      Integrative Project — 3rd Semester (PI)                       |
|  **Institution**   |                  ISEP — Instituto Superior de Engenharia do Porto                  |
|     **Degree**     |                           B.Sc. in Computer Engineering                            |
| **Academic Year**  |                                     2025/2026                                      |
|      **Team**      | Henri Fontes, Miguel Ribeiro, Rodrigo Barbosa, Rodrigo Guimarães and Mariana Sousa |
