"""Exact HF5 delta + actual Nexus periodic-save regression and final binary evidence."""
from pathlib import Path
import json,re
import build,merge_journal
H=Path(__file__).resolve().parent;R=H/'reports'
def main():
 baseline=json.loads((H/'hf6_baseline.json').read_text());old_name='haute-capitale-quetes-HF12-PQ02-R1-HF5-1.21.11.jar'
 assert baseline[old_name]=='7de6f0888cb7f531c1c4cd6a1ba7ef2b0c1f1ea8dcae906499f0fc726d1b4e2c'
 before=baseline[old_name+'_entries'];jar=build.read(merge_journal.OUTPUT);digest=merge_journal.sha(merge_journal.OUTPUT)
 changed=sorted(n for n in before if build.sha(jar[n])!=before[n]);added=sorted(set(jar)-set(before));assert set(before)<=set(jar)
 assert changed==['fabric.mod.json','indivis_pq02.mixins.json','net/hautecapitale/quetes/sequence/Pq02Runtime.class'],changed
 assert added==['net/hautecapitale/quetes/pq02/mixin/Pq02NexusCheckpointMixin.class','net/hautecapitale/quetes/sequence/Pq02ProfileCheckpoint$Scope.class','net/hautecapitale/quetes/sequence/Pq02ProfileCheckpoint.class'],added
 assert not any(any(p.startswith('.') for p in n.split('/')) for n in jar)
 packs={k:build.sha((H/'output'/build.FILES[k]).read_bytes()) for k in ['core','quests']}
 assert packs=={'core':'592ad598e9cb8164782adbfa873e325172fdb2c81458f3252de5e75f28851a9d','quests':'154cbdf0105296f5a94ccf6d54d7f9292c8f6fde05f9419cb7ff62225cec0fde'}
 counts={name:int(re.search(r'^PASS (\d+)',(R/file).read_text(),re.M).group(1)) for name,file in [('native','merged_native.txt'),('checkpoint','hf6_profile_checkpoint_native.txt'),('hf5_reproduction','hf5_profile_save_repro.txt'),('client_protocol','protocol_hf6.txt')]}
 assert counts=={'native':2425,'checkpoint':398,'hf5_reproduction':43,'client_protocol':566},counts
 checkpoint=(R/'hf6_profile_checkpoint_native.txt').read_text();native=(R/'merged_native.txt').read_text()
 assert sum('real Nexus snapshot preserves session, camera actor and choices at ' in x for x in checkpoint.splitlines())==111
 assert 'periodic save keeps the escort walking' in checkpoint and 'real final profile save still suspends' in checkpoint and 'queued first click still opens' in checkpoint
 fixtures={n:json.loads((R/n).read_text()) for n in ['merged_native_fixture.json','hf6_profile_checkpoint_native_fixture.json','hf5_profile_save_repro_fixture.json','protocol_hf6_fixture.json','merge_boot.json']}
 assert fixtures['hf5_profile_save_repro_fixture.json']['mod_production_sha256']==baseline[old_name]
 assert all(fixtures[n]['mod_production_sha256']==digest for n in ['merged_native_fixture.json','hf6_profile_checkpoint_native_fixture.json'])
 assert fixtures['protocol_hf6_fixture.json']['jar_sha256']==digest==fixtures['merge_boot.json']['production_jar_sha256']
 assert fixtures['merge_boot.json']['status']=='PASS' and fixtures['merge_boot.json']['nexus_periodic_mixin_applied_to_original_opt3_class'] and fixtures['merge_boot.json']['main_startup_checks']==9
 assert len([x for x in native.splitlines() if x.startswith('Gautier HF3:')])==15
 assert json.loads((R/'static.json').read_text())['status']=='PASS'
 for n in ['merged_native_server.log','hf6_profile_checkpoint_native_server.log','hf5_profile_save_repro_server.log','merge_boot.log']:
  text=(R/n).read_text();assert all(s not in text for s in ['Failed to load function','MixinApplyError','InvalidMixinException','[Indivis PQ02 1.0]','java.lang.AssertionError','Profile score save failed','Profile hook failed']),n
 assert '— ECRAN_FERME' in (R/'hf5_profile_save_repro_server.log').read_text()
 for n,data in jar.items():
  if n.endswith('.json'):json.loads(data)
 cfg=json.loads(jar['indivis_pq02.mixins.json']);assert cfg['client']==['Pq02ScreenLifecycleMixin'] and len(cfg['mixins'])==5
 finding=json.loads((R/'hf6_log_findings.json').read_text());assert len(finding['periodic_direct_save_callsites'])==1 and finding['cadence_ticks']==100
 report={'status':'PASS','revision':'PQ02 R1 HF6','production_jar_sha256':digest,'counts':counts,'changed_existing_entries':changed,'added_entries':added,'removed_entries':[],'preserved_existing_entries':len(before)-len(changed),'datapacks_unchanged':packs,'gautier_and_all_client_classes_byte_identical_to_hf5':True,'graph_rules_motion_rewards_waypoints_persistence_policy_byte_identical_to_hf5':True,'root_cause_reproduced_with_original_nexus_profile_writer':True,'original_nexus_opt3_target_transformed_by_production_mixin':True,'automatic_save_camera_nodes_tested':111,'full_main_startup_checks':9,'duplicate_companion_rejected':True,'fix':'Scope the original 100-tick Nexus save as a PQ02 checkpoint; keep its writer and core hook, preserve active/queued scene, restore the transient lock; keep final saves and explicit suspension destructive','limitations':['Nexus profile writer/policy/DTO and file output are real; account selection and periodic scheduler invocation use a fixture with the same original invocation site','Production checkpoint mixin is also applied to the untouched original OPT3 class in the startup test; Nexus account/network entrypoints are disabled there','Client graphics/world/entities and C2S networking remain doubled; native b10 lifecycle and Fabric message events are real','Currency/CapSkills/random port rewards retain documented fixture substitutions in the native narrative suite','No rendered Minecraft client, user map, real camera framing, FPS or recorded voices tested']}
 (R/'hf6.json').write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n')
 qa=json.loads((R/'archive_qa.json').read_text());qa.update({'single_mod_filename':merge_journal.OUTPUT.name,'single_mod_sha256':digest,'hf6_archive_diff_verified':True});(R/'archive_qa.json').write_text(json.dumps(qa,ensure_ascii=False,indent=2)+'\n')
 print(json.dumps(report,ensure_ascii=False,indent=2))
if __name__=='__main__':main()
