# CapSkills 0.9.5 — canalisation visuelle indépendante du vrai cast.
# Ne déclenche jamais Rafale : sert uniquement à rendre la charge lisible pour l’archer et les adversaires.
function capskills:integration/ensure_current_self
execute unless entity @s[tag=capskills.trait.rafale.r1] run function capskills:skill/trait/visual_channel_clear_self
execute if entity @s[tag=capskills.trait.rafale.r1] if score @s CAPSK_RAFALE_CD matches 1.. run function capskills:skill/trait/visual_channel_clear_self
execute if entity @s[tag=capskills.trait.rafale.r1] if score @s CAPSK_RAFALE_CD matches 0 unless entity @s[tag=capskills.trait.rafale.visual_channel] run function capskills:skill/trait/visual_channel_start_self
execute if entity @s[tag=capskills.trait.rafale.r1] if score @s CAPSK_RAFALE_CD matches 0 run scoreboard players add @s CAPSK_RAFALE_VIS 1
execute if entity @s[tag=capskills.trait.rafale.r1] if score @s CAPSK_RAFALE_CD matches 0 if score @s CAPSK_RAFALE_VIS matches ..30 run function capskills:visual/trait_channel_ring_small_self
execute if entity @s[tag=capskills.trait.rafale.r1] if score @s CAPSK_RAFALE_CD matches 0 if score @s CAPSK_RAFALE_VIS matches 31..79 run function capskills:visual/trait_channel_ring_medium_self
execute if entity @s[tag=capskills.trait.rafale.r1] if score @s CAPSK_RAFALE_CD matches 0 if score @s CAPSK_RAFALE_VIS matches 80.. run function capskills:visual/trait_channel_ring_ready_self
execute if entity @s[tag=capskills.trait.rafale.r1] if score @s CAPSK_RAFALE_CD matches 0 if score @s CAPSK_RAFALE_VIS matches ..30 run title @s actionbar {"text":"Rafale : canalisation visible — rouge","color":"red"}
execute if entity @s[tag=capskills.trait.rafale.r1] if score @s CAPSK_RAFALE_CD matches 0 if score @s CAPSK_RAFALE_VIS matches 31..79 run title @s actionbar {"text":"Rafale : canalisation visible — orange","color":"gold"}
execute if entity @s[tag=capskills.trait.rafale.r1] if score @s CAPSK_RAFALE_CD matches 0 if score @s CAPSK_RAFALE_VIS matches 80.. run title @s actionbar {"text":"Rafale prête — cible l’axe avant de relâcher","color":"green"}
execute if score @s CAPSK_RAFALE_VIS matches 120.. run scoreboard players set @s CAPSK_RAFALE_VIS 120
# 0.9.6 fallback : si l'ancien tick visuel est appelé, utilise aussi le visuel persistant.
execute if entity @s[tag=capskills.trait.rafale.r1] if score @s CAPSK_RAFALE_CD matches 0 run function capskills:visual/rafale_charge_persistent_self
