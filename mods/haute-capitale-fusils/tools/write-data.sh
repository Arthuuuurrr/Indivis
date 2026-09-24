#!/usr/bin/env bash
# Écrit les fichiers de données du mod (profils, munitions, modifications, variantes).
set -e
cd "$(dirname "$0")/../src/main/resources/data/haute_capitale_fusils"
P=fusil_profil; M=fusil_munition; D=fusil_modification; V=fusil_variante
mkdir -p $P $M $D $V

cat > $P/pistolet_silex.json <<'EOF'
{
  "pose": "pistol", "magazine": 1, "reload_ticks": 40, "fire_mode": "semi",
  "stats": { "damage": 20, "bullet_speed": 9, "spread": 3, "fire_delay": 1, "blowback": 0.5, "gravity": 0.05, "range": 64, "crit_chance": 0.05 },
  "recoil": { "magnitude": 35, "horizontal_ratio": 0.45, "frequency": 1.7, "seed": 0 },
  "muzzle_flash": { "distance": 1.5, "types": ["large"] },
  "adjuster": "lower_hammer",
  "occupancy": { "reload": "both" },
  "aim_offset": [ -3.2, 1.6, 0.5 ], "aim_fov": 0.85,
  "sounds": {
    "shoot": { "id": "haute_capitale_fusils:pistolet_silex.tir", "volume": 1.0, "pitch_min": 0.9, "pitch_max": 1.1 },
    "echo": { "id": "haute_capitale_fusils:arme.echo_poudre", "volume": 1.0, "pitch_min": 1.0, "pitch_max": 1.2 },
    "dry": { "id": "haute_capitale_fusils:arme.a_vide", "volume": 0.75, "pitch_min": 1.4, "pitch_max": 1.6 },
    "equip": { "id": "haute_capitale_fusils:pistolet_silex.equip", "volume": 0.75, "pitch_min": 0.9, "pitch_max": 1.1 },
    "reload_cues": [
      { "at": 0.0, "sound": { "id": "haute_capitale_fusils:arme.cuir", "volume": 0.75, "pitch_min": 0.95, "pitch_max": 1.05 } },
      { "at": 0.2, "sound": { "id": "haute_capitale_fusils:pistolet_silex.insert", "volume": 1.25, "pitch_min": 0.95, "pitch_max": 1.05 } },
      { "at": 1.0, "sound": { "id": "haute_capitale_fusils:pistolet_silex.bourre", "volume": 1.25, "pitch_min": 1.05, "pitch_max": 1.15 } },
      { "at": 1.75, "sound": { "id": "haute_capitale_fusils:arme.cuir", "volume": 0.75, "pitch_min": 0.85, "pitch_max": 0.95 } },
      { "at": 1.85, "sound": { "id": "haute_capitale_fusils:arme.chien", "volume": 1.25, "pitch_min": 0.85, "pitch_max": 0.95 } }
    ]
  },
  "assets": { "model": "haute_capitale_fusils:item/pistolet_silex", "texture": "haute_capitale_fusils:textures/item/pistolet_silex.png", "animation": "haute_capitale_fusils:item/pistolet_silex" }
}
EOF

