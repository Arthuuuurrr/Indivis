package net.hautecapitale.fusils.command;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.hautecapitale.fusils.api.FusilAPI;
import net.hautecapitale.fusils.data.Stat;
import net.hautecapitale.fusils.gun.AmmoType;
import net.hautecapitale.fusils.gun.ComposedShot;
import net.hautecapitale.fusils.gun.FusilsData;
import net.hautecapitale.fusils.gun.GunModification;
import net.hautecapitale.fusils.gun.GunplayManager;
import net.hautecapitale.fusils.gun.HunterMark;
import net.hautecapitale.fusils.gun.ShotComposer;
import net.hautecapitale.fusils.item.FusilItem;
import net.hautecapitale.fusils.skills.HunterSkills;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * {@code /fusil} : le pont entre le mod et vos systèmes (datapacks, PNJ, capacités).
 * <pre>
 * /fusil give &lt;joueurs&gt; &lt;profil|variante&gt;
 * /fusil charge &lt;joueurs&gt; &lt;munition&gt; [tirs]
 * /fusil skill &lt;joueurs&gt; &lt;rafale|tir_incapacitant|tir_perforant|tir_explosif|repli|marque&gt; [param]
 * /fusil mark &lt;cibles&gt; [ticks] [marqueur]
 * /fusil install &lt;joueurs&gt; &lt;modification&gt;   /fusil uninstall &lt;joueurs&gt; &lt;canon|mecanisme&gt;
 * /fusil reload &lt;joueurs&gt; [coups]           /fusil stats [joueur]
 * /fusil list &lt;profils|variantes|munitions|modifications&gt;
 * </pre>
 */
public final class FusilCommands {
	private FusilCommands() {}

