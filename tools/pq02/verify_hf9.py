"""Conservation octet par octet et portée exacte du patch HF9."""
from pathlib import Path
import json,hashlib,zipfile,subprocess,os
import build,build_hf9,model
H=Path(__file__).resolve().parent;checks=[]
def ok(value,label):
 assert value,label
 checks.append(label)
old=build.read(build_hf9.BASE);new=build.read(H/'output'/build_hf9.DEST)
ok(set(old)<=set(new),'no installed JAR entry removed')
manifest=json.loads((H/'reports/hf9_build.json').read_text())
ok(manifest['sha256']==build_hf9.sha(H/'output'/build_hf9.DEST),'candidate JAR matches compiled hash')
for n in old:
 if n not in manifest['changed']:ok(new[n]==old[n],'preserved '+n)
ok(all('Pq02' in n or n in {'fabric.mod.json','indivis_pq02_graph.json'} for n in manifest['changed']+manifest['added']),'only PQ02 controller, camera, graph and release metadata changed')
ok(not any('Bench' in n or n.startswith(('net/minecraft/','net/tompsen/')) for n in new),'no fixture, dependency class or test shipped')
a=json.loads(old['fabric.mod.json']);b=json.loads(new['fabric.mod.json'])
for key in set(a)-{'version','description'}:ok(a[key]==b[key],'original mod metadata preserved '+key)
before=json.loads(old['indivis_pq02_graph.json']);after=json.loads(new['indivis_pq02_graph.json'])
previous={n['key']:n for n in before['nodes']};current={n['key']:n for n in after['nodes']}
ok({k:n['id'] for k,n in previous.items()}=={k:n['id'] for k,n in current.items()},'111 stable persisted narrative IDs and both legacy aliases preserved')
for key,node in previous.items():
 updated=current[key]
 if key in {'t01','t04'}:
  left=node.copy();right=updated.copy();left.pop('choices');right.pop('choices');ok(left==right,'pause uses exact original lore quote '+key)
  ok(updated['choices'][:len(node['choices'])]==node['choices'],'existing dialogue answers unchanged '+key)
  ok(any(c['target']=='t_resume' for c in updated['choices']),'route resume directly offered '+key)
 else:ok(updated==node,'narrative node unchanged '+key)
for key in set(before)-{'nodes','source_quotes_used'}:ok(before[key]==after[key],'narrative source metadata preserved '+key)
for source in ['kit/Indivis_PQ02_Script_Consolide_v1_0.md','kit/Indivis_PQ02_Script_Consolide_v1_1_Don_Systematique.md']:
 ok(build.sha((H/source).read_bytes())==(before['parent_script_sha256'] if 'v1_0' in source else before['script_sha256']),'authoritative script not rewritten '+source)
for name,digest in manifest['datapacks_unchanged'].items():ok(build_hf9.sha(H/'output'/name)==digest,'entire installed datapack preserved '+name)
baseline=(H/'baseline_hf8_before_pacing_20261101/src/Pq02Runtime.java').read_text();runtime=(H/'src/Pq02Runtime.java').read_text()
for start,end in [('static void giftAtScene','static Object focus'),('static boolean effect','static void stopActor')]:
 ok(baseline[baseline.index(start):baseline.index(end)]==runtime[runtime.index(start):runtime.index(end)],'gift, decisions and terminal events retain their exact HF8 implementation '+start)
rt=H/'runtime_hf9';java=next((rt/'jdk').glob('*/bin/java'));classes=H/'rules_hf9_classes';classes.mkdir(exist_ok=True)
nexus=H/'dependencies/recovered_nexus/NexusCharacters-Indivis-1.21.11-PRE35-SI1-OPT1(1).jar'
ok(build_hf9.sha(nexus)=='bdb1525d5e4448cb8127e38bdcdce063d629b7039a856bf9ea74be5ea06dea11','actual Nexus persistence policy input pinned')
cp=os.pathsep.join(map(str,[H/'output'/build_hf9.DEST,nexus,rt/'bootstrap/.fabric/remappedJars/minecraft-1.21.11-0.19.5/server-intermediary.jar',*sorted((rt/'libraries').glob('*.jar')),*sorted((rt/'minecraft_bundle/META-INF/libraries').rglob('*.jar'))]))
subprocess.run([java.with_name('javac'),'--release','21','-proc:none','-cp',cp,'-d',classes,H/'tests/RulesBench.java'],check=True)
r=subprocess.run([java,'-cp',str(classes)+os.pathsep+cp,'net.hautecapitale.quetes.sequence.RulesBench'],capture_output=True,text=True,check=True)
(H/'reports/hf9_rules.txt').write_text(r.stdout+r.stderr);ok(r.stdout.startswith('PASS'),'Java21 decision, persistence and motion rules pass')
report={'status':'PASS','checks':len(checks),'evidence':checks,'jar_sha256':manifest['sha256'],'static_only':True,'rules':r.stdout.strip(),'narrative_nodes':len(current),'narrative_edges':sum(len(n['choices'])+bool(n['next']) for n in current.values()),'datapacks':manifest['datapacks_unchanged']}
(H/'reports/hf9_static.json').write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n');print(json.dumps({k:v for k,v in report.items() if k!='evidence'},ensure_ascii=False,indent=2))
