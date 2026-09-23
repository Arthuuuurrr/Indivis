
# RC9h — retry temporaire ouverture identité obligatoire pour clients lents
scoreboard players remove @a[tag=capitale_identity_open_pending,scores={CAP_ID_OPEN_CD=1..}] CAP_ID_OPEN_CD 1
execute as @a[tag=capitale_identity_open_pending,scores={CAP_ID_OPEN_CD=0}] run function capitale:spawn/identity_open_retry_self
# Initialisation et migration joueurs sorties du tick permanent :
# data/capitale/advancement/player/bootstrap_runtime_v100.json -> capitale:player/bootstrap_runtime_v100_self
# Justice recalculée uniquement lorsque CAP_CRIME change.
execute as @a[tag=capitale_init] unless score @s CAP_CRIME = @s CAP_CRIME_LAST run function capitale:justice/state/sync_if_crime_changed_self

# Triggers : réactivation uniquement après usage, pas en boucle sur tous les joueurs.
execute as @a[scores={CapChoix=1..}] run function capitale:choice/dispatch_self
scoreboard players enable @a[scores={CapChoix=1..}] CapChoix
scoreboard players set @a[scores={CapChoix=1..}] CapChoix 0
execute as @a[scores={QuestChoix=1..}] run function capitale:quest/dialogue/dispatch_self
scoreboard players enable @a[scores={QuestChoix=1..}] QuestChoix
scoreboard players set @a[scores={QuestChoix=1..}] QuestChoix 0
execute as @a[scores={ShopChoix=1..}] run function capitale:shop/dispatch_self
scoreboard players enable @a[scores={ShopChoix=1..}] ShopChoix
scoreboard players set @a[scores={ShopChoix=1..}] ShopChoix 0
execute as @a[scores={InfoRP=1}] run function capitale:debug/info_self
execute as @a[scores={InfoRP=2}] run function capitale:debug/journal/menu_self
execute as @a[scores={InfoRP=3}] run function capitale:debug/journal/active_self
execute as @a[scores={InfoRP=4}] run function capitale:debug/journal/progression_self
execute as @a[scores={InfoRP=5}] run function capitale:debug/inforp/status_detail_self
execute as @a[scores={InfoRP=6}] run function capitale:debug/inforp/legal_detail_self
execute as @a[scores={InfoRP=7}] run function capitale:debug/inforp/reputations_detail_self
execute as @a[scores={InfoRP=8}] run function capitale:debug/inforp/access_detail_self
execute as @a[scores={InfoRP=9}] run function capitale:debug/inforp/guidage_detail_self
execute as @a[scores={InfoRP=10}] run function capitale:debug/inforp/portails_detail_self
execute as @a[scores={InfoRP=11}] run function capitale:debug/inforp/triggers_help_self
execute as @a[scores={InfoRP=20}] run function capitale:profiles/menu_self
scoreboard players enable @a[scores={InfoRP=1..}] InfoRP
scoreboard players set @a[scores={InfoRP=1..}] InfoRP 0

# 1.5.4 — lecture volontaire des documents scellés de la Banque
execute as @a[scores={CAP_READ_BANQUE=1..}] run function capitale:quest/journalieres/banque/documents/read_break_seal_self
scoreboard players enable @a[scores={QUEST_DAILY_BANQUE=10,CAP_BANQUE_SCEAU=0}] CAP_READ_BANQUE
scoreboard players set @a[scores={CAP_READ_BANQUE=1..}] CAP_READ_BANQUE 0

