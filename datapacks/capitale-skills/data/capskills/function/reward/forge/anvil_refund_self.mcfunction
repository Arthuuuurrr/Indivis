execute store result score #roll CAPSK_TMP run random value 1..100
execute if score #roll CAPSK_TMP matches ..45 run experience add @s 8 points
execute if score #roll CAPSK_TMP matches ..45 run title @s actionbar {"text":"Réparateur d’enclume : +8 XP récupérés.","color":"gold"}
