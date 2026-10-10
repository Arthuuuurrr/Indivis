package net.tompsen.nexuscharacters;
import java.lang.reflect.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
/** ClassValue associates entries with class identity without rooting its classloader. */
final class ReflectionAccess {
 static final boolean DIAGNOSTICS=Boolean.getBoolean("nexuscharacters.pre30.diagnostics");
 static final AtomicLong methodHits=new AtomicLong(),methodMisses=new AtomicLong(),fieldHits=new AtomicLong(),fieldMisses=new AtomicLong(),methodScans=new AtomicLong(),fieldScans=new AtomicLong();
 static final class Members {
  final ConcurrentMap<String,ConcurrentMap<Integer,Method>> generic=new ConcurrentHashMap<>(),surface=new ConcurrentHashMap<>();
  final ConcurrentMap<String,Field> fields=new ConcurrentHashMap<>();
 }
 static final ClassValue<Members> MEMBERS=new ClassValue<>(){protected Members computeValue(Class<?> type){return new Members();}};
 static Method method(Class<?> type,String name,int count)throws NoSuchMethodException{return method(type,name,count,false);}
 static Method method(Class<?> type,String name,int count,boolean publicOnly)throws NoSuchMethodException{
  Members members=MEMBERS.get(type);
  var table=publicOnly?members.surface:members.generic;
  var methods=table.get(name);
  if(methods!=null){Method hit=methods.get(count);if(hit!=null){if(DIAGNOSTICS)methodHits.incrementAndGet();return hit;}}
  synchronized(members){
   methods=table.get(name);
   Method found=methods==null?null:methods.get(count);
   if(found!=null){if(DIAGNOSTICS)methodHits.incrementAndGet();return found;}
   if(DIAGNOSTICS){methodMisses.incrementAndGet();methodScans.incrementAndGet();}
   if(publicOnly){
    for(Method m:type.getMethods())if(m.getName().equals(name)&&m.getParameterCount()==count){found=m;break;}
    if(found==null)throw new NoSuchMethodException(name);
   }else found=GenericSkinLayerSupport.resolveMethodPRE30(type,name,count);
   if(methods==null){methods=new ConcurrentHashMap<>();table.put(name,methods);}
   methods.put(count,found);return found;
  }
 }
 static Field field(Class<?> type,String name,boolean qualified)throws NoSuchFieldException{
  Members members=MEMBERS.get(type);Field f=members.fields.get(name);
  if(f!=null){if(DIAGNOSTICS)fieldHits.incrementAndGet();return f;}
  synchronized(members){
   f=members.fields.get(name);if(f!=null){if(DIAGNOSTICS)fieldHits.incrementAndGet();return f;}
   if(DIAGNOSTICS)fieldMisses.incrementAndGet();
   for(Class<?> c=type;c!=null;c=c.getSuperclass()){
    try{if(DIAGNOSTICS)fieldScans.incrementAndGet();f=c.getDeclaredField(name);f.setAccessible(true);members.fields.put(name,f);return f;}
    catch(NoSuchFieldException ignored){}
   }
   throw new NoSuchFieldException(qualified?type.getName()+"."+name:name);
  }
 }
 static Object genericField(Object target,String name)throws Exception{
  Class<?> type=target instanceof Class<?> c?c:target.getClass();
  return field(type,name,true).get(target instanceof Class<?>?null:target);
 }
 static Object surfaceField(Object target,String name)throws Exception{
  Class<?> type=target instanceof Class<?> c?c:target.getClass();
  return field(type,name,false).get(target instanceof Class<?>?null:target);
 }
 static void surfaceSet(Object target,String name,Object value)throws Exception{
  field(target.getClass(),name,false).set(target,value);
 }
 static Object surfaceCall(Object target,String name,Object... args)throws Exception{
  return method(target.getClass(),name,args.length,true).invoke(target,args);
 }
}
