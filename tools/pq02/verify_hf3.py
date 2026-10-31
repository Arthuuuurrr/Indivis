"""Périmètre HF3 : écran PQ02 et rythme Gautier ; datapacks et autre gameplay conservés."""
from pathlib import Path
import json,re,difflib
import build,merge_journal
H=Path(__file__).resolve().parent;R=H/'reports'
baseline=json.loads((H/'hf3_baseline.json').read_text())
assert baseline['baseline_jar_sha256']=='78b76966458ff4d51e8cfd0fd222a0873f51389cf731292d121fb0e87d3be736'
expected={'core':'592ad598e9cb8164782adbfa873e325172fdb2c81458f3252de5e75f28851a9d','quests':'154cbdf0105296f5a94ccf6d54d7f9292c8f6fde05f9419cb7ff62225cec0fde'}
for kind,digest in expected.items():assert build.sha((H/'output'/build.FILES[kind]).read_bytes())==digest,kind
jar=build.read(merge_journal.OUTPUT);old=baseline['entries']
changed=[n for n in old if old[n]!=build.sha(jar[n])]
allowed=['fabric.mod.json','net/hautecapitale/quetes/sequence/Pq02Client.class','net/hautecapitale/quetes/sequence/Pq01Bridge.class','net/hautecapitale/quetes/sequence/Pq01Bridge$1.class']
assert set(changed)==set(allowed),changed
assert set(old)==set(jar),'No class/resource addition or removal in HF3'
for name,digest in baseline['sources'].items():
 if name!='src/Pq02Client.java':assert build.sha((H/name).read_bytes())==digest,name
before=(H/'reference/tools/pq01_full/java/net/hautecapitale/quetes/sequence/Pq01Bridge.java').read_text()
after=(H/'src_pq01/Pq01Bridge.java').read_text()
assert build.sha(before.encode())==baseline['pq01_bridge_source_sha256']
a=before.index('    v.waitingNode=Pq01Ids.id(expected);',before.index('static int moveScene('));b=before.index('    return 1;\n  }',a)
c=after.index('    // Le retour au poste accompagne');d=after.index('    return 1;\n  }',c)
normalized=(after[:c]+before[a:b]+after[d:]).replace('double speed=a.role.equals("gautier")?1.25d:0.65d;','double speed=a.role.equals("gautier")?1.0d:0.65d;')
assert normalized==before,'PQ01 changes must remain limited to the return block and Gautier speed'
(R/'hf3_pq01.patch').write_text(''.join(difflib.unified_diff(before.splitlines(keepends=True),after.splitlines(keepends=True),fromfile='HF12/Pq01Bridge.java',tofile='HF3/Pq01Bridge.java')))
native=(R/'merged_native.txt').read_text();count=int(re.search(r'^PASS (\d+) native checks',native).group(1))
groups={tag:[l for l in native.splitlines() if l.startswith(tag)] for tag in ['screen HF3:','Gautier HF3:','entry HF2:','setup HF1:']}
assert count==1697 and len(groups['screen HF3:'])==17 and len(groups['Gautier HF3:'])==15
assert len(groups['entry HF2:'])==15 and len(groups['setup HF1:'])==37
assert (R/'hf3_screen_repro.txt').read_text().startswith('PASS REPRODUCED_HF2_ABSENT_SCREEN_ECRAN_FERME')
repro=json.loads((R/'hf3_screen_repro_fixture.json').read_text());assert repro['mod_production_sha256']==baseline['baseline_jar_sha256']
fixture=json.loads((R/'merged_native_fixture.json').read_text());boot=json.loads((R/'merge_boot.json').read_text())
digest=merge_journal.sha(merge_journal.OUTPUT)
assert fixture['mod_production_sha256']==digest==boot['production_jar_sha256']
assert boot['status']=='PASS' and boot['duplicate_companion_rejected']
for name in ['merged_native_server.log','merge_boot.log']:
 text=(R/name).read_text()
 assert all(s not in text for s in ['Failed to load function','MixinApplyError','[Indivis PQ01-R3-HF11]','[Indivis PQ02 1.0]']),name
assert json.loads((R/'static.json').read_text())['status']=='PASS'
report={'status':'PASS','revision':'PQ02 R1 HF3','production_jar_sha256':digest,'native_checks':count,'new_screen_checks':17,'new_gautier_checks':15,'evidence':groups,'datapacks_identical_to_hf2':expected,'baseline_hf2_jar_sha256':baseline['baseline_jar_sha256'],'changed_existing_jar_entries':changed,'added_entries':[],'removed_entries':[],'preserved_existing_entries':len(jar)-len(changed),'preserved_hf12_classes':182,'preserved_hf12_entries':220,'pq01_scope':'Only moveScene return block and Gautier navigation speed 1.0 -> 1.25; all other bridge source identical','pq02_scope':'Only client adapter changed; server runtime, graph, rules, motion and all mixins identical to HF2','screen_reproduction':'Fixture deliberately removes capture GUI; native b10 monitor emits ECRAN_FERME with HF2. Exact originating GUI-removal operation on user client is not identified by supplied logs.','headless_client_substitutes':['Minecraft client/screen and graphical camera','C2S send redirected only in test DialogueClientState to recording sink'],'limitations':['No rendered Minecraft client or actual Indivis map','Connections/chunk receipts and Nexus selection synthetic','Currency/CapSkills/external bonus implementations substituted in gameplay fixture only','Full startup smoke uses metadata-only Nexus/Puffish/RPG declarations, no gameplay API test']}
(R/'hf3.json').write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n');print(json.dumps(report,ensure_ascii=False,indent=2))
