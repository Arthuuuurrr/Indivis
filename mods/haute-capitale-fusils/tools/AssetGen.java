import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

/**
 * Générateur des assets originaux du mod Fusils du Chasseur : modèles GeckoLib (.geo.json),
 * animations (.animation.json), textures d'armes (pixel-art bois / acier / laiton), particules,
 * icônes d'objets 2D, fond d'atelier, icône du mod. Aucun fichier d'un autre mod n'est lu.
 *
 * Usage : java AssetGen <dossier src/main/resources>
 */
public final class AssetGen {
	static final String NS = "haute_capitale_fusils";
	static Path root;

	// ------------------------------------------------------------------ matériaux
	enum Mat {
		WOOD(0x6B4423, 0x4A2E17, 0x8A5C3A), WOOD_DARK(0x4E3018, 0x33200F, 0x6A4526), IRON(0x6E7378, 0x474C50, 0x9DA3A8),
		STEEL(0x3F4A57, 0x28313B, 0x6C7C8C), BRASS(0xB8862B, 0x85601C, 0xE3B94F), LEATHER(0x5A3A22, 0x3C2615, 0x7A5232),
		GLASS(0x9FD8E8, 0x5FA3B8, 0xE4F7FC), BRONZE(0x8C6A3C, 0x5F4626, 0xB58E55);

		final int base, dark, light;

		Mat(int base, int dark, int light) {
			this.base = base;
			this.dark = dark;
			this.light = light;
		}
	}

	record Box(Mat mat, float x, float y, float z, float w, float h, float d, float inflate, float[] pivot, float[] rot) {
		static Box of(Mat m, float x, float y, float z, float w, float h, float d) {
			return new Box(m, x, y, z, w, h, d, 0, null, null);
		}

		Box inflated(float i) { return new Box(mat, x, y, z, w, h, d, i, pivot, rot); }
		Box rotated(float px, float py, float pz, float rx, float ry, float rz) { return new Box(mat, x, y, z, w, h, d, inflate, new float[] {px, py, pz}, new float[] {rx, ry, rz}); }
	}

	static final class Bone {
		final String name, parent;
		final float[] pivot;
		final float[] rot;
		final List<Box> boxes = new ArrayList<>();

		Bone(String name, String parent, float px, float py, float pz) { this(name, parent, px, py, pz, 0, 0, 0); }

		Bone(String name, String parent, float px, float py, float pz, float rx, float ry, float rz) {
			this.name = name;
			this.parent = parent;
			this.pivot = new float[] {px, py, pz};
			this.rot = new float[] {rx, ry, rz};
		}

		Bone add(Box... b) { for (Box x : b) boxes.add(x); return this; }
	}

	static final class Model {
		final String name;
		final int texW, texH;
		final List<Bone> bones = new ArrayList<>();

		Model(String name, int texW, int texH) { this.name = name; this.texW = texW; this.texH = texH; }

		Bone bone(String n, String parent, float px, float py, float pz) { Bone b = new Bone(n, parent, px, py, pz); bones.add(b); return b; }
		Bone bone(String n, String parent, float px, float py, float pz, float rx, float ry, float rz) { Bone b = new Bone(n, parent, px, py, pz, rx, ry, rz); bones.add(b); return b; }
	}

	// ------------------------------------------------------------------ UV packing + texture painting
	static final class Packer {
		final int w, h;
		int cx = 0, cy = 0, rowH = 0;

		Packer(int w, int h) { this.w = w; this.h = h; }

		int[] alloc(int rw, int rh) {
			rw = Math.max(1, rw); rh = Math.max(1, rh);
			if (cx + rw > w) { cx = 0; cy += rowH; rowH = 0; }
			if (cy + rh > h) throw new IllegalStateException("texture trop petite pour " + rw + "x" + rh);
			int[] r = {cx, cy};
			cx += rw;
			rowH = Math.max(rowH, rh);
			return r;
		}
	}

	static int mix(int a, int b, float t) {
		int r = Math.round(((a >> 16) & 255) * (1 - t) + ((b >> 16) & 255) * t);
		int g = Math.round(((a >> 8) & 255) * (1 - t) + ((b >> 8) & 255) * t);
		int bl = Math.round((a & 255) * (1 - t) + (b & 255) * t);
		return 0xFF000000 | r << 16 | g << 8 | bl;
	}

	static void paintFace(BufferedImage img, int x0, int y0, int fw, int fh, Mat m, Random rnd, String face, boolean horizontalGrain) {
		for (int y = 0; y < fh; y++) {
			for (int x = 0; x < fw; x++) {
				float n = (rnd.nextFloat() - 0.5f) * 0.16f;
				int c = mix(m.base, n < 0 ? m.dark : m.light, Math.abs(n) * 2);
				if (m == Mat.WOOD || m == Mat.WOOD_DARK) {
					int line = horizontalGrain ? y : x;
					int span = horizontalGrain ? fw : fh;
					if (span > 2 && (line + (horizontalGrain ? x / 5 : y / 5)) % 3 == 0) c = mix(c, m.dark, 0.35f);
				} else if (m == Mat.BRASS || m == Mat.BRONZE) {
					if ((x + y) % 5 == 0) c = mix(c, m.light, 0.35f);
				} else if (m == Mat.IRON || m == Mat.STEEL) {
					if (horizontalGrain && fh > 2 && y == fh / 2) c = mix(c, m.light, 0.25f);
				}
				boolean edge = x == 0 || y == 0 || x == fw - 1 || y == fh - 1;
				if (edge && fw > 2 && fh > 2) c = mix(c, y == 0 ? m.light : m.dark, y == 0 ? 0.35f : 0.45f);
				if (face.equals("down")) c = mix(c, m.dark, 0.35f);
				if (face.equals("up")) c = mix(c, m.light, 0.15f);
				img.setRGB(x0 + x, y0 + y, c);
			}
		}
	}

