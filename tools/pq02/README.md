# PQ02 R1 HF4 — Le registre du Port

Implémentation du kit Work v1.2 et du script PQ02 v1.0, sur copies des deux archives de production fournies. Cible : Fabric 1.21.11, Java 21. PQ02 est intégrée au mod de quêtes HF12, côté serveur et clients. Le compagnon séparé n’est plus à installer. 182 des 184 classes historiques de HF12 restent identiques ; deux classes de la passerelle PQ01 sont corrigées pour le rythme de Gautier. HF1 adapte uniquement l’administration aux repères existants : préparation à distance, coordonnées détaillées, cache et refus des doublons ; les règles, dialogues, déplacements et mixins de R1 sont conservés.

Les procédures de joueur et d’administrateur sont dans `INSTALLATION.md` et `COMMANDES_PQ02.md`. L’audit, les limites et les preuves figurent dans `RAPPORT.md` et `reports/`. Dans GitHub, ces documents sont aussi dans `docs/quetes/PQ02_R1/` ; `patches/pq02-r1/` contient les seules ressources modifiées. Les anciens répertoires de datapacks du dépôt ne sont pas substitués à l’archive serveur fournie.

## Reproduire

Placer ce dossier sous `<projet>/work/pq02/` (ou `<projet>/tools/pq02/`) et les originaux sous `<projet>/upload/`, avec les noms attendus par `build.py`. Le fichier `input_manifest.json` identifie les originaux. Garder le script narratif fourni sous `kit/Indivis_PQ02_Script_Consolide_v1_0.md`.

Le banc attend aussi EasyNPC **7.16.0 pour Fabric 1.21.11**, dans `dependencies/`, sous le nom fourni `easy_npc-fabric-1.21.11-7.16.0(1).jar` ou un autre nom `.jar` si c’est la seule dépendance de ce dossier. Les JAR d’origine du journal HF12 et de Dialogue b10 sont lus depuis `upload/`.

```sh
python work/pq02/prepare_runtime.py
python work/pq02/bootstrap_runtime.py
python work/pq02/build.py
python work/pq02/test_static.py
python work/pq02/merge_journal.py
PQ02_INTEGRATED=1 python work/pq02/run_native.py
python work/pq02/test_merged_boot.py
python work/pq02/verify_hf1.py
python work/pq02/run_protocol.py
PQ02_PROTOCOL_LEGACY=1 PQ02_PROTOCOL_JAR=/chemin/vers/HF3.jar python work/pq02/run_protocol.py
python work/pq02/verify_hf4.py
python work/pq02/package_merged.py
```

Adapter le préfixe `work/` en `tools/` si nécessaire. Java 21 et le serveur officiel, Fabric Loader/API et les bibliothèques sont préparés sous `/tmp/indivis_pq02_runtime`. Les fonctions tests et les connexions synthétiques restent dans un monde jetable, jamais dans le core livré. Les journaux et résultats sont dans `reports/` ; les archives finales dans `delivery_merged/`.

Le banc ne lance pas de client graphique et n’utilise pas la carte Indivis. Les comptes/connexions, le sélecteur Nexus et les intégrations économiques externes y sont explicitement simulés. Les règles PRE35, les classes HF12, le serveur Minecraft, les entités/navigation EasyNPC, les fonctions testées, les trackers de visibilité et les sessions/captures b10 sont réels. Les annotations d’environnement client sont retirées seulement de la copie b10 du banc ; les fichiers de production ne contiennent aucune doublure.

## Organisation

`model.py` extrait les citations et construit les 111 nœuds. `build.py` vérifie les empreintes des ZIP originaux, applique les changements limités et compile le compagnon contre les véritables dépendances. `test_static.py` contrôle les différences exactes, les graphes et exécute les règles Java. `run_native.py` construit et démarre le serveur de test. `package_delivery.py` refuse d’assembler une livraison sans résultats PASS et sans correspondance de l’empreinte du mod testé.

`build.py` produit les composants compilés intermédiaires ; `merge_journal.py` les assemble dans HF12 avec un seul identifiant Fabric. `package_delivery.py` conserve la trace du conditionnement compagnon antérieur ; la livraison actuelle utilise `package_merged.py`. Les essais de démarrage conservent le JAR fusionné de production intact et vérifient aussi le refus du compagnon en doublon.

