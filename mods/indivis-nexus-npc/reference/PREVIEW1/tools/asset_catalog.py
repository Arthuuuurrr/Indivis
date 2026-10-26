from pathlib import Path
from PIL import Image,ImageDraw
import zipfile,io,re
root=Path(__file__).resolve().parents[1]
with zipfile.ZipFile(root/'base/NexusCharacters-Indivis-1.21.11-PRE35-SI1.jar') as z:
 paths=sorted(p for p in z.namelist() if re.fullmatch('assets/nexuscharacters/appearance_parts_v064/outfits/outfit_[0-9]+.png',p))
 sheet=Image.new('RGB',(8*145,((len(paths)+7)//8)*160),'#303039');draw=ImageDraw.Draw(sheet)
 for i,path in enumerate(paths):
  image=Image.open(io.BytesIO(z.read(path))).convert('RGBA');image.thumbnail((128,128),Image.Resampling.NEAREST)
  x=i%8*145;y=i//8*160;sheet.paste(image,(x,y),image);draw.text((x,y+132),Path(path).name,fill='white')
 sheet.save(root/'qa/outfit-catalog.png')
