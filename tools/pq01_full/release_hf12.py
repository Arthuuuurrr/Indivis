"""Livraison HF12/QMARK2 : trois composants modifiés, guides et audits vérifiés."""
from pathlib import Path
import json,re,shutil,zipfile
import build
from quest_markers import Q,GIVERS

NAME='Indivis_PQ01_HF12_QMARK2_2026-10-26'
def main():
    audit=build.OUT/'hf12_audit'
    report=json.loads((audit/'validation.json').read_text());assert report['status']=='PASS'
    docs=build.HERE/'reference_docs/HF12';docs.mkdir(parents=True,exist_ok=True)
    files=build.FILES
    readme=f'''# Correctif des liaisons au quai et des « ! » — HF12 / QMARK2

26 octobre 2026 — Minecraft Fabric 1.21.11 / EasyNPC 7.16.0.

Cette livraison contient les **trois composants modifiés**. Elle reprend le prologue HF11, la Séquence I et les règles de disponibilité des quêtes du core.

## Installation

1. Fermer les conversations et arrêter le serveur après la sauvegarde habituelle du monde et des profils.
2. Remplacer le mod de quêtes/journal par `{files['journal']}` sur le **serveur et chaque client**.
3. Remplacer les deux datapacks du monde serveur par `{files['core']}` et `{files['quests']}`. Ne garder qu'une version active de chacun.
4. Redémarrer. Les liaisons, points et progressions déjà enregistrés sont réutilisés. Pour les PNJ dont la liaison a été refusée, relancer seulement la commande de leur rôle, près du bon PNJ.

**Décompresser le ZIP de livraison** : son dossier `installation/mods/` contient le JAR et `installation/datapacks/` les deux vrais datapacks. Les deux ZIP intérieurs sont directement installables dans le dossier des datapacks du monde serveur ; ils contiennent chacun `pack.mcmeta` à leur racine. Le ZIP de livraison lui-même n'est pas un datapack. Les dossiers décompressés sont également utilisables si `pack.mcmeta` est directement à leur racine.

Le JAR Nexus, Dialogue b10, EasyNPC, le mod admin et les voix ne sont pas fournis dans ce correctif. Conserver leurs versions actuelles.

## Ce qui est corrigé

- Althéon au quai : reconnaissance de l'ancienne action `capitaine_quai/interact_self` et acheminement du clic vers la phase correcte.
- Gautier, Hugues, Anselme et Perrin au quai : reconnaissance des anciennes actions `matelot_01_self` à `matelot_04_self` restées sur les copies. Les variantes `_quai_self` et les actions natives du même rôle sont reconnues aussi.
- Les « ! » du core sont actualisés par le journal toutes les 20 ticks serveur, sans dépendre du tick de la Séquence I. Les donneurs sont reconnus par leurs actions connues, leurs tags ou leur nom, y compris les noms colorés.
- Le marqueur est relevé au-dessus du nom et éclairé ; les conditions de quête, de délai, de rang et d'escorte sont conservées.

Les commandes de liaison gardent les autres actions et refusent les rôles incompatibles ou les déclencheurs multiples. Aucun reset ou déplacement de PNJ n'est nécessaire pour cette mise à jour.

- [Commandes exactes au quai](COMMANDES_QUAI.md)
- [Guide complet : liaisons, pose et déplacements](INSTALLATION_ET_POSE_PNJ.md)
- [Marqueurs et diagnostic](GUIDE_REPERES.md)
- [Vérifications et limites](VALIDATION.md)
- [GitHub : PR nº177](https://github.com/Arthuuuurrr/Indivis/pull/177)
'''
    rows=['| PNJ | Commande à proximité du PNJ voulu |','|---|---|']
    for role,name in [('geraud','Géraud Rivet'),('altheon','Althéon Brumeforge'),('gautier','Gautier — matelot 01'),('hugues','Hugues — matelot 02'),('anselme','Anselme — matelot 03'),('perrin','Perrin — matelot 04')]:
        rows.append(f'| {name} | `/function capitale:pq01/liaison/{role}_quai` |')
    commands='''# Commandes des six PNJ au quai — HF12

Les noms des commandes ne changent pas. La mise à jour corrige leur reconnaissance des anciennes actions EasyNPC.

Se placer à moins de trois blocs du PNJ voulu, plus près de lui que des autres EasyNPC d'au moins 0,5 bloc. Exécuter seulement sa ligne ; une liaison déjà réussie n'est pas à refaire.

'''+ '\n'.join(rows)+'''

Dans la capture du 26 octobre, les anciennes actions `capitaine_quai/interact_self` et `matelot_01_self` étaient rejetées : HF12 les reconnaît. Une autre tentative d'`altheon_quai` ciblait `matelot_03_quai_self`, qui appartient à **Anselme** : utiliser sa ligne, ou se rapprocher du véritable capitaine. Le refus d'un autre rôle est volontairement conservé.

Les liaisons servent à associer un PNJ ; elles ne le déplacent pas. Les commandes de pose et des points de marche sont détaillées dans [le guide complet](INSTALLATION_ET_POSE_PNJ.md). Les gardes, liaisons de la Séquence I et points déjà enregistrés restent réutilisés.
'''
    giver_rows=['| Donneur | Quêtes | Association facultative du marqueur |','|---|---|---|']
    for giver,(names,tags) in GIVERS.items():giver_rows.append('| '+names[0]+' | '+', '.join(q['qid'].upper() for q in Q if q['giver']==giver)+' | `/function capitale:quest/markers/liaison/'+giver+'` |')
    guide='''# Marqueurs personnels du core — QMARK2

Un « ! » jaune apparaît lorsqu'une offre est accessible au personnage Nexus sélectionné. Les 24 quêtes du core et leurs 20 donneurs sont couverts. Les marqueurs existants de la Séquence I sont conservés.

## Actualisation et reconnaissance

Le journal HF12 cherche les EasyNPC publics dans un rayon de 24 blocs, une fois toutes les 20 ticks serveur. Il ne dépend plus du déclenchement de `hc_quetes:runtime/second`. Les types humanoïdes et villageois sont couverts.

Une association manuelle est prioritaire. Sinon les actions d'interaction connues du core sont examinées, puis les anciens tags de rôle et le nom normalisé. Les couleurs Minecraft, espaces et casse du nom sont normalisés. Les actions ne sont pas exécutées pour identifier le donneur. Une action ambiguë n'est pas attribuée automatiquement. La lecture du jeu d'actions est mise en cache dix secondes ; le NBT complet de l'entité n'est pas lu en boucle.

Le marqueur est à 3,05 blocs au-dessus des pieds, avec luminosité propre et texte visible à travers les obstacles, pour éviter de se confondre avec le nom ou l'ombre d'un auvent. Il réutilise son UUID, même après une actualisation ou une migration QMARK1. Il reste privé au compte **et au personnage Nexus** ; les autres joueurs n'en reçoivent pas les apparitions.

Les règles des offres restent celles de QMARK1 : états de migration, rang, délais, quête active et blocage d'escorte lorsque l'acceptation les applique. Le Greffier peut prononcer son accueil alors que l'acceptation est bloquée par une escorte : entendre l'accueil ne suffit donc pas à établir l'éligibilité. Le repère n'accepte ni ne fait avancer de quête.

## Diagnostic en cas d'absence

Avec un personnage sélectionné, près du donneur, lancer comme administrateur :

```mcfunction
/indivispq01 diagnostic_reperes
```

Le diagnostic indique si les fonctions QMARK2 sont chargées, le personnage/verrou de profil, l'état et le délai du Greffier, le blocage d'escorte, puis les trois EasyNPC les plus proches avec leur type, rôle reconnu et disponibilité d'offre. Il écrit aussi une ligne dédiée dans le log serveur. Il ne modifie pas la progression.

## Donneur renommé

Si le donneur a un nom et une action entièrement personnalisés, se placer à moins de quatre blocs et plus près de lui que de tout autre EasyNPC public, puis utiliser sa ligne. Cette commande associe seulement son repère ; elle ne change ni son nom, ni son apparence, ni son dialogue, ni ses points de déplacement.

'''+ '\n'.join(giver_rows)+'''

Pour libérer une association manuelle, utiliser le même identifiant après `capitale:quest/markers/liberer/`, par exemple `/function capitale:quest/markers/liberer/greffier`. La reconnaissance automatique par actions, tags ou nom reste active.

## Charge et nettoyage

Les repères immobiles sont réutilisés sans réapparition réseau. Un déplacement du donneur entraîne une mise à jour de position si l'écart dépasse 0,05 bloc. Aucun nouvel appel global de visibilité n'est ajouté. Les repères devenus inutiles ou éloignés sont supprimés dans la dimension courante ; les règles privées historiques traitent le changement de personnage et la déconnexion. Les éventuels repères dans une autre dimension restent réutilisables au retour.

Pour revenir aux versions précédentes, arrêter le serveur puis remettre ensemble HF11 et les deux datapacks QMARK1 (ou les trois composants antérieurs). Les tags automatiques du marqueur sont inertes sans le module. Un admin peut retirer les seuls affichages QMARK dans chaque dimension concernée avec `/kill @e[type=minecraft:text_display,tag=cap_qmark]`.
'''
    install=(build.HERE/'reference_docs/HF11_INSTALLATION_ET_POSE_PNJ.md').read_text()
    a=install.index('**Mise à jour');b=install.index('\n\n',a)
    install=install[:a]+'**HF12 / QMARK2 — 26 octobre 2026 : remplacer le journal sur le serveur et chaque client, et les deux datapacks serveur fournis.** Les liaisons et points réussis sont réutilisés ; relancer uniquement les liaisons qui avaient été refusées. [Installation](README.md), [commandes au quai](COMMANDES_QUAI.md) et [diagnostic des « ! »](GUIDE_REPERES.md).'+install[b:]
    a=install.index('## 2. Installer');b=install.index('## 3.',a)
    install=install[:a]+f'''## 2. Installer HF12 / QMARK2

| Composant | Destination | Fichier à installer |
|---|---|---|
| Journal | Mods du serveur et de chaque client | `{files['journal']}` |
| Core | Datapacks du monde serveur | `{files['core']}` |
| Quêtes | Datapacks du monde serveur | `{files['quests']}` |

Décompresser d'abord le ZIP de livraison et utiliser ses trois fichiers intérieurs. Arrêter le serveur, remplacer chaque ancienne version active puis redémarrer. Les deux ZIP intérieurs de datapack sont directement installables. Garder Nexus SI1 actuellement installé, Dialogue b10, EasyNPC 7.16.0 et les voix. Aucun reset, nouvelle pose ou changement de points n'est requis. Les commandes suivantes servent aux éditions volontaires du décor.

'''+install[b:]
    install=install.replace('journal PQ01-R3-HF11','HF12 / QMARK2',1)
    install=install.replace('(../HOTFIX_MARCHE_R3_HF11.md)','(https://github.com/Arthuuuurrr/Indivis/blob/feat/pq01-dirigeable-r2-20261022/docs/quests/pq01-dirigeable-r3/HOTFIX_MARCHE_R3_HF11.md)')
    install=install.replace('enregistrée par le journal HF7','enregistrée par le journal')
    validation='''# Vérifications HF12 / QMARK2 — 26 octobre 2026

'''+ '\n'.join('- '+line for line in report['native_server_results'].values())+'''

- Le rejet d'Althéon au quai est reproduit sur le JAR HF11 exact, puis corrigé sur HF12 avec les mêmes anciennes actions.
- Fonctions de liaison/pose/libération exécutées avec l'initialisation de production, `function-permission-level=2`, les vrais objets EasyNPC et leurs actions. Contrôles avant/après `/reload`, conservation des autres actions et restauration exacte ; un autre rôle et plusieurs déclencheurs restent refusés sans écriture.
- Marqueurs : appels du tick de production sans contexte Séquence I ; 24 disponibilités, 20 donneurs, villageois Greffier reconnu par action puis nom coloré, délais, escorte et rangs ; propriétaires/observateur avec vrais trackers et paquets d'apparition/destruction ; UUID stable, déplacement, changement de personnage, déconnexion et nettoyage.
- Graphe des dialogues et scène couplée, marche réelle de Gautier et Perrin, obstacle, joueurs simultanés et sources très lentes contrôlés par les bancs de régression existants.
- Vérifications NBT, API natives, clics, capture Dialogue b10, couleurs et paramètres de navigation ; détails dans `audit/`.
- Comparaison octet par octet : scènes et fonctions du prologue identiques au core HF9 ; fonctions MQ/Séquence I, gardes et 25 biomes conservés. Dans `hc_quetes`, seule la description change par rapport à HF4 : l'ajout de tick QMARK1 est retiré.
- Le JAR HF12 change seulement `Pq01Actions`, `Pq01Bridge`, le nouveau module `CoreQuestMarkers`, son catalogue et les métadonnées. Toutes les classes de déplacement, de dialogue client, de visibilité, tous les mixins et les helpers historiques restent identiques à HF11.

## Limites

'''+ '\n'.join('- '+line for line in report['limits'])+'''

Les logs de banc inclus sont synthétiques et ne contiennent pas les journaux privés transmis par l'utilisateur. Les résultats ne prouvent pas un rendu graphique ou un gain de FPS sur son modpack.

## Reconstruction

```bash
python3 work/pq01_full/build.py
CORE_QUEST_MARKERS=1 python3 work/pq01_full/run_world_test.py
PQ01_QUAI_COMMANDS=1 python3 work/pq01_full/run_world_test.py
python3 work/pq01_full/run_world_test.py
PQ01_MULTIPLAYER=1 python3 work/pq01_full/run_world_test.py
PQ01_WALK_REGRESSION=1 python3 work/pq01_full/run_world_test.py
python3 work/pq01_full/verify_hf12.py
python3 work/pq01_full/release_hf12.py
```

Le contrôle avant correctif utilise le JAR HF11 exact avec `PQ01_JAR` et `PQ01_TEST_LABEL=quai-hf11-regression`. Les bancs de serveur s'exécutent successivement car ils emploient le même port. Les scripts de construction historiques sont rangés dans `tools/pq01_full/` sur GitHub ; le répertoire de travail local reste `work/pq01_full/`.
'''
    texts={'README.md':readme,'COMMANDES_QUAI.md':commands,'GUIDE_REPERES.md':guide,'INSTALLATION_ET_POSE_PNJ.md':install,'VALIDATION.md':validation}
    for n,s in texts.items():(docs/n).write_text(s)
    dest=build.ROOT/'deliverables'/NAME
    if dest.exists():shutil.rmtree(dest)
    dest.mkdir(parents=True)
    sums=[]
    for kind,name in files.items():
        source=build.OUT/name;assert build.sha(source.read_bytes())==report['files'][kind]['sha256']
        target=dest/'installation'/('mods' if kind=='journal' else 'datapacks')/name;target.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(source,target)
        sums.append(report['files'][kind]['sha256']+'  '+target.relative_to(dest).as_posix())
    (dest/'audit').mkdir()
    for p in audit.iterdir():
        if p.is_file() and p.suffix in {'.json','.txt','.log'}:shutil.copyfile(p,dest/'audit'/p.name)
    shutil.copyfile(build.OUT/'qmark2_catalogue.json',dest/'audit/catalogue.json')
    for n in texts:shutil.copyfile(docs/n,dest/n)
    (dest/'SHA256SUMS').write_text('\n'.join(sums)+'\n')
    functions={n.split('/')[1]+':'+n.split('/function/',1)[1][:-11] for k in ['core','quests'] for n in build.load(build.OUT/files[k]) if '/function/' in n and n.endswith('.mcfunction')}
    commands=set();links=0
    for p in dest.glob('*.md'):
        for target in re.findall(r'/function ([a-z0-9_.-]+:[a-z0-9_/.-]+)',p.read_text()):
            if target.endswith('/'):continue
            assert target in functions,(p,target);commands.add(target)
        for target in re.findall(r'\]\(([^)]+)\)',p.read_text()):
            if target.startswith(('http:','https:','#')):continue
            assert (p.parent/target.split('#')[0]).is_file(),(p,target);links+=1
    archive=dest.with_suffix('.zip')
    with zipfile.ZipFile(archive,'w',zipfile.ZIP_DEFLATED,compresslevel=9) as z:
        for p in sorted(dest.rglob('*')):
            if p.is_file():z.write(p,p.relative_to(dest).as_posix())
    with zipfile.ZipFile(archive) as z:
        assert z.testzip() is None
        assert len([n for n in z.namelist() if n.endswith('.jar')])==1
        assert len([n for n in z.namelist() if n.endswith('.zip')])==2
    result={'path':str(archive),'bytes':archive.stat().st_size,'sha256':build.sha(archive.read_bytes()),'documented_functions':len(commands),'local_links':links,'files':report['files']}
    (build.OUT/'hf12_release_manifest.json').write_text(json.dumps(result,ensure_ascii=False,indent=2)+'\n')
    print(json.dumps(result,ensure_ascii=False,indent=2));return result
if __name__=='__main__':main()
