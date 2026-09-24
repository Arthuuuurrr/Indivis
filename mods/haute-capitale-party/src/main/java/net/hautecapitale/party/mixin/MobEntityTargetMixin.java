package net.hautecapitale.party.mixin;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.hautecapitale.party.combat.FriendlyFire;
import net.hautecapitale.party.config.PartyConfig;
import net.hautecapitale.party.party.PartyManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Empeche une invocation / un familier d'un membre de <b>cibler</b> un autre membre de son groupe.
 *
 * <p>L'equipe de scoreboard et {@link FriendlyFire} annulent deja les <em>degats</em> entre allies,
 * mais une creature possedee (golem d'invocateur, loup apprivoise, invocation de sort) continuait a
 * <em>prendre pour cible</em> et a foncer sur les allies de son maitre une fois son ennemi mort.
 * On coupe la a la racine : quand une {@link MobEntity} tente de prendre pour cible un joueur, si la
 * creature appartient (chaine de propriete) a un joueur du meme groupe que la cible, on annule la
 * prise de cible. Le ciblage des ennemis (mobs, joueurs hors groupe) n'est pas touche.
 *
 * <p>Cote serveur uniquement (garde {@code ServerPlayerEntity} + serveur non nul) ; sur un mob non
 * possede, la garde ne fait rien.
 */
@Mixin(MobEntity.class)
public abstract class MobEntityTargetMixin {

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void hcparty$blockAllyTargeting(LivingEntity target, CallbackInfo ci) {
        if (!(target instanceof ServerPlayerEntity victim)) {
            return;
        }
        PartyConfig config = PartyConfig.get();
        if (!config.friendlyFireProtection || !config.guardSummonTargeting) {
            return;
        }
        UUID owner = FriendlyFire.rootPlayer((MobEntity) (Object) this);
        if (owner == null) {
            return; // creature sans maitre : ciblage normal
        }
        MinecraftServer server = victim.getEntityWorld().getServer();
        if (server != null && PartyManager.sameParty(server, owner, victim.getUuid())) {
            ci.cancel(); // le maitre et la cible sont dans le meme groupe : pas de prise de cible
        }
    }
}
