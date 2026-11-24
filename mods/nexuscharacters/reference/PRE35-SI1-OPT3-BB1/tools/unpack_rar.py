"""Read a RAR with system libarchive; bounded, traversal-safe asset extraction."""
import argparse,ctypes,json,stat
from pathlib import Path,PurePosixPath

p=argparse.ArgumentParser();p.add_argument('archive');p.add_argument('--extract',type=Path)
args=p.parse_args();lib=ctypes.CDLL('libarchive.so.13')
def bind(name,restype,argtypes):
    f=getattr(lib,name);f.restype=restype;f.argtypes=argtypes;return f
ptr=ctypes.c_void_p
new=bind('archive_read_new',ptr,[])
filters=bind('archive_read_support_filter_all',ctypes.c_int,[ptr])
formats=bind('archive_read_support_format_all',ctypes.c_int,[ptr])
open_archive=bind('archive_read_open_filename',ctypes.c_int,[ptr,ctypes.c_char_p,ctypes.c_size_t])
header=bind('archive_read_next_header',ctypes.c_int,[ptr,ctypes.POINTER(ptr)])
name=bind('archive_entry_pathname',ctypes.c_char_p,[ptr])
size=bind('archive_entry_size',ctypes.c_int64,[ptr])
kind=bind('archive_entry_filetype',ctypes.c_uint,[ptr])
read=bind('archive_read_data',ctypes.c_ssize_t,[ptr,ptr,ctypes.c_size_t])
skip=bind('archive_read_data_skip',ctypes.c_int,[ptr])
error=bind('archive_error_string',ctypes.c_char_p,[ptr])
free=bind('archive_read_free',ctypes.c_int,[ptr])
archive=new();filters(archive);formats(archive);entries=[];total=0
def check(status):
    if status<0:raise RuntimeError((error(archive)or b'libarchive error').decode(errors='replace'))
try:
    check(open_archive(archive,str(Path(args.archive).resolve()).encode(),65536))
    entry=ptr()
    while True:
        status=header(archive,ctypes.byref(entry))
        if status==1:break
        check(status)
        raw=name(entry).decode('utf-8');path=PurePosixPath(raw.replace('\\','/'))
        if path.is_absolute()or '..' in path.parts:raise ValueError('Unsafe archive path')
        length=size(entry);mode=kind(entry);total+=max(0,length)
        if length>100*1024*1024 or total>500*1024*1024:raise ValueError('Archive exceeds asset limit')
        entries.append({'name':str(path),'size':length,'type':mode})
        if args.extract and mode==stat.S_IFREG:
            target=args.extract.joinpath(*path.parts);target.parent.mkdir(parents=True,exist_ok=True)
            if target.exists():raise ValueError('Refusing to overwrite '+str(target))
            buffer=ctypes.create_string_buffer(65536)
            with target.open('xb')as out:
                written=0
                while True:
                    n=read(archive,buffer,len(buffer));check(n)
                    if not n:break
                    written+=n
                    if written>length:raise ValueError('Decompressed length mismatch')
                    out.write(buffer.raw[:n])
            if written!=length:raise ValueError('Truncated archive member')
        else:check(skip(archive))
finally:free(archive)
print(json.dumps({'entries':entries,'total_bytes':total,'extracted':str(args.extract)if args.extract else None},ensure_ascii=False,indent=2))
