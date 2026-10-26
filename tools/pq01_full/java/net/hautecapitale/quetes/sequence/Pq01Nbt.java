package net.hautecapitale.quetes.sequence;

import java.lang.reflect.*;

/** Copie par le contrat NbtElement, commun aux composés, listes et valeurs. */
final class Pq01Nbt {
  private static final Method COPY;
  static {
    try {COPY=Class.forName("net.minecraft.class_2520").getMethod("method_10707");}
    catch(ReflectiveOperationException e){throw new ExceptionInInitializerError(e);}
  }
  private Pq01Nbt(){}
  static Object copy(Object value){
    if(value==null)return null;
    try {return COPY.invoke(value);}
    catch(ReflectiveOperationException e){throw new IllegalStateException("Copie NBT PQ01 impossible",e instanceof InvocationTargetException i?i.getCause():e);}
  }
}
