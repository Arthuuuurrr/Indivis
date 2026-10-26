from pathlib import Path
import os,json,shutil,zipfile,hashlib,subprocess,time,datetime,sys
R=Path(__file__).resolve().parent;RT=R.parent/'pre31/runtime';J=R.parent/'pre31/jdk/bin'
stamp=datetime.datetime.now(datetime.UTC).strftime('%Y%m%dT%H%M%S');kind=os.environ.get('NPC_QA_MODE','npc');menus=kind.startswith('menus');baseline=kind=='menus-baseline';collection=kind=='collection'
G=R/'qa/games'/stamp;M=G/'mods';M.mkdir(parents=True)
reload_from=os.environ.get('NPC_RELOAD_FROM')
if reload_from:
 old=Path(reload_from).resolve();shutil.copytree(old/'saves',G/'saves');shutil.copytree(old/'config/indivis-nexus-npc',G/'config/indivis-nexus-npc')
if collection:
 shutil.copytree(R/'collections/morphologies-v2/staging/config',G/'config',dirs_exist_ok=True)
for p in (R/'base').glob('*.jar'):shutil.copy2(p,M/p.name)
addon=R/'Indivis-Nexus-NPC-0.1.0-PREVIEW1-1.21.11.jar'
if not baseline:shutil.copy2(addon,M/addon.name)
for name in ['fabric-api','skinlayers','sodium','immediatelyfast']:shutil.copy2(RT/(name+'.jar'),M/(name+'.jar'))
for folder in [R/'runtime-extra',R.parent/'nexus-pre35/runtime-extra']:
 for p in folder.glob('*.jar'):shutil.copy2(p,M/p.name)
cp=':'.join(p for p in (RT/'classpath.txt').read_text().split(':') if '/fabric-loader/' not in p)+':'+str(RT/'fabric-loader.jar')+':'+str(RT/'client-intermediary.jar')
compile_jars=R/'qa/dependency-classes';compile_jars.mkdir(exist_ok=True)
def nested(path):
 with zipfile.ZipFile(path) as z:
  for n in z.namelist():
   if n.endswith('.jar'):
    data=z.read(n);dest=compile_jars/(hashlib.sha256(data).hexdigest()[:16]+'-'+Path(n).name)
    if not dest.exists():dest.write_bytes(data);nested(dest)
for p in M.glob('*.jar'):nested(p)
ccp=cp+':'+':'.join(map(str,[*M.glob('*.jar'),*compile_jars.glob('*.jar')]))
C=G/'classes';C.mkdir()
sources=list(map(str,(R.parent/'nexus-pre35/tests/client').rglob('*.java'))) if menus else [*map(str,(R/'tests/client').rglob('*.java')),str(R.parent/'nexus-pre35/tests/client/RenderPerf.java')]
subprocess.run([str(J/'javac'),'-proc:none','-cp',ccp,'-d',str(C),*sources],check=True)
with zipfile.ZipFile(M/'npc-harness.jar','w') as z:
 meta={'schemaVersion':1,'id':'indivis_npc_qa','version':'1','environment':'client','entrypoints':{'client':['Pre30Regression' if menus else 'NpcIntegrationTest']}}
 if not menus:meta['mixins']=['npc-qa.mixins.json'];z.writestr('npc-qa.mixins.json',json.dumps({'required':True,'package':'npctest','compatibilityLevel':'JAVA_21','client':['NpcTrace','FeatureTrace','FacingFixture']}))
 z.writestr('fabric.mod.json',json.dumps(meta))
 for p in C.rglob('*.class'):z.write(p,p.relative_to(C))
 if kind=='future':
  with zipfile.ZipFile(R/'base/NexusCharacters-Indivis-1.21.11-PRE35-SI1.jar') as base:
   for src,dst in [('appearance_parts_v064/hair/hair_long_07.png','appearance_parts_v064/hair/hair_930.png'),('appearance_parts_v068/facial_hair/beard_light_02.png','appearance_parts_v068/facial_hair/beard_930.png'),('appearance_parts_v064/outfits/outfit_22.png','appearance_parts_v064/outfits/outfit_930.png')]:z.writestr('assets/nexuscharacters/'+dst,base.read('assets/nexuscharacters/'+src))
