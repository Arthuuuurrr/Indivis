"""HF4: readiness lifecycle across all PQ02 cameras, with HF3 Gautier preserved."""
from pathlib import Path
import json,re
import build,merge_journal
H=Path(__file__).resolve().parent;R=H/'reports';baseline=json.loads((H/'hf4_baseline.json').read_text())
assert baseline['jar_sha256']=='f06ed47cd917ea1672afcc403b5d9a53e3fa17ab95ae84abcd4db26767f9c9cf'
packs={'core':'592ad598e9cb8164782adbfa873e325172fdb2c81458f3252de5e75f28851a9d','quests':'154cbdf0105296f5a94ccf6d54d7f9292c8f6fde05f9419cb7ff62225cec0fde'}
for k,v in packs.items():assert build.sha((H/'output'/build.FILES[k]).read_bytes())==v
jar=build.read(merge_journal.OUTPUT);before=baseline['entries'];changed=[n for n in before if before[n]!=build.sha(jar[n])];added=[n for n in jar if n not in before]
assert set(before)<=set(jar)
assert all(n=='fabric.mod.json' or '/Pq02Runtime' in n or '/Pq02Client' in n for n in changed),changed
assert len(added)==3 and all('/Pq02Focus' in n for n in added),added
for n,v in baseline['sources'].items():
 if n not in ['src/Pq02Client.java','src/Pq02Runtime.java']:assert build.sha((H/n).read_bytes())==v,n
for n in ['net/hautecapitale/quetes/sequence/Pq01Bridge.class','net/hautecapitale/quetes/sequence/Pq01Bridge$1.class']:assert build.sha(jar[n])==before[n]
native=(R/'merged_native.txt').read_text();count=int(re.search(r'^PASS (\d+) native checks',native).group(1));assert count>=2400
groups={s:[l for l in native.splitlines() if l.startswith(s)] for s in ['focus HF4:','Gautier HF3:','entry HF2:','setup HF1:']}
assert len(groups['focus HF4:'])==12 and len(groups['Gautier HF3:'])==15
protocol=(R/'protocol_hf4.txt').read_text();client_count=int(re.search(r'^PASS (\d+) real client protocol checks',protocol,re.M).group(1));assert client_count>=566
assert 'all 111 nodes pass native session and actual Fabric message pipeline' in protocol
protocol_fixture=json.loads((R/'protocol_hf4_fixture.json').read_text())
legacy=(R/'protocol_hf3_legacy.txt').read_text();assert 'PASS 2 HF3 adverse-order checks' in legacy
fixture=json.loads((R/'merged_native_fixture.json').read_text());boot=json.loads((R/'merge_boot.json').read_text());digest=merge_journal.sha(merge_journal.OUTPUT)
assert digest==fixture['mod_production_sha256']==boot['production_jar_sha256']==protocol_fixture['jar_sha256']
assert boot['status']=='PASS' and boot['duplicate_companion_rejected']
assert json.loads((R/'static.json').read_text())['status']=='PASS'
for name in ['merged_native_server.log','merge_boot.log']:
 text=(R/name).read_text();assert all(s not in text for s in ['Failed to load function','MixinApplyError','[Indivis PQ01-R3-HF11]','[Indivis PQ02 1.0]']),name
report={'status':'PASS','revision':'PQ02 R1 HF4','production_jar_sha256':digest,'native_checks':count,'real_client_protocol_checks':client_count,'native_readiness_checks':12,'nodes_through_real_fabric_events':111,'groups':groups,'datapacks_identical_to_hf3':packs,'changed_existing_jar_entries':changed,'added_jar_entries':added,'removed_jar_entries':[],'preserved_existing_entries':len(before)-len(changed),'gautier_hf3_classes_byte_identical':True,'rules_motion_graph_mixins_persistence_sources_identical_to_hf3':True,'protocol':'Server opens native session; client waits for session, UUID-matched actor and stable capture screen; non-OP ready command unlocks page; wait protected at START_CLIENT_TICK; acknowledgements bound to view/character/stage/session/token','legacy_test_limit':'The adverse-order test demonstrates a real HF3 dropped-page path; it does not reproduce or identify the original ECRAN_FERME GUI-removal caller.','diagnostics':'Bounded focus/page logs and one unexpected ScreenEvents.remove stack per focus, no chat/debug overlay','limitations':['Client protocol uses real b10 state and Fabric message events with graphical client/world/screen and C2S output doubled','Native gameplay suite simulates readiness acknowledgement; protocol suite exercises real client acknowledgement generation separately','Connections/chunk receipt/Nexus selection synthetic; external economic integrations substituted in gameplay fixture','No rendered Minecraft, actual Indivis map, FPS or recorded audio playback tested']}
(R/'hf4.json').write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n');print(json.dumps({k:v for k,v in report.items() if k!='groups'},ensure_ascii=False,indent=2))
