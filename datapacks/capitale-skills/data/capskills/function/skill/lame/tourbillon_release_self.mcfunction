# CapSkills 0.9.103 — interdit le relâchement sans arme ou outil compatible.
execute unless items entity @s weapon.mainhand #capskills:weapon/tourbillon_compatible run function capskills:skill/lame/tourbillon_cancel_self
execute if items entity @s weapon.mainhand #capskills:weapon/tourbillon_compatible run function capskills:skill/lame/tourbillon_release_validated_self
