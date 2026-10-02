import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/** Minimal RCON client: Rcon <host> <port> <password> <command...> — prints the server's reply. */
public class Rcon {
    static int id = 0;
    public static void main(String[] a) throws Exception {
        try (Socket s = new Socket(a[0], Integer.parseInt(a[1]))) {
            s.setSoTimeout(15000);
            OutputStream out = s.getOutputStream();
            DataInputStream in = new DataInputStream(s.getInputStream());
            send(out, 3, a[2]);                       // login
            if (read(in) == -1) { System.out.println("RCON AUTH FAILED"); return; }
            StringBuilder cmd = new StringBuilder();
            for (int i = 3; i < a.length; i++) { if (i > 3) cmd.append(' '); cmd.append(a[i]); }
            for (String line : cmd.toString().split("\s*;;\s*")) {
                if (line.isBlank()) continue;
                send(out, 2, line);
                read(in);
            }
        }
    }
    static void send(OutputStream out, int type, String body) throws IOException {
        byte[] b = body.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buf = ByteBuffer.allocate(14 + b.length).order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(10 + b.length).putInt(++id).putInt(type).put(b).put((byte) 0).put((byte) 0);
        out.write(buf.array()); out.flush();
    }
    static int read(DataInputStream in) throws IOException {
        byte[] head = new byte[4]; in.readFully(head);
        int len = ByteBuffer.wrap(head).order(ByteOrder.LITTLE_ENDIAN).getInt();
        byte[] rest = new byte[len]; in.readFully(rest);
        ByteBuffer b = ByteBuffer.wrap(rest).order(ByteOrder.LITTLE_ENDIAN);
        int rid = b.getInt(); b.getInt();
        String body = new String(rest, 8, len - 10, StandardCharsets.UTF_8);
        if (!body.isBlank()) System.out.println("> " + body.trim());
        return rid;
    }
}
