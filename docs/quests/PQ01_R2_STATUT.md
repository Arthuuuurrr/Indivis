# Statut PQ01

Version actuelle : **journal R3-HF11**, core **R3-HF9**, datapack `hc_quetes` **R3-HF4**.

Le retour du 25 octobre signale Gautier très lent et une scène dégradée, sans problème de framerate sur ce passage. Le serveur et le client chargent HF10 ; le trajet expire après les 300 ticks prévus. Le profil client joint se termine avant la conversation, et ne mesure pas ce trajet. La vitesse réelle du PNJ source n’est pas présente dans les logs.

Un défaut de HF10 est reproduit en monde Fabric : une vitesse de décor positive à 0,02 reste héritée par la copie et l’approche expire. HF11 impose un plancher de base 0,25 uniquement aux copies mobiles Gautier/Perrin ; Gautier navigue avec un coefficient 1,0, Perrin conserve 0,65. Les originaux gardent leurs attributs et positions. Les expirations décrivent position, destination et distance restante dans le journal. Aucun TP ajouté et délai borné conservé.

Le même test passe après correction : approche à neuf blocs en 83 ticks, retour en 75 ; aller 24 blocs en 185 ticks, retour en 157. Les 12 contrôles de marche s’ajoutent aux 48 multijoueur, 1 161 de fonctions/capture/audio, 25 de scène, 26 en monde Fabric, 15 d’activité de Perrin, 44 client et 191 de capture b10 intacte.

Les textes, blocs manuels, questions et conclusion verte, prises et hooks de doublage sont conservés. Le code client de lecture, le filtre personnel, Pq01Actors et les mixins sont identiques à HF10. Core identique à HF9 et hc_quetes identique à HF4 ; gardes et zones sûres du messager préservés. Aucun parcours complet de la séquence I sur la carte utilisateur.

Remplacer seulement le JAR sur le serveur et chaque client. Aucune commande de pose changée, aucun reset ou reliaison. Aucun Nexus inchangé redistribué.

[Marche et preuves HF11](pq01-dirigeable-r3/HOTFIX_MARCHE_R3_HF11.md) · [installation complète](pq01-dirigeable-r3/docs/INSTALLATION_ET_POSE_PNJ.md) · [validation](pq01-dirigeable-r3/audit/hf11_validation.json).

Le cas contrôlé ne prouve pas la vitesse du PNJ de la carte utilisateur. La géométrie du navire reste hors banc ; connexions/profils Nexus synthétiques, caméra/panneau doublés dans la scène couplée. Aucune validation de FPS revendiquée ; [analyse du précédent profil serveur](pq01-dirigeable-r3/audit/SPARK_GiYiuD2gYw_2026-10-24.md) conservée. PR nº177 en brouillon, sans merge ni déploiement. Aucun log brut utilisateur publié.