	/** Écrit la géométrie et peint la texture en même temps (les UV par face sont attribuées ici). */
	static void writeModel(Model m) throws Exception {
		BufferedImage tex = new BufferedImage(m.texW, m.texH, BufferedImage.TYPE_INT_ARGB);
		Packer packer = new Packer(m.texW, m.texH);
		Random rnd = new Random(m.name.hashCode());
		StringBuilder sb = new StringBuilder();
		sb.append("{\n\t\"format_version\": \"1.12.0\",\n\t\"minecraft:geometry\": [\n\t\t{\n\t\t\t\"description\": {\n");
		sb.append("\t\t\t\t\"identifier\": \"geometry.").append(m.name).append("\",\n");
		sb.append("\t\t\t\t\"texture_width\": ").append(m.texW).append(",\n\t\t\t\t\"texture_height\": ").append(m.texH).append(",\n");
		sb.append("\t\t\t\t\"visible_bounds_width\": 4,\n\t\t\t\t\"visible_bounds_height\": 3,\n\t\t\t\t\"visible_bounds_offset\": [0, 0.5, 0]\n\t\t\t},\n\t\t\t\"bones\": [\n");
		boolean firstBone = true;
		for (Bone b : m.bones) {
			if (!firstBone) sb.append(",\n");
			firstBone = false;
			sb.append("\t\t\t\t{\n\t\t\t\t\t\"name\": \"").append(b.name).append("\",\n");
			if (b.parent != null) sb.append("\t\t\t\t\t\"parent\": \"").append(b.parent).append("\",\n");
			sb.append("\t\t\t\t\t\"pivot\": ").append(vec(b.pivot));
			if (b.rot[0] != 0 || b.rot[1] != 0 || b.rot[2] != 0) sb.append(",\n\t\t\t\t\t\"rotation\": ").append(vec(b.rot));
			if (!b.boxes.isEmpty()) {
				sb.append(",\n\t\t\t\t\t\"cubes\": [\n");
				boolean firstCube = true;
				for (Box c : b.boxes) {
					if (!firstCube) sb.append(",\n");
					firstCube = false;
					sb.append("\t\t\t\t\t\t{\n\t\t\t\t\t\t\t\"origin\": ").append(vec(new float[] {c.x, c.y, c.z}));
					sb.append(",\n\t\t\t\t\t\t\t\"size\": ").append(vec(new float[] {c.w, c.h, c.d}));
					if (c.inflate != 0) sb.append(",\n\t\t\t\t\t\t\t\"inflate\": ").append(f(c.inflate));
					if (c.pivot != null) {
						sb.append(",\n\t\t\t\t\t\t\t\"pivot\": ").append(vec(c.pivot));
						sb.append(",\n\t\t\t\t\t\t\t\"rotation\": ").append(vec(c.rot));
					}
					sb.append(",\n\t\t\t\t\t\t\t\"uv\": {\n");
					int w = Math.max(1, Math.round(c.w)), h = Math.max(1, Math.round(c.h)), d = Math.max(1, Math.round(c.d));
					String[] faces = {"north", "south", "east", "west", "up", "down"};
					int[][] sizes = {{w, h}, {w, h}, {d, h}, {d, h}, {w, d}, {w, d}};
					for (int i = 0; i < 6; i++) {
						int[] at = packer.alloc(sizes[i][0], sizes[i][1]);
						boolean grain = sizes[i][0] >= sizes[i][1];
						paintFace(tex, at[0], at[1], sizes[i][0], sizes[i][1], c.mat, rnd, faces[i], grain);
						sb.append("\t\t\t\t\t\t\t\t\"").append(faces[i]).append("\": {\"uv\": [").append(at[0]).append(", ").append(at[1])
								.append("], \"uv_size\": [").append(sizes[i][0]).append(", ").append(sizes[i][1]).append("]}");
						sb.append(i < 5 ? ",\n" : "\n");
					}
					sb.append("\t\t\t\t\t\t\t}\n\t\t\t\t\t\t}");
				}
				sb.append("\n\t\t\t\t\t]");
			}
			sb.append("\n\t\t\t\t}");
		}
		sb.append("\n\t\t\t]\n\t\t}\n\t]\n}\n");
		write("assets/" + NS + "/geckolib/models/item/" + m.name + ".geo.json", sb.toString());
		ImageIO.write(tex, "png", root.resolve("assets/" + NS + "/textures/item/" + m.name + ".png").toFile());
	}

	static String vec(float[] v) { return "[" + f(v[0]) + ", " + f(v[1]) + ", " + f(v[2]) + "]"; }

	static String f(float v) {
		if (v == Math.rint(v)) return String.valueOf((int) v);
		return String.format(Locale.ROOT, "%.4f", v).replaceAll("0+$", "").replaceAll("\\.$", "");
	}

	static void write(String rel, String content) throws Exception {
		Path p = root.resolve(rel);
		Files.createDirectories(p.getParent());
		Files.writeString(p, content, StandardCharsets.UTF_8);
	}

	// ------------------------------------------------------------------ animations
	static final class Anim {
		final String name;
		final float length;
		final boolean loop;
		final Map<String, Map<String, Map<Float, float[]>>> channels = new LinkedHashMap<>(); // bone -> channel -> t -> (x,y,z)
		final Map<String, Map<String, Map<Float, String>>> easings = new LinkedHashMap<>();

		Anim(String name, float length, boolean loop) { this.name = name; this.length = length; this.loop = loop; }

		Anim rot(String bone, float t, float x, float y, float z) { return kf(bone, "rotation", t, x, y, z, "easeInOutSine"); }
		Anim rot(String bone, float t, float x, float y, float z, String ease) { return kf(bone, "rotation", t, x, y, z, ease); }
		Anim pos(String bone, float t, float x, float y, float z) { return kf(bone, "position", t, x, y, z, "easeInOutSine"); }
		Anim pos(String bone, float t, float x, float y, float z, String ease) { return kf(bone, "position", t, x, y, z, ease); }

		Anim kf(String bone, String ch, float t, float x, float y, float z, String ease) {
			channels.computeIfAbsent(bone, b -> new LinkedHashMap<>()).computeIfAbsent(ch, c -> new LinkedHashMap<>()).put(t, new float[] {x, y, z});
			easings.computeIfAbsent(bone, b -> new LinkedHashMap<>()).computeIfAbsent(ch, c -> new LinkedHashMap<>()).put(t, ease);
			return this;
		}

		/** Une pose constante (utile pour idle et pour figer un os dans une autre animation). */
		Anim hold(String bone, float rx, float ry, float rz, float px, float py, float pz) {
			rot(bone, 0f, rx, ry, rz);
			pos(bone, 0f, px, py, pz);
			return this;
		}

		void emit(StringBuilder sb, boolean last) {
			sb.append("\t\t\"").append(name).append("\": {\n");
			if (loop) sb.append("\t\t\t\"loop\": true,\n");
			else sb.append("\t\t\t\"animation_length\": ").append(f(length)).append(",\n");
			sb.append("\t\t\t\"bones\": {\n");
			int bi = 0;
			for (var be : channels.entrySet()) {
				sb.append("\t\t\t\t\"").append(be.getKey()).append("\": {\n");
				int ci = 0;
				for (var ce : be.getValue().entrySet()) {
					sb.append("\t\t\t\t\t\"").append(ce.getKey()).append("\": {\n");
					int ki = 0;
					for (var ke : ce.getValue().entrySet()) {
						String ease = easings.get(be.getKey()).get(ce.getKey()).get(ke.getKey());
						sb.append("\t\t\t\t\t\t\"").append(f(ke.getKey())).append("\": {\"vector\": ").append(vec(ke.getValue()));
						if (ki > 0 && ease != null) sb.append(", \"easing\": \"").append(ease).append("\"");
						sb.append("}");
						sb.append(++ki < ce.getValue().size() ? ",\n" : "\n");
					}
					sb.append("\t\t\t\t\t}").append(++ci < be.getValue().size() ? ",\n" : "\n");
				}
				sb.append("\t\t\t\t}").append(++bi < channels.size() ? ",\n" : "\n");
			}
			sb.append("\t\t\t}\n\t\t}").append(last ? "\n" : ",\n");
		}
	}

	static void writeAnimations(String name, Anim... anims) throws Exception {
		StringBuilder sb = new StringBuilder("{\n\t\"format_version\": \"1.8.0\",\n\t\"animations\": {\n");
		for (int i = 0; i < anims.length; i++) anims[i].emit(sb, i == anims.length - 1);
		sb.append("\t}\n}\n");
		write("assets/" + NS + "/geckolib/animations/item/" + name + ".animation.json", sb.toString());
	}

	// Poses de bras communes (première personne). L'os right_arm est au pivot (0,0,0) : le bras
	// du joueur y est dessiné, la main sur la poignée.
	static final float[] R_ARM_ROT = {-100, 0, 0}, R_ARM_POS = {-0.5f, -1, 0};
	static final float[] L_ARM_RIFLE_ROT = {-62, 18, 90}, L_ARM_RIFLE_POS = {0, 0, -16};
	static final float[] L_ARM_PISTOL_ROT = {-108, 0, 0}, L_ARM_PISTOL_POS = {12, -8, 6};

