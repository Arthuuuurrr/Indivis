package net.hautecapitale.fusils.client.render;

import net.hautecapitale.fusils.item.FusilItem;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

/** Chemins GeckoLib d'un fusil : ceux du profil, ou ceux d'une variante (texture / modèle alternatifs). */
public class GunGeoModel extends GeoModel<FusilItem> {
	private final FusilItem item;

	public GunGeoModel(FusilItem item) {
		this.item = item;
	}

	@Override
	public Identifier getModelResource(GeoRenderState renderState) {
		if (renderState.hasGeckolibData(GunRenderTickets.MODEL)) return renderState.getGeckolibData(GunRenderTickets.MODEL);
		return this.item.profile().assets().model();
	}

	@Override
	public Identifier getTextureResource(GeoRenderState renderState) {
		if (renderState.hasGeckolibData(GunRenderTickets.TEXTURE)) return renderState.getGeckolibData(GunRenderTickets.TEXTURE);
		return this.item.profile().assets().texture();
	}

	@Override
	public Identifier getAnimationResource(FusilItem animatable) {
		return animatable.profile().assets().animation();
	}
}
