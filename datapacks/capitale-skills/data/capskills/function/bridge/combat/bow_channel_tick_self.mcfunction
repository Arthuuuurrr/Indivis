# 0.9.2 — hook mod : tick visuel seulement. Le vrai tick Rafale est exécuté par capskills:tick tant que le tag channeling existe.
execute if entity @s[tag=capskills.trait.rafale.channeling] run function capskills:visual/trait_channel_ring_self
