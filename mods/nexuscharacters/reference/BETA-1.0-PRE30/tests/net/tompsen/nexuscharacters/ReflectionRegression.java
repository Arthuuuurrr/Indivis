package net.tompsen.nexuscharacters;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
public final class ReflectionRegression {
 static int checks;
 static void check(boolean c,String text){checks++;if(!c)throw new AssertionError(text);}
 interface Default {default String face(){return "interface";}}
 static class Parent {private int value=7;private String privateMethod(){return "private";} public String inherited(){return "parent";}}
 static class Child extends Parent implements Default {
  static String global="old"; public String overloaded(String x){return x;} public String overloaded(Integer x){return "integer";}
  public void fail(){throw new IllegalStateException("sentinel");}
 }
 static Throwable failure(Method m,Object target,Object... args){try{m.invoke(target,args);throw new AssertionError();}catch(Throwable e){return e;}}
 public static void main(String[] args)throws Exception {
  Child child=new Child();
  for(String name:List.of("privateMethod","inherited","face","overloaded","fail")){
   int count=name.equals("overloaded")?1:0;
   Method original=GenericSkinLayerSupport.resolveMethodPRE30(Child.class,name,count);
   Method cached=ReflectionAccess.method(Child.class,name,count);
   check(original.equals(cached),"Legacy selection changed "+name);
   long scans=ReflectionAccess.methodScans.get();
   for(int i=0;i<10000;i++)check(ReflectionAccess.method(Child.class,name,count)==cached,"Cache identity");
   check(scans==ReflectionAccess.methodScans.get(),"Warm method scanned");
  }
  check(ReflectionAccess.method(Child.class,"privateMethod",0).invoke(child).equals("private"),"Private inherited");
  check(ReflectionAccess.method(Child.class,"face",0).invoke(child).equals("interface"),"Default interface");
  Throwable a=failure(GenericSkinLayerSupport.resolveMethodPRE30(Child.class,"fail",0),child),b=failure(ReflectionAccess.method(Child.class,"fail",0),child);
  check(a.getClass()==b.getClass()&&a.getCause().getClass()==b.getCause().getClass(),"Invocation exception changed");
  check(ReflectionAccess.genericField(child,"value").equals(7),"Private parent field");
  ReflectionAccess.surfaceSet(child,"value",42);check(ReflectionAccess.genericField(child,"value").equals(42),"Cached mutable value");
  check(ReflectionAccess.genericField(Child.class,"global").equals("old"),"Static field");Child.global="new";check(ReflectionAccess.genericField(Child.class,"global").equals("new"),"Static cached mutable value");
  long fields=ReflectionAccess.fieldScans.get();for(int i=0;i<10000;i++)ReflectionAccess.genericField(child,"value");check(fields==ReflectionAccess.fieldScans.get(),"Warm field scanned");
  for(boolean qualified:List.of(true,false))try{ReflectionAccess.field(Child.class,"absent",qualified);throw new AssertionError();}catch(NoSuchFieldException e){check(e.getMessage().equals(qualified?Child.class.getName()+".absent":"absent"),"Missing field exception");}
  try{ReflectionAccess.method(Child.class,"absent",0);throw new AssertionError();}catch(NoSuchMethodException e){check(e.getMessage().equals(Child.class.getName()+".absent/0"),"Missing method exception");}
  try{ReflectionAccess.surfaceCall(child,"privateMethod");throw new AssertionError();}catch(NoSuchMethodException expected){check(expected.getMessage().equals("privateMethod"),"Public-only semantics");}
  class Concurrent extends Parent {public String cold(){return "cold";}}
  long cold=ReflectionAccess.methodScans.get();ExecutorService pool=Executors.newFixedThreadPool(8);List<Future<Method>> futures=new ArrayList<>();
  for(int t=0;t<8;t++)futures.add(pool.submit(()->ReflectionAccess.method(Concurrent.class,"cold",0)));
  Method first=futures.getFirst().get();for(var f:futures)check(f.get()==first,"Concurrent cold winner");pool.shutdown();check(ReflectionAccess.methodScans.get()==cold+1,"Concurrent duplicate resolution");
  System.out.println("REFLECTION_PASS checks="+checks+" methodHits="+ReflectionAccess.methodHits+" methodMisses="+ReflectionAccess.methodMisses+" fieldHits="+ReflectionAccess.fieldHits+" fieldMisses="+ReflectionAccess.fieldMisses);
 }
}
