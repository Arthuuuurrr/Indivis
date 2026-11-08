"""Conservation des archives, du scénario et des contrats Nexus, sur les fichiers livrés."""
from pathlib import Path
import json,sys,hashlib,subprocess,os,re
import build,model
H=Path(__file__).resolve().parent;R=H/'reports';checks=[]
def ok(value,label):
 if not value:raise AssertionError(label)
 checks.append(label)
def digest(b):return hashlib.sha256(b).hexdigest()
manifest=json.loads((R/'build.json').read_text());old={k:build.read(p) for k,p in build.BASE.items()};new={k:build.read(H/'output'/n) for k,n in build.FILES.items()}
for k in build.FILES:
 ok(digest(build.BASE[k].read_bytes())==build.EXPECTED[k],k+': base exacte fournie')
 ok(digest((H/'output'/build.FILES[k]).read_bytes())==manifest['files'][k]['sha256'],k+': empreinte du build')
 ok(set(old[k])<=set(new[k]),k+': aucun fichier supprimé')
 ok(not any('..' in Path(n).parts or n.startswith('/') for n in new[k]),k+': chemins ZIP sûrs')
 for n,b in new[k].items():
  if n.endswith('.json') or n=='pack.mcmeta':json.loads(b)
 ok(True,k+': tous les JSON lisibles')
core_special={'pack.mcmeta','data/capitale/function/core/load.mcfunction','data/capitale/function/core/tick.mcfunction','data/capitale/function/profiles/before_save_self.mcfunction','data/capitale/function/profiles/after_load_self.mcfunction','data/capitale/function/quest/guidage/reset/aurele_self.mcfunction','data/capitale/function/quest/guidage/lost/second_self.mcfunction'}
allowed=('data/capitale/function/quest/sous_le_regard_du_coeur/','data/capitale/function/npc/aurele/','data/capitale/function/pq03/')
actual=[]
for k in old:
 changed=[n for n in old[k] if new[k][n]!=old[k][n]];added=sorted(set(new[k])-set(old[k]))
 ok(sorted(changed)==manifest['diffs'][k]['changed'] and added==manifest['diffs'][k]['added'],k+': liste complète des modifications')
 for n in changed+added:
  actual.append({'archive':k,'path':n,'operation':'modified' if n in old[k] else 'added','before_sha256':digest(old[k][n]) if n in old[k] else None,'after_sha256':digest(new[k][n]),'reason':manifest['reasons'].get(n,manifest['reasons'].get('quests/'+n,'Métadonnées de version ou extension PQ03 du JAR'))})
for n in set(old['core'])|set(new['core']):
 if n in core_special or n.startswith(allowed):continue
 ok(old['core'][n]==new['core'][n],'core conservé : '+n)
card='data/hc_quetes/hcq_quests/pq_03_sous_le_regard_du_coeur.json'
for n in old['quests']:
 if n not in {'pack.mcmeta',card}:ok(old['quests'][n]==new['quests'][n],'Fusion C1 conservée : '+n)
before=json.loads(old['quests'][card]);after=json.loads(new['quests'][card]);scrub=lambda d:{k:v for k,v in d.items() if k not in {'description','rewards','objectives'}}
ok(scrub(before)==scrub(after),'identifiant et conditions canoniques de PQ03 conservés')
for a,b in zip(before['objectives'],after['objectives']):
 ok({k:v for k,v in a.items() if k not in {'text','waypoint'}}=={k:v for k,v in b.items() if k not in {'text','waypoint'}},'état et identifiant de l’objectif '+a['id'])
ok(len(before['objectives'])==len(after['objectives']),'aucun objectif ajouté ou supprimé')
ok(before['rewards'][1:]==after['rewards'][1:],'175 XP, compétences et prime aléatoire conservés dans le journal')
mod_allowed={'fabric.mod.json','indivis_pq02_graph.json','net/hautecapitale/quetes/sequence/Pq02ProfileCheckpoint.class','net/hautecapitale/quetes/sequence/Pq02Runtime.class'}
for n in old['mod']:
 if n not in mod_allowed:ok(old['mod'][n]==new['mod'][n],'JAR existant conservé : '+n)
