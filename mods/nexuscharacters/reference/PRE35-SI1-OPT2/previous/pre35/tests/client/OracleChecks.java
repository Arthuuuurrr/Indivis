import java.nio.file.*;
public final class OracleChecks {
 public static void finish(Path out)throws Exception{
  if(!Boolean.getBoolean("pre35.oracle"))return;
  Class<?> c=Class.forName("net.tompsen.nexuscharacters.SurfaceComparisonOracle");
  long calls=c.getField("calls").getLong(null),polygons=c.getField("polygons").getLong(null),vertices=c.getField("vertices").getLong(null);
  if(calls<100||polygons<10000)throw new AssertionError("Oracle did not run");
  String pass="ORACLE35_PASS calls="+calls+" polygons="+polygons+" vertices="+vertices;
  Files.createDirectories(out);Files.writeString(out.resolve("oracle-counts.txt"),pass+"\n");System.out.println(pass);
 }
}

