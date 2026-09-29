# Exécuté en tant que squelette passager direct d'un serpent.
# Détache immédiatement le jockey puis l'envoie sous le monde afin d'éviter tout drop parasite à proximité.
ride @s dismount
execute at @s run tp @s ~ -512 ~
