
scoreboard players set @s CAP_ACTE_SUD_PASS 1
scoreboard players set @s CAP_PASS_ZONE 20
scoreboard players set @s CAP_PASS_TIMER 12000
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Odon Varenne]","color":"yellow"},{"text":" : Vous avez laissé expirer le passage ? Très bien. Dix minutes de plus pour les Quartiers hauts sud. Essayez de ne pas confondre délai et invitation.","color":"white"}]
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Accès temporaire] Quartiers hauts sud — passage renouvelé pour dix minutes.","color":"green"}
execute at @s run playsound minecraft:block.beacon.activate master @s ~ ~ ~ 0.55 0.95
