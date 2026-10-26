# Morphologies V2 — travail en cours, non livré

26 octobre 2026. Suite à la demande d’Arthur : conserver les cinq profils PREVIEW1,
ajouter cinq nouveaux profils aux curseurs de taille et carrure exactement centrés.
Haut-Elfe masculin, Elfe des Bois féminin, nouveau Nordique plus sobre.

Les cinq JSON sont préparés, pas encore validés visuellement. Ne pas déployer.
Le module PREVIEW1 et ses cinq profils intégrés n’ont pas été modifiés.
Les modifications Java concernent uniquement le harnais de vérification.

## Blocage signalé pendant la préparation

Arthur a fourni une capture multijoueur où les proportions des trois PNJ importés
sont distinctes, mais où leurs textures sont Steve/Alex. L’import EasyNPC indique
une limitation par les règles serveur, avec mise à jour du propriétaire. Ce seul
message ne prouve pas que la recette d’apparence a été filtrée.

Les essais PREVIEW1 précédents tournaient sur serveur intégré, pas sur client et
serveur dédiés séparés. Il faut identifier la cause avec le journal du client et
la présence/version du module sur les deux côtés avant de déclarer ce défaut corrigé.
Ne pas présenter ces nouveaux presets comme une correction du problème multijoueur.

## Reprise

`python3 tools/collection_v2.py` prépare un pack de données additionnel sans JAR.
`NPC_QA_MODE=collection NPC_DISPLAY=137 python3 run_client.py` contrôle le rendu et
les imports CUSTOM sur une instance isolée avec le module PREVIEW1 inchangé.
Le premier essai a arrêté à la compilation du harnais (accesseur `skin()` corrigé
en `tone()`), avant de lancer Minecraft. Aucun test V2 validé pour le moment.
