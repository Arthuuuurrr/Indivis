# Bootstrap joueur v101 : initialise la suite Prologue pour les profils existants
# sans écraser les progressions de quête déjà accomplies.
# RC9l lazy init QUEST : valeurs créées uniquement avant lecture.
scoreboard players add @s QUEST_SPAWN 0
scoreboard players add @s QUEST_GARDEPORT 0
scoreboard players add @s QUEST_GARDECOEUR 0
scoreboard players add @s QUEST_PROFONDEURS 0
scoreboard players add @s QUEST_RESIDENCE_PROF 0
scoreboard players add @s QUEST_PROLOGUE 0

# Niveau de progression reconstitué depuis les quêtes existantes.
scoreboard players set @s QUEST_PROLOGUE 10
execute if score @s QUEST_SPAWN matches 100.. run scoreboard players set @s QUEST_PROLOGUE 20
execute if score @s QUEST_GARDEPORT matches 1.. run scoreboard players set @s QUEST_PROLOGUE 20
execute if score @s QUEST_GARDEPORT matches 100.. run scoreboard players set @s QUEST_PROLOGUE 30
execute if score @s QUEST_GARDECOEUR matches 1.. run scoreboard players set @s QUEST_PROLOGUE 30
execute if score @s QUEST_GARDECOEUR matches 100.. run scoreboard players set @s QUEST_PROLOGUE 40
execute if score @s QUEST_PROFONDEURS matches 1.. run scoreboard players set @s QUEST_PROLOGUE 40
execute if score @s QUEST_PROFONDEURS matches 100.. run scoreboard players set @s QUEST_PROLOGUE 50
execute if score @s QUEST_RESIDENCE_PROF matches 1.. run scoreboard players set @s QUEST_PROLOGUE 50
execute if score @s QUEST_RESIDENCE_PROF matches 100.. run scoreboard players set @s QUEST_PROLOGUE 100
