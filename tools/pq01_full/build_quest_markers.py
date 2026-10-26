"""Reproduit HF11 puis ajoute QMARK1 uniquement aux deux datapacks."""
import json, hashlib
import build
from quest_markers import apply

FILES={'core':'capitale_core_1.5.7_V2_SEQUENCE_I_SI1_PQ01_R3_HF9_QMARK1.zip',
       'quests':'hc_quetes_b22_V2_SEQUENCE_I_SI1b_PQ01_R3_HF4_QMARK1.zip'}

def main():
 packs,_=build.build()
 jar=build.OUT/build.FILES['journal']
 assert build.sha(jar.read_bytes())=='1f63a074ae4871d02daa0c70729032b3985f9b5029548a9539ae5627625a1f83', 'Le journal HF11 doit rester identique.'
 before={k:packs[k].copy() for k in ['core','quests']}
 catalog=apply(packs)
 audit={}
 for kind,filename in FILES.items():
  build.save(packs[kind],build.OUT/filename)
  audit[kind]={'filename':filename,'sha256':build.sha((build.OUT/filename).read_bytes()),'bytes':(build.OUT/filename).stat().st_size,
   'changed':[n for n in before[kind] if before[kind][n]!=packs[kind][n]],'added':sorted(set(packs[kind])-set(before[kind])),
   'removed':sorted(set(before[kind])-set(packs[kind]))}
  assert not audit[kind]['removed']
 assert audit['core']['changed']==['pack.mcmeta']
 assert sorted(audit['quests']['changed'])==['data/hc_quetes/function/runtime/second.mcfunction','pack.mcmeta']
 out=build.OUT/'qmark1_audit';out.mkdir(exist_ok=True)
 (out/'diffs.json').write_text(json.dumps(audit,ensure_ascii=False,indent=2)+'\n')
 (out/'catalogue.json').write_text(json.dumps(catalog,ensure_ascii=False,indent=2)+'\n')
 print(json.dumps({'quests':len(catalog['quests']),'givers':len(catalog['givers']),'journal_unchanged':True,'files':audit},ensure_ascii=False,indent=2))

if __name__=='__main__':main()
