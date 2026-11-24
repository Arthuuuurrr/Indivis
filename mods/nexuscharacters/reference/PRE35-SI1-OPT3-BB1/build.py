"""Reproducible OPT3 -> BB1 addition. Authored geometry/PNG bytes stay intact."""
from pathlib import Path
import collections,csv,hashlib,json,math,os,struct,subprocess,tempfile,zipfile,zlib

R=Path(__file__).resolve().parent;RT=R/'runtime';J=RT/'jdk/bin'
BASE=R/'base/NexusCharacters-Indivis-1.21.11-PRE35-SI1-OPT3(1).jar'
BASE_SHA='de06ecab0f5cf7ccee29043bff2ccb5fd2b7425c173da145c1f27559b49def7b'
assert hashlib.sha256(BASE.read_bytes()).hexdigest()==BASE_SHA
ROOT=R/'input/JSON-BLOCKBENCH-COIFFURES-BARBES-OREILLES'
distribution=json.loads((R/'distribution.json').read_text())
hair_ids=json.loads((R/'hair_ids.json').read_text())
materials=json.loads((R/'materials.json').read_text())
ornaments=sorted({(rgb[0]<<16)|(rgb[1]<<8)|rgb[2]for palette in materials['ornaments'].values()for rgb in palette})
assert len(set(hair_ids.values()))==len(hair_ids) and all(500<=v<=9999 for v in hair_ids.values()),'Invalid stable hair IDs'
for p in ['build','qa','out']:(R/p).mkdir(exist_ok=True)
labels={
 'boucle':'Bouclés','boucles_mi_longs':'Boucles mi-longues','cheveux_longs':'Cheveux longs','cheveux_ondules':'Cheveux ondulés','chignon':'Chignon','chignon_guerrier':'Chignon guerrier','chignon_messy':'Chignon décoiffé',
 'court_classique':'Courts classiques','court_degrade':'Courts dégradés','court_ebouriffe':'Courts ébouriffés','court_stylise':'Courts stylisés','crete_sauvage':'Crête sauvage','degrade_rase':'Dégradé rasé','demi_attache':'Demi-attachés','double_queue':'Deux queues',
 'ebouriffe':'Ébouriffés','elfe_noble':'Noblesse haut-elfique','frange_longue':'Frange longue','frange_longue_2':'Frange longue II','longs_tresses':'Longs tressés','long_degrade':'Longs dégradés','long_lisse':'Longs lisses','long_messy':'Longs décoiffés','meches_rebelles':'Mèches rebelles',
 'ondule':'Ondulés','ondules_romantiques':'Ondulations souples','queue_de_cheval':'Queue-de-cheval','queue_de_cheval_2':'Queue-de-cheval II','rase_motif':'Rasés à motif','rase_undercut':'Undercut rasé','style_assassine':'Style d’éclaireuse','style_elfique':'Style elfique','style_guerrier':'Style guerrier','style_mage':'Style de mage','style_noble':'Style noble','style_sauvage':'Style sauvage',
 'tresses_elegantes':'Tresses élégantes','tresses_laterales':'Tresses latérales','tresses_viking':'Tresses nordiques','tresses_viking_2':'Tresses nordiques II','tresse_laterale':'Tresse latérale',
 'barbe_ancienne':'Barbe ancienne','barbe_blanche':'Barbe blanche','barbe_carree':'Barbe carrée','barbe_courte':'Barbe courte','barbe_epaisse':'Barbe épaisse','barbe_forge':'Barbe de la forge','barbe_grisonnante':'Barbe grisonnante','barbe_guerrier':'Barbe guerrière','barbe_longue':'Barbe longue','barbe_mineur':'Barbe de mineur','barbe_montagne':'Barbe des montagnes','barbe_pleine':'Barbe pleine','barbe_royale':'Barbe royale','barbe_royale_naine':'Barbe royale naine','barbe_runique':'Barbe runique','barbe_seigneur_profondeurs':'Seigneur des Profondeurs','barbe_sombre':'Barbe sombre','barbe_totemique':'Barbe totémique','barbe_tressee':'Barbe tressée','barbe_tressee_clan':'Tresses de clan','barbe_viking':'Barbe nordique','barbichette':'Barbichette','bouc_van_dyke':'Bouc Van Dyke','moustache_barbichette':'Moustache et barbichette','moustache_classique':'Moustache classique','moustache_fine':'Moustache fine','moustache_imperiale':'Moustache impériale','style_aventurier':'Style aventurier','visage_rase':'Visage rasé',
 'anneaux':'À anneaux','bijou_dore':'Bijou doré','classique':'Classiques','courbee':'Courbées','cristalline':'Cristallines','draconique':'Draconiques','elancee':'Élancées','en_angle':'Anguleuses','fine':'Fines','large':'Larges','longue':'Longues','lunaire':'Lunaires','ornementee':'Ornementées','pennee':'Pennées','relevee':'Relevées','repliee':'Repliées','runique':'Runiques','sauvage':'Sauvages','sombre':'Sombres','sylvestre':'Sylvestres'
}
resources={};entries=[];checks=[];model_names={}
def vec(a):
 assert len(a)==3 and all(isinstance(n,(int,float)) and math.isfinite(n) for n in a)
