import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

/** Persist the harness's result independently of the client's logging configuration. */
public final class EvidenceLog {
  public static void install() {
    try {
      Path folder=Path.of(System.getProperty("evidenceDir","pre24/evidence/actual"));
      Files.createDirectories(folder);
      OutputStream file=Files.newOutputStream(folder.resolve("status.log"));
      PrintStream previous=System.out;
      OutputStream tee=new OutputStream() {
        public synchronized void write(int value)throws IOException {file.write(value);previous.write(value);}
        public synchronized void write(byte[] b,int off,int n)throws IOException {file.write(b,off,n);previous.write(b,off,n);}
        public synchronized void flush()throws IOException {file.flush();previous.flush();}
      };
      PrintStream stream=new PrintStream(tee,true,StandardCharsets.UTF_8);
      System.setOut(stream);System.setErr(stream);
      System.out.println("TEST_MOD_VERSION "+net.fabricmc.loader.api.FabricLoader.getInstance().getModContainer("nexuscharacters").orElseThrow().getMetadata().getVersion());
    }catch(IOException e){throw new IllegalStateException(e);}
  }
}