(G/'options.txt').write_text('guiScale:2\nrenderDistance:3\nsimulationDistance:5\nmaxFps:60\n')
env=os.environ.copy();display=os.environ.get('NPC_DISPLAY','135');env.update(DISPLAY='127.0.0.1:'+display,LIBGL_ALWAYS_SOFTWARE='true',ALSOFT_DRIVERS='null',LD_LIBRARY_PATH=str(RT/'xvfb/usr/lib/x86_64-linux-gnu')+':'+str(RT/'natives'))
out=G/'evidence';out.mkdir();log=R/'qa'/('client-'+stamp+'.log')
cmd=[str(J/'java'),'-Xmx2000M','-Dnpcqa.future='+str(kind=='future').lower(),'-Dpre30.baseline='+str(baseline).lower(),'-Dpre35.noMeasurements=true','-Dnpcqa.reload='+str(bool(reload_from)).lower(),'-Dfabric.gameMappingNamespace=intermediary','-Dfabric.runtimeMappingNamespace=intermediary','-Dfabric.gameJarPath='+str(RT/'client-intermediary.jar'),'-Djava.library.path='+str(RT/'natives'),'-Dnpcqa.out='+str(out),'-Dnexuscharacters.pre30.diagnostics=true','-cp',cp,'net.fabricmc.loader.impl.launch.knot.KnotClient','--gameDir',str(G),'--assetsDir',str(RT/'assets'),'--assetIndex','29','--version','1.21.11','--username','NpcQa','--uuid','00000000000000000000000000000001','--accessToken','offline','--width','1920','--height','1080']
xlog=(G/'xvfb.log').open('w');xs=subprocess.Popen([str(RT/'xvfb/usr/bin/Xvfb-local'),':'+display,'-screen','0','1920x1080x24','-ac','-nolisten','unix','-nolisten','local','-listen','tcp'],env=env,stdout=xlog,stderr=subprocess.STDOUT)
cmd.insert(1,'-Dnpcqa.visual='+str(kind=='visual' or collection).lower())
cmd.insert(1,'-Dnpcqa.collection='+str(collection).lower())
start=time.time();time.sleep(1);print('NPC_CLIENT_START',stamp,flush=True)
try:
 with log.open('w') as f:result=subprocess.run(cmd,cwd=R.parent,env=env,stdout=f,stderr=subprocess.STDOUT,timeout=420)
finally:xs.terminate();xs.wait();xlog.close()
output=log.read_text();print(output[-2000:])
if menus:
 old=R.parent/'nexus-pre35/qa';folder=old/('baseline-evidence' if baseline else 'evidence');geom=old/('baseline' if baseline else 'candidate')
 assert (folder/'result.txt').stat().st_mtime>=start and '_FAILURE' not in output,'Menu regression failed'
 shutil.copytree(folder,out,dirs_exist_ok=True);shutil.copytree(geom,out/'geometry',dirs_exist_ok=True);shutil.copy2(folder/'result.txt',out/'pass.txt')
assert result.returncode==0 and not (out/'failure.txt').exists() and (out/'pass.txt').exists(),'NPC client failure: '+str(out)
record={'mode':('collection-reload' if reload_from else 'collection') if collection else kind if kind=='visual' else 'reload' if reload_from else kind,'jar_sha256':None if baseline else hashlib.sha256(addon.read_bytes()).hexdigest(),'start_utc':datetime.datetime.fromtimestamp(start,datetime.UTC).isoformat(),'end_utc':datetime.datetime.now(datetime.UTC).isoformat(),'game':str(G),'log':str(log),'pass':(out/'pass.txt').read_text()}
(R/('qa/last-'+record['mode']+'.json')).write_text(json.dumps(record,indent=2,ensure_ascii=False)+'\n')
shutil.copytree(out,R/('qa/evidence-'+record['mode']),dirs_exist_ok=True)
print('NPC_CLIENT_PASS',record['mode'],record['jar_sha256'],str(out),flush=True)
