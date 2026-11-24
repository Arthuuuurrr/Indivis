"""Freeze only a successfully tested binary; package user-facing deliverables."""
from pathlib import Path
import hashlib,json,shutil,zipfile

R=Path(__file__).resolve().parent;OUT=R/'out';QA=R/'previous/npc/qa'
build=json.loads((R/'qa/build.json').read_text());binary=Path(build['jar']);sha=hashlib.sha256(binary.read_bytes()).hexdigest()
assert sha==build['sha256']
records={name:json.loads((QA/f'last-{name}.json').read_text())for name in ['bb1-base','bb1-legacy','bb1-client','bb1-npc','bb1-npc-reload']}
for name,r in records.items():
    assert r['nexus_sha256']==(build['base_sha256']if name=='bb1-base'else sha),name
    assert 'PASS'in r['pass']and not (Path(r['game'])/'evidence/failure.txt').exists(),name
comparisons={}
for filename in ['geometry.csv','pose-geometry.csv']:
    a=(QA/'evidence-bb1-base/geometry'/filename).read_bytes();b=(QA/'evidence-bb1-legacy/geometry'/filename).read_bytes()
    assert a==b,filename
    comparisons[filename]={'identical':True,'states':len(a.splitlines()),'sha256':hashlib.sha256(a).hexdigest()}
audit=json.loads((R/'qa/audit.json').read_text());counts=json.loads((R/'qa/counts.json').read_text())
server_linkage=(R/'qa/server-linkage.txt').read_text();assert 'BB_SERVER_LINKAGE_PASS'in server_linkage
evidence=QA/'evidence-bb1-client';assert len(list(evidence.glob('preset-*.png')))==182
assert 'variants=76'in(evidence/'palettes-pass.txt').read_text()
assert '15 NPCs'in records['bb1-npc-reload']['pass']
names={'human':'Humains','nordic':'Nordiques','dwarf':'Nains','wood_elf':'Elfes des bois','high_elf':'Hauts-elfes'}
notes={
 'human':'Choix polyvalents, coupes sobres et citadines ; styles nobles/de mage ; moustache impériale et barbe royale.',
 'nordic':'Coupes guerrières, rasées et tressées ; deux variantes de tresses nordiques et barbe viking réservées à ce groupe.',
 'dwarf':'Grand choix de tresses, coupes robustes et barbes épaisses ; forge, mineur, runes, clan et royauté naine.',
 'wood_elf':'Coupes libres, sauvages, tressées ou d’éclaireuse ; oreilles sylvestres, larges, pennées et repliées.',
 'high_elf':'Coupes lisses, structurées et élégantes ; noblesse haut-elfique exclusive ; oreilles ornées, dorées, runiques et cristallines.'}
lines=['# Nexus BB1 — ajout et répartition des modèles','',
 'Version Minecraft : **1.21.11 / Fabric**. Rapport du **24 novembre 2026**.',
 'Base conservée : **PRE35-SI1-OPT3**. Version ajoutée : **OPT3.BB1**.','',
 '## Contenu','',
 'L’archive contient 91 modèles : 41 coiffures, 30 modèles de pilosité et 20 paires d’oreilles, avec leurs 91 textures. '
 '`visage_rase.geo.json` étant vide, il correspond au choix « Aucune » déjà présent. '
 '**90 nouveaux choix 3D** sont donc ajoutés : 41 coiffures, 29 barbes/moustaches et 20 oreilles, sans doublon « visage rasé ».','',
 'Les fichiers géométriques et PNG sont embarqués sans modification. Il ne s’agit pas de skins reconstruits. '
 'Les barbes comportant des bijoux les conservent ; les ornements intégrés ne sont pas cumulés avec les ornements génériques de Nexus.','',
 '## Répartition des nouveaux choix','',
 '| Race | Coiffures | Barbes/moustaches 3D | Oreilles |',
 '|---|---:|---:|---:|']
for race,label in names.items():
    c=counts[race];lines.append(f'| {label} | {c["coiffures"]} | {c["barbes"]} | {c["oreilles"]} |')
