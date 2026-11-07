package net.hautecapitale.quetes.sequence;
/** Le pipeline PQ02 complet reste fonctionnel avec l'écouteur PQ03 installé. */
public final class Pq03CoexistenceBench {
 public static void main(String[] args)throws Exception{new Pq03Client().onInitializeClient();Pq02ProtocolBench.main(args);}
}
