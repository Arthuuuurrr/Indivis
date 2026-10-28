from pathlib import Path
import zipfile,json,hashlib,re,subprocess,os
import model,build
H=Path(__file__).resolve().parent
checks=[]
def ok(b,s):
 assert b,s
 checks.append(s)
old={k:build.read(p) for k,p in build.BASE.items()};new={k:build.read(build.OUT/build.FILES[k]) for k in old}
diffs=json.loads((H/'reports/diffs.json').read_text())
baseline=json.loads((H/'hf1_baseline.json').read_text())
added={'data/capitale/function/pq02/admin/'+s+'.mcfunction' for s in baseline['added_admin_functions']}
preserved={n:b for n,b in new['core'].items() if n not in added and n not in baseline['changed_r1_entries']}
aggregate=''.join(n+' '+build.sha(b)+'\n' for n,b in sorted(preserved.items())).encode()
ok(len(preserved)==baseline['core_preserved_count'] and build.sha(aggregate)==baseline['core_preserved_manifest_sha256'],'HF1: all other R1 core entries byte-identical (aggregate original hashes)')
ok(all(n in new['core'] for n in added) and all(build.sha(new['core'][n])!=digest for n,digest in baseline['changed_r1_entries'].items()),'HF1: four new admin functions and only two changed existing entries')
ok(build.sha((build.OUT/build.FILES['quests']).read_bytes())==baseline['quests_sha256'],'HF1: hc_quetes archive byte-identical to R1')
for name,digest in baseline['preserved_sources'].items():ok(build.sha((H/name).read_bytes())==digest,'HF1: gameplay source preserved '+name)
for k in old:ok(not diffs[k]['removed'],k+': no existing file deleted')
for key in ['hugues_menu','hugues_36','hugues_38','hugues_40','hugues_42','hugues_44']:
 n='data/capitale/function/pq01/menu/'+key+'.mcfunction'
 expected=b''.join(l for l in old['core'][n].splitlines(keepends=True) if 'Écouter leur échange sur les procédures'.encode() not in l)
 ok(new['core'][n]==expected,key+': only option6 display removed')
 n=n.replace('/menu/','/choice/')
 expected=old['core'][n].replace(b'execute if score @s hcm_pq1_pick matches 6 run return run function capitale:pq01/banter_self',b'execute if score @s hcm_pq1_pick matches 6 run return 0')
 ok(new['core'][n]==expected,key+': only option6 dispatch neutralized')
pq=[n for n in diffs['core']['changed'] if '/pq01/' in n]
ok(len(pq)==12,'exactly 12 PQ01 files changed, all Hugues option6')
for k in old:
 for n,b in old[k].items():
  if re.search(r'/quests/mq0[1234]/|/runtime/interact/|/pq01/open/altheon_self|/npc/altheon_brumeforge/',n):ok(b==new[k][n],'preserved '+k+'/'+n)
q='data/capitale/function/quest/spawn_dirigeable_couronne/capitaine_quai/interact_self.mcfunction'
ok(new['core'][q].endswith(old['core'][q]),'quay captain only MQ03 priority prefix, all original body preserved')
ok(b'hcq_mq03 matches 10..30' in new['core'][q],'MQ03 priority matches original large captain')
for n,b in new['core'].items():
 if '/pq02/' in n or '/quest/le_registre_du_port/' in n:
  ok(b'scoreboard players add @s REP_GARDEPORT 5' not in b,'no PQ02 fixed+5 '+n)
for k in new:
 for n,b in new[k].items():
  if n.endswith('.json') or n=='pack.mcmeta':json.loads(b)
ok(True,'all JSONs and metadata parse')
ok(max(len(s) for s in build.PERSIST)<=16,'all persistent score names <=16')
olddef=json.loads(old['quests']['data/hc_quetes/hcq_quests/pq_02_registre_port.json']);newdef=json.loads(new['quests']['data/hc_quetes/hcq_quests/pq_02_registre_port.json'])
ok(olddef['id']==newdef['id'] and olddef['source']==newdef['source'],'journal ID and stages preserved')
ok([o['id'] for o in olddef['objectives']]==[o['id'] for o in newdef['objectives']],'objective IDs preserved')
edges=[]
for key,n in model.N.items():
 for target in [c['target'] for c in n['choices']]+([n['next']] if n['next'] else []):edges.append((key,target))
