# Resynchronisation centralisée après chargement d'un profil personnage.
# À appeler après restauration complète des scores/tags du slot par le mod HUD/profils.
# Les valeurs sources viennent du snapshot ; les valeurs dérivées sont reconstruites ici.
function capitale:profiles/resync_runtime_after_profile_load_self
scoreboard players set @s CAP_PROFILE_LOCK 0
scoreboard players enable @s InfoRP
scoreboard players enable @s QuestChoix
scoreboard players enable @s ShopChoix
scoreboard players enable @s CapChoix
