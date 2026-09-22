import net.minecraft.class_2960;

public class TestIcons {
  public static void main(String[] args) throws Exception {
    var fire = new class_2960("wizards","fireball");
    var witch = new class_2960("witcher_rpg","rend_boost_a");
    var unknown = new class_2960("foo","bar");
    for (var x: new class_2960[]{fire,witch,unknown}) {
      var r = net.spell_engine.client.util.SpellRender.iconTexture(x);
      System.out.println(x+" -> "+r);
    }
  }
}
