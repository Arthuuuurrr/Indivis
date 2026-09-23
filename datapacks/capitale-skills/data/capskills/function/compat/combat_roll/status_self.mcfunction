# CapSkills 0.9.64 EXP — diagnostic Combat Roll.
function capskills:integration/sync_self
tellraw @s [{"text":"[CapSkills CR] distance x100=","color":"dark_aqua"},{"score":{"name":"@s","objective":"CAPSK_CR_DIST"},"color":"aqua"},{"text":" recharge=","color":"dark_aqua"},{"score":{"name":"@s","objective":"CAPSK_CR_RECH"},"color":"aqua"},{"text":" count=","color":"dark_aqua"},{"score":{"name":"@s","objective":"CAPSK_CR_COUNT"},"color":"aqua"}]
