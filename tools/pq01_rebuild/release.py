"""Assembler la livraison relisible et les deux seuls datapacks modifiés."""
from pathlib import Path
import csv,json,shutil,zipfile
from build import ROOT,HERE,OUT,INPUTS,FILES,SCORES,SAVED,function_path,load,digest
from model import N,IDS,ROLES,OWNERS,PROMPT

NAME='Indivis_PQ01_R2_Candidat_Texte_2026-10-22'
DEST=ROOT/'deliverables'/NAME
def put(path,text):
 p=DEST/path;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(text);return p
def copy(source,path):
 p=DEST/path;p.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(source,p)
def release():
 validation=json.loads((OUT/'validation.json').read_text());assert validation['status']=='PASS'
 assert (OUT/'nexus_policy.log').read_text().startswith('PASS 40')
 diffs=json.loads((OUT/'diffs.json').read_text())
 if DEST.exists():shutil.rmtree(DEST)
 DEST.mkdir(parents=True)
 for filename in FILES.values():copy(OUT/filename,'installation/datapacks/'+filename)
 put('README.md',f'''# PQ01 — Sous pavillon de la Couronne — R2

Candidat texte du 22 octobre 2026 pour Minecraft Java Fabric 1.21.11.

Cette livraison reconstruit les dialogues approuvés et le mode de relecture après la perte du travail R1. Elle contient **deux datapacks réellement modifiés**. Aucun JAR n'est à remplacer pour ce lot.

**Limite explicite : Gautier intervient en version statique. Son trajet individuel vers Géraud et son retour animé ne sont pas implémentés dans R2.** Aucun PNJ n'est déplacé ou dupliqué. Cette version permet la recette des dialogues, des choix, de l'amarrage et de la conclusion. Elle ne constitue pas la livraison complète de l'animation prioritaire demandée.

Les {len(N)} nœuds contiennent les 145 occurrences de dialogue approuvées du prompt : vraies questions/sous-questions, retours aux menus, fermeture, amnésie présentée avant la conclusion, Avaleiv débloqué par Althéon. Les points de réputation, compétences et prime de fin restent dans le parcours normal. Visiteur est attribué par le magistrat, sans grant à la fin de PQ01.

Installation et commandes : `docs/INSTALLATION_ET_TEST.md`. Limites et preuves : `docs/VALIDATION_ET_LIMITES.md`. Modifications exhaustives : `audit/DIFFS_EXHAUSTIFS.csv`. Sources reproductibles : `sources/work/pq01_rebuild/`.

Aucun test Minecraft/Fabric réel, aucune installation sur le serveur, aucun déploiement ni merge GitHub effectué par cette livraison.
''')
 setup='\n'.join(f'/function capitale:pq01/pose/{role}_{phase}' for phase in ('large','quai') for role in ROLES)
 hook_rows=[]
 hook_rows.append('| Géraud Rivet | `capitale:npc/geraud_rivet/interact_self` | Même routeur ; l’étape décide du dialogue |')
 hook_rows.append('| Althéon Brumeforge | `capitale:quest/spawn_dirigeable_couronne/capitaine_large/interact_self` | Même routeur, MQ03 reste prioritaire |')
 for i,role in enumerate(('gautier','hugues','anselme','perrin'),1):
  hook_rows.append(f'| {ROLES[role]} | `capitale:quest/spawn_dirigeable_couronne/matelots/matelot_0{i}_self` | `capitale:quest/spawn_dirigeable_couronne/matelots/matelot_0{i}_quai_self` |')
 guide=f'''# Installation et recette de PQ01-R2

## Contenu à installer

| Composant remplacé | Fichier fourni |
| --- | --- |
| Core 1.5.7 V2 SI1 | `{FILES['core']}` |
| Quêtes b22 V2 SI1b | `{FILES['quests']}` |

Les deux archives sont des datapacks complets : `pack.mcmeta` se trouve à leur racine. Le dossier `installation` du ZIP de livraison sert à ranger ces fichiers ; ce ZIP extérieur n'est pas lui-même un datapack.

1. Fermer les dialogues et sortir des relectures en cours. Sauvegarder le monde, ses données de commandes et les profils Nexus avant le remplacement.
2. Arrêter le serveur et remplacer les deux versions correspondantes dans le répertoire des datapacks du monde par celles fournies. Une seule version de chaque composant doit être chargée.
3. Conserver les JAR fournis : journal `0.1.0+1.21.11.b7-SEQUENCE-I-SI1b`, Nexus `PRE34-SI1`, Dialogue `b10-DATAPACK-COMMAND-HOTFIX`, EasyNPC et son interface `7.16.0`. Aucun JAR inchangé n'est redistribué dans ce correctif.
4. Redémarrer. Les commandes ci-dessous exigent un administrateur en jeu avec un personnage Nexus sélectionné et chargé.

## PNJ existants et hooks

R2 ne crée, ne remplace, ne déplace et ne modifie aucun modèle/skin/équipement EasyNPC. Dans l'interface EasyNPC, conserver les propriétés existantes et vérifier le nom affiché. Les quatre matelots portent seulement Gautier, Hugues, Anselme et Perrin, sans patronymes. Si un ancien nom est encore visible dans le monde, le modifier dans cette interface ; les noms des conversations sont déjà corrigés dans les fonctions.

Pour un événement d'interaction de type COMMAND, la commande doit être exécutée **comme le joueur qui interagit**, avec le niveau de permission de fonction déjà utilisé sur le serveur. Ne pas ajouter un deuxième événement si le bon hook existe. Les anciennes adresses ci-dessous sont conservées.

| PNJ | Interaction au large | Interaction au quai |
| --- | --- | --- |
{chr(10).join(hook_rows)}

Dans le champ de commande, utiliser par exemple `function capitale:quest/spawn_dirigeable_couronne/matelots/matelot_01_self`. Ne pas remplacer tous les événements NBT d'un PNJ : ses near, animations, travail, équipements et actions autres qu'interaction doivent être conservés.

Near Géraud : conserver le raccordement existant vers `capitale:quest/spawn_dirigeable_couronne/geraud_large_start_self`. Near Althéon : conserver `capitale:quest/spawn_dirigeable_couronne/capitaine_large/near_self`. L'interpellation existante est appelée pour la première introduction ; son nouveau bouton « Je vous écoute. » conduit directement au premier nœud sans second clic PNJ. Une conversation PQ01 active empêche sa propre réouverture en boucle. Les paramètres near et exports NBT du monde n'ont pas été fournis : leur configuration réelle reste à contrôler dans l'interface EasyNPC.

Les commandes `pose` suivantes enregistrent **un point de waypoint à la position de l'administrateur**. Elles ne placent ni n'attachent automatiquement un PNJ. Les lancer une par une près du PNJ correspondant, sur le bon exemplaire du dirigeable, à l'emplacement réellement accessible au joueur. Ne pas exécuter les douze commandes depuis le même endroit.

```mcfunction
{setup}
```

Pour PQ01, les points nécessaires au journal sont Géraud au large, Althéon au large et Géraud au quai. Les autres sont disponibles pour documenter les emplacements individuels. Aucun point n'est inventé à partir des anciennes coordonnées génériques. Tant que le point requis n'est pas enregistré, le waypoint correspondant reste à configurer.

Gautier reste à son poste pour l'interruption de R2. Hugues reste près des soutes/inventaires, Anselme près des instruments et Perrin à son poste de service. Ces poses/occupations se règlent dans EasyNPC sur les PNJ existants, sans modifier le navire. Le dialogue optionnel Hugues/Gautier est désactivé par défaut. Après vérification manuelle qu'ils sont chargés et peuvent se parler à portée dans le décor, l'activer avec `/data modify storage capitale:pq01_config allow_banter set value 1`. Il refuse la présence d'un autre joueur à 16 blocs et utilise un cooldown de 1 200 ticks en ligne ; il n'anime pas leurs déplacements. Pour le désactiver, remettre cette valeur à `0`.

## Rejouer uniquement PQ01

Ces fonctions existent dans le core livré : leurs chemins sont contrôlés à l'assemblage. Elles sauvegardent/restaurent les états propres à PQ01, `QUEST_PROLOGUE`, `CAP_QUETEACTIVE` et la position, avec une clé **compte Minecraft + personnage Nexus**. Elles ne réinitialisent ni les autres quêtes, ni l'inventaire, ni les rangs, ni les réputations.

```mcfunction
/function capitale:pq01/admin/test_debut
```

Cette commande sauvegarde l'état actuel une seule fois et ramène ce personnage au début historique du dirigeable, dans l'Overworld à `764 299 1101`. La fin du parcours de test donne le statut PQ01 terminé pour la recette, mais aucune récompense, aucun rang et aucun lancement de PQ02. L'amarrage utilise le TP historique `93 228 82` ; le `spawnpoint` réel n'est pas changé pendant le test.

Après la recette, fermer le dialogue et attendre la fin d'une éventuelle transition, puis :

```mcfunction
/function capitale:pq01/admin/test_fin
```

Cette commande restaure les scores sauvegardés et la position précédente, puis supprime cette sauvegarde. Après une déconnexion ou un changement de personnage, sélectionner de nouveau le même personnage pour restaurer sa propre sauvegarde. Une deuxième commande `test_debut` refuse d'écraser une sauvegarde existante.

Pour cibler un joueur, remplacer `NomMinecraft` par son pseudo réel :

```mcfunction
/execute as NomMinecraft at @s run function capitale:pq01/admin/test_debut
/execute as NomMinecraft at @s run function capitale:pq01/admin/test_fin
```

Commandes de diagnostic/récupération :

```mcfunction
/function capitale:pq01/admin/status
/function capitale:pq01/admin/fermer_dialogue
/function capitale:pq01/admin/reparer_scene_gautier
```

`reparer_scene_gautier` réarme uniquement l'introduction de Géraud tant que PQ01 est avant le quai. C'est une réparation administrateur, qui autorise volontairement la relecture de cette scène sans toucher aux récompenses ou aux autres quêtes. Elle ne rétablit pas un déplacement animé.

Le bouton de fermeture, la perte du contexte de personnage, un changement de dimension, l'éloignement à plus de 10 blocs du point d'ouverture ou le timeout ferment la session sans conclure la quête. Par défaut, le timeout est de 3 600 ticks ; il est réglable dans `capitale:pq01_config timeout_ticks` entre 200 et 12 000 ticks. Les phrases longues attendent un choix manuel, sans ancienne cascade toutes les 20 ticks.

## Voix futures

R2 ne génère et ne remplace aucun fichier audio. `docs/DIALOGUES_ORDRE_JEU.md` donne les identifiants de réplique ; `config/VOIX_ET_TEMPS.csv` donne les durées de lecture conseillées. Configurer hors dialogue le son d'un nœud, lorsque la ressource existe réellement :

```mcfunction
/data modify storage capitale:pq01_config voices.c1 set value {{sound:"indivis:voix/altheon/pq01_c1",ticks:180}}
```

Cet identifiant de son est un exemple à remplacer par celui de la banque audio réelle. Le fichier audio n'est pas fourni. `ticks` correspond à sa durée en ticks et bloque la progression jusqu'à sa fin ; le joueur peut toujours fermer. Sans son configuré, la lecture reste entièrement manuelle. Le nettoyage arrête uniquement le son configuré de ce nœud, dans la catégorie voix. Les anciennes ressources et les sons de transition du core restent présents. La compatibilité avec un éventuel mod de voix supplémentaire n'est pas certifiée sans ses fichiers.

## Recette en jeu à effectuer

- Nouveau personnage : réveil, interruption statique Gautier lue une fois, retour au menu de Géraud, objectif Althéon.
- Fermer après chaque réplique de Gautier puis rouvrir : reprendre après la réplique déjà affichée. Near répété sans dupliquer la session.
- Quatre matelots : toutes les questions et la fermeture. Vérifier les noms visibles et les hooks du monde.
- Althéon : « Je vous écoute. » fonctionne immédiatement ; amnésie explicite avant le menu. Relire chaque famille A–E et ses sous-questions ; Avaleiv apparaît après la mission d'Althéon, indépendamment de Hugues.
- Questions facultatives : aucun TP. Seule la conclusion complète d'Althéon lance l'amarrage hors champ ; vérifier caméra, sons et position d'arrivée.
- Géraud au quai : cinq questions réutilisables ; seule la dernière conclusion termine PQ01. En parcours normal : Couronne +2, compétences et prime conservées, PQ02 disponible et objectif Léovic. Aucun Visiteur ici ; rang 10 seulement au magistrat, aucun abaissement d'un rang élevé.
- Tester les anciens boutons, double clics, fermeture, éloignement et voix configurée. Ils ne doivent pas déclencher une autre étape ou doubler une récompense.
- Deux joueurs à 10 secondes d'écart, puis deux personnages d'un compte : états, menus et sauvegardes de relecture distincts. Tester reconnexion et changement de personnage pendant chaque introduction.
- Après PQ01 : salut bref et phrases au quai. Sur un personnage avec MQ03 active, Althéon garde le routeur MQ03 prioritaire.
- Faire la recette sur une copie du monde. Aucun de ces scénarios n'a été exécuté ici sur un serveur Minecraft réel.

## Retour arrière

1. Tant que R2 est installé, chaque personnage ayant une relecture active utilise `test_fin` ; fermer les dialogues et laisser finir tout amarrage.
2. Arrêter le serveur et remettre les deux archives d'entrée sauvegardées. Les JAR n'ayant pas changé, aucun remplacement de mod n'est nécessaire.
3. Conserver les données supplémentaires `capitale:pq01_tests`, `capitale:pq01_runtime` et `capitale:pq01_config` pour diagnostic si nécessaire ; elles ne sont plus utilisées par l'ancienne version. Ne pas effectuer de reset global des profils Nexus ou des quêtes.
4. La remise des anciens datapacks ne retire pas les récompenses normales déjà obtenues. Pour annuler aussi des effets de jeu, utiliser la sauvegarde cohérente du monde et des profils prise avant installation.
'''
 put('docs/INSTALLATION_ET_TEST.md',guide)
 dialogue=['# Dialogues de PQ01-R2','', 'Texte extrait du prompt validé. Les libellés « Continuer » et « Fermer » sont des contrôles techniques. La scène Gautier est statique dans R2.','']
 for k,n in N.items():
  dialogue+=['## '+k+' — '+ROLES[n['role']], '']
  dialogue+=[f'> {s}' for s in n['lines']]
  dialogue+=['']+[f'- {c["label"]} → `{c["target"]}`'+(' (après la mission d’Althéon)' if c.get('condition') else '') for c in n['choices']]+['']
 put('docs/DIALOGUES_ORDRE_JEU.md','\n'.join(dialogue)+'\n')
 put('docs/DIRECTION_ACTEUR.md','''# Direction d'acteur — PQ01

## Althéon Brumeforge — cabine, branches C, puis quai

Voix mature, posée et assurée ; diction soignée. C1–C2 : accueil cordial, quitter ses documents. C3–C6 : inquiétude sincère ; laisser une pause après la question sur les souvenirs et après « Je vois... ». La réponse du protagoniste sur l'amnésie n'est ni hésitante ni spéculative. A : faits du sauvetage, sans dramatiser. B : caractère officiel de la livraison, distinction nette entre faits et hypothèses. Avaleiv : précision géopolitique naturelle. Capitale : rassurer sans promettre une ville idéale. Mémoire : chaleur mesurée, aucune promesse médicale. Conclusion : prendre le temps de la dernière phrase protectrice ; le TP vient après, sans ordres de manœuvre. Au quai : officier de nouveau occupé. Garder la voix cohérente avec MQ03.

## Géraud Rivet — scènes A et E

Timbre rugueux et vivant, débit variable. A1–A2 : sollicitude spontanée, avec une autorité pratique. A4 « Matelot ! » : rupture de ton brève et sèche, pas un hurlement. A5 : ferme et précis. A7 : relâcher la tension avec une ironie discrète ; A8 : reprendre le ton ordinaire d'un homme qui travaille. Questions facultatives : humour sec et expérience, aucune récitation uniforme. E1–E2 : accueillir au Port. Réponses E : indications concrètes et protectrices. EX3 : une hésitation avant « Prenez soin de vous », voix moins brusque. EX4 : retrouver le travail et l'équipage avec énergie.

## Gautier — interruption, questions B et quai

Diction appliquée et ferme sur son service. Interruption A3 : empressement d'un matelot qui veut bien faire, départ de phrase assuré puis suspension avant d'être coupé. A6 : réponse rapide, embarrassée, discipline immédiate. Dans ses questions : défendre ses compétences et son ambition ; ne pas le rendre naïf ni timide en permanence. Au quai : satisfaction de pouvoir annoncer une procédure achevée. Dans R2, ne pas prendre l'absence d'animation comme une nouvelle intention de personnage : c'est une limite technique du candidat texte.

## Hugues — soutes, questions B et quai

Vétéran débrouillard. Accueil : lassitude devant les caisses puis retour courtois au passager. Administration : ironie populaire ; livraison à Avaleiv : pragmatisme, pas d'insubordination. Sauvetage : passer à une gravité discrète avant de relever que le joueur se tient debout. Ambiance Gautier/Hugues : courte joute, silences secs de « Oui. » et « Pas encore. », sans farce caricaturale. Au quai : le travail recommence, sourire complice envers celui qui peut partir.

## Anselme — instruments, questions B et quai

Distrait à l'accueil, concentré sur son métier. Expliquer les vents, la sustentation, la propulsion et la direction avec passion ; accélérer légèrement sans rendre les mots illisibles. La correction « Notre bâtiment » exprime son orgueil technique, pas du mépris. Ne pas traiter le dirigeable comme une rareté merveilleuse. Au quai : satisfaction technique puis retenue lorsqu'il s'aperçoit qu'il commence à trop expliquer.

## Perrin — pont, questions B et quai

Voix expressive et souriante, débit spontané. Curiosité et indiscrétion amicales, jamais de cruauté envers l'amnésie. Accueil : intérêt immédiat pour le rescapé. Capitale : parler avec plaisir des gens et des quais. Enregistrement : dédramatiser sans dire que la formalité est évitable. Fin : encourager la découverte de la ville, sans proposer une autre histoire.

## Prise et minutage

Les six personnages ont des intentions distinctes ; le pitch seul ne remplace pas le jeu. Enregistrer une réplique par nœud, conserver les pauses expressives dans sa durée et reporter la durée réelle dans les hooks `voices.<identifiant>`. La progression est manuelle. Aucun audio de synthèse n'est produit dans ce lot.
''')
 (DEST/'config').mkdir(parents=True,exist_ok=True)
 with (DEST/'config/VOIX_ET_TEMPS.csv').open('w',newline='') as file:
  writer=csv.writer(file);writer.writerow(['node','role','mots','lecture_conseillee_ticks','son_configure_par_defaut','blocage_audio_ticks_par_defaut'])
  for k,n in N.items():
   words=sum(len(s.split()) for s in n['lines']);writer.writerow([k,ROLES[n['role']],words,max(40,words*8+20) if words else 0,'',0])
 put('docs/IDENTIFIANTS.md','''# Identifiants et compatibilité

Identifiant public inchangé : `pq_01_sous_pavillon_couronne`. Autorité principale inchangée : `hcq_pq01`. Le registre CSV transmis décrivait encore une migration à faire, alors que les archives reçues utilisent déjà les identifiants v2 ; aucune nouvelle migration globale n'est exécutée.

Étapes : 0 avant contact ; 10 Géraud/cabine ; 20 entretien Althéon ; 40 arrivée au quai ; 100 terminé. La fiche du journal marque désormais le capitaine terminé à 40 et le retour au quai comme remise disponible entre 40 et 99. Les 69 autres fiches sont inchangées.

Nouveaux états persistants, reconnus par la politique Nexus SI1 : `hch_pq1node` (reprise de lecture), `hcf_pq1_a` (intro Géraud), `hcf_pq1_c` (intro Althéon), `hcf_pq1_av` (mention Avaleiv par Althéon), `hcf_pq1_gain` (gain normal consommé), `hcf_pq1_test` (relecture), `hcf_pq1_ver` (initialisation locale), `hcd_pq1_banter` (cooldown). Les préfixes `hcm_pq1_*` sont transitoires ; ils ne portent aucune progression durable.

Propriétaires : Géraud 91, Althéon 92, Gautier 93, Hugues 94, Anselme 95, Perrin 96. Le propriétaire reste celui du PNJ qui ouvre l'échange, même lorsque Gautier interrompt Géraud. `QuestChoix` reçoit un nouveau bloc de valeurs par affichage, avec un nonce stocké entre 1 000 000 000 et 1 999 999 900 ; indices 1–7 pour les choix et 15 pour fermer. Les anciens boutons ne correspondent plus au jeton actif. Les mêmes scores ne sont pas utilisés pour deux joueurs.

Les séquences héritées 101–103, 201–251 et 401–403 sont redirigées vers les nouveaux routeurs et ne gardent aucune ancienne attribution automatique. Les séquences 301–303 restent l'amarrage reçu. Les autres séquences sont inchangées. Le relais fixe PQ01 de `QuestChoix=5` est remplacé par le bouton neuf du nœud `c0` ; les relais Léovic/Aurèle/Roch restent en place.

Les personnages existants ne subissent aucun reset de quête/rang : première initialisation locale, un état >=10 est considéré comme ayant déjà rencontré Géraud ; >=40 comme ayant déjà terminé l'introduction d'Althéon ; >=100 comme ayant déjà consommé le gain. Pour rejouer les nouveaux dialogues, utiliser le mode de test avec sauvegarde. La continuité historique de chaque ancien enregistrement doit encore être contrôlée en jeu.

Stockages : `capitale:pq01_runtime` (nonce et ancres par compte, avec vérification du personnage), `capitale:pq01_tests` (sauvegardes par couple compte/personnage), `capitale:pq01_config` (timeout, voix, ambiance). Points de journal : `hc_quetes:lieux pq01_<role>_<large|quai>`. Tag éphémère d'ambiance : `hcm_pq1_viewer`, ajouté et retiré dans la même fonction. Aucun tag durable n'est ajouté à un PNJ.
''')
 validation_text=f'''# Validation et limites — PQ01-R2

Résultats propres à cette reconstruction ; aucun résultat du candidat R1 perdu n'est réutilisé.

- {validation['test_cases']} cas de contrôle statique réussis sur les archives produites : JSON, dépendances internes, périmètre des modifications, répliques exactes, vrais choix, passages obligatoires de l'amnésie, fins explicites, conservation du corps des récompenses, magistrat inchangé, sauvegardes par compte/personnage, gardes de contexte/jeton/voix/timeout, priorité MQ03 et autres fiches du journal.
- {validation['json_files_checked']} fichiers JSON/pack.mcmeta chargés par le validateur.
- {len(N)} nœuds ; 145 occurrences de dialogues/questions comparées au prompt.
- 0 référence interne obligatoire manquante dans core + quêtes. Les macro-appels dynamiques ne sont pas assimilés à des chemins littéraux ; les tags `required:false` sont traités comme facultatifs. Les références externes CapSkills déjà présentes restent inchangées ; leur liste est dans `audit/validation.json`. Cela ne certifie pas l'installation d'un autre datapack sur le serveur.
- 40 assertions exécutées sur la classe V2QuestPolicy réellement extraite du Nexus fourni : les nouveaux états `hch_`/`hcf_`/`hcd_` sont persistants, les `hcm_` transitoires. Ce banc teste une politique Java, pas une sauvegarde/restauration de personnage en jeu.
- Aucun fichier retiré des deux datapacks. Gardes, magistrat, Léovic, Aurèle, Roch et autres quêtes préservés par comparaison des contenus. Aucun JAR modifié ; aucun ancien JAR redistribué comme correctif.
- SHA-256 des entrées et sorties : `audit/diffs.json` et `SHA256SUMS`. Inventaire de chaque entrée changée/ajoutée : `audit/DIFFS_EXHAUSTIFS.csv`.

## Ce qui n'est pas validé

Pas de serveur Minecraft/Fabric lancé ici et pas de monde/NBT des six PNJ fourni. Le contrôle n'est pas un chargement Brigadier/Minecraft, un test réseau/caméra, une recette à deux joueurs ou une restauration réelle des profils Nexus. Les commandes publiques sont contrôlées comme chemins existants dans le ZIP, pas proclamées jouées en jeu. La compatibilité avec un mod/banque de voix supplémentaire non fourni reste à vérifier.

**Mouvement : absent de R2.** L'architecture de visibilité SI1b sait masquer les entités `hcm_private` aux autres personnages, mais cette visibilité ne résout pas à elle seule les collisions et l'IA d'un double EasyNPC mobile. Faute de reconstruire et tester le correctif de collision/navigation et sans exports du monde, ce lot laisse les PNJ existants en place et utilise une interruption textuelle personnelle. Il n'y a pas de chemin fictif, de téléportation d'un Gautier à travers un mur ou de clone permanent. L'animation individuelle demandée reste à développer et à recetter ; elle n'est pas présentée comme achevée.

La recette complète à effectuer est dans `INSTALLATION_ET_TEST.md`. Les conclusions de sécurité sont limitées aux contrôles ci-dessus ; aucun « zéro bug » n'est garanti.

## Références de format

Les hooks et structures existants proviennent des archives 1.21.11 fournies. Les notes officielles Mojang décrivent `click_event` et son champ `command` depuis 1.21.5 : https://www.minecraft.net/en-us/article/minecraft-java-edition-1-21-5 . Les macros et `return run` sont décrits ici : https://www.minecraft.net/en-us/article/minecraft-snapshot-23w31a . Ces références de syntaxe ne constituent pas un essai du candidat dans la version 1.21.11.
'''
 put('docs/VALIDATION_ET_LIMITES.md',validation_text)
 for n in ('diffs.json','DIFFS_EXHAUSTIFS.csv','validation.json','validation.log','nexus_policy.log','dialogues.json'):copy(OUT/n,'audit/'+n)
 for p in HERE.rglob('*'):
  if p.is_file() and (p.name in ('model.py','build.py','validate.py','release.py') or p.suffix=='.java'):
   copy(p,'sources/work/pq01_rebuild/'+str(p.relative_to(HERE)))
 copy(PROMPT,'sources/upload/PROMPT_WORK_PQ01_DIRIGEABLE.md')
 put('sources/README.md','''# Reconstruire

Python 3 et Java 17+ pour le banc de politique Nexus. Remettre dans `sources/upload/` les deux archives d'entrée, avec leurs noms et SHA-256 exacts listés dans `audit/diffs.json`. Le prompt validé est inclus. Les JAR/données du serveur ne sont pas remplacés par des copies anciennes.

Depuis `sources/` :

```sh
python3 work/pq01_rebuild/build.py
python3 work/pq01_rebuild/validate.py
```

Le banc `tests/NexusPolicyBench.java` utilise la classe `net/tompsen/nexuscharacters/V2QuestPolicy.class` extraite sans modification de `NexusCharacters-Indivis-1.21.11-PRE34-SI1.jar` (SHA-256 454bd4206b50737bf386d65eb09f4f0e3ad1294a1fd4e6df426f9fe205cb31d0), placée avec son chemin de package dans `nexus_classes`. Compiler le banc avec ce dossier dans le classpath, puis l'exécuter. Aucun stub Minecraft ni classe Nexus modifiée n'est embarqué dans les datapacks.
''')
 put('SHA256SUMS',''.join(v['output_sha256']+'  installation/datapacks/'+v['output']+'\n' for v in diffs.values()))
 # Vérifier chaque commande /function publique du guide, et chaque hook EasyNPC cité.
 packs=[load(OUT/f) for f in FILES.values()]
 defined={p.split('/')[1]+':'+p.split('/function/',1)[1][:-11] for pack in packs for p in pack if '/function/' in p and p.endswith('.mcfunction')}
 import re
 public=set(re.findall(r'(?:/function |function |`)(capitale:(?:pq01/|quest/|npc/)[a-z0-9_./-]+)',guide))
 assert public and all(ref in defined for ref in public),public-defined
 archive=ROOT/'deliverables'/(NAME+'.zip')
 with zipfile.ZipFile(archive,'w',zipfile.ZIP_DEFLATED,compresslevel=9) as z:
  for p in sorted(DEST.rglob('*')):
   if p.is_file():z.write(p,NAME+'/'+str(p.relative_to(DEST)))
 with zipfile.ZipFile(archive) as z:assert z.testzip() is None
 (OUT/'release_metadata.json').write_text(json.dumps({'path':str(archive),'bytes':archive.stat().st_size,'sha256':digest(archive.read_bytes()),'public_function_paths_checked':len(public)},indent=2)+'\n')
 print((OUT/'release_metadata.json').read_text())
if __name__=='__main__':release()
