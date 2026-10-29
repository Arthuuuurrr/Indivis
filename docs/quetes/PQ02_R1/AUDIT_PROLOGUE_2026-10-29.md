# Audit du prologue Indivis — 29 octobre 2026

## Résultat

**Aucun nouveau blocage reproduit dans les parcours couverts par les bancs.** La vérification est étendue à PQ01 et aux raccords de PQ03–PQ05. Elle confirme notamment le service de Perrin sur un terrain de test, les questions encore accessibles après une réponse, la récompense unique de PQ01 et l’entrée de PQ02 à son état canonique **30**.

**Aucun fichier de jeu modifié.** Conserver le mod de quêtes **HF12-PQ02-R1-HF4** déjà livré, le core **PQ02 R1 HF1** et `hc_quetes` **PQ02 R1**. Aucune commande, liaison, pose, réinitialisation ou nouvelle installation n’est requise par cet audit. Gautier reste dans la version HF3 conservée par HF4.

Le résultat n’est pas une certification de tout le prologue en jeu. La fermeture de Léovic sur le client réel et les escortes historiques partagées restent les deux réserves principales.

## Vérifications nouvelles

| Vérification | Résultat | Portée |
| --- | --- | --- |
| Références et cohérence des ressources | **388 contrôles PASS** | 1 291 fonctions de PQ01 à PQ05 ; aucune référence littérale interne manquante. Cohérence des blocs, choix, questions et clôture verte du capitaine. |
| Protocole client PQ01 | **584 assertions PASS** | Les 94 blocs affichables et l’alias immédiat `c0 → c1`. Vrais événements de messages Fabric et cycle natif Dialogue b10. |
| Serveur PQ01 et raccords de progression | **1 159 assertions PASS** | 94 blocs, 317 choix directs exécutés par `trigger QuestChoix` et le dispatcher de production ; proximité de Géraud, Perrin, récompense, offres suivantes et normalisation. |
| Chargement des fonctions | **PASS** | Les 2 312 fonctions du pack de test sont chargées par le serveur natif ; aucun échec de fonction ni erreur de mixin dans le journal final. |
| Compatibilité de la persistance Nexus OPT3 | **Comparaison binaire conforme** | Les classes de politique des scores et de persistance sont identiques à OPT1, référence des essais précédents. Les changements de classes observés entre ces deux JAR concernent la géométrie. |

Le banc client contrôle les noms exacts des six interlocuteurs, les répliques et choix, l’absence d’avancement par minuteur, le refus de sauter pendant la frappe ou avant la dernière page, le jeton de continuation et Échap. Il utilise les ressources effectives du core livré. Les deux menus sans nouvelle réplique (`c_menu` et `e_menu`) sont traités comme des menus de questions, pas comme des répliques manquantes.

Le banc serveur ouvre réellement les sessions et attend les ticks du moteur. Il exécute les 317 branches directes, y compris les variantes de questions après une réponse, avec la connaissance facultative d’Avaleiv activée. Les transitions particulières de la scène de Gautier restent couvertes par les 15 assertions HF4 antérieures sur le même binaire. L’entrée du banter retirée du choix 6 de Hugues reste absente.

## Points particulièrement contrôlés

- **Géraud :** aucune interpellation lorsque le joueur reste immobile après le délai de spawn ; déclenchement après déplacement et approche à trois blocs. Refus d’un jeton périmé sans progression.
- **Gautier :** aucune modification de la version améliorée. Les tests HF4 existants couvrent marche à 1,25, maintien au point de réplique, reprise de Géraud pendant le retour, retour sans téléportation et isolation des acteurs.
- **Perrin :** deux copies EasyNPC distinctes marchent réellement de A à B puis reviennent à A. La copie d’un joueur est invisible à l’autre. Le PNJ public conserve sa position et sa vitesse de base nulle. Nettoyer une scène conserve l’acteur de l’autre joueur.
- **Capitaine, Hugues et Anselme :** leurs blocs et choix directs passent par les fonctions réelles. Le choix du capitaine qui lance l’amarrage reste vert et explicite. Seul le choix 6 demandé de Hugues reste neutralisé.
- **PQ01 → PQ02 :** la conclusion normale donne Couronne +2 une seule fois, termine PQ01 et ouvre PQ02 à **30**. Le suffixe historique `migration/write/pq02/v10` écrit bien 30 ; il ne faut pas remplacer cet état par 10.
- **PQ03 → PQ05 :** les offres d’Aurèle, Roch et du magistrat des Profondeurs deviennent disponibles après les prérequis attendus ; un résident déjà inscrit ne reçoit pas une nouvelle offre d’inscription. La normalisation reconnaît les cinq quêtes terminées.

## PQ02 et quête principale : résultats conservés

