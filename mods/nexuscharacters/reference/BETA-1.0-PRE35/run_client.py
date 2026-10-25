from pathlib import Path
import json,os,subprocess,shutil,zipfile,sys,time,hashlib,datetime
R=Path(__file__).resolve().parent;RT=R.parent/'pre31/runtime';J=R.parent/'pre31/jdk/bin'
baseline='--baseline' in sys.argv;oracle='--oracle' in sys.argv
kind='baseline' if baseline else 'oracle' if oracle else 'candidate'
mode='world' if '--world' in sys.argv else 'future' if '--future' in sys.argv else 'perf' if '--perf' in sys.argv else 'menus'
candidate=R/('base/NexusCharacters-Indivis-1.21.11-PRE34-SI1.jar' if baseline else 'qa/NexusCharacters-PRE35-instrumented.jar' if oracle else 'NexusCharacters-Indivis-1.21.11-PRE35-SI1.jar')
run_id=kind+'-'+mode+'-'+datetime.datetime.now(datetime.UTC).strftime('%Y%m%dT%H%M%S')
G=R/'qa/games'/run_id;M=G/'mods';M.mkdir(parents=True)
for name in ['fabric-api','skinlayers','sodium','immediatelyfast']:shutil.copy2(RT/(name+'.jar'),M/(name+'.jar'))
for p in (R/'runtime-extra').glob('*.jar'):shutil.copy2(p,M/p.name)
shutil.copy2(candidate,M/'NexusCharacters-Test.jar')
cp=(RT/'classpath.txt').read_text()+':'+str(RT/'fabric-loader.jar')+':'+str(RT/'client-intermediary.jar')
api=R/'qa/dependency-classes';api.mkdir(exist_ok=True)
def nested(path):
 with zipfile.ZipFile(path) as z:
  for n in z.namelist():
   if n.endswith('.jar'):
    data=z.read(n);dest=api/(hashlib.sha256(data).hexdigest()[:16]+'-'+Path(n).name)
    if not dest.exists():dest.write_bytes(data);nested(dest)
for p in M.glob('*.jar'):nested(p)
ccp=cp+':'+':'.join(map(str,[*M.glob('*.jar'),*api.glob('*.jar')]))
classes=G/'classes';classes.mkdir()
sources=[str(p) for p in (R/'tests/client').rglob('*.java')]
subprocess.run([str(J/'javac'),'-proc:none','-cp',ccp,'-d',str(classes),*sources],check=True)
with zipfile.ZipFile(M/'harness.jar','w') as z:
 entry='WorldDiagnostic' if mode=='world' else 'FutureAssetsRegression' if mode=='future' else 'Pre30Regression'
 meta={'schemaVersion':1,'id':'pre30_harness','version':'1','environment':'client','entrypoints':{'client':[entry]}}
 if mode=='world':
  meta['mixins']=['pre30-test.mixins.json'];z.writestr('pre30-test.mixins.json',json.dumps({'required':True,'package':'pre30test','compatibilityLevel':'JAVA_21','client':['FrameAuditMixin','HandTraceMixin','PlayerTraceMixin']}))
 z.writestr('fabric.mod.json',json.dumps(meta))
 for p in classes.rglob('*.class'):z.write(p,p.relative_to(classes))
 if mode=='future':
  with zipfile.ZipFile(candidate) as base:
   for src,dst in [('appearance_parts_v064/hair/hair_long_07.png','appearance_parts_v064/hair/hair_930.png'),('appearance_parts_v068/facial_hair/beard_light_02.png','appearance_parts_v068/facial_hair/beard_930.png'),('appearance_parts_v064/outfits/outfit_22.png','appearance_parts_v064/outfits/outfit_930.png')]:z.writestr('assets/nexuscharacters/'+dst,base.read('assets/nexuscharacters/'+src))
(G/'options.txt').write_text('guiScale:2\nrenderDistance:3\nsimulationDistance:5\nmaxFps:60\n')
display=os.environ.get('PRE35_DISPLAY','125')
env=os.environ.copy();env.update(DISPLAY='127.0.0.1:'+display,LIBGL_ALWAYS_SOFTWARE='true',ALSOFT_DRIVERS='null',LD_LIBRARY_PATH=str(RT/'xvfb/usr/lib/x86_64-linux-gnu')+':'+str(RT/'natives'))
cmd=[str(J/'java'),'-Xmx1800M','-Dfabric.gameMappingNamespace=intermediary','-Dfabric.runtimeMappingNamespace=intermediary','-Dfabric.gameJarPath='+str(RT/'client-intermediary.jar'),'-Dpre30.baseline='+str(baseline).lower(),'-Djava.library.path='+str(RT/'natives'),'-Dnexuscharacters.pre30.diagnostics=true','-Dpre35.noMeasurements=true','-Dpre35.worldOutput='+str(G/'evidence'),'-Dpre35.oracle='+str(oracle).lower()]
if mode=='perf':cmd+=['-Dpre30.scene=22','-Dpre35.multiPerf=true']
cmd+=['-cp',cp,'net.fabricmc.loader.impl.launch.knot.KnotClient','--gameDir',str(G),'--assetsDir',str(RT/'assets'),'--assetIndex','29','--version','1.21.11','--username','PRE35Test','--uuid','00000000000000000000000000000001','--accessToken','offline','--width','1920','--height','1080']
started=time.time();log=R/'qa'/('run-'+run_id+'.log');print('CLIENT35_START',run_id,flush=True)
xlog=(G/'xvfb.log').open('w')
xserver=subprocess.Popen([str(RT/'xvfb/usr/bin/Xvfb-local'),':'+display,'-screen','0','1920x1080x24','-ac','-nolisten','unix','-nolisten','local','-listen','tcp'],env=env,stdout=xlog,stderr=subprocess.STDOUT)
time.sleep(1)
try:
 with log.open('w') as f:result=subprocess.run(cmd,cwd=R.parent,env=env,stdout=f,stderr=subprocess.STDOUT,timeout=540)
finally:xserver.terminate();xserver.wait();xlog.close()
output=log.read_text();print(output[-9000:],flush=True)
assert result.returncode==0 and '_FAILURE' not in output,'Client failed: '+str(log)
expected=G/'evidence/pass.txt' if mode=='world' else R/'qa/future-evidence/pass.txt' if mode=='future' else R/'qa/recapture/result.txt' if mode=='perf' else R/('qa/baseline-evidence/result.txt' if baseline else 'qa/evidence/result.txt')
assert expected.exists() and expected.stat().st_mtime>=started,'Missing fresh completion '+str(expected)
if mode=='world':
 dest=R/'qa'/('baseline-world-evidence' if baseline else 'oracle-world-evidence' if oracle else 'world-evidence')
 shutil.copytree(G/'evidence',dest,dirs_exist_ok=True)
record={'run_id':run_id,'start_utc':datetime.datetime.fromtimestamp(started,datetime.UTC).isoformat(),'end_utc':datetime.datetime.now(datetime.UTC).isoformat(),'jar_sha256':hashlib.sha256(candidate.read_bytes()).hexdigest(),'log':str(log.relative_to(R)),'completion':expected.read_text()}
(R/'qa'/('last-'+kind+'-'+mode+'.json')).write_text(json.dumps(record,indent=2)+'\n')
print('CLIENT35_PASS',json.dumps(record),flush=True)
