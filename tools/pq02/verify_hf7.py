"""HF7: reproduce the user's actual close choice, test native handoffs, retain the exact HF6 remainder."""
from pathlib import Path
import json,re
import build,merge_journal
H=Path(__file__).resolve().parent;R=H/'reports'
def main():
 b=json.loads((H/'hf7_baseline.json').read_text());assert b['jar_sha256']=='4f94704c1b204051a85b76836552e5a03ab71a3b071c0cfa919ff7f6d1b6cc73'
 jar=build.read(merge_journal.OUTPUT);digest=merge_journal.sha(merge_journal.OUTPUT);before=b['entries'];assert set(before)<=set(jar)
 changed=sorted(n for n in before if build.sha(jar[n])!=before[n]);added=sorted(set(jar)-set(before))
 assert changed==['fabric.mod.json','net/hautecapitale/quetes/sequence/Pq02Runtime.class'],changed
 assert added==['net/hautecapitale/quetes/sequence/Pq02Transitions.class'],added
 assert not any(any(p.startswith('.') for p in n.split('/')) for n in jar)
 packs={k:build.sha((H/'output'/build.FILES[k]).read_bytes()) for k in ['core','quests']}
 assert packs=={'core':'592ad598e9cb8164782adbfa873e325172fdb2c81458f3252de5e75f28851a9d','quests':'154cbdf0105296f5a94ccf6d54d7f9292c8f6fde05f9419cb7ff62225cec0fde'}
 counts={name:int(re.search(r'^PASS (\d+)',(R/file).read_text(),re.M).group(1)) for name,file in [('native','merged_native.txt'),('handoff','hf7_handoff_native.txt'),('hf6_reproduction','hf6_handoff_repro.txt'),('nexus_checkpoint','hf7_profile_checkpoint_native.txt'),('client_protocol','protocol_hf7.txt')]}
 assert counts=={'native':2425,'handoff':305,'hf6_reproduction':73,'nexus_checkpoint':398,'client_protocol':566},counts
 native=(R/'merged_native.txt').read_text();handoff=(R/'hf7_handoff_native.txt').read_text();legacy=(R/'hf6_handoff_repro.txt').read_text()
 assert "HF6 reproduces Arthur's trace" in legacy and 'private interaction blocked' in legacy
 assert 'same actor reaches elevator P' in handoff and 'Escape resumes the paused waypoint' in handoff and 'real private interaction also accepts' in handoff and 'actual native b10 EasyNPC look-at' in handoff
 graph=json.loads(jar['indivis_pq02_graph.json']);events=[n for n in graph['nodes'] if n['next'].startswith('event:')]
 assert sum('visible green action replaces misleading close at ' in s for s in handoff.splitlines())==len(events)
 assert sum('handles native ' in s for s in handoff.splitlines())==18
 assert sum('never starts an accepted departure silently' in s for s in handoff.splitlines())==8
 fixtures={n:json.loads((R/n).read_text()) for n in ['merged_native_fixture.json','hf7_handoff_native_fixture.json','hf6_handoff_repro_fixture.json','hf7_profile_checkpoint_native_fixture.json','protocol_hf7_fixture.json','merge_boot.json']}
 assert fixtures['hf6_handoff_repro_fixture.json']['mod_production_sha256']==b['jar_sha256']
 for n in ['merged_native_fixture.json','hf7_handoff_native_fixture.json','hf7_profile_checkpoint_native_fixture.json']:assert fixtures[n]['mod_production_sha256']==digest,n
 assert digest==fixtures['protocol_hf7_fixture.json']['jar_sha256']==fixtures['merge_boot.json']['production_jar_sha256']
 assert fixtures['merge_boot.json']['status']=='PASS' and fixtures['merge_boot.json']['main_startup_checks']==9 and fixtures['merge_boot.json']['nexus_periodic_mixin_applied_to_original_opt3_class'] and fixtures['merge_boot.json']['duplicate_companion_rejected']
 assert json.loads((R/'static.json').read_text())['status']=='PASS'
 assert sum(s.startswith('Gautier HF3:') for s in native.splitlines())==15
 for n in ['merged_native_server.log','hf7_handoff_native_server.log','hf6_handoff_repro_server.log','hf7_profile_checkpoint_native_server.log','merge_boot.log']:
  text=(R/n).read_text();assert all(s not in text for s in ['Failed to load function','MixinApplyError','InvalidMixinException','[Indivis PQ02 1.0]','java.lang.AssertionError','Profile score save failed','Profile hook failed']),n
 for n,data in jar.items():
  if n.endswith('.json'):json.loads(data)
 findings=json.loads((R/'hf7_log_findings.json').read_text());assert findings['last_action_pick']==15
 report={'status':'PASS','revision':'PQ02 R1 HF7','production_jar_sha256':digest,'counts':counts,'changed_existing_entries':changed,'added_entries':added,'removed_entries':[],'preserved_existing_entries':len(before)-len(changed),'datapacks_unchanged':packs,'client_gautier_nexus_checkpoint_navigation_graph_rewards_and_previous_mixins_byte_identical_to_hf6':True,'actual_log_trace_reproduced_on_exact_hf6_binary':True,'native_event_page_controls':len(events),'native_close_handoff_cases':18,'rejected_automatic_progression_reasons':8,'real_easy_npc_private_interaction_with_and_without_preopened_capture':True,'native_actor_reached_magistrate_and_elevator_after_optional_menu_escape':True,'native_b10_easy_npc_look_open_close_callbacks_exercised':True,'full_main_startup_checks':9,'duplicate_companion_rejected':True,'limitations':['Navigation uses a flat fixture route and synthetic connections/character selection; the real user map and its obstacles are not loaded','Real Minecraft server, EasyNPC actions/navigation and b10 sessions/capture; native close packets are invoked through their actual server handler','Client graphics/world/entities and outbound network remain doubled; actual Fabric events and b10 state are exercised separately','Nexus regression uses its real profile writer/policy/DTO; its account/network startup is disabled in the original-class mixin smoke test','Currency/CapSkills/random port rewards keep documented fixture substitutions; these are not shipped','No rendered client, user-map camera framing, FPS or actual voice playback tested']}
 (R/'hf7.json').write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n')
 qa=json.loads((R/'archive_qa.json').read_text());qa.update({'single_mod_filename':merge_journal.OUTPUT.name,'single_mod_sha256':digest,'hf7_archive_diff_verified':True});(R/'archive_qa.json').write_text(json.dumps(qa,ensure_ascii=False,indent=2)+'\n')
 print(json.dumps(report,ensure_ascii=False,indent=2))
if __name__=='__main__':main()
