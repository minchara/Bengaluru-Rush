# NEXUS DRIVE – BENGALURU RUSH 🏎️💨

> **A futuristic Java arcade car racing game set in the bustling streets, highways, and iconic landmarks of Bengaluru, India.**

Built using **Java 25**, **JavaFX 21**, **Maven**, and **Object-Oriented Architecture**, delivering classic pseudo-3D Outrun/Road Rash-style road projection and detailed pixel-art visuals.

---

## 🌆 Bengaluru Features

### 📍 6 Authentic Bengaluru Locations
1. **MG Road (ಎಂ.ಜಿ. ರಸ್ತೆ)**: Urban commercial district, shopping storefronts, streetlights, and overhead Purple Line Namma Metro viaduct.
2. **Silk Board Junction (ಸಿಲ್ಕ್ ಬೋರ್ಡ್)**: The legendary traffic bottleneck — multi-lane chaos, heavy vehicle density, and frequent lane-cutting autos.
3. **Outer Ring Road (ಔಟರ್ ರಿಂಗ್ ರಸ್ತೆ)**: Tech park corridors, glass facade IT campuses, wide asphalt, and high-speed commuters.
4. **Vidhana Soudha (ವಿಧಾನ ಸೌಧ)**: Night racing past illuminated Dravidian architecture, grand government arches, and glowing boulevards.
5. **NICE Road (ನೈಸ್ ರಸ್ತೆ)**: Expansive expressway with long straights, sweeping highway curves, and high top-speed runs.
6. **Lalbagh & Cubbon Park (ಲಾಲ್ ಬಾಗ್)**: Scenic green tree canopy, park gardens, and heritage greenery.

### 🛺 Authentic Bengaluru Traffic AI
- **BMTC Buses**: Red/cream liveries, wide turning radius, stops at bus bays.
- **Auto-Rickshaws**: Iconic yellow-black bodies with realistic sudden lane-weaving maneuvers.
- **Delivery Bikes & Motorcycles**: Rapid couriers weaving between lanes.
- **Namma Metro Trains**: Purple & silver trains roaring overhead on metro routes.
- **Ola/Uber Cabs, SUVs, and Heavy Trucks**.
- **Local Traffic Signals**: Working red/green signal cycles with stopping AI.
- **Karnataka Number Plates**: KA-format plates rendered under vehicles.

### ⚡ Game Mechanics
- **Time Rewind (Press `R`)**: Rewind up to 5 seconds of gameplay to undo crashes using energy.
- **Nitro Boost (Press `SPACE`)**: High-speed acceleration with dynamic dual-color particle exhaust trails.
- **Monsoon Weather**: Bengaluru rain storms with 500 falling rain streaks, wet reflective asphalt, and slicker road physics.
- **Near-Miss Rewards & Combos**: Narrowly dodge traffic at speed to trigger `"CLOSE CALL!"` flashes and score multiplier combos up to 10×.
- **Persistent Garage & Upgrades**: 5 unlockable cyberpunk/street cars with customizable colors and 4 upgradeable stats (Engine, Brakes, Handling, Nitro). Progress is saved automatically to JSON.

---

## 🎮 Controls

| Key | Action |
|---|---|
| **W** / **UP ARROW** | Accelerate |
| **S** / **DOWN ARROW** | Brake / Reverse |
| **A** / **LEFT ARROW** | Steer Left |
| **D** / **RIGHT ARROW** | Steer Right |
| **SPACE** | Nitro Boost |
| **R** | Time Rewind (Hold) |
| **ESC** | Pause / Back |

---

## 🚗 Car Garage

1. **Bengaluru Street Racer**: Tuned agile urban hatchback (Unlocked by default).
2. **Neon Phantom**: Aerodynamic street machine with high handling (1,500 coins).
3. **Turbo GT**: Raw power tuned for Outer Ring Road sprints (3,000 coins).
4. **Cyber Beast**: Heavy torque powerhouse built for Silk Board survival (6,000 coins).
5. **Thunder X**: Gold-plated futuristic hypercar with blistering top speed (10,000 coins).

---

## 🏆 Game Modes

1. **Bengaluru Endless Rush**: Infinite traffic survival across the city.
2. **Silk Board Challenge**: High-density traffic gridlock escape.
3. **MG Road Night Race**: Fast neon street race under the Namma Metro tracks.
4. **NICE Road Highway**: High-speed long-distance expressway dash.
5. **Monsoon Survival**: Rain storm challenge with slippery road handling.
6. **Time Trial**: Beat the clock to reach target checkpoints.
7. **Landmark Challenge**: Heritage sprint across Bengaluru landmarks.

---

## 🚀 How to Run

### Requirements
- **JDK 21+** (Tested and verified on JDK 25).
- No external Maven or Gradle installation needed — Maven Wrapper is pre-configured.

### Quick Start
Double-click:
```cmd
run.bat
```

Or from terminal / PowerShell:
```cmd
set JAVA_HOME=C:\Program Files\Java\jdk-25
.\mvnw.cmd javafx:run
```

To compile a standalone package:
```cmd
.\mvnw.cmd clean package
```
The compiled JAR will be output to `target/nexus-drive-1.0-SNAPSHOT.jar`.
