/*
 * Software Grupo V, S.L.
 */
package com.martin.dal;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.NamingException;

/**
 *
 * @author jagimeno
 */
public class EntityUtil {

    /**
     * Default contructor private
     */
    private EntityUtil() {
    }

    /**
     * Return null si la colección es nula o una copia defensiva
     * 
     * @param c
     * @return 
     */
    public static Collection defensiveCopy(Collection c){
        if (c == null) {
            return null;
        }
        return c;
    }
    
    /**
     * Devuelve null si el parametro es nulo o retorna la fecha clonada.
     *
     * @param date fecha a settear
     * @return
     */
    public static Date defensiveCopy(Date date) {
        if (date == null) {
            return null;
        }
        //de momento no se clona por seguridad del codigo
        //comentado return (Date)date.clone()
        return date;
    }

    /**
     * Return null si la lista es nula o una copia defensiva
     *
     * @param <T> Entidad
     * @param list Lista
     * @return
     */
    public static <T> List<T> defensiveCopy(List<T> list) {
        if (list == null) {
            return null;
        }
        //de momento retorno directo return new ArrayList(list)
        return list;
    }

    /**
     * Return null si el array es nulo o una copia defensiva
     *
     * @param <T> Entidad
     * @param array array
     * @return
     */
    public static <T> T[] defensiveCopy(T[] array) {
        if (array == null) {
            return null;
        }
        //de momento retorno directo 
        return array;
    }

    /**
     * Return null si el array es nulo o una copia defensiva
     *
     * @param array array booleano
     * @return
     */
    public static boolean[] defensiveCopy(boolean[] array) {
        if (array == null) {
            return null;
        }
        //de momento retorno directo 
        return array;
    }

    /**
     * Return null si el array es nulo o una copia defensiva
     *
     * @param array array long
     * @return
     */
    public static long[] defensiveCopy(long[] array) {
        if (array == null) {
            return null;
        }
        //de momento retorno directo 
        return array;
    }

    /**
     * Return null si el array es nulo o una copia defensiva
     *
     * @param array array short
     * @return
     */
    public static short[] defensiveCopy(short[] array) {
        if (array == null) {
            return null;
        }
        //de momento retorno directo 
        return array;
    }

    /**
     * Return null si el array es nulo o una copia defensiva
     *
     * @param array array byte
     * @return
     */
    public static byte[] defensiveCopy(byte[] array) {
        if (array == null) {
            return null;
        }
        //de momento retorno directo 
        return array;
    }

    /**
     * Return null si el array es nulo o una copia defensiva
     *
     * @param array array int
     * @return
     */
    public static int[] defensiveCopy(int[] array) {
        if (array == null) {
            return null;
        }
        //de momento retorno directo 
        return array;
    }

    /**
     * Return null si el array es nulo o una copia defensiva
     *
     * @param array array double
     * @return
     */
    public static double[] defensiveCopy(double[] array) {
        if (array == null) {
            return null;
        }
        //de momento retorno directo 
        return array;
    }

    /**
     * Return null si el array es nulo o una copia defensiva
     *
     * @param array array float
     * @return
     */
    public static float[] defensiveCopy(float[] array) {
        if (array == null) {
            return null;
        }
        //de momento retorno directo 
        return array;
    }

    /**
     * Vacia el buffer (flush) y cierra el stream, si falla loguea la excecion
     * con warn.
     *
     * @param out
     */
    public static void silentFlushAndClose(OutputStream out) {
        try {
            if (out != null) {
                out.flush();
                out.close();
            }
        } catch (IOException ioex) {
            Logger.getLogger(EntityUtil.class.getName()).log(Level.WARNING,ioex.getMessage(),ioex);
        }
    }

    /**
     * Vacia el buffer (flush) y si falla loguea la excecion con warn.
     *
     * @param flushable
     */
    public static void silentFlush(Flushable flushable) {
        try {
            if (flushable != null) {
                flushable.flush();
            }
        } catch (IOException ioex) {
            Logger.getLogger(EntityUtil.class.getName()).log(Level.WARNING,ioex.getMessage(),ioex);
        }
    }

    /**
     * Cierra el objeto pasado y si falla loguea la excecion con warn.
     *
     * @param closeable
     */
    public static void silentClose(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ioex) {
            Logger.getLogger(EntityUtil.class.getName()).log(Level.WARNING,ioex.getMessage(),ioex);
        }
    }

    /**
     * Cierra el objeto pasado y si falla loguea la excecion con warn.
     *
     * @param context
     */
    public static void silentClose(Context context) {
        try {
            if (context != null) {
                context.close();
            }
        } catch (NamingException nex) {
            Logger.getLogger(EntityUtil.class.getName()).log(Level.WARNING,nex.getMessage(),nex);
        }
    }

}
