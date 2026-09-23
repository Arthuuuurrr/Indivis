function capitale:player/ensure_runtime_self
tag @s remove ACCESS_CAPITALE
tag @s remove ACCESS_QUARTIERS_HAUTS_NORD
tag @s remove ACCESS_QUARTIERS_HAUTS_SUD
tag @s remove ACCESS_PALAIS
tag @s remove ACCESS_ARCHIVES
tag @s remove ACCESS_BANQUE
tag @s remove ACCESS_TRIBUNAL
tag @s remove ACCESS_COEUR
tag @s remove ACCESS_SEUILS_SCELLES
scoreboard players set @s QUEST_SPAWN 0
scoreboard players set @s QUEST_PROLOGUE 0
scoreboard players set @s QUEST_COURONNE 0
scoreboard players set @s QUEST_GARDECOEUR 0
scoreboard players set @s QUEST_GARDEPORT 0
scoreboard players set @s QUEST_PROFONDEURS 0
scoreboard players set @s QUEST_RESIDENCE_PROF 0
scoreboard players set @s QUEST_JOURNALIERES 0
scoreboard players set @s QUEST_DAILY_BEER_GC 0
scoreboard players set @s QUEST_DIVERS_LIBRAIRE_NOBLE 0
scoreboard players set @s QUEST_DAILY_REGISTRE_PORT 0
scoreboard players set @s QUEST_DIVERS_RELAIS_COEUR 0
scoreboard players set @s QUEST_DIVERS_ACTE_DETTE 0
scoreboard players set @s QUEST_SIDE_BA_Q01 0
scoreboard players set @s QUEST_SIDE_BA_Q02 0
scoreboard players set @s QUEST_SIDE_BA_Q03 0
scoreboard players set @s QUEST_SIDE_BA_Q04 0
scoreboard players set @s REP_BAS_ANNEAUX 0
scoreboard players set @s REP_BA_GARDES 0
scoreboard players set @s REP_BA_ARTISANS 0
scoreboard players set @s REP_BA_MONDE_GRIS 0
scoreboard players set @s QUEST_VN_CAPITAINE 0
scoreboard players set @s QUEST_VN_DOCKS 0
scoreboard players set @s QUEST_VN_ASCENSION 0
scoreboard players set @s QUEST_VN_NAVIRE 0
scoreboard players set @s REP_COURONNE 0
scoreboard players set @s REP_GARDECOEUR -20
scoreboard players set @s REP_GARDEPORT 0
scoreboard players set @s REP_GARDEPROFONDEURS -10
scoreboard players set @s REP_PROFONDEURS 0
scoreboard players set @s REP_NOBLESSE 0
scoreboard players set @s CAP_CRIME 0
scoreboard players set @s CAP_CRIME_LAST 0
scoreboard players set @s CAP_RANGSOCIAL 0
scoreboard players set @s CAP_ROLE 0
scoreboard players set @s CAP_DEBUG 0
scoreboard players set @s CAP_QUETEACTIVE 0
scoreboard players set @s CAP_TRAITEMENT 0
scoreboard players set @s CAP_MENACE 0
scoreboard players set @s CAP_DIALOGUE 0
scoreboard players set @s CAP_RANGCLASSE 0
scoreboard players set @s CAP_CRIMECLASSE 0
scoreboard players set @s CAP_REPCLASSE 20
scoreboard players set @s CAP_PREFIXMODE 0
scoreboard players set @s CAP_ASSAULT_CD 0
scoreboard players set @s CAP_ARREST_CD 0
scoreboard players set @s CAP_ALERT 0
scoreboard players set @s CAP_ARREST_PENDING 0
scoreboard players set @s CAP_ARREST_TIMER 0
scoreboard players set @s CAP_ARREST_SOURCE 0
scoreboard players set @s NPC_NEAR_CD 0
scoreboard players set @s NPC_PATROL_STATE 0
scoreboard players set @s NPC_PATROL_CD 0
scoreboard players set @s NPC_PATROL_STUCK 0
scoreboard players set @s CAP_ACCESS_CD 0
scoreboard players set @s CAP_ACCESS_WARN 0
scoreboard players set @s CAP_ACCESS_ZONE 0
scoreboard players set @s CAP_BRIBE_CD 0
scoreboard players set @s CAP_BRIBE_CHANCE 0
scoreboard players set @s CAP_BRIBE_ROLL 0
scoreboard players set @s CAP_BRIBE_FAILS 0
scoreboard players set @s CAP_BRIBE_RESULT 0
scoreboard players set @s CAP_PASS_ZONE 0
scoreboard players set @s CAP_PASS_TIMER 0
scoreboard players set @s CAP_BRIBE_ZONE 0
scoreboard players set @s CAP_BRIBE_FACTION 0
scoreboard players set @s CAP_AMENDE 0
scoreboard players set @s CAP_COMPARUTION 0
scoreboard players set @s CAP_FUITE_JUSTICE 0
scoreboard players set @s CAP_RETENTION_TRIBUNAL 0
scoreboard players set @s CAP_JPASS_TIMER 0
scoreboard players set @s CAP_JUSTICE_LEVEL 0
scoreboard players set @s CAP_JCOINS 0
scoreboard players set @s CAP_TRIB_EXIT_MSG_CD 0
scoreboard players set @s CAP_QDIALOG_OWNER 0
scoreboard players set @s CAP_QDIALOG_KEEP 0
scoreboard players set @s CAP_QDIALOG_TOKEN 0
scoreboard players set @s QuestChoix 0
scoreboard players set @s ShopChoix 0
scoreboard players set @s CAP_QSEQ 0
scoreboard players set @s CAP_QSEQ_TIMER 0
scoreboard players set @s CAP_ESCORT_WAIT_CD 0
scoreboard players set @s CAP_CHAMBRE_PROF 0
scoreboard players set @s CAP_FLAG 0
scoreboard players set @s CAP_PROF_CAISSE 0
scoreboard players set @s CAP_TEMP 0
scoreboard players set @s NPC_RETURN_TIMER 0
scoreboard players set @s CAP_LEOVIC_GIFT 0
scoreboard players set @s CAP_DAILY_BEER_GC_DAY -1
scoreboard players set @s CAP_DAILY_BEER_GC_EVER 0
scoreboard players set @s CAP_DAILY_DONE_TOTAL 0
scoreboard players set @s CAP_BEER_STORY_HEARD 0
scoreboard players set @s CAP_DAILY_REGISTRE_PORT_DAY -1
scoreboard players set @s CAP_DAILY_REGISTRE_PORT_EVER 0
scoreboard players set @s CAP_LIBRAIRE_NOBLE_PASS 0
scoreboard players set @s CAP_RELAIS_COEUR_A 0
scoreboard players set @s CAP_RELAIS_COEUR_B 0
scoreboard players set @s CAP_RELAIS_COEUR_C 0
scoreboard players set @s CAP_RELAIS_COEUR_COUNT 0
scoreboard players set @s CAP_DETTE_CHOIX 0
scoreboard players set @s CAP_ACTE_SUD_PASS 0
scoreboard players set @s CAP_JOURNAL_ACTIVE 0
scoreboard players set @s CAP_JOURNAL_TMP 0
scoreboard players set @s QUEST_CITOYENNETE 0
scoreboard players set @s QUEST_DAILY_RESSORT_CLOCHER 0
scoreboard players set @s QUEST_DAILY_TEMOIGNAGES_QUAIS 0
scoreboard players set @s CAP_CD_BEER_GC 0
scoreboard players set @s CAP_CD_REGISTRE_PORT 0
scoreboard players set @s CAP_CD_RELAIS_COEUR 0
scoreboard players set @s CAP_CD_RESSORT_CLOCHER 0
scoreboard players set @s CAP_CD_TEMOIGNAGES_QUAIS 0
scoreboard players set @s CAP_CD_GLOBE 0
scoreboard players set @s CAP_CD_BEER_GC_END 0
scoreboard players set @s CAP_CD_REGISTRE_PORT_END 0
scoreboard players set @s CAP_CD_RELAIS_COEUR_END 0
scoreboard players set @s CAP_CD_RESSORT_CLOCHER_END 0
scoreboard players set @s CAP_CD_TEMOIGNAGES_QUAIS_END 0
scoreboard players set @s CAP_CD_GLOBE_END 0
scoreboard players set @s CAP_CD_BANQUE_END 0
scoreboard players set @s CAP_DAILY_RELAIS_COEUR_EVER 0
scoreboard players set @s CAP_DAILY_RESSORT_CLOCHER_EVER 0
scoreboard players set @s CAP_DAILY_TEMOIGNAGES_QUAIS_EVER 0
scoreboard players set @s CAP_DAILY_GLOBE_EVER 0
scoreboard players set @s CAP_RESSORT_TIMER 0
scoreboard players set @s CAP_TEMOIN_QUAI_A 0
scoreboard players set @s CAP_TEMOIN_QUAI_B 0
scoreboard players set @s CAP_TEMOIN_QUAI_C 0
scoreboard players set @s CAP_TEMOIGNAGES_COUNT 0
clear @s minecraft:written_book[minecraft:custom_model_data={strings:['quest_libraire_exemplaire_noble']}]
clear @s minecraft:paper[minecraft:custom_model_data={strings:['acte_dette_falsifie']}]
clear @s minecraft:tripwire_hook[minecraft:custom_model_data={strings:['ressort_clocher']}]
# Reset réel des guides / PNJ d’escortes de quête du Prologue.
# Utilisé par le reset admin afin de retrouver un état de test propre.
function capitale:quest/le_registre_du_port/escort/failsafe_to_a
function capitale:quest/sous_le_regard_du_coeur/escort/failsafe_to_a
function capitale:quest/au_seuil_des_profondeurs/escort_roch/failsafe_to_a
function capitale:quest/au_seuil_des_profondeurs/escort_colin/failsafe_to_a
scoreboard players set @s ACCESS_CAPITALE 0
scoreboard players set @s ACCESS_QH_NORD 0
scoreboard players set @s ACCESS_QH_SUD 0
scoreboard players set @s ACCESS_PALAIS 0
scoreboard players set @s ACCESS_ARCHIVES 0
scoreboard players set @s ACCESS_BANQUE 0
scoreboard players set @s ACCESS_TRIBUNAL 0
scoreboard players set @s ACCESS_COEUR 0
scoreboard players set @s ACCESS_SEUILS 0
scoreboard players set @s CAP_NEAR_REACT 0
scoreboard players set @s CAP_CTX_TYPE 0
scoreboard players set @s CAP_CTX_ZONE 0
scoreboard players set @s CAP_CTX_FACTION 0
scoreboard players set @s CAP_CTX_MODE 0
scoreboard players set @s CAP_CTX_TIMER 0
scoreboard players set @s CapChoix 0
scoreboard players set @s CAP_INIT 1
scoreboard players set @s CAP_VERSION 100
scoreboard players set @s CAP_ETAT 0
scoreboard players set @s CAP_QUESTLOCK 0
scoreboard players set @s CAP_SANCTIONLOCK 0
tag @s add capitale_init
function capitale:access/sync_tags_self
function capitale:display/prefix/sync_self
tellraw @s {"text":"[Capitale] Dossier RP et états de quêtes réinitialisés pour la beta 1.5.","color":"gray"}
scoreboard players enable @s QuestChoix
scoreboard players enable @s ShopChoix
scoreboard players enable @s CapChoix


