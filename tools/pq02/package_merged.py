"""Livraison finale à un seul mod de quêtes, après validation de la fusion."""
from pathlib import Path
import json,zipfile,hashlib,shutil,re
import build,merge_journal
H=Path(__file__).resolve().parent;R=H/'reports';D=H/'delivery_merged';D.mkdir(exist_ok=True)
def main():
 m=json.loads((R/'merge.json').read_text());native=(R/'merged_native.txt').read_text();fixture=json.loads((R/'merged_native_fixture.json').read_text());boot=json.loads((R/'merge_boot.json').read_text());digest=merge_journal.sha(merge_journal.OUTPUT)
 count=int(re.search(r'^PASS (\d+) native checks',native).group(1));setup_count=sum(line.startswith('setup HF1:') for line in native.splitlines());entry_count=sum(line.startswith('entry HF2:') for line in native.splitlines())
 assert count>1646 and setup_count>=37 and entry_count>=14
 assert json.loads((R/'hf1.json').read_text())['status']=='PASS'
 assert json.loads((R/'hf2.json').read_text())['status']=='PASS'
 assert fixture['integrated'] and fixture['mod_production_sha256']==digest==m['sha256']==boot['production_jar_sha256']
 assert boot['status']=='PASS' and boot['duplicate_companion_rejected'] and boot['production_metadata_and_entrypoints_unchanged_in_smoke_test']
 for name in ['merged_native_server.log','merge_boot.log']:
  log=(R/name).read_text();assert 'Failed to load function' not in log and '[Indivis PQ02 1.0]' not in log and 'MixinApplyError' not in log,name
 for k in ['core','quests']:assert build.sha((H/'output'/build.FILES[k]).read_bytes())==m['datapacks_unchanged'][k]
 m['status']='PASS_BYTE_PRESERVATION_NATIVE_AND_FULL_MAIN_STARTUP';m['tests']={'native':count,'admin_native':setup_count,'new_entry_native':entry_count,'full_production_main_startup':8,'obsolete_companion_guard':1,'client_rendered':False,'user_map_tested':False};(R/'merge.json').write_text(json.dumps(m,ensure_ascii=False,indent=2)+'\n')
 sums=[m['datapacks_unchanged'][k]+'  '+build.FILES[k] for k in ['core','quests']]+[digest+'  '+merge_journal.OUTPUT.name]
 (H/'SHA256SUMS.txt').write_text('# SHA-256 — livraison intégrée (un seul mod de quêtes)\n'+'\n'.join(sums)+'\n')
 summary={'status':'PASS','revision':'PQ02 R1 HF2','journal':merge_journal.OUTPUT.name,'sha256':digest,'preserved_hf12_classes':m['original_classes_preserved'],'preserved_hf12_resources':m['original_entries_preserved'],'pq02_entries_identical_to_build_component':m['pq02_entries_identical'],'native_checks':count,'admin_native_checks':setup_count,'new_entry_native_checks':entry_count,'previous_failure_reproduced':True,'full_main_startup_checks':8,'duplicate_guard_checks':1,'mod_ids':['haute_capitale_quetes'],'datapacks_changed_since_hf1':False,'limitations':['Gameplay test connections and Nexus selection synthetic','External currency/CapSkills/bonus implementations substituted only in gameplay fixture','Full metadata smoke uses declarative Nexus/Puffish/RPG providers, no players or their APIs','No rendered client, FPS or user map tested']}
 (R/'merged_tests_summary.json').write_text(json.dumps(summary,ensure_ascii=False,indent=2)+'\n')
 report=H/'RAPPORT.md';text=report.read_text();text=text.replace('les fonctions de banter et le JAR HF12 sont conservés','les fonctions de banter et le bytecode historique HF12 sont conservés');text=text.replace('`build.json` et `SHA256SUMS.txt` identifient exactement les binaires testés.','`merge.json` et `SHA256SUMS.txt` identifient exactement les binaires intégrés testés. `build.json` conserve la trace des composants intermédiaires.');row='| Démarrage du JAR fusionné de production intact | **8 PASS + garde anti-doublon PASS** | Identifiant unique, quatre commandes et trois initialiseurs réels ; Nexus/Puffish/RPG déclarés par des métadonnées de test, sans leurs API de jeu. |\n';text=text if row in text else text.replace('| Client graphique sur carte Indivis, FPS, voix enregistrées |',row+'| Client graphique sur carte Indivis, FPS, voix enregistrées |');report.write_text(text)
 docs=['INSTALLATION.md','COMMANDES_PQ02.md','CORRECTIF_PQ02_HF2.md','RAPPORT.md','COUVERTURE.md','SHA256SUMS.txt']
 evidence=['merge.json','merged_tests_summary.json','merged_native.txt','merged_native_server.log','merged_native_fixture.json','merge_boot.txt','merge_boot.json','merge_boot.log','merge_duplicate_guard.log','static.json','rules.txt','diffs.json','datapacks.patch','coverage.json','archive_qa.json','hf1.json','hf2.json','hf2_repro.txt','hf2_repro_fixture.json','hf2_repro_server.log']
 content={}
 for k in ['core','quests']:content['serveur/datapacks/'+build.FILES[k]]=(H/'output'/build.FILES[k]).read_bytes()
 for side in ['serveur','client']:content[side+'/mods/'+merge_journal.OUTPUT.name]=merge_journal.OUTPUT.read_bytes()
 for name in docs:content['docs/'+name]=(H/name).read_bytes()
 for name in evidence:content['verification/'+name]=(R/name).read_bytes()
 content['LIRE_AVANT_INSTALLATION.txt']='PQ02 R1 HF2 : correction du premier dialogue de Léovic par fonction imbriquée. Si HF1 est déjà installé, remplacer UNIQUEMENT le JAR de quêtes sur serveur ET clients puis redémarrer. Conserver le core R1 HF1, hc_quetes R1, les PNJ, les repères et les progressions. Aucune réinitialisation et aucune nouvelle liaison à faire. Un seul mod de quêtes, aucun compagnon indivis-pq02. Lire docs/CORRECTIF_PQ02_HF2.md. Installation complète : docs/INSTALLATION.md.\n'.encode()
 bundle=D/'Indivis_PQ02_R1_HF2_Installation_Integree.zip';build.write(bundle,content)
 shutil.copyfile(merge_journal.OUTPUT,D/merge_journal.OUTPUT.name)
 for name in docs:shutil.copyfile(H/name,D/name)
 # Verify the actual inner binaries and absence of either old installable JAR.
 with zipfile.ZipFile(bundle) as z:
  jars=[n for n in z.namelist() if n.endswith('.jar')];assert len(jars)==2 and all(n.endswith(merge_journal.OUTPUT.name) for n in jars)
  for n in jars:assert z.read(n)==merge_journal.OUTPUT.read_bytes()
  for k in ['core','quests']:assert z.read('serveur/datapacks/'+build.FILES[k])==(H/'output'/build.FILES[k]).read_bytes()
 source={}
 for folder in ['src','tests','kit']:
  for p in (H/folder).rglob('*'):
   if p.is_file() and p.suffix in ['.md','.java','.py','.json']:source[p.relative_to(H).as_posix()]=p.read_bytes()
 for p in (H/'reference/tools/pq01_full/tests').rglob('*.java'):source[p.relative_to(H).as_posix()]=p.read_bytes()
 for name in [*docs,'README.md','build.py','model.py','merge_journal.py','run_native.py','test_static.py','test_merged_boot.py','package_merged.py','verify_hf1.py','verify_hf2.py','hf1_baseline.json','hf2_baseline.json','prepare_runtime.py','bootstrap_runtime.py','runtime_plan.json','input_manifest.json']:source[name]=(H/name).read_bytes()
 for name in evidence:source['reports/'+name]=(R/name).read_bytes()
 build.write(D/'Indivis_PQ02_R1_HF2_Sources_Integrees.zip',source)
 patch={'mods/'+merge_journal.OUTPUT.name:merge_journal.OUTPUT.read_bytes(),'LIRE_AVANT_INSTALLATION.md':(H/'CORRECTIF_PQ02_HF2.md').read_bytes(),'RAPPORT.md':(H/'RAPPORT.md').read_bytes(),'SHA256SUMS.txt':(H/'SHA256SUMS.txt').read_bytes()}
 for name in ['hf2.json','merged_tests_summary.json','merged_native.txt','hf2_repro.txt']:patch['verification/'+name]=(R/name).read_bytes()
 build.write(D/'PQ02_HF2.zip',patch)
 print(json.dumps({'status':'PASS','files':[{'name':p.name,'bytes':p.stat().st_size,'sha256':merge_journal.sha(p)} for p in sorted(D.iterdir())]},ensure_ascii=False,indent=2))
if __name__=='__main__':main()
