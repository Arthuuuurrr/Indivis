package net.hautecapitale.fusils.client.render;

import net.hautecapitale.fusils.FusilsIds;
import net.hautecapitale.fusils.gun.GunProfile;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.constant.dataticket.DataTicket;

/** Données capturées à chaque rendu d'un fusil et lues pendant le rendu. */
public final class GunRenderTickets {
	public static final DataTicket<Integer> MAGAZINE = DataTicket.create(FusilsIds.id("chargeur").toString(), Integer.class);
	public static final DataTicket<Integer> CAPACITY = DataTicket.create(FusilsIds.id("capacite").toString(), Integer.class);
	public static final DataTicket<Double> RELOAD_SECONDS = DataTicket.create(FusilsIds.id("recharge_s").toString(), Double.class);
	public static final DataTicket<GunProfile.Adjuster> ADJUSTER = DataTicket.create(FusilsIds.id("ajusteur").toString(), GunProfile.Adjuster.class);
	public static final DataTicket<GunProfile.HandOccupancy> OCCUPANCY = DataTicket.create(FusilsIds.id("mains").toString(), GunProfile.HandOccupancy.class);
	public static final DataTicket<Identifier> TEXTURE = DataTicket.create(FusilsIds.id("texture").toString(), Identifier.class);
	public static final DataTicket<Identifier> MODEL = DataTicket.create(FusilsIds.id("modele").toString(), Identifier.class);
	public static final DataTicket<Boolean> SCOPE = DataTicket.create(FusilsIds.id("lunette").toString(), Boolean.class);
	public static final DataTicket<Float> AIM = DataTicket.create(FusilsIds.id("visee").toString(), Float.class);
	public static final DataTicket<float[]> AIM_OFFSET = DataTicket.create(FusilsIds.id("visee_decalage").toString(), float[].class);
	public static final DataTicket<Boolean> LOCAL_FIRST_PERSON = DataTicket.create(FusilsIds.id("premiere_personne").toString(), Boolean.class);

	private GunRenderTickets() {}
}
