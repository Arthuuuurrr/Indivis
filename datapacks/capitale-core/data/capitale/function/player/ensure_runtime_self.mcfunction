# Sécurité runtime v32 : crée les scores absents sans modifier les valeurs existantes.
# RC9l: QUEST_* retirés de l'initialisation globale ; lazy init dans les points d’entrée/lecture.
# RC9l lazy init QUEST : valeurs créées uniquement avant lecture.
# RC9ad — lazy init prologue complet restauré
scoreboard players add @s QUEST_SPAWN 0
scoreboard players add @s QUEST_PROLOGUE 0
scoreboard players add @s QUEST_COURONNE 0
scoreboard players add @s QUEST_GARDEPORT 0
scoreboard players add @s QUEST_GARDECOEUR 0
scoreboard players add @s QUEST_PROFONDEURS 0
scoreboard players add @s QUEST_RESIDENCE_PROF 0
scoreboard players add @s QUEST_DAILY_BEER_GC 0
# RC9al — lazy init restaurée pour quêtes secondaires/journalières héritées.
scoreboard players add @s QUEST_DIVERS_LIBRAIRE_NOBLE 0
scoreboard players add @s QUEST_DAILY_REGISTRE_PORT 0
scoreboard players add @s QUEST_DIVERS_ACTE_DETTE 0
scoreboard players add @s QUEST_DAILY_RESSORT_CLOCHER 0
scoreboard players add @s QUEST_DAILY_TEMOIGNAGES_QUAIS 0
scoreboard players add @s QUEST_DAILY_BANQUE 0
scoreboard players add @s QUEST_DIVERS_LAMPE 0
scoreboard players add @s QUEST_VN_CAPITAINE 0
scoreboard players add @s QUEST_VN_DOCKS 0
scoreboard players add @s QUEST_VN_ASCENSION 0
scoreboard players add @s QUEST_VN_NAVIRE 0
# RC9aj — normalisation centralisée des transitions du prologue.
function capitale:quest/prologue/normalize_self
scoreboard players add @s QUEST_DIVERS_RELAIS_COEUR 0
scoreboard players add @s QUEST_JOURNALIERES 0
scoreboard players add @s REP_COURONNE 0
scoreboard players add @s REP_GARDECOEUR 0
scoreboard players add @s REP_GARDEPORT 0
scoreboard players add @s REP_GARDEPROFONDEURS 0
scoreboard players add @s REP_PROFONDEURS 0
scoreboard players add @s CAP_CRIME 0
scoreboard players add @s CAP_CRIME_LAST 0
scoreboard players add @s CAP_RANGSOCIAL 0
scoreboard players add @s CAP_ROLE 0
scoreboard players add @s CAP_TEMP 0
scoreboard players add @s CAP_DEBUG 0
scoreboard players add @s CAP_QUETEACTIVE 0
scoreboard players add @s CAP_TRAITEMENT 0
scoreboard players add @s CAP_MENACE 0
scoreboard players add @s CAP_DIALOGUE 0
scoreboard players add @s CAP_RANGCLASSE 0
scoreboard players add @s CAP_CRIMECLASSE 0
scoreboard players add @s CAP_REPCLASSE 0
scoreboard players add @s CAP_PREFIXMODE 0
scoreboard players add @s CAP_ASSAULT_CD 0
scoreboard players add @s CAP_ARREST_CD 0
scoreboard players add @s CAP_ALERT 0
scoreboard players add @s CAP_ARREST_PENDING 0
scoreboard players add @s CAP_ARREST_TIMER 0
scoreboard players add @s CAP_ARREST_SOURCE 0
scoreboard players add @s NPC_NEAR_CD 0
scoreboard players add @s NPC_PATROL_STATE 0
scoreboard players add @s NPC_PATROL_CD 0
scoreboard players add @s NPC_PATROL_STUCK 0
scoreboard players add @s CAP_INIT 0
scoreboard players add @s CAP_VERSION 0
scoreboard players add @s CAP_ETAT 0
scoreboard players add @s CAP_QUESTLOCK 0
scoreboard players add @s CAP_SANCTIONLOCK 0

