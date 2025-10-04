package com.ferre.config;

import com.ferre.model.Usuario;

public final class Session {
    private static Usuario current;
    private Session(){}
    public static void set(Usuario u){ current = u; }
    public static Usuario get(){ return current; }
    public static void clear(){ current = null; }
}
