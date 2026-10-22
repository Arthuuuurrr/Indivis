import net.hautecapitale.quetes.sequence.Pq01Motion;
import net.hautecapitale.quetes.sequence.Pq01Motion.Point;

/** Exécute le vrai contrôleur compilé ; aucun monde Minecraft n'est simulé. */
public final class Pq01MotionBench {
  static int checks,cases;
  static void check(boolean ok,String name){checks++;if(!ok)throw new AssertionError(name);}
  static Point point(double x){return new Point("minecraft:overworld",x,80,0);}
  static final class Port implements Pq01Motion.Port {
    Point position=point(0),target;boolean valid=true,path=true;int moves,stops,arrivals,failures;String reason="";
    public boolean valid(){return valid;} public Point position(){return position;}
    public boolean navigate(Point p){moves++;target=p;return path;}
    public void stop(){stops++;}public void arrived(){arrivals++;}public void failed(String r){failures++;reason=r;}
    void at(double x){position=point(x);}
  }
  static Pq01Motion motion(Port p,double x,int timeout){return new Pq01Motion(p,point(x),timeout);}
  public static void main(String[] args){
    Port p=new Port();Pq01Motion m=motion(p,6,300);m.start();check(p.moves==1,"navigation native demandée");check(p.arrivals==0,"pas de réplique avant arrivée");p.at(5.3);m.tick();check(m.done()&&p.arrivals==1&&p.stops==1,"arrivée et arrêt");cases++;
    m.tick();m.start();m.cancel();check(p.arrivals==1&&p.moves==1&&p.stops==1,"aucun callback après fin");cases++;
    p=new Port();m=motion(p,0.5,300);m.start();check(m.done()&&p.moves==0&&p.arrivals==1,"déjà arrivé");cases++;
    p=new Port();p.path=false;m=motion(p,6,300);m.start();check(m.done()&&p.failures==1&&p.arrivals==0&&p.reason.contains("aucun chemin"),"chemin refusé");cases++;
    p=new Port();p.valid=false;m=motion(p,6,300);m.start();check(m.done()&&p.moves==0&&p.failures==1,"acteur absent avant départ");cases++;
    p=new Port();m=new Pq01Motion(p,new Point("minecraft:the_nether",6,80,0),300);m.start();check(m.done()&&p.moves==0&&p.failures==1,"autre dimension au départ");cases++;
    p=new Port();m=motion(p,24.01,300);m.start();check(m.done()&&p.moves==0&&p.failures==1,"trajet au-delà de 24 blocs");cases++;
    p=new Port();m=motion(p,24,300);m.start();check(!m.done()&&p.moves==1,"borne 24 blocs acceptée");m.cancel();cases++;
    p=new Port();m=motion(p,6,300);m.start();for(int i=0;i<39;i++)m.tick();check(p.moves==1,"pas de replanification chaque tick");m.tick();check(p.moves==2,"replanification à 40 ticks");p.path=false;for(int i=0;i<40;i++)m.tick();check(m.done()&&p.failures==1&&p.reason.contains("inaccessible"),"chemin coupé");cases++;
    p=new Port();m=motion(p,6,300);m.start();for(int i=0;i<299;i++)m.tick();check(!m.done(),"délai non prématuré");m.tick();check(m.done()&&p.failures==1&&p.reason.contains("délai"),"timeout");cases++;
    p=new Port();m=motion(p,6,1);m.start();for(int i=0;i<19;i++)m.tick();check(!m.done(),"timeout minimum 20");m.tick();check(m.done(),"timeout minimum appliqué");cases++;
    p=new Port();m=motion(p,6,9999);m.start();for(int i=0;i<1199;i++)m.tick();check(!m.done(),"timeout maximum 1200 non prématuré");m.tick();check(m.done(),"timeout maximum appliqué");cases++;
    p=new Port();m=motion(p,6,300);m.start();p.valid=false;m.tick();check(m.done()&&p.arrivals==0&&p.failures==1,"changement personnage/déconnexion invalide le port");cases++;
    p=new Port();m=motion(p,6,300);m.start();p.position=new Point("minecraft:the_nether",0,80,0);m.tick();check(m.done()&&p.arrivals==0&&p.reason.contains("dimension"),"dimension perdue en marche");cases++;
    p=new Port();m=motion(p,6,300);m.start();m.cancel();m.tick();check(m.done()&&p.stops==1&&p.arrivals==0&&p.failures==0,"fermeture annule sans progression");cases++;
    Port a=new Port(),b=new Port();Pq01Motion ma=motion(a,6,300),mb=motion(b,6,300);ma.start();for(int i=0;i<200;i++)ma.tick();mb.start();a.at(6);ma.tick();check(a.arrivals==1&&b.arrivals==0&&!mb.done(),"deux ports décalés de dix secondes");b.at(6);mb.tick();check(b.arrivals==1&&a.arrivals==1,"arrivées indépendantes");cases++;
    p=new Port();m=motion(p,6,300);m.start();p.at(6);m.tick();Pq01Motion back=motion(p,0,300);back.start();check(p.arrivals==1&&!back.done(),"retour attend son propre trajet");p.at(0);back.tick();check(p.arrivals==2&&back.done(),"aller et retour réels");cases++;
    System.out.println("PASS "+cases+" cas, "+checks+" assertions sur Pq01Motion compilé. Aucun test Minecraft réel.");
  }
}
