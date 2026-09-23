# Ouverture obligatoire du menu d'identité pour un nouveau personnage RP HUD.
# RC9j : purge l'ancien tag d'identité validée avant d'armer le retry,
# afin qu'un nouveau slot créé depuis un personnage déjà validé ne stoppe pas le retry par un ancien ACK.
# On arme un pending court, puis identity_open_retry_self retente /capidentity menu
# pendant une fenêtre temporaire pour les clients lents.
tag @s remove capitale.identity.created
tag @s add capitale_identity_open_pending
tag @s remove capitale_identity_menu_opened
scoreboard players set @s CAP_ID_OPEN_TRY 0
scoreboard players set @s CAP_ID_OPEN_CD 0
function capitale:spawn/identity_open_retry_self