roots=['a01','t_menu','t01','t_sentinel_observed','t07','m06_registered','m07','f_menu','f_questions','f07','post_menu','post_very_friendly','post_friendly','post_neutral','post_unfriendly']
edges.append(('f05','f05_short')) # Reprise conditionnelle : statut déjà expliqué au magistrat.
visited=set(roots)
while True:
 more={b for a,b in edges if a in visited and not b.startswith('event:')}-visited
 if not more:break
 visited|=more
ok(set(model.N)<=visited,'all narrative nodes reachable from phase entrances')
# Each used quote is read directly from authoritative text; explicit audit of unused spoken lines.
quotes=[(i+1,j,t) for i,l in enumerate(model.LINES) for j,t in enumerate(re.findall('«([^»]+)»',l))]
unused=[{'line':i,'quote':t} for i,j,t in quotes if (i,j) not in model.USED]
(H/'reports/unused_quotes.json').write_text(json.dumps(unused,ensure_ascii=False,indent=2)+'\n')
coverage=[]
for key,n in model.N.items():coverage.append({'editorial_id':n['editorial'],'runtime':key,'source_lines':n['source_lines'],'camera':n['camera'],'condition':n['condition'],'next':n['next'],'choices':n['choices']})
(H/'reports/coverage.json').write_text(json.dumps(coverage,ensure_ascii=False,indent=2)+'\n')
rt=Path('/tmp/indivis_pq02_runtime');java=next((rt/'jdk').glob('*/bin/java'));classes=H/'test_classes';classes.mkdir(exist_ok=True)
nexus=H/'dependencies/recovered_nexus/NexusCharacters-Indivis-1.21.11-PRE35-SI1-OPT1(1).jar'
if not nexus.exists():nexus=H.parents[1]/'upload/NexusCharacters-Indivis-1.21.11-PRE35-SI1-OPT1(1).jar'
assert build.sha(nexus.read_bytes())=='bdb1525d5e4448cb8127e38bdcdce063d629b7039a856bf9ea74be5ea06dea11','Incomplete or unexpected Nexus PRE35 test input'
libs=[*sorted((rt/'libraries').glob('*.jar')),*sorted((rt/'minecraft_bundle/META-INF/libraries').rglob('*.jar')),rt/'bootstrap/.fabric/remappedJars/minecraft-1.21.11-0.19.5/server-intermediary.jar',H/'output'/build.FILES['mod'],nexus,*H.parents[1].joinpath('upload').glob('haute-capitale-*.jar')]
cp=os.pathsep.join(map(str,libs));subprocess.run([java.with_name('javac'),'--release','21','-proc:none','-cp',cp,'-d',classes,H/'tests/RulesBench.java'],check=True)
r=subprocess.run([java,'-cp',str(classes)+os.pathsep+cp,'net.hautecapitale.quetes.sequence.RulesBench'],capture_output=True,text=True,check=True)
(H/'reports/rules.txt').write_text(r.stdout);ok(r.stdout.startswith('PASS'),'real Java21 pure tests and actual Nexus policy pass')
result={'status':'PASS','checks':len(checks),'evidence':checks,'nodes':len(model.N),'edges':len(edges),'quotes':len(model.USED),'additional_navigation_labels':model.NAVIGATION_ADDITIONS,'unused_quotes_review':unused,'rules':r.stdout.strip()}
(H/'reports/static.json').write_text(json.dumps(result,ensure_ascii=False,indent=2)+'\n')
print(json.dumps({k:v for k,v in result.items() if k not in ['evidence','unused_quotes_review']},ensure_ascii=False,indent=2))
