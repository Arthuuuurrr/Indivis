package net.hautecapitale.quetes.sequence;
import java.io.*;import java.nio.charset.StandardCharsets;import java.util.*;import com.google.gson.*;
final class Pq02Data {
 static final class Choice {String text,target,effect,condition;int source_line;}
 static final class Node {int id;String key,speaker,next,camera,editorial,condition;List<String> lines;List<Integer> source_lines;List<Choice> choices;}
 static final class Graph {int schema;List<Node> nodes;}
 static final Map<String,Node> NODES=new LinkedHashMap<>();static final Map<Integer,Node> IDS=new HashMap<>();
 static {try(InputStream stream=Pq02Data.class.getResourceAsStream("/indivis_pq02_graph.json")){
  if(stream==null)throw new IOException("Graphe PQ02 absent");
  Graph graph=new Gson().fromJson(new InputStreamReader(stream,StandardCharsets.UTF_8),Graph.class);
  if(graph.schema!=1)throw new IOException("Schéma PQ02 invalide");
  for(Node n:graph.nodes){if(n.choices.size()>13||NODES.put(n.key,n)!=null||IDS.put(n.id,n)!=null)throw new IOException("Nœud ou choix PQ02 ambigu");}
  for(Node n:graph.nodes){List<String> targets=new ArrayList<>();if(!n.next.isEmpty())targets.add(n.next);for(Choice c:n.choices)targets.add(c.target);for(String t:targets)if(!t.startsWith("event:")&&!NODES.containsKey(t))throw new IOException("Cible absente : "+t);}
 }catch(IOException failure){throw new ExceptionInInitializerError(failure);}}
 static Node get(String key){Node n=NODES.get(key);if(n==null)throw new IllegalArgumentException("Nœud PQ02 absent : "+key);return n;}
 private Pq02Data(){}
}
