# Capitale Skills 0.9.0-rc2 HIT_FEEDBACK_SHADOW_ITEM - load
# Datapack séparé : ne modifie pas Capitale Core, mais réutilise CAP_PVP si présent.
scoreboard objectives add CAPSK_TMP dummy
scoreboard objectives add CAPSK_CLOCK dummy
scoreboard objectives add CAPSK_RUNTIME_VER dummy
scoreboard objectives add CAPSK_SYNC_DIRTY dummy
scoreboard objectives add CAPSK_RAY_STEP dummy
scoreboard objectives add CAPSK_SEC_RANK dummy
scoreboard objectives add CAPSK_ALCH_RANK dummy
scoreboard objectives add CAPSK_REMP_RANK dummy
scoreboard objectives add CAPSK_HEAL_CD dummy
scoreboard objectives add CAPSK_ZONE_CD dummy
scoreboard objectives add CAPSK_AEGIS_CD dummy
scoreboard objectives add CAPSK_REMP_SELF_CD dummy
scoreboard objectives add CAPSK_REMP_TARGET_CD dummy
scoreboard objectives add CAPSK_REMP_ZONE_CD dummy
scoreboard objectives add CAPSK_TAUNT_CD dummy
scoreboard objectives add CAPSK_REMP_ROOT_CD dummy
scoreboard objectives add CAPSK_REMP_ROOT_VIS dummy
scoreboard objectives add CAPSK_ALCH_CD dummy
scoreboard objectives add CAPSK_SELF_CD dummy
scoreboard objectives add CAPSK_MAGIE_CD dummy
scoreboard objectives add CAPSK_MAG_RANK dummy
scoreboard objectives add CAPSK_SUPPORT_RANK dummy
scoreboard objectives add CAPSK_BASTION_RANK dummy
scoreboard objectives add CAPSK_EGIDE_RANK dummy
scoreboard objectives add CAPSK_BRISE_RANK dummy
scoreboard objectives add CAPSK_MAG_DPS_RANK dummy
scoreboard objectives add CAPSK_MAG_CTL_RANK dummy
scoreboard objectives add CAPSK_MAG_RES_RANK dummy
scoreboard objectives add CAPSK_MAG_SUP_RANK dummy
scoreboard objectives add CAPSK_HP health
scoreboard objectives add CAP_PVP dummy
scoreboard objectives add CAP_PVP_TIMER dummy
scoreboard players set #clock CAPSK_CLOCK 0
scoreboard players set #slow CAPSK_CLOCK 0
scoreboard players set #audit CAPSK_CLOCK 0
scoreboard players set #hit CAPSK_TMP 0
scoreboard players set #block CAPSK_TMP 0

scoreboard objectives add CAPSK_XP dummy
scoreboard objectives add CAPSK_LEVEL dummy
scoreboard objectives add CAPSK_SELF_UNLOCK dummy

scoreboard objectives add CAPSK_RAFALE_CD dummy
scoreboard objectives add CAPSK_LAME_CD dummy
scoreboard objectives add CAPSK_OMBRESTEP_CD dummy
scoreboard objectives add CAPSK_OMBRE_INT_CD dummy

scoreboard objectives add CAPSK_RAFALE_CHARGE dummy
scoreboard objectives add CAPSK_RAFALE_VIS dummy
scoreboard objectives add CAPSK_RAFALE_FORCE_VIS dummy
scoreboard objectives add CAPS_A_MELEE dummy
scoreboard objectives add CAPS_A_RANGE dummy
scoreboard objectives add CAPS_A_MAGIC dummy
scoreboard objectives add CAPS_A_BOWSPD dummy
scoreboard objectives add CAPS_A_HEAL dummy
scoreboard objectives add CAPS_A_RES dummy
scoreboard objectives add CAPS_A_MRES dummy
scoreboard objectives add CAPS_A_KB dummy
scoreboard objectives add CAPS_A_HP dummy
scoreboard objectives add CAPS_A_FALL dummy
scoreboard objectives add CAPS_A_STEALTH dummy
scoreboard objectives add CAPS_A_SPRINT dummy
scoreboard objectives add CAPS_A_STAM dummy
scoreboard objectives add CAPS_A_XP dummy
scoreboard objectives add CAPS_A_REPAIR dummy
scoreboard objectives add CAPS_A_MINE dummy
scoreboard objectives add CAPS_A_ASHRED dummy
scoreboard objectives add CAPS_A_MSHRED dummy