def validate(path,geo,png):
 assert geo['format_version']=='1.12.0' and len(geo['minecraft:geometry'])==1,path
 g=geo['minecraft:geometry'][0];names={b['name'] for b in g['bones']};assert len(names)==len(g['bones'])
 assert 'head'in names
 parents={b['name']:b.get('parent')for b in g['bones']}
 for name in names:
  chain=set();n=name
  while n is not None:
   assert n in names and n not in chain,(path,'bad/cyclic bone',n)
   chain.add(n);n=parents[n]
 width=g['description']['texture_width'];height=g['description']['texture_height']
 assert png[:8]==b'\x89PNG\r\n\x1a\n' and struct.unpack('>II',png[16:24])==(width,height)
 count=0
 for bone in g['bones']:
  vec(bone.get('pivot',[0,24,0]));vec(bone.get('rotation',[0,0,0]))
  for c in bone.get('cubes',[]):
   assert set(c)<=set(['origin','size','inflate','uv']),(path,'unsupported cube fields')
   vec(c['origin']);vec(c['size']);assert all(v>0 for v in c['size'])
   assert isinstance(c['uv'],list)and len(c['uv'])==2 and all(int(v)==v and v>=0 for v in c['uv'])
   u,v=c['uv'];w,h,d=c['size'];assert u+2*(w+d)<=width+1e-5 and v+d+h<=height+1e-5,(path,'UV outside',c)
   assert math.isfinite(c['inflate']) and -1<c['inflate']<2 and all(v+2*c['inflate']>0 for v in c['size']),(path,'invalid dilation',c)
   count+=1
 checks.append({'file':str(path.relative_to(ROOT)),'bones':len(names),'cubes':count,'texture':[width,height]})
 return count
for kind in ['coiffures','barbes','oreilles']:
 spec=distribution[kind];assigned={}
 common_races=spec.get('common_races',distribution['races'])
 for name in spec['common']:assigned[name]={'races':common_races,'alias':None}
 for group in spec['groups']:
  for name in group['models']:
   assert name not in assigned,(kind,'duplicate distribution',name)
   assigned[name]={'races':group['races'],'alias':group.get('alias')}
 files=sorted((ROOT/kind).glob('*.geo.json'));model_names[kind]=[p.name[:-9]for p in files]
 assert set(assigned)==set(model_names[kind]),(kind,'distribution mismatch',set(assigned)^set(model_names[kind]))
 for index,path in enumerate(files):
  name=path.name[:-9];row=assigned[name];pngname=('coiffure_'+name+'.png')if kind=='coiffures'else('oreilles_'+name+'_defaut.png')if kind=='oreilles'else('barbe_'+name+'_defaut.png')
  texture=(path.parent/pngname).read_bytes();geo=json.loads(path.read_text());count=validate(path,geo,texture)
  identity='bb_'+name;assert len(identity)<=32
  hair_id=hair_ids[name] if kind=='coiffures'else 0
  entry={'kind':kind,'id':identity,'hair_id':hair_id,'label':labels[name],'races':row['races'],'alias_none':row['alias']=='none','geometry':'blockbench/'+kind+'/'+path.name,'texture':'textures/blockbench/'+kind+'/'+pngname,'cubes':count,'source':str(path.relative_to(ROOT)),'preserve_colors':ornaments}
  assert not entry['alias_none']or count==0
  entries.append(entry);resources['assets/nexuscharacters/'+entry['geometry']]=path.read_bytes();resources['assets/nexuscharacters/'+entry['texture']]=texture
resources['assets/nexuscharacters/blockbench/catalog.json']=json.dumps({'schema':1,'entries':entries},ensure_ascii=False,indent=2).encode()
resources['assets/nexuscharacters/blockbench/distribution.json']=(R/'distribution.json').read_bytes()
def blank_png():
 def chunk(t,data):return struct.pack('>I',len(data))+t+data+struct.pack('>I',zlib.crc32(t+data)&0xffffffff)
 return b'\x89PNG\r\n\x1a\n'+chunk(b'IHDR',struct.pack('>IIBBBBB',64,64,8,6,0,0,0))+chunk(b'IDAT',zlib.compress((b'\x00'+b'\x00'*(64*4))*64))+chunk(b'IEND',b'')
