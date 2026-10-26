# Nexus PRE35 SI1 OPT1 — optimisation en cours

Issue #182. Branche `fix/nexus-posed-surface-cost`, sans fusion dans main.
Base PRE35 SI1 SHA-256 : `399a4c64a7672e60ebbaaa52044cb547f5c41576039fe5aafe2c42485e2e8a8a`.

Le premier changement remplace la collecte et le tri répétés des volumes candidats
par un index temporaire appartenant à la grille de la pose. Les extrema de chaque
fragment sont calculés une seule fois. La découpe, les EPS, les coordonnées UV et
les cosmétiques restent ceux de PRE35. Les rangs ambigus conservent le chemin
PRE35 exact. Chaque ajout à une grille invalide son index.

Le second changement réutilise séparément les surfaces d'une partie du corps.
Il exige l'identité des six templates et shapes, l'égalité exacte de la géométrie
d'entrée et des matrices de toutes les parties dont les cellules peuvent la
recouper. Les bornes sont celles de la grille PRE35, sans seuil de distance ajouté.
Un changement d'intersection potentielle invalide aussi la réutilisation.
Les matrices sont copiées. Les caches appartiennent au workspace PRE35 et sont
donc abandonnés avec les états invalidés (cubes/ressources). Limites par workspace :
six profils, estimation conservatrice de 8 Mio de surfaces. Les entrées non finies
ou hors de la plage spatiale bornée sont calculées normalement sans réutilisation.
Le chemin première personne/local n'utilise pas ce second cache.

Les recherches excluent aussi les parties dont les cellules occupées ne peuvent
pas recouper les cellules du polygone. Ce rejet utilise les mêmes indices entiers
que PRE35, avec une garde contre l'alias des clés 21 bits. Les tables d'indices
d'une partie ne sont créées que lorsqu'une recherche en a réellement besoin.

Le test de faces extérieures conserve les projections exactes des sommets pendant
un test polygone/volume : PRE35 les recalculait jusqu'à six fois. Le scratch est
local à l'invocation, sans cache numérique inter-pose. Le code de découpe PRE35
et l'oracle de faces extérieures original restent inchangés.

Le code original est conservé en `ExactSurfaceCleaner.cleanPRE35`. Le mode de QA
`-Dnexus.opt1.verify=true` compare les sorties sur les mêmes objets, valeur flottante
par valeur flottante, sans arrondi. Ce mode n’est pas une mesure de performance.

Ne pas déployer avant validation. Les premières mesures du seul index ne suffisent
pas : rotations de tête encore coûteuses ; deuxième passe en cours de vérification.
La version interne reste beta.35 et ajoute le marqueur de build OPT1 afin de
préserver la dépendance de la liaison PREVIEW1 ; le chargement Fabric réel avec
la liaison non modifiée a été confirmé. Les assets, protocoles et UI sont inchangés.
