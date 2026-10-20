# Banque de démonstration technique — français

Huit prises générées le 19 octobre 2026 avec **eSpeak NG 1.51**, voix `fr-fr`,
155 mots/minute. Synthèse formantique : aucune imitation d'acteur, aucun clone
vocal, aucun enregistrement de tiers. Ce matériel sert à tester la synchronisation.
Il ne définit pas le casting, l'accent, les prononciations ni la direction d'acteur.

Textes reproduits exactement depuis les fonctions du core 1.5.7 fourni pour
Indivis. Les droits sur les textes du projet restent à leurs auteurs. La composante
audio générée pour ce prototype est fournie avec le module sous sa licence MIT.
Le moteur eSpeak NG est GPL-3.0-or-later et **n'est pas redistribué dans le pack** :
https://github.com/espeak-ng/espeak-ng ; sa licence ne devient pas automatiquement
celle d'un texte rendu par synthèse.

Chaîne : synthèse WAV PCM → FLAC sans pertes (`demo-masters/` dans les sources) ;
WAV PCM → OGG Vorbis mono, cible 64 kb/s, directement une seule fois. Durées
mesurées par ffprobe et SHA-256 des OGG dans `voice-manifest.json`. Les masters
FLAC sont la copie sans pertes des WAV, pas un décodage des OGG.

Avant un casting humain : conserver les IDs de réplique, fournir WAV/FLAC avec
autorisation de diffusion, changer takeVersion/revision/hash/durée en générant
un nouveau catalogue **et** un nouveau pack. Le JAR ne contient aucune piste.
