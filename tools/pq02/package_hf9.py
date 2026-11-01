"""Livraison HF9 liée aux preuves du candidat exact, sans reconstruire les packs."""
from pathlib import Path
import json,re,hashlib,difflib
import build,build_hf9
H=Path(__file__).resolve().parent;R=H/'reports';D=H/'delivery_hf9';D.mkdir(exist_ok=True)
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def main():
 m=json.loads((R/'hf9_build.json').read_text());static=json.loads((R/'hf9_static.json').read_text());fixture=json.loads((R/'hf9_native_fixture.json').read_text());protocol=json.loads((R/'protocol_hf9_fixture.json').read_text())
 jar=H/'output'/build_hf9.DEST;digest=sha(jar)
 assert digest==m['sha256']==static['jar_sha256']==fixture['mod_production_sha256']==protocol['jar_sha256']
 assert static['status']==protocol['status']=='PASS'
 native=(R/'hf9_native.txt').read_text();match=re.match(r'PASS (\d+) native checks',native);assert match
 count=int(match[1]);pacing=sum(s.startswith('HF9 pacing:') for s in native.splitlines());c1=sum(s.startswith('C1 merge:') for s in native.splitlines())
 assert pacing>=88 and c1==105 and count>=1710
 client=(R/'protocol_hf9.txt').read_text();assert 'PASS 566 real client protocol checks' in client
 log=(R/'hf9_native_server.log').read_text();assert '70 fiche(s) de quête, 0 POI, 0 erreur(s)' in log
 assert not any(s in log for s in ['[Indivis PQ02','Failed to load function','AssertionError','MixinApplyError'])
 # The sole ERROR is the offline fixture's unrelated Yggdrasil key fetch.
 error_lines=[s for s in log.splitlines() if '/ERROR]' in s];assert all('Failed to request yggdrasil public key' in s for s in error_lines)
 for n,d in m['datapacks_unchanged'].items():assert sha(H/'output'/n)==d
 patch=[];before=H/'baseline_hf8_before_pacing_20261101'
 for n in ['src/Pq02Runtime.java','src/Pq02Motion.java','src/Pq02Cameras.java','model.py']:
  old=(before/n).read_text().splitlines(keepends=True) if (before/n).exists() else []
  patch.extend(difflib.unified_diff(old,(H/n).read_text().splitlines(keepends=True),fromfile='HF8/'+n,tofile='HF9/'+n))
 (R/'hf9.patch').write_text(''.join(patch))
 proof={'status':'PASS','revision':'PQ02-R1-HF9','jar':m,'static_checks':static['checks'],'java_rules':static['rules'],'server_assertions':count,'hf9_specific_assertions':pacing,'c1_specific_assertions':c1,'client_protocol_assertions':566,'loaded_quest_definitions':70,'quest_definition_errors':0,'function_parse_errors':0,'runtime_pq02_errors':0,'core_tick_and_frame_interleavings':re.search(r'Full integration core ticks=\d+, narrative interleavings=\d+',native)[0],'graphical_client':False,'user_map':False,'fps_mspt_benchmark':False,'voice_playback_tested':False,'tests_substitutions':['synthetic network connections, acknowledgements and Nexus selection','actual OPT3 profile writer/loader; synthetic players permitted to execute supplied profile hooks','currency replaced by gold nuggets inside fixture only','CapSkills and random bonus replaced by counters','mmospawn and identity commands stubbed','seven unchanged external equipment rewards not exercised'],'unrelated_fixture_log_errors':error_lines,'evidence':{n:sha(R/n) for n in ['hf9_build.json','hf9_static.json','hf9_rules.txt','hf9_native.txt','hf9_native_fixture.json','protocol_hf9.txt','protocol_hf9_fixture.json','hf9.patch']}}
 (R/'hf9.json').write_text(json.dumps(proof,ensure_ascii=False,indent=2)+'\n')
 report=f'''# Indivis — PQ02 R1 HF9 : pauses, marche et caméras

Date : 1er novembre 2026. Minecraft/Fabric **1.21.11**, Java **21**.

## Installation depuis HF8 + Fusion C1

**Seul le JAR de quêtes change, sur le serveur et sur chaque client. Aucune commande, liaison, pose de PNJ ou réinitialisation à refaire.** Remplacer l’ancien JAR par `{jar.name}`, garder les deux datapacks actuels et redémarrer complètement serveur et clients.

`PQ02_HF9.zip` contient `mods/` et la documentation. L’archive complète ajoute les mêmes datapacks HF8/Fusion C1, ainsi que les dossiers `serveur/` et `client/`. Les noms HF8 des datapacks sont intentionnels : les fichiers sont strictement conservés. Un seul mod `haute_capitale_quetes` est livré ; aucun compagnon ni dépendance n’est ajouté.

## Constats et modifications

| Retour | Cause constatée | HF9 |
| --- | --- | --- |
| Absence de pauses de lore | D/H étaient des points de transit sans arrêt narratif ; les questions exigeaient un clic pendant la marche. | Arrêt réel à D puis H, orientation vers le joueur et ouverture des répliques existantes à proximité. |
| Marche lente et arrêts entre repères | Consigne 0,8, arrêt/annulation de vitesse à chaque arrivée, prochain trajet au tick suivant ; relance inactive toutes les 20 ticks. | Consigne 1,2, soit +50 % ; passage au trajet suivant dans le même tick sans annuler l’élan ; relance inactive après 4 ticks au maximum. |
| Cadrage de l’entretien | Ancre commune basse et profil générique avec décalage de visée ; un log signalait une caméra hors de portée. | Ancre centrée entre les deux PNJ à 1,55 bloc au-dessus de leur niveau moyen ; profils b10 propres à l’entretien, visée centrée et décalage latéral réduit ; repli sur un interlocuteur accessible. |

### Pauses de lore

- **D** : réplique T01 sur l’activité du Port, avec les rubriques de questions existantes.
- **H** : réplique T04 sur la Garde du Port et les responsabilités des corps, avec ses questions.
- Les deux entrées proposent directement **« Reprenons notre route. »**. Échap permet aussi de poursuivre. Les sujets facultatifs et leurs conditions ne deviennent pas obligatoires.
- La pause attend le joueur : jusqu’à quatre blocs, dans la limite de la portée configurée, sans remplacer une autre conversation ouverte. Un texte bref au-dessus de la barre d’objets signale l’arrêt. Le joueur en retrait ne provoque pas une ouverture de caméra lointaine.
- Aucun délai ne retire le texte. La pause est privée et ne déplace pas le Léovic public.
- L’attente d’une pause non encore présentée est enregistrée dans le stockage de scène du personnage. Une sauvegarde/reprise la conserve ; après présentation ou départ volontaire, elle n’est pas rejouée.
- Une ancienne escorte reprend à sa position et à son repère enregistrés. Les pauses déjà dépassées ne sont pas imposées rétroactivement.

### Navigation

Le pathfinding reste celui d’EasyNPC/Minecraft, avec ses obstacles réels, ses objectifs A–P et sa suspension pendant les dialogues. Aucun déplacement de l’acteur par téléportation n’est ajouté. Les points ordinaires s’enchaînent ; D, H, M et P gardent leurs arrêts. Les pauses, un joueur éloigné, une erreur de chemin ou un repère indisponible arrêtent explicitement le guide.

La relance plus courte s’applique **seulement à une navigation inactive**, au plus cinq demandes par seconde ; aucun recalcul périodique d’un chemin actif n’est ajouté. Les recherches de PNJ/repères, la visibilité et les marqueurs conservent leur architecture. La consigne +50 % ne constitue pas une mesure de vitesse sur la carte ni un résultat de FPS/MSPT.

### Caméras

L’entretien conserve les plans communs des échanges M01/M05c et les plans sur l’interlocuteur des autres blocs. Le point commun est remonté vers les visages, la visée n’a plus de biais horizontal, le plan commun est élargi selon la séparation des PNJ, les plans individuels réduisent le décalage d’épaule et la transition native reste progressive (450 ms). Les PNJ s’orientent vers leur interlocuteur.

Les profils sont appliqués pendant l’ouverture de la seule session privée, puis l’ancienne configuration est immédiatement restaurée. Aucun réglage global ou fichier de configuration Dialogue n’est écrit. Le contrôleur b10 et son traitement des collisions restent utilisés. Si le point commun dépasse la portée d’ouverture, le cadrage utilise un acteur privé accessible ; si aucun ne l’est, le joueur doit se rapprocher sans perdre sa progression.

**Le rendu n’a pas été inspecté dans un client graphique.** Les paramètres, le centrage, le choix de cible et la restauration de configuration ont été contrôlés ; les obstacles et angles réels doivent être vérifiés sur la carte Indivis.

## Préservation

- **{m['preserved_count']} entrées du JAR HF8 identiques**, {len(m['changed'])} entrées modifiées, {len(m['added'])} ajout. La liste exacte figure dans `rapports/hf9_build.json`.
- Tous les mixins, le client, PQ01/Gautier, le journal existant et la séquence I conservent leurs entrées binaires HF8. Aucun autre mod ne change.
- **111 identifiants narratifs stables** ; seules les listes de réponses de T01 et T04 s’enrichissent de navigation. Les répliques du script, les autres nœuds et les décisions ne sont pas réécrits.
- Les **50 Martins automatiques pendant F06**, les réponses, les gains conditionnels, le souvenir global et les gardes de récompense restent ceux de HF8. Aucune nouvelle récompense de lore.
- Le **core HF8** et **hc_quetes Fusion C1** sont conservés **octet pour octet**. Les 70 fiches du journal, les corrections de Clément, Hugues et MQ01–MQ03 ne sont pas réécrits.
- Aucun objectif scoreboard, commande d’installation ou repère n’est ajouté ou renommé. Le nouveau champ d’attente est dans le stockage de scène déjà cloisonné par personnage et propriétaire.

## Résultats des contrôles exécutés

| Banc sur le candidat livré | Résultat |
| --- | --- |
| Compilation Java 21, assemblage du JAR HF8 et conservation | PASS |
| Conservation/statique | **{static['checks']} contrôles PASS** |
| Règles Java et politique Nexus | **145 assertions PASS**, dont 108 combinaisons de décisions |
| Serveur Fabric réel, JAR de production et mixins, fonctions core et Fusion C1 | **{count} assertions PASS**, dont **{pacing} propres à HF9** et **{c1} propres à C1** |
| Pipeline de messages et état b10 natifs sur les 111 nœuds | **566 assertions PASS** |
| Registre de quêtes réel | **70 fiches, 0 erreur** |
| Chargement des fonctions incluses dans le banc | **0 erreur de parsing**, {fixture['functions']} fonctions incluses |
| Exceptions du runtime PQ02 | **0** |

Le parcours chronologique démarre par les interactions de PNJ et rejoue **deux escortes A–P**. Il contrôle les deux pauses chez chaque joueur, six secondes de maintien du dialogue à chaque pause, la reprise explicite et par Échap, une reprise disque Nexus avant l’ouverture d’une pause, les sauvegardes périodiques, les choix, l’inscription et les 50 Martins. Il vérifie aussi les cadrages paramétrés b10 et leur repli à portée, l’identité du guide privé, la stabilité du PNJ public, la suite auprès d’Aurèle, Hugues et le retour MQ03 Althéon → Archives → MQ04.

{proof['core_tick_and_frame_interleavings']}. Les 105 cas C1 rejouent notamment insignes perdus, paiement des dix Martins, refus puis acceptation d’Aurèle, persistance du drapeau et nettoyage des documents de branches à deux joueurs.

### Limites explicites

Le monde est une fixture locale, pas la carte Indivis. Les connexions, l’accusé de réception client et la sélection Nexus sont synthétiques ; le writer/loader OPT3 est réellement exécuté. Les joueurs du test ont les permissions nécessaires aux hooks de profils fournis. Le pipeline client est testé sans moteur graphique. Monnaie remplacée par des pépites d’or, compétences/bonus par compteurs ; identité et mmospawn simulés ; sept récompenses d’équipement de mods absents ne sont pas exercées. **Aucun substitut de test n’est livré.** Le seul message ERROR du serveur de test est une récupération de clé Yggdrasil indisponible dans cet environnement ; aucun échec PQ02 ou de fonction n’est masqué.

Pas de test en jeu sur le serveur utilisateur, de rendu graphique des caméras, de doublage réel, ni de benchmark FPS/MSPT annoncé. Les preuves HF8 antérieures sont historiques et ne sont pas comptées comme de nouveaux tests HF9.

## Recette visuelle restante

1. Sur un personnage qui n’a pas dépassé D/H, laisser Léovic marcher puis vérifier ses deux arrêts, le temps de lecture et les questions. Tester « Reprenons notre route. » et Échap.
2. Rester quelques blocs en retrait pendant un arrêt, puis s’approcher. Vérifier qu’une autre conversation ouverte reste prioritaire.
3. Vérifier la marche entre les repères réels, notamment virages, marches et différences de hauteur.
4. Pendant M01/M05c puis les réponses au magistrat, inspecter les visages, les décors et les collisions de caméra avec le modpack réel.
5. À deux joueurs, vérifier à l’écran les copies privées et les pauses indépendantes ; terminer sans revenir pour recevoir les 50 Martins.

## Empreintes SHA-256

| Fichier | SHA-256 |
| --- | --- |
| `{jar.name}` | `{digest}` |
'''
 for name,d in m['datapacks_unchanged'].items():report+=f'| `{name}` (inchangé) | `{d}` |\n'
 report+='\nLes preuves détaillées sont dans `rapports/`. Les instructions de première préparation restent dans `COMMANDES_PQ02.md` ; elles ne sont pas à refaire pour HF9.\n'
 (H/'CORRECTIF_PQ02_HF9.md').write_text(report);(D/'RAPPORT_PQ02_HF9.md').write_text(report)
 sums=''.join(f'{d}  {n}\n' for n,d in [(jar.name,digest),*m['datapacks_unchanged'].items()]);(D/'SHA256SUMS_HF9.txt').write_text(sums)
 common={n:(H/n).read_bytes() for n in ['INSTALLATION.md','COMMANDES_PQ02.md','CORRECTIF_PQ02_HF9.md']};common['SHA256SUMS_HF9.txt']=sums.encode()
 for n in ['hf9.json','hf9_build.json','hf9_static.json','hf9_rules.txt','hf9_native.txt','hf9_native_fixture.json','protocol_hf9.txt','protocol_hf9_fixture.json','hf9.patch']:common['rapports/'+n]=(R/n).read_bytes()
 small={**common,'mods/'+jar.name:jar.read_bytes()};build.write(D/'PQ02_HF9.zip',small)
 full=common.copy()
 for side in ['serveur','client']:full[side+'/mods/'+jar.name]=jar.read_bytes()
 for n in m['datapacks_unchanged']:full['serveur/datapacks/'+n]=(H/'output'/n).read_bytes()
 build.write(D/'Indivis_PQ02_R1_HF9_Installation_Integree.zip',full)
 for file,expected in [(D/'PQ02_HF9.zip',small),(D/'Indivis_PQ02_R1_HF9_Installation_Integree.zip',full)]:
  actual=build.read(file);assert actual==expected and all('Bench.class' not in n for n in actual)
 delivery={'status':'PASS','jar_sha256':digest,'files':{str(p.relative_to(H)):{'bytes':p.stat().st_size,'sha256':sha(p)} for p in [jar,D/'PQ02_HF9.zip',D/'Indivis_PQ02_R1_HF9_Installation_Integree.zip',D/'RAPPORT_PQ02_HF9.md']}}
 (R/'hf9_delivery.json').write_text(json.dumps(delivery,ensure_ascii=False,indent=2)+'\n');print(json.dumps(delivery,ensure_ascii=False,indent=2))
if __name__=='__main__':main()