cat > $P/mousquet.json <<'EOF'
{
  "pose": "rifle", "magazine": 1, "reload_ticks": 60, "fire_mode": "semi",
  "stats": { "damage": 18, "bullet_speed": 18, "spread": 0.5, "fire_delay": 1, "gravity": 0.04, "range": 110, "crit_chance": 0.08 },
  "recoil": { "magnitude": 30, "horizontal_ratio": 0.45, "frequency": 1.9, "seed": 111 },
  "muzzle_flash": { "distance": 2.5, "types": ["large"] },
  "adjuster": "lower_hammer",
  "aim_offset": [ -4.0, 1.2, 1.0 ], "aim_fov": 0.75,
  "sounds": {
    "shoot": { "id": "haute_capitale_fusils:mousquet.tir", "volume": 1.0, "pitch_min": 0.9, "pitch_max": 1.1 },
    "echo": { "id": "haute_capitale_fusils:arme.echo_poudre", "volume": 1.0, "pitch_min": 1.05, "pitch_max": 1.25 },
    "dry": { "id": "haute_capitale_fusils:arme.a_vide", "volume": 0.75, "pitch_min": 1.4, "pitch_max": 1.6 },
    "equip": { "id": "haute_capitale_fusils:mousquet.equip", "volume": 0.75, "pitch_min": 0.9, "pitch_max": 1.1 },
    "reload_cues": [
      { "at": 0.0, "sound": { "id": "haute_capitale_fusils:arme.cuir", "volume": 1.25, "pitch_min": 0.75, "pitch_max": 0.85 } },
      { "at": 0.9, "sound": { "id": "haute_capitale_fusils:pistolet_silex.insert", "volume": 1.25, "pitch_min": 0.85, "pitch_max": 0.95 } },
      { "at": 1.7, "sound": { "id": "haute_capitale_fusils:pistolet_silex.bourre", "volume": 1.25, "pitch_min": 0.95, "pitch_max": 1.05 } },
      { "at": 2.88, "sound": { "id": "haute_capitale_fusils:arme.cuir", "volume": 1.25, "pitch_min": 0.85, "pitch_max": 0.95 } },
      { "at": 2.9, "sound": { "id": "haute_capitale_fusils:arme.chien", "volume": 1.25, "pitch_min": 0.85, "pitch_max": 0.95 } }
    ]
  },
  "assets": { "model": "haute_capitale_fusils:item/mousquet", "texture": "haute_capitale_fusils:textures/item/mousquet.png", "animation": "haute_capitale_fusils:item/mousquet" }
}
EOF

cat > $P/arquebuse.json <<'EOF'
{
  "pose": "rifle", "magazine": 4, "reload_ticks": 50, "fire_mode": "semi",
  "top_load": { "loop_start": 0.75, "loop_end": 1.75, "loop_duration": 0.33 },
  "stats": { "damage": 12, "bullet_speed": 14, "spread": 1, "fire_delay": 20, "gravity": 0.05, "range": 96, "crit_chance": 0.1 },
  "recoil": { "magnitude": 30, "horizontal_ratio": 0.45, "frequency": -1.9, "seed": 222 },
  "muzzle_flash": { "distance": 2.0, "types": ["large"] },
  "adjuster": "lower_hammer",
  "aim_offset": [ -4.0, 1.2, 1.0 ], "aim_fov": 0.75,
  "sounds": {
    "shoot": { "id": "haute_capitale_fusils:arquebuse.tir", "volume": 1.0, "pitch_min": 0.9, "pitch_max": 1.1 },
    "echo": { "id": "haute_capitale_fusils:arme.echo", "volume": 1.0, "pitch_min": 1.4, "pitch_max": 1.6 },
    "dry": { "id": "haute_capitale_fusils:arme.a_vide", "volume": 0.75, "pitch_min": 1.4, "pitch_max": 1.6 },
    "equip": { "id": "haute_capitale_fusils:arquebuse.ferme_culasse", "volume": 0.5, "pitch_min": 0.9, "pitch_max": 1.1 },
    "reload_cues": [
      { "at": 0.0, "sound": { "id": "haute_capitale_fusils:arquebuse.ouvre_culasse", "volume": 1.25, "pitch_min": 0.95, "pitch_max": 1.05 } },
      { "at": 0.6, "sound": { "id": "haute_capitale_fusils:arquebuse.charge_chambre", "volume": 1.25, "pitch_min": 0.9, "pitch_max": 1.1 } },
      { "at": 0.95, "sound": { "id": "haute_capitale_fusils:arquebuse.charge_chambre", "volume": 1.25, "pitch_min": 0.9, "pitch_max": 1.1 } },
      { "at": 1.3, "sound": { "id": "haute_capitale_fusils:arquebuse.charge_chambre", "volume": 1.25, "pitch_min": 0.9, "pitch_max": 1.1 } },
      { "at": 1.65, "sound": { "id": "haute_capitale_fusils:arquebuse.charge_chambre", "volume": 1.25, "pitch_min": 0.9, "pitch_max": 1.1 } },
      { "at": 2.13, "sound": { "id": "haute_capitale_fusils:arme.chien", "volume": 1.25, "pitch_min": 1.0, "pitch_max": 1.1 } },
      { "at": 2.5, "sound": { "id": "haute_capitale_fusils:arquebuse.ferme_culasse", "volume": 1.25, "pitch_min": 0.95, "pitch_max": 1.1 } }
    ],
    "fire_cycle_cues": [
      { "at": 0.33, "sound": { "id": "haute_capitale_fusils:arquebuse.ouvre_culasse", "volume": 1.0, "pitch_min": 0.9, "pitch_max": 1.1 } },
      { "at": 0.8, "sound": { "id": "haute_capitale_fusils:arquebuse.ferme_culasse", "volume": 1.0, "pitch_min": 0.9, "pitch_max": 1.1 } }
    ]
  },
  "assets": { "model": "haute_capitale_fusils:item/arquebuse", "texture": "haute_capitale_fusils:textures/item/arquebuse.png", "animation": "haute_capitale_fusils:item/arquebuse" }
}
EOF

