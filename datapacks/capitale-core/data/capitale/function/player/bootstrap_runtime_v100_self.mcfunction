# Bootstrap one-shot joueur v100.
# Déclenché par l'advancement capitale:player/bootstrap_runtime_v100 à la première connexion
# après installation de la 1.4.13.

# Nouveau joueur : initialisation canonique + départ immédiat au vrai début du prologue.
# Cette logique reste hors tick permanent : elle passe par l’advancement one-shot v100.
execute unless entity @s[tag=capitale_init] run function capitale:spawn/first_join_to_prologue_start_self

# Ancien joueur encore sous une version antérieure : migration.
execute if entity @s[tag=capitale_init] unless score @s CAP_VERSION matches 100.. run function capitale:player/migrate_v26

# Compléments runtime introduits au fil des betas, une seule fois par joueur.
function capitale:player/ensure_runtime_self

# Synchronisation judiciaire initiale unique, puis mémorisation du niveau de crime.
function capitale:justice/state/sync_self
scoreboard players operation @s CAP_CRIME_LAST = @s CAP_CRIME

# Les triggers sont activés une première fois ; ensuite, le tick les réactive seulement après usage.
scoreboard players enable @s InfoRP
scoreboard players enable @s CapChoix
scoreboard players enable @s QuestChoix
scoreboard players enable @s ShopChoix
