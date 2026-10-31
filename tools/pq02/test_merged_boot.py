"""Load the exact integrated artifact without rewriting its metadata or entrypoints."""
from pathlib import Path
import json,zipfile,shutil,subprocess,os,re
import build,merge_journal
H=Path(__file__).resolve().parent;RT=Path('/tmp/indivis_pq02_runtime');JAVA=next((RT/'jdk').glob('*/bin/java'))
BASE=RT/'merged_boot';BASE.mkdir(exist_ok=True);C=H/'boot_classes';C.mkdir(exist_ok=True)
MC=RT/'bootstrap/.fabric/remappedJars/minecraft-1.21.11-0.19.5/server-intermediary.jar'
api=[]
with zipfile.ZipFile(RT/'fabric-api.jar') as z:
 for n in z.namelist():
  if n.endswith('.jar'):
   p=RT/'api_modules'/Path(n).name;p.parent.mkdir(exist_ok=True);p.write_bytes(z.read(n));api.append(p)
libs=[*sorted((RT/'libraries').glob('*.jar')),*sorted((RT/'minecraft_bundle/META-INF/libraries').rglob('*.jar')),MC,*api]
cp=os.pathsep.join(map(str,libs));subprocess.run([JAVA.with_name('javac'),'--release','21','-proc:none','-cp',cp,'-d',C,H/'tests/MergeBoot.java'],check=True)
mods=BASE/'mods';shutil.rmtree(mods,ignore_errors=True);mods.mkdir()
shutil.copyfile(merge_journal.OUTPUT,mods/merge_journal.OUTPUT.name)
shutil.copyfile(RT/'fabric-api.jar',mods/'fabric-api.jar')
shutil.copyfile(next((H/'dependencies').glob('*.jar')),mods/'easy-npc.jar')
shutil.copyfile(next((build.ROOT/'upload').glob('haute-capitale-dialogue*.jar')),mods/'dialogue-original.jar')
for name,version in {'nexuscharacters':'1.0.0-beta.35','puffish_skills':'0.17.3','haute_capitale_rpg':'0.3.0'}.items():
 if name=='nexuscharacters':
  # Load the real OPT3 class as a Mixin target, without enabling its account/network startup.
  with zipfile.ZipFile(next((H/'dependencies/audit_nexus_opt3').glob('*.jar'))) as z:entries={n:z.read(n) for n in z.namelist() if not n.endswith('/')}
  metadata=json.loads(entries['fabric.mod.json']);metadata['entrypoints']={};metadata['mixins']=[];metadata['depends']={'minecraft':'1.21.11'};entries['fabric.mod.json']=json.dumps(metadata).encode();build.write(mods/'nexus-original-classes-test.jar',entries)
  continue
 with zipfile.ZipFile(mods/(name+'-declaration-test.jar'),'w') as z:z.writestr('fabric.mod.json',json.dumps({'schemaVersion':1,'id':name,'version':version,'description':'TEST ONLY: declared dependency, no implementation'}))
with zipfile.ZipFile(mods/'merge-boot-test.jar','w') as z:
 for p in C.glob('*.class'):z.write(p,p.name)
 z.writestr('fabric.mod.json',json.dumps({'schemaVersion':1,'id':'merge_boot_test','version':'1.0.0','entrypoints':{'main':['MergeBoot']},'depends':{'fabric-api':'*'}}))
GAME=RT/'minecraft_bundle/META-INF/versions/1.21.11/server-1.21.11.jar';cp=os.pathsep.join(map(str,[*sorted((RT/'libraries').glob('*.jar')),*sorted((RT/'minecraft_bundle/META-INF/libraries').rglob('*.jar')),GAME]))
command=[JAVA,'-XX:ActiveProcessorCount=2','-Xmx2G',f'-Dfabric.gameJarPath={GAME}','-Dfabric.log.disableAnsi=true','-cp',cp,'net.fabricmc.loader.impl.launch.knot.KnotServer','nogui']
(BASE/'eula.txt').write_text('eula=true\n');(BASE/'server.properties').write_text('online-mode=false\nserver-ip=127.0.0.1\nserver-port=25581\nview-distance=2\nsimulation-distance=2\nmax-tick-time=0\npause-when-empty-seconds=0\n')
# Wrong installation must be rejected by Fabric before loading duplicate runtimes.
shutil.copyfile(merge_journal.COMPANION,mods/'obsolete-pq02-companion.jar')
with (H/'reports/merge_duplicate_guard.log').open('w') as f:r=subprocess.run(command,cwd=BASE,stdout=f,stderr=subprocess.STDOUT,timeout=90)
guard=(H/'reports/merge_duplicate_guard.log').read_text();assert r.returncode!=0 and 'indivis_pq02' in guard and ('breaks' in guard or 'incompatible' in guard.lower()),guard[-6000:]
(mods/'obsolete-pq02-companion.jar').unlink()
with (H/'reports/merge_boot.log').open('w') as f:r=subprocess.run(command,cwd=BASE,stdout=f,stderr=subprocess.STDOUT,timeout=180)
result=BASE/'smoke-result.txt';assert r.returncode==0 and result.exists() and result.read_text().startswith('PASS'),(H/'reports/merge_boot.log').read_text()[-8000:]
shutil.copyfile(result,H/'reports/merge_boot.txt')
summary={'status':'PASS','production_jar_sha256':build.sha(merge_journal.OUTPUT.read_bytes()),'main_startup_checks':9,'duplicate_companion_rejected':True,'production_metadata_and_entrypoints_unchanged_in_smoke_test':True,'nexus_periodic_mixin_applied_to_original_opt3_class':True,'dependency_substitutes':['Nexus original OPT3 classes, no account/network entrypoints','Puffish metadata only','RPG metadata only'],'players':'none','graphical_client':'not run'}
(H/'reports/merge_boot.json').write_text(json.dumps(summary,indent=2)+'\n');print(json.dumps(summary,indent=2))
