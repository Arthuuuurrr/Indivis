# Correctif des liaisons au quai et des « ! » — HF12 / QMARK2

26 octobre 2026 — Minecraft Fabric 1.21.11 / EasyNPC 7.16.0.

Cette livraison contient les **trois composants modifiés**. Elle reprend le prologue HF11, la Séquence I et les règles de disponibilité des quêtes du core.

## Installation

1. Fermer les conversations et arrêter le serveur après la sauvegarde habituelle du monde et des profils.
2. Remplacer le mod de quêtes/journal par `haute-capitale-quetes-0.1.0+1.21.11.b7-SEQUENCE-I-SI1b-PQ01-R3-HF12.jar` sur le **serveur et chaque client**.
3. Remplacer les deux datapacks du monde serveur par `capitale_core_1.5.7_V2_SEQUENCE_I_SI1_PQ01_R3_HF9_QMARK2.zip` et `hc_quetes_b22_V2_SEQUENCE_I_SI1b_PQ01_R3_HF4_QMARK2.zip`. Ne garder qu'une version active de chacun.
4. Redémarrer. Les liaisons, points et progressions déjà enregistrés sont réutilisés. Pour les PNJ dont la liaison a été refusée, relancer seulement la commande de leur rôle, près du bon PNJ.

**Décompresser le ZIP de livraison** : son dossier `installation/mods/` contient le JAR et `installation/datapacks/` les deux vrais datapacks. Les deux ZIP intérieurs sont directement installables dans le dossier des datapacks du monde serveur ; ils contiennent chacun `pack.mcmeta` à leur racine. Le ZIP de livraison lui-même n'est pas un datapack. Les dossiers décompressés sont également utilisables si `pack.mcmeta` est directement à leur racine.

Le JAR Nexus, Dialogue b10, EasyNPC, le mod admin et les voix ne sont pas fournis dans ce correctif. Conserver leurs versions actuelles.

## Ce qui est corrigé

- Althéon au quai : reconnaissance de l'ancienne action `capitaine_quai/interact_self` et acheminement du clic vers la phase correcte.
- Gautier, Hugues, Anselme et Perrin au quai : reconnaissance des anciennes actions `matelot_01_self` à `matelot_04_self` restées sur les copies. Les variantes `_quai_self` et les actions natives du même rôle sont reconnues aussi.
- Les « ! » du core sont actualisés par le journal toutes les 20 ticks serveur, sans dépendre du tick de la Séquence I. Les donneurs sont reconnus par leurs actions connues, leurs tags ou leur nom, y compris les noms colorés.
- Le marqueur est relevé au-dessus du nom et éclairé ; les conditions de quête, de délai, de rang et d'escorte sont conservées.

Les commandes de liaison gardent les autres actions et refusent les rôles incompatibles ou les déclencheurs multiples. Aucun reset ou déplacement de PNJ n'est nécessaire pour cette mise à jour.

- [Commandes exactes au quai](COMMANDES_QUAI.md)
- [Guide complet : liaisons, pose et déplacements](INSTALLATION_ET_POSE_PNJ.md)
- [Marqueurs et diagnostic](GUIDE_REPERES.md)
- [Vérifications et limites](VALIDATION.md)
- [GitHub : PR nº177](https://github.com/Arthuuuurrr/Indivis/pull/177)