	static Anim idle(boolean rifle, boolean hammer, float lz) {
		Anim a = new Anim("idle", 0, true);
		if (hammer) a.rot("hammer", 0, -60, 0, 0);
		a.hold("right_arm", R_ARM_ROT[0], R_ARM_ROT[1], R_ARM_ROT[2], R_ARM_POS[0], R_ARM_POS[1], R_ARM_POS[2]);
		if (rifle) a.hold("left_arm", L_ARM_RIFLE_ROT[0], L_ARM_RIFLE_ROT[1], L_ARM_RIFLE_ROT[2], L_ARM_RIFLE_POS[0], L_ARM_RIFLE_POS[1], lz);
		else a.hold("left_arm", L_ARM_PISTOL_ROT[0], L_ARM_PISTOL_ROT[1], L_ARM_PISTOL_ROT[2], L_ARM_PISTOL_POS[0], L_ARM_PISTOL_POS[1], L_ARM_PISTOL_POS[2]);
		return a;
	}

	static Anim fire(float length, float kick, float rise, boolean hammer, boolean rifle, float lz) {
		Anim a = new Anim("fire", length, false);
		if (hammer) a.rot("hammer", 0, 0, 0, 0);
		a.rot("root", 0, 0, 0, 0).rot("root", length * 0.16f, -rise, 1.5f, 6f, "easeOutSine").rot("root", length * 0.68f, rise * 0.2f, 0, -1f).rot("root", length, 0, 0, 0);
		a.pos("root", 0, 0, 0, 0).pos("root", length * 0.16f, 0, -1, kick, "easeOutSine").pos("root", length * 0.5f, 0, -1, -1).pos("root", length, 0, 0, 0);
		a.hold("right_arm", R_ARM_ROT[0], R_ARM_ROT[1], R_ARM_ROT[2], R_ARM_POS[0], R_ARM_POS[1], R_ARM_POS[2]);
		if (rifle) a.hold("left_arm", L_ARM_RIFLE_ROT[0], L_ARM_RIFLE_ROT[1], L_ARM_RIFLE_ROT[2], L_ARM_RIFLE_POS[0], L_ARM_RIFLE_POS[1], lz);
		else a.hold("left_arm", L_ARM_PISTOL_ROT[0], L_ARM_PISTOL_ROT[1], L_ARM_PISTOL_ROT[2], L_ARM_PISTOL_POS[0], L_ARM_PISTOL_POS[1], L_ARM_PISTOL_POS[2]);
		return a;
	}

	static Anim equip(float length, boolean hammer, boolean rifle, float lz) {
		Anim a = new Anim("equip", length, false);
		if (hammer) a.rot("hammer", 0, -60, 0, 0).rot("hammer", length, -60, 0, 0);
		a.rot("root", 0, 28, -86, -69).rot("root", length * 0.35f, 80, -70, -110).rot("root", length * 0.8f, 0, 2, 0).rot("root", length, 0, 0, 0);
		a.pos("root", 0, -13, -12, -5).pos("root", length * 0.35f, -6, -4, 2).pos("root", length * 0.8f, 0, 0, 0.5f).pos("root", length, 0, 0, 0);
		a.rot("right_arm", 0, -150, 14, -50).rot("right_arm", length * 0.6f, R_ARM_ROT[0], R_ARM_ROT[1], R_ARM_ROT[2]);
		a.pos("right_arm", 0, -0.5f, -3, 8).pos("right_arm", length * 0.6f, R_ARM_POS[0], R_ARM_POS[1], R_ARM_POS[2]);
		if (rifle) {
			a.rot("left_arm", 0, -130, -1, -100).rot("left_arm", length * 0.55f, -117, 41, 1).rot("left_arm", length, L_ARM_RIFLE_ROT[0], L_ARM_RIFLE_ROT[1], L_ARM_RIFLE_ROT[2]);
			a.pos("left_arm", 0, -1, 4, -16).pos("left_arm", length * 0.55f, 1, 4, -16).pos("left_arm", length, L_ARM_RIFLE_POS[0], L_ARM_RIFLE_POS[1], lz);
		} else {
			a.hold("left_arm", L_ARM_PISTOL_ROT[0], L_ARM_PISTOL_ROT[1], L_ARM_PISTOL_ROT[2], L_ARM_PISTOL_POS[0], L_ARM_PISTOL_POS[1], L_ARM_PISTOL_POS[2]);
		}
		return a;
	}

	/** Recharge par la bouche (pistolet, mousquet, canon à main) : l'arme se dresse, la main libre insère et bourre. */
	static Anim reloadMuzzle(float length, boolean rifle, float lz, float muzzleZ) {
		Anim a = new Anim("reload", length, false);
		float t1 = length * 0.18f, t2 = length * 0.35f, tRam1 = length * 0.5f, tRam2 = length * 0.6f, tRam3 = length * 0.7f, tBack = length * 0.85f, tCock = length * 0.93f;
		a.rot("hammer", 0, -60, 0, 0).rot("hammer", tBack, 0, 0, 0).rot("hammer", tCock, -60, 0, 0);
		a.rot("root", 0, 0, 0, 0).rot("root", t1, -38, -14, 8).rot("root", tBack, -38, -14, 8).rot("root", length, 0, 0, 0);
		a.pos("root", 0, 0, 0, 0).pos("root", t1, 1, -4, 4).pos("root", tBack, 1, -4, 4).pos("root", length, 0, 0, 0);
		a.hold("right_arm", R_ARM_ROT[0], R_ARM_ROT[1], R_ARM_ROT[2], R_ARM_POS[0], R_ARM_POS[1], R_ARM_POS[2]);
		float[] lr = rifle ? L_ARM_RIFLE_ROT : L_ARM_PISTOL_ROT;
		float[] lp = rifle ? new float[] {L_ARM_RIFLE_POS[0], L_ARM_RIFLE_POS[1], lz} : L_ARM_PISTOL_POS;
		a.rot("left_arm", 0, lr[0], lr[1], lr[2]).rot("left_arm", t1, -118, -44, 24).rot("left_arm", t2, -128, -60, 34)
				.rot("left_arm", tRam1, -110, -50, 20).rot("left_arm", tRam2, -125, -60, 34).rot("left_arm", tRam3, -110, -50, 20)
				.rot("left_arm", tBack, -99, -4, -24).rot("left_arm", length, lr[0], lr[1], lr[2]);
		a.pos("left_arm", 0, lp[0], lp[1], lp[2]).pos("left_arm", t1, -1.5f, -1, muzzleZ + 2).pos("left_arm", t2, 0.5f, 3, muzzleZ - 6)
				.pos("left_arm", tRam1, 0.5f, 3, muzzleZ + 2).pos("left_arm", tRam2, 0.5f, 3, muzzleZ - 4).pos("left_arm", tRam3, 0.5f, 3, muzzleZ + 2)
				.pos("left_arm", tBack, -2.5f, 1, 0).pos("left_arm", length, lp[0], lp[1], lp[2]);
		a.pos("ramrod", 0, 0, 0, 0).pos("ramrod", t2, 0, 0, -10).pos("ramrod", tRam1, 0, 0, 2).pos("ramrod", tRam2, 0, 0, -6).pos("ramrod", tRam3, 0, 0, 2).pos("ramrod", tBack, 0, 0, 0);
		return a;
	}