cat > $P/tromblon.json <<'EOF'
{
  "pose": "rifle", "magazine": 2, "reload_ticks": 30, "fire_mode": "semi",
  "stats": { "damage": 22, "projectile_count": 8, "bullet_speed": 11, "spread": 7, "fire_delay": 1, "blowback": 0.4, "gravity": 0.06, "range": 40, "knockback": 0.5 },
  "recoil": { "magnitude": 30, "horizontal_ratio": 0.35, "frequency": 2.0, "seed": 999 },
  "muzzle_flash": { "distance": 2.0, "types": ["large"] },
  "adjuster": "double_hammer",
  "aim_offset": [ -4.0, 1.4, 1.0 ], "aim_fov": 0.85,
  "sounds": {
    "shoot": { "id": "haute_capitale_fusils:tromblon.tir", "volume": 1.0, "pitch_min": 0.9, "pitch_max": 1.1 },
    "echo": { "id": "haute_capitale_fusils:arme.echo_poudre", "volume": 1.0, "pitch_min": 0.65, "pitch_max": 0.85 },
    "dry": { "id": "haute_capitale_fusils:arme.a_vide", "volume": 0.75, "pitch_min": 1.4, "pitch_max": 1.6 },
    "equip": { "id": "haute_capitale_fusils:tromblon.ferme", "volume": 0.75, "pitch_min": 0.9, "pitch_max": 1.1 },
    "reload_cues": [
      { "at": 0.25, "sound": { "id": "haute_capitale_fusils:tromblon.ouvre", "volume": 1.25, "pitch_min": 0.9, "pitch_max": 1.1 } },
      { "at": 0.9, "sound": { "id": "haute_capitale_fusils:tromblon.charge", "volume": 1.25, "pitch_min": 0.9, "pitch_max": 1.1 } },
      { "at": 1.15, "sound": { "id": "haute_capitale_fusils:arme.chien", "volume": 1.25, "pitch_min": 1.1, "pitch_max": 1.3 } },
      { "at": 1.27, "sound": { "id": "haute_capitale_fusils:tromblon.ferme", "volume": 1.25, "pitch_min": 0.9, "pitch_max": 1.1 } }
    ]
  },
  "assets": { "model": "haute_capitale_fusils:item/tromblon", "texture": "haute_capitale_fusils:textures/item/tromblon.png", "animation": "haute_capitale_fusils:item/tromblon" }
}
EOF