for e in entries:
 if e['kind']=='coiffures':resources[f"assets/nexuscharacters/appearance_parts_v064/hair/hair_{e['hair_id']}.png"]=blank_png()
work=Path(tempfile.mkdtemp(prefix='classes-',dir=R/'build'));patch=work/'patch';patch.mkdir()
asm=':'.join(map(str,(RT/'libs/org/ow2/asm').rglob('*.jar')))
subprocess.run([str(J/'javac'),'-cp',asm,'-d',str(work),str(R/'tools/PatchBlockbench.java')],check=True)
subprocess.run([str(J/'java'),'-cp',str(work)+':'+asm,'PatchBlockbench',str(BASE),str(patch)],check=True)
nested=R/'build/dependencies';nested.mkdir(exist_ok=True)
with zipfile.ZipFile(RT/'mods/fabric-api.jar')as z:
 for name in z.namelist():
  if name.endswith('.jar'):
   p=nested/Path(name).name
   if not p.exists():p.write_bytes(z.read(name))
cp=':'.join(map(str,[patch,BASE,RT/'client-intermediary.jar',RT/'fabric-loader.jar',RT/'mods/skinlayers.jar',*sorted((RT/'libs').rglob('*.jar')),*sorted(nested.glob('*.jar'))]))
sources=sorted((R/'src').rglob('*.java'))
compiled=subprocess.run([str(J/'javac'),'-proc:none','-encoding','UTF-8','-cp',cp,'-d',str(patch),*map(str,sources)])
if compiled.returncode:raise SystemExit('BB1 Java compilation failed')
classes={p.relative_to(patch).as_posix():p.read_bytes()for p in patch.rglob('*.class')}
target=R/'out/NexusCharacters-Indivis-1.21.11-PRE35-SI1-OPT3-BB1.jar';temporary=target.with_suffix('.building')
with zipfile.ZipFile(BASE)as src,zipfile.ZipFile(temporary,'w')as out:
 for info in src.infolist():
  data=src.read(info)
  if info.filename in classes:data=classes.pop(info.filename)
  if info.filename=='fabric.mod.json':
   meta=json.loads(data);meta['version']='1.0.0-beta.35+indivis.1.21.11.SI1.OPT3.BB1'
   meta['description']+=' | BB1: race-curated authored Blockbench hair, beards and ears; OPT3 retained.'
   meta['entrypoints']['client'].append('net.tompsen.nexuscharacters.BlockbenchClient')
   data=json.dumps(meta,ensure_ascii=False,indent=2).encode()
  assert info.filename not in resources,('Cannot replace existing asset',info.filename)
  out.writestr(info,data)
 for name,data in sorted({**classes,**resources}.items()):
  info=zipfile.ZipInfo(name,(2026,11,24,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;out.writestr(info,data)
with zipfile.ZipFile(BASE)as old,zipfile.ZipFile(temporary)as new:
 assert new.testzip()is None
 changed=sorted(n for n in old.namelist()if old.read(n)!=new.read(n));added=sorted(set(new.namelist())-set(old.namelist()))
 assets=[n for n in old.namelist()if n.startswith('assets/')];assert len(assets)==4966
 assert all(old.read(n)==new.read(n)for n in assets)
 for cls in ['SurfaceGeometry','SpatialSurfaceCleaner','SpatialSurfaceCleaner$Index','ExactSurfaceClipper','CompactSurfaceClipper','PosedSurfaceSupport','PartSurfaceReuse','PartVolumeCache','PoseScratch','PreparedSurfaceCache','LowerJointSupport']:
  name='net/tompsen/nexuscharacters/'+cls+'.class';assert old.read(name)==new.read(name),cls
temporary.replace(target)
record={'jar':str(target),'sha256':hashlib.sha256(target.read_bytes()).hexdigest(),'base_sha256':BASE_SHA,'changed_entries':changed,'added_entries':added,'unchanged_assets':len(assets),'classes':str(patch),'entries':entries,'validation':checks}
(R/'qa/build.json').write_text(json.dumps(record,ensure_ascii=False,indent=2)+'\n')
counts={race:{kind:sum(race in e['races']and not e['alias_none']for e in entries if e['kind']==kind)for kind in model_names}for race in distribution['races']}
(R/'qa/counts.json').write_text(json.dumps(counts,indent=2)+'\n')
with(R/'out/REPARTITION_PRESETS_BB1.csv').open('w',newline='',encoding='utf-8-sig')as f:
 writer=csv.writer(f);writer.writerow(['categorie','modele','libelle','identifiant','hair_id','races','alias_none'])
 for e in entries:writer.writerow([e['kind'],e['source'],e['label'],e['id'],e['hair_id'],';'.join(e['races']),e['alias_none']])
print('BB1_BUILD_PASS',record['sha256'],json.dumps(counts),flush=True)
