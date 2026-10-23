package net.hautecapitale.quetes.sequence;

/** Contrôleur de trajet indépendant de Minecraft ; le port déplace réellement l'acteur. */
public final class Pq01Motion {
  // La navigation au sol d'EasyNPC s'arrête avant le centre exact du bloc.
  private static final double ARRIVAL_RADIUS=1.25;
  public record Point(String dimension,double x,double y,double z) {
    public double distance2(Point p){double a=x-p.x,b=y-p.y,c=z-p.z;return a*a+b*b+c*c;}
  }
  public interface Port {
    boolean valid(); Point position(); boolean navigate(Point p); default boolean navigating(){return false;} void stop(); void arrived(); void failed(String reason);
  }
  private final Port port; private final Point target; private final int timeout;
  private int ticks,stalled; private boolean done;private Point last;
  public Pq01Motion(Port port,Point target,int timeout){this.port=port;this.target=target;this.timeout=Math.max(20,Math.min(1200,timeout));}
  public void start(){
    if(done)return;
    if(!port.valid()){fail("acteur ou personnage indisponible");return;}
    Point at=port.position();last=at;
    if(!at.dimension.equals(target.dimension)||at.distance2(target)>24*24){fail("point de trajet hors portée ou dans une autre dimension");return;}
    if(at.distance2(target)<=ARRIVAL_RADIUS*ARRIVAL_RADIUS){finish();return;}
    port.navigate(target); // Une création de chemin peut attendre le premier contact au sol.
  }
  public void tick(){
    if(done)return;
    if(!port.valid()){fail("acteur ou contexte de personnage perdu");return;}
    Point at=port.position();
    if(last!=null&&at.distance2(last)>0.0004)stalled=0;else stalled++;last=at;
    if(!at.dimension.equals(target.dimension)){fail("dimension changée");return;}
    if(at.distance2(target)<=ARRIVAL_RADIUS*ARRIVAL_RADIUS){finish();return;}
    if(++ticks>=timeout){fail("délai de déplacement dépassé");return;}
    if(ticks%20==0&&!port.navigating()&&!port.navigate(target)&&stalled>=60)fail("aucun chemin utilisable après plusieurs tentatives ; position="+at+", destination="+target);
  }
  private void finish(){done=true;port.stop();port.arrived();}
  private void fail(String reason){done=true;port.stop();port.failed(reason);}
  public void cancel(){if(!done){done=true;port.stop();}}
  public boolean done(){return done;}
}
