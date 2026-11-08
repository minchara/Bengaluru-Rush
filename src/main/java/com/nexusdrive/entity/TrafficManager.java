package com.nexusdrive.entity;

import com.nexusdrive.world.LocationType;
import com.nexusdrive.world.BengaluruMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Manages the lifecycle of all AI traffic vehicles: spawning, updating,
 * culling, traffic-signal logic, and sorted retrieval for renderer use.
 *
 * <p>Vehicle mix is location-aware to reflect authentic Bengaluru traffic
 * patterns (e.g., heavier auto density at Silk Board, more SUVs on NICE Road).
 */
public class TrafficManager {

    private final List<AITraffic> vehicles = new ArrayList<>();
    private LocationType location;
    private BengaluruMap map;
    private final Random rand = new Random();

    // ── Traffic signal ────────────────────────────────────────────────────────
    private double signalTimer = 0;
    private boolean signalGreen = true;
    /** Duration of a single green-or-red phase in seconds. */
    private static final double SIGNAL_CYCLE = 15.0;

    // ── Spawn parameters (set per location) ───────────────────────────────────
    private double spawnInterval;
    private double nextSpawnTime = 0;
    private double spawnDistance;
    private int    maxVehicles;

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Initialises the traffic manager for a new location.
     * Pre-populates vehicles spread ahead of the start line.
     *
     * @param location  the current route location (determines density / lane count)
     * @param map       road map used for signal-zone queries
     */
    public void setup(LocationType location, BengaluruMap map) {
        this.location = location;
        this.map      = map;
        vehicles.clear();

        double density   = location.trafficDensity;
        spawnInterval    = Math.max(0.5, 1.5 - density * 1.0); // 0.5 s – 1.5 s
        spawnDistance    = 25_000.0; // 250 segments ahead
        maxVehicles      = (int) (30 + density * 50);

        // Pre-populate spread ahead of the player
        for (int i = 0; i < 15; i++) {
            spawnVehicle(rand.nextDouble() * spawnDistance, 0);
        }
    }

    /**
     * Advances all traffic, manages signals, culls old vehicles, and spawns
     * new ones.
     *
     * @param playerZ     player's world-z
     * @param playerX     player's lateral position
     * @param playerSpeed player's speed in km/h
     * @param dt          delta-time in seconds
     */
    public void update(double playerZ, double playerX, double playerSpeed, double dt) {

        // ── Signal phase ──────────────────────────────────────────────────────
        signalTimer += dt;
        if (signalTimer > SIGNAL_CYCLE) {
            signalTimer  = 0;
            signalGreen  = !signalGreen;

            if (!signalGreen) {
                // Red: stop vehicles near signal segments
                for (AITraffic v : vehicles) {
                    int segIdx = (int) (v.z / 200.0) % map.getSegmentCount();
                    if (map.getSegment(segIdx).hasSignal) {
                        v.stopAtSignal();
                    }
                }
            } else {
                // Green: resume all waiting vehicles
                for (AITraffic v : vehicles) {
                    v.resumeFromSignal();
                }
            }
        }

        // ── Update each vehicle ───────────────────────────────────────────────
        for (AITraffic v : vehicles) {
            v.update(playerZ, playerX, playerSpeed, dt);
        }

        // ── Cull vehicles that have fallen too far behind the player ──────────
        vehicles.removeIf(v -> v.z < playerZ - 3_000);

        // ── Spawn new vehicles ────────────────────────────────────────────────
        nextSpawnTime -= dt;
        if (nextSpawnTime <= 0 && vehicles.size() < maxVehicles) {
            nextSpawnTime = spawnInterval + rand.nextDouble() * spawnInterval;
            double spawnZ = playerZ + 5_000 + rand.nextDouble() * spawnDistance;
            spawnVehicle(spawnZ, playerZ);
        }
    }

    // ── Internal helpers ──────────────────────────────────────────────────────

    private void spawnVehicle(double z, double playerZ) {
        int         lanes = location.roadLanes;
        int         lane  = rand.nextInt(lanes);
        VehicleType type  = chooseVehicleType();
        vehicles.add(new AITraffic(type, lane, lanes, z));
    }

    /**
     * Selects a vehicle type probabilistically, weighted by the current
     * location's authentic traffic composition.
     */
    private VehicleType chooseVehicleType() {
        double r = rand.nextDouble();
        return switch (location) {
            case SILK_BOARD -> {
                if      (r < 0.25) yield VehicleType.AUTO_RICKSHAW;
                else if (r < 0.40) yield VehicleType.BMTC_BUS;
                else if (r < 0.55) yield VehicleType.TRUCK;
                else if (r < 0.70) yield VehicleType.CAR;
                else if (r < 0.85) yield VehicleType.MOTORCYCLE;
                else                yield VehicleType.DELIVERY_BIKE;
            }
            case MG_ROAD -> {
                if      (r < 0.20) yield VehicleType.AUTO_RICKSHAW;
                else if (r < 0.30) yield VehicleType.BMTC_BUS;
                else if (r < 0.50) yield VehicleType.CAR;
                else if (r < 0.65) yield VehicleType.OLA_TAXI;
                else if (r < 0.80) yield VehicleType.MOTORCYCLE;
                else                yield VehicleType.DELIVERY_BIKE;
            }
            case NICE_ROAD -> {
                if      (r < 0.10) yield VehicleType.AUTO_RICKSHAW;
                else if (r < 0.25) yield VehicleType.TRUCK;
                else if (r < 0.60) yield VehicleType.CAR;
                else if (r < 0.75) yield VehicleType.SUV;
                else                yield VehicleType.MOTORCYCLE;
            }
            case LALBAGH -> {
                if      (r < 0.15) yield VehicleType.AUTO_RICKSHAW;
                else if (r < 0.35) yield VehicleType.MOTORCYCLE;
                else if (r < 0.65) yield VehicleType.CAR;
                else                yield VehicleType.OLA_TAXI;
            }
            default -> {
                // Generic Bengaluru mix
                if      (r < 0.20) yield VehicleType.AUTO_RICKSHAW;
                else if (r < 0.30) yield VehicleType.BMTC_BUS;
                else if (r < 0.50) yield VehicleType.CAR;
                else if (r < 0.60) yield VehicleType.MOTORCYCLE;
                else if (r < 0.70) yield VehicleType.DELIVERY_BIKE;
                else if (r < 0.80) yield VehicleType.OLA_TAXI;
                else if (r < 0.90) yield VehicleType.SUV;
                else                yield VehicleType.TRUCK;
            }
        };
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Returns all vehicles visible ahead of the player, sorted far-to-near
     * (descending z) so the renderer can paint back-to-front correctly.
     *
     * @param playerZ player's current world-z
     * @return sorted list of vehicles within render range
     */
    public List<AITraffic> getVehiclesSortedForRendering(double playerZ) {
        return vehicles.stream()
                .filter(v -> v.z > playerZ - 500)
                .sorted((a, b) -> Double.compare(b.z, a.z))
                .collect(Collectors.toList());
    }

    /** Returns all vehicles (unsorted). */
    public List<AITraffic> getAllVehicles() { return vehicles; }

    /** Whether the current signal phase is green. */
    public boolean isSignalGreen() { return signalGreen; }

    /**
     * Returns the fraction [0, 1] of the current signal phase elapsed.
     * Useful for rendering a countdown indicator on the HUD.
     */
    public double getSignalProgress() { return signalTimer / SIGNAL_CYCLE; }
}
