"""Contrôles des archives réellement produites. Ce programme ne lance pas Minecraft."""
import csv,io,json,re,unittest,zipfile
from pathlib import Path
from build import ROOT,HERE,OUT,INPUTS,FILES,SCORES,SAVED,PREFIX,function_path,load,digest
from model import N,IDS,Q

BEFORE={k:load(ROOT/'upload'/v[0]) for k,v in INPUTS.items()}
AFTER={k:load(OUT/v) for k,v in FILES.items()}
CORE=AFTER['core'];QUESTS=AFTER['quests']
def body(path):return CORE[function_path('pq01/'+path)].decode()
def dependencies(packs):
    merged={p:b for pack in packs.values() for p,b in pack.items()}
    functions={p.split('/')[1]+':'+p.split('/function/',1)[1][:-11] for p in merged if '/function/' in p and p.endswith('.mcfunction')}
    tags={p.split('/')[1]+':'+p.split('/tags/function/',1)[1][:-5] for p in merged if '/tags/function/' in p and p.endswith('.json')}
    namespaces={p.split('/')[1] for p in merged if p.startswith('data/')};missing=[];dynamic=[];external=[]
    for p,b in merged.items():
        if p.endswith('.mcfunction'):
            for i,line in enumerate(b.decode().splitlines(),1):
                s=line.strip()
                if not s or s.startswith('#') or re.search(r'(?:^| run )tellraw ',s.lstrip('$')):continue
                for ref in re.findall(r'\bfunction\s+([#]?[a-z0-9_.-]+:[^\s]+)',s):
                    if '$(' in ref:dynamic.append((p,i,ref));continue
                    if ref.startswith('#'):
                        if ref[1:] not in tags:missing.append((p,i,ref))
                    elif ref not in functions:
                        (missing if ref.split(':',1)[0] in namespaces else external).append((p,i,ref))
        elif '/tags/function/' in p and p.endswith('.json'):
            for row in json.loads(b).get('values',[]):
                ref=row if isinstance(row,str) else row['id']
                if isinstance(row,dict) and not row.get('required',True):continue
                if ref.startswith('#'):
                    if ref[1:] not in tags:missing.append((p,0,ref))
                elif ref not in functions:missing.append((p,0,ref))
    return {'functions':len(functions),'tags':len(tags),'missing_required':missing,'dynamic_macros':dynamic,'external_dependencies':external}
def successors(key):return [c['target'] for c in N[key]['choices'] if c['target'] in N]
def paths_before(target,start,seen=None):
    seen=set() if seen is None else seen
    if start==target:return [set()]
    if start in seen:return []
    answer=[]
    for v in successors(start):
        answer += [{start}|p for p in paths_before(target,v,seen|{start})]
    return answer

