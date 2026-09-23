# RC9aj — normalisation compat prologue.
# Objectif : garder les quêtes séparées, mais maintenir un état commun cohérent.
# Scores par quête : QUEST_SPAWN, QUEST_GARDEPORT, QUEST_GARDECOEUR, QUEST_PROFONDEURS, QUEST_RESIDENCE_PROF.
# QUEST_PROLOGUE reste un index de chapitre, pas une quête unique.

scoreboard players add @s QUEST_SPAWN 0
scoreboard players add @s QUEST_PROLOGUE 0
scoreboard players add @s QUEST_GARDEPORT 0
scoreboard players add @s QUEST_GARDECOEUR 0
scoreboard players add @s QUEST_PROFONDEURS 0
scoreboard players add @s QUEST_RESIDENCE_PROF 0

# 1) Reconstituer QUEST_PROLOGUE depuis les quêtes déjà avancées.
execute if score @s QUEST_SPAWN matches 1..99 if score @s QUEST_PROLOGUE matches ..9 run scoreboard players set @s QUEST_PROLOGUE 10
execute if score @s QUEST_SPAWN matches 100.. if score @s QUEST_PROLOGUE matches ..19 run scoreboard players set @s QUEST_PROLOGUE 20
execute if score @s QUEST_GARDEPORT matches 1..99 if score @s QUEST_PROLOGUE matches ..19 run scoreboard players set @s QUEST_PROLOGUE 20
execute if score @s QUEST_GARDEPORT matches 100.. if score @s QUEST_PROLOGUE matches ..29 run scoreboard players set @s QUEST_PROLOGUE 30
execute if score @s QUEST_GARDECOEUR matches 1..99 if score @s QUEST_PROLOGUE matches ..29 run scoreboard players set @s QUEST_PROLOGUE 30
execute if score @s QUEST_GARDECOEUR matches 100.. if score @s QUEST_PROLOGUE matches ..39 run scoreboard players set @s QUEST_PROLOGUE 40
execute if score @s QUEST_PROFONDEURS matches 1..99 if score @s QUEST_PROLOGUE matches ..39 run scoreboard players set @s QUEST_PROLOGUE 40
execute if score @s QUEST_PROFONDEURS matches 100.. if score @s QUEST_PROLOGUE matches ..49 run scoreboard players set @s QUEST_PROLOGUE 50
execute if score @s QUEST_RESIDENCE_PROF matches 1..99 if score @s QUEST_PROLOGUE matches ..49 run scoreboard players set @s QUEST_PROLOGUE 50
execute if score @s QUEST_RESIDENCE_PROF matches 100.. if score @s QUEST_PROLOGUE matches ..99 run scoreboard players set @s QUEST_PROLOGUE 100

# 2) Activer l'étape suivante de manière homogène.
# 10 = disponible / interception possible.
execute if score @s QUEST_SPAWN matches 100.. if score @s QUEST_PROLOGUE matches 20 if score @s QUEST_GARDEPORT matches 0 run scoreboard players set @s QUEST_GARDEPORT 10
execute if score @s QUEST_GARDEPORT matches 100.. if score @s QUEST_PROLOGUE matches 30 if score @s QUEST_GARDECOEUR matches 0 run scoreboard players set @s QUEST_GARDECOEUR 10
execute if score @s QUEST_GARDECOEUR matches 100.. if score @s QUEST_PROLOGUE matches 40 if score @s QUEST_PROFONDEURS matches 0 run scoreboard players set @s QUEST_PROFONDEURS 10
execute if score @s QUEST_PROFONDEURS matches 100.. if score @s QUEST_PROLOGUE matches 50 if score @s QUEST_RESIDENCE_PROF matches 0 run scoreboard players set @s QUEST_RESIDENCE_PROF 10