cat > $P/fusil_rouages.json <<'EOF'
{
  "pose": "rifle", "magazine": 8, "reload_ticks": 30, "fire_mode": "semi", "allow_auto": true,
  "stats": { "damage": 6, "bullet_speed": 12, "spread": 2, "fire_delay": 6, "blowback": 0.05, "gravity": 0.05, "range": 80, "crit_chance": 0.05 },
  "recoil": { "magnitude": 7.5, "horizontal_ratio": 0.35, "frequency": 0.6, "seed": 6969 },
  "muzzle_flash": { "distance": 2.0, "types": ["triangle", "etoile"] },
  "adjuster": "magazine_slide",
  "aim_offset": [ -4.0, 1.2, 1.0 ], "aim_fov": 0.8,
  "sounds": {
    "shoot": { "id": "haute_capitale_fusils:fusil_rouages.tir", "volume": 1.0, "pitch_min": 0.9, "pitch_max": 1.1 },
    "echo": { "id": "haute_capitale_fusils:arme.echo", "volume": 0.8, "pitch_min": 0.9, "pitch_max": 1.1 },
    "dry": { "id": "haute_capitale_fusils:arme.a_vide", "volume": 0.75, "pitch_min": 1.4, "pitch_max": 1.6 },
    "equip": { "id": "haute_capitale_fusils:fusil_rouages.equip", "volume": 0.75, "pitch_min": 0.9, "pitch_max": 1.1 },
    "reload_cues": [
      { "at": 0.38, "sound": { "id": "haute_capitale_fusils:fusil_rouages.ejecte", "volume": 1.25, "pitch_min": 0.9, "pitch_max": 1.1 } },
      { "at": 1.04, "sound": { "id": "haute_capitale_fusils:fusil_rouages.insere", "volume": 1.25, "pitch_min": 0.9, "pitch_max": 1.1 } }
    ]
  },
  "assets": { "model": "haute_capitale_fusils:item/fusil_rouages", "texture": "haute_capitale_fusils:textures/item/fusil_rouages.png", "animation": "haute_capitale_fusils:item/fusil_rouages" }
}
EOF

cat > $P/canon_main.json <<'EOF'
{
  "pose": "rifle", "magazine": 1, "reload_ticks": 80, "fire_mode": "semi",
  "stats": { "damage": 34, "bullet_speed": 14, "spread": 2, "fire_delay": 1, "blowback": 0.6, "gravity": 0.07, "range": 48, "knockback": 1.2, "crit_chance": 0.1 },
  "recoil": { "magnitude": 45, "horizontal_ratio": 0.5, "frequency": 1.3, "seed": 7 },
  "muzzle_flash": { "distance": 3.0, "types": ["large"] },
  "adjuster": "lower_hammer",
  "aim_offset": [ -3.5, 1.2, 1.0 ], "aim_fov": 0.9,
  "sounds": {
    "shoot": { "id": "haute_capitale_fusils:canon_main.tir", "volume": 1.0, "pitch_min": 0.9, "pitch_max": 1.1 },
    "echo": { "id": "haute_capitale_fusils:arme.echo_poudre", "volume": 1.0, "pitch_min": 0.5, "pitch_max": 0.7 },
    "dry": { "id": "haute_capitale_fusils:arme.a_vide", "volume": 0.75, "pitch_min": 1.2, "pitch_max": 1.4 },
    "equip": { "id": "haute_capitale_fusils:mousquet.equip", "volume": 0.75, "pitch_min": 0.7, "pitch_max": 0.9 },
    "reload_cues": [
      { "at": 0.3, "sound": { "id": "haute_capitale_fusils:canon_main.charge", "volume": 1.25, "pitch_min": 0.7, "pitch_max": 0.9 } },
      { "at": 1.5, "sound": { "id": "haute_capitale_fusils:pistolet_silex.bourre", "volume": 1.25, "pitch_min": 0.7, "pitch_max": 0.8 } },
      { "at": 2.6, "sound": { "id": "haute_capitale_fusils:pistolet_silex.bourre", "volume": 1.25, "pitch_min": 0.7, "pitch_max": 0.8 } },
      { "at": 3.5, "sound": { "id": "haute_capitale_fusils:arme.chien", "volume": 1.25, "pitch_min": 0.7, "pitch_max": 0.8 } }
    ]
  },
  "assets": { "model": "haute_capitale_fusils:item/canon_main", "texture": "haute_capitale_fusils:textures/item/canon_main.png", "animation": "haute_capitale_fusils:item/canon_main" }
}
EOF