scoreboard players add @s CAP_RAW 0
scoreboard players add @s CAP_FLAG 0
scoreboard players add @s ACCESS_CAPITALE 0
scoreboard players add @s ACCESS_QH_NORD 0
scoreboard players add @s ACCESS_QH_SUD 0
scoreboard players add @s ACCESS_PALAIS 0
scoreboard players add @s ACCESS_ARCHIVES 0
scoreboard players add @s ACCESS_BANQUE 0
scoreboard players add @s ACCESS_TRIBUNAL 0
scoreboard players add @s ACCESS_COEUR 0
scoreboard players add @s ACCESS_SEUILS 0
scoreboard players add @s CAP_ACCESS_CD 0
scoreboard players add @s CAP_ACCESS_WARN 0
scoreboard players add @s CAP_ACCESS_ZONE 0
scoreboard players add @s CAP_BRIBE_CD 0
scoreboard players add @s CAP_BRIBE_CHANCE 0
scoreboard players add @s CAP_BRIBE_ROLL 0
scoreboard players add @s CAP_BRIBE_FAILS 0
scoreboard players add @s CAP_BRIBE_RESULT 0
scoreboard players add @s CAP_PASS_ZONE 0
scoreboard players add @s CAP_PASS_TIMER 0
scoreboard players add @s CAP_BRIBE_ZONE 0
scoreboard players add @s CAP_BRIBE_FACTION 0
scoreboard players add @s CAP_AMENDE 0
scoreboard players add @s CAP_COMPARUTION 0
scoreboard players add @s CAP_FUITE_JUSTICE 0
scoreboard players add @s CAP_RETENTION_TRIBUNAL 0
scoreboard players add @s CAP_JPASS_TIMER 0
scoreboard players add @s CAP_JUSTICE_LEVEL 0
scoreboard players add @s CAP_JCOINS 0
scoreboard players add @s CAP_QDIALOG_OWNER 0
scoreboard players add @s CAP_QDIALOG_KEEP 0
scoreboard players add @s CAP_QDIALOG_TOKEN 0
scoreboard players add @s QuestChoix 0
scoreboard players add @s ShopChoix 0
scoreboard players add @s CAP_QSEQ 0
scoreboard players add @s CAP_QSEQ_TIMER 0
scoreboard players add @s CAP_ESCORT_WAIT_CD 0
scoreboard players add @s CAP_LEOVIC_GIFT 0
scoreboard players add @s CAP_PROF_CAISSE 0
scoreboard players add @s CAP_CHAMBRE_PROF 0
scoreboard players add @s CAP_DAILY_BEER_GC_DAY 0
scoreboard players add @s CAP_DAILY_BEER_GC_EVER 0
scoreboard players add @s CAP_DAILY_DONE_TOTAL 0
scoreboard players add @s CAP_BEER_STORY_HEARD 0
scoreboard players add @s CAP_DAILY_REGISTRE_PORT_DAY 0
scoreboard players add @s CAP_DAILY_REGISTRE_PORT_EVER 0
scoreboard players add @s CAP_LIBRAIRE_NOBLE_PASS 0
scoreboard players add @s REP_NOBLESSE 0
scoreboard players add @s CAP_RELAIS_COEUR_A 0
scoreboard players add @s CAP_RELAIS_COEUR_B 0
scoreboard players add @s CAP_RELAIS_COEUR_C 0
scoreboard players add @s CAP_RELAIS_COEUR_COUNT 0
scoreboard players add @s CAP_DETTE_CHOIX 0
scoreboard players add @s CAP_JOURNAL_ACTIVE 0
scoreboard players add @s CAP_JOURNAL_TMP 0
execute if score @s CAP_DAILY_BEER_GC_EVER matches 0 if score @s CAP_DAILY_BEER_GC_DAY matches 0 run scoreboard players set @s CAP_DAILY_BEER_GC_DAY -1
execute if score @s CAP_DAILY_REGISTRE_PORT_EVER matches 0 if score @s CAP_DAILY_REGISTRE_PORT_DAY matches 0 run scoreboard players set @s CAP_DAILY_REGISTRE_PORT_DAY -1
scoreboard players add @s CAP_ACTE_SUD_PASS 0
scoreboard players add @s CAP_CD_BEER_GC 0
scoreboard players add @s CAP_CD_REGISTRE_PORT 0
scoreboard players add @s CAP_CD_RELAIS_COEUR 0
scoreboard players add @s CAP_CD_RESSORT_CLOCHER 0
scoreboard players add @s CAP_CD_TEMOIGNAGES_QUAIS 0
scoreboard players add @s CAP_CD_GLOBE 0
scoreboard players add @s CAP_CD_BEER_GC_END 0
scoreboard players add @s CAP_CD_REGISTRE_PORT_END 0
scoreboard players add @s CAP_CD_RELAIS_COEUR_END 0
scoreboard players add @s CAP_CD_RESSORT_CLOCHER_END 0
scoreboard players add @s CAP_CD_TEMOIGNAGES_QUAIS_END 0
scoreboard players add @s CAP_CD_GLOBE_END 0
scoreboard players add @s CAP_CD_BANQUE_END 0
scoreboard players add @s CAP_DAILY_RELAIS_COEUR_EVER 0
scoreboard players add @s CAP_DAILY_RESSORT_CLOCHER_EVER 0
scoreboard players add @s CAP_DAILY_TEMOIGNAGES_QUAIS_EVER 0
scoreboard players add @s CAP_DAILY_GLOBE_EVER 0
scoreboard players add @s CAP_RESSORT_TIMER 0
scoreboard players add @s CAP_TEMOIN_QUAI_A 0
scoreboard players add @s CAP_TEMOIN_QUAI_B 0
scoreboard players add @s CAP_TEMOIN_QUAI_C 0
scoreboard players add @s CAP_TEMOIGNAGES_COUNT 0
# Migration 1.4.42 → 1.4.43 : Les Relais du Cœur deviennent une journalière.
execute if score @s QUEST_DIVERS_RELAIS_COEUR matches 100.. if score @s CAP_DAILY_RELAIS_COEUR_EVER matches 0 run scoreboard players add @s QUEST_JOURNALIERES 1
execute if score @s QUEST_DIVERS_RELAIS_COEUR matches 100.. if score @s CAP_DAILY_RELAIS_COEUR_EVER matches 0 run scoreboard players set @s CAP_DAILY_RELAIS_COEUR_EVER 1
execute if score @s QUEST_DIVERS_RELAIS_COEUR matches 100.. run scoreboard players set @s QUEST_DIVERS_RELAIS_COEUR 0
function capitale:access/sync_tags_self

