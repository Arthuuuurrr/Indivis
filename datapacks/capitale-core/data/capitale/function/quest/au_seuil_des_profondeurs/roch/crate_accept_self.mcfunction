function capitale:quest/dialogue/clear_self
scoreboard players set @s CAP_PROF_CAISSE 1
give @s minecraft:barrel[minecraft:custom_name={text:"Caisse du poste inférieur",color:'gold',bold:true,italic:false}] 1
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Roch Vallet]","color":"yellow"},{"text":" : Prenez. Colin Férand la réceptionnera au poste inférieur. Pour un détour si court, je veillerai à ce que votre peine soit payée.","color":"white"}]
tag @e[type=armor_stand,tag=guide_roch_profondeurs,limit=1] add escort_crate_h_resolved