lines += ['', 'Les modèles partagés sont comptés dans chaque race concernée ; ces colonnes ne s’additionnent pas en modèles uniques.', '']
for race,label in names.items():lines.append(f'- **{label}** : {notes[race]}')
lines += ['', '### Règles conservées','',
 '- Tous les anciens choix restent disponibles ; seuls les nouveaux modèles sont répartis par race.',
 '- Les elfes restent imberbes, selon la règle existante. Les oreilles nouvelles sont réservées aux deux races elfiques.',
 '- La barbe obligatoire des nains n’est pas supprimée par le modèle « visage rasé ».',
 '- Aucune race n’est créée. « Draconique », « lunaire », « cristalline » et « pennée » sont traités comme des noms esthétiques d’assets, pas comme un ajout au lore.',
 '- Les couleurs existantes restent accessibles. Les nouvelles coiffures suivent la palette cheveux, les barbes la palette de pilosité et les oreilles la teinte de peau.', '',
 '## Installation','',
 '1. Fermer Minecraft et arrêter le serveur avant remplacement ; conserver une sauvegarde habituelle.',
 '2. Remplacer **uniquement l’ancien JAR NexusCharacters** par `Nexus-BB1-1.21.11.jar`, côté client **et** côté serveur. Ne pas conserver deux JAR NexusCharacters simultanément.',
 '3. Conserver les autres mods, la liaison Indivis-Nexus-NPC, les configurations et les données existantes. Aucun mod supplémentaire n’est requis pour ces modèles.', '',
 'Les choix sont dans les commandes habituelles « Coiffure », « Pilosité » et « Oreilles » de la création/édition Nexus. '
 'Les tenues et les corps ne sont pas remplacés par ce pack.', '',
 '## Vérifications effectuées sur ce binaire','',
 '| Vérification | Résultat |','|---|---|',
 '| Géométries et textures de l’archive | 91 couples validés, 2 968 cubes natifs, dimensions UV et hiérarchies contrôlées |',
 '| Client Minecraft réel, nouveaux modèles | 91 modèles parcourus ; 182 captures face/dos ; soumissions des 90 modèles non vides vérifiées |',
 '| Palettes nouvelles | 76 variantes vérifiées : 12 cheveux, 48 barbes (dont blondes/rousses/violettes), 16 peau/oreilles ; alpha et bijoux contrôlés |',
 '| Anciennes géométries | 320 états et 91 poses comparés à OPT3 : empreintes identiques |',
 '| Anciens menus et première personne | 23 scènes de menus, bras, poses et réflexion HUD : bancs de régression passés |',
 '| EasyNPC réel | 5 nouvelles recettes raciales, corps classiques/slim, export éditeur, validation serveur et rendu de 10 modèles distincts |',
 '| Redémarrage du monde de test | 15 PNJ retrouvés, mêmes UUID et recettes ; 5 tags sentinelles préservés ; nouveaux modèles rendus après chargement |',
 '| API commune côté serveur | 292 contrôles de catalogue/normalisation avec les classes client explicitement interdites au chargement |',
 f'| Audit du code existant | {audit["unchanged_classes"]} classes inchangées ; {audit["preserved_method_bodies"]} corps de méthodes conservés dans les {audit["patched_classes"]} classes adaptées |',
 '| Assets existants | 4 966 entrées inchangées octet pour octet |', '',
 'Les aperçus du ZIP proviennent des captures du client réel. Le parcours de chaque modèle et les scènes combinées ne constituent pas une vérification de toutes les combinaisons possibles de vêtements, animations, shaders et mods. '
 'Ce travail n’inclut pas de mesure FPS sur Lion-Port ou de connexion à ton serveur de production. '
 'Le moteur de surfaces OPT3 est conservé ; les géométries nouvelles sont compilées une fois et les textures colorées sont réutilisées en cache (256 variantes maximum).', '',
 'Le banc isolé utilise Fabric Loader 0.19.5, Fabric API 0.141.6, Skin Layers 1.10.2, EasyNPC 7.16.0 et la liaison Indivis-Nexus-NPC 0.1.0-PREVIEW1. '
 'Les journaux de ce banc hors connexion contiennent aussi des avertissements Mojang/Realms et de manifeste de persistance non configuré ; ils ne sont pas présentés comme une validation de la synchronisation de production.', '',
 '### Identité du fichier testé','',f'- SHA-256 BB1 : `{sha}`.',f'- SHA-256 base OPT3 : `{build["base_sha256"]}`.', '',
 '## Ajouter d’autres assets ensuite','',
 'Le catalogue des modèles et la répartition sont fondés sur des données. Dans les sources, ajouter le couple geo.json/PNG, son libellé et les races autorisées dans `distribution.json`, puis reconstruire. '
 'Pour une nouvelle coiffure, attribuer un ID libre dans `hair_ids.json` (à partir de 541), sans modifier les anciens. '
 'Les ajouts de PNG de l’ancien système restent pris en charge. Installer la nouvelle version reconstruite sur les clients et le serveur.', '',
 'Les nouveaux modèles doivent utiliser le sous-ensemble contrôlé : box UV, os imbriqués, rotations d’os et dilation des cubes. '
 'Les formats non pris en charge sont refusés explicitement. Déposer n’importe quel fichier Blockbench sans reconstruction n’est pas présenté comme une fonction disponible.', '',
 '## Catalogue exact par modèle','',
 'H = Humains ; N = Nordiques ; D = Nains ; EB = Elfes des bois ; HE = Hauts-elfes. « — » signifie non proposé pour cette race.', '']
for kind,title in [('coiffures','Coiffures'),('barbes','Barbes et moustaches'),('oreilles','Oreilles')]:
    lines += [f'### {title}', '', '| Modèle source | H | N | D | EB | HE |', '|---|:---:|:---:|:---:|:---:|:---:|']
    for e in build['entries']:
        if e['kind']!=kind:continue
        marks=['Rasé'if e['alias_none']and race in e['races']else 'Oui'if race in e['races']else '—'for race in names]
        lines.append('| `'+e['source'].split('/')[-1]+'` | '+' | '.join(marks)+' |')
    lines.append('')
report=OUT/'Repartition-et-verifications-BB1.md';report.write_text('\n'.join(lines)+'\n')
final=OUT/'Nexus-BB1-1.21.11.jar';shutil.copy2(binary,final);assert hashlib.sha256(final.read_bytes()).hexdigest()==sha
summary={'nexus_sha256':sha,'base_sha256':build['base_sha256'],'comparisons':comparisons,'audit':audit,'server_linkage':server_linkage,'runs':records}
(R/'qa/release.json').write_text(json.dumps(summary,ensure_ascii=False,indent=2)+'\n')
archive=OUT/'Nexus-BB1-1.21.11.zip'
with zipfile.ZipFile(archive,'w',zipfile.ZIP_DEFLATED)as z:
    for file in [final,report,OUT/'REPARTITION_PRESETS_BB1.csv']:z.write(file,file.name)
    z.write(R/'qa/release.json','verifications/resultats.json')
    for image in sorted((R/'qa/review').glob('*.png')):z.write(image,'apercus/'+image.name)
assert zipfile.ZipFile(archive).testzip()is None
print(json.dumps({'sha256':sha,'jar':str(final),'zip':str(archive),'report':str(report),'bytes':{p.name:p.stat().st_size for p in [final,archive,report]}},ensure_ascii=False,indent=2))
