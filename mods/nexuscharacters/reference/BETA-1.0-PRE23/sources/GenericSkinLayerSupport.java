package net.tompsen.nexuscharacters;

import java.io.InputStream;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Native outer layers built from the skin submitted by Nexus, independently of account skin meshes.
 */
public final class GenericSkinLayerSupport {
  private static final String[] PART_FIELDS = {
    "field_3394", "field_3483", "field_3482", "field_3479", "field_3484", "field_3486"
  };
  private static final String[] FLAGS = {
    "enableHat",
    "enableJacket",
    "enableLeftPants",
    "enableRightPants",
    "enableLeftSleeve",
    "enableRightSleeve"
  };
  private static final String[] OFFSETS = {
    "HEAD",
    "BODY",
    "LEFT_LEG",
    "RIGHT_LEG",
    "LEFT_ARM",
    "RIGHT_ARM",
    "LEFT_ARM_SLIM",
    "RIGHT_ARM_SLIM"
  };
  private static final int[][] SPECS = {
    {8, 8, 8, 32, 0, 0},
    {8, 12, 4, 16, 32, 1},
    {4, 12, 4, 0, 48, 1},
    {4, 12, 4, 0, 32, 1},
    {4, 12, 4, 48, 48, 1},
    {4, 12, 4, 40, 32, 1},
    {3, 12, 4, 48, 48, 1},
    {3, 12, 4, 40, 32, 1}
  };
  private static final int[][] BASE_UV = {
    {0, 0}, {16, 16}, {16, 48}, {0, 16}, {32, 48}, {40, 16}, {32, 48}, {40, 16}
  };
  public static long drawnBase, drawnOuter;
  private static final Map<Object, String> ASSETS =
      Collections.synchronizedMap(new WeakHashMap<>());
  private static final Map<Object, Pending> PENDING =
      Collections.synchronizedMap(new WeakHashMap<>());
  private static final Map<String, Plan> PLANS = new ConcurrentHashMap<>();
  private static final Map<Mask, Object[]> MESHES =
      Collections.synchronizedMap(
          new LinkedHashMap<>(32, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<Mask, Object[]> e) {
              return size() > 128;
            }
          });
  private static final Map<String, Boolean> LOGGED = new ConcurrentHashMap<>();
  private static Method originalOverlay, pixelGet, pixelSet;
  private static Constructor<?> imageCtor;

  private GenericSkinLayerSupport() {}

  /** Called by the patched loader; the ResourceManager honors resource pack overrides. */
  public static Object load(String path) throws Exception {
    InputStream in = null;
    try {
      int cut = path.indexOf("/assets/");
      if (cut >= 0) {
        String rest = path.substring(cut + 8);
        int slash = rest.indexOf('/');
        Object id = identifier(rest.substring(0, slash), rest.substring(slash + 1));
        Object client = client();
        Object rm = client == null ? null : call(client, "method_1478");
        Object opt = rm == null ? Optional.empty() : call(rm, "method_14486", id);
        if (opt instanceof Optional<?> optional && optional.isPresent())
          in = (InputStream) call(optional.get(), "method_14482");
      }
    } catch (ReflectiveOperationException ignored) {
    }
    if (in == null) in = GenericSkinLayerSupport.class.getResourceAsStream(path);
    if (in == null) throw new IllegalStateException("Missing appearance asset " + path);
    Object image;
    try (InputStream stream = in) {
      image =
          Class.forName("net.minecraft.class_1011")
              .getMethod("method_4309", InputStream.class)
              .invoke(null, stream);
    }
    // The inherited compositor uses 64x64 UVs. Normalize HD resource-pack
    // cosmetic overrides before it reads them, rather than reading a corner.
    if (((Number) call(image, "method_4307")).intValue() != 64) {
      Object normalized = newImage(readPixels(image));
      close(image);
      image = normalized;
    }
    ASSETS.put(image, path);
    return image;
  }

  /**
   * Replaces only the compositor's overlay dispatch; unrelated tint modes use its original code.
   */
  public static void overlay(Object output, Object source, int color, Object mode, int dx, int dy) {
    try {
      String path = ASSETS.get(source);
      boolean hair = path != null && path.contains("/hair/");
      boolean beard = path != null && path.contains("/facial_hair/");
      boolean outfit = path != null && path.contains("/outfits/");
      if (!hair && !beard && !outfit) {
        original(output, source, color, mode, dx, dy);
        return;
      }
      if (dx != 0 || dy != 0)
        throw new IllegalArgumentException("Cosmetic UV offsets must be zero");
      int[] raw = readPixels(source);
      // Retain authored outer details and mark promoted lower pixels separately.
      // Their depth is selected per part, without filling upper detail gaps.
      int[] adjusted = GenericLayerPixels.prepareCosmetic(raw, hair, beard);
      Object image = newImage(adjusted);
      try {
        original(output, image, color, mode, 0, 0);
      } finally {
        close(image);
      }
      Pending pending = PENDING.computeIfAbsent(output, k -> new Pending());
      int owner = hair ? 2 : beard ? 3 : 1;
      for (int y = 0; y < 64; y++)
        for (int x = 0; x < 64; x++) {
          int i = y * 64 + x;
          if (GenericLayerPixels.alpha(adjusted[i]) > 0) {
            pending.baseOwner[i] = (byte) owner;
            pending.promoted[i] =
                GenericLayerPixels.isOuter(x, y) && GenericLayerPixels.alpha(raw[i]) == 0;
          }
        }
    } catch (Throwable e) {
      // Original source is still alive and provides a complete flat fallback.
      fail("COMPOSE", e);
      try {
        original(output, source, color, mode, dx, dy);
      } catch (Exception nested) {
        throw new IllegalStateException(nested);
      }
    }
  }

  private static void original(Object out, Object src, int color, Object mode, int dx, int dy)
      throws Exception {
    if (originalOverlay == null) {
      originalOverlay =
          method(
              Class.forName("net.tompsen.nexuscharacters.DynamicAppearanceSupport"),
              "nexus$overlayOriginal",
              6);
    }
    originalOverlay.invoke(null, out, src, color, mode, dx, dy);
  }

  /** Called once after composition, before the image is handed to TextureManager. */
  public static Object complete(Object image, String marker) {
    try {
      Pending pending = PENDING.remove(image);
      int[] pixels = readPixels(image);
      PLANS.put("nexuscharacters:dynamic/appearance/" + marker, new Plan(pixels, pending));
    } catch (Throwable e) {
      fail("SNAPSHOT", e);
    }
    return image;
  }

  public static void preview(Object widget, Object dto) {
    if (widget == null || dto == null) return;
    try {
      Object skin =
          callStatic("net.tompsen.nexuscharacters.PreviewDummyPlayerManager", "skin", dto);
      String key = textureKey(skin);
      Plan plan = plan(key);
      if (plan == null) {
        clearModel(field(widget, "field_59834"));
        clearModel(field(widget, "field_59835"));
        return;
      }
      apply(field(widget, "field_59834"), plan, false, false);
      apply(field(widget, "field_59835"), plan, true, false);
      ready("preview|" + key, "preview", plan);
    } catch (Throwable e) {
      fail("PREVIEW", e);
    }
  }

  /** PlayerModel setupAnim tail, after Skin Layers has applied its own LOD/config checks. */
  public static void player(Object model, Object state) {
    if (model == null || state == null) return;
    try {
      if (Class.forName("net.minecraft.class_9950").isInstance(model)) return;
      String key = textureKey(field(state, "field_53520"));
      Plan plan = PLANS.get(key);
      if (plan == null) {
        PosedSurfaceSupport.clear(model);
        LowerJointSupport.apply(model, false);
        return;
      }
      boolean slim = (Boolean) field(model, "field_3480");
      Object entity = call(state, "getTransitionEntity");
      boolean dummy =
          entity != null
              && Class.forName("net.tompsen.nexuscharacters.NexusDummyEntity").isInstance(entity);
      if (dummy) {
        // GUI preview entities live at (0,0,0), not at the camera. They
        // also have no network-populated skin-part visibility flags.
        for (String name : PART_FIELDS)
          field(model, name).getClass().getField("field_3665").setBoolean(field(model, name), true);
      }
      if (!dummy
          && !key.startsWith("nexuscharacters:dynamic/appearance/")
          && !inWorldRange(entity)) {
        PosedSurfaceSupport.clear(model);
        LowerJointSupport.apply(model, false);
        return;
      }
      apply(model, plan, slim, !dummy && hiddenHead(entity));
      ready("world|" + key, "world", plan);
    } catch (Throwable e) {
      fail("PLAYER", e);
    }
  }

  private static Plan plan(String key) throws Exception {
    if (key == null) return null;
    Plan plan = PLANS.get(key);
    if (plan != null) return plan;
    // Account/preset skins also get ordinary outer layer depth, without moving base-face paint.
    Object id = parseIdentifier(key);
    Object image = callStatic("dev.tr7zw.skinlayers.SkinUtil", "getTexture", id, null);
    if (image == null) return null;
    // SkinUtil may return a borrowed TextureManager image; never close it here.
    plan = new Plan(readPixels(image), null);
    PLANS.put(key, plan);
    return plan;
  }

  public static void firstPerson(Object renderer, Object texture, Object arm) {
    try {
      Object model = call(renderer, "method_4038");
      Plan plan = PLANS.get(String.valueOf(texture));
      if (plan == null) {
        PosedSurfaceSupport.clear(model);
        return;
      }
      boolean slim = (Boolean) field(model, "field_3480");
      PosedSurfaceSupport.firstPerson(model, plan.cookedFirstPerson(), slim);
      ready("hand|" + texture, "first-person", plan);
    } catch (Throwable e) {
      fail("FIRST_PERSON", e);
    }
  }

  private static boolean inWorldRange(Object entity) throws Exception {
    Object c = client();
    if (entity == null || field(c, "field_1724") == null) return false;
    Object camera = call(field(c, "field_1773"), "method_19418");
    Object position = call(camera, "method_71156");
    double distance = ((Number) call(entity, "method_5707", position)).doubleValue();
    int lod =
        ((Number)
                field(
                    field(Class.forName("dev.tr7zw.skinlayers.SkinLayersModBase"), "config"),
                    "renderDistanceLOD"))
            .intValue();
    return distance <= (double) lod * lod;
  }

  private static boolean hiddenHead(Object entity) throws Exception {
    Object helmet =
        call(entity, "method_6118", field(Class.forName("net.minecraft.class_1304"), "field_6169"));
    return helmet != null
        && ((Set<?>)
                field(Class.forName("dev.tr7zw.skinlayers.SkinLayersModBase"), "hideHeadLayers"))
            .contains(call(helmet, "method_7909"));
  }

  private static void apply(Object model, Plan plan, boolean slim, boolean skipHead)
      throws Exception {
    LowerJointSupport.apply(model, false);
    PosedSurfaceSupport.apply(model, plan.cooked(), slim, skipHead);
  }

  private static void clearModel(Object model) throws Exception {
    PosedSurfaceSupport.clear(model);
    Method set =
        method(
            Class.forName("dev.tr7zw.skinlayers.accessor.ModelPartInjector"), "setInjectedMesh", 2);
    for (String name : PART_FIELDS) set.invoke(field(model, name), null, null);
  }

  private static int geometryMode() {
    try {
      Object config = field(Class.forName("dev.tr7zw.skinlayers.SkinLayersModBase"), "config");
      return Objects.hash(
          field(config, "irisCompatibilityMode"),
          field(config, "applySodiumWorkaround"),
          field(config, "baseVoxelSize"),
          field(config, "bodyVoxelWidthSize"),
          field(config, "headVoxelSize"),
          field(config, "firstPersonPixelScaling"));
    } catch (Exception ignored) {
      return 0;
    }
  }

  private static Object composite(int index, Object[] layers) throws Exception {
    List<Object> meshes = Arrays.stream(layers).filter(Objects::nonNull).toList();
    return meshes.isEmpty() ? null : NativeVoxelSurface.mesh(NativeVoxelSurface.combine(meshes));
  }

  private static Object[] meshSet(int[] pixels, boolean base) throws Exception {
    return meshSet(pixels, base, 0);
  }

  private static Object[] meshSet(int[] pixels, boolean base, int category) throws Exception {
    return meshSet(pixels, base, category, 255);
  }

  private static Object[] meshSet(int[] pixels, boolean base, int category, int shallowBits)
      throws Exception {
    Mask key = new Mask(pixels, base, category, shallowBits);
    synchronized (MESHES) {
      Object[] cached = MESHES.get(key);
      if (cached != null) return cached;
      Object[] parts = new Object[8];
      if (GenericLayerPixels.count(pixels) > 0) {
        Object image = newImage(pixels);
        try {
          for (int i = 0; i < 8; i++)
            if (surfacePixels(pixels, SPECS[i]) > 0)
              parts[i] =
                  NativeVoxelSurface.mesh(
                      NativeVoxelSurface.build(
                          (net.minecraft.class_1011) image,
                          i,
                          category / 2 + 1,
                          category % 2 == 0,
                          (shallowBits & (1 << i)) != 0));
        } finally {
          close(image);
        }
      }
      MESHES.put(key, parts);
      return parts;
    }
  }

  private static int surfacePixels(int[] pixels, int[] s) {
    int w = s[0], h = s[1], d = s[2], u = s[3], v = s[4], count = 0;
    for (int y = v; y < v + d + h; y++)
      for (int x = u; x < u + 2 * d + 2 * w; x++) {
        if (y < v + d && (x < u + d || x >= u + d + 2 * w)) continue;
        if (GenericLayerPixels.alpha(pixels[y * 64 + x]) > 0) count++;
      }
    return count;
  }

  public static void clearCaches() {
    PLANS.clear();
    MESHES.clear();
    ASSETS.clear();
    PENDING.clear();
    LOGGED.clear();
    NativeVoxelSurface.clear();
    DynamicAssetCatalog.clear();
    try {
      Class<?> c = Class.forName("net.tompsen.nexuscharacters.DynamicAppearanceSupport");
      for (String f : new String[] {"SKINS", "IDS"}) ((Map<?, ?>) field(c, f)).clear();
    } catch (Exception e) {
      fail("RELOAD", e);
    }
  }

  private static String textureKey(Object skin) throws Exception {
    if (skin == null) return null;
    Object ref = call(skin, "comp_1626");
    return String.valueOf(
        ref.getClass().getName().equals("net.minecraft.class_2960") ? ref : call(ref, "comp_3627"));
  }

  private static int[] readPixels(Object image) throws Exception {
    initPixels();
    int w = ((Number) call(image, "method_4307")).intValue(),
        h = ((Number) call(image, "method_4323")).intValue();
    if (w < 64 || h != w || w % 64 != 0)
      throw new IllegalArgumentException("Unsupported skin size " + w + "x" + h);
    int[] result = new int[4096];
    int scale = w / 64;
    for (int y = 0; y < 64; y++)
      for (int x = 0; x < 64; x++)
        result[y * 64 + x] = ((Number) pixelGet.invoke(image, x * scale, y * scale)).intValue();
    return result;
  }

  private static Object newImage(int[] pixels) throws Exception {
    initPixels();
    Object image = imageCtor.newInstance(64, 64, false);
    for (int y = 0; y < 64; y++)
      for (int x = 0; x < 64; x++) pixelSet.invoke(image, x, y, pixels[y * 64 + x]);
    return image;
  }

  private static synchronized void initPixels() throws Exception {
    if (imageCtor != null) return;
    Class<?> c = Class.forName("net.minecraft.class_1011");
    pixelGet = c.getMethod("method_61940", int.class, int.class);
    pixelSet = c.getMethod("method_61941", int.class, int.class, int.class);
    imageCtor = c.getConstructor(int.class, int.class, boolean.class);
  }

  private static Object identifier(String ns, String path) throws Exception {
    return Class.forName("net.minecraft.class_2960")
        .getMethod("method_60655", String.class, String.class)
        .invoke(null, ns, path);
  }

  private static Object parseIdentifier(String value) throws Exception {
    int i = value.indexOf(':');
    return identifier(value.substring(0, i), value.substring(i + 1));
  }

  private static Object client() throws Exception {
    return callStatic("net.minecraft.class_310", "method_1551");
  }

  private static void close(Object object) {
    try {
      call(object, "close");
    } catch (Exception ignored) {
    }
  }

  private static Object callStatic(String type, String name, Object... args) throws Exception {
    return method(Class.forName(type), name, args.length).invoke(null, args);
  }

  private static Object call(Object object, String name, Object... args) throws Exception {
    return method(object.getClass(), name, args.length).invoke(object, args);
  }

  private static Method method(Class<?> type, String name, int argc) throws NoSuchMethodException {
    for (Class<?> c = type; c != null; c = c.getSuperclass())
      for (Method m : c.getDeclaredMethods())
        if (m.getName().equals(name) && m.getParameterCount() == argc) {
          m.setAccessible(true);
          return m;
        }
    for (Method m : type.getMethods())
      if (m.getName().equals(name) && m.getParameterCount() == argc) {
        m.setAccessible(true);
        return m;
      }
    throw new NoSuchMethodException(type.getName() + "." + name + "/" + argc);
  }

  private static Object field(Object object, String name) throws Exception {
    Class<?> type = object instanceof Class<?> c ? c : object.getClass();
    for (Class<?> c = type; c != null; c = c.getSuperclass())
      try {
        Field f = c.getDeclaredField(name);
        f.setAccessible(true);
        return f.get(object instanceof Class<?> ? null : object);
      } catch (NoSuchFieldException ignored) {
      }
    throw new NoSuchFieldException(type.getName() + "." + name);
  }

  private static Object objectMethod(Object p, Method m, Object[] a, String label) {
    return switch (m.getName()) {
      case "toString" -> label;
      case "hashCode" -> System.identityHashCode(p);
      case "equals" -> p == (a == null ? null : a[0]);
      default -> null;
    };
  }

  private static void ready(String key, String path, Plan p) {
    if (LOGGED.putIfAbsent(key, true) == null)
      System.out.println(
          "[NexusCharacters][NativeLayers23] PREPARED path="
              + path
              + " basePaint="
              + GenericLayerPixels.count(p.base)
              + " outerVoxels="
              + GenericLayerPixels.count(p.outer)
              + " hair="
              + p.hairCount
              + " classicBeard="
              + p.beardCount);
  }

  private static void fail(String stage, Throwable e) {
    String key = stage + "|" + e;
    if (LOGGED.putIfAbsent(key, true) == null) {
      System.err.println("[NexusCharacters][NativeLayers23] FAIL stage=" + stage + " " + e);
      e.printStackTrace();
    }
  }

  private static final class Pending {
    final byte[] baseOwner = new byte[4096];
    final boolean[] promoted = new boolean[4096];
  }

  private static final class Plan {
    final int[] base, outer;
    final int[][] layers = new int[6][4096];
    final int hairCount, beardCount;
    private Object[] cooked, firstPersonCooked;
    private int mode;

    Plan(int[] pixels, Pending pending) {
      base = new int[4096];
      outer = new int[4096];
      int hc = 0, bc = 0;
      for (int y = 0; y < 64; y++)
        for (int x = 0; x < 64; x++) {
          int i = y * 64 + x;
          if (GenericLayerPixels.isOuter(x, y)) {
            outer[i] = pixels[i];
            int owner = pending == null ? 1 : pending.baseOwner[i];
            layers[Math.max(0, owner - 1) * 2 + (pending != null && pending.promoted[i] ? 0 : 1)][
                    i] =
                pixels[i];
          } else if (pending != null && pending.baseOwner[i] != 0) {
            base[i] = pixels[i];
            if (pending.baseOwner[i] == 2) hc++;
            if (pending.baseOwner[i] == 3) bc++;
          }
        }
      hairCount = hc;
      beardCount = bc;
    }

    synchronized Object[] cooked() throws Exception {
      int currentMode = geometryMode();
      if (cooked != null && mode == currentMode) return cooked;
      Object[][] o = new Object[6][];
      for (int c = 0; c < 6; c++) {
        int bits = 0;
        if (c % 2 == 0)
          for (int p = 0; p < 8; p++)
            if (surfacePixels(layers[c + 1], SPECS[p]) > 0) bits |= 1 << p;
        o[c] = meshSet(layers[c], false, c, bits);
      }
      Object[] out = new Object[8];
      for (int i = 0; i < 8; i++)
        out[i] = composite(i, new Object[] {o[0][i], o[1][i], o[2][i], o[3][i], o[4][i], o[5][i]});
      cooked = out;
      firstPersonCooked = null;
      mode = currentMode;
      return out;
    }

    synchronized Object[] cookedFirstPerson() throws Exception {
      Object[] normal = cooked();
      if (firstPersonCooked != null) return firstPersonCooked;
      firstPersonCooked = normal.clone();
      Object config =
          field(
              field(Class.forName("dev.tr7zw.skinlayers.SkinLayersModBase"), "config"),
              "firstPersonPixelScaling");
      float first = ((Number) config).floatValue(),
          third =
              ((Number)
                      field(
                          field(Class.forName("dev.tr7zw.skinlayers.SkinLayersModBase"), "config"),
                          "baseVoxelSize"))
                  .floatValue();
      org.joml.Matrix4f scale = new org.joml.Matrix4f().scale(first / third, 1, first / third);
      for (int p = 4; p < 8; p++) {
        Object template = normal[p];
        if (template == null) continue;
        NativeVoxelSurface.Shape shape = NativeVoxelSurface.SHAPES.get(template);
        List<NativeVoxelSurface.Bounds> boxes = new ArrayList<>();
        for (NativeVoxelSurface.Bounds b : shape.boxes()) {
          org.joml.Vector3f lo = scale.transformPosition(new org.joml.Vector3f(b.lo())),
              hi = scale.transformPosition(new org.joml.Vector3f(b.hi()));
          boxes.add(
              new NativeVoxelSurface.Bounds(
                  new float[] {lo.x, lo.y, lo.z}, new float[] {hi.x, hi.y, hi.z}, b.rank()));
        }
        firstPersonCooked[p] =
            NativeVoxelSurface.mesh(
                new NativeVoxelSurface.Shape(
                    SurfaceGeometry.transform(shape.faces(), scale), List.copyOf(boxes)));
      }
      return firstPersonCooked;
    }
  }

  private static final class Mask {
    final byte[] alpha = new byte[4096];
    final int hash;
    final int mode = geometryMode();
    final boolean base;
    final int category, shallowBits;

    Mask(int[] pixels, boolean base, int category, int shallowBits) {
      this.base = base;
      this.category = category;
      this.shallowBits = shallowBits;
      for (int i = 0; i < 4096; i++) alpha[i] = (byte) (pixels[i] >>> 24);
      hash =
          31 * Arrays.hashCode(alpha)
              + mode
              + (base ? 127 : 0)
              + category * 131
              + shallowBits * 127;
    }

    public int hashCode() {
      return hash;
    }

    public boolean equals(Object other) {
      return other instanceof Mask m
          && base == m.base
          && mode == m.mode
          && category == m.category
          && shallowBits == m.shallowBits
          && Arrays.equals(alpha, m.alpha);
    }
  }
}
