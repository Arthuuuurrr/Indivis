# Fusion hc_quetes C1

Fusion des correctifs b22 de Clément avec V2/SI1b/PQ01/QMARK2/PQ02 HF8. Voir `output/RAPPORT_FUSION_C1.md` et `validation_finale.json` pour les fichiers, preuves et limites. Installation : remplacer uniquement le datapack hc_quetes, aucun JAR/core/PNJ à changer.

Les deux archives fournies et la base historique b21 sont identifiées dans `reconcile.py`. Le convertisseur et les tables gelées viennent des sources de la livraison SI1b du 20 octobre 2026, sous l’arborescence `canonical_reference/` fournie avec le script. Ces références et les dépendances tierces sont nécessaires pour reproduire le banc ; les fichiers privés et les dépendances ne sont pas republiés avec ces scripts.

```sh
python3 reconcile.py
python3 validate_merge.py
python3 prepare_runtime.py
python3 bootstrap_runtime.py
python3 run_regression.py
python3 package_report.py
```

Les fichiers livrés restent séparés des fixtures. Le banc complet réutilise les tests PQ02 HF8 depuis le projet adjacent. Les connexions, sélection Nexus, confirmation de caméra et dépendances économiques externes ont les substitutions explicites du rapport. Aucun essai graphique sur la carte Indivis n’est déduit des résultats.
