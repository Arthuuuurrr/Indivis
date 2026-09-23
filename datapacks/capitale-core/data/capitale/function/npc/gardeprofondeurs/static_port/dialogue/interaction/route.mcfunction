execute if score @s CAP_RANGCLASSE matches 100..999 run function capitale:npc/gardeprofondeurs/static_port/dialogue/interaction/empereur/route
execute if score @s CAP_RANGCLASSE matches 90..99 run function capitale:npc/gardeprofondeurs/static_port/dialogue/interaction/dignitaire/route
execute if score @s CAP_RANGCLASSE matches 80..89 run function capitale:npc/gardeprofondeurs/static_port/dialogue/interaction/agent/route
execute if score @s CAP_RANGCLASSE matches 60..79 run function capitale:npc/gardeprofondeurs/static_port/dialogue/interaction/noble/route
execute if score @s CAP_RANGCLASSE matches 50..59 run function capitale:npc/gardeprofondeurs/static_port/dialogue/interaction/notable/route
execute if score @s CAP_RANGCLASSE matches 30..49 run function capitale:npc/gardeprofondeurs/static_port/dialogue/interaction/commun/route
execute if score @s CAP_RANGCLASSE matches ..29 run function capitale:npc/gardeprofondeurs/static_port/dialogue/interaction/etranger/route
