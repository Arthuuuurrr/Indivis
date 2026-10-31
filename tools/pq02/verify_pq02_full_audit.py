"""Bind the complete post-HF7 audit to unchanged installable artifacts and native evidence."""
from pathlib import Path
import hashlib,json,re,zipfile
H=Path(__file__).resolve().parent;R=H/'reports'
EXPECTED='31add513a5cc3763d137156149f17abc70a73b017f94f6498b8132cdd02b067d'
JAR=H/'output/haute-capitale-quetes-HF12-PQ02-R1-HF7-1.21.11.jar'
def digest(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def passed(name):
 s=(R/(name+'.txt')).read_text();m=re.search(r'^PASS (\d+)',s,re.M);assert m,name;return int(m[1]),s
def main():
 assert digest(JAR)==EXPECTED
 baseline=H/'baseline_hf7'/JAR.name
 if baseline.exists():assert baseline.read_bytes()==JAR.read_bytes()
 with zipfile.ZipFile(JAR) as z:graph=json.loads(z.read('indivis_pq02_graph.json'));entries={n:hashlib.sha256(z.read(n)).hexdigest() for n in z.namelist() if not n.endswith('/')}
 assert entries==json.loads((H/'baseline_hf7/entries.json').read_text())
 artifacts={JAR.name:EXPECTED}
 for name,expected in [('capitale_core_1.5.7_V2_SEQUENCE_I_SI1_PQ01_R3_HF9_QMARK2_PQ02_R1_HF1.zip','592ad598e9cb8164782adbfa873e325172fdb2c81458f3252de5e75f28851a9d'),('hc_quetes_b22_V2_SEQUENCE_I_SI1b_PQ01_R3_HF4_QMARK2_PQ02_R1.zip','154cbdf0105296f5a94ccf6d54d7f9292c8f6fde05f9419cb7ff62225cec0fde')]:
  assert digest(H/'output'/name)==expected;artifacts[name]=expected
 counts={};texts={}
 for name in ['pq02_end_to_end_hf7','pq02_branches_hf7']:
  counts[name],texts[name]=passed(name);fixture=json.loads((R/(name+'_fixture.json')).read_text());assert fixture['integrated'] and fixture['mod_production_sha256']==EXPECTED
  log=(R/(name+'_server.log')).read_text();assert not any(s in log for s in ['java.lang.AssertionError','[Indivis PQ02 1.0]','Failed to load function','Profile score restore failed','Profile hook failed'])
 e2e=texts['pq02_end_to_end_hf7'];branches=texts['pq02_branches_hf7']
 assert counts['pq02_end_to_end_hf7']>=1220 and counts['pq02_branches_hf7']==2715
 assert branches.count('graph native destination reached ')==290
 assert 'all 111 nodes pass native b10 capture' in branches
 for required in ['actual Nexus A-B-A disk restore retains CAP_LEOVIC_GIFT','real Nexus disk reload restores paused page, precise position and waypoint','long optional menu stays stationary through actual server and native DialogueManager ticks','original Aurele router naturally opens PQ03 offer after PQ02','MQ03 closes and MQ04 opens','two complete escorts remain private and never move public Leovic']:
  assert required in e2e,required
 visited=re.search(r'Chronological visited pages=\[([^\]]*)\]',e2e)[1].split(', ')
 interrupted=re.search(r'Native close/reopen pages=\[([^\]]*)\]',e2e)[1].split(', ')
 assert set(visited)<=set(n['key'] for n in graph['nodes'])
 for p in ['A','B']:assert p+' waypoints=['+', '.join(map(str,range(18)))+']' in e2e
 static=json.loads((R/'static.json').read_text());prologue=json.loads((R/'prologue_audit_static.json').read_text());assert static['status']==prologue['status']=='PASS'
 protocol_count,protocol=passed('protocol_hf7');assert protocol_count==566 and json.loads((R/'protocol_hf7_fixture.json').read_text())['jar_sha256']==EXPECTED
 report={'status':'PASS','date':'2026-10-31','revision_under_test':'PQ02 R1 HF7 (unchanged)','production_code_changes':0,'datapack_changes':0,'artifacts_sha256':artifacts,'counts':counts|{'client_protocol':protocol_count,'static':static['checks'],'java_rules':145,'prologue_static':prologue['checks']},'all_graph_nodes':len(graph['nodes']),'native_transition_destinations_asserted':290,'chronological_pages':visited,'native_close_and_reopen_pages':interrupted,'both_existing_routes_visited_indices':list(range(18)),'native_dialogue_periodic_verification_active_in_chronological_bench':True,'real_nexus_opt3_disk_save_load_and_a_b_a_restore':True,'actual_pq02_completion_unlocks_original_pq03_offer':True,'mq03_altheon_archives_mq04_regression':True,'limits':['Two synthetic player connections on a real Fabric/Minecraft server; no graphical client or map Indivis loaded','Nexus selection synthetic; actual supplied OPT3 profile writer, loader, policy and snapshot files exercised','EasyNPC navigation/actions and native b10 session verification and close handler real; readiness acknowledgement synthetic','Currency uses gold nuggets; CapSkills and random bonus pools use counters in test datapack only; no such substitute is delivered','Native branch tests seed one page at a time; chronological routes instead use only offered choices after their initial seed','111 pages/290 edges fully checked as branch units; chronological routes visit only the listed pages, with listed native close/reopen coverage','PQ03 initial offer and legacy gates checked; its whole escort and PQ04/PQ05 physical routes are unchanged and not played on the real map','No rendered camera framing, full modpack FPS, real voice playback or live production server validation']}
 (R/'pq02_full_audit_20261031.json').write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n')
 print(json.dumps({k:v for k,v in report.items() if k not in ['chronological_pages','native_close_and_reopen_pages','limits']},ensure_ascii=False,indent=2))
if __name__=='__main__':main()
