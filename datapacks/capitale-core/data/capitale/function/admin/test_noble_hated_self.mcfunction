scoreboard players set @s CAP_RANGSOCIAL 70
scoreboard players set @s CAP_CRIME 0
scoreboard players set @s REP_GARDECOEUR -100
scoreboard players set @s REP_GARDEPORT -50
scoreboard players set @s REP_GARDEPROFONDEURS -30
scoreboard players set @s REP_PROFONDEURS -80
function capitale:display/prefix/sync_self
tellraw @s {"text": "[Test] Profil noble mal vu appliqué.", "color": "gold"}
