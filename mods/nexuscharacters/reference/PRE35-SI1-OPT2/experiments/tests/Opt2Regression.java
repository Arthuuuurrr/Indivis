package net.tompsen.nexuscharacters;

import java.util.*;

/** Test for rejected experimental cache; NOT part of release validation. */
public final class Opt2Regression {
    record Key(int a,int b,int c,int d,int e,int f,int g) {}
    static int checks;
    static void check(boolean ok,String message){checks++;if(!ok)throw new AssertionError(message);}
    static int[] get(CellQueryCache c,Key k){return c.get(k.a,k.b,k.c,k.d,k.e,k.f,k.g);}
    static void put(CellQueryCache c,Key k,int[] v){c.put(k.a,k.b,k.c,k.d,k.e,k.f,k.g,v);}
    static void cache() {
        Random r=new Random(0x354f505432L);
        for(int round=0;round<12;round++) {
            CellQueryCache cache=new CellQueryCache();Map<Key,int[]> reference=new LinkedHashMap<>();
            check(cache.size()==0&&get(cache,new Key(0,0,0,0,0,0,0))==null,"New cache is not empty");
            for(int i=0;i<5100;i++) {
                Key key=i==0?new Key(0,0,0,0,0,0,0):new Key(r.nextInt(),r.nextInt(),r.nextInt(),r.nextInt(),r.nextInt(),r.nextInt(),r.nextInt(64));
                int[] answer=i%11==0?new int[0]:new int[]{round,i};
                put(cache,key,answer);if(reference.size()<CellQueryCache.LIMIT)reference.put(key,answer);
                check(get(cache,key)==reference.get(key),"Full integer key changed");
                check(cache.size()==reference.size(),"Cache bound/growth differs");
            }
            for(var e:reference.entrySet()) {
                Key key=e.getKey();check(get(cache,key)==e.getValue(),"Growth lost answer identity");
                int[] fields={key.a,key.b,key.c,key.d,key.e,key.f,key.g};
                for(int j=0;j<7;j++) {
                    fields[j]^=1<<21;Key changed=new Key(fields[0],fields[1],fields[2],fields[3],fields[4],fields[5],fields[6]);
                    check(get(cache,changed)==reference.get(changed),"Packed coordinate/flag alias");fields[j]^=1<<21;
                }
            }
            var key=reference.keySet().iterator().next();int[] replacement={99};
            put(cache,key,replacement);check(get(cache,key)==replacement&&cache.size()==CellQueryCache.LIMIT,"Existing key replacement at capacity failed");
        }
    }
    public static void main(String[] args)throws Exception {
        cache();System.out.println("OPT2_REGRESSION_PASS checks="+checks+" full-32-bit-keys/bounds/empty-answers/growth");
    }
}

