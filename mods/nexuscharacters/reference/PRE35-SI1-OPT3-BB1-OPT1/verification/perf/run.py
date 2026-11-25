"""Run read-only comparisons of the delivered BB1 and its OPT3 base in isolated clients."""
from pathlib import Path
import argparse,datetime,hashlib,json,os,shutil,subprocess,sys,time,zipfile
R=Path(__file__).resolve().parent;P=R.parent/'presets_import';RT=P/'runtime';J=RT/'jdk/bin'
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
p=argparse.ArgumentParser();p.add_argument('version',choices=['OPT3','BB1']);p.add_argument('--label',required=True);p.add_argument('--pilot',action='store_true');p.add_argument('--jfr',action='store_true');p.add_argument('--freeze',action='store_true');p.add_argument('--jar',type=Path);p.add_argument('--expected-sha');p.add_argument('--animated-only',action='store_true');a=p.parse_args()
jar=P/('out/Nexus-BB1-1.21.11.jar'if a.version=='BB1'else'base/NexusCharacters-Indivis-1.21.11-PRE35-SI1-OPT3(1).jar')
expected='a5f0c4a211a64503e30ffb93a6499a9172b7c22183542b18fc2e1a4cd231b10f'if a.version=='BB1'else'de06ecab0f5cf7ccee29043bff2ccb5fd2b7425c173da145c1f27559b49def7b'
if a.jar:
 assert a.expected_sha and a.version=='BB1' and not a.freeze,'Explicit candidate SHA and BB1 required; frozen candidates forbidden'
 jar=a.jar.resolve();expected=a.expected_sha
assert not (R/(a.label+'.json')).exists(),'Do not overwrite a completed measurement'
assert sha(jar)==expected,'Binary differs from delivered/reference version'
stamp=datetime.datetime.now(datetime.UTC).strftime('%Y%m%dT%H%M%S');game=R/'games'/(stamp+'-'+a.label);mods=game/'mods';mods.mkdir(parents=True);out=game/'evidence';out.mkdir()
shutil.copy2(jar,mods/'NexusCharacters.jar');shutil.copy2(P/'previous/npc/Indivis-Nexus-NPC-0.1.0-PREVIEW1-1.21.11.jar',mods/'Indivis-Nexus-NPC.jar')
for name in ['fabric-api','skinlayers','sodium','immediatelyfast']:shutil.copy2(RT/'mods'/(name+'.jar'),mods/(name+'.jar'))
for folder in [P/'previous/npc/base',P/'previous/npc/runtime-extra',P/'previous/pre35/runtime-extra']:
 for path in folder.glob('*.jar'):
  if path.name.startswith('NexusCharacters')or'entityculling'in path.name.lower():continue
  shutil.copy2(path,mods/path.name)
cp=':'.join(x for x in (RT/'classpath.txt').read_text().split(':')if'/fabric-loader/'not in x)+':'+str(RT/'fabric-loader.jar')+':'+str(RT/'client-intermediary.jar')
nested=R/'compile-deps';nested.mkdir(exist_ok=True)
def unpack(path):
 with zipfile.ZipFile(path)as z:
  for n in z.namelist():
   if n.endswith('.jar'):
    data=z.read(n);dest=nested/(hashlib.sha256(data).hexdigest()[:16]+'-'+Path(n).name)
    if not dest.exists():dest.write_bytes(data);unpack(dest)
for path in mods.glob('*.jar'):unpack(path)
ccp=cp+':'+':'.join(map(str,[*mods.glob('*.jar'),*nested.glob('*.jar')]))
classes=game/'classes';classes.mkdir()
sources=[*(R/'src').rglob('*.java'),*((R/'control-src').rglob('*.java')if a.freeze else [])]
compiled=subprocess.run([str(J/'javac'),'-proc:none','-encoding','UTF-8','-cp',ccp,'-d',str(classes),*map(str,sources)],capture_output=True,text=True)
if compiled.returncode:print(compiled.stderr);raise SystemExit(compiled.returncode)
with zipfile.ZipFile(mods/'perf-harness.jar','w')as z:
 z.writestr('fabric.mod.json',json.dumps({'schemaVersion':1,'id':'indivis_perf_qa','version':'1','environment':'client','entrypoints':{'client':['perfqa.PerfMain']},'mixins':['perf-qa.mixins.json']+(['freeze-qa.mixins.json']if a.freeze else [])}))
 z.writestr('perf-qa.mixins.json',json.dumps({'required':True,'package':'perfqa.mixin','compatibilityLevel':'JAVA_21','client':['FrameTrace','NativeTrace','CosmeticTrace','NpcPose','PlayerPose'],'injectors':{'defaultRequire':1}}))
 if a.freeze:z.writestr('freeze-qa.mixins.json',json.dumps({'required':True,'package':'freezeqa.mixin','compatibilityLevel':'JAVA_21','client':['FreezePose'],'injectors':{'defaultRequire':1}}))
 for file in classes.rglob('*.class'):z.write(file,file.relative_to(classes))
