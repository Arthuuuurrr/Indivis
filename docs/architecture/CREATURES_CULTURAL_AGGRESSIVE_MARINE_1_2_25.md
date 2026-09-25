# Creatures 1.2.25 — cultural aggressive pool + Myths marine stage

Status: candidate only; not deployed.

BETA 0.19 applies the validated cultural hostile distribution and keeps the passive-pool rebalance deferred. Raven is excluded; Droughtroot Forest Hag is reserved for a future haunted forest; Minotaur remains reserved for a proper Dwarven underground/structure predicate.

The bundle keeps the 1.2.23 family-weight system and 1.2.24 fifteen-city safety. It adds validated Chaos attack damage (Maggot 2.0, Baby Spider 2.5, Corpse Fly 3.0).

Myths marine test stage: Abaia, Bake Kujira, Hippocampus, Kraken and Leviathan are added to the generic strict-water locomotion controller. They refill air underwater, receive autonomous 3D steering, prefer an existing combat target while it remains in water, and dry out on land. Bunyip is deliberately excluded as amphibious. Natural Myths spawning remains disabled until runtime validation.

Cave rules currently use lush_caves and dripstone_caves only; a true “any underground Y-level” rule needs a later runtime predicate.

Validation: archive tests clean; BETA 0.19 JSON parse clean; Raven/Droughtroot absent from active rules; Bunyip absent from strict-water set.
