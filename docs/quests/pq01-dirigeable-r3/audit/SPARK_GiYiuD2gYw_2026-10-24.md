# PQ01 — régression de framerate signalée le 24 octobre 2026

**Statut : performances de rendu non validées. HF10 n’est pas présenté comme une résolution de la chute de FPS.**

## Périmètre du fichier

Profil `GiYiuD2gYw.sparkprofile`, analysé localement selon les schémas officiels de Spark. Les métadonnées confirment le journal HF10, le core HF9 et Minecraft Fabric 1.21.11. Le fichier contient uniquement `Server thread`, sans `Render thread` ni mesure FPS. Deux joueurs sont enregistrés dans les deux fenêtres de cette capture ; cela ne permet pas d’écarter le ralentissement également signalé en solo.

Durée : 120.117 s ; 2 400 ticks ; intervalle d’échantillonnage 4 ms.

| Mesure | Valeur | Portée |
| --- | ---: | --- |
| TPS, dernière minute | 19.99975 | Serveur |
| MSPT médian, dernière minute | 9.62 ms | Serveur |
| MSPT 95e centile, dernière minute | 13.52 ms | Serveur |
| MSPT maximum, dernière minute | 370.37 ms | Pic ponctuel ; ne pas masquer ce pic derrière la moyenne |
| Attente `parkNanos` | 76.43 % | Temps total échantillonné du thread serveur |
| `Pq01Bridge.tick` inclusif | 196 ms, soit 0.163 % | Sur l’ensemble de la capture ; pas un temps par tick |
| `Pq01Bridge.tick` / boucle serveur active | 0.73 % | Rapport aux échantillons de `MinecraftServer.method_3748` |

Les valeurs sont issues d’un échantillonnage, pas de compteurs exacts d’appels. Les méthodes parentes et enfants se recouvrent et leurs pourcentages ne doivent pas être additionnés. Le profil ne montre pas une saturation serveur durable pendant cet intervalle ; il n’innocente ni le code client de PQ01, ni les interactions du prologue avec le rendu du modpack.

## Vérification du code actuel

Les sources HF10 ont été relues au commit `3a31456e3d9e9a178ea0bd0a21fa3fe08ec057e3`, puis comparées à la copie HF8 disponible. HF9 a ajouté la lecture manuelle et un hook `ScreenEvents.afterRender` pour l’indication de suite ; HF10 conserve cette classe cliente. Le cycle de création/destruction des acteurs privés et les changements de caméra restent aussi des pistes. Ce relevé serveur ne fournit aucune mesure permettant de départager ces pistes. Aucune causalité vers Nexus n’est affirmée à partir des anciens logs.

La relecture de `DialoguePanel.nom/attente` du b10 fourni montre une affectation de nom et de booléen ; aucun rechargement de modèle n’y est directement déclenché. Cela ne mesure pas le coût total d’un écran, du texte ou du rendu des acteurs.

Les contrôles fonctionnels, réseau et serveur des versions précédentes ne constituent pas une validation du framerate client. Aucun correctif de production ni nouveau JAR n’a été produit sur la seule base de ce profil, pour conserver une comparaison exploitable avec HF10.

## Capture nécessaire pour identifier la régression

Sur le client affecté, dans le chat du jeu :

```mcfunction
/sparkc profiler start --timeout 60 --thread *
```

Pendant la minute : 15 secondes avant le dialogue, 30 secondes dans la scène qui provoque la chute, puis 15 secondes après fermeture, au même endroit et avec les mêmes options graphiques. Conserver HF10 et les réglages actuels pour cette capture. Le lien ou fichier doit contenir `Render thread`. Une capture de comparaison de la dernière version fluide sera utile ensuite si la première pile ne suffit pas à isoler la régression.

La commande `/sparkc` vise le client Fabric ; `/spark` vise le serveur. Source : [documentation officielle Spark](https://spark.lucko.me/docs/Command-Usage).

[Résumé chiffré sans données personnelles](SPARK_GiYiuD2gYw_2026-10-24.json). Aucun profil brut, nom de joueur, UUID, adresse réseau ou configuration privée publié.
