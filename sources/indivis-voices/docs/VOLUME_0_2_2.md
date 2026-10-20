# Volume de lecture — 0.2.2

20 octobre 2026. Arthur signale des voix trop faibles avec Narrator/Voice au
maximum et demande un niveau environ trois fois supérieur, en privilégiant
le volume de lecture plutôt qu'une modification des enregistrements.

## Correction

Les 20 fichiers OGG, les masters, les codes de réplique, les durées et la
révision `prologue-fr-arthur-1` restent identiques à 0.2.1. Il n'y a ni
normalisation des fichiers, ni réencodage, ni remplacement d'interprétation.

Minecraft 1.21.11 limite le volume du son à 1 avant de l'envoyer à OpenAL.
Le réglage 0.2.2 intervient sur la source native, après ce calcul. Il multiplie
son volume par 3 et relève son seul plafond AL_MAX_GAIN à 3. Le multiplicateur
est réservé aux objets de dialogue Voices et à leur aperçu local ; les
autres sources ne sont pas marquées. Les paramètres de position, de distance,
de pitch et d'atténuation restent natifs et inchangés.

Le multiplicateur s'applique aussi aux mises à jour natives du volume et au
fondu existant de 50 ms. Une source est rétablie au profil normal à son arrêt
ou sa fermeture. Toutes les opérations OpenAL passent par la file audio
native ; aucun appel OpenAL n'est exécuté par le thread de rendu du dialogue.
Il n'y a pas de nouvelle lecture de fichier ou de traitement PCM par tick.

## Protection et compatibilité

L'extension `AL_SOFT_gain_clamp_ex` et la limite effective du moteur sont
contrôlées. Le multiplicateur ne dépasse ni 3 ni cette limite. Le mod vérifie
aussi que le limiteur de sortie `ALC_SOFT_output_limiter` est déjà actif sur le
périphérique. Minecraft 1.21.11 le demande explicitement à la création de son
contexte audio. Voices ne change pas le volume du listener, les catégories,
le périphérique ou les paramètres globaux du mélangeur.

Le volume de lecture est, physiquement, un gain lui aussi. La différence ici
est l'absence de modification des échantillons et l'utilisation du limiteur
de sortie existant. Celui-ci peut réduire les pics d'un passage fort : un
facteur de lecture 3 ne promet pas une sensation acoustique exactement triplée.
Il ne répare pas une saturation qui serait déjà présente dans un enregistrement.

Si l'extension ou le limiteur est indisponible, la lecture au volume normal
reste disponible. Le diagnostic apparaît dans `/ivvoices` et le log ; le mod
ne contourne pas cette protection. Une modification du périphérique est prise
en compte lors de l'allocation de la source suivante.

Le serveur 0.2.1 reste compatible : protocole, catalogue et empreintes audio
sont inchangés. L'enchaînement natif corrigé en 0.2.1 reste conservé.

## Vérifications

La vérification a utilisé le vrai client Minecraft 1.21.11 (SHA-1
`ba2df812c2d12e0219c489c4cd9a5e1f0760f5bd`), ses mappings Yarn build.6 et
la bibliothèque OpenAL LWJGL 3.3.3 publiée dans ses métadonnées.

`tools/verify_playback_volume.py` vérifie l'archive Linux par SHA-256
`9030fed928a71eac2fdf4ce1643cc4ec724d8710e3b437bdce750bdc9e982b2a`, puis
utilise cette bibliothèque dans un contexte de rendu loopback privé :

| Contrôle natif | Résultat local |
| --- | --- |
| Version OpenAL | 1.1 ALSOFT 1.23.1 |
| Signal faible, rapport RMS volume 3 / volume 1 | 2.99999997 |
| Signal fort à volume 3, amplitude de crête de sortie | 0.99999994, dans la plage numérique |
| Limiteur de sortie | Actif |
| Source indépendante | Volume et plafond restés à 1 |
| Plafond de la source après arrêt | Rétabli à 1 |

Six tests JUnit couvrent les conditions d'activation, les moteurs limités,
le silence, la proportion du curseur et du fondu, les valeurs invalides et
la borne ×3. La CI recompile le JAR, exécute ces tests avec les précédents,
le banc de dispatcher Fabric et le banc OpenAL. Les résultats effectifs sont
dans l'artefact `indivis-voices-prototype` de la [PR #171](https://github.com/Arthuuuurrr/Indivis/pull/171/checks).

La lecture dans le client complet d'Arthur, son périphérique Windows et le
mélange avec les musiques doivent encore être écoutés en jeu. Le banc natif
n'est ni un relevé FPS/MSPT ni une validation du volume acoustique du casque.

Références primaires : [extension du plafond de gain](https://openal-soft.org/openal-extensions/SOFT_gain_clamp_ex.txt),
[limiteur de sortie](https://openal-soft.org/openal-extensions/SOFT_output_limiter.txt).

## Essai

Remplacer Voices côté client par `indivis-voices-0.2.2+1.21.11.jar`, puis
redémarrer le client. Un serveur déjà en 0.2.1 peut être conservé.
`/ivvoices test P01-GER-004`, puis `/ivvoices`, doivent permettre d'écouter
et de lire `lecture ×3.0 (limiteur de sortie actif, fichiers intacts)` sur un
moteur compatible. Vérifier ensuite un dialogue et un passage volontaire
pendant une voix. `/ivvoices volume 33` ramène environ à l'ancien niveau
si le nouveau réglage est trop élevé.

Aucune modification directe de main ni déploiement en production.