scoreboard players add @s CAP_NEAR_REACT 0
scoreboard players add @s CAP_CTX_TYPE 0
scoreboard players add @s CAP_CTX_ZONE 0
scoreboard players add @s CAP_CTX_FACTION 0
scoreboard players add @s CAP_CTX_MODE 0
scoreboard players add @s CAP_CTX_TIMER 0
scoreboard players add @s CapChoix 0
scoreboard players add @s CAP_PROFILE_ACTIVE 0
scoreboard players add @s CAP_PROFILE_LOCK 0
scoreboard players add @s CAP_PROFILE_SWITCH 0
scoreboard players add @s CAP_PROFILE_TMP 0
# Réactivation de sécurité des triggers utilisateur.
scoreboard players enable @s InfoRP
scoreboard players enable @s QuestChoix
scoreboard players enable @s ShopChoix
scoreboard players enable @s CapChoix
scoreboard players add @s CAP_GLOBE_DONE 0
scoreboard players add @s CAP_GLOBE_L1 0
scoreboard players add @s CAP_GLOBE_L2 0
scoreboard players add @s CAP_GLOBE_L3 0
scoreboard players add @s CAP_GLOBE_L4 0
scoreboard players add @s CAP_GLOBE_L5 0
scoreboard players add @s CAP_GLOBE_L6 0
scoreboard players add @s CAP_GLOBE_L7 0
scoreboard players add @s CAP_GLOBE_L8 0
scoreboard players add @s CAP_DISC_PROFONDEURS 0
function capitale:quest/journalieres/cooldown/sync_self

# 1.5.4 alpha 5 hotfix 3 — spawn/prologue
scoreboard players add @s CAP_FIRST_JOIN_DONE 0

# 1.5.4 alpha 5 hotfix 10 — CapSkills : flags de récompenses déjà accordées
scoreboard players add @s CAPSK_FIRST_BANQUE 0
scoreboard players add @s CAPSK_FIRST_BEER 0
scoreboard players add @s CAPSK_FIRST_RESSORT 0
scoreboard players add @s CAPSK_FIRST_TEMOINS 0
scoreboard players add @s CAPSK_FIRST_REGISTRE 0
scoreboard players add @s CAPSK_FIRST_RELAIS 0
scoreboard players add @s CAPSK_FIRST_GLOBE 0
scoreboard players add @s CAPSK_REWARD_SPAWN 0
scoreboard players add @s CAPSK_REWARD_REGPORT 0
scoreboard players add @s CAPSK_REWARD_AURELE 0
scoreboard players add @s CAPSK_REWARD_PROF 0
scoreboard players add @s CAPSK_REWARD_LIBRAIRE 0
scoreboard players add @s CAPSK_REWARD_DETTE 0
scoreboard players add @s CAPSK_REWARD_VN1 0
scoreboard players add @s CAPSK_REWARD_VN2 0
scoreboard players add @s CAPSK_REWARD_VN3 0
scoreboard players add @s CAPSK_REWARD_VN4 0
scoreboard players add @s CAP_CUR_LEGACY 0
scoreboard players add @s CAP_CUR_CONVERTED 0
scoreboard players add @s CAP_CUR_BANK 0


# Aldren le Traqueur - quete "La menace des Orcs"
scoreboard players add @s QUEST_ALDREN_ORCS 0
scoreboard players add @s CAP_ALDREN_WARR 0
scoreboard players add @s CAP_ALDREN_ARCH 0
scoreboard players add @s CAP_ALDREN_CHAMP 0
scoreboard players add @s CAP_ALDREN_MORGRA 0
