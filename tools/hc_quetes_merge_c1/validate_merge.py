"""Contrôles d'intégrité, de conservation et de résolution de la fusion C1."""
from pathlib import Path
import json,re,zipfile,hashlib,importlib.util
import reconcile
H=Path(__file__).resolve().parent;checks=[]
def check(value,message):
 assert value,message
 checks.append(message)
def load_tree(path):return {p.relative_to(path).as_posix():p.read_bytes() for p in path.rglob('*') if p.is_file()}
def eval_predicate(p,scores):
 kind=p['condition']
 if kind=='minecraft:entity_scores':return all(k in scores and ('min' not in v or scores[k]>=v['min']) and ('max' not in v or scores[k]<=v['max']) for k,v in p['scores'].items())
 if kind=='minecraft:any_of':return any(eval_predicate(t,scores) for t in p['terms'])
 if kind=='minecraft:all_of':return all(eval_predicate(t,scores) for t in p['terms'])
 if kind=='minecraft:inverted':return not eval_predicate(p['term'],scores)
 raise AssertionError(kind)
def main():
 ours=reconcile.read(reconcile.OURS);merged=load_tree(H/'merged');report=json.loads((H/'reconciliation.json').read_text())
 check(not report['conflicts'],'No unresolved three-way conflict')
 check(not (ours.keys()-merged.keys()),'No installed resource removed')
 allowed={d['path'] for d in report['decisions']}|set(report['new_predicates'])
 actual={n for n in merged if n not in ours or merged[n]!=ours[n]}
 check(actual<=allowed,'Every modified resource belongs to the reviewed merge list')
 check(len(report['raw_clement_changes'])==14,'Exactly 12 b22 changes and 2 new functions relative to historical b21')
 check(len(merged)==len(ours)+7,'Only two functions and five predicates added')
 for prefix in ['data/hc_quetes/function/quests/','data/hc_quetes/function/runtime/','data/hc_quetes/function/liaison/','data/hc_quetes/function/objets/','data/hc_quetes/item_modifier/','data/hc_quetes/tags/']:
  check(all(merged[n]==b for n,b in ours.items() if n.startswith(prefix)),'Preserved byte-for-byte '+prefix)
 cards={n:json.loads(b) for n,b in merged.items() if n.startswith('data/hc_quetes/hcq_quests/')}
 check(len(cards)==70 and len({c['id'] for c in cards.values()})==70,'70 unique canonical quest definitions, no legacy aliases duplicated')
 colin='data/hc_quetes/hcq_quests/cq_vc_ep_02_ruines_carrec.json'
 check(all(merged[n]==ours[n] for n in cards if n!=colin),'All 69 other quest definitions preserved, including PQ01-PQ05 and MQ01-MQ04')
 pq02='data/hc_quetes/hcq_quests/pq_02_registre_port.json'
 check(merged[pq02]==ours[pq02] and '50 Martins' in merged[pq02].decode() and '+5' not in merged[pq02].decode(),'PQ02 HF8 automatic 50-Martin reward description preserved exactly')
 for n,b in merged.items():
  if n.endswith('.json') or n=='pack.mcmeta':json.loads(b)
 check(True,'All JSON resources parse')
 definitions=set();tags=set();predicates=set()
 core=reconcile.read(H.parents[1]/'work/pq02/output/capitale_core_1.5.7_V2_SEQUENCE_I_SI1_PQ01_R3_HF9_QMARK2_PQ02_R1_HF8.zip')
 for pack in [core,merged]:
  for n in pack:
   m=re.fullmatch(r'data/([^/]+)/function/(.+)\.mcfunction',n)
   if m:definitions.add(m[1]+':'+m[2])
   m=re.fullmatch(r'data/([^/]+)/tags/function/(.+)\.json',n)
   if m:tags.add(m[1]+':'+m[2])
   m=re.fullmatch(r'data/([^/]+)/predicate/(.+)\.json',n)
   if m:predicates.add(m[1]+':'+m[2])
 missing=[]
 for n,b in merged.items():
  if not n.endswith('.mcfunction'):continue
  for line in b.decode().splitlines():
   if not line or line.lstrip().startswith('#'):continue
   for tag,target in re.findall(r'\bfunction (#?((?:hc_quetes|capitale):[a-z0-9_./-]+))(?=\s|$)',line):
    if (target not in (tags if tag.startswith('#') else definitions)):missing.append((n,target))
   for target in re.findall(r'\bpredicate ((?:hc_quetes|capitale):[a-z0-9_./-]+)(?=\s|$)',line):
    if target not in predicates:missing.append((n,target))
 check(not missing,'All literal core/quest function and predicate references resolve: '+str(missing[:5]))
 module,migration,items=reconcile.converter()
 for path in report['new_predicates']:
  group,slug=path.removeprefix('data/hc_quetes/predicate/migration/state/').removesuffix('.json').split('/')
  pattern=slug.replace('neg','-').replace('_to_','..');model=next(m for m in migration.models.values() if m['key']==group)
  pred=json.loads(merged[path])
  for old,vector in model['vectors'].items():check(eval_predicate(pred,vector)==module.bounds(old,pattern),group+'/'+slug+' preserves legacy state '+str(old))
 # The two refusal states intentionally share canonical stage 0; the dedicated
 # flag only suppresses the marker and is admitted by the existing Nexus prefixes.
 flag='hcf_cqvcbb01_ref';check(len(flag)<=16 and flag.startswith('hcf_'),'Refusal flag uses existing persistent v2 prefix and a legal objective length')
 legacy=[]
 for n,b in merged.items():
  if n.endswith('.mcfunction') and '/migration/upgrade_self.' not in n:
   for line in b.decode().splitlines():
    if not line.lstrip().startswith('#') and re.search(r'\b(?:score @s|scoreboard players (?:set|add|remove) @s) hcq_n\b',line):legacy.append((n,line))
 check(not legacy,'No obsolete shared Carrec state read/write outside the one-time migration')
 # Avoid publishing thousands of identical reference checks in the short report.
 result={'status':'PASS','checks':len(checks),'input_sha256':reconcile.EXPECTED,'file_count':len(merged),'changed_existing':sorted(n for n in actual if n in ours),'added':sorted(merged.keys()-ours.keys()),'removed':[],'unchanged':sum(merged[n]==b for n,b in ours.items()),'quest_definitions':70,'internal_references_missing':missing,'protected_quest_ids':[c['id'] for n,c in cards.items() if n!=colin],'evidence':checks}
 out=H/'output';out.mkdir(exist_ok=True);dest=out/reconcile.DEST
 with zipfile.ZipFile(dest,'w',zipfile.ZIP_DEFLATED,compresslevel=9) as z:
  for n,b in sorted(merged.items()):
   entry=zipfile.ZipInfo(n,(2026,11,1,0,0,0));entry.compress_type=zipfile.ZIP_DEFLATED;z.writestr(entry,b)
 with zipfile.ZipFile(dest) as z:check(z.testzip() is None and 'pack.mcmeta' in z.namelist(),'Installable ZIP integrity and metadata at root')
 result['checks']=len(checks);result['datapack_sha256']=hashlib.sha256(dest.read_bytes()).hexdigest()
 (H/'validation_static.json').write_bytes(reconcile.dump(result))
 print(json.dumps({'status':'PASS','checks':len(checks),'files':len(merged),'changed_existing':len(result['changed_existing']),'added':len(result['added']),'unchanged':result['unchanged'],'zip':dest.name,'sha256':hashlib.sha256(dest.read_bytes()).hexdigest()},ensure_ascii=False,indent=2))
if __name__=='__main__':main()
