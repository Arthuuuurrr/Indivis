package net.indivis.admin;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

final class Reflect {
  private static final Map<String,Method> METHODS=new ConcurrentHashMap<>();
  static Class<?> type(String name){try{return Class.forName(name);}catch(ClassNotFoundException e){throw new IllegalStateException("API absente : "+name,e);}}
  private static Class<?> box(Class<?> c){if(c==int.class)return Integer.class;if(c==boolean.class)return Boolean.class;if(c==long.class)return Long.class;if(c==double.class)return Double.class;return c;}
  static Object call(Object object,String name,Object...args) {
    Class<?> c=object instanceof Class<?> t?t:object.getClass();
    String key=c.getName()+"#"+name+Arrays.toString(Arrays.stream(args).map(a->a==null?null:a.getClass()).toArray());
    Method method=METHODS.get(key);
    if(method==null) {
      outer:for(Method m:c.getMethods()) {
        if(!m.getName().equals(name)||m.getParameterCount()!=args.length)continue;
        Class<?>[] types=m.getParameterTypes();for(int i=0;i<types.length;i++)if(args[i]!=null&&!box(types[i]).isInstance(args[i]))continue outer;
        method=m;method.setAccessible(true);METHODS.put(key,m);break;
      }
    }
    if(method==null)throw new IllegalStateException("Méthode absente : "+key);
    try{return method.invoke(object instanceof Class<?>?null:object,args);}
    catch(ReflectiveOperationException e){throw new IllegalStateException(key,e instanceof InvocationTargetException?((InvocationTargetException)e).getCause():e);}
  }
  static Object stat(String owner,String name,Object...args){return call(type(owner),name,args);}
  static Object field(String owner,String name){try{return type(owner).getField(name).get(null);}catch(ReflectiveOperationException e){throw new IllegalStateException(owner+"#"+name,e);}}
  private Reflect(){}
}