	private static SuggestionProvider<CommandSourceStack> suggest(Function<Void, Collection<Identifier>> ids) {
		return (ctx, builder) -> SharedSuggestionProvider.suggestResource(ids.apply(null), builder);
	}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext ctx, Commands.CommandSelection selection) {
		dispatcher.register(Commands.literal("fusil")
				.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
				.then(Commands.literal("give")
						.then(Commands.argument("joueurs", EntityArgument.players())
								.then(Commands.argument("arme", StringArgumentType.string())
										.suggests(suggest(v -> { var l = new java.util.ArrayList<>(FusilsData.profiles().keySet()); l.addAll(FusilsData.variants().keySet()); return l; }))
										.executes(c -> give(c.getSource(), EntityArgument.getPlayers(c, "joueurs"), StringArgumentType.getString(c, "arme"))))))
				.then(Commands.literal("charge")
						.then(Commands.argument("joueurs", EntityArgument.entities())
								.then(Commands.argument("munition", StringArgumentType.string())
										.suggests(suggest(v -> FusilsData.ammo().keySet()))
										.executes(c -> charge(c.getSource(), EntityArgument.getEntities(c, "joueurs"), StringArgumentType.getString(c, "munition"), 0))
										.then(Commands.argument("tirs", IntegerArgumentType.integer(1, 64))
												.executes(c -> charge(c.getSource(), EntityArgument.getEntities(c, "joueurs"), StringArgumentType.getString(c, "munition"), IntegerArgumentType.getInteger(c, "tirs")))))))
				.then(Commands.literal("skill")
						.then(Commands.argument("joueurs", EntityArgument.entities())
								.then(Commands.argument("competence", StringArgumentType.word())
										.suggests((c, b) -> SharedSuggestionProvider.suggest(HunterSkills.ALL, b))
										.executes(c -> skill(c.getSource(), EntityArgument.getEntities(c, "joueurs"), StringArgumentType.getString(c, "competence"), 0))
										.then(Commands.argument("param", IntegerArgumentType.integer(0))
												.executes(c -> skill(c.getSource(), EntityArgument.getEntities(c, "joueurs"), StringArgumentType.getString(c, "competence"), IntegerArgumentType.getInteger(c, "param")))))))
				.then(Commands.literal("mark")
						.then(Commands.argument("cibles", EntityArgument.entities())
								.executes(c -> mark(c.getSource(), EntityArgument.getEntities(c, "cibles"), 0, null))
								.then(Commands.argument("ticks", IntegerArgumentType.integer(1))
										.executes(c -> mark(c.getSource(), EntityArgument.getEntities(c, "cibles"), IntegerArgumentType.getInteger(c, "ticks"), null))
										.then(Commands.argument("marqueur", EntityArgument.player())
												.executes(c -> mark(c.getSource(), EntityArgument.getEntities(c, "cibles"), IntegerArgumentType.getInteger(c, "ticks"), EntityArgument.getPlayer(c, "marqueur")))))))
				.then(Commands.literal("install")
						.then(Commands.argument("joueurs", EntityArgument.entities())
								.then(Commands.argument("modification", StringArgumentType.string())
										.suggests(suggest(v -> FusilsData.modifications().keySet()))
										.executes(c -> install(c.getSource(), EntityArgument.getEntities(c, "joueurs"), StringArgumentType.getString(c, "modification"))))))
				.then(Commands.literal("uninstall")
						.then(Commands.argument("joueurs", EntityArgument.entities())
								.then(Commands.argument("categorie", StringArgumentType.word())
										.suggests((c, b) -> SharedSuggestionProvider.suggest(new String[] {"canon", "mecanisme"}, b))
										.executes(c -> uninstall(c.getSource(), EntityArgument.getEntities(c, "joueurs"), StringArgumentType.getString(c, "categorie"))))))
				.then(Commands.literal("reload")
						.then(Commands.argument("joueurs", EntityArgument.entities())
								.executes(c -> reload(c.getSource(), EntityArgument.getEntities(c, "joueurs"), 0))
								.then(Commands.argument("coups", IntegerArgumentType.integer(1))
										.executes(c -> reload(c.getSource(), EntityArgument.getEntities(c, "joueurs"), IntegerArgumentType.getInteger(c, "coups"))))))
				.then(Commands.literal("startreload")
						.then(Commands.argument("joueurs", EntityArgument.entities())
								.executes(c -> {
									int n = 0;
									for (Entity e : EntityArgument.getEntities(c, "joueurs")) {
										if (e instanceof LivingEntity p && FusilItem.isGun(p.getMainHandItem())) {
											GunplayManager.ReloadResult r = GunplayManager.startReload(p, p.getMainHandItem());
											c.getSource().sendSuccess(() -> Component.literal(p.getName().getString() + " : " + r), false);
											n++;
										}
									}
									return n;
								})))
				.then(Commands.literal("stats")
						.executes(c -> stats(c.getSource(), c.getSource().getPlayerOrException()))
						.then(Commands.argument("joueur", EntityArgument.player())
								.executes(c -> stats(c.getSource(), EntityArgument.getPlayer(c, "joueur")))))
				.then(Commands.literal("list")
						.then(Commands.literal("profils").executes(c -> list(c.getSource(), "profils", FusilsData.profiles().keySet())))
						.then(Commands.literal("variantes").executes(c -> list(c.getSource(), "variantes", FusilsData.variants().keySet())))
						.then(Commands.literal("munitions").executes(c -> list(c.getSource(), "munitions", FusilsData.ammo().keySet())))
						.then(Commands.literal("modifications").executes(c -> list(c.getSource(), "modifications", FusilsData.modifications().keySet())))));
	}

	private static int give(CommandSourceStack source, Collection<ServerPlayer> players, String id) {
		Identifier rid = FusilsData.idOf(id);
		Optional<ItemStack> stack = FusilAPI.create(rid);
		if (stack.isEmpty()) {
			source.sendFailure(Component.literal("Arme inconnue : " + rid));
			return 0;
		}
		for (ServerPlayer p : players) {
			ItemStack s = stack.get().copy();
			if (!p.getInventory().add(s)) p.drop(s, false);
		}
		source.sendSuccess(() -> Component.literal("Donné " + rid + " à " + players.size() + " joueur(s)"), true);
		return players.size();
	}

	private static int charge(CommandSourceStack source, Collection<? extends Entity> players, String id, int shots) {
		Identifier rid = FusilsData.idOf(id);
		AmmoType ammo = FusilsData.ammo(rid);
		if (ammo == null) {
			source.sendFailure(Component.literal("Munition inconnue : " + rid));
			return 0;
		}
		int n = 0;
		for (Entity e : players) {
			if (e instanceof LivingEntity p && GunplayManager.charge(p, p.getMainHandItem(), rid, shots > 0 ? shots : ammo.defaultShots())) n++;
		}
		final int count = n;
		source.sendSuccess(() -> Component.literal("Munition " + rid + " chargée sur " + count + " arme(s)"), true);
		return n;
	}

	private static int skill(CommandSourceStack source, Collection<? extends Entity> players, String skill, int param) {
		int n = 0;
		for (Entity e : players) {
			if (!(e instanceof LivingEntity p)) continue;
			Optional<String> err = HunterSkills.use(p, skill, param);
			if (err.isPresent()) source.sendFailure(Component.literal(p.getName().getString() + " : " + err.get()));
			else n++;
		}
		return n;
	}

	private static int mark(CommandSourceStack source, Collection<? extends Entity> targets, int ticks, ServerPlayer marker) {
		LivingEntity by = marker != null ? marker : (source.getEntity() instanceof LivingEntity l ? l : null);
		if (by == null) {
			source.sendFailure(Component.literal("Préciser le joueur marqueur"));
			return 0;
		}
		int n = 0;
		for (Entity e : targets) {
			if (e instanceof LivingEntity living && living != by) {
				HunterMark.apply(living, by, ticks > 0 ? ticks : net.hautecapitale.fusils.config.FusilsConfig.INSTANCE.mark_duration_ticks);
				n++;
			}
		}
		final int count = n;
		source.sendSuccess(() -> Component.literal(count + " cible(s) marquée(s)"), true);
		return n;
	}

	private static int install(CommandSourceStack source, Collection<? extends Entity> players, String id) {
		Identifier rid = FusilsData.idOf(id);
		if (FusilsData.modification(rid) == null) {
			source.sendFailure(Component.literal("Modification inconnue : " + rid));
			return 0;
		}
		int n = 0;
		for (Entity e : players) if (e instanceof LivingEntity p && FusilAPI.install(p.getMainHandItem(), rid)) n++;
		final int count = n;
		source.sendSuccess(() -> Component.literal(rid + " installée sur " + count + " arme(s)"), true);
		return n;
	}

	private static int uninstall(CommandSourceStack source, Collection<? extends Entity> players, String category) {
		GunModification.Category cat = category.equalsIgnoreCase("canon") ? GunModification.Category.CANON : GunModification.Category.MECANISME;
		int n = 0;
		for (Entity e : players) {
			if (!(e instanceof LivingEntity p)) continue;
			if (FusilItem.isGun(p.getMainHandItem())) {
				FusilAPI.uninstall(p.getMainHandItem(), cat);
				n++;
			}
		}
		return n;
	}

	private static int reload(CommandSourceStack source, Collection<? extends Entity> players, int rounds) {
		int n = 0;
		for (Entity e : players) {
			if (!(e instanceof LivingEntity p)) continue;
			if (FusilItem.isGun(p.getMainHandItem())) n += GunplayManager.instantReload(p, p.getMainHandItem(), rounds);
		}
		return n;
	}

	private static int stats(CommandSourceStack source, ServerPlayer player) {
		ItemStack gun = player.getMainHandItem();
		if (!FusilItem.isGun(gun)) {
			source.sendFailure(Component.literal("Pas de fusil en main"));
			return 0;
		}
		ComposedShot composed = ShotComposer.compose(player, gun);
		StringBuilder sb = new StringBuilder("profil " + composed.profileId());
		if (composed.variant() != null) sb.append(" / variante ").append(gun.get(net.hautecapitale.fusils.registry.FusilsComponents.VARIANT));
		sb.append(" / chargeur ").append(FusilItem.magazine(gun).count()).append("/").append(FusilItem.magazineCapacity(gun, composed));
		for (Stat stat : Stat.values()) {
			double v = composed.stats().get(stat);
			if (v != stat.defaultValue() || stat == Stat.DAMAGE || stat == Stat.SPREAD || stat == Stat.FIRE_DELAY) {
				sb.append("\n ").append(stat.getSerializedName()).append(" = ").append(String.format(java.util.Locale.ROOT, "%.3f", v));
			}
		}
		if (composed.stats().ammoId() != null) sb.append("\n munition chargée : ").append(composed.stats().ammoId());
		if (!composed.stats().effects().isEmpty()) sb.append("\n effets : ").append(composed.stats().effects().size());
		source.sendSuccess(() -> Component.literal(sb.toString()), false);
		return 1;
	}

	private static int list(CommandSourceStack source, String what, Collection<Identifier> ids) {
		StringBuilder sb = new StringBuilder(what + " (" + ids.size() + ")");
		for (Identifier id : ids) sb.append("\n ").append(id);
		source.sendSuccess(() -> Component.literal(sb.toString()), false);
		return ids.size();
	}
}
