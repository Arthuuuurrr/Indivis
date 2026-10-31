package net.hautecapitale.quetes.sequence;
/** Trame bornée et cadence de lecture ; aucune progression hors du jeton de choix. */
final class Pq02Pacing {
  record Frame(String key,String role,int token,int readingTicks,int voiceTicks){}
  static Frame parse(String value){
    if(value==null||!value.startsWith("indivis:pq02:v1:"))return null;
    String[] p=value.split(":",-1);if(p.length!=8)return null;
    try{
      String role=p[4];if(!java.util.Set.of("leovic","magistrat").contains(role)||!p[3].matches("[a-z][a-z0-9_]{0,63}"))return null;
      int token=Integer.parseInt(p[5]),read=Integer.parseInt(p[6]),voice=Integer.parseInt(p[7]);
      if(token!=0&&(token<1000000001||token>1999999915)||read<0||read>12000||voice<0||voice>12000)return null;
      return new Frame(p[3],role,token,read,voice);
    }catch(NumberFormatException e){return null;}
  }
  static boolean ready(Frame f,int age,int finalPageTicks,boolean lastPage,boolean typing,boolean voicePlaying,boolean typewriter){
    // Le lecteur décide quand avancer ; seules la nouvelle capture et la voix sont attendues.
    return f!=null&&f.token()!=0&&!voicePlaying&&lastPage&&!typing&&age>=Math.max(2,f.voiceTicks())&&finalPageTicks>=1;
  }
}

