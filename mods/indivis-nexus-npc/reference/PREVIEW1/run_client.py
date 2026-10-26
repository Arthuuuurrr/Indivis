from pathlib import Path
import os,json,shutil,zipfile,hashlib,subprocess,time,datetime,sys
R=Path(__file__).resolve().parent;RT=R.parent/'pre31/runtime';J=R.parent/'pre31/jdk/bin'
stamp=datetime.datetime.now(datetime.UTC).strftime('%Y%m%dT%H%M%S');kind='npc'
G=R/'qa/games'/stamp;M=G/'mods';M.mkdir(parents=True)
reload_from=os.environ.get('NPC_RELOAD_FROM')
if reload_from:
 old=Path(reload_from).resolve();shutil.copytree(old/'saves',G/'saves');shutil.copytree(old/'config/indivis-nexus-npc',G/'config/indivis-nexus-npc')
for p in (R/'base').glob('*.jar'):shutil.copy2(p,M/p.name)
addon=R/'Indivis-Nexus-NPC-0.1.0-PREVIEW1-1.21.11.jar';shutil.copy2(addon,M/addon.name)
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
sources=[*map(str,(R/'tests/client').rglob('*.java')),str(R.parent/'nexus-pre35/tests/client/RenderPerf.java')]
subprocess.run([str(J/'javac'),'-proc:none','-cp',ccp,'-d',str(C),*sources],check=True)
with zipfile.ZipFile(M/'npc-harness.jar','w') as z:
 z.writestr('fabric.mod.json',json.dumps({'schemaVersion':1,'id':'indivis_npc_qa','version':'1','environment':'client','entrypoints':{'client':['NpcIntegrationTest']},'mixins':['npc-qa.mixins.json']}))
 z.writestr('npc-qa.mixins.json',json.dumps({'required':True,'package':'npctest','compatibilityLevel':'JAVA_21','client':['NpcTrace','FeatureTrace']}))
 for p in C.rglob('*.class'):z.write(p,p.relative_to(C))
(G/'options.txt').write_text('guiScale:2\nrenderDistance:3\nsimulationDistance:5\nmaxFps:60\n')
env=os.environ.copy();display=os.environ.get('NPC_DISPLAY','135');env.update(DISPLAY='127.0.0.1:'+display,LIBGL_ALWAYS_SOFTWARE='true',ALSOFT_DRIVERS='null',LD_LIBRARY_PATH=str(RT/'xvfb/usr/lib/x86_64-linux-gnu')+':'+str(RT/'natives'))
out=G/'evidence';out.mkdir();log=R/'qa'/('client-'+stamp+'.log')
cmd=[str(J/'java'),'-Xmx2000M','-Dnpcqa.reload='+str(bool(reload_from)).lower(),'-Dfabric.gameMappingNamespace=intermediary','-Dfabric.runtimeMappingNamespace=intermediary','-Dfabric.gameJarPath='+str(RT/'client-intermediary.jar'),'-Djava.library.path='+str(RT/'natives'),'-Dnpcqa.out='+str(out),'-Dnexuscharacters.pre30.diagnostics=true','-cp',cp,'net.fabricmc.loader.impl.launch.knot.KnotClient','--gameDir',str(G),'--assetsDir',str(RT/'assets'),'--assetIndex','29','--version','1.21.11','--username','NpcQa','--uuid','00000000000000000000000000000001','--accessToken','offline','--width','1920','--height','1080']
xlog=(G/'xvfb.log').open('w');xs=subprocess.Popen([str(RT/'xvfb/usr/bin/Xvfb-local'),':'+display,'-screen','0','1920x1080x24','-ac','-nolisten','unix','-nolisten','local','-listen','tcp'],env=env,stdout=xlog,stderr=subprocess.STDOUT)
start=time.time();time.sleep(1);print('NPC_CLIENT_START',stamp,flush=True)
try:
 with log.open('w') as f:result=subprocess.run(cmd,cwd=R.parent,env=env,stdout=f,stderr=subprocess.STDOUT,timeout=420)
finally:xs.terminate();xs.wait();xlog.close()
print(log.read_text()[-10000:])
assert result.returncode==0 and not (out/'failure.txt').exists() and (out/'pass.txt').exists(),'NPC client failure: '+str(out)
record={'jar_sha256':hashlib.sha256(addon.read_bytes()).hexdigest(),'start_utc':datetime.datetime.fromtimestamp(start,datetime.UTC).isoformat(),'end_utc':datetime.datetime.now(datetime.UTC).isoformat(),'game':str(G),'log':str(log),'pass':(out/'pass.txt').read_text()}
(R/'qa/last-client.json').write_text(json.dumps(record,indent=2,ensure_ascii=False)+'\n')
shutil.copytree(out,R/'qa/evidence',dirs_exist_ok=True)
print('NPC_CLIENT_PASS',json.dumps(record),flush=True)
