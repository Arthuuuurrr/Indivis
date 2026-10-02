package net.hautecapitale.spawns.state;

import com.mojang.serialization.Codec;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Les etats des points, sauvegardes avec le monde ({@code data/haute_capitale_spawns.dat}
 * de l'Overworld — les zones peuvent vivre dans n'importe quelle dimension, l'etat
 * n'appartient donc a aucune).
 *
 * <p>Une entree par point connu ; les entrees de points supprimes sont purgees au
 * chargement par le moteur (voir {@code repair}).
 */
public final class SpawnStateStore extends PersistentState {

    private static final String ID = "haute_capitale_spawns";

    private static final Codec<SpawnStateStore> CODEC = PointState.Saved.CODEC.listOf()
            .xmap(SpawnStateStore::new, store -> {
                List<PointState.Saved> out = new ArrayList<>(store.states.size());
                for (PointState state : store.states.values()) {
                    out.add(state.toSaved());
                }
                return out;
            });

    private static final PersistentStateType<SpawnStateStore> TYPE =
            new PersistentStateType<>(ID, () -> new SpawnStateStore(List.of()), CODEC, DataFixTypes.LEVEL);

    private final Map<String, PointState> states = new HashMap<>();

    private SpawnStateStore(List<PointState.Saved> loaded) {
        for (PointState.Saved saved : loaded) {
            this.states.put(saved.fullId(), PointState.fromSaved(saved));
        }
    }

    /** Pour les tests hors jeu. */
    public static SpawnStateStore empty() {
        return new SpawnStateStore(List.of());
    }

    public static SpawnStateStore of(MinecraftServer server) {
        return server.getOverworld().getPersistentStateManager().getOrCreate(TYPE);
    }

    public static Codec<SpawnStateStore> codec() {
        return CODEC;
    }

    public PointState getOrCreate(String fullId) {
        PointState state = this.states.get(fullId);
        if (state == null) {
            state = new PointState(fullId);
            this.states.put(fullId, state);
            this.markDirty();
        }
        return state;
    }

    public Optional<PointState> get(String fullId) {
        return Optional.ofNullable(this.states.get(fullId));
    }

    public Collection<PointState> all() {
        return this.states.values();
    }

    public boolean remove(String fullId) {
        boolean removed = this.states.remove(fullId) != null;
        if (removed) {
            this.markDirty();
        }
        return removed;
    }

    public int size() {
        return this.states.size();
    }

    /** A appeler apres toute transition : la sauvegarde du monde emportera le changement. */
    public void changed() {
        this.markDirty();
    }
}
