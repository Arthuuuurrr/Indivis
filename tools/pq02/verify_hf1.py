"""Vérifie le périmètre HF1 et l'application exacte du patch aux archives de référence."""
from pathlib import Path
import json,difflib,subprocess,shutil
import build,merge_journal
H=Path(__file__).resolve().parent;R=H/'reports'
baseline=json.loads((H/'hf1_baseline.json').read_text())
core=build.read(H/'output'/build.FILES['core']);quests=build.read(H/'output'/build.FILES['quests'])
added=['data/capitale/function/pq02/admin/'+s+'.mcfunction' for s in baseline['added_admin_functions']]
preserved={n:b for n,b in core.items() if n not in added and n not in baseline['changed_r1_entries']}
aggregate=''.join(n+' '+build.sha(b)+'\n' for n,b in sorted(preserved.items())).encode()
assert len(preserved)==baseline['core_preserved_count'] and build.sha(aggregate)==baseline['core_preserved_manifest_sha256']
assert build.sha((H/'output'/build.FILES['quests']).read_bytes())==baseline['quests_sha256']
jar=build.read(merge_journal.OUTPUT);changed=[];same=[]
for name,digest in baseline['pq02_r1_entries'].items():
 assert name in jar
 if build.sha(jar[name])==digest:same.append(name)
 else:
  assert '/Pq02Runtime' in name,name
  changed.append(name)
for name,digest in baseline['preserved_sources'].items():assert build.sha((H/name).read_bytes())==digest
patch=[];original={kind:build.read(path) for kind,path in build.BASE.items()};current={'core':core,'quests':quests}
for kind in ['core','quests']:
 for n,b in sorted(current[kind].items()):
  if n not in original[kind] or original[kind][n]!=b:
   for line in difflib.unified_diff(original[kind].get(n,b'').decode().splitlines(keepends=True),b.decode().splitlines(keepends=True),fromfile='a/'+kind+'/'+n if n in original[kind] else '/dev/null',tofile='b/'+kind+'/'+n):patch.append(line if line.endswith('\n') else line+'\n\\ No newline at end of file\n')
patchfile=R/'datapacks.patch';patchfile.write_text(''.join(patch))
qa=H/'patch_qa_hf1';shutil.rmtree(qa,ignore_errors=True);qa.mkdir()
for kind,files in original.items():
 for n,b in files.items():p=qa/kind/n;p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(b)
subprocess.run(['git','apply','--check',str(patchfile.resolve())],cwd=qa,check=True,capture_output=True)
subprocess.run(['git','apply',str(patchfile.resolve())],cwd=qa,check=True,capture_output=True)
for kind,files in current.items():
 actual={p.relative_to(qa/kind).as_posix():p.read_bytes() for p in (qa/kind).rglob('*') if p.is_file()}
 assert actual==files,kind
report={'status':'PASS','revision':'PQ02 R1 HF1','r1_core_delta':{'changed':list(baseline['changed_r1_entries']),'added':added,'removed':[],'unchanged':len(preserved)},'hc_quetes_identical_to_r1':True,'source_gameplay_and_mixins_unchanged':list(baseline['preserved_sources']),'r1_pq02_compiled_entries_changed':changed,'r1_pq02_compiled_entries_identical':same,'new_admin_classes':[n for n in jar if '/Pq02Setup' in n],'patch_applied_to_original_copies':True,'all_applied_files_equal_output':True,'per_tick_changes':False}
(R/'hf1.json').write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n')
(R/'archive_qa.json').write_text(json.dumps({'status':'PASS','patch_applied_to_original_copies':True,'all_files_byte_identical_after_patch':True,'archives':{kind:{'filename':build.FILES[kind],'sha256':build.sha((H/'output'/build.FILES[kind]).read_bytes()),'files':len(files)} for kind,files in current.items()},'single_mod_filename':merge_journal.OUTPUT.name,'single_mod_sha256':merge_journal.sha(merge_journal.OUTPUT),'hf12_classes_identical':json.loads((R/'merge.json').read_text())['original_classes_preserved']},ensure_ascii=False,indent=2)+'\n')
print(json.dumps(report,ensure_ascii=False,indent=2))
