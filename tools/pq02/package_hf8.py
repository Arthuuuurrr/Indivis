"""Valide les preuves HF8 et assemble les archives destinées à l’installation.

Les bancs natifs doivent avoir fini, séquentiellement, avant cet assemblage.
Aucun composant de test ou ancien JAR installable n'est livré.
"""
from pathlib import Path
import hashlib,json,re,zipfile,shutil,subprocess
import build,merge_journal,model
H=Path(__file__).resolve().parent;R=H/'reports';D=H/'delivery_merged'
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def load(name):return json.loads((R/name).read_text())
def passed(name):
 text=(R/(name+'.txt')).read_text();match=re.search(r'^PASS (\d+)',text,re.M)
 assert match,name
 return int(match[1]),text
def main():
 D.mkdir(exist_ok=True)
 jar=merge_journal.OUTPUT;digest=sha(jar);baseline=json.loads((H/'hf8_baseline.json').read_text())
 entries=build.read(jar);now={n:build.sha(b) for n,b in entries.items()}
 old=baseline['journal_entries'];changed={n for n in old if old[n]!=now.get(n)}
 expected={'fabric.mod.json','indivis_pq02_graph.json','net/hautecapitale/quetes/sequence/Pq02Runtime.class','net/hautecapitale/quetes/sequence/Pq02Runtime$1.class'}
 assert set(now)==set(old) and changed==expected
 assert not any('Bench' in n or n.startswith(('net/minecraft/','net/tompsen/')) for n in entries)
 javap=next(Path('/tmp/indivis_pq02_runtime/jdk').glob('*/bin/javap'))
 disassembly=subprocess.check_output([javap,'-p','-c','-cp',jar,'net.hautecapitale.quetes.sequence.Pq02Runtime$1'])
 assert hashlib.sha256(disassembly).hexdigest()==baseline['navigation_disassembly_sha256']
 (R/'navigation_bytecode_hf8.json').write_text(json.dumps({'status':'PASS','class':'net.hautecapitale.quetes.sequence.Pq02Runtime$1','comparison':'javap -p -c output matches frozen HF7 instructions; line-number-only class changes','sha256_disassembly':baseline['navigation_disassembly_sha256']},indent=2)+'\n')
 graph=json.loads(entries['indivis_pq02_graph.json']);assert len(graph['nodes'])==111
 assert {n['key']:n['id'] for n in graph['nodes']}==baseline['graph']['ids']
 counts={};texts={};evidence=[]
 for label in ['pq02_full_system_hf8','merged_native_hf8','hf8_profile_checkpoint_native','hf8_handoff_native']:
  count,text=passed(label);counts[label]=count;texts[label]=text
  fixture=load(label+'_fixture.json');assert fixture['integrated'] and fixture['mod_production_sha256']==digest
  log=(R/(label+'_server.log')).read_text()
  for forbidden in ['java.lang.AssertionError','[Indivis PQ02 1.0]','Failed to load function','MixinApplyError','Profile score restore failed','Profile hook failed']:
   assert forbidden not in log,(label,forbidden)
  evidence += [label+'.txt',label+'_fixture.json',label+'_server.log']
  # Compare the two changed functions actually installed in each executed world.
  for fn in ['gift_once_self','complete_once_self']:
   path='data/capitale/function/pq02/'+fn+'.mcfunction'
   actual=Path('/tmp/indivis_pq02_runtime')/label/'world/datapacks/pq02_fixture'/path
   expected_bytes=build.read(H/'output'/build.FILES['core'])[path].replace(b'capitale_currency:martin_dor',b'minecraft:gold_nugget')
   assert actual.read_bytes()==expected_bytes,(label,fn)
 assert counts['pq02_full_system_hf8']>=1400 and counts['merged_native_hf8']>=2800
 assert counts['hf8_profile_checkpoint_native']>=398 and counts['hf8_handoff_native']>=300
 native=texts['merged_native_hf8'];full=texts['pq02_full_system_hf8']
 edges=sum(len(n['choices'])+bool(n['next']) for n in graph['nodes'])
 assert native.count('graph native destination reached ')==edges==280
 for text in [native,full]:
  assert sum(l.startswith('HF8 systematic gift:') for l in text.splitlines())==76
 for required in ['full system: original QuestEngine still active at completion','original Aurele router naturally opens PQ03 offer after PQ02','actual Nexus A-B-A disk restore retains CAP_LEOVIC_GIFT','MQ03 closes and MQ04 opens','two complete escorts remain private and never move public Leovic']:
  assert required in full,required
 for player in ['A','B']:assert player+' waypoints=['+', '.join(map(str,range(18)))+']' in full
 integration=re.search(r'Full integration core ticks=(\d+), narrative interleavings=(\d+)',full)
 assert integration and int(integration[1])>300 and int(integration[2])>100
 protocol_count,protocol=passed('protocol_hf8');assert protocol_count==566
 assert load('protocol_hf8_fixture.json')['jar_sha256']==digest
 static=load('static.json');prologue=load('prologue_audit_static.json');boot=load('merge_boot.json')
 assert static['status']==prologue['status']==boot['status']=='PASS'
 assert boot['production_jar_sha256']==digest and boot['duplicate_companion_rejected']
 assert 'PASS 145 checks' in (R/'rules.txt').read_text()
 artifacts={k:{'filename':build.FILES[k],'sha256':sha(H/'output'/build.FILES[k])} for k in ['core','quests']}
 artifacts['journal']={'filename':jar.name,'sha256':digest}
 visited=re.search(r'Chronological visited pages=\[([^\]]*)\]',full)[1].split(', ')
 interrupted=re.search(r'Native close/reopen pages=\[([^\]]*)\]',full)[1].split(', ')
 limits=['Pas de client graphique ni de carte Indivis chargée ; cadrage, obstacles réels, FPS du modpack et voix non testés.',
  'Connexions, réception des chunks, sélection Nexus et confirmation de disponibilité caméra synthétiques. Rédacteur/chargeur/politique/fichiers de profils Nexus OPT3 réels.',
  'Monnaie remplacée par des pépites d’or dans le banc ; CapSkills et tirages aléatoires comptés par fonctions de test. Ces substitutions ne sont pas livrées.',
  'Le mode complet active les initialiseurs de quêtes/Dialogue et le tick core réel avec ses dépendances transitives utiles. Les initialisations comptes/réseau Nexus, RPG et Puffish restent isolées ou déclarées par fixtures ; le modpack complet n’est pas exécuté.',
  'Les 280 transitions sont testées par pages initialisées séparément. Les deux parcours chronologiques utilisent les choix et la marche après initialisation, et ne couvrent pas toutes les combinaisons.']
 result={'status':'PASS','revision':'PQ02 R1 HF8','date':'2026-11-01','artifacts':artifacts,
  'authority':'Décision utilisateur du 01/11/2026 : versement systématique pendant F06, réponse sèche −1, aucun retour pour récupérer les 50 Martins.',
  'counts':counts|{'client_protocol':protocol_count,'static':static['checks'],'java_rules':145,'prologue_static':prologue['checks'],'startup':9,'duplicate_companion_guard':1},
  'systematic_gift_checks_per_native_suite':76,'graph_records':111,'active_records':109,'legacy_aliases':{'f_report':'f07','post_gift':'post_menu'},'native_transitions_asserted':edges,
  'full_system':{'core_ticks':int(integration[1]),'narrative_interleavings':int(integration[2]),'original_quest_and_dialogue_jars_intact':True,'native_npc_interactions':True,'config_ui':'7.16.0'},
  'chronological_pages':visited,'native_close_and_reopen_pages':interrupted,'both_route_indices':list(range(18)),
  'jar_changed_entries_since_hf7':sorted(changed),'all_other_jar_entries_identical':len(now)-len(changed),
  'core_changed_since_hf1':sorted(baseline['core']['approved_changes']),'core_entries_preserved_since_hf1':baseline['core']['preserved_count'],
  'quests_changed_since_r1':sorted(baseline['quests']['approved_changes']),'other_narrative_nodes_preserved':99,
  'mq03_altheon_archives_mq04':True,'legacy_completed_profiles':'Aucun crédit/réputation/récompense rétroactif. Suppression du menu de récupération tardive.',
  'limits':limits}
 (R/'hf8.json').write_text(json.dumps(result,ensure_ascii=False,indent=2)+'\n')
 merged=load('merge.json');assert merged['sha256']==digest
 if 'datapacks_unchanged' in merged:merged['datapacks_sha256']=merged.pop('datapacks_unchanged')
 merged['status']='PASS_HF8_NATIVE_FULL_SYSTEM_PROTOCOL_STARTUP_AND_PRESERVATION';merged['tests']=result['counts']
 (R/'merge.json').write_text(json.dumps(merged,ensure_ascii=False,indent=2)+'\n')
 # The report describes the current revision, not the obsolete refusal policy.
 rows='\n'.join('| '+name+' | **PASS '+str(count)+'** |' for name,count in result['counts'].items())
 report=f'''# PQ02 R1 HF8 — rapport de correction et de validation

Livraison du 1er novembre 2026, Fabric/Minecraft 1.21.11, Java 21. Version installable destinée à la recette sur le serveur ; aucun déploiement en production ni essai graphique sur la carte Indivis effectué ici.

## Règle validée avec l’utilisateur

Le kit Work v1.2 et le script intégral v1.0 fournis ont été lus. Ils contenaient encore le don refusable/reportable. La précision du 1er novembre prévaut : **les 50 Martins d’Or sont versés systématiquement pendant la scène F06, avant la réponse, une seule fois par personnage**. Aucun retour auprès du Léovic permanent n’est requis.

Les quatre réponses sont conservées : curiosité (0/0), embarras (0/0), reconnaissance (+1/+1) et « Je ne demandais pas votre charité. » (−1/−1). Les pièces restent acquises dans les quatre cas. La formulation embarrassée et la réponse correspondante de Léovic ont été adaptées pour retirer le refus ; ces adaptations sont identifiées dans le script de travail v1.1 et `editorial_hf8.json`. Le script v1.0 fourni n’est pas écrasé.

## Changement de déroulement

Après l’inscription et la marche M–P, les questions de F restent disponibles. Entrer dans F06 remet la bourse et présente A/B/C/D. Chaque réponse mène à sa réplique de Léovic puis à F07. Il n’y a plus de deuxième menu pour accepter/refuser, ni d’offre différée après la quête. Ouvrir une question pratique, un menu administratif ou charger un personnage ne verse pas d’argent.

Le versement utilise la fonction réelle du core et l’item `capitale_currency:martin_dor`, 50 unités. Le verrou `CAP_LEOVIC_GIFT` est propre au personnage Nexus. La fonction exige l’inscription validée, la scène 90 et le propriétaire de dialogue PQ02. L’achèvement exige le don reçu et la décision F06 ; le moteur ne peut plus terminer une nouvelle scène non payée.

Les gains antérieurs restent conditionnels : A01-A +1 réputation/+1 impression, T04b-A +1/+1, M04-A 0/+1. F06-C +1/+1 et F06-D −1/−1 restent exclusifs. La réputation totale de PQ02 reste −1 à +3 ; l’impression globale −1 à +4. Aucun +5 automatique n’est réintroduit. Les 165 XP, compétences et les deux tirages historiques du Port sont conservés, un tirage à la fin et l’autre avec la bourse. Le contenu des pools reste intact ; leurs éventuelles pièces sont distinctes des 50 Martins personnels.

## Sauvegardes et compatibilité

Les 111 identifiants de nœud sont identiques à HF7 : 109 nœuds actifs et deux alias conservés uniquement pour les sauvegardes. `f_report` reprend à `f07` ; `post_gift` reprend au menu pratique sans versement. Une quête en cours à F06/F07 reçoit les pièces manquantes à la reprise de la scène, en conservant sa décision sociale. Une bourse déjà marquée reçue n’est pas recréée même si ses pièces ont été dépensées. Fermer/rouvrir, déconnecter ou sauvegarder/charger ne duplique pas le don.

Pour un personnage ayant déjà terminé PQ02 avant HF8 : aucun recalcul de réputation, seconde récompense ou rattrapage automatique à la connexion. L’ancien menu de récupération est supprimé. Ce choix de migration conserve les dossiers historiques et ne relance pas une quête terminée.

## Périmètre exact depuis HF7

| Fichier | Différence |
| --- | --- |
| JAR de quêtes | 4 entrées recompilées/modifiées : métadonnées, graphe, runtime PQ02 et sa classe interne. Les {len(now)-len(changed)} autres entrées sont strictement identiques, dont clients, Gautier, règles, moteur de déplacement, sauvegarde Nexus et mixins. Les instructions de la classe interne de navigation sont identiques à HF7 (`javap -p -c`) ; seules ses références de lignes compilées changent. |
| Core HF1 → HF8 | Seulement `pq02/gift_once_self`, `pq02/complete_once_self` et `pack.mcmeta`. Les {baseline['core']['preserved_count']} autres entrées sont identiques. |
| Journal R1 → HF8 | Description de la bourse dans la définition PQ02 et métadonnées. Identifiants, objectifs, conditions, plages et autres récompenses conservés. |
| Texte PQ02 | 99 nœuds hors don/retrouvailles inchangés ; 691 positions de lignes de script conservées. Chaque adaptation est recensée. |

PQ01 n’est pas réécrite : Hugues garde l’accroche, cinq questions et son congé ; le seul retrait éditorial reste le choix 6. Le correctif antérieur de Gautier est conservé à l’identique. Les fichiers de MQ01–MQ03 restent identiques à HF7. Le retour MQ03 vers Althéon à 10/20/30, puis les Sentinelles et l’Archivaire à 40/50, la clôture et MQ04 sont exécutés dans les bancs. Les marqueurs de quête et repères A–P ne sont pas déplacés. Aucune recherche ni boucle de versement n’est ajoutée au tick.

## Tests exécutés sur les artefacts livrés

| Suite | Résultat |
| --- | --- |
{rows}

Le banc complet conserve les JAR de quêtes et Dialogue d’origine de la version testée, avec leurs métadonnées, initialiseurs serveur et mixins, et active EasyNPC + ConfigUI. Le véritable `capitale:core/tick` tourne **{integration[1]} fois**, avec **{integration[2]} intercalations entre opérations narratives**. Deux joueurs suivent naturellement les 18 indices A–P. Les échanges au magistrat, l’ascenseur, la fin, les retrouvailles facultatives, le chargement Nexus A–B–A et l’offre suivante d’Aurèle sont contrôlés. Les parcours visitent {len(visited)} pages distinctes et testent une fermeture/réouverture native sur {len(interrupted)} pages ; les listes exactes figurent dans `hf8.json`.

La suite de branches exige la destination de chacune des **280 transitions explicites** des 111 enregistrements. Les deux alias ne présentent pas leur ancien dialogue. Les **76 contrôles ciblés du versement** sont exécutés dans le banc complet et dans celui des branches : les quatre réponses, argent avant choix, fermeture, disque Nexus réel, fin sans retour, dix anciennes positions de scène, absence de double paiement, argent déjà dépensé, ancien dossier terminé et refus d’achèvement impayé.

Le banc de sauvegarde vérifie les 111 positions, les sessions, jetons et acteurs pendant les instantanés périodiques Nexus ; la sauvegarde finale conserve son comportement de suspension. Le banc de transitions couvre les départs/reprises acceptés et les interruptions sans progression implicite. Les 108 combinaisons de décision font partie des 145 vérifications Java. L’analyse du prologue couvre 1 291 ressources de PQ01–PQ05 et ne trouve aucune référence littérale interne manquante.

Les contrôles de protocole utilisent l’état et la capture b10 ainsi que les vrais événements Fabric, avec écran et réseau de test. Le démarrage du JAR intact passe ses neuf contrôles, dont le mixin sur la classe OPT3 originale ; Fabric refuse le compagnon PQ02 obsolète en doublon. Les empreintes exactes sont dans `SHA256SUMS.txt` et `verification/hf8.json`.

## Limites des vérifications

'''+ '\n\n'.join(limits)+'''

Ces limites concernent le banc ; les substitutions ne sont pas installées dans les JAR/datapacks livrés. La recette graphique sur la carte reste décrite dans `INSTALLATION.md`. Les rapports HF1–HF7 sont historiques et leurs anciennes branches de don ne décrivent plus HF8.

## Installation

Remplacer le JAR de quêtes sur serveur et clients, le core sur serveur et le datapack du journal sur serveur, puis redémarrer complètement. **Aucune commande, pose, liaison ou réinitialisation à refaire.** Le mod reste intégré sous un seul identifiant. Lire `INSTALLATION.md` pour les noms exacts et `COMMANDES_PQ02.md` pour une première préparation éventuelle.
'''
 (H/'RAPPORT.md').write_text(report)
 (H/'CORRECTIF_PQ02_HF8.md').write_text('''# PQ02 HF8 — don systématique pendant la scène

**Les 50 Martins sont versés avant les quatre réponses de F06.** La réponse sèche donne −1 en réputation et en impression, tout en conservant l’argent ; la reconnaissance conserve +1/+1. Les réponses de curiosité et d’embarras restent neutres. Les refus/reports et la récupération au retour sont supprimés.

Installer le JAR HF8 sur le serveur et chaque client, et les deux datapacks HF8 sur le serveur, puis redémarrer. Les trois fichiers à remplacer sont dans `PQ02_HF8.zip`. Aucun compagnon supplémentaire, aucune commande ou liaison à refaire, aucune remise à zéro du personnage.

Les anciens points de F06/F07 reprennent sans double versement ni rejeu de réputation. Les quêtes déjà terminées gardent leur historique et ne sont pas créditées automatiquement à la connexion. Les détails et les résultats des bancs, distincts d’un essai en jeu sur la carte, figurent dans `RAPPORT.md` et `verification/hf8.json`.
''')
 coverage=['# PQ02 HF8 — couverture narrative','',f'{len(model.N)} identifiants conservés, dont 109 actifs et deux alias de sauvegarde ; {len(model.USED)} citations extraites du script de travail v1.1 ; 280 transitions explicites. Le contrôle de capture ne constitue pas un rendu graphique.','', '| Nœud | ID | Lignes du script v1.1 | Locuteur | Suite/compatibilité |','| --- | --- | --- | --- | --- |']
 for key,n in model.N.items():coverage.append('| '+key+' | '+str(n['id'])+' | '+(', '.join(map(str,n['source_lines'])) or 'menu/alias')+' | '+n['speaker']+' | '+(n.get('legacy_alias') and 'alias → '+n['legacy_alias'] or n['next'] or 'choix')+' |')
 (H/'COUVERTURE.md').write_text('\n'.join(coverage)+'\n')
 (H/'SHA256SUMS.txt').write_text('\n'.join(v['sha256']+'  '+v['filename'] for v in artifacts.values())+'\n')
 evidence += ['hf8.json','static.json','rules.txt','prologue_audit_static.json','protocol_hf8.txt','protocol_hf8_fixture.json','merge_boot.json','merge_boot.txt','merge_boot.log','merge_duplicate_guard.log','navigation_bytecode_hf8.json','editorial_hf8.json','coverage.json','unused_quotes.json','diffs.json','datapacks.patch','HF7_HF8_datapacks.patch','build.json','merge.json']
 docs=['INSTALLATION.md','COMMANDES_PQ02.md','CORRECTIF_PQ02_HF8.md','RAPPORT.md','COUVERTURE.md','SHA256SUMS.txt']
 patch={'mods/'+jar.name:jar.read_bytes()}
 for k in ['core','quests']:patch['datapacks/'+build.FILES[k]]=(H/'output'/build.FILES[k]).read_bytes()
 for name in docs:patch[name]=(H/name).read_bytes()
 for name in evidence:patch['verification/'+name]=(R/name).read_bytes()
 patch['script/Indivis_PQ02_Script_Consolide_v1_1_Don_Systematique.md']=model.SCRIPT.read_bytes()
 patchfile=D/'PQ02_HF8.zip';build.write(patchfile,patch)
 complete={n:b for n,b in patch.items() if not n.startswith(('mods/','datapacks/'))}
 for side in ['serveur','client']:complete[side+'/mods/'+jar.name]=jar.read_bytes()
 for k in ['core','quests']:complete['serveur/datapacks/'+build.FILES[k]]=(H/'output'/build.FILES[k]).read_bytes()
 bundle=D/'Indivis_PQ02_R1_HF8_Installation_Integree.zip';build.write(bundle,complete)
 for p in [patchfile,bundle]:
  archive=build.read(p);jars=[n for n in archive if n.endswith('.jar')];assert len(jars)==(1 if p==patchfile else 2)
  assert all(archive[n]==jar.read_bytes() for n in jars)
  for k in ['core','quests']:
   matches=[n for n in archive if n.endswith(build.FILES[k])];assert len(matches)==1 and archive[matches[0]]==(H/'output'/build.FILES[k]).read_bytes()
 shutil.copyfile(jar,D/jar.name)
 for name in docs:shutil.copyfile(H/name,D/name)
 # Source archive includes only project source/references, never private helpers or dependencies.
 source={}
 for folder in ['src','src_pq01','tests','kit']:
  for p in (H/folder).rglob('*'):
   if p.is_file() and p.suffix in {'.java','.py','.json','.md'} and not any(s.startswith('.') for s in p.relative_to(H).parts):source[p.relative_to(H).as_posix()]=p.read_bytes()
 for p in (H/'reference/tools/pq01_full').rglob('*.java'):source[p.relative_to(H).as_posix()]=p.read_bytes()
 for p in H.glob('*.py'):source[p.name]=p.read_bytes()
 for p in H.glob('*baseline.json'):source[p.name]=p.read_bytes()
 for name in [*docs,'README.md','input_manifest.json','runtime_plan.json']:source[name]=(H/name).read_bytes()
 for name in evidence:source['reports/'+name]=(R/name).read_bytes()
 sources=D/'Indivis_PQ02_R1_HF8_Sources_Integrees.zip';build.write(sources,source)
 files=[D/jar.name,patchfile,bundle,sources]
 manifest={'status':'PASS','files':[{'name':p.name,'bytes':p.stat().st_size,'sha256':sha(p)} for p in files]}
 (R/'delivery_hf8.json').write_text(json.dumps(manifest,ensure_ascii=False,indent=2)+'\n')
 print(json.dumps(manifest,ensure_ascii=False,indent=2))
if __name__=='__main__':main()
