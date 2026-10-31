"""Banc jetable, aucune carte serveur utilisateur ni JAR installé modifié."""
from pathlib import Path
import json,zipfile,subprocess,os,shutil,re
import build
H=Path(__file__).resolve().parent;RT=Path('/tmp/indivis_pq02_runtime');INTEGRATED=os.environ.get('PQ02_INTEGRATED')=='1';REPRO=os.environ.get('PQ02_REPRO_ENTRY')=='1';SCREEN_REPRO=os.environ.get('PQ02_REPRO_SCREEN')=='1';LABEL='hf3_screen_repro' if SCREEN_REPRO else 'hf2_repro' if REPRO else 'merged_native' if INTEGRATED else 'native';S=RT/LABEL;S.mkdir(exist_ok=True);(S/'mods').mkdir(exist_ok=True)
AUDIT=os.environ.get('PROLOGUE_AUDIT')=='1'
SCREEN_GUARD=os.environ.get('PQ02_SCREEN_GUARD','')
if SCREEN_GUARD:LABEL='hf4_screen_reset_repro' if SCREEN_GUARD=='legacy' else 'hf5_screen_guard_native';S=RT/LABEL;S.mkdir(exist_ok=True);(S/'mods').mkdir(exist_ok=True)
if AUDIT:LABEL='prologue_audit_native';S=RT/LABEL;S.mkdir(exist_ok=True);(S/'mods').mkdir(exist_ok=True)
JAVA=next((RT/'jdk').glob('*/bin/java'));MC=RT/'bootstrap/.fabric/remappedJars/minecraft-1.21.11-0.19.5/server-intermediary.jar'
JOURNAL=H/'output'/build.JOURNAL_FILENAME if INTEGRATED else next(build.ROOT.joinpath('upload').glob('haute-capitale-quetes*.jar'));DIALOGUE=next(build.ROOT.joinpath('upload').glob('haute-capitale-dialogue*.jar'));NPC=next(H.joinpath('dependencies').glob('*.jar'))
if (REPRO or SCREEN_REPRO) and os.environ.get('PQ02_REPRO_JAR'):JOURNAL=Path(os.environ['PQ02_REPRO_JAR']).resolve()
if SCREEN_GUARD and os.environ.get('PQ02_GUARD_JAR'):JOURNAL=Path(os.environ['PQ02_GUARD_JAR']).resolve()
libs=[*sorted((RT/'libraries').glob('*.jar')),*sorted((RT/'minecraft_bundle/META-INF/libraries').rglob('*.jar')),MC,JOURNAL,DIALOGUE,NPC]+([H/'output'/build.JOURNAL_FILENAME] if REPRO else [])+([] if INTEGRATED else [H/'output'/build.FILES['mod']])
CP=os.pathsep.join(map(str,libs));C=H/'native_classes';shutil.rmtree(C,ignore_errors=True);C.mkdir()
sources=[H/'tests/Pq02WorldBench.java',H/'tests/Pq02SetupBench.java',H/'tests/Pq02EntryBench.java',H/'tests/Pq02ScreenBench.java',H/'tests/Pq02ScreenNetwork.java',H/'tests/Pq01GautierBench.java',H/'tests/Pq02FocusBench.java',H/'tests/NexusCharacters.java',H/'tests/MakeSink.java',H/'tests/SkillsAPI.java',H/'tests/Pq02CaptureBench.java',H/'tests/Mq03Bench.java',H/'tests/HuguesBench.java',*list((H/'reference/tools/pq01_full/tests/native_capture_doubles').rglob('*.java')),H/'reference/tools/pq01_full/tests/client_doubles/net/hautecapitale/dialogue/client/capture/CaptureDialogueScreen.java']
sources.append(H/'tests/Pq01AuditBench.java')
sources=[p for p in sources if p.name not in ['class_310.java','class_634.java','DialogueCameraController.java','CameraFocus.java']]
sources += [p for p in (H/'tests/protocol_doubles').rglob('*.java')]
sources.append(H/'tests/Pq02ScreenGuardBench.java')
subprocess.run([JAVA.with_name('javac'),'--release','21','-proc:none','-cp',CP,'-d',C,*sources],check=True)
subprocess.run([JAVA,'-cp',str(C)+os.pathsep+CP,'MakeSink',C],check=True)
with zipfile.ZipFile(JOURNAL) as z:out={n:z.read(n) for n in z.namelist() if not n.endswith('/')}
meta=json.loads(out['fabric.mod.json']);meta['entrypoints']={'main':['net.hautecapitale.quetes.sequence.Pq02WorldBench']};meta['depends']={'fabricloader':'>=0.19.5','fabric-api':'*','minecraft':'1.21.11','easy_npc':'7.16.0'};meta['provides']=['nexuscharacters'];meta['mixins']=['indivis_pq01.mixins.json','pq02_test_tracker.mixins.json'];out['fabric.mod.json']=json.dumps(meta).encode()
if INTEGRATED:
 meta['mixins'].append('indivis_pq02.mixins.json');out['fabric.mod.json']=json.dumps(meta).encode()
