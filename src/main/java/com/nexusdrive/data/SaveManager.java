package com.nexusdrive.data;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;

/**
 * Thin persistence layer that saves and loads a {@link PlayerProfile} as a
 * pretty-printed JSON file on disk.
 *
 * <h3>Save location</h3>
 * <pre>
 *   {@literal <user.home>}/.nexusdrive/save.json
 * </pre>
 * On Windows this typically resolves to:
 * <pre>
 *   C:\Users\&lt;username&gt;\.nexusdrive\save.json
 * </pre>
 *
 * <h3>Error policy</h3>
 * All {@link IOException}s are caught and printed to {@code stderr};
 * the game is never crashed by a save failure.  On load failure a default
 * {@code PlayerProfile} is returned so the game can always start.
 */
public final class SaveManager {

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    private static final String SAVE_DIR  =
            System.getProperty("user.home") + File.separator + ".nexusdrive";

    private static final String SAVE_FILE = SAVE_DIR + File.separator + "save.json";

    // -------------------------------------------------------------------------
    // Jackson ObjectMapper — shared, thread-safe singleton
    // -------------------------------------------------------------------------

    private static final ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper();

        // Write null-valued properties so a full round-trip always preserves
        // fields that are explicitly null (e.g., an empty list stays []).
        MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,  false);
        MAPPER.configure(SerializationFeature.INDENT_OUTPUT,         true);

        // Tolerate extra fields added by future game versions without crashing.
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Deserialise unknown enum values as null instead of throwing.
        MAPPER.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
    }

    // -------------------------------------------------------------------------
    // Private constructor — utility class
    // -------------------------------------------------------------------------

    private SaveManager() {
        throw new UnsupportedOperationException("SaveManager is a static utility class.");
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Loads the player profile from disk.
     *
     * <ul>
     *   <li>Returns a fresh {@link PlayerProfile} (with defaults) if the save
     *       file does not yet exist.</li>
     *   <li>Returns a fresh profile and prints the exception if the file is
     *       corrupt or unreadable.</li>
     * </ul>
     *
     * @return the saved {@link PlayerProfile}, or a new one if none exists
     */
    public static PlayerProfile load() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            System.out.println("[SaveManager] No save file found — starting fresh.");
            return new PlayerProfile();
        }

        try {
            PlayerProfile profile = MAPPER.readValue(file, PlayerProfile.class);
            System.out.println("[SaveManager] Profile loaded for: " + profile.getPlayerName());
            return profile;
        } catch (IOException e) {
            System.err.println("[SaveManager] Failed to load save file — starting fresh.");
            e.printStackTrace();
            return new PlayerProfile();
        }
    }

    /**
     * Writes the player profile to disk as pretty-printed JSON.
     * Creates the {@code .nexusdrive} directory if it does not exist.
     *
     * @param profile the profile to persist; must not be {@code null}
     */
    public static void save(PlayerProfile profile) {
        if (profile == null) {
            System.err.println("[SaveManager] Cannot save a null profile — ignoring.");
            return;
        }

        // Ensure the save directory exists.
        File dir = new File(SAVE_DIR);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                System.err.println("[SaveManager] Could not create save directory: " + SAVE_DIR);
                return;
            }
        }

        // Write JSON.
        try {
            File file = new File(SAVE_FILE);
            MAPPER.writeValue(file, profile);
            System.out.println("[SaveManager] Profile saved → " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("[SaveManager] Failed to write save file.");
            e.printStackTrace();
        }
    }

    /**
     * Deletes the save file (used for a "reset progress" option in Settings).
     *
     * @return {@code true} if the file was deleted, {@code false} if it did
     *         not exist or could not be deleted
     */
    public static boolean deleteSave() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) return false;
        boolean deleted = file.delete();
        if (deleted) {
            System.out.println("[SaveManager] Save file deleted.");
        } else {
            System.err.println("[SaveManager] Could not delete save file: " + SAVE_FILE);
        }
        return deleted;
    }

    /**
     * @return the absolute path to the save file (useful for displaying in Settings)
     */
    public static String getSaveFilePath() {
        return SAVE_FILE;
    }
}
