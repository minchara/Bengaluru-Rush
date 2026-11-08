package com.nexusdrive;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Central keyboard input handler for NEXUS DRIVE.
 *
 * <h3>Usage</h3>
 * <pre>{@code
 *   InputHandler input = new InputHandler();
 *   input.attachToScene(primaryScene);
 *
 *   // Inside game loop update():
 *   if (input.isAccelerating()) { ... }
 *   input.clearJustPressed();   // must be called once per frame, at the END
 * }</pre>
 *
 * <h3>Key bindings</h3>
 * <ul>
 *   <li>W / UP    — accelerate</li>
 *   <li>S / DOWN  — brake / reverse</li>
 *   <li>A / LEFT  — steer left</li>
 *   <li>D / RIGHT — steer right</li>
 *   <li>SPACE     — nitro burst</li>
 *   <li>R         — bullet-time rewind</li>
 *   <li>ESC / P   — pause toggle</li>
 * </ul>
 */
public class InputHandler {

    // -------------------------------------------------------------------------
    // Internal state
    // -------------------------------------------------------------------------

    /** All keys currently held down. Thread-access is single-threaded (JavaFX thread). */
    private final Set<KeyCode> heldKeys = EnumSet.noneOf(KeyCode.class);

    /**
     * Keys that were pressed for the first time in the current frame.
     * Cleared by {@link #clearJustPressed()} at the end of every update tick.
     */
    private final Set<KeyCode> justPressedKeys = EnumSet.noneOf(KeyCode.class);

    // -------------------------------------------------------------------------
    // Public API — attachment
    // -------------------------------------------------------------------------

    /**
     * Attaches this handler to a JavaFX {@link Scene}.
     * Key-press and key-release events will be routed to this handler.
     * Can be called again to re-attach to a different scene (e.g., after a scene swap).
     *
     * @param scene the JavaFX scene to listen on
     */
    public void attachToScene(Scene scene) {
        scene.addEventHandler(KeyEvent.KEY_PRESSED,  this::handleKeyPress);
        scene.addEventHandler(KeyEvent.KEY_RELEASED, this::handleKeyRelease);
    }

    // -------------------------------------------------------------------------
    // Public API — event handlers (also usable directly)
    // -------------------------------------------------------------------------

    /**
     * Records that a key was pressed.
     * Called automatically when attached via {@link #attachToScene(Scene)}.
     *
     * @param e the JavaFX key event
     */
    public void handleKeyPress(KeyEvent e) {
        KeyCode code = e.getCode();
        if (!heldKeys.contains(code)) {
            justPressedKeys.add(code);
        }
        heldKeys.add(code);
    }

    /**
     * Records that a key was released.
     * Called automatically when attached via {@link #attachToScene(Scene)}.
     *
     * @param e the JavaFX key event
     */
    public void handleKeyRelease(KeyEvent e) {
        heldKeys.remove(e.getCode());
        // Note: justPressedKeys is NOT cleared here — it lives until clearJustPressed().
    }

    /**
     * Clears all "just pressed" flags.
     * <strong>Must be called exactly once per game-loop frame, after all input has been
     * consumed for that frame.</strong>
     */
    public void clearJustPressed() {
        justPressedKeys.clear();
    }

    // -------------------------------------------------------------------------
    // Public API — held-state queries
    // -------------------------------------------------------------------------

    /** @return {@code true} while W or UP-ARROW is held. */
    public boolean isAccelerating() {
        return heldKeys.contains(KeyCode.W) || heldKeys.contains(KeyCode.UP);
    }

    /** @return {@code true} while S or DOWN-ARROW is held. */
    public boolean isBraking() {
        return heldKeys.contains(KeyCode.S) || heldKeys.contains(KeyCode.DOWN);
    }

    /** @return {@code true} while A or LEFT-ARROW is held. */
    public boolean isLeft() {
        return heldKeys.contains(KeyCode.A) || heldKeys.contains(KeyCode.LEFT);
    }

    /** @return {@code true} while D or RIGHT-ARROW is held. */
    public boolean isRight() {
        return heldKeys.contains(KeyCode.D) || heldKeys.contains(KeyCode.RIGHT);
    }

    /** @return {@code true} while SPACE is held (nitro). */
    public boolean isNitro() {
        return heldKeys.contains(KeyCode.SPACE);
    }

    /** @return {@code true} while R is held (rewind mode). */
    public boolean isRewind() {
        return heldKeys.contains(KeyCode.R);
    }

    // -------------------------------------------------------------------------
    // Public API — "just pressed" (single-frame) queries
    // -------------------------------------------------------------------------

    /**
     * @return {@code true} only on the frame ESC or P was first pressed (not held).
     */
    public boolean isPauseJustPressed() {
        return justPressedKeys.contains(KeyCode.ESCAPE)
                || justPressedKeys.contains(KeyCode.P);
    }

    /**
     * @return {@code true} only on the frame SPACE was first pressed.
     */
    public boolean isNitroJustPressed() {
        return justPressedKeys.contains(KeyCode.SPACE);
    }

    /**
     * @return {@code true} only on the frame R was first pressed.
     */
    public boolean isRewindJustPressed() {
        return justPressedKeys.contains(KeyCode.R);
    }

    // -------------------------------------------------------------------------
    // Diagnostic helpers
    // -------------------------------------------------------------------------

    /**
     * Returns an unmodifiable view of all currently held keys.
     * Useful for debugging or building custom key combinations.
     *
     * @return read-only set of held {@link KeyCode}s
     */
    public Set<KeyCode> getHeldKeys() {
        return Collections.unmodifiableSet(heldKeys);
    }

    /**
     * Returns an unmodifiable view of keys pressed this frame only.
     *
     * @return read-only set of just-pressed {@link KeyCode}s
     */
    public Set<KeyCode> getJustPressedKeys() {
        return Collections.unmodifiableSet(justPressedKeys);
    }

    /** Releases all held and just-pressed keys (e.g., after a scene change). */
    public void clearAll() {
        heldKeys.clear();
        justPressedKeys.clear();
    }
}
