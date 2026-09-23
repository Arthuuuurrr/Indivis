# @s = joueur allié visé. Décide si le soin ciblé est possible. Si un joueur est visé mais que le soin ciblé est en cooldown, on ne bascule pas automatiquement vers le self-heal.
execute unless entity @a[tag=capskills.caster,scores={CAPSK_SEC_RANK=1..},limit=1] run title @a[tag=capskills.caster,limit=1] actionbar {"text":"Secours ciblé non débloqué : prenez Secours impérial I.","color":"red"}
execute if entity @a[tag=capskills.caster,scores={CAPSK_SEC_RANK=1..,CAPSK_HEAL_CD=1..},limit=1] run title @a[tag=capskills.caster,limit=1] actionbar {"text":"Secours ciblé en recharge.","color":"red"}
execute if entity @a[tag=capskills.caster,scores={CAPSK_SEC_RANK=1..,CAPSK_HEAL_CD=0},limit=1] run function capskills:skill/secours/apply_target_as_target
scoreboard players set #hit CAPSK_TMP 1
