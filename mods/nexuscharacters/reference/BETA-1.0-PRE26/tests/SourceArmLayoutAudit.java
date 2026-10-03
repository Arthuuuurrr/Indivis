import java.util.*;
import net.minecraft.class_1011;
import net.tompsen.nexuscharacters.*;

/** Exercise resource sidecars for new body, sparse slim outfit and classic outfit. */
public final class SourceArmLayoutAudit {
  public static void run()throws Exception {
    String[] paths={"bodies/body_future_slim.png","outfits/outfit_future_sparse.png","outfits/outfit_future_classic.png"};
    int[] widths={3,3,4};
    for(int k=0;k<paths.length;k++) {
      String path="/assets/nexuscharacters/appearance_parts_v064/"+paths[k];
      try(class_1011 raw=class_1011.method_4309(SourceArmLayoutAudit.class.getResourceAsStream(path));
          class_1011 loaded=(class_1011)GenericSkinLayerSupport.load(path)) {
        int[] pixels=new int[4096];
        for(int y=0;y<64;y++)for(int x=0;x<64;x++)pixels[y*64+x]=raw.method_61940(x,y);
        int[] expected=ArmUvLayout.classicTexture(pixels,widths[k]);
        for(int y=0;y<64;y++)for(int x=0;x<64;x++)
          if(loaded.method_61940(x,y)!=expected[y*64+x])throw new AssertionError("Resource layout not applied "+path);
      }
      System.out.println("NEW_ASSET_ARM_LAYOUT_PASS "+paths[k]+" width="+widths[k]);
    }
  }
}