with zipfile.ZipFile(P/'out/Nexus-BB1-1.21.11.jar')as z:catalog=json.loads(z.read('assets/nexuscharacters/blockbench/catalog.json'))['entries']
heavy={}
for race in ['human','nordic','dwarf','wood_elf','high_elf']:
 row={}
 for kind,key in [('coiffures','hair_id'),('oreilles','ears'),('barbes','beard')]:
  choices=[e for e in catalog if e['kind']==kind and race in e['races']and not e['alias_none']]
  e=max(choices,key=lambda v:v['cubes'])if choices else None
  row[key]=e['hair_id']if e and kind=='coiffures'else e['id']if e else'none'
  row[key+'_cubes']=e['cubes']if e else 0
 heavy[race]=row
recipes=game/'heavy-recipes.json';recipes.write_text(json.dumps(heavy,indent=2))
(game/'options.txt').write_text('guiScale:2\nrenderDistance:2\nsimulationDistance:5\nmaxFps:260\nenableVsync:false\npauseOnLostFocus:false\nrenderClouds:false\ngraphicsMode:0\nmipmapLevels:0\nentityDistanceScaling:1.0\nparticles:2\n')
env=os.environ.copy();env.update(DISPLAY='127.0.0.1:148',LIBGL_ALWAYS_SOFTWARE='true',LP_NUM_THREADS='4',ALSOFT_DRIVERS='null',LD_LIBRARY_PATH=str(RT/'natives'))
cmd=[str(J/'java'),'-Xms1024M','-Xmx2600M','-XX:ActiveProcessorCount=4','-Dperfqa.out='+str(out),'-Dperfqa.bb='+str(a.version=='BB1').lower(),'-Dperfqa.pilot='+str(a.pilot).lower(),'-Dperfqa.recipes='+str(recipes),'-Dfabric.gameMappingNamespace=intermediary','-Dfabric.runtimeMappingNamespace=intermediary','-Dfabric.gameJarPath='+str(RT/'client-intermediary.jar'),'-Djava.library.path='+str(RT/'natives'),'-cp',cp,'net.fabricmc.loader.impl.launch.knot.KnotClient','--gameDir',str(game),'--assetsDir',str(RT/'assets'),'--assetIndex','29','--version','1.21.11','--username','PerfQa','--uuid','00000000000000000000000000000001','--accessToken','offline','--width','800','--height','450']
log=game/'client.log';start=time.time();print('PERF_CLIENT_START',a.label,game,flush=True)
cmd.insert(1,'-Dperfqa.animatedOnly='+str(a.animated_only).lower())
if a.jfr:cmd.insert(1,'-XX:StartFlightRecording=filename='+str(out/'profile.jfr')+',settings=profile,dumponexit=true')
with(game/'display.log').open('w')as display_log:
 display=subprocess.Popen(['Xorg',':148','-config',str(P/'dummy-xorg.conf'),'-noreset','-novtswitch','-nolisten','unix','-nolisten','local','-listen','tcp','-ac','-logfile',str(game/'xorg.log')],env=env,stdout=display_log,stderr=subprocess.STDOUT)
 try:
  time.sleep(1)
  with log.open('w')as stream:result=subprocess.run(cmd,cwd=game,env=env,stdout=stream,stderr=subprocess.STDOUT,timeout=900)
 finally:display.terminate();display.wait()
if result.returncode or(out/'failure.txt').exists()or not(out/'pass.txt').exists():
 print(log.read_text()[-5000:]);raise SystemExit('PERF_CLIENT_FAILED '+str(game))
record={'label':a.label,'version':a.version,'pilot':a.pilot,'freeze':a.freeze,'jfr':a.jfr,'animated_only':a.animated_only,'jar_sha256':sha(jar),'game':str(game),'evidence':str(out),'start_utc':datetime.datetime.fromtimestamp(start,datetime.UTC).isoformat(),'end_utc':datetime.datetime.now(datetime.UTC).isoformat(),'pass':(out/'pass.txt').read_text(),'resolution':[800,450],'java_processors':4,'software_renderer_threads':4,'entityculling':False,'diagnostics':False,'mods':{p.name:sha(p)for p in sorted(mods.glob('*.jar'))}}
(R/(a.label+'.json')).write_text(json.dumps(record,indent=2)+'\n');print('PERF_CLIENT_PASS',a.label,record['pass'].strip(),flush=True)
