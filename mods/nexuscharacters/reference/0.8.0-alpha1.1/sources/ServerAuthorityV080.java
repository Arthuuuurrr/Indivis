package net.tompsen.nexuscharacters;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Dedicated-server authority gate introduced for Haute Capitale 0.8.0.
 *
 * alpha1.1 fixes legacy/dynamic preset compatibility: skinUsername is not always
 * a Mojang username. Haute Capitale uses this field for preset markers, and the
 * v69 marker can legitimately exceed 64 characters. Dedicated authority remains
 * bounded and server-canonical, but uses the actual schema size instead of the
 * incorrect username-only assumption.
 */
public final class ServerAuthorityV080 {
    private static final String CLAIM = ".hc_character_v080.owner";
    private static final String OWNER_0710 = ".hc_persistence_v0710.owner";
    private static final String OWNER_0700 = ".hc_persistence_v0700.owner";
    private static final String MIGRATION_DIR = "server_migration_v080";
    private static final int MAX_NAME = 48;
    private static final int MAX_SKIN_VALUE = 32768;
    private static final int MAX_SKIN_SIGNATURE = 16384;
    private static final int MAX_SKIN_USERNAME = 256;
    private static final int MAX_STYLE = 64;
    private static final ThreadLocal<String> LAST_REJECTION = new ThreadLocal<>();

    private ServerAuthorityV080() {}

    public static synchronized CharacterDto resolveCanonical(UUID owner, CharacterDto incoming) {
        LAST_REJECTION.remove();
        try {
            return resolveCanonicalStrict(owner, incoming);
        } catch (Throwable t) {
            String reason = rejectionReason(t);
            LAST_REJECTION.set(reason);
            System.err.println("[Nexus][HC 0.8.0-alpha1.1] dedicated selection rejected: " + t);
            return null;
        }
    }

    public static String consumeRejectionReason() {
        String reason = LAST_REJECTION.get();
        LAST_REJECTION.remove();
        return reason == null || reason.isBlank() ? "validation serveur du personnage impossible" : reason;
    }

    public static String consumeRejectionDisconnectMessage() {
        return "NexusCharacters: personnage refusé par l’autorité serveur — " + consumeRejectionReason();
    }

    private static String rejectionReason(Throwable t) {
        String s = t == null ? null : t.getMessage();
        if (s == null || s.isBlank()) s = t == null ? "erreur inconnue" : t.getClass().getSimpleName();
        s = s.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ').trim();
        if (s.length() > 180) s = s.substring(0, 180) + "…";
        return s;
    }

    private static CharacterDto resolveCanonicalStrict(UUID owner, CharacterDto incoming) throws IOException {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(incoming, "incoming character");
        Objects.requireNonNull(incoming.id(), "character id");

        Path vault = VaultManager.getVaultDir(incoming.id());
        UUID persistenceOwner = readPersistenceOwner(vault);
        if (persistenceOwner != null && !owner.equals(persistenceOwner)) {
            throw new SecurityException("character vault belongs to another account");
        }
        claimOwner(vault, owner);

        Optional<CharacterDto> known = NexusCharacters.DATA_FILE_MANAGER.findById(incoming.id());
        if (known.isPresent()) {
            CharacterDto canonical = sanitize(known.get());
            if (!canonical.equals(known.get())) {
                NexusCharacters.DATA_FILE_MANAGER.updateCharacter(canonical);
            }
            return canonical;
        }

        CharacterDto canonical = sanitize(incoming);
        NexusCharacters.DATA_FILE_MANAGER.addCharacter(canonical);
        System.out.println("[Nexus][HC 0.8.0-alpha1.1] registered server-canonical character "
                + canonical.id() + " for account " + owner + ".");
        return canonical;
    }

