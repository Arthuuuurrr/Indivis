import java.net.*;
import java.nio.file.*;
import java.lang.reflect.*;
import java.util.*;
import java.lang.management.ManagementFactory;

/** Compares the actual PRE30/PRE31 writer in isolated class loaders. */
public final class WriterRegression {
    static int checks;
    static void check(boolean ok, String label) { checks++; if (!ok) throw new AssertionError(label); }
    static final class Writer implements AutoCloseable {
        final URLClassLoader loader;
        final Constructor<?> vertex, polygon, vector;
        final Method write;
        Writer(String jar, String joml) throws Exception {
            loader = new URLClassLoader(new URL[]{Path.of(jar).toUri().toURL(),Path.of(joml).toUri().toURL()}, ClassLoader.getPlatformClassLoader());
            Class<?> geometry = loader.loadClass("net.tompsen.nexuscharacters.SurfaceGeometry");
            Class<?> v = loader.loadClass("net.tompsen.nexuscharacters.SurfaceGeometry$Vertex"), p=loader.loadClass("net.tompsen.nexuscharacters.SurfaceGeometry$Polygon"), vec=loader.loadClass("org.joml.Vector3f");
            vertex=v.getConstructor(float.class,float.class,float.class,float.class,float.class);
            vector=vec.getConstructor(float.class,float.class,float.class);polygon=p.getConstructor(List.class,vec,int.class);
            write=geometry.getMethod("write",List.class);
        }
        List<Object> faces(List<float[][]> inputs, float[] normal) throws Exception {
            List<Object> out=new ArrayList<>();
            for (float[][] face:inputs) {
                List<Object> vs=new ArrayList<>();
                for(float[] v:face)vs.add(vertex.newInstance(v[0],v[1],v[2],v[3],v[4]));
                out.add(polygon.newInstance(vs,vector.newInstance(normal[0],normal[1],normal[2]),713));
            }
            return out;
        }
        float[] data(List<Object> f) throws Exception { return (float[])write.invoke(null,f); }
        public void close() throws Exception { loader.close(); }
    }
    static float[][] face(Random random, int size, int caseNo) {
        float[][] v=new float[size][5];
        for(int i=0;i<size;i++) {
            double angle=2*Math.PI*i/Math.max(1,size);float radius=.001f+random.nextFloat()*3;
            v[i]=new float[]{(float)Math.cos(angle)*radius,(float)Math.sin(angle)*radius,caseNo%7==0?random.nextFloat():.31f,random.nextFloat(),random.nextFloat()};
        }
        if(caseNo%19==0)for(float[] p:v){p[0]=p[1]=p[2]=0;}
        if(caseNo%23==0&&size>0)v[0][0]=Float.NaN;
        if(caseNo%29==0&&size>0)v[0][1]=Float.POSITIVE_INFINITY;
        if(caseNo%31==0&&size>0){v[0][3]=-0.0f;v[0][4]=Float.intBitsToFloat(0x7fc00321);}
        return v;
    }
    public static void main(String[] args) throws Exception {
        Path out=Path.of(args[3]);Files.createDirectories(out);
        try(Writer old=new Writer(args[0],args[2]);Writer fresh=new Writer(args[1],args[2])) {
            Random random=new Random(3102026);
            List<float[][]> large=new ArrayList<>();
            for(int test=0;test<4000;test++) {
                List<float[][]> input=new ArrayList<>();int count=test%13;
                for(int n=0;n<count;n++)input.add(face(random,(test+n)%17,test+n));
                float[] normal={test%11==0?-0.0f:.17f,test%37==0?Float.intBitsToFloat(0x7fc00123):.91f,-.03f};
                List<Object> a=old.faces(input,normal),b=fresh.faces(input,normal);
                float[] before=old.data(a),after=fresh.data(b);
                check(before.length==after.length,"length case="+test);
                for(int i=0;i<before.length;i++)check(Float.floatToRawIntBits(before[i])==Float.floatToRawIntBits(after[i]),"float bits case="+test+" offset="+i);
                check(Arrays.equals(before,old.data(a))&&Arrays.equals(after,fresh.data(b)),"input or output changed");
                if(test<500)large.add(face(random,4,test+1));
            }
            List<Object> a=old.faces(large,new float[]{.2f,.8f,-.1f}),b=fresh.faces(large,new float[]{.2f,.8f,-.1f});
            var bean=(com.sun.management.ThreadMXBean)ManagementFactory.getThreadMXBean();bean.setThreadAllocatedMemoryEnabled(true);
            for(int i=0;i<500;i++){old.data(a);fresh.data(b);}
            List<String> rows=new ArrayList<>();rows.add("case,sample,iterations,cpu_ns,allocated_bytes");
            for(int sample=0;sample<5;sample++)for(boolean baseline:new boolean[]{true,false}) {
                Writer writer=baseline?old:fresh;List<Object> f=baseline?a:b;int count=1000;long id=Thread.currentThread().threadId(),cpu=bean.getCurrentThreadCpuTime(),bytes=bean.getThreadAllocatedBytes(id);
                for(int i=0;i<count;i++)writer.data(f);
                rows.add((baseline?"PRE30":"PRE31")+","+sample+","+count+","+(bean.getCurrentThreadCpuTime()-cpu)+","+(bean.getThreadAllocatedBytes(id)-bytes));
            }
            Files.write(out.resolve("writer-perf.csv"),rows);
            String pass="WRITER_PASS cases=4000 checks="+checks+" raw_float_bits_equal=true";
            Files.writeString(out.resolve("writer-pass.txt"),pass+"\n");System.out.println(pass);
        }
    }
}
