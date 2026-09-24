# HazennStuff HC SETFIX1

Candidate:
`hazennstuff-fabric-1.21.11-1.0.0-b4-HC-SPELLCOMPAT1-SETFIX1.jar`

SHA-256:
`c727bc949077f6a3262767e4dff97d46ab6163fb8e1c6aac2f0d7ed43366a651`

This is a focused set-bonus compatibility fix. It does **not** alter ARMOR40 or the validated raw armor values.

Changes:
- explicit partial/variant set rules while retaining strict 4-piece matching as the default;
- Fireblossom Warrior bonus: +2 ARMOR / +2 toughness as fixed ADD_VALUE instead of an ineffective base multiplier;
- nine unrelated former Tyrant's Grace families remapped to existing specialized Hazenn set effects.

The numerical per-piece outliers (Dreadsteel, Permafrost, Frostbite, divergent visual variants) are intentionally deferred until their tier/progression context is fully compared.

Runtime validation is required before promotion to the current server JAR library.
