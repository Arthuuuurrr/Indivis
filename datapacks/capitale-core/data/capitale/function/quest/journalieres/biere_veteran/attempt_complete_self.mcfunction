scoreboard players set @s CAP_TEMP 0
execute store result score @s CAP_TEMP run clear @s minecraft:potion[potion_contents={potion:'minecraft:water'},custom_name=[{text:'Bière',italic:false,color:'gold'}],lore=[[{text:'Bière brune du Port, servie aux gardes et aux bateliers.',italic:false,color:'dark_purple'}]]] 0
execute if score @s CAP_TEMP matches 1.. run function capitale:quest/journalieres/biere_veteran/consume_beer_success_self
execute if score @s CAP_TEMP matches 0 run function capitale:quest/journalieres/biere_veteran/missing_beer_self
