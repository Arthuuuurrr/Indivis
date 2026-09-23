# CapSkills 0.9.56 — téléporte la cible de Poigne à la position courante validée.
# Exécuté depuis le lanceur, à une position dont les blocs pieds+tête sont sûrs.
tp @e[tag=capskills.mod_pull_target,limit=1,sort=nearest] ~ ~ ~
scoreboard players set #placed CAPSK_TMP 1