cat > $M/standard.json <<'EOF'
{ "name": { "translate": "munition.haute_capitale_fusils.standard", "fallback": "Balle standard" }, "color": 14406341, "effects": [] }
EOF
cat > $M/perforante.json <<'EOF'
{ "name": { "translate": "munition.haute_capitale_fusils.perforante", "fallback": "Balle perforante" }, "color": 11184810, "muzzle_tint": 13421772,
  "effects": [ { "type": "haute_capitale_fusils:armor_pierce", "fraction": 0.4, "piercing": 1 }, { "type": "haute_capitale_fusils:stat", "stat": "bullet_speed", "op": "mul", "value": 0.15 } ] }
EOF
cat > $M/explosive.json <<'EOF'
{ "name": { "translate": "munition.haute_capitale_fusils.explosive", "fallback": "Balle explosive" }, "color": 16755010,
  "effects": [ { "type": "haute_capitale_fusils:explosion", "radius": 2.5, "damage_fraction": 1.0 } ] }
EOF
cat > $M/incendiaire.json <<'EOF'
{ "name": { "translate": "munition.haute_capitale_fusils.incendiaire", "fallback": "Balle incendiaire" }, "color": 16746496,
  "effects": [ { "type": "haute_capitale_fusils:ignite", "ticks": 80 }, { "type": "haute_capitale_fusils:stat", "stat": "damage", "op": "mul", "value": 0.1 } ] }
EOF
cat > $M/barbelee.json <<'EOF'
{ "name": { "translate": "munition.haute_capitale_fusils.barbelee", "fallback": "Balle barbelée" }, "color": 11740202,
  "effects": [ { "type": "haute_capitale_fusils:bleed", "ticks": 100, "period": 20, "damage_per_tick": 1.0 } ] }
EOF
cat > $M/choc.json <<'EOF'
{ "name": { "translate": "munition.haute_capitale_fusils.choc", "fallback": "Balle de choc" }, "color": 15263999,
  "effects": [ { "type": "haute_capitale_fusils:knockback", "multiplier": 3.0 }, { "type": "haute_capitale_fusils:stun", "ticks": 15 } ] }
EOF
cat > $M/givre.json <<'EOF'
{ "name": { "translate": "munition.haute_capitale_fusils.givre", "fallback": "Balle de givre" }, "color": 10477567,
  "effects": [ { "type": "haute_capitale_fusils:freeze", "ticks": 80, "slowness_amplifier": 1 } ] }
EOF
cat > $M/argent.json <<'EOF'
{ "name": { "translate": "munition.haute_capitale_fusils.argent", "fallback": "Balle d'argent" }, "color": 15330805, "default_shots": 1, "item": "haute_capitale_fusils:balle_argent",
  "effects": [ { "type": "haute_capitale_fusils:bonus_vs_tag", "tag": "haute_capitale_fusils:vulnerable_argent", "multiplier": 1.5 } ] }
EOF
cat > $M/aetherium.json <<'EOF'
{ "name": { "translate": "munition.haute_capitale_fusils.aetherium", "fallback": "Balle d'Aetherium" }, "color": 8454143, "muzzle_tint": 9498367, "default_shots": 1, "item": "haute_capitale_fusils:balle_aetherium",
  "effects": [ { "type": "haute_capitale_fusils:stat", "stat": "seeking", "op": "set", "value": 0.35 }, { "type": "haute_capitale_fusils:stat", "stat": "gravity", "op": "set", "value": 0.0 },
    { "type": "haute_capitale_fusils:stat", "stat": "damage", "op": "mul", "value": 0.3 }, { "type": "haute_capitale_fusils:armor_pierce", "fraction": 0.25, "piercing": 2 },
    { "type": "haute_capitale_fusils:trail", "color": 8454143, "muzzle_tint": 9498367 } ] }
EOF

cat > $D/canon_raye.json <<'EOF'
{ "category": "canon", "name": { "translate": "item.haute_capitale_fusils.canon_raye" }, "item": "haute_capitale_fusils:canon_raye",
  "description": [ { "text": "Rayures hélicoïdales : la balle file droit." } ],
  "stats": { "spread": { "op": "mul", "value": -0.25 }, "bullet_speed": { "op": "mul", "value": 0.1 }, "underwater_drag": { "op": "set", "value": 0.985 } } }
