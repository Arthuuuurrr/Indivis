# CapSkills 0.9.1 — sélection du cercle de Provocation impériale selon le rang Bastion.
execute if score @s CAPSK_BASTION_RANK matches 2.. run function capskills:visual/taunt_ring_r9_self
execute if score @s CAPSK_BASTION_RANK matches 1 unless score @s CAPSK_BASTION_RANK matches 2.. run function capskills:visual/taunt_ring_r6_self
