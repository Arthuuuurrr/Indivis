"""Lie la livraison C1 aux fichiers fusionnés et au banc natif effectivement passé."""
from pathlib import Path
import hashlib,json,re,difflib,zipfile
import reconcile
H=Path(__file__).resolve().parent
def main():
 static=json.loads((H/'validation_static.json').read_text());assert static['status']=='PASS'
 archive=H/'output'/reconcile.DEST;digest=hashlib.sha256(archive.read_bytes()).hexdigest();assert digest==static['datapack_sha256']
 reports=H/'regression/reports';text=(reports/'hcmerge_full_system.txt').read_text();log=(reports/'hcmerge_full_system_server.log').read_text()
 match=re.match(r'PASS (\d+) native checks',text);assert match and int(match[1])==1580
 specific=[l for l in text.splitlines() if l.startswith('C1 merge:')];assert len(specific)==105
 for value in ['Failed to load function','java.lang.AssertionError','[Indivis PQ02 1.0]','Profile hook failed','Profile score restore failed']:assert value not in log,value
 assert '[quetes] 70 fiche(s) de quête, 0 POI, 0 erreur(s)' in log
 fixture=json.loads((reports/'hcmerge_full_system_fixture.json').read_text())
 journal=H.parents[1]/'work/pq02/output/haute-capitale-quetes-HF12-PQ02-R1-HF8-1.21.11.jar'
 jsha=hashlib.sha256(journal.read_bytes()).hexdigest();assert jsha=='92dbafab347b1fd775de6476d36e75b6bae2ebefbcc574bfaac141fc63c79180'==fixture['mod_production_sha256']
 # The native fixture copies the production quest archive before generating
 # its explicit currency and third-party service substitutes.
 assert (H/'regression/output'/reconcile.DEST).read_bytes()==archive.read_bytes()
 installed=H/'runtime/hcmerge_full_system/world/datapacks/pq02_fixture'
 own=reconcile.read(reconcile.OURS);merged=reconcile.read(archive)
 for n in static['changed_existing']+static['added']:
  if n.endswith('.mcfunction') and (installed/n).exists():assert (installed/n).read_bytes()==merged[n].replace(b'capitale_currency:martin_dor',b'minecraft:gold_nugget'),n
 # This directory is test-only: no substituted implementation is packaged.
 preserved=static['unchanged'];parts=[]
 for n in static['changed_existing']+static['added']:
  parts += difflib.unified_diff(own.get(n,b'').decode().splitlines(True),merged[n].decode().splitlines(True),fromfile='a/'+n,tofile='b/'+n)
 (H/'FUSION_C1.patch').write_text(''.join(parts))
 limits=['Serveur Fabric/Minecraft 1.21.11, véritables fonctions, entités EasyNPC, dialogues et moteur de journal ; pas de client graphique ni de carte Indivis chargée.',
  'Connexions, sélection de personnage Nexus et disponibilité de caméra synthétiques. Écriture/chargement des profils OPT3 réels, dont le nouveau drapeau de refus et son isolation entre personnages.',
  'Monnaie remplacée par des pépites d’or dans le banc ; CapSkills et bonus aléatoires comptés par fixtures. mmospawn est un service simulé et sept récompenses d’équipement inchangées de mods tiers sont des placeholders non exercés.',
  'Aucun substitut de banc dans le datapack livré. Pas de mesure de FPS/MSPT, de rendu des livres, d’écoute des voix ni de prélèvement natif dans un mod de sac à dos.']
 summary={'status':'PASS','revision':'HC_QUETES_FUSION_C1','date':'2026-11-01','filename':archive.name,'sha256':digest,'bytes':archive.stat().st_size,'inputs':reconcile.EXPECTED,
  'files':3330,'changed_existing':static['changed_existing'],'added':static['added'],'removed':[],'unchanged':preserved,
  'tests':{'static':static['checks'],'native_total':1580,'native_c1':105,'quest_definitions_loaded':70,'quest_decode_errors':0,'function_load_errors':0},
  'journal_hf8_unchanged_sha256':jsha,'core_unchanged_sha256':'6ff671f7b71ff696980b1fdfa4302481dec30cac7db8eca6e0fb2c9bcd4a9aa6',
  'no_user_commands_or_npc_rebinding_required':True,'limits':limits}
 (H/'validation_finale.json').write_bytes(reconcile.dump(summary))
 table='\n'.join('| `'+n.removeprefix('data/hc_quetes/')+'` |' for n in static['changed_existing']+static['added'])
 report=f'''# Fusion hc_quetes C1 — 1er novembre 2026

## Résultat

Les corrections b22 de Clément sont fusionnées dans la version d’Arthur **V2 / SI1b / PQ01 / QMARK2 / PQ02 HF8**. Le datapack livré contient **3 330 fichiers** : **17 fichiers existants modifiés**, **7 ajouts** et **{preserved} fichiers conservés octet pour octet**. Aucun fichier existant n’est supprimé.

Le ZIP est directement installable : `pack.mcmeta` et `data/` sont à la racine. Le core et les JAR HF8 restent inchangés.

## Méthode et provenance

La comparaison directe des deux fichiers fournis mélangeait les corrections de Clément avec la migration v2. La base commune **b21 originale**, antérieure aux adaptations de lore, a été retrouvée et identifiée par son empreinte. Clément y a changé **12 fichiers** et ajouté **2 fonctions**. Ses différences de mise en forme JSON n’ont pas été appliquées comme des modifications de jeu.

Le convertisseur historique et ses tables de migration gelées de SI1b sont réutilisés pour traduire les étapes, objectifs auxiliaires et objets. Les valeurs ne sont pas renumérotées à partir des nouveaux textes. Une fusion à trois versions conserve les modifications d’Arthur ; elle résout notamment le tick sans perdre ses branchements de séquence I. Aucun conflit n’est laissé dans la livraison.

| Entrée | SHA-256 |
| --- | --- |
| Clément `hc_quetes-0.1.0.b22.zip` | `{reconcile.EXPECTED['clement']}` |
| Arthur `hc_quetes…PQ02_R1_HF8.zip` | `{reconcile.EXPECTED['ours']}` |
| Base commune `hc_quetes-0.1.0.b21.zip` | `{reconcile.EXPECTED['common']}` |
| Fusion C1 livrée | `{digest}` |

## Corrections de Clément intégrées

| Sujet | Comportement conservé dans la fusion |
| --- | --- |
| Insigne d’Orrin Vasca | Une mort ultérieure peut rendre l’insigne perdu, sans redonner la boussole. L’insigne n’est pas régénéré après la quête qui en a besoin. |
| Mère Aveline | L’interaction peut rendre l’insigne manquant puis enchaîner le pardon dans le même appel. |
| Remise à Hector | L’étape n’avance qu’après réception effective de l’insigne ; un inventaire plein produit le message prévu. La reprise d’un dossier déjà bloqué peut rendre l’insigne manquant. |
| Caisse de médicaments | Le remplacement exige réellement dix Martins et en retire exactement dix. Les montants 0, 1, 3, 9, 10, 11 et 20 ont été exercés. Le message pour un montant insuffisant explique l’inventaire principal et le sac à dos ; le datapack ne prélève pas à l’intérieur d’un sac de mod. |
| Offre d’Aurèle refusée | Le « ! » disparaît après refus, mais l’offre reste accessible au clic et peut être acceptée plus tard. |
| Documents souvenirs | Retrait en fin de quête selon les bornes de Clément, avec conservation des documents encore utilisés par une quête suivante et respect des différentes branches. |
| Cages de Colin | L’objectif de clé est distingué de l’ouverture des cages ; guidage et descriptions fournis par Clément conservés avec les identifiants v2. |

Le refus d’Aurèle utilisait un nouveau `hcq_n=-1` dans b22. La fusion l’adapte avec **`hcf_cqvcbb01_ref`**, un drapeau de personnage distinct : les étapes canoniques restent à 0 avant acceptation. Le marqueur seul est filtré ; l’interaction manuelle reste ouverte. Le drapeau est créé automatiquement, conservé par Nexus, effacé à l’acceptation et par le reset existant. Il n’y a pas de migration vers un ancien score partagé.

## Préservation des modifications d’Arthur

- Les **70 identifiants canoniques** restent présents, sans doubles fiches sous leurs anciens noms.
- **69 définitions de quête** sont strictement identiques à HF8 ; seule la fiche des ruines de Carrec reçoit le changement de Clément concernant Colin.
- PQ02 garde son versement systématique de **50 Martins pendant F06**, ses réponses et ses gains conditionnels, sans retour requis ni +5 automatique.
- Les fonctions de la séquence I, liaisons de gardes, runtime MQ et données de marqueurs sont identiques à HF8. Le retour MQ03 vers Althéon puis les Archives est exercé dans la régression.
- Les objets et livres de lore d’Arthur, leurs textes et leur mise en page sont **identiques**. Les anciennes versions de livres présentes dans le b22 brut ne remplacent pas le travail de lore.
- Les JAR et le core ne sont pas recompilés ni remplacés pour C1. Les parcours et liaisons de PNJ déjà placés sont réutilisés.

## Tests exécutés

| Vérification | Résultat |
| --- | --- |
| Intégrité, conservation, références et correspondances d’états | **{static['checks']} contrôles statiques PASS** |
| Banc serveur complet sur l’archive fusionnée | **1 580 assertions PASS**, dont **105 spécifiques à C1** |
| Décodage par le véritable journal HF8 | **70 fiches**, **0 erreur** |
| Chargement des fonctions de la fixture | **0 échec** après substitution explicite des dépendances tierces absentes |

Les contrôles C1 portent sur les paiements exacts, la récupération et la non-duplication des insignes, l’inventaire plein, le refus/reprise d’offre, la persistance du nouveau drapeau sur disque Nexus et l’isolation entre personnages. Le balayage est exécuté pour les documents et les fins de branches, avec un second joueur dont la quête est encore en cours. Le relevé du naturaliste et la copie réutilisée dans J09 restent disponibles tant que leur dernière quête dépendante n’est pas terminée.

La régression HF8 rejoue deux parcours de PQ02 jusqu’au magistrat puis à l’ascenseur, les choix et reprises de la scène finale, les sauvegardes Nexus, l’offre suivante d’Aurèle, Hugues et MQ03. Le vrai tick core tourne 773 fois, avec 245 intercalations narratives. Les résultats sont liés au JAR HF8 d’origine et au ZIP C1 par leurs empreintes.

## Limites

'''+ '\n\n'.join(limits)+f'''

Les erreurs intermédiaires de fixture (objectifs de test incomplets, monde réutilisé et dépendances externes non déclarées) ont été corrigées dans le banc. Elles ne sont pas des changements apportés au datapack livré. Les résultats ci-dessus sont ceux du dernier passage complet, pas de ces tentatives intermédiaires.

## Installation

1. Arrêter le serveur et conserver une copie du datapack remplacé.
2. Dans `<monde>/datapacks/`, remplacer uniquement l’ancien `hc_quetes` par **`{archive.name}`**. Conserver une seule version, y compris si l’ancienne était décompressée.
3. Garder le **core PQ02 HF8** et le **JAR de quêtes HF8** déjà installés, puis redémarrer le serveur.

**Aucune commande, pose, liaison de PNJ ou remise à zéro à refaire. Aucun fichier client à changer pour cette fusion.** Le drapeau ajouté se crée avec les fonctions de chargement normales du datapack.

## Ressources modifiées ou ajoutées

Les noms ci-dessous sont relatifs à `data/hc_quetes/`, sauf `pack.mcmeta` :

| Ressource |
| --- |
{table}
'''
 (H/'output/RAPPORT_FUSION_C1.md').write_text(report)
 (H/'output/SHA256SUMS_FUSION_C1.txt').write_text(digest+'  '+archive.name+'\n')
 (H/'README.md').write_text('''# Fusion hc_quetes C1

Fusion des correctifs b22 de Clément avec V2/SI1b/PQ01/QMARK2/PQ02 HF8. Voir `output/RAPPORT_FUSION_C1.md` et `validation_finale.json` pour les fichiers, preuves et limites. Installation : remplacer uniquement le datapack hc_quetes, aucun JAR/core/PNJ à changer.

Les deux archives fournies et la base historique b21 sont identifiées dans `reconcile.py`. Le convertisseur et les tables gelées viennent des sources de la livraison SI1b du 20 octobre 2026, sous l’arborescence `canonical_reference/` fournie avec le script. Ces références et les dépendances tierces sont nécessaires pour reproduire le banc ; les fichiers privés et les dépendances ne sont pas republiés avec ces scripts.

```sh
python3 reconcile.py
python3 validate_merge.py
python3 prepare_runtime.py
python3 bootstrap_runtime.py
python3 run_regression.py
python3 package_report.py
```

Les fichiers livrés restent séparés des fixtures. Le banc complet réutilise les tests PQ02 HF8 depuis le projet adjacent. Les connexions, sélection Nexus, confirmation de caméra et dépendances économiques externes ont les substitutions explicites du rapport. Aucun essai graphique sur la carte Indivis n’est déduit des résultats.
''')
 print(json.dumps({'status':'PASS','zip':archive.name,'sha256':digest,'native':1580,'c1':105,'report':'RAPPORT_FUSION_C1.md'},ensure_ascii=False,indent=2))
if __name__=='__main__':main()
