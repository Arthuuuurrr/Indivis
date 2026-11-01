"""Fusion de hc_quetes b22 avec les états v2 et les personnalisations d'Arthur.

Une base historique b21 commune distingue les corrections b22 des différences
de branche (lore v16.1, migration v2, SI1, PQ01, marqueurs et PQ02 HF8).
Le convertisseur et ses tables historiques sont réutilisés avec leurs modèles
gelés ; aucun objectif de personnage n'est renuméroté à partir des nouveaux textes.
"""
from pathlib import Path
import hashlib,importlib.util,json,re,subprocess,zipfile,difflib,copy
H=Path(__file__).resolve().parent;ROOT=H.parents[1]
OURS=ROOT/'upload/hc_quetes_b22_V2_SEQUENCE_I_SI1b_PQ01_R3_HF4_QMARK2_PQ02_R1_HF8.zip'
CLEMENT=ROOT/'upload/hc_quetes-0.1.0.b22.zip'
COMMON=next((H/'reference').rglob('hc_quetes-0.1.0.b21.zip'))
EXPECTED={'ours':'393ac02d857774f4de7483ce4d179f2e67a13f1ec1ae9cab4830608568b891a6','clement':'07d0c6e3b711fa9ccf1808e568ce72cc4cb0b577132013f40513aa6b066df6a3','common':'4e14c6584a175c864bccf3dbf36569c601cab013078dbc9ce0335f47a8a17360'}
DEST='hc_quetes_b22_V2_SEQUENCE_I_SI1b_PQ01_R3_HF4_QMARK2_PQ02_R1_HF8_FUSION_C1.zip'
def sha(b):return hashlib.sha256(b).hexdigest()
def read(p):
 with zipfile.ZipFile(p) as z:
  assert z.testzip() is None
  names=[n for n in z.namelist() if not n.endswith('/')]
  assert len(names)==len(set(names)) and all(not n.startswith('/') and '..' not in Path(n).parts for n in names)
  return {n:z.read(n) for n in names}
def equal(path,a,b):
 if a is None or b is None:return a is b
 return json.loads(a)==json.loads(b) if path.endswith('.json') or path=='pack.mcmeta' else a==b
def dump(obj):return (json.dumps(obj,ensure_ascii=False,indent=2)+'\n').encode()

def converter():
 parent=H/'canonical_reference'
 spec=importlib.util.spec_from_file_location('historical_migrate',parent/'migrate.py')
 module=importlib.util.module_from_spec(spec);spec.loader.exec_module(module)
 frozen=json.loads((parent/'mapping.json').read_text())
 obj=module.Migration.__new__(module.Migration)
 obj.models=copy.deepcopy(frozen['models'])
 for model in obj.models.values():model['vectors']={int(k):v for k,v in model['vectors'].items()}
 obj.aux=frozen['auxiliary_mapping'];obj.predicates={};obj.generated={}
 obj.legacy_pattern=re.compile(r'(?<![\w])(?:'+'|'.join(map(re.escape,[*obj.models,*obj.aux,*module.IDS]))+r')(?![\w])')
 return module,obj,frozen['quest_item_mapping']

def normalize(pack,module,migration,items):
 def command(s):
  # b22 introduit -1 pour refuser l'offre de la Bête blanche. En v2, garder
  # l'étape canonique à 0 et mémoriser ce refus dans un drapeau persistant.
  s=s.replace('scoreboard players set @s hcq_n -1','scoreboard players set @s hcf_cqvcbb01_ref 1')
  s=migration.text(s)
  for old,new in items.items():
   s=re.sub(r'(hc_quete\s*:\s*["\'])'+re.escape(old)+r'(["\'])',lambda m:m[1]+new+m[2],s)
   s=re.sub(r'(function hc_quetes:(?:lib/(?:retire_objet|a_objet)|veille/porte)\s+\{id:["\'])'+re.escape(old)+r'(["\'])',lambda m:m[1]+new+m[2],s)
  return s
 def walk(value,key=''):
  if isinstance(value,dict):return {k:walk(v,k) for k,v in value.items()}
  if isinstance(value,list):return [walk(v,key) for v in value]
  if isinstance(value,str):return items.get(value,value) if key in {'done_item','hc_quete'} else command(value)
  return value
 result={};renames={}
 for path,data in pack.items():
  target=path
  if path.startswith('data/hc_quetes/hcq_quests/') and path.endswith('.json'):
   card=json.loads(data);row=module.BY_OLD[card['id']]
   data=dump(walk(migration.migrate_card(card,row)))
   target='data/hc_quetes/hcq_quests/'+row['new_id']+'.json'
  elif path.endswith('.json'):data=dump(walk(json.loads(migration.text(data.decode()))))
  elif path.endswith('.mcfunction'):data=command(data.decode()).encode()
  result[target]=data;renames[path]=target
 return result,renames

