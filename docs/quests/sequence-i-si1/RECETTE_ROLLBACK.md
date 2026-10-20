# Recette en jeu et retour arrière

## Recette à effectuer sur une copie

Tous les essais ci-dessous portent le statut **non effectué — serveur Fabric** dans cette livraison.
Les bancs automatisés ne les remplacent pas.

1. Comparer une Sentinelle nouvellement posée avec le garde du Cœur de référence : contrôle sans lettre,
   avertissement, arrangement, reconduite, criminalité, agression, réputation et retour à son ancrage.
   Avec MQ, vérifier que l'autorisation annule bien le timer d'accès en attente, et que les timers judiciaires persistent.
2. Terminer le prologue jusqu'à Résident ; tester 4 puis 5 points cumulés, avant et après une dépense.
   Tester une cité impériale, une cité étrangère, un sol dangereux, un monstre proche et un combat récent.
3. Jouer MQ01 à MQ03 avec toutes les questions répétables. Vérifier le nom du pli, sa remise unique,
   l'inventaire plein, la perte du pli et le repli sans lettre, les entrées du journal, `!/?` et Xaero.
4. Vérifier les six variantes de race et trois attitudes de la Garde ; Orc peut être un cas simulé si non jouable.
   Tester la priorité judiciaire lorsque la criminalité est active.
5. Sortir des Archives après MQ02, revenir par les Sentinelles après Althéon, puis sortir après MQ03.
   Pour ACCESS_ARCHIVES initial 0, 1, 2 et 3 et pour un droit judiciaire, contrôler que seule la source MQ expire.
   Accorder puis révoquer un droit individuel pendant une autorisation MQ pour vérifier la composition des sources.
6. Même compte : A atteint MQ03, B n'a pas MQ01, puis retour A. Contrôler scores, inventaire/lettre,
   droits, journal, marqueurs et Xaero. Cliquer un ancien bouton A depuis B et après retour A : aucun avancement.
7. Deux comptes conversent simultanément auprès du Scribe/Archivaire. Déconnecter au milieu d'un dialogue,
   redémarrer, puis reprendre. Vérifier l'absence de gain doublé et de messager persistant pour un propriétaire absent.
8. Vérifier les anciennes 66 quêtes, leurs objets historiques et macros, livres, marqueurs et pings.
   Tester des objets anciens repris dans l'inventaire et en main secondaire après la migration.
9. MQ04 doit seulement s'ouvrir avec le port de Havre-Fort comme premier objectif. Atteindre le port valide
   cet objectif, sans terminer MQ04 ni déclencher une enquête maritime, une île ou de nouvelles révélations.
10. Profiler avec un puis plusieurs joueurs/PNJ et comparer le MSPT au même serveur sans SI1.
    Les contrôles SI1 sont espacés d'une seconde ; le nettoyage des entités privées s'effectue toutes les cinq secondes.

Commandes de lecture utiles : `/function hc_quetes:admin/sequence_i/status`,
`/scoreboard players get @s hcq_mq01` (puis mq02/mq03/mq04),
`/scoreboard players get @s ACCESS_ARCHIVES`, `/scoreboard players get @s CAP_POINTS_TOTAL`.
`/indivissequence refresh` rafraîchit le journal et les règles de visibilité du personnage actif.
Les essais sur des états forcés s'effectuent exclusivement avec un personnage de test et une sauvegarde.

## Retour arrière

Avant installation, sauvegarder le **dossier complet du serveur arrêté**, incluant monde, profils Nexus,
configurations, datapacks et mods ; sauvegarder les mods clients également. La migration modifie des états
persistants et les identifiants de certains objets. Restaurer seulement les anciens ZIP n'annule pas ces changements.

Sur la copie en jeu, si un retrait des nouvelles entités est souhaité avant restauration :
`/function hc_quetes:admin/sequence_i/rollback_entities`, dans chaque dimension concernée et avec leurs chunks chargés.
La fonction cible le tag `hcm_created_si` et retire la liaison ajoutée à Althéon ; les gardes sources ne sont pas visés.
Le nettoyage des entités chargées n'est pas une restauration de toutes les données du monde.

Arrêter ensuite le serveur. Restaurer ensemble les deux anciens datapacks et les deux anciens JAR,
ainsi que la sauvegarde du monde et des profils Nexus prise avant migration. Restaurer les JAR clients correspondants.
L'installateur conserve les fichiers précédents dans ses dossiers `indivis_si1_backup_*` avec un manifeste,
mais n'effectue aucune sauvegarde ou restauration du monde à votre place.
