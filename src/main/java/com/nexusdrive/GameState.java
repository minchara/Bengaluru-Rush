package com.nexusdrive;

/**
 * Represents every high-level state the game application can be in.
 * The main GameLoop / SceneManager switches rendering and input handling
 * based on the current GameState.
 */
public enum GameState {
    /** Title screen / splash — first state shown on launch. */
    MAIN_MENU,

    /** Player is choosing a city location to race in. */
    LOCATION_SELECT,

    /** Player is choosing a game mode (Endless, Time Trial, etc.). */
    MODE_SELECT,

    /** Garage / car customisation screen. */
    GARAGE,

    /** Active racing — the main gameplay loop is running. */
    PLAYING,

    /** Game is paused; pause overlay is visible. */
    PAUSED,

    /** Bullet-time rewind mechanic is active. */
    REWIND,

    /** Player has crashed or run out of time; game-over screen. */
    GAME_OVER,

    /** Achievements / trophy screen. */
    ACHIEVEMENTS,

    /** Audio / graphics settings screen. */
    SETTINGS,

    /** Asset-loading splash shown between heavy transitions. */
    LOADING
}