EOF
cat > $D/canon_evase.json <<'EOF'
{ "category": "canon", "name": { "translate": "item.haute_capitale_fusils.canon_evase" }, "item": "haute_capitale_fusils:canon_evase",
  "description": [ { "text": "Bouche en trompette : plus de plombs, moins de portée." } ],
  "stats": { "projectile_count": 3, "damage": { "op": "mul", "value": 0.25 }, "spread": 3, "range": { "op": "mul", "value": -0.25 } } }
EOF
cat > $D/canon_lourd.json <<'EOF'
{ "category": "canon", "name": { "translate": "item.haute_capitale_fusils.canon_lourd" }, "item": "haute_capitale_fusils:canon_lourd",
  "description": [ { "text": "Fonte épaisse : la balle cogne." } ],
  "stats": { "bullet_speed": { "op": "mul", "value": -0.1 }, "knockback": 0.5, "damage": { "op": "mul", "value": 0.1 }, "recoil_multiplier": { "op": "mul", "value": 0.1 } } }
EOF
cat > $D/canon_long.json <<'EOF'
{ "category": "canon", "name": { "translate": "item.haute_capitale_fusils.canon_long" }, "item": "haute_capitale_fusils:canon_long",
  "description": [ { "text": "Un canon allongé : plus vite, plus loin, plus fort." } ],
  "stats": { "bullet_speed": { "op": "mul", "value": 0.25 }, "damage": { "op": "mul", "value": 0.15 }, "range": { "op": "mul", "value": 0.3 }, "recoil_multiplier": { "op": "mul", "value": 0.2 } } }
EOF
cat > $D/longue_vue.json <<'EOF'
{ "category": "canon", "name": { "translate": "item.haute_capitale_fusils.longue_vue" }, "item": "haute_capitale_fusils:longue_vue", "scope": true,
  "description": [ { "text": "Lentilles d'artificier : visée à la longue-vue." } ],
  "stats": { "spread": -1, "aim_spread_multiplier": { "op": "mul", "value": -0.4 } },
  "attachment": { "bone": "attachment_optic", "model": "haute_capitale_fusils:item/longue_vue", "texture": "haute_capitale_fusils:textures/item/longue_vue_modele.png" } }
EOF
cat > $D/detente_legere.json <<'EOF'
{ "category": "mecanisme", "name": { "translate": "item.haute_capitale_fusils.detente_legere" }, "item": "haute_capitale_fusils:detente_legere",
  "description": [ { "text": "Ressort de détente allégé : la cadence grimpe." } ],
  "stats": { "fire_rate": { "op": "mul", "value": 0.5 } } }
EOF
cat > $D/event_vapeur.json <<'EOF'
{ "category": "mecanisme", "name": { "translate": "item.haute_capitale_fusils.event_vapeur" }, "item": "haute_capitale_fusils:event_vapeur",
  "description": [ { "text": "Évacue la surpression : moitié moins de recul." } ],
  "stats": { "recoil_multiplier": { "op": "mul", "value": -0.5 } } }
EOF
cat > $D/huile_armurier.json <<'EOF'
{ "category": "mecanisme", "name": { "translate": "item.haute_capitale_fusils.huile_armurier" }, "item": "haute_capitale_fusils:huile_armurier",
  "description": [ { "text": "Un mécanisme bien huilé se recharge plus vite." } ],
  "stats": { "reload_speed": { "op": "mul", "value": 0.25 } } }
EOF
cat > $D/ressort_amortisseur.json <<'EOF'
{ "category": "mecanisme", "name": { "translate": "item.haute_capitale_fusils.ressort_amortisseur" }, "item": "haute_capitale_fusils:ressort_amortisseur",
  "description": [ { "text": "Absorbe le souffle arrière : le tireur ne bouge plus." } ],
  "stats": { "blowback": { "op": "set", "value": 0 } } }
