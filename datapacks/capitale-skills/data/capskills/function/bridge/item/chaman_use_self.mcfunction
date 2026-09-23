# 0.9.40 — Fétiche des Ancêtres : route vers les totems Chaman.
function capskills:integration/ensure_current_self
execute if entity @s[nbt={Pose:"CROUCHING"}] run function capskills:bridge/item/chaman_use_guerison_self
execute unless entity @s[nbt={Pose:"CROUCHING"}] run function capskills:bridge/item/chaman_use_protection_self
