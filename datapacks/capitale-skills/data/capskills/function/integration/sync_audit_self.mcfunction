# Audit de sécurité peu fréquent : récupère les modifications externes qui ne passent pas par les récompenses Puffish.
scoreboard players set #audit CAPSK_CLOCK 0
execute as @a run function capskills:integration/sync_all_self
