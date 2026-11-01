"""HF5 : garde de fenêtre PQ02 et différences exactes avec le JAR HF4 livré."""
from pathlib import Path
import json,re,hashlib,zipfile
import build,merge_journal
H=Path(__file__).resolve().parent;R=H/'reports'

def main():
 b=json.loads((H/'hf5_baseline.json').read_text());assert b['jar_sha256']=='a401eec8b5fbe1396c651715cd457a01ff86cb531c5bcc5686352b44a9f19285'
 jar=build.read(merge_journal.OUTPUT);before=b['entries'];digest=merge_journal.sha(merge_journal.OUTPUT)
 changed=sorted(n for n in before if build.sha(jar[n])!=before[n]);added=sorted(set(jar)-set(before))
 assert set(before)<=set(jar)
 assert changed==['fabric.mod.json','indivis_pq02.mixins.json','net/hautecapitale/quetes/sequence/Pq02Client.class'],changed
 assert added==['net/hautecapitale/quetes/pq02/mixin/Pq02ScreenLifecycleMixin.class'],added
 assert not any(any(p.startswith('.') for p in n.split('/')) for n in jar)
 for name,sha in b['sources'].items():
  if name!='src/Pq02Client.java':assert build.sha((H/name).read_bytes())==sha,name
 for name in ['net/hautecapitale/quetes/sequence/Pq01Bridge.class','net/hautecapitale/quetes/sequence/Pq01Bridge$1.class']:assert build.sha(jar[name])==before[name]
 packs={'core':'592ad598e9cb8164782adbfa873e325172fdb2c81458f3252de5e75f28851a9d','quests':'154cbdf0105296f5a94ccf6d54d7f9292c8f6fde05f9419cb7ff62225cec0fde'}
 for k,v in packs.items():assert build.sha((H/'output'/build.FILES[k]).read_bytes())==v
 native=(R/'merged_native.txt').read_text();guard=(R/'hf5_screen_guard_native.txt').read_text();legacy=(R/'hf4_screen_reset_repro.txt').read_text();protocol=(R/'protocol_hf5.txt').read_text()
 counts={name:int(re.search(r'^PASS (\d+)',s).group(1)) for name,s in [('native',native),('screen_guard',guard),('hf4_reproduction',legacy)]}
 counts['client_protocol']=int(re.search(r'^PASS (\d+) real client protocol checks',protocol,re.M).group(1))
 assert counts=={'native':2425,'screen_guard':63,'hf4_reproduction':7,'client_protocol':566},counts
 assert 'mixin de production est réellement appliqué' in guard and 'menu étranger furtif entre deux ticks' in guard
 assert 'HF4 reproduit ECRAN_FERME' in legacy and 'HF4 manque aussi un menu étranger' in legacy
 fixtures={n:json.loads((R/n).read_text()) for n in ['merged_native_fixture.json','hf5_screen_guard_native_fixture.json','hf4_screen_reset_repro_fixture.json','protocol_hf5_fixture.json','merge_boot.json']}
 assert fixtures['hf4_screen_reset_repro_fixture.json']['mod_production_sha256']==b['jar_sha256']
 assert digest==fixtures['merged_native_fixture.json']['mod_production_sha256']==fixtures['hf5_screen_guard_native_fixture.json']['mod_production_sha256']==fixtures['protocol_hf5_fixture.json']['jar_sha256']==fixtures['merge_boot.json']['production_jar_sha256']
 assert fixtures['merge_boot.json']['status']=='PASS' and fixtures['merge_boot.json']['duplicate_companion_rejected']
 assert json.loads((R/'static.json').read_text())['status']=='PASS'
 assert len([s for s in native.splitlines() if s.startswith('Gautier HF3:')])==15
 for name in ['merged_native_server.log','hf5_screen_guard_native_server.log','merge_boot.log']:
  text=(R/name).read_text();assert all(s not in text for s in ['Failed to load function','MixinApplyError','InvalidMixinException','[Indivis PQ01-R3-HF11]','[Indivis PQ02 1.0]','java.lang.AssertionError']),name
 for n,data in jar.items():
  if n.endswith('.json'):json.loads(data)
 cfg=json.loads(jar['indivis_pq02.mixins.json']);assert cfg['client']==['Pq02ScreenLifecycleMixin'] and len(cfg['mixins'])==4
 qa=json.loads((R/'archive_qa.json').read_text());qa.update({'single_mod_filename':merge_journal.OUTPUT.name,'single_mod_sha256':digest,'hf5_archive_diff_verified':True,'unchanged_datapack_patch_proof_reused_from_hf1':True});(R/'archive_qa.json').write_text(json.dumps(qa,ensure_ascii=False,indent=2)+'\n')
 report={'status':'PASS','revision':'PQ02 R1 HF5','production_jar_sha256':digest,'counts':counts,'changed_existing_entries':changed,'added_entries':added,'removed_entries':[],'preserved_existing_entries':len(before)-len(changed),'datapacks_unchanged':packs,'gautier_pq01_byte_identical_to_hf4':True,'runtime_graph_rewards_route_persistence_and_previous_mixins_byte_identical_to_hf4':True,'client_mixin_applied_by_real_knot_in_fixture':True,'production_mixin_environment':'client only','reproduced_failure':'HF4 loses an acknowledged capture containing a real quote and choice after setScreen(null), then native b10 sends ECRAN_FERME; a foreign menu opened and closed between ticks has the same result','fix':'Intercept only a null-screen reset of the PQ02-owned active capture before removal; retain explicit close/server close/other sessions; record a short foreign-menu visit so native return still applies','origin_of_original_live_setScreen_caller_identified':False,'limitations':['Minecraft screen/client world/entities and graphics are doubled, native b10 lifecycle and production mixin are exercised through Knot','Test mixin client list is enabled in the server-side fixture only; production stays client only','Actual Fabric message pipeline is exercised separately for all111 PQ02 nodes; no full graphical network client/server was launched','Connections/Nexus selection and external economic integrations retain documented fixture substitutions','No rendered Indivis map, FPS, sound playback or actual original GUI-reset caller tested']}
 (R/'hf5.json').write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n');print(json.dumps(report,ensure_ascii=False,indent=2))

if __name__=='__main__':main()
