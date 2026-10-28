import java.util.*;
import net.minecraft.*;
import de.markusbordihn.easynpc.entity.easynpc.EasyNPC;
import fr.indivis.nexusnpc.NpcAppearance;
import net.tompsen.nexuscharacters.*;

/** Bounded exact-oracle workload, separately from uninstrumented timings. */
final class Opt2OracleStress {
    static void run(class_310 client)throws Exception {
        List<class_1314> npcs=new ArrayList<>();
        for(var e:client.field_1687.method_18112())if(e instanceof class_1314 mob&&e instanceof EasyNPC<?> npc
            &&NpcAppearance.read(npc.getEasyNPCSkinData().getSkinDataEntry())!=null)npcs.add(mob);
        var players=OptPlayerCost.players(client);int states=0;
        NpcIntegrationTest.benchmark=true;
        try {
            for(int f=0;f<24;f++) {
                String pose=f%2==0?"head_turn":"walking";
                for(int i=0;i<npcs.size();i++){OptCost.frame(client,npcs.get(i),pose,36000+f,i);states++;}
                for(int i=0;i<players.size();i++){OptPlayerCost.frame(client,players.get(i),pose,36000+f,i);states++;}
            }
        } finally {
            for(var p:players)client.field_1687.method_2945(p.method_5628(),class_1297.class_5529.field_26998);
            NpcIntegrationTest.benchmark=false;
        }
        NpcIntegrationTest.results.add("OPT2_ORACLE_STRESS_PASS live NPC/player states="+states+" changing poses; not a timing measurement");
    }
}