	/** Recharge par la culasse (arquebuse) : ouverture, boucle de chargement des chambres [0.75, 1.75], fermeture. */
	static Anim reloadBreech(float length, float lz) {
		Anim a = new Anim("reload", length, false);
		a.rot("hammer", 0, -60, 0, 0).rot("hammer", 2.0f, -60, 0, 0).rot("hammer", 2.13f, 0, 0, 0).rot("hammer", 2.4f, -60, 0, 0);
		a.rot("breech", 0, 0, 0, 0).rot("breech", 0.35f, -95, 0, 0).rot("breech", 2.3f, -95, 0, 0).rot("breech", 2.5f, 0, 0, 0);
		a.rot("root", 0, 0, 0, 0).rot("root", 0.3f, 14, -22, 28).rot("root", 2.3f, 14, -22, 28).rot("root", length, 0, 0, 0);
		a.pos("root", 0, 0, 0, 0).pos("root", 0.3f, 2, -2, 3).pos("root", 2.3f, 2, -2, 3).pos("root", length, 0, 0, 0);
		a.hold("right_arm", R_ARM_ROT[0], R_ARM_ROT[1], R_ARM_ROT[2], R_ARM_POS[0], R_ARM_POS[1], R_ARM_POS[2]);
		a.rot("left_arm", 0, L_ARM_RIFLE_ROT[0], L_ARM_RIFLE_ROT[1], L_ARM_RIFLE_ROT[2]).rot("left_arm", 0.3f, -120, -30, 40);
		a.pos("left_arm", 0, L_ARM_RIFLE_POS[0], L_ARM_RIFLE_POS[1], lz).pos("left_arm", 0.3f, 2, 4, -2);
		// Une chambre toutes les 0,33 s : quatre coups de main entre 0,75 et 1,75 s.
		for (int i = 0; i < 4; i++) {
			float t = 0.6f + i * 0.33f;
			a.rot("left_arm", t, -128, -36, 46).pos("left_arm", t, 3, 6, -4);
			a.rot("left_arm", t + 0.17f, -118, -30, 38).pos("left_arm", t + 0.17f, 2, 4, -2);
		}
		a.rot("left_arm", 2.3f, -120, -30, 40).rot("left_arm", length, L_ARM_RIFLE_ROT[0], L_ARM_RIFLE_ROT[1], L_ARM_RIFLE_ROT[2]);
		a.pos("left_arm", 2.3f, 2, 4, -2).pos("left_arm", length, L_ARM_RIFLE_POS[0], L_ARM_RIFLE_POS[1], lz);
		return a;
	}

	/** Recharge à bascule (tromblon) : le canon bascule, deux cartouches, fermeture. */
	static Anim reloadBreak(float length, float lz) {
		Anim a = new Anim("reload", length, false);
		a.rot("hammer_left", 0, -60, 0, 0).rot("hammer_left", 1.1f, -60, 0, 0).rot("hammer_left", 1.15f, 0, 0, 0).rot("hammer_left", 1.3f, -60, 0, 0);
		a.rot("hammer_right", 0, -60, 0, 0).rot("hammer_right", 1.1f, -60, 0, 0).rot("hammer_right", 1.17f, 0, 0, 0).rot("hammer_right", 1.32f, -60, 0, 0);
		a.rot("barrel", 0, 0, 0, 0).rot("barrel", 0.25f, 28, 0, 0).rot("barrel", 1.12f, 28, 0, 0).rot("barrel", 1.27f, 0, 0, 0);
		a.rot("root", 0, 0, 0, 0).rot("root", 0.25f, 22, -18, 20).rot("root", 1.15f, 22, -18, 20).rot("root", length, 0, 0, 0);
		a.pos("root", 0, 0, 0, 0).pos("root", 0.25f, 1, -3, 2).pos("root", 1.15f, 1, -3, 2).pos("root", length, 0, 0, 0);
		a.hold("right_arm", R_ARM_ROT[0], R_ARM_ROT[1], R_ARM_ROT[2], R_ARM_POS[0], R_ARM_POS[1], R_ARM_POS[2]);
		a.rot("left_arm", 0, L_ARM_RIFLE_ROT[0], L_ARM_RIFLE_ROT[1], L_ARM_RIFLE_ROT[2]).rot("left_arm", 0.3f, -118, -34, 36).rot("left_arm", 0.6f, -130, -44, 48)
				.rot("left_arm", 0.9f, -120, -36, 40).rot("left_arm", 1.05f, -130, -44, 48).rot("left_arm", 1.2f, -110, -20, 20).rot("left_arm", length, L_ARM_RIFLE_ROT[0], L_ARM_RIFLE_ROT[1], L_ARM_RIFLE_ROT[2]);
		a.pos("left_arm", 0, L_ARM_RIFLE_POS[0], L_ARM_RIFLE_POS[1], lz).pos("left_arm", 0.3f, 1, 3, -6).pos("left_arm", 0.6f, 2, 6, -9).pos("left_arm", 0.9f, 1, 3, -6)
				.pos("left_arm", 1.05f, 2, 6, -9).pos("left_arm", 1.2f, 0, 1, -8).pos("left_arm", length, L_ARM_RIFLE_POS[0], L_ARM_RIFLE_POS[1], lz);
		return a;
	}

	/** Recharge par magasin (fusil à rouages) : éjection du magasin harmonica, insertion d'un neuf. */
	static Anim reloadMagazine(float length, float lz) {
		Anim a = new Anim("reload", length, false);
		a.rot("root", 0, 0, 0, 0).rot("root", 0.25f, 12, -24, 30).rot("root", 1.2f, 12, -24, 30).rot("root", length, 0, 0, 0);
		a.pos("root", 0, 0, 0, 0).pos("root", 0.25f, 2, -2, 2).pos("root", 1.2f, 2, -2, 2).pos("root", length, 0, 0, 0);
		a.pos("magazine", 0, 0, 0, 0).pos("magazine", 0.3f, 0, 0, 0).pos("magazine", 0.45f, 14, -3, 0, "easeInSine").pos("magazine", 0.7f, 14, -3, 0).pos("magazine", 1.04f, 0, 0, 0, "easeOutSine");
		a.hold("right_arm", R_ARM_ROT[0], R_ARM_ROT[1], R_ARM_ROT[2], R_ARM_POS[0], R_ARM_POS[1], R_ARM_POS[2]);
		a.rot("left_arm", 0, L_ARM_RIFLE_ROT[0], L_ARM_RIFLE_ROT[1], L_ARM_RIFLE_ROT[2]).rot("left_arm", 0.3f, -110, -40, 30).rot("left_arm", 0.45f, -100, -70, 20)
				.rot("left_arm", 0.7f, -100, -70, 20).rot("left_arm", 1.04f, -112, -38, 30).rot("left_arm", length, L_ARM_RIFLE_ROT[0], L_ARM_RIFLE_ROT[1], L_ARM_RIFLE_ROT[2]);
		a.pos("left_arm", 0, L_ARM_RIFLE_POS[0], L_ARM_RIFLE_POS[1], lz).pos("left_arm", 0.3f, -4, 2, -4).pos("left_arm", 0.45f, 12, -1, -4)
				.pos("left_arm", 0.7f, 12, -1, -4).pos("left_arm", 1.04f, -4, 2, -4).pos("left_arm", length, L_ARM_RIFLE_POS[0], L_ARM_RIFLE_POS[1], lz);
		return a;
	}

	// ------------------------------------------------------------------ les six armes
	static void addArms(Model m) {
		m.bone("right_arm", "root", 0, 0, 0);
		m.bone("left_arm", "root", 0, 0, 0);
	}

	static void optic(Model m, float y, float z, float len) {
		Bone o = m.bone("attachment_optic", "gun", 0, y, z);
		o.add(Box.of(Mat.BRASS, -0.75f, y - 0.25f, z - len / 2, 1.5f, 1.5f, len));
		o.add(Box.of(Mat.GLASS, -0.55f, y - 0.05f, z - len / 2 - 0.3f, 1.1f, 1.1f, 0.3f));
		o.add(Box.of(Mat.BRASS, -0.4f, y - 1.2f, z - 1, 0.8f, 1.2f, 2));
	}

