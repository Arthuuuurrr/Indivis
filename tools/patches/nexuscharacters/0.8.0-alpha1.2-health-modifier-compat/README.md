# NexusCharacters alpha1.2 — health modifier compatibility

Issue: #15

The character identity system continues to own the **racial base** of Minecraft
`MAX_HEALTH`, but it must not treat that base as the player's total maximum
health.

alpha1.1 correctly wrote the racial base (20/22/18), but then clamped current
health against that same racial value every time `HauteCapitaleIdentity`
reapplied the identity. This prevented equipment modifiers from ever being
filled by regeneration/healing.

alpha1.2 changes only the current-health clamp: it uses the player's actual
`getMaxHealth()` total after all modifiers. Race base assignment is unchanged.

SHA-256:
`ab6cbe03ce2ce29378c6b43759546790c6d0128fecc650113b94c283098a5ad1`

The matching HUD 1.3.3 patch is required because HUD 1.3.2 contained its own
independent total-health normalization paths.