if SCREEN_GUARD:
 guard_config=json.loads(out['indivis_pq02.mixins.json']);guard_config['mixins']+=guard_config.pop('client',[])
 out['indivis_pq02.mixins.json']=json.dumps(guard_config).encode()
out['pq02_test_tracker.mixins.json']=json.dumps({'required':True,'package':'net.hautecapitale.quetes.mixin','compatibilityLevel':'JAVA_21','mixins':['EntityTrackerMixin'],'injectors':{'defaultRequire':1}}).encode()
for p in C.rglob('*.class'):
 if not str(p.relative_to(C)).startswith('net/hautecapitale/dialogue/'):out[p.relative_to(C).as_posix()]=p.read_bytes()
build.write(S/'mods/journal_test.jar',out)
asm=RT/'libraries/asm-9.10.1.jar'
subprocess.run([JAVA.with_name('javac'),'-cp',asm,'-d',C,H/'reference/tools/pq01_full/tests/world/StripClientEnvironment.java'],check=True)
subprocess.run([JAVA,'-cp',str(C)+os.pathsep+str(asm),'StripClientEnvironment',DIALOGUE,C],check=True)
with zipfile.ZipFile(DIALOGUE) as z:out={n:z.read(n) for n in z.namelist() if not n.endswith('/')}
for p in (C/'net/hautecapitale/dialogue').rglob('*.class'):out[p.relative_to(C).as_posix()]=p.read_bytes()
meta=json.loads(out['fabric.mod.json']);meta['entrypoints']={};meta['depends'].pop('haute_capitale_rpg',None);out['fabric.mod.json']=json.dumps(meta).encode();build.write(S/'mods/dialogue_test.jar',out)
shutil.copyfile(NPC,S/'mods/easy_npc.jar');shutil.copyfile(RT/'fabric-api.jar',S/'mods/fabric-api.jar')
if not INTEGRATED:shutil.copyfile(H/'output'/build.FILES['mod'],S/'mods/pq02_production.jar')
else:assert not (S/'mods/pq02_production.jar').exists(),'Stale standalone PQ02 in integrated fixture'
(S/'eula.txt').write_text('eula=true\n');(S/'server.properties').write_text('online-mode=false\nserver-ip=127.0.0.1\nserver-port=25579\nview-distance=2\nsimulation-distance=2\nspawn-protection=0\nmax-tick-time=0\npause-when-empty-seconds=0\n')
# Fixture only: production already allows its quest functions. Do not ship or alter user security.
security=S/'config/easy_npc/security.cfg';security.parent.mkdir(parents=True,exist_ok=True)
lines=security.read_text().splitlines() if security.exists() else []
lines=[line for line in lines if not line.startswith('executeAsUserCommandAllowList.GAMEMASTERS=')]
lines=[line if not line.startswith('unsafeNpcCommands=') else 'unsafeNpcCommands='+','.join(s for s in line.partition('=')[2].split(',') if s!='function') for line in lines]
if not any(line.startswith('unsafeNpcCommands=') for line in lines):lines.append('unsafeNpcCommands=ban,ban-ip,debug,deop,forceload,kick,op,reload,save-all,stop,whitelist')
lines.append('executeAsUserCommandAllowList.GAMEMASTERS=function');security.write_text('\n'.join(lines)+'\n')
shutil.rmtree(S/'world',ignore_errors=True);P=S/'world/datapacks/pq02_fixture';P.mkdir(parents=True)
orig={k:build.read(H/'output'/build.FILES[k]) for k in ['core','quests']};pack={'pack.mcmeta':orig['core']['pack.mcmeta']}
# All newly changed functions plus all MQ03 runtime functions for native parser/route checks.
paths=['data/capitale/function/pq02/','data/capitale/function/pq01/menu/hugues','data/capitale/function/pq01/choice/hugues','data/hc_quetes/function/migration/write/pq02/','data/hc_quetes/function/quests/','data/hc_quetes/function/runtime/','data/hc_quetes/predicate/','data/capitale/predicate/','data/hc_quetes/tags/']
if AUDIT:paths+=['data/capitale/function/pq01/','data/capitale/function/quest/spawn_dirigeable_couronne/','data/capitale/function/quest/sous_le_regard_du_coeur/','data/capitale/function/quest/au_seuil_des_profondeurs/','data/capitale/function/quest/une_adresse_dans_les_profondeurs/','data/capitale/function/quest/prologue/','data/capitale/function/quest/markers/']
for files in orig.values():
 for n,b in files.items():
  if any(n.startswith(p) for p in paths) or '/worldgen/biome/' in n:pack[n]=b