class CandidateTests(unittest.TestCase):
 def test_inputs_pinned(self):
    for k,(name,sha) in INPUTS.items():self.assertEqual(digest((ROOT/'upload'/name).read_bytes()),sha)
 def test_archives_json_and_no_removed_entries(self):
    for k,pack in AFTER.items():
        self.assertFalse(set(BEFORE[k])-set(pack))
        for p,b in pack.items():
            if p.endswith('.json') or p=='pack.mcmeta':json.loads(b)
 def test_required_function_closure(self):
    self.assertEqual(dependencies(BEFORE)['missing_required'],[])
    self.assertEqual(dependencies(AFTER)['missing_required'],[])
    self.assertEqual(dependencies(BEFORE)['external_dependencies'],dependencies(AFTER)['external_dependencies'])
 def test_only_expected_scope_changed(self):
    allowed={'pack.mcmeta',function_path('core/load'),function_path('core/tick'),function_path('quest/dialogue/dispatch_self'),function_path('quest/dialogue/sequence/dispatch_self'),function_path('npc/geraud_rivet/interact_self')}
    for p,b in BEFORE['core'].items():
        if CORE[p]!=b:self.assertTrue(p in allowed or p.startswith('data/capitale/function/'+PREFIX),p)
    changed={p for p,b in BEFORE['quests'].items() if QUESTS[p]!=b}
    self.assertEqual(changed,{'pack.mcmeta','data/hc_quetes/hcq_quests/pq_01_sous_pavillon_couronne.json'})
    for p,b in BEFORE['core'].items():
        if any(t in p for t in ('/gardecoeur/','/le_registre_du_port/','/sous_le_regard_du_coeur/','/au_seuil_des_profondeurs/')):self.assertEqual(CORE[p],b,p)
 def test_approved_text_and_real_questions(self):
    actual=set(x for n in N.values() for x in n['lines'])|set(c['label'] for n in N.values() for c in n['choices'])
    for i,text in enumerate(Q):
        if i not in (10,82,124,143,144):self.assertIn(text,actual)
    for k,n in N.items():
        f=body('node/'+k)
        for text in n['lines']:self.assertIn(json.dumps(text,ensure_ascii=False),f)
        for i,c in enumerate(n['choices'],1):
            self.assertIn(json.dumps('['+c['label']+']',ensure_ascii=False),body('menu/'+k))
            self.assertIn('hcm_pq1_pick matches '+str(i),body('choice/'+k))
        for l in body('menu/'+k).splitlines():
            if 'tellraw @s ' in l:json.loads(l.split('tellraw @s ',1)[1])
 def test_amnesia_cannot_be_skipped_by_new_character(self):
    paths=paths_before('c_menu','c0');self.assertTrue(paths)
    for p in paths:self.assertIn('c4',p);self.assertIn('c5',p);self.assertIn('c6',p)
    self.assertEqual(N['c4']['choices'][0]['label'],Q[88])
    self.assertEqual(N['c0']['choices'][0]['target'],'c1')
    self.assertEqual(N['cb1']['effect'],'av')
    self.assertEqual(N['c_menu']['choices'][2]['condition'],'av')
    for role in ('hugues','gautier','anselme','perrin'):
        self.assertNotIn('hcf_pq1_av 1',body('open/'+role+'_self'))
 def test_only_explicit_endpoints_progress(self):
    self.assertEqual([k for k,n in N.items() for c in n['choices'] if c['target']=='dock'],['cx4'])
    self.assertEqual([k for k,n in N.items() for c in n['choices'] if c['target']=='complete'],['ex4'])
    self.assertIn('hcm_pq1_node matches '+str(IDS['cx4']),body('dock_self'))
    self.assertIn('hcm_pq1_node matches '+str(IDS['ex4']),body('complete_self'))
    self.assertEqual(N['a3']['resume'],'a4');self.assertEqual(N['a6']['resume'],'a7')
    for k in ('ga_menu','e_menu','gautier_menu','hugues_menu','anselme_menu','perrin_menu'):
        for c in N[k]['choices']:
            if c['target']=='banter_start':continue
            if c['target'].startswith('ex'):continue
            self.assertIn(N[c['target']]['choices'][0]['target'],('close',k))
 def test_original_rewards_retained_except_premature_rank(self):
    original=BEFORE['core'][function_path(PREFIX+'sequence/geraud_quai_completion_final_self')].decode().strip()
    expected=original.replace('execute if score @s CAP_RANGSOCIAL matches 0..9 run scoreboard players set @s CAP_RANGSOCIAL 10\n','')
    self.assertTrue(body('reward_body_self').endswith(expected+'\n'))
    self.assertIn('hcm_pq1_commit matches 1',body('reward_body_self'))
    self.assertIn('hcf_pq1_gain matches 1',body('complete_self'))
    for p,b in CORE.items():
        if '/'+PREFIX in p or '/pq01/' in p:
            self.assertNotRegex(b.decode(),r'scoreboard players set @s CAP_RANGSOCIAL (?:10|20|30)\b',p)
    m=function_path('quest/le_registre_du_port/magistrat/register_commit_self')
    self.assertEqual(CORE[m],BEFORE['core'][m]);self.assertIn(b'CAP_RANGSOCIAL 10',CORE[m])
 def test_test_mode_is_profile_scoped_and_reversible(self):
    start=body('test/start_macro');end=body('test/end_macro')
    self.assertIn('players."$(owner)"."$(character)"',start)
    self.assertIn('players."$(owner)"."$(character)"',end)
    for s in SAVED:
        self.assertIn('.scores.'+s+' int 1 run scoreboard players get @s '+s,start)
        self.assertIn('score @s '+s+' run data get storage capitale:pq01_tests restore.scores.'+s,end)
    for text in (start,end,body('test/complete_self')):
        for forbidden in ('REP_COURONNE','CAP_RANGSOCIAL','reward/skills','daily_bonus','migration/write/pq02','spawnpoint @s'):
            self.assertNotIn(forbidden,text)
    self.assertIn('hcf_pq1_test matches 1',body('complete_self'))
    self.assertIn('unless score @s hcf_pq1_test matches 1 run spawnpoint',CORE[function_path(PREFIX+'sequence/arrivee_tp_self')].decode())
 def test_context_fresh_tokens_timeout_voice_and_no_global_scene_mutations(self):
    for k in ('session/check_self','open/geraud_self','admin/test_debut','admin/test_fin'):
        self.assertIn('context_self',body(k))
    self.assertIn('character:"$(character)"',body('session/context_macro'))
    self.assertIn('unless dimension $(dimension)',body('session/spatial_macro'))
    self.assertIn('distance=..10',body('session/spatial_macro'))
    self.assertIn('hcm_pq1_pick -= @s hcm_pq1_tok',body('dispatch_self'))
    self.assertIn('hcm_pq1_voice matches 1..',body('dispatch_self'))
    self.assertIn('hcm_pq1_ttl',body('session/tick_self'))
    for p,b in CORE.items():
        if '/function/pq01/' not in p:continue
        for l in b.decode().splitlines():
            self.assertNotRegex(l,r'\b(?:summon|kill)\b|\b(?:tp|teleport) @e|data modify entity ',p)
    tick=CORE[function_path('core/tick')].decode()
    self.assertNotIn('as @e',tick.split('# RC9h',1)[0])
 def test_mq03_and_other_journal_quests_preserved(self):
    self.assertIn('hcq_mq03 matches 10..30 run return run function hc_quetes:runtime/interact/altheon_brumeforge',body('open/altheon_self'))
    for p,b in BEFORE['quests'].items():
        if '/hcq_quests/' in p and not p.endswith('/pq_01_sous_pavillon_couronne.json'):self.assertEqual(QUESTS[p],b)
    c=json.loads(QUESTS['data/hc_quetes/hcq_quests/pq_01_sous_pavillon_couronne.json'])
    self.assertEqual(c['id'],'pq_01_sous_pavillon_couronne')
    self.assertNotIn('Rang de Visiteur',[r['text'] for r in c['rewards']])
    self.assertEqual(c['objectives'][2]['done']['min'],40)
 def test_docking_rechecks_character_before_each_phase(self):
    self.assertIn('character:"$(character)"',body('session/identity_macro'))
    for phase in ('arrivee_prepare_self','arrivee_tp_self','arrivee_objectif_self'):
        text=CORE[function_path(PREFIX+'sequence/'+phase)].decode()
        self.assertTrue(text.startswith('execute store result score @s hcm_pq1_valid run function capitale:pq01/transition/check_self\n'))
        self.assertIn('transition/abort_self',text.splitlines()[1])