# 0.8.19 : fallback datapack si le hook mod arc ne se déclenche pas.
scoreboard objectives add CAPSK_BOW_USED minecraft.used:minecraft.bow
# 0.9.16 : fallback de consommation du tir d'arbalète si le hook projectile rate ou si le carreau manque.
scoreboard objectives add CAPSK_CROSS_USED minecraft.used:minecraft.crossbow
scoreboard objectives add CAPSK_CROSS_SHOT_T dummy
scoreboard objectives add CAPSK_CROSS_PROJ_AGE dummy
scoreboard objectives add CAPSK_CROSS_DIRECT_HIT dummy
scoreboard objectives add CAPSK_CROSS_DIRECT_DBG dummy
scoreboard objectives add CAPSK_CROSS_ADV_T dummy
scoreboard objectives add CAPSK_BOW_LCLICK dummy
scoreboard objectives add CAPSK_CBOW_LCLK dummy
scoreboard objectives add CAPSK_RNG_LCLK_T dummy
scoreboard objectives add CAPSK_RNG_LCLK_B dummy
scoreboard objectives add CAPSK_ARBA_MARK_CD dummy
scoreboard objectives add CAPSK_ARBA_MARK_T dummy
scoreboard objectives add CAPSK_ARBA_VULN_T dummy

# 0.9.40 : Chaman / vrai Fétiche moddé + totems.
scoreboard objectives add CAPSK_CHAMAN_USE minecraft.used:minecraft.carrot_on_a_stick
scoreboard objectives add CAPSK_CHAMAN_TOTEM_CD dummy
scoreboard objectives add CAPSK_CHAMAN_TOTEM_T dummy
scoreboard objectives add CAPSK_CHAMAN_PULSE dummy
scoreboard objectives add CAPSK_CHAMAN_OWNER dummy
scoreboard objectives add CAPSK_UID dummy
scoreboard players add #next CAPSK_UID 0

scoreboard objectives add CAPSK_ALCH_CLAIM trigger
scoreboard objectives add CAPSK_TIME dummy
scoreboard objectives add CAPSK_ALCH_LAST dummy
scoreboard objectives add CAPSK_ALCH_ELAPSED dummy
scoreboard objectives add CAPSK_LOGIN_INIT dummy
scoreboard objectives add CAPSK_LEAVE_SEEN dummy
scoreboard objectives add CAPSK_LEAVES minecraft.custom:minecraft.leave_game
scoreboard objectives add CAPSK_MINE_COAL minecraft.mined:minecraft.coal_ore
scoreboard objectives add CAPSK_MINE_DCOAL minecraft.mined:minecraft.deepslate_coal_ore
scoreboard objectives add CAPSK_MINE_COPPER minecraft.mined:minecraft.copper_ore
scoreboard objectives add CAPSK_MINE_DCOPPER minecraft.mined:minecraft.deepslate_copper_ore
scoreboard objectives add CAPSK_MINE_IRON minecraft.mined:minecraft.iron_ore
scoreboard objectives add CAPSK_MINE_DIRON minecraft.mined:minecraft.deepslate_iron_ore
scoreboard objectives add CAPSK_MINE_GOLD minecraft.mined:minecraft.gold_ore
scoreboard objectives add CAPSK_MINE_DGOLD minecraft.mined:minecraft.deepslate_gold_ore
scoreboard objectives add CAPSK_MINE_REDS minecraft.mined:minecraft.redstone_ore
scoreboard objectives add CAPSK_MINE_DREDS minecraft.mined:minecraft.deepslate_redstone_ore
scoreboard objectives add CAPSK_MINE_LAPIS minecraft.mined:minecraft.lapis_ore
scoreboard objectives add CAPSK_MINE_DLAPIS minecraft.mined:minecraft.deepslate_lapis_ore
scoreboard objectives add CAPSK_MINE_DIAM minecraft.mined:minecraft.diamond_ore
scoreboard objectives add CAPSK_MINE_DDIAM minecraft.mined:minecraft.deepslate_diamond_ore
scoreboard objectives add CAPSK_MINE_EMER minecraft.mined:minecraft.emerald_ore
scoreboard objectives add CAPSK_MINE_DEMER minecraft.mined:minecraft.deepslate_emerald_ore
scoreboard objectives add CAPSK_MINE_QUARTZ minecraft.mined:minecraft.nether_quartz_ore
scoreboard objectives add CAPSK_MINE_NGOLD minecraft.mined:minecraft.nether_gold_ore


