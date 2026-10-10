from pathlib import Path
import json,os,subprocess,shutil,zipfile,sys,time
R=Path(__file__).resolve().parent;RT=R/'runtime'; baseline='--baseline' in sys.argv;world='--world-diag' in sys.argv;future='--future-assets' in sys.argv;candidate=R/('base/NexusCharacters-Indivis-1.21.11-BETA-1.0-PRE29.jar' if baseline else 'NexusCharacters-Indivis-1.21.11-BETA-1.0-PRE30.jar');G=R/('qa/future-game' if future else 'qa/world-game' if world else 'qa/baseline-game' if baseline else 'qa/game');M=G/'mods';M.mkdir(parents=True,exist_ok=True)
for name in ['fabric-api','skinlayers','sodium','immediatelyfast']:shutil.copy2(RT/(name+'.jar'),M/(name+'.jar'))
shutil.copy2(candidate,M/'NexusCharacters-Test.jar')
if world:shutil.copy2(RT/'puffish_skills.jar',M/'puffish_skills.jar')
cp=':'.join(x for x in (RT/'classpath.txt').read_text().split(':') if '/fabric-loader/' not in x)+':'+str(RT/'fabric-loader.jar')+':'+str(RT/'client-intermediary.jar')
ccp=cp+':'+str(candidate)+':'+':'.join(map(str,[*M.glob('*.jar')]))
# Fabric API classes live inside nested jars.
api=RT/'fabric-api-classes';api.mkdir(exist_ok=True)
with zipfile.ZipFile(RT/'fabric-api.jar') as z:
 for name in z.namelist():
  if name.endswith('.jar'):(api/Path(name).name).write_bytes(z.read(name))
ccp+=':'+':'.join(map(str,api.glob('*.jar')))
classes=R/'qa/classes';classes.mkdir(parents=True,exist_ok=True)
sources=[str(p) for p in (R/'tests/client').rglob('*.java') if world or (p.name!='WorldDiagnostic.java' and p.parent.name!='pre30test')]
if not sources:raise Exception('Missing client harness')
subprocess.run([str(R/'jdk/bin/javac'),'-proc:none','-cp',ccp,'-d',str(classes),*sources],check=True)
with zipfile.ZipFile(M/'harness.jar','w') as z:
 meta={'schemaVersion':1,'id':'pre30_harness','version':'1','environment':'client','entrypoints':{'client':['FutureAssetsRegression' if future else 'WorldDiagnostic' if world else 'Pre30Regression']}}
 if world:
  meta['mixins']=['pre30-test.mixins.json'];z.writestr('pre30-test.mixins.json',json.dumps({'required':True,'package':'pre30test','compatibilityLevel':'JAVA_21','client':['FrameAuditMixin']}))
 z.writestr('fabric.mod.json',json.dumps(meta))
 for p in classes.rglob('*.class'):z.write(p,p.relative_to(classes))
 if future:
  with zipfile.ZipFile(candidate) as base:
   for src,dst in [('appearance_parts_v064/hair/hair_long_07.png','appearance_parts_v064/hair/hair_930.png'),('appearance_parts_v068/facial_hair/beard_light_02.png','appearance_parts_v068/facial_hair/beard_930.png'),('appearance_parts_v064/outfits/outfit_22.png','appearance_parts_v064/outfits/outfit_930.png')]:z.writestr('assets/nexuscharacters/'+dst,base.read('assets/nexuscharacters/'+src))
(G/'options.txt').write_text('guiScale:2\nrenderDistance:3\nsimulationDistance:5\nmaxFps:60\n')
env=os.environ.copy();env.update(DISPLAY='127.0.0.1:124',LIBGL_ALWAYS_SOFTWARE='true',ALSOFT_DRIVERS='null',LD_LIBRARY_PATH=str(RT/'xvfb/usr/lib/x86_64-linux-gnu')+':'+str(RT/'natives'))
cmd=[str(R/'jdk/bin/java'),'-Xmx1800M','-Dfabric.gameMappingNamespace=intermediary','-Dfabric.runtimeMappingNamespace=intermediary','-Dfabric.gameJarPath='+str(RT/'client-intermediary.jar'),'-Dpre30.baseline='+str(baseline).lower(),'-Djava.library.path='+str(RT/'natives'),'-cp',cp,'net.fabricmc.loader.impl.launch.knot.KnotClient','--gameDir',str(G),'--assetsDir',str(RT/'assets'),'--assetIndex','29','--version','1.21.11','--username','PRE30Test','--uuid','00000000000000000000000000000001','--accessToken','offline','--width','1920','--height','1080']
if world:cmd.insert(1,'-Dnexuscharacters.pre30.diagnostics=true')
if '--scene12' in sys.argv:cmd.insert(1,'-Dpre30.scene=12')
log=R/('qa/recapture.log' if '--scene12' in sys.argv else 'qa/future.log' if future else 'qa/world.log' if world else 'qa/baseline.log' if baseline else 'qa/render.log');print('CLIENT_START',flush=True)
xlog=(RT/'xvfb-client.log').open('w')
xserver=subprocess.Popen([str(RT/'xvfb/usr/bin/Xvfb-local'),':124','-screen','0','1920x1080x24','-ac','-nolisten','unix','-nolisten','local','-listen','tcp'],env=env,stdout=xlog,stderr=subprocess.STDOUT)
time.sleep(1)
try:
 with log.open('w') as f:result=subprocess.run(cmd,cwd=R.parent,env=env,stdout=f,stderr=subprocess.STDOUT)
finally:xserver.terminate();xserver.wait();xlog.close()
print(log.read_text()[-10000:]);print('CLIENT_EXIT',result.returncode)
if result.returncode:raise SystemExit(result.returncode)
