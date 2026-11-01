execute as @a[tag=!hcq_v2_ready] run function hc_quetes:migration/initialize_self
# --- choix de dialogue
execute as @a[scores={hcq_choix=1..}] run function hc_quetes:choix/dispatch
# --- clic sur un PNJ cable par trigger (action Easy NPC : trigger hcq_pnj set N)
execute as @a[scores={hcq_pnj=1..}] at @s run function hc_quetes:pnj/dispatch_trigger
scoreboard players enable @a hcq_pnj
# --- kills comptes par le gestionnaire de spawns (le handler remet le compteur a 0)
execute as @a[scores={hcc_nans=1..},predicate=hc_quetes:migration/state/arc_h/1] run function hc_quetes:hector/q1/kill_nans
execute as @a[scores={hcc_portefeu=1..},predicate=hc_quetes:migration/state/sqvcso02/1_to_2] run function hc_quetes:rosalinde/kill_portefeu
execute as @a[scores={hcc_rayee=1..},predicate=hc_quetes:migration/state/arc_m/1_to_3] run function hc_quetes:maelle/q3/kill_rayee
execute as @a[scores={hcc_corvin=1..},predicate=hc_quetes:migration/state/arc_h/3_to_6] run function hc_quetes:hector/q5/kill_corvin
execute as @a[scores={hcc_geolier=1..},predicate=hc_quetes:migration/state/arc_a/5_to_6] run function hc_quetes:aurele/q8/kill_geolier
# Fenetre elargie a 5..6 : Vasca est un point permanent, et a l.etape 6 le re-tuer est le seul
# recours du joueur qui a perdu l.insigne (defaut remonte en jeu). kill_vasca est rejouable.
execute as @a[scores={hcc_vasca=1..},predicate=hc_quetes:migration/state/arc_a/5_to_6] run function hc_quetes:aurele/q8/kill_vasca
# la sorciere tuee avant ou apres le choix devant Aveline : sa mort vaut choix de l.acier (b19)
execute as @a[scores={hcc_aveline=1..},predicate=hc_quetes:migration/state/arc_m/5_to_7] run function hc_quetes:maelle/q7/kill_aveline
execute as @a[scores={hcc_brehal=1..},predicate=hc_quetes:migration/state/arc_h/8] run function hc_quetes:hector/q9/kill_brehal
execute as @a[scores={hcc_veilleur=1..},predicate=hc_quetes:migration/state/arc_h/8] run function hc_quetes:hector/q9/kill_veilleur
execute as @a[scores={hcc_lauzac=1..},predicate=hc_quetes:migration/state/arc_h/11] run function hc_quetes:hector/q11/kill_lauzac
# la bete tuee avant les nids est memorisee (hcq_cqvchr02_o1) : les deux ordres valident (b19)
execute as @a[scores={hcc_v_alpha=1..},predicate=hc_quetes:migration/state/arc_v/20_to_22] run function hc_quetes:malaury/b/kill_alpha
execute as @a[scores={hcc_guenole=1..},predicate=hc_quetes:migration/state/arc_p/2_to_3] run function hc_quetes:patrouille/kill_guenole
execute as @a[scores={hcc_mahe=1..},predicate=hc_quetes:migration/state/arc_p/2_to_3] run function hc_quetes:patrouille/kill_mahe
execute as @a[scores={hcc_portier=1..},predicate=hc_quetes:migration/state/arc_p/2_to_20] run function hc_quetes:patrouille/kill_portier
# la Bete blanche tuee avant d.avoir parle a Gwenn compte aussi (b19)
execute as @a[scores={hcc_bete=1..},predicate=hc_quetes:migration/state/arc_n/4_to_5] run function hc_quetes:carrec/kill_bete
execute as @a[scores={hcc_malaquin=1..},predicate=hc_quetes:migration/state/arc_n/20] run function hc_quetes:carrec/b/kill_malaquin
execute as @a[scores={hcc_malaquin=1..},predicate=hc_quetes:migration/state/arc_n/40] run function hc_quetes:carrec/a/kill_malaquin
execute as @a[scores={hcc_carn=1..},predicate=hc_quetes:migration/state/arc_n/40] run function hc_quetes:carrec/a/kill_carn
execute as @a[scores={hcc_malaquin=1..},predicate=hc_quetes:migration/state/arc_n/62] run function hc_quetes:carrec/c/kill_malaquin
execute as @a[scores={hcc_marcheuse=1..},predicate=hc_quetes:migration/state/arc_c/4] run function hc_quetes:bannieres/kill_marcheuse
execute as @a[scores={hcc_marcheur=1..},predicate=hc_quetes:migration/state/arc_c/4_to_5] run function hc_quetes:bannieres/kill_marcheur
execute as @a[scores={hcc_marcheur=1..},predicate=hc_quetes:migration/state/arc_c/43_to_49] run function hc_quetes:bannieres/a/kill_marcheur
# --- lecture d'un lutrin dans le rayon d'un lieu (statistique hcm_lectern, purgee plus bas)
execute as @a[scores={hcm_lectern=1..},predicate=hc_quetes:migration/state/arc_v/1] if data storage hc_quetes:lieux archives_lutrin if function hc_quetes:prox/archives_lutrin run function hc_quetes:malaury/q13/lit_livre
execute as @a[scores={hcm_lectern=1..},predicate=hc_quetes:migration/state/arc_v/41] if data storage hc_quetes:lieux archives_registres if function hc_quetes:prox/archives_registres run function hc_quetes:malaury/a/lit_role
# --- un kill hors quete ne se garde pas
scoreboard players set @a[scores={hcc_nans=1..}] hcc_nans 0
scoreboard players set @a[scores={hcc_portefeu=1..}] hcc_portefeu 0
scoreboard players set @a[scores={hcc_rayee=1..}] hcc_rayee 0
scoreboard players set @a[scores={hcc_corvin=1..}] hcc_corvin 0
scoreboard players set @a[scores={hcc_geolier=1..}] hcc_geolier 0
scoreboard players set @a[scores={hcc_vasca=1..}] hcc_vasca 0
scoreboard players set @a[scores={hcc_aveline=1..}] hcc_aveline 0
scoreboard players set @a[scores={hcc_brehal=1..}] hcc_brehal 0
scoreboard players set @a[scores={hcc_veilleur=1..}] hcc_veilleur 0
scoreboard players set @a[scores={hcc_lauzac=1..}] hcc_lauzac 0
scoreboard players set @a[scores={hcc_v_alpha=1..}] hcc_v_alpha 0
scoreboard players set @a[scores={hcc_guenole=1..}] hcc_guenole 0
scoreboard players set @a[scores={hcc_mahe=1..}] hcc_mahe 0
scoreboard players set @a[scores={hcc_portier=1..}] hcc_portier 0
scoreboard players set @a[scores={hcc_bete=1..}] hcc_bete 0
scoreboard players set @a[scores={hcc_malaquin=1..}] hcc_malaquin 0
scoreboard players set @a[scores={hcc_carn=1..}] hcc_carn 0
scoreboard players set @a[scores={hcc_marcheur=1..}] hcc_marcheur 0
scoreboard players set @a[scores={hcc_marcheuse=1..}] hcc_marcheuse 0
# hcm_lectern est une statistique cumulative : sans cette purge, la lecture resterait vraie a chaque tick.
scoreboard players set @a[scores={hcm_lectern=1..}] hcm_lectern 0
# --- proximite (lieux poses par /function hc_quetes:pose/lieu/...)
execute as @a[scores={hcq_sqvccr01_o1=0},predicate=hc_quetes:migration/state/arc_h/1] if data storage hc_quetes:lieux anselin if function hc_quetes:prox/anselin run function hc_quetes:hector/q1/prox_anselin
execute as @a[scores={hcq_sqvcso02_o1=0},predicate=hc_quetes:migration/state/sqvcso02/2] if data storage hc_quetes:lieux tour_malecombe if function hc_quetes:prox/tour_malecombe run function hc_quetes:rosalinde/prox_tour
execute as @a[predicate=hc_quetes:migration/state/arc_m/1] if data storage hc_quetes:lieux camp_bucherons if function hc_quetes:prox/camp_bucherons run function hc_quetes:maelle/q3/prox_camp
execute as @a[scores={hcq_cqvcra01_o1=0},predicate=hc_quetes:migration/state/arc_m/2] if data storage hc_quetes:lieux indice_a if function hc_quetes:prox/indice_a run function hc_quetes:maelle/q3/prox_a
execute as @a[scores={hcq_cqvcra01_o1=1},predicate=hc_quetes:migration/state/arc_m/2] if data storage hc_quetes:lieux indice_b if function hc_quetes:prox/indice_b run function hc_quetes:maelle/q3/prox_b
execute as @a[scores={hcq_cqvcra01_o1=2},predicate=hc_quetes:migration/state/arc_m/2] if data storage hc_quetes:lieux chantier if function hc_quetes:prox/chantier run function hc_quetes:maelle/q3/prox_chantier
execute as @a[scores={hcq_sqvcso01_o1=0},predicate=hc_quetes:migration/state/sqvcso01/1] if data storage hc_quetes:lieux gue_segre if function hc_quetes:prox/gue_segre run function hc_quetes:tobie/prox_gue
execute as @a[scores={hcq_sqvccr02_o2=0},predicate=hc_quetes:migration/state/arc_h/4_to_5] if data storage hc_quetes:lieux tour_guetbas if function hc_quetes:prox/tour_guetbas run function hc_quetes:hector/q5/prox_tour
execute as @a[scores={hcq_sqvccr02_o2=1},predicate=hc_quetes:migration/state/arc_h/5] if data storage hc_quetes:lieux piste_bottes if function hc_quetes:prox/piste_bottes run function hc_quetes:hector/q5/prox_bottes
execute as @a[scores={hcq_sqvccr02_o2=2},predicate=hc_quetes:migration/state/arc_h/5] if data storage hc_quetes:lieux piste_bouclier if function hc_quetes:prox/piste_bouclier run function hc_quetes:hector/q5/prox_bouclier
execute as @a[scores={hcq_sqvccr02_o2=3},predicate=hc_quetes:migration/state/arc_h/5] if data storage hc_quetes:lieux piste_cairn if function hc_quetes:prox/piste_cairn run function hc_quetes:hector/q5/prox_cairn
execute as @a[scores={hcq_sqvcep01_o1=0},predicate=hc_quetes:migration/state/arc_a/2] if data storage hc_quetes:lieux epave if function hc_quetes:prox/epave run function hc_quetes:aurele/q6/prox_epave
execute as @a[scores={hcq_sqvcra02_o1=0},predicate=hc_quetes:migration/state/arc_m/5] if data storage hc_quetes:lieux cabane if function hc_quetes:prox/cabane run function hc_quetes:maelle/q7/prox_cabane
execute as @a[scores={hcq_sqvccr03_o2=0},predicate=hc_quetes:migration/state/arc_h/8] if data storage hc_quetes:lieux fosses_cages if function hc_quetes:prox/fosses_cages run function hc_quetes:hector/q9/prox_cages
execute as @a[scores={hcq_sqvccr03_o2=1},predicate=hc_quetes:migration/state/arc_h/8] if data storage hc_quetes:lieux fosses_fosse if function hc_quetes:prox/fosses_fosse run function hc_quetes:hector/q9/prox_fosse
execute as @a[scores={hcq_sqvccr03_o2=2},predicate=hc_quetes:migration/state/arc_h/8] if data storage hc_quetes:lieux fosses_tombe if function hc_quetes:prox/fosses_tombe run function hc_quetes:hector/q9/prox_tombe
execute as @a[scores={hcq_sqvccr04_o5=0},predicate=hc_quetes:migration/state/sqvccr04/1_to_2] if data storage hc_quetes:lieux sculpteur if function hc_quetes:prox/sculpteur run function hc_quetes:anselme/prox_sculpteur
execute as @a[scores={hcq_sqvccr04_o2=0},predicate=hc_quetes:migration/state/sqvccr04/2] if data storage hc_quetes:lieux ronces if function hc_quetes:prox/ronces run function hc_quetes:anselme/prox_ronces
execute as @a[scores={hcq_sqvccr05_o3=0},predicate=hc_quetes:migration/state/arc_h/11_to_12] if data storage hc_quetes:lieux sanctum if function hc_quetes:prox/sanctum run function hc_quetes:hector/q11/prox_sanctum
execute as @a[predicate=hc_quetes:migration/state/arc_h/11_to_13,tag=!hcq_porte] if data storage hc_quetes:lieux porte_bronze if function hc_quetes:prox/porte_bronze if items entity @s container.* *[custom_data~{hc_quete:"cqvcep02_cle_bronze"}] run function hc_quetes:hector/q11/porte_ouvre
execute as @a[predicate=hc_quetes:migration/state/arc_v/20] if data storage hc_quetes:lieux egouts_entree if function hc_quetes:prox/egouts_entree run function hc_quetes:malaury/b/prox_collecteur
execute as @a[predicate=hc_quetes:migration/state/arc_p/1] if data storage hc_quetes:lieux arche_moussue if function hc_quetes:prox/arche_moussue run function hc_quetes:patrouille/prox_arche
execute as @a[predicate=hc_quetes:migration/state/arc_p/4] if data storage hc_quetes:lieux perchoir if function hc_quetes:prox/perchoir run function hc_quetes:patrouille/prox_perchoir
execute as @a[predicate=hc_quetes:migration/state/arc_n/3] if data storage hc_quetes:lieux ossements if function hc_quetes:prox/ossements run function hc_quetes:carrec/prox_ossements
execute as @a[scores={hcq_cqvcbb01_o2=0},predicate=hc_quetes:migration/state/arc_n/8] if data storage hc_quetes:lieux carrec_note if function hc_quetes:prox/carrec_note run function hc_quetes:carrec/prox_carrec
execute as @a[predicate=hc_quetes:migration/state/arc_c/2] if data storage hc_quetes:lieux pavillon if function hc_quetes:prox/pavillon run function hc_quetes:bannieres/prox_pavillon
# Rattrapage : un joueur deja passe a l.etape 5+ a forcement eu sa nuit — on referme le test pour lui.
execute as @a[scores={hcq_sqvcbn01_o4=0},predicate=hc_quetes:migration/state/arc_c/5_to_] run scoreboard players set @s hcq_sqvcbn01_o4 1
execute as @a[scores={hcq_sqvcbn01_o4=0},predicate=hc_quetes:migration/state/arc_c/4] at @s if data storage hc_quetes:lieux pavillon if function hc_quetes:prox/pavillon run function hc_quetes:bannieres/nuit_test
# --- horloge : balayage anti-echange et journal toutes les 5 s
scoreboard players add #tick hcm_tick 1
execute if score #tick hcm_tick matches 100.. run function hc_quetes:lib/toutes_les_5s
# ---------------------------------------------------------------- Jungle / Sainte-Veille
# Kills comptes par haute_capitale_spawns : le point porte « counter add hcc_<nom> », le mod
# fait +1 a chaque joueur credite, et on ne convertit que DANS la fenetre d'etat de la quete.
# Chaque chaine tient ses propres convertisseurs, et on appelle les leurs, jamais un
# doublon : route/j01 j03 j04 j05 j10 = gen_j_route.sh · route/j07 j08 = gen_j_soins.sh ·
# veille/** = gen_j_cathedrale.sh. Les noms et les fenetres ci-dessous sont ceux que les
# trois generateurs demandent, repris mot pour mot de leurs blocs de raccords.
# J02 n'a pas de ligne ici : les glandes se prennent sur la faune naturelle, comptee par
# l'advancement tue/komodo de gen_j_soins.sh — qui compte aussi les varans du filet.
execute as @a[scores={hcc_rte_raptor=1..},predicate=hc_quetes:migration/state/sqlpjo01/1_to_9] run function hc_quetes:route/j01/kill_raptor
execute as @a[scores={hcc_gob_pillard=1..},predicate=hc_quetes:migration/state/sqlpjo03/1_to_9] run function hc_quetes:route/j03/kill_pillard
execute as @a[scores={hcc_gob_tireur=1..},predicate=hc_quetes:migration/state/sqlpjo03/1_to_9] run function hc_quetes:route/j03/kill_tireur
# Les deux fenetres larges qui suivent sont la correction du piege des deux ordres : le chef
# du camp et le meneur de la meute sont des points permanents, et le joueur peut les tuer
# avant d'accepter la quete qui les demande.
execute as @a[scores={hcc_gob_chef=1..},predicate=hc_quetes:migration/state/sqlpjo03/50_to_,predicate=hc_quetes:migration/state/sqlpjo04/_to_49] run function hc_quetes:route/j04/kill_chef
execute as @a[scores={hcc_camp_raptor=1..},predicate=hc_quetes:migration/state/sqlpjo05/1_to_9] run function hc_quetes:route/j05/kill_adulte
execute as @a[scores={hcc_rte_meneur=1..},predicate=hc_quetes:migration/state/sqlpjo01/50_to_,predicate=hc_quetes:migration/state/sqlpjo05/_to_49] run function hc_quetes:route/j05/kill_meneur
execute as @a[scores={hcc_vl_nef=1..},predicate=hc_quetes:migration/state/sqlpjo06/1_to_4] run function hc_quetes:veille/j06/kill_nef
execute as @a[scores={hcc_source_pred=1..},predicate=hc_quetes:migration/state/sqlpjo07/1_to_9] run function hc_quetes:route/j07/kill_predateur
execute as @a[scores={hcc_gob_pillard=1..},predicate=hc_quetes:migration/state/sqlpjo08/1_to_9] run function hc_quetes:route/j08/kill_garde
execute as @a[scores={hcc_psg_gob=1..},predicate=hc_quetes:migration/state/sqlpjo10/2] run function hc_quetes:route/j10/kill_vague
execute as @a[scores={hcc_psg_raptor=1..},predicate=hc_quetes:migration/state/sqlpjo10/2] run function hc_quetes:route/j10/kill_vague
execute as @a[scores={hcc_psg_marel=1..},predicate=hc_quetes:migration/state/sqlpjo10/20] run function hc_quetes:route/j10/kill_marel
execute as @a[scores={hcc_vl_esprit=1..},predicate=hc_quetes:migration/state/sqlpjo11/1_to_4] run function hc_quetes:veille/j11/kill_esprit
execute as @a[scores={hcc_vl_garde=1..},predicate=hc_quetes:migration/state/sqlpjo12/1_to_4] run function hc_quetes:veille/j12/kill_garde
execute as @a[scores={hcc_vl_gardien=1..},predicate=hc_quetes:migration/state/sqlpjo13/1_to_4] run function hc_quetes:veille/j13/kill_gardien
# Un kill hors quete ne se garde pas : la purge est la contrepartie obligatoire de chaque
# compteur, sinon le meme kill serait recompte a chaque tick.
scoreboard players set @a[scores={hcc_rte_raptor=1..}] hcc_rte_raptor 0
scoreboard players set @a[scores={hcc_camp_raptor=1..}] hcc_camp_raptor 0
scoreboard players set @a[scores={hcc_rte_meneur=1..}] hcc_rte_meneur 0
scoreboard players set @a[scores={hcc_gob_pillard=1..}] hcc_gob_pillard 0
scoreboard players set @a[scores={hcc_gob_tireur=1..}] hcc_gob_tireur 0
scoreboard players set @a[scores={hcc_gob_chef=1..}] hcc_gob_chef 0
scoreboard players set @a[scores={hcc_source_pred=1..}] hcc_source_pred 0
scoreboard players set @a[scores={hcc_couvert_komodo=1..}] hcc_couvert_komodo 0
scoreboard players set @a[scores={hcc_psg_gob=1..}] hcc_psg_gob 0
scoreboard players set @a[scores={hcc_psg_raptor=1..}] hcc_psg_raptor 0
scoreboard players set @a[scores={hcc_psg_marel=1..}] hcc_psg_marel 0
scoreboard players set @a[scores={hcc_vl_nef=1..}] hcc_vl_nef 0
scoreboard players set @a[scores={hcc_vl_esprit=1..}] hcc_vl_esprit 0
scoreboard players set @a[scores={hcc_vl_garde=1..}] hcc_vl_garde 0
scoreboard players set @a[scores={hcc_vl_funer=1..}] hcc_vl_funer 0
scoreboard players set @a[scores={hcc_vl_gardien=1..}] hcc_vl_gardien 0
# Arrivees sur les lieux. Un lieu que le joueur n'a pas enregistre ne declenche rien :
# « if data storage hc_quetes:lieux <nom> » — non configure, donc desactive, et aucune
# quete ne se bloque. Les six premieres lignes appellent les fonctions des chaines, qui
# font avancer l'etat et se gardent elles-memes ; les quatre dernieres sont de simples
# notes d'arrivee pour la cathedrale, donnees une fois par joueur (etiquette hcq_vu_*).
execute as @a[predicate=hc_quetes:migration/state/sqlpjo01/1_to_9] at @s if data storage hc_quetes:lieux j01_route if function hc_quetes:prox/j01_route run function hc_quetes:route/j01/prox_route
execute as @a[predicate=hc_quetes:migration/state/sqlpjo03/1_to_9] at @s if data storage hc_quetes:lieux j03_ruines if function hc_quetes:prox/j03_ruines run function hc_quetes:route/j03/prox_ruines
execute as @a[predicate=hc_quetes:migration/state/sqlpjo05/1_to_9] at @s if data storage hc_quetes:lieux j05_camp if function hc_quetes:prox/j05_camp run function hc_quetes:route/j05/prox_camp
execute as @a[predicate=hc_quetes:migration/state/sqlpjo07/1_to_9] at @s if data storage hc_quetes:lieux j07_source if function hc_quetes:prox/j07_source run function hc_quetes:route/j07/prox_source
execute as @a[predicate=hc_quetes:migration/state/sqlpjo08/1_to_9] at @s if data storage hc_quetes:lieux j08_chemin if function hc_quetes:prox/j08_chemin run function hc_quetes:route/j08/prox_chemin
execute as @a[predicate=hc_quetes:migration/state/sqlpjo06/1_to_9,tag=!hcq_vu_j06_nef] at @s if data storage hc_quetes:lieux j06_nef if function hc_quetes:prox/j06_nef run function hc_quetes:jsocle/prox/j06_nef
execute as @a[predicate=hc_quetes:migration/state/sqlpjo11/1_to_9,tag=!hcq_vu_j11_galerie] at @s if data storage hc_quetes:lieux j11_galerie if function hc_quetes:prox/j11_galerie run function hc_quetes:jsocle/prox/j11_galerie
execute as @a[predicate=hc_quetes:migration/state/sqlpjo12/1_to_9,tag=!hcq_vu_j12_travees] at @s if data storage hc_quetes:lieux j12_travees if function hc_quetes:prox/j12_travees run function hc_quetes:jsocle/prox/j12_travees
execute as @a[predicate=hc_quetes:migration/state/sqlpjo13/1_to_9,tag=!hcq_vu_j13_funeraire] at @s if data storage hc_quetes:lieux j13_funeraire if function hc_quetes:prox/j13_funeraire run function hc_quetes:jsocle/prox/j13_funeraire
# --- marqueurs ! et ? au-dessus des PNJ de quete : une seconde suffit a suivre un clic
scoreboard players add #tick20 hcm_tick 1
execute if score #tick20 hcm_tick matches 20.. run scoreboard players set #tick20 hcm_tick 0
execute if score #tick20 hcm_tick matches 0 run function hc_quetes:marqueurs/tick