# 0.8.29 : cooldowns actifs avancés.
scoreboard objectives add CAPSK_ALTER_TARGET_CD dummy
scoreboard objectives add CAPSK_ALTER_LEV_CD dummy
scoreboard objectives add CAPSK_MAG_DOT_CD dummy
scoreboard objectives add CAPSK_MAGE_CONTACT_CD dummy
scoreboard objectives add CAPSK_C_IRON_SWORD minecraft.crafted:minecraft.iron_sword
scoreboard objectives add CAPSK_C_IRON_AXE minecraft.crafted:minecraft.iron_axe
scoreboard objectives add CAPSK_C_IRON_PICK minecraft.crafted:minecraft.iron_pickaxe
scoreboard objectives add CAPSK_C_IRON_SHOV minecraft.crafted:minecraft.iron_shovel
scoreboard objectives add CAPSK_C_IRON_HOE minecraft.crafted:minecraft.iron_hoe
scoreboard objectives add CAPSK_C_IRON_HELM minecraft.crafted:minecraft.iron_helmet
scoreboard objectives add CAPSK_C_IRON_CHEST minecraft.crafted:minecraft.iron_chestplate
scoreboard objectives add CAPSK_C_IRON_LEGS minecraft.crafted:minecraft.iron_leggings
scoreboard objectives add CAPSK_C_IRON_BOOTS minecraft.crafted:minecraft.iron_boots
scoreboard objectives add CAPSK_C_DIAM_SWORD minecraft.crafted:minecraft.diamond_sword
scoreboard objectives add CAPSK_C_DIAM_AXE minecraft.crafted:minecraft.diamond_axe
scoreboard objectives add CAPSK_C_DIAM_PICK minecraft.crafted:minecraft.diamond_pickaxe
scoreboard objectives add CAPSK_C_DIAM_SHOV minecraft.crafted:minecraft.diamond_shovel
scoreboard objectives add CAPSK_C_DIAM_HOE minecraft.crafted:minecraft.diamond_hoe
scoreboard objectives add CAPSK_C_DIAM_HELM minecraft.crafted:minecraft.diamond_helmet
scoreboard objectives add CAPSK_C_DIAM_CHEST minecraft.crafted:minecraft.diamond_chestplate
scoreboard objectives add CAPSK_C_DIAM_LEGS minecraft.crafted:minecraft.diamond_leggings
scoreboard objectives add CAPSK_C_DIAM_BOOTS minecraft.crafted:minecraft.diamond_boots
scoreboard objectives add CAPSK_C_BOW minecraft.crafted:minecraft.bow
scoreboard objectives add CAPSK_C_CROSSBOW minecraft.crafted:minecraft.crossbow
scoreboard objectives add CAPSK_C_SHIELD minecraft.crafted:minecraft.shield
scoreboard objectives add CAPSK_F_WHEAT minecraft.mined:minecraft.wheat
scoreboard objectives add CAPSK_F_CARROTS minecraft.mined:minecraft.carrots
scoreboard objectives add CAPSK_F_POTATOES minecraft.mined:minecraft.potatoes
scoreboard objectives add CAPSK_F_BEETS minecraft.mined:minecraft.beetroots
scoreboard objectives add CAPSK_F_NWART minecraft.mined:minecraft.nether_wart
scoreboard objectives add CAPSK_F_COCOA minecraft.mined:minecraft.cocoa
scoreboard objectives add CAPSK_F_MELON minecraft.mined:minecraft.melon
scoreboard objectives add CAPSK_F_PUMPKIN minecraft.mined:minecraft.pumpkin

scoreboard objectives add CAPS_P_MELEE dummy
scoreboard objectives add CAPS_P_RANGE dummy
scoreboard objectives add CAPS_P_MAGIC dummy
scoreboard objectives add CAPS_P_BOWSPD dummy
scoreboard objectives add CAPS_P_HEAL dummy
scoreboard objectives add CAPS_P_RES dummy
scoreboard objectives add CAPS_P_MRES dummy
scoreboard objectives add CAPS_P_FALL dummy
scoreboard objectives add CAPS_P_SPRINT dummy
scoreboard objectives add CAPS_P_XP dummy
scoreboard objectives add CAPS_P_REPAIR dummy
scoreboard objectives add CAPS_P_MINE dummy
scoreboard objectives add CAPS_P_HP dummy
scoreboard objectives add CAPS_P_MSHRED dummy
scoreboard objectives add CAPS_P_STAM dummy

