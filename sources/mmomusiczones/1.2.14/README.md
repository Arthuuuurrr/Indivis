# MMOMusicZones 1.2.14

Version : `1.2.14-indivis-city-playlists-water-refresh`

SHA-256 du JAR compilé :
`acec714940e7d572a85a6406a2f5c1cda89e4186c3a48b04fe27f3f9118b0cee`

## Changements
- 70 pistes VILLE présentes et enregistrées.
- 40 pistes NATURE présentes et enregistrées.
- Affectations jour/nuit finales de la passe du 26 septembre 2026.
- Pointe-Rouge : 31 + 48 jour, 50 nuit.
- Haut-Arsenal et les nouveaux bourgs intégrés.
- Urzak-Tor volontairement non configurée.
- plages + rivières + océans : remplacement complet par NATURE 38/39/40.
- le pool aquatique écrase les contributions superposées (notamment neige sur océans/rivières gelés).
- corruption/donjon et correctifs 1.2.12 conservés.

## Validation
- JAR ZIP : OK.
- réassemblage des 11 parties : byte-identical avec le JAR source.
- SHA-256 après réassemblage : identique.
- CityBiomeMusic chargé avec `java -Xverify:all`.
- 17 villes configurées ; Urzak-Tor retourne null volontairement.
- VILLE : 70/70 fichiers OGG et 70/70 événements.
- NATURE : 40/40 fichiers OGG et 40/40 événements.
- nouvelles pistes OGG lisibles par ffprobe.
