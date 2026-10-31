"""Vérifie le correctif de l'entrée par fonction, sans modification des datapacks HF1/R1."""
from pathlib import Path
import json,re
import build,merge_journal
H=Path(__file__).resolve().parent;R=H/'reports'
expected={'core':'592ad598e9cb8164782adbfa873e325172fdb2c81458f3252de5e75f28851a9d','quests':'154cbdf0105296f5a94ccf6d54d7f9292c8f6fde05f9419cb7ff62225cec0fde'}
for kind,digest in expected.items():assert build.sha((H/'output'/build.FILES[kind]).read_bytes())==digest,kind
repro=(R/'hf2_repro.txt').read_text();assert repro.startswith('PASS REPRODUCED_HF1_FUNCTION_ENTRY_FAILURE')
repro_fixture=json.loads((R/'hf2_repro_fixture.json').read_text());assert repro_fixture['mod_production_sha256']=='807dc8b5ce286c74b3137a2161fb59b941fcc99eadb8ae5a16cc8b3bef64b24d'
native=(R/'merged_native.txt').read_text();count=int(re.search(r'^PASS (\d+) native checks',native).group(1));entry=[s for s in native.splitlines() if s.startswith('entry HF2:')]
assert count>1646 and len(entry)>=14
jar=build.read(merge_journal.OUTPUT);baseline=json.loads((H/'hf2_baseline.json').read_text())
assert baseline['baseline_jar_sha256']==repro_fixture['mod_production_sha256']
before=baseline['entries'];changed=[n for n in before if before[n]!=build.sha(jar[n])];added=[n for n in jar if n not in before]
assert all(n=='fabric.mod.json' or '/Pq02Runtime' in n for n in changed),changed
assert all('/Pq02Entries' in n for n in added),added
assert set(before)<=set(jar)
for name,digest in baseline['sources'].items():assert build.sha((H/name).read_bytes())==digest,name
report={'status':'PASS','revision':'PQ02 R1 HF2','reproduced_previous_jar_sha256':repro_fixture['mod_production_sha256'],'failure_reproduced':'Native Minecraft function dispatch, before private actor is available','native_checks':count,'new_entry_assertions':len(entry),'entry_assertions':entry,'datapacks_identical_to_hf1':expected,'changed_existing_jar_entries':changed,'added_jar_entries':added,'removed_jar_entries':[],'preserved_existing_entries':sum(before[n]==build.sha(jar[n]) for n in before),'client_rules_motion_graph_mixins_identical_to_hf1':True,'request_queue':'one request per account, validated character/world/stage/connection/session; drained at server end tick','rendered_client':False,'user_map_tested':False}
(R/'hf2.json').write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n');print(json.dumps(report,ensure_ascii=False,indent=2))
