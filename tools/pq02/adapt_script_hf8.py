"""Adaptation limitée du script fourni à la décision utilisateur du 01/11/2026.

La source v1.0 reste intacte. Les 691 positions de lignes sont conservées pour
les références du graphe et le rapport recense chaque ligne adaptée.
"""
from pathlib import Path
import hashlib,json
H=Path(__file__).resolve().parent
source=H/'kit/Indivis_PQ02_Script_Consolide_v1_0.md'
lines=source.read_text().splitlines()
changes={
4:'**Version de travail : 1.1 — 1er novembre 2026 ; v1.0 fournie + décision utilisateur sur le versement systématique.**',
5:'**Statut :** adaptation éditoriale limitée au don et à ses réponses ; les essais de banc ne constituent pas un essai graphique sur la carte du serveur.',
13:'## Journal des décisions v1.1 (la décision utilisateur du 01/11/2026 prévaut sur le don v1.0)',
18:'4. **Argent** : 50 Martins d’Or donnés personnellement par Léovic, systématiquement pendant F06 et une seule fois par personnage. Aucun refus, report ou retour pour récupérer la bourse. La réplique sèche D conserve son −1 ; C conserve son +1.',
31:'**Réputation et dons :** l’ancien bonus automatique de +5 est supprimé. A01-A, T04b-A et F06-C donnent chacun +1 (maximum +3) ; F06-D donne −1 (minimum −1). Les réponses neutres, prudentes ou embarrassées valent 0. Léovic verse personnellement 50 Martins d’Or pendant F06, avant la réponse, une seule fois par personnage. Ce don n’est pas une allocation officielle et aucune récupération au retour n’est requise.',
493:'**[Mise en scène / versement]** Il présente puis remet une petite bourse. Les 50 Martins sont versés à l’entrée dans F06, avant le choix A/B/C/D, une seule fois par personnage Nexus. Pas de cinématique longue ni de geste imposé sans animation cohérente.',
502:'- **B — Embarras :** « C’est généreux, mais je ne sais pas quoi dire. » → **F06b** ; réputation **0**, impression **0**. **[Adaptation v1.1]**',
504:'- **D — Réponse sèche :** « Je ne demandais pas votre charité. » → **F06d** ; **Garde du Port −1, impression Léovic −1** ; les 50 Martins restent acquis. **[Formulation utilisateur]**',
508:'**[Enchaînement v1.1]** Après F06a, passer à **F07**, sans choix supplémentaire d’acceptation ou de report.',
510:'**F06b — Léovic :** « Vous n’avez pas à me dire quoi que ce soit. C’est un coup de main, de bon cœur. » **[Adaptation v1.1]**',
512:'**[Enchaînement v1.1]** Après F06b, passer à **F07**, sans choix supplémentaire d’acceptation ou de refus.',
514:'**F06c — Léovic :** « Ce n\'est pas grand-chose. Faites-en bon usage. » → **F07**.',
518:'**Léovic**, plus réservé : « Enfin, vous êtes libre d\'en penser ce que vous voulez. » → **F07**. Les 50 Martins déjà versés restent acquis ; son attitude est mémorisée dans le bilan global, sans transformer Léovic en adversaire.',
520:'**F06 — [Système]** Donner systématiquement les 50 Martins à l’entrée dans la scène, selon le mécanisme monétaire du core ; verrouiller `CAP_LEOVIC_GIFT` pour le personnage Nexus actif. Ne jamais appeler un cadeau au retour. Le versement ne modifie aucune réputation : C donne +1/+1 et D −1/−1 uniquement lors du choix initial.',
522:'**[Compatibilité F06-REPORTER]** Cette ancienne branche est retirée du dialogue. Une sauvegarde en cours qui y était arrêtée reprend à F07, avec versement unique si nécessaire et sans rejouer sa décision.',
524:'**[Système — verrouillage]** La prise de position A/B/C/D et ses effets ne sont jamais rejoués. Fermer, reprendre ou recharger F06/F07 ne verse pas une seconde bourse. Une quête déjà terminée avant cette version ne reçoit pas de rattrapage automatique à la connexion ; son historique de réputation et de récompenses est conservé.',
550:'**Déclencheur :** le joueur peut revenir au poste habituel près des tentes pour une salutation fondée sur l’impression générale et des questions pratiques. Ce retour est facultatif et ne conditionne aucune récompense de PQ02 ; il ne propose aucune bourse.',
563:'### P02-POST-DON — Ancienne offre reportée supprimée (compatibilité uniquement)',
565:'**[Décision utilisateur v1.1]** Aucun menu de récupération n’est présenté après PQ02. Les anciennes sauvegardes pointant vers POST-DON sont redirigées vers le menu pratique.',
567:'**[Menu]** Les questions pratiques demeurent ; aucune option de don n’est ajoutée.',
569:'**[Compatibilité]** L’ancien identifiant POST-DON reste réservé pour les sauvegardes, sans réplique ni versement.',
571:'**[Aucune réponse d’acceptation ou de report après la quête.]**',
573:'- Aucun versement tardif : les 50 Martins ont été reçus pendant F06.',
574:'- Aucun recalcul tardif de réputation ou d’impression.',
576:'Les retrouvailles ne rouvrent ni F06/F07 ni les récompenses. La mémoire globale de Léovic reste déterminée par les quatre décisions initiales.',
595:'Les autres réponses, dont l’embarras B, n’apportent aucun point. C et D s’excluent mutuellement. Le versement des 50 Martins est systématique et ne constitue pas une décision récompensée.',
603:'- **Bourse personnelle** : `CAP_LEOVIC_GIFT` marque le don déjà versé pendant F06. L’ancien état d’offre reste compatible en sauvegarde mais n’ouvre aucun menu tardif ; les 50 Martins ne sont jamais versés deux fois.',
648:'- Sauvegarder par personnage les quatre décisions (A01, T04b, M04, F06), les sujets découverts, le point de passage, l’étape de l’entretien, le versement unique des 50 Martins et la fin de PQ02.',
650:'- L’ancien bonus +5 est retiré à sa source et jamais compensé ailleurs. Les 50 Martins sont versés systématiquement pendant F06, avant la réponse, indépendamment des effets de C ou D.',
666:'- [ ] Les 50 Martins sont versés systématiquement pendant F06 ; l’embarras est neutre et la réponse sèche D coûte −1 sans annuler le don.',
677:'- [ ] Les 50 Martins ne sont jamais versés deux fois, y compris après fermeture, reconnexion ou reprise d’une ancienne sauvegarde en cours ; aucun menu de récupération au retour.',
685:'3. **Don : décision utilisateur du 01/11/2026.** Versement systématique pendant la scène, réponses A/B neutres, C +1/+1, D −1/−1 ; aucun refus/report ni retour pour obtenir l’argent.',
691:'**Fin — PQ02 v1.1 : source v1.0 conservée, adaptation limitée au versement systématique demandé le 01/11/2026.**',
}
assert len(lines)==691
audit=[]
for number,text in changes.items():
 audit.append({'line':number,'before':lines[number-1],'after':text})
 lines[number-1]=text
dest=H/'kit/Indivis_PQ02_Script_Consolide_v1_1_Don_Systematique.md'
dest.write_text('\n'.join(lines)+'\n')
(H/'reports/editorial_hf8.json').write_text(json.dumps({'source_sha256':hashlib.sha256(source.read_bytes()).hexdigest(),'output_sha256':hashlib.sha256(dest.read_bytes()).hexdigest(),'authority':'Précision explicite utilisateur : versement systématique, réponse sèche −1, sans récupération au retour.','line_count':len(lines),'changes':audit},ensure_ascii=False,indent=2)+'\n')
print('691 lines preserved;',len(changes),'documented editorial adaptations')
