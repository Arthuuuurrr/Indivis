package net.hautecapitale.quetes.sequence;
import net.hautecapitale.quetes.sequence.Pq01Motion.Point;
/** Trajet réel borné ; annuler la navigation suspend aussi tout délai de parcours. */
public final class Pq02Motion {
 public interface Port {boolean valid();Point position();boolean navigate(Point point);boolean navigating();void stop();void arrived();void failed(String reason);}
 private final Port port;private final Point goal;private final int timeout;private int age,stalled;private Point last;private boolean done;
 public Pq02Motion(Port port,Point goal,int timeout){this.port=port;this.goal=goal;this.timeout=Math.max(200,Math.min(12000,timeout));}
 public void start(){if(done)return;if(!port.valid()){fail("contexte perdu");return;}last=port.position();if(!last.dimension().equals(goal.dimension())||last.distance2(goal)>256*256){fail("repère hors portée de navigation (256 blocs)");return;}if(arrived(last)){finish();return;}port.navigate(goal);}
 public void tick(){if(done)return;if(!port.valid()){fail("acteur ou personnage indisponible");return;}Point at=port.position();if(!at.dimension().equals(goal.dimension())){fail("dimension changée");return;}if(arrived(at)){finish();return;}stalled=last!=null&&at.distance2(last)<0.0004?stalled+1:0;last=at;if(++age>=timeout){fail("délai de navigation dépassé");return;}if(age%20==0&&!port.navigating()&&!port.navigate(goal)&&stalled>=80)fail("chemin indisponible");}
 private boolean arrived(Point at){return Math.abs(at.y()-goal.y())<=0.75&&at.distance2(goal)<=1.0;}
 private void finish(){done=true;port.stop();port.arrived();}private void fail(String s){done=true;port.stop();port.failed(s);}
 public void pause(){if(!done){done=true;port.stop();}}public boolean done(){return done;}public Point target(){return goal;}
}