for n in ['quest/le_registre_du_port/leovic/interact_self','quest/le_registre_du_port/magistrat/register_self','reward/skills/core/registre_port_self','reward/xp_vanilla/core/registre_port_self','bounds/reputation_all_self','bounds/rangsocial_self','access/recalculate_by_rank_self','display/prefix/sync_self','dialogue/sound/gain_quete_self','npc/altheon_brumeforge/interact_self','quest/spawn_dirigeable_couronne/capitaine_quai/interact_self','quest/spawn_dirigeable_couronne/capitaine_large/interact_self','pq01/open/altheon_self','pq01/node/a3','pq01/node/a6','pq01/node/a7']:
 key='data/capitale/function/'+n+'.mcfunction';pack[key]=orig['core'][key]
pack['data/capitale/function/pq02/gift_once_self.mcfunction']=pack['data/capitale/function/pq02/gift_once_self.mcfunction'].replace(b'capitale_currency:martin_dor',b'minecraft:gold_nugget')
# External integrations are recorded substitutes, not replacements in delivered packs.
for fn,body in {'capskills:reward/core/registre_port_self':'scoreboard players add @s fixture_skills 1\n','capitale:rewards/daily_bonus/roll/port_self':'scoreboard players add @s fixture_bonus 1\n'}.items():
 ns,path=fn.split(':');pack['data/'+ns+'/function/'+path+'.mcfunction']=body.encode()
