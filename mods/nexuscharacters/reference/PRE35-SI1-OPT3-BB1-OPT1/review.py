"""Contact sheets from actual final-client PNGs; never synthetic renders."""
from pathlib import Path
import json,math
from PIL import Image,ImageDraw,ImageFont
R=Path(__file__).resolve().parent;P=R.parent/'presets_import';Q=P/'previous/npc/qa/evidence-opt1-final-assets'
entries=json.loads((P/'qa/build.json').read_text())['entries'];OUT=R/'qa/review';OUT.mkdir(exist_ok=True)
font=ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf',12)
for kind in ['coiffures','barbes','oreilles']:
 rows=[(i,e)for i,e in enumerate(entries)if e['kind']==kind]
 for page in range(math.ceil(len(rows)/12)):
  subset=rows[page*12:(page+1)*12];sheet=Image.new('RGB',(1200,math.ceil(len(subset)/4)*240),'#eee8da');draw=ImageDraw.Draw(sheet)
  for cell,(index,entry)in enumerate(subset):
   x=cell%4*300;y=cell//4*240
   for side in range(2):
    name=f'preset-{index:02d}-back.png'if side else f'preset-{index:02d}-{kind}-{entry["id"]}.png'
    with Image.open(Q/name)as image:
     crop=image.crop((1100,200,1700,980));crop.thumbnail((150,300));sheet.paste(crop,(x+side*150,y+25))
   draw.text((x+5,y+5),f'{index:02d} {entry["label"]}',fill='black',font=font)
  sheet.save(OUT/f'{kind}-{page+1}.png')
print('CONTACT_SHEETS_READY',len(list(OUT.glob('*.png'))))