EOF
cat > $D/chambre_vent.json <<'EOF'
{ "category": "mecanisme", "name": { "translate": "item.haute_capitale_fusils.chambre_vent" }, "item": "haute_capitale_fusils:chambre_vent",
  "description": [ { "text": "Chambre à air comprimé : tir stable en plein saut, souffle doublé." } ],
  "stats": { "blowback": { "op": "mul", "value": 0.5 }, "in_air_penalty": { "op": "mul", "value": -0.5 } } }
EOF
cat > $D/repeteur_rouages.json <<'EOF'
{ "category": "mecanisme", "name": { "translate": "item.haute_capitale_fusils.repeteur_rouages" }, "item": "haute_capitale_fusils:repeteur_rouages", "force_auto": true,
  "description": [ { "text": "Un train d'engrenages tire tant que la détente est pressée (armes compatibles)." } ],
  "stats": { "spread": 0.5 } }
EOF
cat > $D/accelerateur_volant.json <<'EOF'
{ "category": "mecanisme", "name": { "translate": "item.haute_capitale_fusils.accelerateur_volant" }, "item": "haute_capitale_fusils:accelerateur_volant",
  "description": [ { "text": "Chaque coup tiré dans la dernière seconde ajoute 12 % de dégâts." } ],
  "stats": { "accelerating": 0.12 } }
EOF
cat > $D/double_chambre.json <<'EOF'
{ "category": "mecanisme", "name": { "translate": "item.haute_capitale_fusils.double_chambre" }, "item": "haute_capitale_fusils:double_chambre",
  "description": [ { "text": "Une chambre de plus, et un rechargement un peu plus long." } ],
  "stats": { "magazine_bonus": 1, "reload_speed": { "op": "mul", "value": -0.15 } } }
EOF
cat > $D/culasse_runique.json <<'EOF'
{ "category": "mecanisme", "name": { "translate": "item.haute_capitale_fusils.culasse_runique" }, "item": "haute_capitale_fusils:culasse_runique",
  "description": [ { "text": "Des runes rendent parfois la balle : 20 % de munitions économisées." } ],
  "stats": { "ammo_consume_chance": { "op": "mul", "value": -0.2 } } }
EOF

cat > $V/arquebuse_chasse_epique.json <<'EOF'
{ "profile": "haute_capitale_fusils:arquebuse", "rarity": "epique",
  "name": { "text": "Arquebuse du Traqueur" },
  "lore": [ { "text": "Prise sur le cadavre d'un chef orc : le bois est encore chaud." } ],
  "stats": { "crit_chance": 0.15, "crit_multiplier": 0.25, "damage": { "op": "mul", "value": 0.1 } },
  "skill_effects": { "tir_perforant": [ { "type": "haute_capitale_fusils:bleed", "ticks": 100, "period": 20, "damage_per_tick": 1.0 } ] } }
EOF
cat > $V/mousquet_veteran_rare.json <<'EOF'
{ "profile": "haute_capitale_fusils:mousquet", "rarity": "rare",
  "name": { "text": "Mousquet du Vétéran" },
  "lore": [ { "text": "Crosse polie par vingt campagnes." } ],
  "stats": { "reload_speed": { "op": "mul", "value": 0.2 }, "spread": { "op": "mul", "value": -0.2 } } }
EOF
cat > $V/tromblon_bourgmestre_legendaire.json <<'EOF'
{ "profile": "haute_capitale_fusils:tromblon", "rarity": "legendaire",
  "name": { "text": "Tromblon du Bourgmestre" },
  "lore": [ { "text": "Laiton ciselé, poudre parfumée." }, { "text": "Chaque plomb brûle." } ],
  "stats": { "damage": { "op": "mul", "value": 0.2 }, "projectile_count": 2, "reload_speed": { "op": "mul", "value": 0.3 } },
  "effects": [ { "type": "haute_capitale_fusils:ignite", "ticks": 40 } ],
  "skill_effects": { "tir_explosif": [ { "type": "haute_capitale_fusils:stat", "stat": "damage", "op": "mul", "value": 0.25 } ] } }
EOF
echo "profils: $(ls $P | wc -l) munitions: $(ls $M | wc -l) modifications: $(ls $D | wc -l) variantes: $(ls $V | wc -l)"
