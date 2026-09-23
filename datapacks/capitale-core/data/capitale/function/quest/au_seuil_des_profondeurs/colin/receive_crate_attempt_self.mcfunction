scoreboard players set @s CAP_TEMP 0
execute store result score @s CAP_TEMP run clear @s minecraft:barrel[minecraft:custom_name={text:"Caisse du poste inférieur",color:'gold',bold:true,italic:false}] 0
execute if score @s CAP_TEMP matches 1.. run function capitale:quest/au_seuil_des_profondeurs/colin/receive_crate_success_self
execute unless score @s CAP_TEMP matches 1.. run function capitale:quest/au_seuil_des_profondeurs/colin/receive_crate_missing_self
scoreboard players set @s CAP_TEMP 0
