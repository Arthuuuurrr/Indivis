# CapSkills 0.9.26 — bridge public appelé par le jar.
# Garde anti-double déclenchement : si l'AttackEntityCallback et un mixin se déclenchent le même tick,
# on évite que le second appel écrase l'actionbar ou consomme un cooldown immédiatement.
execute unless entity @s[tag=capskills.mage.contact.just_triggered] run function capskills:bridge/combat/mage_contact_knockback_checked_self