scoreboard players remove @a[scores={CAP_PATH_DEBUG=1..}] CAP_PATH_DEBUG 1
execute as @a[scores={CAP_PATH_DEBUG=1..}] at @s run function capitale:npc/mira/path/q01/visualize_tick_self
scoreboard players remove @a[scores={NPC_NEAR_CD=1..}] NPC_NEAR_CD 1
scoreboard players remove @a[scores={CAP_ASSAULT_CD=1..}] CAP_ASSAULT_CD 1
scoreboard players remove @a[scores={CAP_ARREST_CD=1..}] CAP_ARREST_CD 1
scoreboard players remove @a[scores={CAP_ARREST_PENDING=1,CAP_ARREST_TIMER=1..}] CAP_ARREST_TIMER 1
execute as @a[scores={CAP_ARREST_PENDING=1,CAP_ARREST_TIMER=0}] run function capitale:crime/arrest/finish_self
scoreboard players remove @a[scores={CAP_ACCESS_CD=1..}] CAP_ACCESS_CD 1
scoreboard players remove @a[scores={CAP_BRIBE_CD=1..}] CAP_BRIBE_CD 1
scoreboard players remove @a[scores={CAP_PASS_TIMER=1..}] CAP_PASS_TIMER 1
scoreboard players remove @a[scores={CAP_JPASS_TIMER=1..}] CAP_JPASS_TIMER 1
execute as @a[scores={CAP_PASS_ZONE=50,CAP_PASS_TIMER=0,QUEST_DIVERS_RELAIS_COEUR=20..49}] run function capitale:quest/divers/relais_coeur/access/expired_self
execute as @a[scores={CAP_PASS_TIMER=0,CAP_PASS_ZONE=1..}] run scoreboard players set @s CAP_PASS_ZONE 0
scoreboard players remove @a[scores={CAP_NEAR_REACT=1..}] CAP_NEAR_REACT 1
scoreboard players remove @a[scores={CAP_TRIB_EXIT_MSG_CD=1..}] CAP_TRIB_EXIT_MSG_CD 1
scoreboard players remove @a[scores={CAP_ESCORT_WAIT_CD=1..}] CAP_ESCORT_WAIT_CD 1
scoreboard players remove @a[scores={CAP_CTX_TYPE=1..,CAP_CTX_TIMER=1..}] CAP_CTX_TIMER 1
execute as @a[scores={CAP_CTX_TYPE=1,CAP_CTX_TIMER=0}] run function capitale:choice/access/auto_follow_self
execute as @a[scores={CAP_CTX_TYPE=2,CAP_CTX_TIMER=0}] run function capitale:justice/intercept/auto_to_tribunal_self
execute as @a[scores={CAP_CTX_TYPE=3,CAP_CTX_TIMER=0}] run function capitale:justice/intercept/auto_to_tribunal_self
execute as @a[scores={CAP_CTX_TYPE=4,CAP_CTX_TIMER=0}] run function capitale:justice/intercept/auto_to_prison_self
scoreboard players remove @a[scores={CAP_QSEQ_TIMER=1..}] CAP_QSEQ_TIMER 1
execute as @a[scores={CAP_QSEQ=1..,CAP_QSEQ_TIMER=0}] run function capitale:quest/dialogue/sequence/dispatch_self
execute as @a[scores={CAP_QDIALOG_OWNER=1..}] run function capitale:quest/dialogue/anchor/check_player_self
execute if entity @e[type=armor_stand,tag=guide_leovic_registre_port,tag=escort_active,limit=1] run function capitale:quest/le_registre_du_port/escort/tick
execute if entity @e[type=armor_stand,tag=guide_leovic_registre_port,tag=escort_returning,limit=1] run function capitale:quest/le_registre_du_port/escort/tick
execute if entity @e[type=armor_stand,tag=guide_aurele_coeur,tag=escort_active,limit=1] run function capitale:quest/sous_le_regard_du_coeur/escort/tick
execute if entity @e[type=armor_stand,tag=guide_aurele_coeur,tag=escort_returning,limit=1] run function capitale:quest/sous_le_regard_du_coeur/escort/tick
execute if entity @e[type=armor_stand,tag=guide_roch_profondeurs,tag=escort_active,limit=1] run function capitale:quest/au_seuil_des_profondeurs/escort_roch/tick
execute if entity @e[type=armor_stand,tag=guide_roch_profondeurs,tag=escort_returning,limit=1] run function capitale:quest/au_seuil_des_profondeurs/escort_roch/tick
execute if entity @e[type=armor_stand,tag=guide_colin_profondeurs,tag=escort_active,limit=1] run function capitale:quest/au_seuil_des_profondeurs/escort_colin/tick
execute if entity @e[type=armor_stand,tag=guide_colin_profondeurs,tag=escort_returning,limit=1] run function capitale:quest/au_seuil_des_profondeurs/escort_colin/tick
# Patrouilles gardes — cooldown global puis tick phasé optimisé
scoreboard players remove @e[type=armor_stand,tag=patrol_active,scores={NPC_PATROL_CD=1..}] NPC_PATROL_CD 1
execute as @e[type=armor_stand,tag=guide_mira_q01,tag=patrol_active,scores={NPC_PATROL_CD=0}] at @s if entity @e[tag=npc_mira_q01,distance=..2] run function capitale:npc/mira/path/q01/dispatch_self
# Failsafe Mira Q01 : si le guide est actif mais trop loin de Mira au tout début, on le recale sur elle.
execute as @e[type=armor_stand,tag=guide_mira_q01,tag=patrol_active,scores={NPC_PATROL_STATE=0,NPC_PATROL_CD=0},limit=1] at @s unless entity @e[tag=npc_mira_q01,distance=..3,limit=1] run function capitale:npc/mira/path/q01/snap_guide_to_mira
function capitale:npc/gardes/tick_phased

