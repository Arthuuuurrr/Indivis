# 0.1.1 — 19 octobre 2026

* Les huit voix de démonstration et `sounds.json` sont désormais inclus dans le
  JAR : aucun pack séparé, téléchargement serveur ou activation à effectuer.
* Correction de la distribution à l'origine du silence du test : le mod était
  installé des deux côtés mais aucune banque audio n'était chargée sur le client.
* `/ivvoices test` joue Géraud localement hors dialogue, sans progression de
  quête. Le test s'arrête à la déconnexion, au rechargement des ressources,
  à la désactivation ou au démarrage d'une réplique serveur.
* `/ivvoices` affiche les prises vérifiées, le pont b10, la présence du canal
  serveur, les volumes Minecraft et le dernier diagnostic. Les logs distinguent
  absence d'audio, hash différent, pont/session/écran indisponible et refus de lecture.
* La compilation vérifie l'audio à l'intérieur du JAR remappé livré ; les
  masters restent externes. Le ZIP reste disponible comme export facultatif.
* Quêtes, protocole v1, timer natif, caméras, choix et dépendances inchangés.

# 0.1.0 — 19 octobre 2026

* Premier mod Fabric indépendant Indivis Voices, Minecraft 1.21.11.
* 8 prises techniques françaises : Géraud, introduction Althéon, offre/départ
  Léovic ; catalogue exact, pack facultatif externe et masters FLAC.
* Synchronisation du timer natif par bail court, sans remplacement de fonction,
  action de quête exécutée par Voices, changement de choix ou modification de b10.
* Lecture privée attachée au PNJ, skip avec enveloppe 50 ms, générations
  invalidables, validation des fins et retour texte borné sur échec.
* Options client et volume par source ; caméra, historique et choix conservés.
* Build verrouillé, tests de codec/autorité, vérification des JAR et des assets.
* Prototype à recetter sur le monde de test avant usage ; rencontres ambiantes
  et casting définitif restent à réaliser. Le datapack core reste en **1.5.7**.
