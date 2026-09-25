# Creatures 1.2.23 — family weights and aggressive-pool first pass

Status: candidate only; not deployed.

## Family-level weights

The pre-1.2.23 controller creates one Fabric spawn entry for every entity variant. Therefore four colour variants at weight 1 give the species a cumulative weight of 4.

1.2.23 keeps the distinct EntityTypes but introduces rule-level `family_weight`. An internal scale of 12 preserves integer spawn weights:
- ordinary logical weight W -> effective W*12;
- family with N variants -> each variant receives max(1, family_weight*12/N).

For the current terrestrial families N is 1, 2, 3 or 4, so division is exact. All colours/sexes remain spawnable while the species receives one budget.

This changes selection probability only. A Minecraft spawn group drawn from one SpawnEntry still contains the selected variant; 1.2.23 does not mix colours inside one group.

## Aggressive-pool changes

- Maggot: logical weight 18 -> 5; group 2–5 -> 1–2; health 12 -> 6 HP.
- Baby Spider: logical weight 18 -> 5; group 2–5 -> 1–2; health 18 -> 8 HP.
- Corpse Fly: logical weight 10 -> 3; group 1–3 -> 1–2; health 34 -> 6 HP.
- HMobs Brown Bear, Lion and Hyena: spawn group CREATURE -> MONSTER because their AI contains player-target/melee goals.
- Passive pool otherwise deferred.

Verified original Chaos attack damage:
- Maggot: 3.0
- Baby Spider: 3.5
- Corpse Fly: 5.0

Damage is deliberately unchanged in 1.2.23 pending the next explicit balance decision.

## Validation

- JAR archive: unzip -t clean.
- Fabric metadata: 1.2.23.
- Controller bytecode contains family_weight / effectiveWeight.
- Nested ChaosConfig bytecode contains forced health properties.
- Datapack BETA 0.17 JSON is parseable and embedded in candidate bundle.
