"""Fixture complète HF9/C1, puis parcours PQ03 ; JAR exact livré, carte synthétique."""
from pathlib import Path
import os,sys,json,shutil,uuid
import build
H=Path(__file__).resolve().parent;OLD=H.parent/'pq02';C1=next(p for p in [H.parent/'hc_quetes_merge_20261101',H.parent/'hc_quetes_merge_c1'] if p.exists());REG=H/'regression';REG.mkdir(exist_ok=True)
for folder in ['reference','audit_prologue','dependencies']:
 target=REG/folder
 if not target.exists():target.symlink_to(OLD/folder,target_is_directory=True)
shutil.copytree(OLD/'tests',REG/'tests',dirs_exist_ok=True)
for p in (H/'tests').glob('*.java'):shutil.copy2(p,REG/'tests'/p.name)
shutil.copy2(C1/'HcQuetesMergeBench.java',REG/'tests/HcQuetesMergeBench.java')
p=REG/'tests/Pq02WorldBench.java';s=p.read_text();s=s.replace('HF12 classes preserved except the two authorized Gautier timing classes; production PQ02 JAR+mixins','PQ03-R1 production JAR+mixins; PQ01 and PQ02 regression suites');s=s.replace('Pq02EndToEndBench.tick())finish();','Pq02EndToEndBench.tick()&&Pq03WorldBench.tick())finish();')
needle='Object p=owners.get(sink);if(p==null)return;';assert needle in s;s=s.replace(needle,needle+'Pq03WorldBench.packet(p,packet);')
s=s.replace('make("com.mojang.authlib.GameProfile",UUID.randomUUID(),name)','make("com.mojang.authlib.GameProfile",(name.equals("PQ02_Observer")&&System.getProperty("pq03.reboot.owner")!=null?UUID.fromString(System.getProperty("pq03.reboot.owner")):UUID.randomUUID()),name)')
s=s.replace('stub(x[0],"capidentity");','stub(x[0],"capidentity");stub(x[0],"mmospawn");');p.write_text(s)
p=REG/'tests/Pq02EndToEndBench.java';s=p.read_text();s=s.replace('Pq02AutomaticGiftBench.runAll(a);done=true;','Pq02AutomaticGiftBench.runAll(a);HcQuetesMergeBench.runAll(a,b);done=true;')
old='check(score(a,"hcq_pq03")==10&&score(a,"CAP_QUETEACTIVE")==3&&score(a,"CAP_QSEQ")==701,"original Aurele router naturally opens PQ03 offer after PQ02");'
new='check(Pq03Entries.PENDING.containsKey(uuid(a))&&score(a,"CAP_QSEQ")!=701,"PQ03 router queues its own private introduction after PQ02");Pq03Entries.cancel(uuid(a));'
assert old in s;s=s.replace(old,new);p.write_text(s)
p=REG/'tests/Pq02FullSystemBench.java';s=p.read_text();needle='if(!Pq02Runtime.VIEWS.containsKey(uuid(player))&&!Pq02Entries.PENDING.containsKey(uuid(player)))';assert needle in s;s=s.replace(needle,'if(!Pq02Runtime.VIEWS.containsKey(uuid(player))&&!Pq02Entries.PENDING.containsKey(uuid(player))&&!Pq03Runtime.VIEWS.containsKey(uuid(player))&&!Pq03Entries.PENDING.containsKey(uuid(player)))');p.write_text(s)
(REG/'output').mkdir(exist_ok=True);(REG/'reports').mkdir(exist_ok=True)
build.JOURNAL_FILENAME=build.FILES['mod']
for name in build.FILES.values():shutil.copy2(H/'output'/name,REG/'output'/name)
label='pq03_full_system_'+uuid.uuid4().hex[:8];source=(OLD/'run_native.py').read_text().replace("RT=Path('/tmp/indivis_pq02_runtime')",'RT=Path('+repr(str(build.RT.resolve()))+')').replace('pq02_full_system_hf8',label)
needle="sources.append(H/'tests/Pq02AutomaticGiftBench.java')";assert needle in source
source=source.replace(needle,needle+"\nsources += [H/'tests/Pq02PacingBench.java',H/'tests/Pq03WorldBench.java',H/'tests/Pq03RestartBench.java',H/'tests/HcQuetesMergeBench.java']")
needle='for files in orig.values():\n for n,b in files.items():';assert needle in source
extra=['data/capitale/function/pq03/','data/capitale/function/npc/aurele/','data/capitale/function/quest/sous_le_regard_du_coeur/','data/hc_quetes/function/aurele/','data/hc_quetes/function/carrec/','data/hc_quetes/function/hector/q5/','data/hc_quetes/function/pnj/aveline/','data/hc_quetes/function/lib/balayage.mcfunction','data/hc_quetes/function/marqueurs/aurele.mcfunction','data/hc_quetes/hcq_quests/']
source=source.replace(needle,'paths += '+repr(extra)+'\n'+needle)
needle="pack['data/fixture/function/objectives.mcfunction']=";assert needle in source
source=source.replace(needle,"scores.update(re.findall(r'scoreboard objectives add (\\S+)',orig['quests']['data/hc_quetes/function/load.mcfunction'].decode()))\nscores.update({'fixture_aurele_skills','fixture_aurele_bonus'})\n"+needle)
needle='for n,b in pack.items():p=P/n;';assert needle in source
substitutes=['rec_vivres','rec_arbalete','rec_piece','rec_jambieres','rec_collier','rec_bicorne','rec_pique']
code="for loot in "+repr(substitutes)+":\n key='data/hc_quetes/function/objets/'+loot+'.mcfunction'\n if key in pack:pack[key]=b'return 1\\n'\n"
code+="pack['data/capskills/function/reward/core/sous_regard_coeur_self.mcfunction']=b'scoreboard players add @s fixture_aurele_skills 1\\n'\npack['data/capitale/function/rewards/daily_bonus/roll/tech_self.mcfunction']=b'scoreboard players add @s fixture_aurele_bonus 1\\n'\n"
source=source.replace(needle,code+needle)
os.environ['PQ02_INTEGRATED']='1';os.environ['PQ02_FULL_SYSTEM']='1'
exec(compile(source,str(REG/'run_native.py'),'exec'),{'__name__':'__main__','__file__':str(REG/'run_native.py')})
for suffix in ['.txt','_fixture.json','_server.log']:shutil.copy2(REG/'reports'/(label+suffix),H/'reports'/('native'+suffix))
# Restart the same saved world with the production JAR byte-identical, replacing
# only the test fixture's entrypoint. No data or source NPC/marker is recreated.
import subprocess
case=build.RT/label;fixture=case/'mods/journal_test.jar';content=build.read(fixture);meta=json.loads(content['fabric.mod.json']);meta['entrypoints']['main']=['net.hautecapitale.quetes.sequence.Pq03RestartBench'];content['fabric.mod.json']=json.dumps(meta).encode();build.write(fixture,content)
java=next((build.RT/'jdk').glob('*/bin/java'));game=build.RT/'minecraft_bundle/META-INF/versions/1.21.11/server-1.21.11.jar';libs=[*sorted((build.RT/'libraries').glob('*.jar')),*sorted((build.RT/'minecraft_bundle/META-INF/libraries').rglob('*.jar')),game]
result=case/'pq03-restart-result.txt';result.unlink(missing_ok=True)
with (H/'reports/restart_server.log').open('w') as f:
 r=subprocess.run([java,'-XX:ActiveProcessorCount=2','-Xmx2G','-Dpq02.full.system=true',f'-Dfabric.gameJarPath={game.resolve()}','-Dfabric.log.disableAnsi=true','-cp',os.pathsep.join(str(p.resolve()) for p in libs),'net.fabricmc.loader.impl.launch.knot.KnotServer','nogui'],cwd=case,stdout=f,stderr=subprocess.STDOUT,timeout=180)
assert r.returncode==0 and result.exists(),(H/'reports/restart_server.log').read_text()[-5000:]
shutil.copy2(result,H/'reports/restart.txt');print(result.read_text());assert result.read_text().startswith('PASS'),result.read_text()
