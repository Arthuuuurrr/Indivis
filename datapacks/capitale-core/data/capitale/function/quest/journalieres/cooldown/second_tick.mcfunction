scoreboard players remove @a[scores={CAP_CD_BEER_GC=1..}] CAP_CD_BEER_GC 1
scoreboard players remove @a[scores={CAP_CD_REGISTRE_PORT=1..}] CAP_CD_REGISTRE_PORT 1
scoreboard players remove @a[scores={CAP_CD_RELAIS_COEUR=1..}] CAP_CD_RELAIS_COEUR 1
scoreboard players remove @a[scores={CAP_CD_RESSORT_CLOCHER=1..}] CAP_CD_RESSORT_CLOCHER 1
scoreboard players remove @a[scores={CAP_CD_TEMOIGNAGES_QUAIS=1..}] CAP_CD_TEMOIGNAGES_QUAIS 1
scoreboard players remove @a[scores={CAP_CD_GLOBE=1..}] CAP_CD_GLOBE 1
execute as @a[scores={CAP_CD_BEER_GC=0,QUEST_DAILY_BEER_GC=100..}] run scoreboard players set @s QUEST_DAILY_BEER_GC 0
