#!/usr/bin/env python3
from __future__ import annotations
import csv, json, math, sys, zipfile
from collections import Counter
from pathlib import Path

def spell_id(name):
    p=name.split('/')
    if len(p)>=4 and p[0]=='data' and p[2]=='spell' and name.endswith('.json'):
        return p[1]+':'+('/'.join(p[3:])[:-5])
    return None

def scan(path, spells, src):
    with zipfile.ZipFile(path) as z:
        for n in z.namelist():
            sid=spell_id(n)
            if not sid:
                continue
            try:
                d=json.loads(z.read(n))
            except Exception as e:
                print('JSON_ERROR',path,n,e)
                continue
            spells[sid]=d
            src[sid]=Path(path).name

def main():
    cap=Path(sys.argv[1])
    jars=[Path(x) for x in sys.argv[2:]]
    spells={}
    src={}
    for j in jars:
        scan(j,spells,src)
    scan(cap,spells,src)

    errors=[]
    rows=[]
    casts=Counter()
    targets=Counter()
    channels=[]
    active=0
    passive=0

    for sid,d in sorted(spells.items()):
        a=d.get('active')
        if not isinstance(a,dict):
            passive+=1
            rows.append([sid,'PASSIVE_OR_MODIFIER','','','','',src[sid]])
            continue

        active+=1
        cast=a.get('cast') if isinstance(a.get('cast'),dict) else {}
        ctype=cast.get('type','STANDARD')
        casts[ctype]+=1
        dur=cast.get('duration',0.0)
        ch=cast.get('channel') if isinstance(cast.get('channel'),dict) else {}
        ticks=ch.get('ticks',0)
        t=d.get('target') if isinstance(d.get('target'),dict) else {}
        ttype=t.get('type','CASTER')
        targets[ttype]+=1
        aim=t.get('aim') if isinstance(t.get('aim'),dict) else {}
        req=bool(aim.get('required',False))
        impacts=d.get('impacts') if isinstance(d.get('impacts'),list) else []

        if ctype not in {'STANDARD','CHANNEL','CHARGE','ITEM_USE','PASSIVE'}:
            errors.append(f'{sid}: unknown cast type {ctype!r}')
        if not isinstance(dur,(int,float)) or not math.isfinite(float(dur)) or float(dur)<0:
            errors.append(f'{sid}: invalid duration {dur!r}')

        if ctype=='CHANNEL':
            channels.append(sid)
            if not isinstance(dur,(int,float)) or float(dur)<=0:
                errors.append(f'{sid}: CHANNEL duration <= 0')
            if not isinstance(ticks,int) or ticks<=0:
                errors.append(f'{sid}: CHANNEL ticks invalid {ticks!r}')
            if isinstance(dur,(int,float)) and isinstance(ticks,int) and ticks>0 and dur>0:
                interval=float(dur)*20/ticks
                if interval < 1.0:
                    errors.append(f'{sid}: CHANNEL interval <1 game tick ({interval})')

        if ttype in {'AREA','NONE','CASTER'} and req:
            errors.append(f'{sid}: targetless target type {ttype} with aim.required=true')
        if req and ttype!='AIM':
            errors.append(f'{sid}: aim.required=true but target.type={ttype}')

        rows.append([sid,ctype,ttype,dur,ticks,req,len(impacts),src[sid]])

    print('total_spells=',len(spells),sep='')
    print('active=',active,sep='')
    print('passive_or_modifier=',passive,sep='')
    print('cast_modes='+json.dumps(dict(sorted(casts.items())),sort_keys=True))
    print('target_modes='+json.dumps(dict(sorted(targets.items())),sort_keys=True))
    print('channels=',len(channels),sep='')
    print('errors=',len(errors),sep='')
    for e in errors:
        print('ERROR',e)

    return 1 if errors else 0

if __name__=='__main__':
    raise SystemExit(main())
