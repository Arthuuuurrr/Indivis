import os,subprocess,time,sys,threading
from pathlib import Path
root=Path(__file__).resolve().parent.parent
rt=root/'runtime'
cp=(rt/'classpath.txt').read_text().strip()+':'+str(rt/'fabric-loader.jar')+':'+str(rt/'client-intermediary.jar')
cp+=':'+':'.join(str(p) for p in (rt/'libs/net/fabricmc/sponge-mixin').rglob('*.jar'))
env=os.environ.copy();env['LD_LIBRARY_PATH']=str(rt/'osmesa/usr/lib/x86_64-linux-gnu')+':'+str(rt/'natives')
env['LIBGL_ALWAYS_SOFTWARE']='true';env['ALSOFT_DRIVERS']='null'
cmd=[str(root/'jdk21/bin/java'),'-Xmx1500M','--add-exports=java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED','-javaagent:'+str(rt/'headless-agent.jar'),'-Djava.library.path='+str(rt/'natives'),'-Dorg.lwjgl.opengl.libname='+str(rt/'osmesa/usr/lib/x86_64-linux-gnu/libOSMesa.so.8'),'-Dfabric.gameMappingNamespace=intermediary','-Dfabric.runtimeMappingNamespace=intermediary','-Dfabric.gameJarPath='+str(rt/'client-intermediary.jar'),'-cp',cp,'net.fabricmc.loader.impl.launch.knot.KnotClient','--gameDir',str(rt/'game'),'--assetsDir',str(rt/'assets'),'--assetIndex','29','--version','1.21.11','--username','PRE20Test','--uuid','00000000000000000000000000000001','--accessToken','offline','--width','1280','--height','900']
cmd.insert(2,'-Dorg.lwjgl.opengl.explicitInit=true')
cmd.insert(2,'-Dmixin.debug.export=true')
cmd=[arg.replace(str(rt/'headless-agent.jar'),str(root/'pre21/headless-agent.jar')) if arg.startswith('-javaagent:') else arg for arg in cmd]
start=time.monotonic()
with (root/'pre22/render.log').open('w') as log:
 p=subprocess.Popen(cmd,cwd=root,env=env,stdout=log,stderr=subprocess.STDOUT)
 def diagnose_startup():
  if p.poll() is None and 'ACTUAL_START_WORLD' not in (root/'pre22/render.log').read_text():
   with (root/'pre22/startup-threads.txt').open('w') as dump:
    subprocess.run([str(root/'jdk21/bin/jstack'),str(p.pid)],stdout=dump,stderr=subprocess.STDOUT,timeout=20)
 timer=threading.Timer(40,diagnose_startup);timer.daemon=True;timer.start()
 p.wait();timer.cancel()
print('CLIENT_EXIT',p.returncode)
print('CLIENT_SECONDS',round(time.monotonic()-start,1))
passed='ACTUAL_PLAYER_REGRESSION_PASS' in (root/'pre22/render.log').read_text()
print('TEST_COMPLETED',passed)
sys.exit(0 if p.returncode==0 and passed else 1)