    private static CharacterDto sanitize(CharacterDto in) {
        String name = bounded("name", in.name(), MAX_NAME, false).trim();
        if (name.isBlank()) throw new IllegalArgumentException("blank character name");
        for (int i = 0; i < name.length(); i++) {
            if (Character.isISOControl(name.charAt(i))) throw new IllegalArgumentException("control character in name");
        }

        CharacterRace race = CharacterRace.fromId(in.race());
        float h = race.clampHeight(finiteOr(in.heightScale(), 1.0f));
        float b = race.clampBuild(finiteOr(in.buildScale(), 1.0f));

        String skinValue = bounded("skinValue", in.skinValue(), MAX_SKIN_VALUE, true);
        String skinSignature = bounded("skinSignature", in.skinSignature(), MAX_SKIN_SIGNATURE, true);
        String skinUsername = bounded("skinUsername", in.skinUsername(), MAX_SKIN_USERNAME, true);
        String ear = bounded("earStyle", in.earStyle(), MAX_STYLE, true);
        String beard = bounded("beardStyle", in.beardStyle(), MAX_STYLE, true);

        return new CharacterDto(in.id(), name, skinValue, skinSignature, skinUsername,
                2, false, race.id(), h, b, ear, beard);
    }

    private static float finiteOr(float v, float fallback) {
        return Float.isFinite(v) ? v : fallback;
    }

    private static String bounded(String field, String s, int max, boolean nullable) {
        if (s == null) {
            if (nullable) return "";
            throw new IllegalArgumentException(field + " is null");
        }
        if (s.length() > max) throw new IllegalArgumentException(field + " exceeds " + max + " chars");
        return s;
    }

    public static synchronized boolean prepareServerVault(UUID owner, CharacterDto character, Path worldRoot) {
        try {
            prepareServerVaultStrict(owner, character, worldRoot);
            return true;
        } catch (Throwable t) {
            System.err.println("[Nexus][HC 0.8.0-alpha1.1] server vault preparation failed for "
                    + (character == null ? "<null>" : character.id()) + ": " + t);
            t.printStackTrace(System.err);
            return false;
        }
    }

    private static void prepareServerVaultStrict(UUID owner, CharacterDto character, Path worldRoot) throws IOException {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(character, "character");
        Objects.requireNonNull(worldRoot, "worldRoot");
        Path world = worldRoot.toAbsolutePath().normalize();
        Path vault = VaultManager.getVaultDir(character.id()).toAbsolutePath().normalize();

        UUID persistenceOwner = readPersistenceOwner(vault);
        if (persistenceOwner != null && !owner.equals(persistenceOwner)) {
            throw new SecurityException("character vault owner mismatch");
        }
        claimOwner(vault, owner);

        if (PersistenceIoV0619.isServerAuthoritative(character.id())) return;

        VaultManager.createVault(character.id());
        boolean legacy = shouldMigrateLegacy(owner, character.id(), world, vault.getParent());
        if (legacy) {
            VaultManager.copyWorldToVault(character.id(), world, owner);
            markMigrated(owner, vault.getParent());
            System.out.println("[Nexus][HC 0.8.0-alpha1.1] migrated existing server state into first character "
                    + character.id() + " for account " + owner + ".");
        } else {
            createEmptyAuthoritativeVault(vault, owner);
            markMigrated(owner, vault.getParent());
            System.out.println("[Nexus][HC 0.8.0-alpha1.1] created empty server-authoritative vault for character "
                    + character.id() + " account " + owner + ".");
        }

        if (!PersistenceIoV0619.isServerAuthoritative(character.id())) {
            throw new IOException("new server vault did not become authoritative");
        }
    }

    private static void createEmptyAuthoritativeVault(Path vault, UUID owner) throws IOException {
        Path nexusDir = vault.getParent() == null ? Path.of("nexuscharacters") : vault.getParent().getParent();
        if (nexusDir == null) nexusDir = Path.of("nexuscharacters");
        Path stagingRoot = nexusDir.resolve("server_empty_stage_v080").resolve(owner + "-" + UUID.randomUUID());
        Files.createDirectories(stagingRoot);
        try {
            PersistenceEngineV0710.copyWorldToVault(vault, stagingRoot, owner, null);
        } finally {
            deleteTreeQuietly(stagingRoot);
        }
    }

