# Retry temporaire de l'ouverture identité obligatoire.
# RC9i : s'arrête dès que le HUD a validé l'identité via le tag capitale.identity.created.
# Appelé une fois par identity_open_mandatory_self puis par core/tick toutes les secondes.
# Pas de boucle permanente : arrêt après 40 essais maximum.
execute if entity @s[tag=capitale.identity.created] run tag @s remove capitale_identity_open_pending
execute if entity @s[tag=capitale.identity.created] run tag @s remove capitale_identity_mandatory
execute unless entity @s[tag=capitale_identity_open_pending] run scoreboard players set @s CAP_ID_OPEN_CD 0
execute unless entity @s[tag=capitale_identity_open_pending] run scoreboard players set @s CAP_ID_OPEN_TRY 40
execute if entity @s[tag=capitale_identity_open_pending,scores={CAP_ID_OPEN_TRY=0..39}] run title @s actionbar {"text":"[CAPITALE_IDENTITY_OPEN]","color":"black"}
execute if entity @s[tag=capitale_identity_open_pending,scores={CAP_ID_OPEN_TRY=0..39}] run capidentity menu
execute if entity @s[tag=capitale_identity_open_pending,scores={CAP_ID_OPEN_TRY=0..39}] run scoreboard players add @s CAP_ID_OPEN_TRY 1
execute if entity @s[tag=capitale_identity_open_pending,scores={CAP_ID_OPEN_TRY=1..40}] run scoreboard players set @s CAP_ID_OPEN_CD 20
execute if entity @s[tag=capitale_identity_open_pending,scores={CAP_ID_OPEN_TRY=40..}] run tellraw @s [{"text":"[Capitale] Si le menu d'identité ne s'est pas ouvert, utilise /capidentity menu.","color":"yellow"}]
execute if entity @s[tag=capitale_identity_open_pending,scores={CAP_ID_OPEN_TRY=40..}] run tag @s remove capitale_identity_open_pending
execute unless entity @s[tag=capitale_identity_open_pending] run scoreboard players set @s CAP_ID_OPEN_CD 0