scoreboard players set @s QUEST_DAILY_BANQUE 0
scoreboard players set @s CAP_BANQUE_TIMER 0
scoreboard players set @s CAP_BANQUE_SCEAU 0
scoreboard players set @s CAP_CD_BANQUE 0
scoreboard players set @s CAP_DAILY_BANQUE_EVER 0
scoreboard players set @s CAP_DISC_CERCLE 0
scoreboard players set @s CAP_DISC_BANQUE 0
scoreboard players set @s CAP_GUIDE_LOCK 0
scoreboard players set @s CAP_GUIDE_ID 0
scoreboard players set @s CAP_GUIDE_MISS_T 0
clear @s minecraft:written_book[minecraft:custom_model_data={strings:['documents_banque_scelles']}]
clear @s minecraft:written_book[minecraft:custom_model_data={strings:['documents_banque_lus']}]
scoreboard players set @s QUEST_DIVERS_LAMPE 0
scoreboard players set @s CAP_GLOBE_DONE 0
scoreboard players set @s CAP_GLOBE_L1 0
scoreboard players set @s CAP_GLOBE_L2 0
scoreboard players set @s CAP_GLOBE_L3 0
scoreboard players set @s CAP_GLOBE_L4 0
scoreboard players set @s CAP_GLOBE_L5 0
scoreboard players set @s CAP_GLOBE_L6 0
scoreboard players set @s CAP_GLOBE_L7 0
scoreboard players set @s CAP_GLOBE_L8 0
scoreboard players set @s CAP_DISC_PROFONDEURS 0

# 1.5.4 alpha 5 hotfix 10 — reset CapSkills local
scoreboard players set @s CAPSK_FIRST_BANQUE 0
scoreboard players set @s CAPSK_FIRST_BEER 0
scoreboard players set @s CAPSK_FIRST_RESSORT 0
scoreboard players set @s CAPSK_FIRST_TEMOINS 0
scoreboard players set @s CAPSK_FIRST_REGISTRE 0
scoreboard players set @s CAPSK_FIRST_RELAIS 0
scoreboard players set @s CAPSK_FIRST_GLOBE 0
scoreboard players set @s CAPSK_REWARD_SPAWN 0
scoreboard players set @s CAPSK_REWARD_REGPORT 0
scoreboard players set @s CAPSK_REWARD_AURELE 0
scoreboard players set @s CAPSK_REWARD_PROF 0
scoreboard players set @s CAPSK_REWARD_LIBRAIRE 0
scoreboard players set @s CAPSK_REWARD_DETTE 0
scoreboard players set @s CAPSK_REWARD_VN1 0
scoreboard players set @s CAPSK_REWARD_VN2 0
scoreboard players set @s CAPSK_REWARD_VN3 0
scoreboard players set @s CAPSK_REWARD_VN4 0
