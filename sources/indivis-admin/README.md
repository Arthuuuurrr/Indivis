# Indivis — Administration 0.1.0-alpha.3

Mod Fabric **Minecraft 1.21.11**, Java **21+**, Fabric Loader **0.18.0+** et Fabric API. **Candidat compilé, non testé dans Minecraft/Fabric.**

Il ouvre une interface native du jeu pour sélectionner un joueur connecté et gérer le personnage Nexus actif : rang social, criminalité, cinq réputations, autorisations de zones, assistance judiciaire, monnaie et diagnostic.

## Installation et utilisation

Serveur arrêté, remplacer alpha.1 ou alpha.2 par `indivis-admin-0.1.0-alpha.3+1.21.11.jar` dans `mods/`, puis redémarrer. Retirer l’ancien JAR de ce même mod. Se reconnecter ensuite avec le personnage choisi ; le hook de chargement du core remet le verrou initial en état normal. Les clients 1.21.11 reçoivent les dialogues natifs sans devoir installer ce mod. Pour une partie locale avec serveur intégré, installer le JAR dans l'instance Fabric locale.

Le core actuel reste nécessaire pour les actions de gestion. Ce correctif remplace uniquement l’ancien JAR `indivis_admin`. Les fichiers des datapacks, de Nexus, du journal et des autres mods restent inchangés. Son identifiant est `indivis_admin` et sa commande est distincte.

- `/indivisadmin` : liste des joueurs connectés, avec leur personnage Nexus actif.
- `/indivisadmin joueur PseudoMinecraft` : accès direct au dossier de ce joueur connecté.
- Sélectionner une rubrique, choisir une action, puis vérifier la cible et confirmer.
- Fermer avec le bouton ou Échap. Réouvrir la commande pour une nouvelle session.

Permissions : opérateur de niveau 2 ou supérieur, vérifié aussi au moment d'appliquer l'action. Le rang social RP ne donne aucun droit d'administration.

La cible est le **personnage actif**, pas un autre personnage hors ligne du même compte. Si Nexus est installé sans personnage actif, si son profil est verrouillé ou si son API ne répond pas, les modifications sont désactivées. Sans Nexus installé, le mod utilise le profil de compte.

## Correctif alpha.3 du 23 octobre 2026

Le mixin alpha.2 utilisait un contrat de compilation incorrect : `@Mixin` était enregistré dans `RuntimeVisibleAnnotations`, alors que `MixinInfo.readPriority` le lit dans `RuntimeInvisibleAnnotations`. Le contrat des points d’injection `at` est également corrigé en tableau `At[]`, conformément au véritable Sponge Mixin `0.17.4+mixin.0.8.7` fourni par Fabric Loader 0.19.5.

Alpha.3 conserve la logique alpha.2 : seuls l’en-tête et la forme des annotations du mixin, la version et le message de démarrage changent. Aucun catalogue, fonction de sauvegarde, contrôle de cible ou dialogue n’est retiré. Compilation contre la vraie dépendance Mixin et comparaison à la compilation avec contrats corrigés : JAR identique.

Le lecteur réel `Annotations.getInvisible` reproduit le défaut sur alpha.2 et reconnaît alpha.3. Le test vérifie aussi le tableau `at`, le code du handler et ses sélecteurs. Les contrôles de construction refusent désormais ces deux formes incorrectes. Le même défaut affectait les deux mixins PQ01 du journal : utiliser le journal R3-HF1 avec les datapacks R3. Axiom est le premier chargement qui déclenche la préparation globale des mixins dans le log ; son fichier n’est pas corrigé par ce lot.

Ces contrôles ne constituent pas un démarrage Fabric. Les limites et la recette en jeu ci-dessous restent applicables.

## Correctifs alpha.2 du 22 octobre 2026

Le log transmis montre le rejet de `dialog show <UUID> ...` et des notifications `tellraw <UUID> ...` : Minecraft attend une cible de joueurs. Le mod utilise désormais `@s` avec une source dont l’entité est l’administrateur connecté. Le destinataire ne dépend ni du nom RP ni de sa mise en forme.

