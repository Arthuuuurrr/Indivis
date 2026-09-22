#!/usr/bin/env python3
from __future__ import annotations
import argparse, csv, json, math, zipfile
from collections import Counter
from pathlib import Path

DEFINITIONS = 'data/capskills/puffish_skills/categories/doctrine/definitions.json'

def spell_id_from_resource(name: str) -> str | None:
    parts=name.split('/')
    if len(parts) < 4 or parts[0] != 'data' or parts[2] != 'spell' or not name.endswith('.json'):
        return None
    return parts[1]+':'+('/'.join(parts[3:])[:-5])

def ability_id_from_resource(name: str) -> str | None:
    if '/capitale_abilities/' not in name or not name.endswith('.json') or not name.startswith('data/'):
        return None
    parts=name.split('/')
    i=parts.index('capitale_abilities')
    return parts[1]+':'+('/'.join(parts[i+1:])[:-5])

def scan_spell_resources(path: Path, out: dict[str,dict], sources: dict[str,str]):
    with zipfile.ZipFile(path) as z:
        for n in z.namelist():
            sid=spell_id_from_resource(n)
            if not sid:
                continue
            try:
                data=json.loads(z.read(n))
            except Exception:
                continue
            out[sid]=data
            sources[sid]=path.name

def main() -> int:
    ap=argparse.ArgumentParser()
    ap.add_argument('capskills', type=Path)
    ap.add_argument('jars', nargs='+', type=Path)
    ap.add_argument('--tsv', type=Path)
    args=ap.parse_args()

    spells: dict[str,dict]={}
    sources: dict[str,str]={}
    for jar in args.jars:
        scan_spell_resources(jar, spells, sources)

    with zipfile.ZipFile(args.capskills) as z:
        for n in z.namelist():
            sid=spell_id_from_resource(n)
            if not sid:
                continue
            spells[sid]=json.loads(z.read(n))
            sources[sid]='CAPSKILLS_OVERRIDE'

        ability_to_spell={}
        for n in z.namelist():
            aid=ability_id_from_resource(n)
            if not aid:
                continue
            d=json.loads(z.read(n))
            if isinstance(d.get('spell'),str):
                ability_to_spell[aid]=d['spell']

        defs=json.loads(z.read(DEFINITIONS))
        visible=[]
        for node,v in defs.items():
            if not isinstance(v,dict):
                continue
            for reward in v.get('rewards',[]):
                if not isinstance(reward,dict) or reward.get('type')!='haute_capitale_rpg:abilities':
                    continue
                for aid in reward.get('data',{}).get('abilities',[]):
                    if aid.startswith('arsenal:hc/'):
                        continue
                    sid=ability_to_spell.get(aid)
                    if sid:
                        visible.append((node,aid,sid))

    errors=[]
    rows=[]
    modes=Counter()
    targets=Counter()
    active_count=0
    passive_count=0
    required_aim=[]
    targetless=[]
    channels=[]

    for node,aid,sid in visible:
        d=spells.get(sid)
        if d is None:
            errors.append(f'missing spell resource: {sid} for {aid}')
            continue
        active=d.get('active')
        if not isinstance(active,dict):
            passive_count += 1
            rows.append([node,aid,sid,'PASSIVE_OR_MODIFIER','','','','',sources.get(sid,'?')])
            continue

        active_count += 1
        cast=active.get('cast') if isinstance(active.get('cast'),dict) else {}
        mode=cast.get('type','STANDARD')
        duration=cast.get('duration',0.0)
        ch=cast.get('channel') if isinstance(cast.get('channel'),dict) else {}
        ticks=ch.get('ticks',0)
        target=d.get('target') if isinstance(d.get('target'),dict) else {}
        target_type=target.get('type','CASTER')
        aim=target.get('aim') if isinstance(target.get('aim'),dict) else {}
        aim_required=bool(aim.get('required',False))

        modes[mode]+=1
        targets[target_type]+=1
        if aim_required:
            required_aim.append(sid)
        if target_type in ('AREA','NONE','CASTER'):
            targetless.append(sid)

        if not isinstance(duration,(int,float)) or not math.isfinite(float(duration)) or float(duration)<0:
            errors.append(f'{sid}: invalid cast duration {duration!r}')
        if mode=='CHANNEL':
            channels.append(sid)
            if float(duration)<=0:
                errors.append(f'{sid}: CHANNEL duration must be >0 (got {duration})')
            if not isinstance(ticks,int) or ticks<=0:
                errors.append(f'{sid}: CHANNEL ticks must be >0 (got {ticks!r})')
        if target_type in ('AREA','NONE','CASTER') and aim_required:
            errors.append(f'{sid}: targetless mode {target_type} unexpectedly has aim.required=true')

        rows.append([node,aid,sid,mode,target_type,duration,ticks,aim_required,sources.get(sid,'?')])

    if len(visible)!=len(set(a for _,a,_ in visible)):
        errors.append('duplicate visible ability rewards')

    print(f'visible_spell_abilities={len(visible)}')
    print(f'unique_visible_spells={len(set(s for _,_,s in visible))}')
    print(f'active={active_count}')
    print(f'passive_or_modifier={passive_count}')
    print('cast_modes='+json.dumps(dict(sorted(modes.items())),sort_keys=True))
    print('target_modes='+json.dumps(dict(sorted(targets.items())),sort_keys=True))
    print(f'channels={len(channels)}')
    print(f'aim_required={len(required_aim)}')
    print(f'targetless_area_none_caster={len(targetless)}')
    print(f'errors={len(errors)}')
    for e in errors:
        print('ERROR '+e)

    if args.tsv:
        args.tsv.parent.mkdir(parents=True,exist_ok=True)
        with args.tsv.open('w',newline='',encoding='utf-8') as f:
            w=csv.writer(f,delimiter='	')
            w.writerow(['node','ability','spell','cast_mode','target_type','duration','channel_ticks','aim_required','source'])
            w.writerows(rows)

    return 1 if errors else 0

if __name__=='__main__':
    raise SystemExit(main())
