"""Real client message events and native b10 state, without a rendered Minecraft client."""
from pathlib import Path
import os,subprocess,shutil,zipfile,json,hashlib
H=Path(__file__).resolve().parent;RT=Path('/tmp/indivis_pq02_runtime')
AUDIT=os.environ.get('PQ01_PROTOCOL_AUDIT')=='1'
JAVA=next((RT/'jdk').glob('*/bin/java'));C=H/'protocol_classes';shutil.rmtree(C,ignore_errors=True);C.mkdir()
import build
jar=Path(os.environ.get('PQ02_PROTOCOL_JAR',str(H/'output'/build.JOURNAL_FILENAME)))
dialogue=next((H.parents[1]/'upload').glob('haute-capitale-dialogue*.jar'))
api=RT/'client_api';api.mkdir(exist_ok=True)
with zipfile.ZipFile(RT/'fabric-api.jar') as z:
 for n in z.namelist():
  if n.endswith('.jar'):(api/Path(n).name).write_bytes(z.read(n))
libs=[jar,H/'output'/build.JOURNAL_FILENAME,dialogue,RT/'bootstrap/.fabric/remappedJars/minecraft-1.21.11-0.19.5/server-intermediary.jar',*sorted((RT/'libraries').glob('*.jar')),*sorted((RT/'minecraft_bundle/META-INF/libraries').rglob('*.jar')),*sorted(api.glob('*.jar'))]
CP=os.pathsep.join(map(str,libs));fixture=H/'reference/tools/pq01_full/tests/native_capture_doubles'
sources=[H/'tests/Pq02ProtocolBench.java',H/'tests/Pq02ScreenNetwork.java',fixture/'net/minecraft/class_437.java',fixture/'net/minecraft/class_5498.java',fixture/'net/hautecapitale/dialogue/client/HudMask.java',H/'reference/tools/pq01_full/tests/client_doubles/net/hautecapitale/dialogue/client/capture/CaptureDialogueScreen.java',*sorted((H/'tests/protocol_doubles').rglob('*.java'))]
if AUDIT:sources.append(H/'tests/Pq01ProtocolAudit.java')
subprocess.run([JAVA.with_name('javac'),'--release','21','-proc:none','-cp',CP,'-d',C,*sources],check=True)
asm=RT/'libraries/asm-9.10.1.jar';subprocess.run([JAVA.with_name('javac'),'-cp',asm,'-d',C,H/'reference/tools/pq01_full/tests/world/StripClientEnvironment.java'],check=True)
subprocess.run([JAVA,'-cp',str(C)+os.pathsep+str(asm),'StripClientEnvironment',dialogue,C],check=True)
r=subprocess.run([JAVA,'-Dhcd.selftest=true',*(['-Dpq02.protocol.legacy=true'] if os.environ.get('PQ02_PROTOCOL_LEGACY')=='1' else []),'-cp',str(C)+os.pathsep+CP,'net.hautecapitale.quetes.sequence.'+('Pq01ProtocolAudit' if AUDIT else 'Pq02ProtocolBench'),*([str(H/'audit_prologue/pq01_protocol_fixture.json')] if AUDIT else [])],text=True,capture_output=True)
(H/('reports/prologue_audit_protocol.txt' if AUDIT else 'reports/protocol_hf3_legacy.txt' if os.environ.get('PQ02_PROTOCOL_LEGACY')=='1' else 'reports/protocol_hf6.txt')).write_text(r.stdout+r.stderr);print((r.stdout+r.stderr)[-12000:]);assert r.returncode==0

(H/("reports/prologue_audit_protocol_fixture.json" if AUDIT else "reports/protocol_hf3_legacy_fixture.json" if os.environ.get("PQ02_PROTOCOL_LEGACY")=="1" else "reports/protocol_hf6_fixture.json")).write_text(json.dumps({"status":"PASS","jar_sha256":hashlib.sha256(jar.read_bytes()).hexdigest(),"mode":"PQ01 full prologue audit" if AUDIT else "legacy adverse-order" if os.environ.get("PQ02_PROTOCOL_LEGACY")=="1" else "all 111 nodes and readiness","native_classes":["DialogueClientState","MessageCapture","Fabric client message events"],"substitutes":["Minecraft client/screen/world/entities without rendering","C2S send recorder"],"graphical_client":False},indent=2)+"\n")
