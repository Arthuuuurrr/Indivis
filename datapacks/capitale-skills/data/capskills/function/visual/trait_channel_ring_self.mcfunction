# CapSkills 0.9.3 — Rafale channel selector. Visible around archer and in first person.
execute if score @s CAPSK_RAFALE_CHARGE matches ..19 run function capskills:visual/trait_channel_ring_small_self
execute if score @s CAPSK_RAFALE_CHARGE matches 20..79 run function capskills:visual/trait_channel_ring_medium_self
execute if score @s CAPSK_RAFALE_CHARGE matches 80.. run function capskills:visual/trait_channel_ring_ready_self