	static Model pistolet() {
		Model m = new Model("pistolet_silex", 64, 64);
		m.bone("root", null, 0, 0, 0);
		addArms(m);
		Bone gun = m.bone("gun", "root", 0, 0, 0);
		gun.add(Box.of(Mat.IRON, -1, 0.25f, -14, 2, 2, 14));                     // canon
		gun.add(Box.of(Mat.BRASS, -1.25f, 0, -13, 2.5f, 2.5f, 1));               // frette de bouche
		gun.add(Box.of(Mat.WOOD, -1.25f, -1.75f, -9, 2.5f, 2.2f, 9));            // fût
		gun.add(Box.of(Mat.BRASS, 1, -0.5f, -4.5f, 0.6f, 2.5f, 4.5f));           // platine
		gun.add(Box.of(Mat.BRASS, -0.5f, -3.4f, -3, 1, 0.6f, 3.5f));             // pontet
		gun.add(Box.of(Mat.STEEL, -0.25f, -2.9f, -1.5f, 0.5f, 1.2f, 0.5f));      // détente
		Bone grip = m.bone("grip", "gun", 0, -0.5f, 0.5f, 42, 0, 0);
		grip.add(Box.of(Mat.WOOD, -1.25f, -7, -0.5f, 2.5f, 7, 3));
		grip.add(Box.of(Mat.BRASS, -1.35f, -7.4f, -0.6f, 2.7f, 0.9f, 3.2f));     // calotte
		Bone hammer = m.bone("hammer", "gun", 1.3f, 0.9f, -2.2f);
		hammer.add(Box.of(Mat.STEEL, 0.95f, 0.6f, -3.6f, 0.7f, 3.2f, 1.4f).rotated(1.3f, 0.9f, -2.2f, 15, 0, 0));
		Bone ramrod = m.bone("ramrod", "gun", 0, -0.9f, -13);
		ramrod.add(Box.of(Mat.IRON, -0.25f, -1.1f, -13.2f, 0.5f, 0.5f, 10));
		optic(m, 2.6f, -6, 5);
		return m;
	}

	static Model mousquet() {
		Model m = new Model("mousquet", 128, 64);
		m.bone("root", null, 0, 0, 0);
		addArms(m);
		Bone gun = m.bone("gun", "root", 0, 0, 0);
		gun.add(Box.of(Mat.IRON, -1, 0.25f, -27, 2, 2, 25));                     // canon
		gun.add(Box.of(Mat.IRON, -0.3f, 2.2f, -26.5f, 0.6f, 0.8f, 1));            // guidon
		gun.add(Box.of(Mat.WOOD, -1.25f, -1.9f, -22, 2.5f, 2.4f, 22));           // fût
		for (float z : new float[] {-9, -16, -23}) gun.add(Box.of(Mat.BRASS, -1.35f, -2, z, 2.7f, 4.4f, 1)); // frettes
		gun.add(Box.of(Mat.BRASS, 1, -0.6f, -5, 0.6f, 2.6f, 5));                  // platine
		gun.add(Box.of(Mat.BRASS, -0.5f, -3.6f, -3.5f, 1, 0.6f, 4));             // pontet
		gun.add(Box.of(Mat.STEEL, -0.25f, -3.1f, -1.8f, 0.5f, 1.3f, 0.5f));      // détente
		Bone butt = m.bone("butt", "gun", 0, 0, 0, 10, 0, 0);
		butt.add(Box.of(Mat.WOOD, -1.5f, -2.6f, -0.5f, 3, 4.2f, 9));             // crosse
		butt.add(Box.of(Mat.WOOD, -1.5f, -5.5f, 5, 3, 3.2f, 4.5f));              // talon
		butt.add(Box.of(Mat.BRASS, -1.6f, -5.7f, 9.2f, 3.2f, 7.5f, 0.6f));        // plaque de couche
		Bone hammer = m.bone("hammer", "gun", 1.3f, 1, -2.5f);
		hammer.add(Box.of(Mat.STEEL, 0.95f, 0.7f, -3.9f, 0.7f, 3.4f, 1.4f).rotated(1.3f, 1, -2.5f, 15, 0, 0));
		Bone ramrod = m.bone("ramrod", "gun", 0, -1, -24);
		ramrod.add(Box.of(Mat.IRON, -0.25f, -2.3f, -25, 0.5f, 0.5f, 20));
		optic(m, 2.7f, -8, 6);
		return m;
	}

	static Model arquebuse() {
		Model m = new Model("arquebuse", 128, 64);
		m.bone("root", null, 0, 0, 0);
		addArms(m);
		Bone gun = m.bone("gun", "root", 0, 0, 0);
		gun.add(Box.of(Mat.STEEL, -1, 0.25f, -21, 2, 2, 17));                    // canon
		gun.add(Box.of(Mat.BRASS, -1.25f, 0, -20.5f, 2.5f, 2.5f, 1));
		gun.add(Box.of(Mat.WOOD_DARK, -1.25f, -1.9f, -17, 2.5f, 2.4f, 13));     // fût court
		gun.add(Box.of(Mat.BRASS, -1.7f, -1, -4.2f, 3.4f, 3.6f, 5));             // boîtier de culasse
		for (int i = 0; i < 4; i++) gun.add(Box.of(Mat.STEEL, -1.4f + i * 0.9f, 2.7f, -3.5f, 0.6f, 0.4f, 3.2f)); // 4 chambres visibles
		gun.add(Box.of(Mat.BRASS, -0.5f, -3.6f, -3, 1, 0.6f, 4));
		gun.add(Box.of(Mat.STEEL, -0.25f, -3.1f, -1.3f, 0.5f, 1.3f, 0.5f));
		gun.add(Box.of(Mat.BRASS, 1.7f, -0.2f, -2.5f, 0.5f, 0.5f, 3));           // levier latéral
		Bone breech = m.bone("breech", "gun", 0, 2.6f, 0.8f);
		breech.add(Box.of(Mat.BRASS, -1.75f, 2.4f, -4.3f, 3.5f, 0.7f, 5.1f));    // trappe de culasse
		Bone butt = m.bone("butt", "gun", 0, 0, 0, 9, 0, 0);
		butt.add(Box.of(Mat.WOOD_DARK, -1.5f, -2.6f, 0, 3, 4, 8));
		butt.add(Box.of(Mat.WOOD_DARK, -1.5f, -5.2f, 4.5f, 3, 3, 3.5f));
		butt.add(Box.of(Mat.BRASS, -1.6f, -5.4f, 7.8f, 3.2f, 7, 0.6f));
		Bone hammer = m.bone("hammer", "gun", 1.6f, 1.2f, -1.5f);
		hammer.add(Box.of(Mat.STEEL, 1.5f, 0.9f, -2.8f, 0.7f, 3.2f, 1.3f).rotated(1.6f, 1.2f, -1.5f, 15, 0, 0));
		optic(m, 3.6f, -9, 6);
		return m;
	}

