package net.tompsen.nexuscharacters;
import java.lang.ref.*;
import java.io.*;
import java.util.*;
public final class LoaderRegression {
 static class Isolated extends ClassLoader {
  final byte[] bytes;
  Isolated(byte[] bytes){super(LoaderRegression.class.getClassLoader());this.bytes=bytes;}
  protected Class<?> loadClass(String name,boolean resolve)throws ClassNotFoundException{
   if(!name.equals("net.tompsen.nexuscharacters.LoaderFixture"))return super.loadClass(name,resolve);
   Class<?> c=findLoadedClass(name);if(c==null)c=defineClass(name,bytes,0,bytes.length);if(resolve)resolveClass(c);return c;
  }
 }
 static List<WeakReference<?>> exercise()throws Exception{
  byte[] data=LoaderRegression.class.getResourceAsStream("LoaderFixture.class").readAllBytes();
  Isolated a=new Isolated(data),b=new Isolated(data);Class<?> x=a.loadClass("net.tompsen.nexuscharacters.LoaderFixture"),y=b.loadClass(x.getName());
  var mx=ReflectionAccess.method(x,"read",0);var my=ReflectionAccess.method(y,"read",0);
  if(mx==my||mx.getDeclaringClass()==my.getDeclaringClass())throw new AssertionError("Class identity conflated");
  Object i=x.getConstructor().newInstance();if(!ReflectionAccess.genericField(i,"value").equals(1))throw new AssertionError();
  return List.of(new WeakReference<>(a),new WeakReference<>(b),new WeakReference<>(x),new WeakReference<>(y),new WeakReference<>(i));
 }
 public static void main(String[] args)throws Exception{
  var refs=exercise();for(int n=0;n<40&&refs.stream().anyMatch(r->r.get()!=null);n++){System.gc();Thread.sleep(20);}
  if(refs.stream().anyMatch(r->r.get()!=null))throw new AssertionError("Cached classloader or instance retained");
  System.out.println("LOADER_PASS distinctClassIdentity+classloaderGC+instanceGC");
 }
}
