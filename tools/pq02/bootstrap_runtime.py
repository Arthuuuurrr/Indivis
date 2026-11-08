from pathlib import Path
import json,zipfile,subprocess,os
RT=Path('/tmp/indivis_pq02_runtime');JAVA=next((RT/'jdk').glob('*/bin/java'));JAVAC=JAVA.with_name('javac');S=RT/'bootstrap';(S/'mods').mkdir(parents=True,exist_ok=True)
src=S/'Bootstrap.java';src.write_text('''import net.fabricmc.api.ModInitializer;import java.lang.reflect.*;public final class Bootstrap implements ModInitializer{static Object call(Object o,String name,Object...args)throws Exception{for(Method m:o.getClass().getMethods())if(m.getName().equals(name)&&m.getParameterCount()==args.length){try{return m.invoke(o,args);}catch(IllegalArgumentException ignored){}}throw new NoSuchMethodException(name);}public void onInitialize(){try{Class<?> e=Class.forName("net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents"),h=Class.forName("net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents$ServerStarted");Object event=e.getField("SERVER_STARTED").get(null);Object cb=Proxy.newProxyInstance(h.getClassLoader(),new Class<?>[]{h},(p,m,a)->{if(m.getDeclaringClass()==Object.class)return m.getName().equals("toString")?"Bootstrap":null;Object s=a[0];call(call(s,"method_3734"),"method_44252",call(s,"method_3739"),"stop");return null;});call(event,"register",cb);}catch(Exception e){throw new RuntimeException(e);}}}''')
loader=RT/'libraries/fabric-loader-0.19.5.jar';subprocess.run([JAVAC,'-cp',loader,'-d',S,src],check=True)
with zipfile.ZipFile(S/'mods/bootstrap.jar','w') as z:
 z.write(S/'Bootstrap.class','Bootstrap.class');z.writestr('fabric.mod.json',json.dumps({'schemaVersion':1,'id':'pq02_runtime_bootstrap','version':'1.0.0','entrypoints':{'main':['Bootstrap']},'depends':{'fabricloader':'>=0.19.5','minecraft':'1.21.11','fabric-api':'*'}}))
(S/'mods/fabric-api.jar').write_bytes((RT/'fabric-api.jar').read_bytes());(S/'eula.txt').write_text('eula=true\n')
(S/'server.properties').write_text('online-mode=false\nserver-ip=127.0.0.1\nserver-port=25579\nview-distance=2\nsimulation-distance=2\nspawn-protection=0\nmax-tick-time=0\npause-when-empty-seconds=0\n')
game=RT/'minecraft_bundle/META-INF/versions/1.21.11/server-1.21.11.jar';cp=os.pathsep.join(map(str,[*sorted((RT/'libraries').glob('*.jar')),*sorted((RT/'minecraft_bundle/META-INF/libraries').rglob('*.jar')),game]))
with (S/'server.log').open('w') as f:r=subprocess.run([JAVA,'-XX:ActiveProcessorCount=2','-Xmx2G',f'-Dfabric.gameJarPath={game}','-Dfabric.log.disableAnsi=true','-cp',cp,'net.fabricmc.loader.impl.launch.knot.KnotServer','nogui'],cwd=S,stdout=f,stderr=subprocess.STDOUT,timeout=360)
assert r.returncode==0,(S/'server.log').read_text()[-5000:]
print('PASS Fabric 1.21.11 baseline started and stopped; no PQ02 gameplay exercised')
print('\n'.join(str(p) for p in S.rglob('*.jar') if 'remap' in str(p)))
