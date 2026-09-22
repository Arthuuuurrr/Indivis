# CapSkills 0.10.9 — Intégration Sorceleur (historique)

## Constat initial

Le mod Witcher était absent de CapSkills 0.10.8.

Audit du JAR :
- 73 définitions Spell Engine ;
- 20 actives ;
- 21 modifiers ;
- 32 passives ;
- 52 capacités alors intégrées à Pufferfish ;
- 21 laissées intrinsèques / liées à l’équipement / internes.

Les exclusions comprenaient notamment :
- defensive_witcher_mechanics ;
- bonus des sets Grandmaster ;
- glyphes supérieurs ;
- bonus de talismans ;
- passifs d’armes ;
- helpers internes.

Toutes les capacités apprises de cette classe exigeaient
`#witcher_rpg:witcher_swords`.

Les manuels/parchemins Spell Engine de progression avaient été neutralisés afin
de ne pas court-circuiter Pufferfish.

Ce rapport correspond à une étape antérieure. La validation TEST3 RC1 finale
retient 53 abilities HC et doit être utilisée comme référence actuelle.
