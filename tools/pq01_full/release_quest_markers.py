"""Archive QMARK1 : seulement deux datapacks modifiés et documentation à jour."""
import json,re,shutil,zipfile
from pathlib import Path
import build
from quest_markers import Q,GIVERS
from build_quest_markers import FILES
from verify_quest_markers import main as verify

def main():
 report=verify();out=build.OUT/'qmark1_audit';docs=build.HERE/'reference_docs/QMARK1';docs.mkdir(exist_ok=True)
 readme='''# Repères des donneurs du capitale core — QMARK1

26 octobre 2026 — Minecraft Fabric 1.21.11, EasyNPC 7.16.0, journal PQ01-R3-HF11.

Un « ! » jaune s'affiche au-dessus d'un donneur qui peut proposer une quête au personnage Nexus sélectionné. Les 24 quêtes du core sont couvertes, pour 20 donneurs. Un point commun suffit quand le même PNJ propose plusieurs quêtes.

## Installation

1. Arrêter le serveur après la sauvegarde habituelle du monde et des profils.
2. Dans les datapacks du monde serveur, remplacer le core HF9 par le fichier HF9_QMARK1 et `hc_quetes` HF4 par HF4_QMARK1 fournis dans `datapacks/`. Ne conserver qu'une version de chaque datapack.
3. Conserver le JAR de quêtes/journal HF11 sur le serveur et les clients. Aucun mod n'est inclus ou à remplacer dans cette livraison.
4. Redémarrer le serveur, sélectionner un personnage et s'approcher d'un donneur à moins de 24 blocs. Actualisation à la cadence existante de 20 ticks serveur, environ une seconde à 20 TPS.

Les points, liaisons et commandes du prologue existants restent valables. Aucun PNJ à déplacer ou recréer, aucune progression à réinitialiser.

- [Fonctionnement, donneurs et noms personnalisés](GUIDE_REPERES.md)
- [Guide complet de pose et configuration des PNJ](INSTALLATION_ET_POSE_PNJ.md)
- [Vérifications et limites](VALIDATION.md)
- [GitHub nº177](https://github.com/Arthuuuurrr/Indivis/pull/177)
'''
 rows=['| Donneur | Quêtes du core | Commande facultative pour un PNJ renommé |','|---|---|---|']
 for giver,(names,tags) in GIVERS.items():rows.append('| '+names[0]+' | '+', '.join(q['qid'].upper() for q in Q if q['giver']==giver)+' | `/function capitale:quest/markers/liaison/'+giver+'` |')
 guide='''# Points d'exclamation du core — QMARK1

## Comportement

- « ! » jaune : une offre est accessible au personnage sélectionné. Il disparaît après acceptation, quand le délai d'une journalière est actif ou qu'un prérequis n'est plus rempli.
- Les étapes récentes et les offres déjà ouvertes sont évaluées via les mêmes prédicats `hc_quetes:migration/state/...` que les interactions du core. Les nombres d'étape historiques ne sont pas recopiés dans les scoreboards actuels.
- Prise en compte des quêtes précédentes, du rang exigé, du blocage pendant une escorte lorsque l'acceptation l'applique, et de la quête active lorsque le core l'exige.
- Elias et Roland : l'interaction de livraison prioritaire masque la nouvelle offre correspondante. Magistrat : résidence et citoyenneté partagent un seul repère. Maelor : les quatre offres successives partagent aussi un repère.
- Le repère est privé au compte Minecraft **et au personnage Nexus**. Les marqueurs « ! » et « ? » déjà installés pour la séquence I sont conservés.
- La recherche est limitée à 24 blocs autour du joueur et aux entités EasyNPC publiques. Aucun PNJ de décor n'est déplacé ; aucun dialogue, rang, accès ou récompense n'est déclenché par le repère.

## Reconnaissance des PNJ

Les tags de rôle existants sont prioritaires, puis les noms connus du core sont utilisés. Le catalogue JSON joint donne tous les noms et tags reconnus. Les installations utilisant ces noms/tags fonctionnent sans commande supplémentaire.

Pour un PNJ renommé, se placer à moins de **quatre blocs**, plus près de lui que de tout autre EasyNPC public, puis exécuter uniquement la commande de sa ligne. Elle ajoute un tag de repère ; elle ne modifie ni les actions EasyNPC, ni le nom, ni l'apparence, ni les points du prologue. Le mot `liaison` désigne ici seulement l'association du repère au donneur.

'''+ '\n'.join(rows)+'''

Pour retirer une association facultative, se placer près du même PNJ et employer `/function capitale:quest/markers/liberer/<identifiant>`, par exemple `/function capitale:quest/markers/liberer/leovic`. La résolution automatique par nom ou ancien tag reste disponible. Les commandes administrateur se lancent comme joueur ; elles ne sélectionnent pas un joueur proche via `@p`.

## Charge et nettoyage

Les mises à jour réutilisent le même `text_display` tant que le donneur reste disponible. Sa position n'est actualisée que s'il s'est déplacé de plus de 0,05 bloc. Il n'y a ni copie de PNJ, ni lecture complète du NBT des PNJ en boucle, ni nouveau rafraîchissement global de visibilité. Les autres joueurs ne reçoivent pas ces repères privés.

Les repères devenus inutiles ou éloignés sont supprimés dans la dimension courante. Le nettoyage privé historique traite les changements de personnage et déconnexions ; les éventuels repères d'une autre dimension restent réutilisables au retour. Un personnage possède au plus un repère par donneur et par dimension chargée.

## Recette sur la carte

1. Avec un personnage éligible, approcher un donneur nommé comme dans le catalogue : un « ! » jaune apparaît. Accepter la quête : il disparaît à la mise à jour suivante.
2. Pour une journalière, vérifier l'absence pendant le délai, puis sa disponibilité lorsque l'offre du core redevient accessible.
3. Tester deux joueurs au même PNJ, dont un a déjà accepté la quête : chacun doit voir uniquement ses propres offres.
4. Changer de personnage Nexus : aucune offre du personnage précédent ne doit rester visible. Tester ensuite déconnexion et reconnexion.
5. Pour le prologue, poursuivre les dialogues et déplacements habituels : les fichiers de scène HF9 et le journal HF11 sont inchangés.

## Retour aux datapacks précédents

Avant de retirer QMARK1, un admin peut enlever ses seuls repères visuels dans chaque dimension concernée : `/kill @e[type=minecraft:text_display,tag=cap_qmark]`. Arrêter ensuite le serveur et remettre le core HF9 et `hc_quetes` HF4 précédents. Conserver le journal HF11. Les éventuels tags facultatifs `cap_qgiver_...` sont inertes sans QMARK1.
'''
 validation=f'''# Vérifications QMARK1 — 26 octobre 2026

**{report['native_checks']} contrôles natifs réussis**, sur Fabric 1.21.11 et EasyNPC 7.16.0.

- 24 quêtes du core recensées directement depuis leurs écritures de migration ; aucune n'est absente du catalogue. 20 donneurs reconnus par leur nom sur de vraies entités EasyNPC.
- Offres, étapes acceptées, prérequis, rangs, délais, escorte, conflit de quête active et priorité des livraisons testés avec les vrais prédicats et fonctions Minecraft.
- Le vrai `hc_quetes:runtime/second` appelle le nouveau repère ; ses autres intégrations sont remplacées par des fonctions sans effet dans ce banc uniquement.
- Deux propriétaires et un observateur : contrôle des vrais trackers et paquets d'apparition/destruction avec `QuestVisibility` et `EntityTrackerMixin` natifs. Changement de personnage, verrou de profil, déconnexion et nettoyage d'orphelins vérifiés.
- Conservation du même identifiant sur 20 mises à jour ; absence de nouvelle apparition réseau pour ce repère immobile. Suivi d'un déplacement, suppression hors de portée et liaison d'un PNJ renommé vérifiés.
- NBT des 20 PNJ d'essai et tous les états/dénouements de quête conservés par les mises à jour.
- Toutes les fonctions du core préexistantes restent identiques octet par octet, dont les dialogues, déplacements, gardes, zones protégées et fonctions administrateur. Dans `hc_quetes`, seule une ligne d'appel commentée est ajoutée à la fin de `runtime/second`, avec la description du pack actualisée.
- JAR HF11 strictement identique : SHA-256 `{report['base_sha256']['journal']}`. Aucun nouveau mod, mixin ou point d'entrée.

Les identités Nexus, connexions et réceptions de chunks sont synthétiques dans le banc. Aucun client graphique n'a été lancé : ni apparence à l'écran ni gain de FPS mesuré. Les noms et la géométrie de la carte utilisateur restent à vérifier lors de la recette. Le correctif n'ajoute pas de marqueurs aux quêtes hors du core ; leurs marqueurs existants restent conservés.

Les résultats détaillés et empreintes sont dans `audit/`. Reconstruction : `python3 work/pq01_full/build_quest_markers.py`, puis `CORE_QUEST_MARKERS=1 python3 work/pq01_full/run_world_test.py` et `python3 work/pq01_full/verify_quest_markers.py`.
'''
 install=(build.HERE/'reference_docs/HF11_INSTALLATION_ET_POSE_PNJ.md').read_text()
 a=install.index('**Mise à jour');b=install.index('\n\n',a)
 install=install[:a]+"**QMARK1 — 26 octobre 2026 : remplacer les deux datapacks serveur par HF9_QMARK1 et HF4_QMARK1 fournis. Conserver le journal HF11 sur le serveur et chaque client.** Aucune pose, liaison du prologue ou progression à refaire. [Guide des points d'exclamation](GUIDE_REPERES.md)."+install[b:]
 install=install.replace('# Installation, configuration et déplacements des PNJ — journal PQ01-R3-HF11','# Installation, configuration et déplacements des PNJ — QMARK1 + journal HF11',1)
 a=install.index('## 2. Installer');b=install.index('## 3.',a)
 install=install[:a]+'''## 2. Installer QMARK1 sur le journal HF11

| Composant | Action | Fichier |
|---|---|---|
| Core du monde serveur | Remplacer HF9 | `'''+FILES['core']+'''` |
| Quêtes du monde serveur | Remplacer HF4 | `'''+FILES['quests']+'''` |
| Journal, serveur et chaque client | Conserver HF11 | `'''+build.FILES['journal']+'''` |

Arrêter le serveur après sauvegarde, remplacer les deux datapacks sans conserver leurs anciennes copies dans le dossier actif, puis redémarrer. Les clients n'ont aucun fichier à changer pour QMARK1 si leur journal est déjà HF11. Nexus PRE34-SI1, Dialogue b10, EasyNPC 7.16.0, les voix et leurs réglages restent réutilisés. Aucun reset ni nouveau placement n'est demandé. Les commandes ci-dessous servent aux éditions volontaires du décor.

'''+install[b:]
 # Le guide est maintenant au même niveau que le rapport HF11 dans l'archive.
 install=install.replace('(../HOTFIX_MARCHE_R3_HF11.md)','(https://github.com/Arthuuuurrr/Indivis/blob/feat/pq01-dirigeable-r2-20261022/docs/quests/pq01-dirigeable-r3/HOTFIX_MARCHE_R3_HF11.md)')
 install=install.replace('Le correctif regroupe les répliques','Le journal HF11 regroupe les répliques',1)
 texts={'README.md':readme,'GUIDE_REPERES.md':guide,'VALIDATION.md':validation,'INSTALLATION_ET_POSE_PNJ.md':install}
 for n,s in texts.items():(docs/n).write_text(s)
 name='Indivis_Core_Reperes_Quetes_QMARK1_2026-10-26';dest=build.ROOT/'deliverables'/name
 if dest.exists():shutil.rmtree(dest)
 (dest/'datapacks').mkdir(parents=True);(dest/'audit').mkdir()
 for filename in FILES.values():shutil.copyfile(build.OUT/filename,dest/'datapacks'/filename)
 for n in texts:shutil.copyfile(docs/n,dest/n)
 for n in ['validation.json','diffs.json','catalogue.json','native_checks.txt']:shutil.copyfile(out/n,dest/'audit'/n)
 # Résoudre chaque commande et lien local de la documentation distribuée.
 functions=set()
 for filename in FILES.values():
  with zipfile.ZipFile(build.OUT/filename) as z:
   functions.update(n.split('/')[1]+':'+n.split('/function/',1)[1][:-11] for n in z.namelist() if '/function/' in n and n.endswith('.mcfunction'))
 refs=set();links=0
 for p in dest.glob('*.md'):
  for target in re.findall(r'/function ([a-z0-9_.-]+:[a-z0-9_/.-]+)',p.read_text()):
   if target.endswith('/') or target.endswith('<'):continue
   assert target in functions,(p,target);refs.add(target)
  for target in re.findall(r'\]\(([^)]+)\)',p.read_text()):
   if target.startswith(('http:','https:','#')):continue
   assert (p.parent/target.split('#')[0]).exists(),(p,target);links+=1
 archive=dest.with_suffix('.zip')
 with zipfile.ZipFile(archive,'w',zipfile.ZIP_DEFLATED,compresslevel=9) as z:
  for p in sorted(dest.rglob('*')):
   if p.is_file():z.write(p,p.relative_to(dest).as_posix())
 with zipfile.ZipFile(archive) as z:assert z.testzip() is None
 print(json.dumps({'path':str(archive),'bytes':archive.stat().st_size,'sha256':build.sha(archive.read_bytes()),'documented_functions':len(refs),'local_links':links},ensure_ascii=False,indent=2))
if __name__=='__main__':main()