	static Model tromblon() {
		Model m = new Model("tromblon", 128, 64);
		m.bone("root", null, 0, 0, 0);
		addArms(m);
		Bone gun = m.bone("gun", "root", 0, 0, 0);
		Bone barrel = m.bone("barrel", "gun", 0, 0.5f, -2);
		barrel.add(Box.of(Mat.IRON, -1.1f, 0.2f, -9, 2.2f, 2.2f, 7));
		barrel.add(Box.of(Mat.IRON, -1.6f, -0.3f, -14, 3.2f, 3.2f, 5));
		barrel.add(Box.of(Mat.BRASS, -2.4f, -1.1f, -18, 4.8f, 4.8f, 4));         // bouche évasée
		barrel.add(Box.of(Mat.BRASS, -2.8f, -1.5f, -18.4f, 5.6f, 5.6f, 0.6f));
		barrel.add(Box.of(Mat.WOOD, -1.35f, -1.9f, -12, 2.7f, 2.3f, 10));       // fût
		barrel.add(Box.of(Mat.BRASS, -1.45f, -2, -6, 2.9f, 4.3f, 1));
		gun.add(Box.of(Mat.BRASS, -1.7f, -1.2f, -2.5f, 3.4f, 3.6f, 3.5f));       // bascule
		gun.add(Box.of(Mat.BRASS, -0.7f, -3.8f, -2.5f, 1.4f, 0.6f, 4));
		gun.add(Box.of(Mat.STEEL, -0.6f, -3.3f, -1.2f, 0.4f, 1.3f, 0.5f));
		gun.add(Box.of(Mat.STEEL, 0.2f, -3.3f, -1.2f, 0.4f, 1.3f, 0.5f));
		Bone butt = m.bone("butt", "gun", 0, 0, 0, 11, 0, 0);
		butt.add(Box.of(Mat.WOOD, -1.5f, -2.6f, 0.5f, 3, 4, 7.5f));
		butt.add(Box.of(Mat.WOOD, -1.5f, -5.2f, 4.5f, 3, 3, 3.5f));
		butt.add(Box.of(Mat.BRASS, -1.6f, -5.4f, 7.8f, 3.2f, 7, 0.6f));
		Bone hl = m.bone("hammer_left", "gun", -1.3f, 1.4f, -0.5f);
		hl.add(Box.of(Mat.STEEL, -1.65f, 1.1f, -1.7f, 0.7f, 3, 1.3f).rotated(-1.3f, 1.4f, -0.5f, 15, 0, 0));
		Bone hr = m.bone("hammer_right", "gun", 1.3f, 1.4f, -0.5f);
		hr.add(Box.of(Mat.STEEL, 0.95f, 1.1f, -1.7f, 0.7f, 3, 1.3f).rotated(1.3f, 1.4f, -0.5f, 15, 0, 0));
		optic(m, 3.6f, -6, 5);
		return m;
	}

	static Model rouages() {
		Model m = new Model("fusil_rouages", 128, 64);
		m.bone("root", null, 0, 0, 0);
		addArms(m);
		Bone gun = m.bone("gun", "root", 0, 0, 0);
		gun.add(Box.of(Mat.STEEL, -1, 0.25f, -25, 2, 2, 19));                    // canon
		gun.add(Box.of(Mat.BRASS, -1.4f, -0.2f, -24.5f, 2.8f, 2.8f, 1.2f));
		gun.add(Box.of(Mat.STEEL, -0.6f, -1.2f, -22, 1.2f, 1.2f, 15));           // tube de ressort sous le canon
		gun.add(Box.of(Mat.WOOD_DARK, -1.25f, -2.4f, -15, 2.5f, 2, 9));          // garde-main
		gun.add(Box.of(Mat.BRASS, -2, -1.2f, -6.5f, 4, 5, 7.5f));                // boîtier à rouages
		gun.add(Box.of(Mat.BRONZE, 2, 0.2f, -5.5f, 0.5f, 3, 3));                 // engrenage latéral
		gun.add(Box.of(Mat.BRONZE, 2.5f, 1.2f, -4.5f, 0.4f, 1, 1));
		gun.add(Box.of(Mat.BRONZE, -2.5f, 0.2f, -5.5f, 0.5f, 3, 3));
		gun.add(Box.of(Mat.STEEL, 2.4f, -0.9f, -3, 0.5f, 0.5f, 2.6f));           // manivelle d'armement
		gun.add(Box.of(Mat.BRASS, -0.5f, -3.6f, -3, 1, 0.6f, 4));
		gun.add(Box.of(Mat.STEEL, -0.25f, -3.1f, -1.3f, 0.5f, 1.3f, 0.5f));
		Bone mag = m.bone("magazine", "gun", -3, 4, -4.5f);
		mag.add(Box.of(Mat.BRASS, -7, 3.8f, -5.8f, 9, 1.6f, 2.6f));              // magasin harmonica
		for (int i = 0; i < 8; i++) mag.add(Box.of(Mat.STEEL, -6.6f + i * 1.1f, 5.4f, -5.2f, 0.6f, 0.5f, 1.4f));
		Bone butt = m.bone("butt", "gun", 0, 0, 0, 9, 0, 0);
		butt.add(Box.of(Mat.WOOD_DARK, -1.5f, -2.6f, 0.5f, 3, 4, 7.5f));
		butt.add(Box.of(Mat.WOOD_DARK, -1.5f, -5.2f, 4.5f, 3, 3, 3.5f));
		butt.add(Box.of(Mat.BRASS, -1.6f, -5.4f, 7.8f, 3.2f, 7, 0.6f));
		optic(m, 3.2f, -10, 6);
		return m;
	}

	static Model canonMain() {
		Model m = new Model("canon_main", 128, 64);
		m.bone("root", null, 0, 0, 0);
		addArms(m);
		Bone gun = m.bone("gun", "root", 0, 0, 0);
		gun.add(Box.of(Mat.IRON, -2, -0.5f, -15, 4, 4, 13));                     // tube de fonte
		gun.add(Box.of(Mat.IRON, -2.5f, -1, -14.5f, 5, 5, 1.2f));                 // bourrelet de bouche
		gun.add(Box.of(Mat.IRON, -2.5f, -1, -8, 5, 5, 1.2f));                     // renfort
		gun.add(Box.of(Mat.IRON, -2.5f, -1, -3, 5, 5, 1.2f));
		gun.add(Box.of(Mat.BRONZE, -0.4f, 3.4f, -4, 0.8f, 0.8f, 1));             // lumière
		gun.add(Box.of(Mat.WOOD, -1.1f, -0.3f, -3, 2.2f, 2.2f, 21));             // hampe
		gun.add(Box.of(Mat.LEATHER, -1.25f, -0.45f, 4, 2.5f, 2.5f, 3));          // poignée de cuir
		gun.add(Box.of(Mat.IRON, -1.25f, -0.45f, 16, 2.5f, 2.5f, 1.5f));         // embout
		gun.add(Box.of(Mat.BRASS, -0.5f, -3.4f, -3, 1, 0.6f, 4));
		gun.add(Box.of(Mat.STEEL, -0.25f, -2.9f, -1.3f, 0.5f, 1.3f, 0.5f));
		Bone hammer = m.bone("hammer", "gun", 1.9f, 3.2f, -3.5f);
		hammer.add(Box.of(Mat.STEEL, 1.7f, 3, -5, 0.6f, 3.4f, 1.4f).rotated(1.9f, 3.2f, -3.5f, 15, 0, 0)); // serpentin porte-mèche
		hammer.add(Box.of(Mat.LEATHER, 1.6f, 5.9f, -5.3f, 0.8f, 0.8f, 2));
		optic(m, 4.6f, -9, 6);
		return m;
	}

