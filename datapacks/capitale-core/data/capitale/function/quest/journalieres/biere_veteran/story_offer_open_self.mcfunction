function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Vétéran de la Garde du Cœur]","color":"yellow"},{"text":" : Cette bière me rappelle la crise de 892. Vous voulez entendre comment les vieux gardes racontent ce genre de nuit ?","color":"white"}]
execute if score @s CAP_BEER_STORY_HEARD matches 0 run function capitale:quest/journalieres/biere_veteran/story_show_choices_first_self
execute if score @s CAP_BEER_STORY_HEARD matches 1.. run function capitale:quest/journalieres/biere_veteran/story_show_choices_repeat_self
