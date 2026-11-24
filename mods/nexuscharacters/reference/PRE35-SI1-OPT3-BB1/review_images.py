"""QA contact sheets of actual in-client captures; not replacement concept renders."""
from pathlib import Path
import argparse,json,math
from PIL import Image,ImageDraw,ImageFont
p=argparse.ArgumentParser();p.add_argument('evidence',type=Path);args=p.parse_args()
r=Path(__file__).resolve().parent
entries=json.loads((r/'qa/build.json').read_text())['entries']
out=r/'qa/review';out.mkdir(exist_ok=True)
font=ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf',12)
for kind in ['coiffures','barbes','oreilles']:
    rows=[(i,e)for i,e in enumerate(entries)if e['kind']==kind]
    for page in range(math.ceil(len(rows)/12)):
        sheet=Image.new('RGB',(1200,math.ceil(len(rows[page*12:(page+1)*12])/4)*240),'#eee8da');draw=ImageDraw.Draw(sheet)
        for cell,(index,entry)in enumerate(rows[page*12:(page+1)*12]):
            x=cell%4*300;y=cell//4*240
            for side in range(2):
                name=f'preset-{index:02d}-back.png'if side else f'preset-{index:02d}-{kind}-{entry["id"]}.png'
                with Image.open(args.evidence/name)as source:
                    crop=source.crop((1100,200,1700,980));crop.thumbnail((150,300));sheet.paste(crop,(x+side*150,y+25))
            draw.text((x+5,y+5),f'{index:02d} {entry["label"]}',fill='black',font=font)
        sheet.save(out/f'{kind}-{page+1}.png')