	// ------------------------------------------------------------------ textures 2D
	static BufferedImage img(int w, int h) { return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB); }

	static void px(BufferedImage i, int x, int y, int rgb) {
		if (x >= 0 && y >= 0 && x < i.getWidth() && y < i.getHeight()) i.setRGB(x, y, 0xFF000000 | rgb);
	}

	static void disc(BufferedImage i, float cx, float cy, float r, int fill, int edge) {
		for (int y = 0; y < i.getHeight(); y++) for (int x = 0; x < i.getWidth(); x++) {
			double d = Math.hypot(x + 0.5 - cx, y + 0.5 - cy);
			if (d <= r - 1) px(i, x, y, fill);
			else if (d <= r) px(i, x, y, edge);
		}
	}

	static void rect(BufferedImage i, int x0, int y0, int w, int h, int fill, int edge) {
		for (int y = y0; y < y0 + h; y++) for (int x = x0; x < x0 + w; x++) {
			boolean e = x == x0 || y == y0 || x == x0 + w - 1 || y == y0 + h - 1;
			px(i, x, y, e ? edge : fill);
		}
	}

	static void save(BufferedImage i, String rel) throws Exception {
		Path p = root.resolve(rel);
		Files.createDirectories(p.getParent());
		ImageIO.write(i, "png", p.toFile());
	}

	/** Icône 2D 16×16 par nom, dessinée en primitives : chaque objet a sa silhouette. */
	static void icon(String name) throws Exception {
		BufferedImage i = img(16, 16);
		switch (name) {
			case "balle" -> { disc(i, 8, 8, 4.5f, 0x8C8F94, 0x4F5257); px(i, 6, 6, 0xC9CCD1); px(i, 7, 6, 0xC9CCD1); px(i, 6, 7, 0xC9CCD1); }
			case "balle_argent" -> { disc(i, 8, 8, 4.5f, 0xE6ECF2, 0x8C99A8); px(i, 6, 6, 0xFFFFFF); px(i, 7, 6, 0xFFFFFF); px(i, 6, 7, 0xFFFFFF); }
			case "balle_aetherium" -> { disc(i, 8, 8, 4.5f, 0x7FD9FF, 0x2E7FA8); disc(i, 8, 8, 2f, 0xE8FBFF, 0xB0EEFF); px(i, 3, 3, 0xBFF0FF); px(i, 12, 12, 0xBFF0FF); px(i, 12, 3, 0xBFF0FF); px(i, 3, 12, 0xBFF0FF); }
			case "canon_raye" -> { rect(i, 2, 6, 12, 4, 0x6E7378, 0x474C50); for (int x = 3; x < 13; x += 2) px(i, x, 7 + (x / 2) % 2, 0x9DA3A8); rect(i, 12, 5, 3, 6, 0xB8862B, 0x85601C); }
			case "canon_evase" -> { rect(i, 1, 6, 8, 4, 0x6E7378, 0x474C50); rect(i, 9, 4, 3, 8, 0xB8862B, 0x85601C); rect(i, 12, 2, 3, 12, 0xE3B94F, 0x85601C); }
			case "canon_lourd" -> { rect(i, 1, 5, 14, 6, 0x3F4A57, 0x28313B); rect(i, 4, 4, 2, 8, 0x6C7C8C, 0x28313B); rect(i, 10, 4, 2, 8, 0x6C7C8C, 0x28313B); }
			case "canon_long" -> { rect(i, 0, 7, 16, 3, 0x6E7378, 0x474C50); rect(i, 13, 6, 3, 5, 0xB8862B, 0x85601C); rect(i, 0, 6, 2, 5, 0xB8862B, 0x85601C); }
			case "longue_vue" -> { rect(i, 1, 6, 6, 4, 0xB8862B, 0x85601C); rect(i, 7, 5, 5, 6, 0xB8862B, 0x85601C); rect(i, 12, 4, 3, 8, 0xE3B94F, 0x85601C); px(i, 13, 6, 0x9FD8E8); px(i, 13, 7, 0xE4F7FC); px(i, 13, 8, 0x9FD8E8); px(i, 13, 9, 0x5FA3B8); }
			case "detente_legere" -> { rect(i, 5, 2, 6, 3, 0x6E7378, 0x474C50); rect(i, 7, 5, 2, 6, 0x9DA3A8, 0x474C50); rect(i, 5, 10, 6, 4, 0xB8862B, 0x85601C); px(i, 8, 11, 0xE3B94F); }
			case "event_vapeur" -> { rect(i, 3, 8, 10, 6, 0xB8862B, 0x85601C); for (int k = 0; k < 4; k++) { px(i, 5 + k * 2, 5 - (k % 2), 0xDDE6EE); px(i, 5 + k * 2, 3 - (k % 2), 0xF2F6FA); px(i, 6 + k * 2, 4, 0xC8D2DB); } rect(i, 6, 6, 4, 2, 0x85601C, 0x5C4213); }
			case "huile_armurier" -> { rect(i, 6, 1, 4, 3, 0x5A3A22, 0x3C2615); rect(i, 4, 4, 8, 10, 0x3F4A57, 0x28313B); rect(i, 6, 7, 4, 4, 0xE3B94F, 0xB8862B); px(i, 5, 5, 0x6C7C8C); px(i, 5, 6, 0x6C7C8C); }
			case "ressort_amortisseur" -> { for (int y = 2; y < 14; y++) { int x = 6 + (int) Math.round(3 * Math.sin(y * 1.2)); px(i, x, y, 0x9DA3A8); px(i, x + 1, y, 0x6E7378); px(i, x + 2, y, 0x474C50); } rect(i, 4, 1, 8, 2, 0x6E7378, 0x474C50); rect(i, 4, 13, 8, 2, 0x6E7378, 0x474C50); }
			case "chambre_vent" -> { disc(i, 8, 8, 6, 0xB8862B, 0x85601C); disc(i, 8, 8, 3.5f, 0xDDE6EE, 0x9DA3A8); px(i, 8, 5, 0xE3B94F); px(i, 8, 11, 0xE3B94F); px(i, 5, 8, 0xE3B94F); px(i, 11, 8, 0xE3B94F); }
			case "repeteur_rouages" -> { gear(i, 5, 6, 4, 0xB8862B, 0x85601C); gear(i, 11, 10, 3, 0x8C6A3C, 0x5F4626); }
			case "accelerateur_volant" -> { disc(i, 8, 8, 6.5f, 0x8C6A3C, 0x5F4626); disc(i, 8, 8, 5, 0xB58E55, 0x8C6A3C); disc(i, 8, 8, 1.5f, 0x3F4A57, 0x28313B); for (int k = 0; k < 4; k++) { px(i, 8 + (k == 0 ? 3 : k == 1 ? -3 : 0), 8 + (k == 2 ? 3 : k == 3 ? -3 : 0), 0x5F4626); } }
			case "double_chambre" -> { rect(i, 2, 4, 5, 9, 0x6E7378, 0x474C50); rect(i, 9, 4, 5, 9, 0x6E7378, 0x474C50); rect(i, 3, 2, 3, 3, 0xB8862B, 0x85601C); rect(i, 10, 2, 3, 3, 0xB8862B, 0x85601C); px(i, 4, 8, 0x9DA3A8); px(i, 11, 8, 0x9DA3A8); }
			case "culasse_runique" -> { rect(i, 3, 3, 10, 10, 0x3F4A57, 0x28313B); for (int k = 0; k < 5; k++) px(i, 5 + k, 8 - (k % 3), 0x7FD9FF); px(i, 8, 5, 0xBFF0FF); px(i, 8, 11, 0xBFF0FF); px(i, 6, 10, 0x7FD9FF); px(i, 10, 10, 0x7FD9FF); }
			default -> rect(i, 2, 2, 12, 12, 0xFF00FF, 0x000000);
		}
		save(i, "assets/" + NS + "/textures/item/" + name + ".png");
	}

	static void gear(BufferedImage i, float cx, float cy, float r, int fill, int edge) {
		disc(i, cx, cy, r, fill, edge);
		for (int k = 0; k < 8; k++) {
			double a = k * Math.PI / 4;
			px(i, (int) Math.round(cx + Math.cos(a) * (r + 0.6) - 0.5), (int) Math.round(cy + Math.sin(a) * (r + 0.6) - 0.5), edge);
		}
		disc(i, cx, cy, r * 0.35f, edge, edge);
	}

	/** Flash de bouche : trois images de plus en plus ternes, forme large / triangle / étoile, 32×32. */
	static void flashFrames(String kind) throws Exception {
		for (int frame = 1; frame <= 3; frame++) {
			BufferedImage i = img(32, 32);
			float fade = frame == 1 ? 1f : frame == 2 ? 0.7f : 0.4f;
			Random rnd = new Random(kind.hashCode() * 31 + frame);
			for (int y = 0; y < 32; y++) for (int x = 0; x < 32; x++) {
				double dx = x + 0.5 - 16, dy = y + 0.5 - 16;
				double d = Math.hypot(dx, dy) / 16.0;
				double ang = Math.atan2(dy, dx);
				double shape;
				switch (kind) {
					case "flash_triangle" -> shape = 0.55 + 0.45 * Math.abs(Math.cos(ang * 1.5));
					case "flash_etoile" -> shape = 0.45 + 0.55 * Math.abs(Math.cos(ang * 3));
					default -> shape = 0.85 + 0.15 * Math.cos(ang * 5);
				}
				double v = shape * fade - d * 1.1 + rnd.nextDouble() * 0.12;
				if (v <= 0.05) continue;
				int a = (int) Math.min(255, v * 420);
				int rgb;
				if (v > 0.55) rgb = 0xFFF6D6; else if (v > 0.35) rgb = 0xFFC44A; else rgb = 0xE0582A;
				i.setRGB(x, y, (a << 24) | rgb);
			}
			save(i, "assets/" + NS + "/textures/particle/" + kind + "_" + frame + ".png");
		}
	}

	static void trailTexture() throws Exception {
		BufferedImage i = img(8, 8);
		for (int y = 0; y < 8; y++) for (int x = 0; x < 8; x++) {
			double d = Math.hypot(x + 0.5 - 4, y + 0.5 - 4) / 4.0;
			int a = (int) Math.max(0, Math.min(255, (1.0 - d) * 300));
			i.setRGB(x, y, (a << 24) | 0xFFFFFF);
		}
		save(i, "assets/" + NS + "/textures/particle/trainee.png");
	}

	static void guiTextures() throws Exception {
		BufferedImage b = img(16, 16);
		disc(b, 8, 8, 4.5f, 0xD9D2C5, 0x8A8378);
		px(b, 6, 6, 0xFFFFFF); px(b, 7, 6, 0xFFFFFF); px(b, 6, 7, 0xFFFFFF);
		save(b, "assets/" + NS + "/textures/gui/balle_icone.png");

		BufferedImage p = img(256, 256);
		int W = 176, H = 166;
		for (int y = 0; y < H; y++) for (int x = 0; x < W; x++) {
			int c = 0xC6C6C6;
			if (x == 0 || y == 0) c = 0xFFFFFF;
			if (x == W - 1 || y == H - 1) c = 0x555555;
			if ((x == 1 || y == 1) && x < W - 1 && y < H - 1) c = 0xDBDBDB;
			if ((x == W - 2 || y == H - 2) && x > 0 && y > 0) c = 0x8B8B8B;
			p.setRGB(x, y, 0xFF000000 | c);
		}
		int[][] parts = {{44, 35}, {80, 35}, {116, 35}};
		int[] tints = {0x9C7A47, 0x6C7C8C, 0x7FB4C8};
		for (int k = 0; k < 3; k++) {
			int sx = parts[k][0] - 1, sy = parts[k][1] - 1;
			rect(p, sx - 3, sy - 3, 24, 24, 0xB5A98F, tints[k]);
			slot(p, sx, sy);
		}
		for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++) slot(p, 7 + col * 18, 83 + row * 18);
		for (int col = 0; col < 9; col++) slot(p, 7 + col * 18, 141);
		rect(p, 148, 3, 24, 24, 0xB5A98F, 0x8A6420);
		for (int x = 8; x < 168; x++) p.setRGB(x, 66, 0xFF8B8B8B);
		save(p, "assets/" + NS + "/textures/gui/atelier.png");

		BufferedImage icon = img(64, 64);
		for (int y = 0; y < 64; y++) for (int x = 0; x < 64; x++) icon.setRGB(x, y, 0xFF1B1F24);
		rect(icon, 6, 28, 46, 8, 0x6E7378, 0x474C50);
		rect(icon, 48, 26, 10, 12, 0xB8862B, 0x85601C);
		rect(icon, 4, 36, 22, 8, 0x6B4423, 0x4A2E17);
		rect(icon, 2, 44, 10, 12, 0x6B4423, 0x4A2E17);
		gear(icon, 44, 48, 7, 0xB8862B, 0x85601C);
		save(icon, "assets/" + NS + "/icon.png");
	}

	static void slot(BufferedImage p, int x, int y) {
		for (int j = 0; j < 18; j++) for (int k = 0; k < 18; k++) {
			int c = 0x8B8B8B;
			if (j == 0 || k == 0) c = 0x373737;
			else if (j == 17 || k == 17) c = 0xFFFFFF;
			p.setRGB(x + k, y + j, 0xFF000000 | c);
		}
	}

	// ------------------------------------------------------------------ main
	public static void main(String[] args) throws Exception {
		root = Path.of(args.length > 0 ? args[0] : "src/main/resources");
		Files.createDirectories(root);

		Model p = pistolet(); writeModel(p);
		writeAnimations("pistolet_silex", idle(false, true, 0), fire(0.55f, 4f, 14f, true, false, 0), reloadMuzzle(2.0f, false, 0, -13), equip(0.85f, true, false, 0));

		Model mu = mousquet(); writeModel(mu);
		writeAnimations("mousquet", idle(true, true, -16), fire(0.8f, 6f, 12f, true, true, -16), reloadMuzzle(3.0f, true, -16, -26), equip(1.3f, true, true, -16));

		Model ar = arquebuse(); writeModel(ar);
		writeAnimations("arquebuse", idle(true, true, -14), fire(0.7f, 4f, 9f, true, true, -14), reloadBreech(2.5f, -14), equip(1.0f, true, true, -14));

		Model tr = tromblon(); writeModel(tr);
		Anim trIdle = idle(true, false, -12).rot("hammer_left", 0, -60, 0, 0).rot("hammer_right", 0, -60, 0, 0);
		Anim trFire = fire(0.8f, 8f, 18f, false, true, -12).rot("hammer_right", 0, 0, 0, 0);
		writeAnimations("tromblon", trIdle, trFire, reloadBreak(1.5f, -12), equip(1.0f, false, true, -12));

		Model ro = rouages(); writeModel(ro);
		writeAnimations("fusil_rouages", idle(true, false, -18), fire(0.35f, 2f, 3f, false, true, -18), reloadMagazine(1.5f, -18), equip(0.75f, false, true, -18));

		Model ca = canonMain(); writeModel(ca);
		writeAnimations("canon_main", idle(true, true, -10), fire(1.0f, 10f, 22f, true, true, -10), reloadMuzzle(4.0f, true, -10, -15), equip(1.4f, true, true, -10));

		for (String n : new String[] {"balle", "balle_argent", "balle_aetherium", "canon_raye", "canon_evase", "canon_lourd", "canon_long", "longue_vue",
				"detente_legere", "event_vapeur", "huile_armurier", "ressort_amortisseur", "chambre_vent", "repeteur_rouages", "accelerateur_volant", "double_chambre", "culasse_runique"}) {
			icon(n);
		}
		flashFrames("flash_large");
		flashFrames("flash_triangle");
		flashFrames("flash_etoile");
		trailTexture();
		guiTextures();
		System.out.println("Assets generes dans " + root.toAbsolutePath());
	}
}
