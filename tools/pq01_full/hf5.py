"""HF5 : filtrage du near historique et récupération des sessions PQ01 orphelines."""
import json
from model import ROLES
def apply(packs):
    core=packs['core']
    core['data/capitale/function/quest/spawn_dirigeable_couronne/geraud_large_near_self.mcfunction']=b'# PQ01 HF5 : delai de chargement, mouvement du joueur et distance <= 3 blocs.\nindivispq01 near_geraud\n'
    for role in ROLES:
        path='data/capitale/function/pq01/open/'+role+'_self.mcfunction'
        before=core[path].decode();marker='execute if score @s hcm_pq1_active matches 1 run return 0'
        assert before.count(marker)==1
        core[path]=before.replace(marker,'indivispq01 reconcile\n'+marker).encode()
    path='data/capitale/function/pq01/test/start_macro.mcfunction'
    before=core[path].decode();marker='execute in minecraft:overworld run tp @s 764 299 1101\n'
    assert before.count(marker)==1
    core[path]=before.replace(marker,marker+'indivispq01 rearm_near\n').encode()
    path='data/capitale/function/pq01/scene/resume_wait_self.mcfunction'
    marker='tellraw @s '+json.dumps({'text':'','insertion':'indivis:pq01:v1:a4:geraud:0:0:0'},separators=(',',':'))+'\n'
    core[path]=marker.encode()+core[path]
    meta=json.loads(core['pack.mcmeta']);meta['pack']['description']+=' — HF5 : proximité et reprise Géraud'
    core['pack.mcmeta']=(json.dumps(meta,ensure_ascii=False,indent=2)+'\n').encode()