Le même log affiche le nom Nexus actif pour les deux joueurs mais les classe en lecture seule. Dans alpha.1, cette combinaison signifie que `CAP_PROFILE_LOCK` est positif. L’examen du core SI1 et du Nexus PRE34 fournis montre que chaque sauvegarde appelle `capitale:profiles/before_save_self`, qui laisse le verrou à 1. Le chargement de profil le remet à 0 ; les sauvegardes suivantes peuvent donc rebloquer le panneau.

Un mixin optionnel, ciblé sur la méthode privée `ProfileStateBridge.runHookIfCorePresent` du PRE34 fourni, remplace uniquement l’adresse du hook avant sauvegarde par `indivis_admin:profile/before_save_self`. Ce wrapper exécute le hook original puis restaure exactement le verrou qui existait avant son appel, y compris un verrou déjà positif ou un score absent. Le hook après chargement reste inchangé. La restauration figure dans la même fonction de commandes, après le hook enfant ; elle fonctionne aussi quand l’exécution de fonctions est mise en file dans un contexte de commande.

Les deux objectifs `hcm_adm_savelock` et `hcm_adm_haslock` sont temporaires et reconnus transitoires par la politique Nexus fournie. Aucun verrou n’est retiré globalement ; une vraie transition déjà verrouillée reste protégée. Sans Nexus, le mixin `@Pseudo` n’a aucune cible et le mode compte demeure disponible.

Après installation, **redémarrer et se reconnecter** pour repartir d’un chargement normal de personnage. Aucune commande de remise à zéro globale des profils n’est nécessaire.

## Actions

| Rubrique | Fonctions cataloguées |
|---|---:|
| Rangs sociaux 0 à 100, par paliers de 10 | 11 |
| Criminalité : valeurs et variations natives | 20 |
| Réputations : Couronne, Cœur, Port, Garde des Profondeurs, Profondeurs | 85 |
| Accorder/retirer des accès aux zones | 20 |
| Justice : effacer le dossier, libérer la procédure, temporisations RP | 3 |
| Monnaie : émissions bancaires et conversion ancienne monnaie | 7 |
| Diagnostic numérique | 2 |

Le catalogue contient **148** entrées. Les fonctions réellement chargées sont vérifiées à chaque interaction ; seules celles présentes sont proposées. L'archive originale core 1.5.7 fournie contient 141 de ces fonctions ; la base SI1 restaurée en contient 148. Les sept entrées de monnaie absentes de l'original ne sont pas inventées dans ce mod. Voir `docs/core_audit.json` et `src/main/resources/admin_actions.tsv`.

Les fonctions `capitale:*_self` s'exécutent avec le joueur sélectionné comme `@s`, dans sa dimension et à sa position. Le core conserve ses règles de recalcul des accès, préfixes, justice et inventaire. Le mod ne reproduit pas ces règles en écrivant directement les scores.

Retirer une autorisation individuelle ne neutralise pas les autres sources d'accès maintenues par le core. Effacer le dossier criminel remet aussi à zéro les amendes et divers états judiciaires : les détails sont affichés avant confirmation. Les émissions de monnaie suivent le comportement natif du core en cas d'inventaire plein.

## Contrôle de la cible et traçabilité

Les boutons sont liés à l'administrateur, à sa connexion et à son personnage, puis à la connexion/personnage de la cible. Ils expirent au bout de cinq minutes et ne sont utilisables qu'une fois. Une reconnexion, un changement de personnage, un verrou de profil ou le retrait des droits opérateur empêche l'ancienne confirmation d'agir. Si les valeurs affichées ont changé, une nouvelle confirmation est demandée.

Les appels sont limités au catalogue embarqué ; les commandes reçues du client ne contiennent ni chemin de fonction libre ni score à écrire. Les appels au core se font en fin de tick serveur, en dehors de l'exécution du bouton Brigadier. Les résultats sont relus après le retour de la fonction.

Chaque appel est tracé dans la console serveur et dans `logs/indivis-admin/actions.jsonl` : administrateur, cible, personnage actif, fonction et valeurs avant/après. Le retour de la méthode native ne garantit pas que chaque commande interne du datapack a réussi ; les valeurs affichées et les logs du core restent la référence. Aucun journal n'est envoyé à un service externe.

## Vérifications effectuées