ok(set(manifest['diffs']['mod']['changed'])==mod_allowed,'quatre seules entrées héritées modifiées')
ok(all('Pq03' in n or 'QuestReputationNotice' in n or n.startswith('indivis_pq03') for n in manifest['diffs']['mod']['added']),'nouvelles entrées limitées à PQ03 et aux notifications de réputation')
ok(not any(re.search(r'(?:Bench|fixture|journal_test|client_doubles)',n,re.I) for n in new['mod']),'aucun substitut ou test livré dans le JAR')
g2=json.loads(new['mod']['indivis_pq02_graph.json']);g2old=json.loads(old['mod']['indivis_pq02_graph.json']);changes=0
g2.pop('editorial_pq03_followup')
for a,b in zip(g2old['nodes'],g2['nodes']):
 for x,y in zip(a['choices'],b['choices']):
  if x['text']!=y['text']:ok(x['text']=='Que vais-je trouver là-haut ?' and y['text']=='Que vais-je trouver au Cœur ?','seule correction éditoriale de l’ascenseur descendant');y['text']=x['text'];changes+=1
ok(g2==g2old and changes==8,'PQ02 : seuls huit libellés de question changent, graphe intact')
g3=json.loads(new['mod']['indivis_pq03_graph.json']);ok(g3==json.loads(json.dumps(model.export())),'graphe livré identique au scénario compilé')
ok(len(g3['nodes'])==57 and len({n['id'] for n in g3['nodes']})==57,'57 nœuds stables et identifiants uniques')
expected={(k,i) for k,items in model.PARTS.items() for i,v in enumerate(items) if (k,i)!=('C07',2)}
ok(model.USED==expected,'chaque réplique et choix du scénario couvert ; seul « validé » rédactionnel exclu')
ok(g3['script_sha256']==digest(model.SCRIPT.read_bytes()),'scénario exact identifié par SHA-256')
for n in g3['nodes']:
 ok(len(n['choices'])<=6,'menu borné : '+n['key'])
 for c in n['choices']:ok(c['target'] in model.N or c['target'].removeprefix('event:') in model.EVENTS,'cible existante : '+n['key']+' → '+c['target'])
ok(len(model.N['c03']['lines'])==4,'quatre répliques finales consécutives regroupées')
ok(model.N['c_bye']['next']=='event:complete','conclusion finale attachée à la réplique d’au revoir')
body=new['core']['data/capitale/function/pq03/complete_once_self.mcfunction'].decode()
for required in ['hcq_pq03 matches 90','CAP_PQ3_END matches 1','CAP_PQ3_DB matches 1..4','CAP_PQ3_REW matches 1..','reputation_ready','rewards/daily_bonus/roll/tech_self','reward/skills/core/sous_regard_coeur_self','migration/write/pq03/v100']:
 ok(required in body,'fin protégée : '+required)
ok('REP_GARDECOEUR' not in body and 'CAP_PQ3_IMPR' not in body,'aucun versement de réputation ni impression ajouté à la clôture')
for n,b in new['core'].items():
 if n.startswith('data/capitale/function/quest/sous_le_regard_du_coeur/') and n.endswith('.mcfunction') and '/waypoints/' not in n and '/debug/' not in n:
  ok(b'@a[' not in b and b'b3330000' not in b and b'run tp ' not in b,'ancien pilote mondial neutralisé : '+n)
tick0=old['core']['data/capitale/function/core/tick.mcfunction'];tick1=new['core']['data/capitale/function/core/tick.mcfunction'];removed=[l for l in tick0.splitlines(keepends=True) if b'quest/sous_le_regard_du_coeur/escort/tick' in l]
ok(len(removed)==2 and b''.join(l for l in tick0.splitlines(keepends=True) if l not in removed)==tick1,'tick core : deux seuls appels Aurèle retirés')
runtime=(H/'src/Pq03Runtime.java').read_text()
ok('UUID.randomUUID()' in runtime and 'STATE+"/"+v.character' in runtime and 'v.character.equals(character(v.player))' in runtime,'identité compte et personnage, stockage distinct')
ok('forceload' not in runtime and 'method_8393' in runtime,'aucun chargement forcé de chunk ajouté')
for n in new['mod']:
 if '/pq03/mixin/' in n and n.endswith('.class'):ok(b'Lorg/spongepowered/asm/mixin/Mixin;' in new['mod'][n],'annotation réelle @Mixin : '+n)