scores={'hcq_pq01','hcq_pq02','hcq_mq01','hcq_mq02','hcq_mq03','hcq_mq04','CAP_PROFILE_LOCK','CAP_QUETEACTIVE','CAP_QDIALOG_OWNER','CAP_QDIALOG_KEEP','CAP_QSEQ','CAP_QSEQ_TIMER','CAP_RANGSOCIAL','CAP_POINTS_TOTAL','REP_GARDEPORT','QUEST_PROLOGUE','CAP_GUIDE_LOCK','CAP_GUIDE_ID','CAP_GUIDE_MISS_T','CAPSK_REWARD_REGPORT','fixture_bonus','fixture_skills','fixture_money','hcq_choix'}
for b in pack.values():scores.update(re.findall(r'(?<!\S)(?:@s|#[\w]+) ([A-Za-z][A-Za-z0-9_]*)',b.decode(errors='replace')))
pack['data/hc_quetes/function/migration/items/check_self.mcfunction']=b'return 1\n'
# Retain real transitive core dependencies where available, stub only outside this focus.
for _ in range(20):
 additions={}
 for n,b in list(pack.items()):
  if not n.endswith('.mcfunction'):continue
  for ns,fn in re.findall(r'\bfunction ([a-z0-9_]+):([a-z0-9_/.-]+)',b.decode()):
   key='data/'+ns+'/function/'+fn+'.mcfunction'
   if key in pack:continue
   src=next((files[key] for files in orig.values() if key in files),None)
   additions[key]=src if src is not None else b'return 1\n'
 if not additions:break
 pack.update(additions)
for n,b in pack.items():
 if n.endswith('.mcfunction'):scores.update(re.findall(r'(?<!\S)(?:@s|#[\w]+) ([A-Za-z][A-Za-z0-9_]*)',b.decode(errors='replace')))
for n,b in list(pack.items()):
 if n.endswith('.mcfunction'):pack[n]=b.replace(b'capitale_currency:martin_dor',b'minecraft:gold_nugget')
pack['data/fixture/function/objectives.mcfunction']=('\n'.join('scoreboard objectives add '+s+' '+('trigger' if s in {'QuestChoix','hcq_choix'} else 'dummy') for s in sorted(scores))+'\n').encode()
for n,b in pack.items():p=P/n;p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(b)
(H/('reports/'+LABEL+'_fixture.json')).write_text(json.dumps({'functions':sum(n.endswith('.mcfunction') for n in pack),'scores':sorted(scores),'external_substitutes':['currency: gold_nugget','CapSkills: fixture counter','random port bonuses: fixture counter','Nexus selection: synthetic'],'fixture_only_security':'function root allowed for native EasyNPC command executor tests; no production security files included','integrated':INTEGRATED,'mod_production_sha256':build.sha((JOURNAL if INTEGRATED else H/'output'/build.FILES['mod']).read_bytes())},indent=2)+'\n')
if AUDIT:shutil.copyfile(H/'audit_prologue/pq01_protocol_fixture.json',S/'pq01_protocol_fixture.json')
GAME=RT/'minecraft_bundle/META-INF/versions/1.21.11/server-1.21.11.jar';CP=os.pathsep.join(map(str,[*sorted((RT/'libraries').glob('*.jar')),*sorted((RT/'minecraft_bundle/META-INF/libraries').rglob('*.jar')),GAME]))
result=S/'world-test-result.txt';result.unlink(missing_ok=True)
with (H/('reports/'+LABEL+'_server.log')).open('w') as f:r=subprocess.run([JAVA,'-XX:ActiveProcessorCount=2','-Xmx2G',*(['-Dpq02.repro.entry=true'] if REPRO else []),*(['-Dpq02.repro.screen=true'] if SCREEN_REPRO else []),*(['-Dpq02.prologue.audit=true'] if AUDIT else []),*(['-Dpq02.prologue.only=true'] if os.environ.get('PROLOGUE_AUDIT_ONLY')=='1' else []),*(['-Dpq02.screen.guard='+SCREEN_GUARD] if SCREEN_GUARD else []),f'-Dfabric.gameJarPath={GAME}','-Dfabric.log.disableAnsi=true','-cp',CP,'net.fabricmc.loader.impl.launch.knot.KnotServer','nogui'],cwd=S,stdout=f,stderr=subprocess.STDOUT,timeout=360)
if result.exists():shutil.copyfile(result,H/('reports/'+LABEL+'.txt'));print(result.read_text())
else:print((H/('reports/'+LABEL+'_server.log')).read_text()[-7000:])
assert r.returncode==0 and result.exists() and result.read_text().startswith('PASS')
