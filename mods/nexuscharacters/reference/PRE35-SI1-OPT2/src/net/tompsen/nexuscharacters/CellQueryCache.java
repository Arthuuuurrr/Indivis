package net.tompsen.nexuscharacters;

/** Invocation-owned exact seven-integer query cache. No packed coordinates,
 * temporary key objects, global state or cached mutable model references.
 * As in OPT1, at most 4096 answers survive for the lifetime of one Grid index.
 */
final class CellQueryCache {
    static final int LIMIT=4096;
    private int[] keys;
    private int[][] values;
    private int size;

    int size() { return size; }

    int[] get(int x0,int x1,int y0,int y1,int z0,int z1,int mask) {
        if(values==null)return null;
        int at=hash(x0,x1,y0,y1,z0,z1,mask)&(values.length-1);
        while(values[at]!=null) {
            if(matches(at,x0,x1,y0,y1,z0,z1,mask))return values[at];
            at=(at+1)&(values.length-1);
        }
        return null;
    }

    void put(int x0,int x1,int y0,int y1,int z0,int z1,int mask,int[] value) {
        if(value==null)throw new NullPointerException("null answers are not cached");
        if(values==null)resize(16);
        int at=hash(x0,x1,y0,y1,z0,z1,mask)&(values.length-1);
        while(values[at]!=null) {
            if(matches(at,x0,x1,y0,y1,z0,z1,mask)){values[at]=value;return;}
            at=(at+1)&(values.length-1);
        }
        if(size==LIMIT)return;
        if((size+1)*2>values.length) {
            resize(values.length*2);
            at=hash(x0,x1,y0,y1,z0,z1,mask)&(values.length-1);
            while(values[at]!=null)at=(at+1)&(values.length-1);
        }
        int k=at*7;
        keys[k]=x0;keys[k+1]=x1;keys[k+2]=y0;keys[k+3]=y1;
        keys[k+4]=z0;keys[k+5]=z1;keys[k+6]=mask;
        values[at]=value;size++;
    }

    private boolean matches(int at,int x0,int x1,int y0,int y1,int z0,int z1,int mask) {
        int k=at*7;
        return keys[k]==x0&&keys[k+1]==x1&&keys[k+2]==y0&&keys[k+3]==y1
            &&keys[k+4]==z0&&keys[k+5]==z1&&keys[k+6]==mask;
    }

    private void resize(int capacity) {
        int[] oldKeys=keys;int[][] oldValues=values;
        keys=new int[capacity*7];values=new int[capacity][];
        if(oldValues==null)return;
        for(int i=0;i<oldValues.length;i++)if(oldValues[i]!=null) {
            int k=i*7;
            int at=hash(oldKeys[k],oldKeys[k+1],oldKeys[k+2],oldKeys[k+3],oldKeys[k+4],oldKeys[k+5],oldKeys[k+6])&(capacity-1);
            while(values[at]!=null)at=(at+1)&(capacity-1);
            System.arraycopy(oldKeys,k,keys,at*7,7);values[at]=oldValues[i];
        }
    }

    private static int hash(int a,int b,int c,int d,int e,int f,int g) {
        int h=0x9e3779b9;
        h=(h^a)*0x85ebca6b;h=(h^b)*0x85ebca6b;h=(h^c)*0x85ebca6b;
        h=(h^d)*0x85ebca6b;h=(h^e)*0x85ebca6b;h=(h^f)*0x85ebca6b;
        h=(h^g)*0x85ebca6b;h^=h>>>16;return h;
    }
}
