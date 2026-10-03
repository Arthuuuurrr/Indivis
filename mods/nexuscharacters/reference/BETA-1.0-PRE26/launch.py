import os,subprocess,time,sys,threading,hashlib,json,zipfile
from pathlib import Path
root=Path(__file__).resolve().parent.parent
rt=root/'runtime'
game=Path(os.environ.get('TEST_GAME_DIR',str(rt/'game')))
cp=(rt/'classpath.txt').read_text().strip()+':'+str(rt/'fabric-loader.jar')+':'+str(rt/'client-intermediary.jar')
cp+=':'+':'.join(str(p) for p in (rt/'libs/net/fabricmc/sponge-mixin').rglob('*.jar'))
env=os.environ.copy();env['LD_LIBRARY_PATH']=str(rt/'osmesa/usr/lib/x86_64-linux-gnu')+':'+str(rt/'natives')
env['LP_NUM_THREADS']='4';env['LIBGL_ALWAYS_SOFTWARE']='true';env['ALSOFT_DRIVERS']='null';env['LP_NUM_THREADS']='4'
cmd=[str(root/'jdk21/bin/java'),'-Xmx1500M','--add-exports=java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED','-javaagent:'+str(rt/'headless-agent.jar'),'-Djava.library.path='+str(rt/'natives'),'-Dorg.lwjgl.opengl.libname='+str(rt/'osmesa/usr/lib/x86_64-linux-gnu/libOSMesa.so.8'),'-Dfabric.gameMappingNamespace=intermediary','-Dfabric.runtimeMappingNamespace=intermediary','-Dfabric.gameJarPath='+str(rt/'client-intermediary.jar'),'-cp',cp,'net.fabricmc.loader.impl.launch.knot.KnotClient','--gameDir',str(rt/'game'),'--assetsDir',str(rt/'assets'),'--assetIndex','29','--version','1.21.11','--username','PRE20Test','--uuid','00000000000000000000000000000001','--accessToken','offline','--width','1280','--height','900']
cmd.insert(2,'-Dorg.lwjgl.opengl.explicitInit=true');cmd.insert(2,'-XX:ActiveProcessorCount=4')
cmd.insert(2,'-DexpectHairDepthFailure='+str(os.environ.get('EXPECT_HAIR_DEPTH_FAILURE','0')=='1').lower())
cmd[cmd.index('--gameDir')+1]=str(game)
cmd.insert(2,'-Dmixin.debug.export=true')
cmd.insert(2,'-DexpectArmLayoutFailure='+str(os.environ.get('EXPECT_ARM_LAYOUT_FAILURE','0')=='1').lower())
cmd.insert(2,'-DexpectClothingGridFailure='+str(os.environ.get('EXPECT_CLOTHING_GRID_FAILURE','0')=='1').lower())
cmd.insert(2,'-DevidenceDir='+os.environ.get('EVIDENCE_DIR','pre26/evidence/candidate'))
cmd.insert(2,'-DcompatibilityTest='+str(os.environ.get('COMPATIBILITY_TEST','0')=='1').lower())
cmd=[arg.replace(str(rt/'headless-agent.jar'),str(root/'pre21/headless-agent.jar')) if arg.startswith('-javaagent:') else arg for arg in cmd]
evidence=root/os.environ.get('EVIDENCE_DIR','pre26/evidence/candidate')
evidence.mkdir(parents=True,exist_ok=True)
status=evidence/('comparison-status.txt' if os.environ.get('HARNESS_ENTRY')=='ClothingComparison' else 'status.log')
status.unlink(missing_ok=True)
nexus=game/'mods/NexusCharacters-PRE20.jar'
jarHash=hashlib.sha256(nexus.read_bytes()).hexdigest()
(evidence/'run.json').write_text(json.dumps({'jar_sha256':jarHash,'entry':os.environ.get('HARNESS_ENTRY'),'started_epoch':time.time()},indent=2)+'\n')
start=time.monotonic()
with (root/'pre26/evidence/client.log').open('w') as log:
 p=subprocess.Popen(cmd,cwd=root,env=env,stdout=log,stderr=subprocess.STDOUT)
 def diagnose_startup():
  if p.poll() is None and 'ACTUAL_START_WORLD' not in (root/'pre26/evidence/client.log').read_text():
   with (root/'pre26/startup-threads.txt').open('w') as dump:
    subprocess.run([str(root/'jdk21/bin/jstack'),str(p.pid)],stdout=dump,stderr=subprocess.STDOUT,timeout=20)
 timer=threading.Timer(40,diagnose_startup);timer.daemon=True;timer.start()
 
 try:p.wait(timeout=600)
 except subprocess.TimeoutExpired:p.terminate();p.wait(timeout=15)
 timer.cancel()
print('CLIENT_EXIT',p.returncode)
print('CLIENT_SECONDS',round(time.monotonic()-start,1))
status=root/os.environ.get('EVIDENCE_DIR','pre26/evidence/candidate')/('comparison-status.txt' if os.environ.get('HARNESS_ENTRY')=='ClothingComparison' else 'status.log')
passed=status.exists() and 'ACTUAL_PLAYER_REGRESSION_PASS' in status.read_text() and 'FAILURE' not in status.read_text() and hashlib.sha256(nexus.read_bytes()).hexdigest()==jarHash
print('TEST_COMPLETED',passed)
sys.exit(0 if p.returncode==0 and passed else 1)
