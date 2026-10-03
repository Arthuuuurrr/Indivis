import java.util.*;
import java.util.zip.*;
import javax.imageio.ImageIO;
import net.tompsen.nexuscharacters.ArmUvLayout;

/** Face-labelled oracle, source preservation and shipped body layouts. */
public class ArmUvRegression {
  static int[] read(ZipFile z,String name)throws Exception {
    try(var in=z.getInputStream(z.getEntry(name))) {
      var im=ImageIO.read(in);return im.getRGB(0,0,64,64,null,0,64);
    }
  }
  public static void main(String[] args)throws Exception {
    int[] source=new int[4096];Arrays.fill(source,0x00112233);
    int[][] blocks={{40,16},{32,48},{40,32},{48,48}};
    // Different colors per face, column and row. A shifted back/side cannot pass.
    for(int p=0;p<4;p++) {
      int u=blocks[p][0],v=blocks[p][1];
      int[][] faces={{u+4,v,3,4},{u+7,v,3,4},{u,v+4,4,12},
          {u+4,v+4,3,12},{u+7,v+4,4,12},{u+11,v+4,3,12}};
      for(int f=0;f<6;f++) for(int y=0;y<faces[f][3];y++) for(int x=0;x<faces[f][2];x++)
        source[(faces[f][1]+y)*64+faces[f][0]+x]=0xff000000|p<<20|f<<16|y<<8|x;
    }
    int[] saved=source.clone(),result=ArmUvLayout.classicTexture(source,3);
    int[] columns={0,1,1,2};int checked=0;
    boolean[] arm=new boolean[4096];
    for(int p=0;p<4;p++) {
      int u=blocks[p][0],v=blocks[p][1];
      int[][] faces={{u+4,v,4,4},{u+8,v,4,4},{u,v+4,4,12},
          {u+4,v+4,4,12},{u+8,v+4,4,12},{u+12,v+4,4,12}};
      for(int f=0;f<6;f++)for(int y=0;y<faces[f][3];y++)for(int x=0;x<4;x++) {
        int i=(faces[f][1]+y)*64+faces[f][0]+x;
        int expected=0xff000000|p<<20|f<<16|y<<8|((f==2||f==4)?x:columns[x]);
        if(result[i]!=expected)throw new AssertionError("Wrong face "+p+":"+f+":"+x+":"+y);
        arm[i]=true;checked++;
      }
    }
    for(int i=0;i<4096;i++)if(!arm[i]&&result[i]!=source[i])throw new AssertionError("Non-arm pixel changed");
    if(!Arrays.equals(source,saved))throw new AssertionError("Source mutated");
    if(ArmUvLayout.classicTexture(source,4)!=source)throw new AssertionError("Classic path changed");
    if(ArmUvLayout.inferredWidth("/assets/nexuscharacters/appearance_parts_v064/outfits/outfit_99.png",source)!=3)
      throw new AssertionError("New slim sleeve not detected");
    try(ZipFile z=new ZipFile(args[0])) {
      String base="assets/nexuscharacters/appearance_parts_v064/bodies/";
      for(String b:List.of("body_basic.png","body_heavy.png","body_muscular.png","body_feminine.png")) {
        int[] raw=read(z,base+b);boolean slim=ArmUvLayout.isSlimBody(raw);
        if(slim!=b.equals("body_heavy.png"))throw new AssertionError("Body layout misclassified: "+b);
        int[] converted=ArmUvLayout.classicBody(raw);
        if(!slim&&converted!=raw)throw new AssertionError("Classic body changed: "+b);
        if(slim)for(int[] uv:new int[][]{{40,16},{32,48}})
          for(int y=uv[1]+4;y<uv[1]+16;y++)for(int x=uv[0];x<uv[0]+16;x++)
            if((converted[y*64+x]>>>24)!=255)throw new AssertionError("Body 2 still has holes");
      }
    }
    System.out.println("ARM_UV_REGRESSION_PASS labelledFacePixels="+checked+" sourcesUnchanged=true classicBodiesUnchanged=true");
  }
}
