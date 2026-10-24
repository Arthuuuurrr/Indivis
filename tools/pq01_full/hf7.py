"""HF7 : départ de Gautier pendant Géraud ; capture des notifications hors dialogue."""
import json
def apply(packs):
    core=packs['core'];name='data/capitale/function/pq01/node/a2.mcfunction'
    core[name]+=b'indivispq01 approach_early\n'
    meta=json.loads(core['pack.mcmeta']);meta['pack']['description']+=' — HF7 : approche de Gautier et capture des dialogues'
    core['pack.mcmeta']=(json.dumps(meta,ensure_ascii=False,indent=2)+'\n').encode()