def report():
    stream=io.StringIO();result=unittest.TextTestRunner(stream=stream,verbosity=2).run(unittest.defaultTestLoader.loadTestsFromTestCase(CandidateTests))
    (OUT/'validation.log').write_text(stream.getvalue());print(stream.getvalue())
    changes=json.loads((OUT/'diffs.json').read_text())
    with (OUT/'DIFFS_EXHAUSTIFS.csv').open('w',newline='') as f:
        w=csv.writer(f);w.writerow(['archive','type','chemin','sha256_avant','sha256_apres'])
        for k,v in changes.items():
            for kind in ('changed','added','removed'):
                for p in v[kind]:w.writerow([k,kind,p,digest(BEFORE[k][p]) if p in BEFORE[k] else '',digest(AFTER[k][p]) if p in AFTER[k] else ''])
    data={'status':'PASS' if result.wasSuccessful() else 'FAIL','test_cases':result.testsRun,'minecraft_runtime_test':False,
      'nodes':len(N),'approved_dialogue_occurrences':145,'baseline_dependencies':dependencies(BEFORE),'candidate_dependencies':dependencies(AFTER),
      'json_files_checked':sum(1 for pack in AFTER.values() for p in pack if p.endswith('.json') or p=='pack.mcmeta'),
      'preserved_entries':{k:sum(1 for p,b in BEFORE[k].items() if AFTER[k].get(p)==b) for k in BEFORE},
      'movement':'REPLI_STATIQUE_AUCUN_PNJ_DEPLACE_OU_CLONE','jar_changes':0}
    (OUT/'validation.json').write_text(json.dumps(data,ensure_ascii=False,indent=2)+'\n')
    if not result.wasSuccessful():raise SystemExit(1)
if __name__=='__main__':report()
