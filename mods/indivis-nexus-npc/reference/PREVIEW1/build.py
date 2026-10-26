from pathlib import Path
import subprocess,zipfile,json,hashlib,tempfile
R=Path(__file__).resolve().parent;RT=R.parent/'pre31/runtime';J=R.parent/'pre31/jdk/bin'
Q=R/'qa';Q.mkdir(exist_ok=True);(R/'build').mkdir(exist_ok=True);C=Path(tempfile.mkdtemp(prefix='classes-',dir=R/'build'))
libs=[RT/'client-intermediary.jar',RT/'fabric-loader.jar',*sorted((RT/'libs').rglob('*.jar')),*sorted((RT/'fabric-api-classes').glob('*.jar')),*sorted((R/'base').glob('*.jar'))]
subprocess.run([str(J/'javac'),'-proc:none','-encoding','UTF-8','-cp',':'.join(map(str,libs)),'-d',str(C),*map(str,(R/'src').rglob('*.java'))],check=True)
meta={'schemaVersion':1,'id':'indivis_nexus_npc','version':'0.1.0-PREVIEW1','name':'Indivis Nexus NPC','environment':'*','entrypoints':{'main':['fr.indivis.nexusnpc.NexusNpc'],'client':['fr.indivis.nexusnpc.client.NexusNpcClient']},'mixins':[{'config':'indivis-nexus-npc.mixins.json','environment':'client'}],'depends':{'minecraft':'~1.21.11','java':'>=21','fabricloader':'>=0.19.5','fabric-api':'>=0.141.6','nexuscharacters':'1.0.0-beta.35+indivis.1.21.11.SI1','easy_npc':'7.16.0'}}
mixins={'required':True,'package':'fr.indivis.nexusnpc.mixin','compatibilityLevel':'JAVA_21','client':['PlayerRendererMixin','RenderDispatcherMixin','LivingFeatureAccessor','AvatarStateMixin','ReloadMixin','EditorMixin','EditorTitleMixin'],'injectors':{'defaultRequire':1}}
target=R/'Indivis-Nexus-NPC-0.1.0-PREVIEW1-1.21.11.jar'
entries={'fabric.mod.json':json.dumps(meta,ensure_ascii=False,indent=2).encode(),'indivis-nexus-npc.mixins.json':json.dumps(mixins,indent=2).encode()}
entries.update({p.relative_to(C).as_posix():p.read_bytes() for p in C.rglob('*.class')})
entries.update({p.relative_to(R/'resources').as_posix():p.read_bytes() for p in (R/'resources').rglob('*') if p.is_file()})
for p in sorted((R/'resources/presets').glob('*.json')):
 preset=json.loads(p.read_text());id=preset['id'];entity='easy_npc:humanoid_slim' if preset['slim'] else 'easy_npc:humanoid'
 quote=lambda value:json.dumps(value,ensure_ascii=False)
 saved={'preset':preset,'originalSkin':'{Type:"DEFAULT",Timestamp:0L}','originalRoot':'{}'}
 content='indivis-nexus-npc:v1:'+json.dumps(saved,ensure_ascii=False,separators=(',',':'))
 scale=f"[{preset['build']}f,{preset['height']}f,{preset['build']}f]"
 snbt='{PresetMetadata:{author:"Indivis",category:"Indivis — Nexus",created:1L,modified:1L,description:"Apparence raciale Nexus 3D. Module Indivis Nexus NPC requis.",entityTypeId:'+quote(entity)+',name:'+quote(preset['name'])+',variantType:'+quote('ALEX' if preset['slim'] else 'STEVE')+',version:"1.0.0"},data:{id:'+quote(entity)+',EasyNPCVersion:3,PersistenceRequired:1b,CustomNameVisible:1b,CustomName:{text:'+quote(preset['name'])+'},ModelData:{Root:{Scale:'+scale+'}},SkinData:{Type:"DEFAULT",Name:'+quote('Nexus: '+id)+',Content:'+quote(content)+',Timestamp:0L},Status:{finalized:1b}}}'
 entries['data/easy_npc/default_preset/indivis_nexus/'+id+'.npc.snbt']=snbt.encode()
with zipfile.ZipFile(target,'w') as z:
 for name,data in sorted(entries.items()):
  info=zipfile.ZipInfo(name,(2026,10,26,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;z.writestr(info,data)
manifest={'jar':target.name,'sha256':hashlib.sha256(target.read_bytes()).hexdigest(),'size_bytes':target.stat().st_size,'nexus_base_sha256':hashlib.sha256((R/'base/NexusCharacters-Indivis-1.21.11-PRE35-SI1.jar').read_bytes()).hexdigest()}
(Q/'build.json').write_text(json.dumps(manifest,indent=2)+'\n');print('NPC_BUILD_PASS',json.dumps(manifest),flush=True)
