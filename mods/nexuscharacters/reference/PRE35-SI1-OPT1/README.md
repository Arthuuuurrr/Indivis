# Nexus PRE35 SI1 OPT1 — optimisation en cours

Issue #182. Branche `fix/nexus-posed-surface-cost`, sans fusion dans main.
Base PRE35 SI1 SHA-256 : `399a4c64a7672e60ebbaaa52044cb547f5c41576039fe5aafe2c42485e2e8a8a`.

Le premier changement remplace la collecte et le tri répétés des volumes candidats
par un index temporaire appartenant à la grille de la pose. Les extrema de chaque
fragment sont calculés une seule fois. La découpe, les EPS, les coordonnées UV et
les cosmétiques restent ceux de PRE35. Les rangs ambigus conservent le chemin
PRE35 exact. Chaque ajout à une grille invalide son index.

Le code original est conservé en `ExactSurfaceCleaner.cleanPRE35`. Le mode de QA
`-Dnexus.opt1.verify=true` compare les sorties sur les mêmes objets, valeur flottante
par valeur flottante, sans arrondi. Ce mode n’est pas une mesure de performance.

Ne pas déployer avant validation. Les gains ne sont pas encore mesurés.
La version interne reste beta.35 et ajoute le marqueur de build OPT1 afin de
préserver la dépendance de la liaison PREVIEW1 ; cela doit être confirmé par le
chargement Fabric réel.
