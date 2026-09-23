function capitale:dialogue/random/roll_3_self
function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Lanternes du Globe]","color":"green"},{"text":" : Les huit lanternes du socle vibrent à peine. Sans demande d’une veilleuse, mieux vaut ne pas troubler leur rythme.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Lanternes du Globe]","color":"green"},{"text":" : La lumière du Globe paraît stable. Les réglages de veille ne se touchent point sans consigne.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text":"[Lanternes du Globe]","color":"green"},{"text":" : Le socle émet une clarté basse, presque régulière. Quelqu’un des Profondeurs saurait sans doute quoi en faire.","color":"white"}]
