# 0.8.25 — affiche une fois par connexion un bouton utile si le joueur a Alchimiste.
scoreboard players add @s CAPSK_LOGIN_INIT 0
scoreboard players add @s CAPSK_LEAVE_SEEN 0
scoreboard players add @s CAPSK_LEAVES 0
execute if score @s CAPSK_LOGIN_INIT matches 0 run function capskills:integration/login_prompt_show_self
execute if score @s CAPSK_LOGIN_INIT matches 1 if score @s CAPSK_LEAVES > @s CAPSK_LEAVE_SEEN run function capskills:integration/login_prompt_show_self
scoreboard players operation @s CAPSK_LEAVE_SEEN = @s CAPSK_LEAVES
scoreboard players set @s CAPSK_LOGIN_INIT 1
