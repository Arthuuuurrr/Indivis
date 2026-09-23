clear @s minecraft:barrel[minecraft:custom_name={text:"Caisse du poste inférieur",color:'gold',bold:true,italic:false}] 1
give @s capitale_currency:martin_dor 25
function capitale:rewards/daily_bonus/roll/profondeurs_self
scoreboard players set @s CAP_PROF_CAISSE 2
scoreboard players add @s REP_GARDEPROFONDEURS 3
function capitale:bounds/reputation_all_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Colin Férand]","color":"yellow"},{"text":" : Voilà la caisse. Roch a bien fait de vous la confier. Tenez : vingt-cinq Martins d’Or pour le service rendu.","color":"white"}]
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Récompense] 25 Martins d’Or — Réputation Gardes des Profondeurs +3.","color":"gold"}
function capitale:reward/skills/core/seuil_profondeurs_self
scoreboard players set @s QUEST_PROFONDEURS 40
function capitale:quest/au_seuil_des_profondeurs/colin/open_choices_self
