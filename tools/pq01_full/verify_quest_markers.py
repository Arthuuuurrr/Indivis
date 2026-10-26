"""Diffs contre les fichiers HF9/HF4 exacts, couverture et essai Fabric QMARK1."""
import json,re,zipfile,hashlib
from pathlib import Path
import build
from build_quest_markers import FILES
from quest_markers import Q,GIVERS,PREFIX

BASE_HASHES={'core':'cd714f723c47a1b69dbd3f7e60413b46a0f4c3d9cca8bc9231763c2cf76e341d',
             'quests':'71478ff2edf9c0246891429144407e5a8532e16baf5a5361494dfb4b78edd153',
             'journal':'1f63a074ae4871d02daa0c70729032b3985f9b5029548a9539ae5627625a1f83'}
def main():
 out=build.OUT/'qmark1_audit';out.mkdir(exist_ok=True)
 base={k:build.load(build.OUT/build.FILES[k]) for k in ['core','quests']}
 for k,h in BASE_HASHES.items():assert build.sha((build.OUT/build.FILES[k]).read_bytes())==h,k
 packs={k:build.load(build.OUT/FILES[k]) for k in FILES}
 diffs=json.loads((out/'diffs.json').read_text())
 for k in packs:
  assert not set(base[k])-set(packs[k]),k
  for n,b in base[k].items():
   allowed=n=='pack.mcmeta' or k=='quests' and n=='data/hc_quetes/function/runtime/second.mcfunction'
   if not allowed:assert packs[k][n]==b,(k,n)
  assert build.sha((build.OUT/FILES[k]).read_bytes())==diffs[k]['sha256']
 second='data/hc_quetes/function/runtime/second.mcfunction'
 assert packs['quests'][second].startswith(base['quests'][second])
 assert packs['quests'][second][len(base['quests'][second]):].decode()=='\n# QMARK1 : offres du core, personnage Nexus courant.\nfunction capitale:quest/markers/tick_self with storage hc_quetes:session current\n'
 core_qids=set()
 for n,b in base['core'].items():
  if n.endswith('.mcfunction'):core_qids.update(re.findall(r'function hc_quetes:migration/write/([a-z0-9_]+)/',b.decode()))
 assert core_qids=={q['qid'] for q in Q} and len(core_qids)==24
 functions={n.split('/')[1]+':'+n.split('/function/',1)[1][:-11] for p in packs.values() for n in p if '/function/' in n and n.endswith('.mcfunction')}
 references=0;generated=0
 for n,b in packs['core'].items():
  if n.startswith('data/capitale/function/quest/markers/'):
   generated+=1
   for target in re.findall(r'\bfunction ([a-z0-9_:/.-]+)',b.decode()):assert target in functions,(n,target);references+=1
   # Aucune écriture d'étape, d'accès, de récompense ou d'action EasyNPC.
   assert not re.search(r'scoreboard players (?:set|add|remove|reset)|data (?:modify|merge)|give @|clear @',b.decode()),n
  if n.endswith('.json'):json.loads(b)
 result=(build.ROOT/'work/pq01_hf4/runtime/qmark1-result.txt').read_text()
 assert result.startswith('PASS ') and 'QMARK1 checks' in result
 serverlog=(build.ROOT/'work/pq01_hf4/runtime/qmark1-server.log').read_text()
 assert 'Failed to load function' not in serverlog and 'Failed to parse' not in serverlog
 checks=int(re.search(r'PASS (\d+) QMARK1 checks',result)[1])
 (out/'native_checks.txt').write_text(result)
 report={'date':'2026-10-26','status':'PASS','core_quests_covered':24,'givers':20,'core_quest_ids':sorted(core_qids),
         'native_checks':checks,'generated_functions':generated,'resolved_function_references':references,
         'base_sha256':BASE_HASHES,'unchanged_journal_HF11':True,'all_preexisting_core_functions_byte_identical':True,
         'all_preexisting_quests_files_byte_identical_except_runtime_second_append_and_metadata':True,
         'all_preexisting_SI1_markers_byte_identical':True,'files':diffs,
         'limits':['Aucun rendu graphique ni FPS mesuré.','Connexions réseau, réception des chunks et identités Nexus synthétiques ; véritables fonctions, prédicats, EasyNPC, QuestVisibility et EntityTrackerMixin.',
                   'Géométrie et noms modifiés de la carte utilisateur non disponibles. Reconnaissance par noms/tags connus ; liaison de marqueur facultative pour les PNJ renommés.',
                   'Les autres quêtes de hc_quetes ne sont pas ajoutées au catalogue du core ; leurs marqueurs existants sont conservés.',
                   'Les marqueurs hors de la dimension courante restent réutilisables au retour ; le nettoyage historique retire les personnages précédents et propriétaires déconnectés.']}
 (out/'validation.json').write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n')
 print(json.dumps({k:report[k] for k in ['status','core_quests_covered','givers','native_checks','generated_functions','resolved_function_references','unchanged_journal_HF11']},indent=2))
 return report
if __name__=='__main__':main()