meta=json.loads(new['mod']['fabric.mod.json']);meta0=json.loads(old['mod']['fabric.mod.json'])
ok(meta['id']==meta0['id'] and meta['depends']==meta0['depends'],'même mod fusionné et mêmes dépendances')
ok(meta['entrypoints']['main'][:-1]==meta0['entrypoints']['main'] and meta['entrypoints']['client'][:-1]==meta0['entrypoints']['client'],'anciens points d’entrée préservés')
ok(meta['mixins'][:-1]==meta0['mixins'],'anciens mixins préservés')
for n,s in manifest['source_sha256'].items():ok(digest((H/n).read_bytes())==s,'source compilée inchangée : '+n)
r1=H.parent/'pq03_fix_20261108/baseline_r1/output'
for kind,name in build.FILES.items():
 original=name.replace('_HF1.zip','.zip').replace('-HF1-1.21.11.jar','-1.21.11.jar');base=build.read(r1/original);updated=new[kind]
 if kind=='quests':ok(base==updated,'journal Fusion C1 R1 intégralement inchangé par HF1')
 if kind=='core':
  differences={n for n in set(base)|set(updated) if base.get(n)!=updated.get(n)}
  ok(differences=={'data/capitale/function/pq03/load.mcfunction','data/capitale/function/pq03/complete_once_self.mcfunction','data/capitale/function/pq03/reputation/instant_ready_self.mcfunction'},'HF1 core : trois seuls chemins changent face à R1')
 if kind=='mod':ok(json.loads(base['indivis_pq03_graph.json'])['nodes']==json.loads(updated['indivis_pq03_graph.json'])['nodes'],'les 57 blocs narratifs PQ03 R1 restent identiques')
base2=(H.parent/'pq02/src/Pq02Runtime.java').read_text();now2=(H/'src/Pq02Runtime.java').read_text().replace('int before=score(p,"REP_GARDEPORT");','').replace('QuestReputationNotice.changed(p,"port",before,score(p,"REP_GARDEPORT"));','')
ok(base2==now2,'PQ02 : seule notification ajoutée à son effet déjà immédiat, aucun calcul ou autre comportement changé')
java=next((build.RT/'jdk').glob('*/bin/java'));classes=H/'rules_classes';classes.mkdir(exist_ok=True)
nexus=next((H.parent/'pq03_fix_20261108/downloads').glob('NexusCharacters-Indivis-1.21.11-PRE35-SI1-OPT3*.jar'))
ok(digest(nexus.read_bytes())=='de06ecab0f5cf7ccee29043bff2ccb5fd2b7425c173da145c1f27559b49def7b','vraie politique Nexus OPT3 fournie')
libs=[H/'output'/build.FILES['mod'],nexus,build.RT/'bootstrap/.fabric/remappedJars/minecraft-1.21.11-0.19.5/server-intermediary.jar',*sorted((build.RT/'libraries').glob('*.jar')),*sorted((build.RT/'minecraft_bundle/META-INF/libraries').rglob('*.jar'))];cp=os.pathsep.join(map(str,libs))
subprocess.run([java.with_name('javac'),'--release','21','-proc:none','-cp',cp,'-d',classes,H/'tests/Pq03RulesBench.java'],check=True)
r=subprocess.run([java,'-cp',str(classes)+os.pathsep+cp,'net.hautecapitale.quetes.sequence.Pq03RulesBench'],capture_output=True,text=True)
(R/'rules.txt').write_text(r.stdout+r.stderr);ok(r.returncode==0 and r.stdout.startswith('PASS'),'contrats Java exécutés : '+r.stdout.strip())
report={'status':'PASS','checks':len(checks),'byte_preservation':{k:manifest['diffs'][k]['preserved'] for k in new},'files':manifest['files'],'rules':r.stdout.strip(),'static_only':True,'evidence':checks}
(R/'static.json').write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n');(R/'files_changed.json').write_text(json.dumps(actual,ensure_ascii=False,indent=2)+'\n')
print(json.dumps({k:v for k,v in report.items() if k!='evidence'},ensure_ascii=False,indent=2))
