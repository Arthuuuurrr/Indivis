package net.hautecapitale.fusils;

import net.minecraft.resources.Identifier;

/** Identifiants du mod. */
public final class FusilsIds {
	public static final String MOD_ID = "haute_capitale_fusils";

	private FusilsIds() {}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	/** Accepte "arquebuse" comme "haute_capitale_fusils:arquebuse". */
	public static Identifier parse(String s) {
		return s.indexOf(':') >= 0 ? Identifier.parse(s) : id(s);
	}
}