# 0.8.30-rc1 : objectifs profil tag-based.
scoreboard objectives add CAPS_P_KB dummy
scoreboard objectives add CAPS_P_STEALTH dummy
scoreboard objectives add CAPS_P_ASHRED dummy
scoreboard objectives add CAPS_P_SHIELD dummy
# 0.8.30-rc4 : Ombre/craft civil.
scoreboard objectives add CAPSK_SHADOW_CLAIM trigger
scoreboard objectives add CAPSK_SHADOW_LAST dummy
scoreboard objectives add CAPSK_SHADOW_ELAPSED dummy
scoreboard objectives add CAPSK_ALTER_CTL_RANK dummy
scoreboard objectives add CAPSK_ANVIL_CD dummy

# 0.9.0-rc1 : Rafale en salve espacée.
scoreboard objectives add CAPSK_RAFALE_BURST_T dummy
scoreboard objectives add CAPSK_RAFALE_BURST_SHOT dummy

# 0.9.0-rc2 : mod beta 1.2.0 + Sceau des Ombres + hit feedback.
# 0.9.3 : socle futur arbalète anti-armure / salve préparée.
scoreboard objectives add CAPSK_CROSS_CD dummy
scoreboard objectives add CAPSK_CROSS_CHARGE dummy
scoreboard objectives add CAPSK_CROSS_READY dummy

# 0.9.4 : Exécution conditionnelle épée.
scoreboard objectives add CAPSK_EXEC_CD dummy
scoreboard objectives add CAPSK_DUAL_PASS_CD dummy
scoreboard objectives add CAPSK_DUAL_TMP dummy

# 0.9.57 : sorts réintroduits.
scoreboard objectives add CAPSK_LAME_TOURB_CD dummy
scoreboard objectives add CAPSK_BOW_ENTRAVE_CD dummy
scoreboard objectives add CAPSK_BOW_ENTRAVE_T dummy
scoreboard objectives add CAPSK_OMBRE_SORTIE_T dummy

# 0.9.50 : cooldown Poigne du Rempart.
scoreboard objectives add CAPSK_REMP_PULL_CD dummy

# 0.9.65 EXP : compat Better Combat / Combat Roll + Tourbillon hook fix.
scoreboard objectives add CAPSK_LAME_TOURB_ARM_T dummy
scoreboard objectives add CAPSK_CR_DIST dummy
scoreboard objectives add CAPSK_CR_RECH dummy
scoreboard objectives add CAPSK_CR_COUNT dummy

# 0.9.68 EXP : Tourbillon actif AoE sans dépendre du hit Better Combat.
scoreboard objectives add CAPSK_LAME_TOURB_SPIN_T dummy
scoreboard objectives add CAPSK_LAME_TOURB_PULSE dummy
# 0.9.69 EXP : canalisation du Tourbillon avant relâchement AoE.
scoreboard objectives add CAPSK_LAME_TOURB_CHAN_T dummy
# 0.9.76 EXP : diagnostics Better Combat/Tourbillon sécurisés.
scoreboard objectives add CAPSK_BC_DIAG_T dummy
scoreboard objectives add CAPSK_TOURB_DIAG dummy

# 0.9.78 EXP : diagnostic dur Tourbillon canalise.
scoreboard objectives add CAPSK_LAME_TOURB_PHASE dummy
scoreboard objectives add CAPSK_LAME_TOURB_TARGETS dummy

# 0.9.123 : Lance longue, rafale monocible synchronisée.
scoreboard objectives add CAPSK_LANCE_CD dummy
scoreboard objectives add CAPSK_LANCE_PHASE dummy
scoreboard objectives add CAPSK_LANCE_HITS dummy
scoreboard objectives add CAPSK_LANCE_RAY dummy
scoreboard objectives add CAPSK_LANCE_OWNER dummy
scoreboard objectives add CAPSK_DASH_OWNER dummy

# 0.9.144 — revolvers hitscan.
scoreboard objectives add CAPREV_COOLDOWN dummy
scoreboard objectives add CAPREV_RELOAD dummy
scoreboard objectives add CAPREV_ALT dummy
scoreboard objectives add CAPREV_MAIN dummy
scoreboard objectives add CAPREV_OFF dummy
scoreboard objectives add CAPREV_SELECTED dummy
scoreboard objectives add CAPREV_RAY dummy
scoreboard objectives add CAPREV_RANGE dummy
scoreboard objectives add CAPREV_SPREAD dummy
scoreboard objectives add CAPREV_HIT dummy
scoreboard objectives add CAPREV_STATE dummy
scoreboard objectives add CAPREV_FIRED dummy
scoreboard objectives add CAPREV_MOTION_X dummy
scoreboard objectives add CAPREV_MOTION_Z dummy
