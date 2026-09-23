give @s minecraft:crossbow[minecraft:custom_name={text:"Arbalète d'entraînement",color:"gray",italic:false},minecraft:lore=[{text:"Arbalète simple remise avec la première spécialisation d'arbalète.",color:"gray",italic:false},{text:"Livrée avec 16 flèches.",color:"gray",italic:false},{text:"Objet de départ : utile si les quêtes n'ont pas encore donné d'équipement.",color:"dark_gray",italic:false}]] 1
give @s minecraft:arrow 16
tellraw @s [{"text":"[CapSkills] ","color":"gold"},{"text":"Arbalète d'entraînement + 16 flèches reçues.","color":"gray"}]
