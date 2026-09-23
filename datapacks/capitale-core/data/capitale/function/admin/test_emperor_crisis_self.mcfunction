scoreboard players set @s CAP_RANGSOCIAL 100
scoreboard players set @s CAP_CRIME 50
scoreboard players set @s REP_GARDECOEUR -100
function capitale:display/prefix/sync_self
tellraw @s {"text": "[Test] Profil empereur en crise appliqué.", "color": "gold"}