    private static void deleteTreeQuietly(Path root) {
        if (root == null || !Files.exists(root)) return;
        try (var paths = Files.walk(root)) {
            paths.sorted(Comparator.reverseOrder()).forEach(p -> {
                try { Files.deleteIfExists(p); } catch (IOException ignored) {}
            });
        } catch (IOException ignored) {}
    }

    private static boolean shouldMigrateLegacy(UUID owner, UUID currentCharacter, Path world, Path vaultsDir) throws IOException {
        Path marker = migrationMarker(owner, vaultsDir);
        if (Files.isRegularFile(marker)) return false;
        if (hasOtherOwnedVault(owner, currentCharacter, vaultsDir)) return false;
        Path playerDat = world.resolve("playerdata").resolve(owner + ".dat");
        return Files.isRegularFile(playerDat) && Files.size(playerDat) > 16L;
    }

    private static boolean hasOtherOwnedVault(UUID owner, UUID currentCharacter, Path vaultsDir) throws IOException {
        if (vaultsDir == null || !Files.isDirectory(vaultsDir)) return false;
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(vaultsDir)) {
            for (Path p : ds) {
                if (!Files.isDirectory(p)) continue;
                if (p.getFileName().toString().equals(currentCharacter.toString())) continue;
                UUID o = readPersistenceOwner(p);
                if (owner.equals(o)) return true;
            }
        }
        return false;
    }

    private static void markMigrated(UUID owner, Path vaultsDir) throws IOException {
        Path marker = migrationMarker(owner, vaultsDir);
        Files.createDirectories(marker.getParent());
        if (!Files.exists(marker)) {
            try {
                Files.writeString(marker, "NEXUS_HC_080_SERVER_MIGRATED\n" + owner + "\n",
                        StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
            } catch (FileAlreadyExistsException ignored) {}
        }
    }

    private static Path migrationMarker(UUID owner, Path vaultsDir) {
        Path nexusDir = vaultsDir == null ? Path.of("nexuscharacters") : vaultsDir.getParent();
        if (nexusDir == null) nexusDir = Path.of("nexuscharacters");
        return nexusDir.resolve(MIGRATION_DIR).resolve(owner + ".done");
    }

    private static void claimOwner(Path vault, UUID owner) throws IOException {
        Files.createDirectories(vault);
        Path claim = vault.resolve(CLAIM);
        if (Files.exists(claim)) {
            UUID old = UUID.fromString(Files.readString(claim, StandardCharsets.UTF_8).trim());
            if (!owner.equals(old)) throw new SecurityException("character registry owner mismatch");
            return;
        }
        try {
            Files.writeString(claim, owner.toString() + "\n", StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
        } catch (FileAlreadyExistsException race) {
            UUID old = UUID.fromString(Files.readString(claim, StandardCharsets.UTF_8).trim());
            if (!owner.equals(old)) throw new SecurityException("character registry owner mismatch");
        }
    }

    private static UUID readPersistenceOwner(Path vault) throws IOException {
        UUID v = readOwnerFile(vault.resolve(OWNER_0710));
        if (v != null) return v;
        return readOwnerFile(vault.resolve(OWNER_0700));
    }

    private static UUID readOwnerFile(Path p) throws IOException {
        if (!Files.isRegularFile(p)) return null;
        if (Files.size(p) > 4096L) throw new IOException("oversized owner metadata " + p);
        for (String line : Files.readAllLines(p, StandardCharsets.UTF_8)) {
            if (line.startsWith("UUID\t")) return UUID.fromString(line.substring(5).trim());
        }
        throw new IOException("invalid owner metadata " + p);
    }
}
