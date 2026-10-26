package fr.indivis.nexusnpc;
import net.minecraft.*;
public record UploadPreset(String json) implements class_8710 {
 public static final class_8710.class_9154<UploadPreset> ID=new class_8710.class_9154<>(class_2960.method_60655("indivis_nexus_npc","upload"));
 public static final class_9139<class_9129,UploadPreset> CODEC=new class_9139<>(){
  public UploadPreset decode(class_9129 buf){return new UploadPreset(buf.method_10800(NpcPreset.MAX_JSON));}
  public void encode(class_9129 buf,UploadPreset data){buf.method_10788(data.json(),NpcPreset.MAX_JSON);}
 };
 public class_8710.class_9154<? extends class_8710> method_56479(){return ID;}
}