def main():
 inputs={'ours':OURS,'clement':CLEMENT,'common':COMMON}
 for key,p in inputs.items():assert sha(p.read_bytes())==EXPECTED[key],key
 packs={key:read(p) for key,p in inputs.items()};ours=packs['ours'];module,migration,items=converter()
 base,renames=normalize(packs['common'],module,migration,items)
 theirs,their_renames=normalize(packs['clement'],module,migration,items)
 assert all(renames[k]==their_renames[k] for k in renames)
 # Semantic JSON comparison suppresses formatting-only changes in the raw ZIP.
 raw_changed=sorted(n for n in packs['common'].keys()|packs['clement'].keys() if not equal(n,packs['common'].get(n),packs['clement'].get(n)))
 assert len(raw_changed)==14
 changed_targets={their_renames.get(n,renames.get(n,n)) for n in raw_changed}
 merged=ours.copy();decisions=[];conflicts=[]
 for path in sorted(changed_targets):
  b,o,t=base.get(path),ours.get(path),theirs.get(path)
  if path=='pack.mcmeta':
   meta=json.loads(o);meta['pack']['description']+=' — FUSION-C1 : correctifs b22 Clément (insigne Vasca, paiement, souvenirs et cage)'
   merged[path]=dump(meta);decisions.append({'path':path,'method':'combined_metadata'});continue
  if equal(path,o,t):decisions.append({'path':path,'method':'already_present'});continue
  if equal(path,b,o):
   if t is None:merged.pop(path,None)
   else:merged[path]=t
   decisions.append({'path':path,'method':'clement_only'});continue
  if path.endswith('.mcfunction') and b is not None and o is not None and t is not None:
   temp=H/'merge_work';temp.mkdir(exist_ok=True)
   for name,content in [('ours',o),('base',b),('theirs',t)]:(temp/name).write_bytes(content)
   proc=subprocess.run(['git','merge-file','--diff3','-p',str(temp/'ours'),str(temp/'base'),str(temp/'theirs')],capture_output=True)
   if proc.returncode==0:merged[path]=proc.stdout;decisions.append({'path':path,'method':'three_way_lines'});continue
   conflict_path=H/'conflicts'/path;conflict_path.parent.mkdir(parents=True,exist_ok=True);conflict_path.write_bytes(proc.stdout)
   conflicts.append({'path':path,'type':'line_conflict','output':str(conflict_path)})
  else:conflicts.append({'path':path,'type':'structured_conflict'})
 # Adaptation v2 du refus : le dialogue reste disponible au clic, seul le !
 # automatique disparaît. Ni les étapes des quatre quêtes ni leurs branches
 # existantes ne sont réécrites pour introduire un état négatif partagé.
 flag='hcf_cqvcbb01_ref';assert len(flag)<=16
 adjustments={
  'data/hc_quetes/function/migration/load.mcfunction':('append','scoreboard objectives add '+flag+' dummy\n'),
  'data/hc_quetes/function/migration/ensure/arc_n.mcfunction':('append','scoreboard players add @s '+flag+' 0\n'),
  'data/hc_quetes/function/migration/write/arc_n/v0.mcfunction':('prepend','scoreboard players set @s '+flag+' 0\n'),
  'data/hc_quetes/function/migration/write/arc_n/v1.mcfunction':('prepend','scoreboard players set @s '+flag+' 0\n'),
 }
 for path,(mode,text) in adjustments.items():
  merged[path]=(text.encode()+ours[path]) if mode=='prepend' else ours[path]+text.encode()
  decisions.append({'path':path,'method':'v2_refusal_flag_lifecycle'})
 marker='data/hc_quetes/function/marqueurs/aurele.mcfunction';text=ours[marker].decode()
 target='if predicate hc_quetes:migration/state/arc_n/0 run return run scoreboard players set @s hcq_mrk_aurele 1'
 assert text.count(target)==1
 merged[marker]=text.replace(target,'if predicate hc_quetes:migration/state/arc_n/0 unless score @s '+flag+' matches 1 run return run scoreboard players set @s hcq_mrk_aurele 1').encode()
 decisions.append({'path':marker,'method':'suppress_only_declined_offer_marker'})
 # New generated predicates must never alter the meaning of installed predicates.
 generated=[]
 for path,data in migration.generated.items():
  if path in ours:
   if not equal(path,ours[path],data):conflicts.append({'path':path,'type':'generated_predicate_difference'})
  elif any(path.removeprefix('data/hc_quetes/predicate/').removesuffix('.json') in merged[n].decode() for n in changed_targets if n.endswith('.mcfunction') and n in merged):
   merged[path]=data;generated.append(path)
 report={'inputs':{k:{'filename':p.name,'sha256':EXPECTED[k]} for k,p in inputs.items()},'raw_clement_changes':raw_changed,'decisions':decisions,'new_predicates':generated,'conflicts':conflicts,'mapping':{'states':len(migration.models),'auxiliary':len(migration.aux),'items':len(items)},'normalized_changed':sorted(changed_targets),'changed_from_ours':sorted(n for n in merged if n not in ours or merged[n]!=ours[n]),'removed_from_ours':sorted(ours.keys()-merged.keys())}
 (H/'reconciliation.json').write_bytes(dump(report))
 for key,pack in [('normalized_base',base),('normalized_clement',theirs)]:
  for path in changed_targets:
   if path in pack:
    target=H/key/path;target.parent.mkdir(parents=True,exist_ok=True);target.write_bytes(pack[path])
 if conflicts:
  print(json.dumps({'status':'CONFLICTS_TO_REVIEW','conflicts':conflicts,'decisions':decisions},ensure_ascii=False,indent=2));return
 output=H/'merged';output.mkdir(exist_ok=True)
 for n,b in merged.items():p=output/n;p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(b)
 print(json.dumps({'status':'MERGED_PENDING_VALIDATION','files':len(merged),'decisions':decisions,'new_predicates':generated},ensure_ascii=False,indent=2))
if __name__=='__main__':main()
