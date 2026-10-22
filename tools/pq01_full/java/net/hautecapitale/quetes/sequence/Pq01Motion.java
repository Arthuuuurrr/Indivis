package net.hautecapitale.quetes.sequence;

/** Contrôleur de trajet indépendant de Minecraft ; le port déplace réellement l'acteur. */
public final class Pq01Motion {
  public record Point(String dimension,double x,double y,double z) {
    public double distance2(Point p){double a=x-p.x,b=y-p.y,c=z-p.z;return a*a+b*b+c*c;}
  }
  public interface Port {
    boolean valid(); Point position(); boolean navigate(Point p); void stop(); void arrived(); void failed(String reason);
  }
  private final Port port; private final Point target; private final int timeout;
  private int ticks; private boolean done;
  public Pq01Motion(Port port,Point target,int timeout){this.port=port;this.target=target;this.timeout=Math.max(20,Math.min(1200,timeout));}
  public void start(){
    if(done)return;
    if(!port.valid()){fail("acteur ou personnage indisponible");return;}
    Point at=port.position();
    if(!at.dimension.equals(target.dimension)||at.distance2(target)>24*24){fail("point de trajet hors portée ou dans une autre dimension");return;}
    if(at.distance2(target)<=0.8*0.8){finish();return;}
    if(!port.navigate(target))fail("aucun chemin au sol vers le point configuré");
  }
  public void tick(){
    if(done)return;
    if(!port.valid()){fail("acteur ou contexte de personnage perdu");return;}
    Point at=port.position();
    if(!at.dimension.equals(target.dimension)){fail("dimension changée");return;}
    if(at.distance2(target)<=0.8*0.8){finish();return;}
    if(++ticks>=timeout){fail("délai de déplacement dépassé");return;}
    if(ticks%40==0&&!port.navigate(target))fail("chemin devenu inaccessible");
  }
  private void finish(){done=true;port.stop();port.arrived();}
  private void fail(String reason){done=true;port.stop();port.failed(reason);}
  public void cancel(){if(!done){done=true;port.stop();}}
  public boolean done(){return done;}
}
