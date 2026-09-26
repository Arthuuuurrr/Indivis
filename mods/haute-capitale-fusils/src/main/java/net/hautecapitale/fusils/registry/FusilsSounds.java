package net.hautecapitale.fusils.registry;

import java.util.ArrayList;
import java.util.List;

import net.hautecapitale.fusils.FusilsIds;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

/**
 * Événements sonores du mod. Les fichiers derrière sont déclarés dans {@code sounds.json} et
 * pointent aujourd'hui vers des sons vanilla composés ; remplacer un son = remplacer l'entrée
 * du {@code sounds.json}, sans toucher au code.
 */
public final class FusilsSounds {
	private static final List<SoundEvent> ALL = new ArrayList<>();

	public static final SoundEvent TIR_GENERIQUE = reg("arme.tir_generique");
	public static final SoundEvent ECHO = reg("arme.echo");
	public static final SoundEvent ECHO_POUDRE = reg("arme.echo_poudre");
	public static final SoundEvent A_VIDE = reg("arme.a_vide");
	public static final SoundEvent CHIEN = reg("arme.chien");
	public static final SoundEvent CUIR = reg("arme.cuir");
	public static final SoundEvent IMPACT = reg("balle.impact");
	public static final SoundEvent RICOCHET = reg("balle.ricochet");
	public static final SoundEvent MARQUE = reg("chasseur.marque");
	public static final SoundEvent CHARGE_MUNITION = reg("chasseur.charge_munition");

	public static final SoundEvent PISTOLET_TIR = reg("pistolet_silex.tir");
	public static final SoundEvent PISTOLET_EQUIP = reg("pistolet_silex.equip");
	public static final SoundEvent PISTOLET_INSERT = reg("pistolet_silex.insert");
	public static final SoundEvent PISTOLET_BOURRE = reg("pistolet_silex.bourre");
	public static final SoundEvent MOUSQUET_TIR = reg("mousquet.tir");
	public static final SoundEvent MOUSQUET_EQUIP = reg("mousquet.equip");
	public static final SoundEvent ARQUEBUSE_TIR = reg("arquebuse.tir");
	public static final SoundEvent ARQUEBUSE_OUVRE = reg("arquebuse.ouvre_culasse");
	public static final SoundEvent ARQUEBUSE_FERME = reg("arquebuse.ferme_culasse");
	public static final SoundEvent ARQUEBUSE_CHARGE = reg("arquebuse.charge_chambre");
	public static final SoundEvent TROMBLON_TIR = reg("tromblon.tir");
	public static final SoundEvent TROMBLON_OUVRE = reg("tromblon.ouvre");
	public static final SoundEvent TROMBLON_FERME = reg("tromblon.ferme");
	public static final SoundEvent TROMBLON_CHARGE = reg("tromblon.charge");
	public static final SoundEvent ROUAGES_TIR = reg("fusil_rouages.tir");
	public static final SoundEvent ROUAGES_EJECTE = reg("fusil_rouages.ejecte");
	public static final SoundEvent ROUAGES_INSERE = reg("fusil_rouages.insere");
	public static final SoundEvent ROUAGES_EQUIP = reg("fusil_rouages.equip");
	public static final SoundEvent CANON_MAIN_TIR = reg("canon_main.tir");
	public static final SoundEvent CANON_MAIN_CHARGE = reg("canon_main.charge");

	private FusilsSounds() {}

	private static SoundEvent reg(String path) {
		SoundEvent e = SoundEvent.createVariableRangeEvent(FusilsIds.id(path));
		Registry.register(BuiltInRegistries.SOUND_EVENT, FusilsIds.id(path), e);
		ALL.add(e);
		return e;
	}

	public static void init() {}
}