Le JAR est exactement celui des essais HF4 : **2 425 assertions serveur**, **566 assertions de protocole client**, les **111 blocs PQ02**, la confirmation de disponibilité des caméras, les reprises, l’isolation des escortes, les gains conditionnels, le don unique et le retour auprès d’Althéon pendant MQ03 puis aux Archives. Ces essais précédents n’ont pas été comptés une seconde fois dans les nouveaux totaux.

Les règles, dialogues, déplacements, récompenses, marqueurs et données de personnage livrés ne sont pas réécrits. La comparaison des archives confirme qu’aucun nouveau pack ou mod n’a été produit par l’audit.

## Réserves et problèmes non verrouillés

### Léovic sur le client réel

Les derniers logs fournis décrivaient une session privée terminée par `ECRAN_FERME`, sans identifier l’appel qui retire l’écran. HF4 passe ses essais de disponibilité, de capture et de protocole. **L’audit ne démontre pas que le symptôme a disparu sur le modpack et la carte réels.** Il faut constater le résultat du clic normal avec HF4 sur serveur et clients. En cas de reproduction, ses traces bornées de demande, confirmation, page et retrait d’écran permettront de poursuivre le diagnostic sans modifier les points du trajet.

### Escortes historiques d’Aurèle, Roch et Colin

Leur mécanisme reste différent de celui des scènes privées PQ01/PQ02 : les fonctions historiques ciblent des armor stands de guidage partagés, notamment `guide_aurele_coeur`, `guide_roch_profondeurs` et `guide_colin_profondeurs`. Les fonctions de démarrage réinitialisent l’état de ces guides ; `CAP_GUIDE_LOCK` est un verrou du joueur.

**Le caractère partagé est confirmé par le code. Un conflit précis entre deux joueurs n’a pas été reproduit sur la carte.** La validation de deux acceptations simultanées et d’une reprise au milieu d’un trajet reste nécessaire avant de considérer ces escortes comme isolées. Elles n’ont pas été remplacées ni munies d’un verrou simplifié dans le cadre de cette vérification.

### Limites des bancs

Les surfaces graphiques, le monde client, ses entités et la sortie C2S sont doublés dans le banc de protocole. Les connexions, la réception des chunks et la sélection Nexus sont synthétiques côté serveur. Le sélecteur graphique et la sérialisation complète des profils n’ont pas été exercés.

La carte Indivis, ses obstacles, les cadrages rendus, les FPS et les doublages n’ont pas été lancés. Pour PQ03–PQ05, l’intégrité des fonctions, les offres et la normalisation sont vérifiées ; leurs escortes complètes et le paiement de résidence ne sont pas déclarés testés en jeu. Les appels externes de monnaie/CapSkills/bonus conservent les substitutions documentées du monde de test, jamais incluses dans les fichiers livrés.

Deux erreurs du nouveau banc ont été corrigées avant le dernier passage réussi : attente erronée de PQ02=10 au lieu de 30, puis lecture du résultat d’une fonction dans un objectif de fixture non déclaré. Ces corrections concernent uniquement les tests.

## Empreintes des fichiers de jeu inchangés

| Fichier | SHA-256 |
| --- | --- |
| JAR de quêtes HF12-PQ02-R1-HF4 | `a401eec8b5fbe1396c651715cd457a01ff86cb531c5bcc5686352b44a9f19285` |
| Core PQ02 R1 HF1 | `592ad598e9cb8164782adbfa873e325172fdb2c81458f3252de5e75f28851a9d` |
| hc_quetes PQ02 R1 | `154cbdf0105296f5a94ccf6d54d7f9292c8f6fde05f9419cb7ff62225cec0fde` |

## Reproduire et consulter les preuves

Avec les entrées originales et le runtime Java/Fabric décrits dans `README.md` :

```sh
python work/pq02/audit_prologue.py
PQ01_PROTOCOL_AUDIT=1 python work/pq02/run_protocol.py
PROLOGUE_AUDIT=1 PROLOGUE_AUDIT_ONLY=1 PQ02_INTEGRATED=1 python work/pq02/run_native.py
python work/pq02/verify_prologue_audit.py
```

Adapter `work/pq02` en `tools/pq02` pour la disposition du dépôt. Sans `PROLOGUE_AUDIT_ONLY`, le banc relance d’abord le parcours serveur PQ02/Gautier existant, puis l’audit PQ01 ; les résultats numériques diffèrent alors du lot isolé ci-dessus.

`reports/prologue_audit.json` donne la synthèse, les empreintes et les limites. Les preuves détaillées sont `prologue_audit_static.json`, `prologue_audit_protocol.txt`, `prologue_audit_protocol_fixture.json`, `prologue_audit_native.txt`, `prologue_audit_native_fixture.json`, `prologue_audit_native_server.log` et `prologue_nexus_compatibility.json`. `hf4.json` identifie les essais PQ02 antérieurs sur le même JAR.