- Banc du contrôleur : **19 assertions** avec doubles d'API, notamment cible distincte, confirmation, rejeu, changement de personnage, reconnexion, expiration, retrait OP et valeur modifiée.
- Banc de l'adaptateur livré : **21 assertions** avec doubles Minecraft/Nexus, notamment scores lus sans création, catalogue `Iterable`, `@s`/dimension/position de la cible, profils verrouillés et liste blanche des fonctions.
- Vérification ASM : **16 classes, 133 méthodes, 0 erreur**, sans résolution des API Minecraft.
- JAR : CRC valide, un mixin de compatibilité Nexus ciblé et aucune classe Minecraft/Fabric/Nexus/Mixin ou classe de test embarquée.
- Alpha.3 : défaut de rétention reproduit sur alpha.2 puis absent dans le lecteur réel Mixin ; forme `At[]` contrôlée ; code et sélecteurs du mixin identiques après normalisation des annotations.

**Non effectués :** démarrage Fabric, décodage/affichage du dialogue dans le jeu, exécution réelle des 148 fonctions, sauvegarde Nexus et test de coexistence sur le serveur complet. Les bancs ne remplacent pas ces vérifications.

Sur une copie du serveur, vérifier l'ouverture OP/non-OP, une modification de rang et de criminalité sur un deuxième joueur, la conservation des valeurs de l'administrateur, les changements de personnage et les accès recalculés. Retirer le JAR désactive l'interface ; cela n'annule pas les modifications administratives déjà appliquées par le core.

## Reconstruction

Un JDK complet **17+** suffit pour ces outils ; le jeu exige Java 21+. La compilation accepte `INDIVIS_MIXIN_CLASSPATH` pointant vers le vrai JAR Sponge Mixin. Sans cette variable, les contrats corrigés et les vérifications de structure sont utilisés. Les accès de production utilisent les noms intermédiaires 1.21.11, les API Fabric présentes à l'exécution et les API Nexus constatées dans le JAR fourni.

```bash
python3 build.py
python3 check.py
```

Le binaire est écrit dans `dist/`. `tools/BuildEntry.java` génère le petit point d'entrée implémentant `ModInitializer` ; les autres classes sont compilées depuis `src/main/java`. Les doubles `tests/java` sont exclusivement des entrées de banc et ne sont jamais livrés dans le mod.

Les contrats natifs sont référencés dans `docs/API.md`. Les traces et limites sont dans `audit/`. La compilation ne fournit pas de validation de liaison avec le jeu.

## Recette du correctif

1. Remplacer alpha.1/alpha.2 par alpha.3 dans les mods serveur, redémarrer puis se reconnecter.
2. `/indivisadmin` doit afficher le panneau ; choisir un autre joueur doit montrer son personnage chargé et les rubriques de gestion.
3. Modifier un rang puis une valeur de criminalité après confirmation : seuls les scores de la cible doivent changer. Vérifier `logs/indivis-admin/actions.jsonl`.
4. Attendre au moins une sauvegarde Nexus automatique (intervalle annoncé : 300 s dans le log fourni), puis rouvrir le panneau : le personnage doit rester modifiable s’il ne change pas de profil. Tester aussi `/nexus save`.
5. Changer de personnage/reconnecter la cible pendant une confirmation : l’ancien bouton doit être refusé. Un véritable `CAP_PROFILE_LOCK` positif doit toujours empêcher l’écriture.
6. Vérifier que les autres dialogues et fonctions de prologue restent accessibles après sauvegarde. Retirer le JAR n’annule pas les changements administratifs déjà appliqués.

Contrôles supplémentaires exécutés : 12 assertions dans un modèle séquentiel réduit utilisant les scripts réels de sauvegarde (ancien blocage reproduit, verrous 0/1/7/absent conservés, deux joueurs et vingt sauvegardes), six assertions sur la vraie classe V2QuestPolicy du Nexus fourni, existence du point d’injection dans le vrai PRE34 et exécution du handler compilé. **Le modèle n’est pas un moteur Minecraft et le mixin n’a pas été appliqué dans Fabric ici.** Aucun démarrage serveur réel revendiqué.

Pour reproduire ces contrôles avec les mêmes entrées :

```bash
python3 regressions.py --core CHEMIN_CORE_SI1.zip --nexus CHEMIN_NEXUS_PRE34_SI1.jar
```