Les quatre mixins de production sont limités à la visibilité des acteurs PQ02, leur physique, leurs cibles de waypoint et la fermeture de leur session Dialogue. PQ01 conserve ses dialogues hors des 12 fichiers du seul choix 6 de Hugues ; les deux classes Gautier adaptées en HF3 restent identiques en HF4. MQ03 reçoit uniquement la priorité manquante du capitaine au quai, identique à celle du capitaine au large.

`hf1_baseline.json` contient les empreintes agrégées de la livraison R1 pour vérifier la conservation de 11 334 entrées de core et du datapack hc_quetes sans exiger de redistribuer ces anciennes archives. `verify_hf1.py` applique aussi le patch à des copies des originaux et compare chaque fichier au ZIP livré. `Pq02SetupBench` exerce les nouvelles commandes sur le vrai serveur, notamment depuis plus de 800 blocs et hors de l’ancienne bande verticale de recherche ; ces coordonnées appartiennent au banc jetable.

HF2 corrige l’entrée par fonction Minecraft : les requêtes de dialogue sont drainées à la fin du tick serveur, hors du contexte des commandes imbriquées. Les deux datapacks restent identiques à HF1. `Pq02EntryBench` passe par le gestionnaire natif de commandes EasyNPC et les fonctions réelles. Le cas d’échec antérieur est conservé dans `reports/hf2_repro*` ; pour le reproduire, utiliser `PQ02_INTEGRATED=1 PQ02_REPRO_ENTRY=1 PQ02_REPRO_JAR=/chemin/absolu/haute-capitale-quetes-HF12-PQ02-R1-HF1-1.21.11.jar python work/pq02/run_native.py`. Le JAR HF1 attendu porte l’empreinte inscrite dans `hf2_baseline.json` ; il n’est pas redistribué comme version installable. `CORRECTIF_PQ02_HF2.md` décrit l’installation sans réinitialisation.


## Historique HF3 — fenêtre de Léovic et rythme de Gautier

Le mécanisme HF3, remplacé par HF4, conservait l’écran de la trame PQ02 et le récupérait une seule fois après un retrait sans fin volontaire. La navigation de Gautier passe à 1,25 et Géraud reprend pendant son retour au poste. `src_pq01/Pq01Bridge.java` ne diffère de la référence HF12 que sur ces deux points ; le constructeur n’en remplace que les classes principale et de navigation. Les packs restent identiques à HF2. Les limites du diagnostic et du banc sont dans `CORRECTIF_PQ02_HF3.md` et `reports/hf3.json`.

`Pq02ScreenBench` exerce l’état réel b10 avec un écran sans rendu et enregistre les sorties C2S dans le banc uniquement. Pour reproduire le chemin ECRAN_FERME de HF2 après retrait volontaire de l’écran par le test : `PQ02_INTEGRATED=1 PQ02_REPRO_SCREEN=1 PQ02_REPRO_JAR=/chemin/absolu/haute-capitale-quetes-HF12-PQ02-R1-HF2-1.21.11.jar python work/pq02/run_native.py`. Les logs utilisateur ne donnent pas l’origine exacte du retrait d’écran. `Pq01GautierBench` utilise de vrais acteurs/navigation EasyNPC et les fonctions natives PQ01, avec deux propriétaires distincts.

## HF4

La réception des pages est précédée d’une confirmation de disponibilité de la session, de l’acteur et de la capture côté client. Le protocole ne crée aucun objectif de scoreboard ni état persistant. `Pq02FocusBench` contrôle le serveur ; `Pq02ProtocolBench` emploie les vraies phases Fabric et le vrai cycle b10 avec des surfaces graphiques doublées. `run_protocol.py` extrait les modules client de Fabric API dans le runtime jetable. Le mode `PQ02_PROTOCOL_LEGACY=1 PQ02_PROTOCOL_JAR=/chemin/HF3.jar` reproduit une page prématurée perdue avec le JAR HF3 exact. Ce test n’identifie pas le retrait d’écran original dans le modpack utilisateur. Lire `CORRECTIF_PQ02_HF4.md`.

`verify_hf4.py` demande le manifeste `hf4_baseline.json` et les sources historiques non modifiées, puis les résultats du banc HF3 défavorable. Ce JAR HF3 historique doit être fourni séparément : il n’est pas redistribué comme version installable. Les 111 blocs client passent par les événements réels ; les menus sans nouvelle réplique y utilisent un texte de fixture. Le banc serveur exerce leurs textes de repli réels. Les deux bancs sont séparés et ne constituent pas un réseau client–serveur graphique complet.
