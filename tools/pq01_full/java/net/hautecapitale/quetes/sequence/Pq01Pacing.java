package net.hautecapitale.quetes.sequence;
/** Trame bornée et cadence de lecture ; aucune progression hors du jeton de choix. */
final class Pq01Pacing {
  record Frame(String key,String role,int token,int readingTicks,int voiceTicks){}
  static Frame parse(String value){
    if(value==null||!value.startsWith("indivis:pq01:v1:"))return null;
    String[] p=value.split(":",-1);if(p.length!=8)return null;
    try{
      String role=Pq01Ids.role(p[3]);if(role==null||!role.equals(p[4]))return null;
      int token=Integer.parseInt(p[5]),read=Integer.parseInt(p[6]),voice=Integer.parseInt(p[7]);
      if(token!=0&&(token<1000000001||token>1999999915)||read<0||read>12000||voice<0||voice>12000)return null;
      return new Frame(p[3],role,token,read,voice);
    }catch(NumberFormatException e){return null;}
  }
  static boolean ready(Frame f,int age,int finalPageTicks,boolean lastPage,boolean typing,boolean voicePlaying){
    return f!=null&&f.token()!=0&&!voicePlaying&&lastPage&&!typing&&age>=Math.max(f.readingTicks(),f.voiceTicks()+10)&&finalPageTicks>=Math.min(80,Math.max(40,f.readingTicks()/3));
  }
}