# Failsafe lent des patrouilles : audit toutes les 2 minutes serveur.
scoreboard players add #cap_patrol_recovery NPC_PATROL_AUDIT_TICK 1
execute if score #cap_patrol_recovery NPC_PATROL_AUDIT_TICK matches 2400.. run function capitale:npc/patrol/recovery/audit_all
execute if score #cap_patrol_recovery NPC_PATROL_AUDIT_TICK matches 2400.. run scoreboard players set #cap_patrol_recovery NPC_PATROL_AUDIT_TICK 0

# Horloge légère des cooldowns et zones protégées : une décrémentation/vérification par seconde serveur.
scoreboard players add #daily CAP_DAILY_TICK 1
execute if score #daily CAP_DAILY_TICK matches 20.. run function capitale:quest/journalieres/cooldown/second_tick
execute if score #daily CAP_DAILY_TICK matches 20.. run function capitale:zone/protection/tick_1s
execute if score #daily CAP_DAILY_TICK matches 20.. run function capitale:adventure/fire_extinguish/tick_1s
execute if score #daily CAP_DAILY_TICK matches 20.. run scoreboard players set #daily CAP_DAILY_TICK 0

# Anti-spawn hostile legacy : désactivé par défaut en RC9, remplacé par le biome capitale:capitale.
# Réactivation temporaire possible via /function capitale:zone/nomob/legacy_enable_self
execute if score #nomob_auto CAP_NOMOB_AUTO matches 1 run scoreboard players add #nomob CAP_PERF_TICK 1
execute if score #nomob_auto CAP_NOMOB_AUTO matches 1 if score #nomob CAP_PERF_TICK matches 100.. run function capitale:zone/nomob/tick_5s
execute if score #nomob_auto CAP_NOMOB_AUTO matches 1 if score #nomob CAP_PERF_TICK matches 100.. run scoreboard players set #nomob CAP_PERF_TICK 0
execute unless score #nomob_auto CAP_NOMOB_AUTO matches 1 run scoreboard players set #nomob CAP_PERF_TICK 0

execute as @a[scores={QUEST_DAILY_RESSORT_CLOCHER=20}] at @s if entity @e[type=armor_stand,tag=zone_vieilles_mecaniques,distance=..8,limit=1] run function capitale:quest/journalieres/ressort_clocher/zone_enter_self

# Gardes statiques : timer de retour phasé toutes les 5 ticks seulement lorsqu’un garde est actif.
execute if entity @e[tag=cap_static_guard_active,limit=1] run scoreboard players add #static_guards CAP_PERF_TICK 1
execute unless entity @e[tag=cap_static_guard_active,limit=1] run scoreboard players set #static_guards CAP_PERF_TICK 0
execute if score #static_guards CAP_PERF_TICK matches 5.. if entity @e[tag=cap_static_guard_active,limit=1] run function capitale:npc/static_guards/defend_reset/tick
execute if score #static_guards CAP_PERF_TICK matches 5.. run scoreboard players set #static_guards CAP_PERF_TICK 0

# RC9AO — détecteur clic gauche à main nue, actif uniquement quand un joueur Aventure vise du feu.
function capitale:adventure/fire_extinguish/bare_hand_tick
