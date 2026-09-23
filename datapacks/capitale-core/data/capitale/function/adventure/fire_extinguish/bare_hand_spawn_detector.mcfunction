# Si le feu est très proche, la hitbox peut être placée dans son bloc.
execute if score @s CAP_FIRE_RAY matches ..5 align xyz positioned ~0.5 ~ ~0.5 run summon minecraft:interaction ~ ~ ~ {Tags:["cap_fire_bare_detector"],width:0.9f,height:1.0f,response:0b}
# Sinon on place la hitbox à 1,5 bloc devant les yeux : elle reste dans la portée d'attaque entité.
execute if score @s CAP_FIRE_RAY matches 6.. at @s anchored eyes positioned ^ ^ ^1.5 run summon minecraft:interaction ~ ~-0.3 ~ {Tags:["cap_fire_bare_detector"],width:0.55f,height:0.6f,response:0b}
