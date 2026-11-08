from pathlib import Path
import build,build_hf9
H=Path(__file__).resolve().parent;build.JOURNAL_FILENAME=build_hf9.DEST
source=(H/'run_protocol.py').read_text().replace("RT=Path('/tmp/indivis_pq02_runtime')","RT=Path("+repr(str(H/'runtime_hf9'))+")").replace('protocol_hf8','protocol_hf9')
exec(compile(source,str(H/'run_protocol.py'),'exec'),{'__name__':'__main__','__file__':str(H/'run_protocol.py')})
