package net.indivis.voices.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.file.*;
import net.fabricmc.loader.api.FabricLoader;
import net.indivis.voices.IndivisVoices;

final class VoiceOptions {
    boolean enabled = true;
    boolean subtitles = true;
    float volume = 1;
    static final Gson JSON = new GsonBuilder().setPrettyPrinting().create();
    static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("indivis-voices.json");
    static VoiceOptions load() {
        try {
            if (Files.exists(PATH)) {
                var o = JSON.fromJson(Files.readString(PATH), VoiceOptions.class);
                if (o != null) { o.volume = Float.isFinite(o.volume) ? Math.clamp(o.volume, 0, 1) : 1; return o; }
            }
        } catch (Exception e) { IndivisVoices.LOG.warn("Voices: options illisibles, valeurs par défaut", e); }
        return new VoiceOptions();
    }
    void save() {
        try { Files.createDirectories(PATH.getParent()); Files.writeString(PATH, JSON.toJson(this)); }
        catch (java.io.IOException e) { IndivisVoices.LOG.warn("Voices: sauvegarde des options impossible", e); }
    }
}
