package net.tompsen.nexuscharacters;

import java.util.List;

/** Serializes the unchanged native 23-float polygon format without boxing. */
public final class PrimitiveSurfaceWriter {
    private PrimitiveSurfaceWriter() {}

    public static float[] write(List<SurfaceGeometry.Polygon> faces) {
        int[] quads = new int[faces.size()];
        int total = 0;
        for (int i = 0; i < faces.size(); i++) {
            List<SurfaceGeometry.Vertex> vertices = faces.get(i).vertices();
            if (!SurfaceGeometry.valid(vertices)) continue;
            int count = vertices.size() == 4 ? 1 : vertices.size() - 2;
            quads[i] = count;
            total = Math.addExact(total, Math.multiplyExact(count, 23));
        }
        float[] data = new float[total];
        int offset = 0;
        for (int i = 0; i < quads.length; i++) {
            if (quads[i] == 0) continue;
            SurfaceGeometry.Polygon face = faces.get(i);
            List<SurfaceGeometry.Vertex> v = face.vertices();
            if (v.size() == 4) {
                offset = add(data, offset, face, v.get(0), v.get(1), v.get(2), v.get(3));
            } else {
                for (int j = 1; j + 1 < v.size(); j++)
                    offset = add(data, offset, face, v.get(0), v.get(j), v.get(j + 1), v.get(j + 1));
            }
        }
        return data;
    }

    private static int add(float[] data, int offset, SurfaceGeometry.Polygon face,
            SurfaceGeometry.Vertex a, SurfaceGeometry.Vertex b,
            SurfaceGeometry.Vertex c, SurfaceGeometry.Vertex d) {
        data[offset++] = face.normal().x;
        data[offset++] = face.normal().y;
        data[offset++] = face.normal().z;
        offset = vertex(data, offset, a);
        offset = vertex(data, offset, b);
        offset = vertex(data, offset, c);
        return vertex(data, offset, d);
    }

    private static int vertex(float[] data, int offset, SurfaceGeometry.Vertex v) {
        data[offset++] = v.x(); data[offset++] = v.y(); data[offset++] = v.z();
        data[offset++] = v.u(); data[offset++] = v.v();
        return offset;
    }
}
