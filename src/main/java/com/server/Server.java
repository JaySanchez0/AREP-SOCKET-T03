package com.server;

import com.server.request.Request;
import com.server.socket.SocketApp;
import java.util.function.Function;

/**
 * Hello world!
 *
 */
public class Server
{

    private static SocketApp app;

    /**
     * Crea el servidor
     * @param port puerto en que corre el servidor
     */
    public static void port(int port){
        app = SocketApp.getInstance(port);
    }

    /**
     *
     * @param directory directorio en el cual buscar archivos estaticos
     */
    public static void staticFiles(String directory){
        app.setStaticFolder(directory);
    }

    /**
     * Asociar evento a solicitud get
     * @param path ruta a signar
     * @param fun funcion a ejecutar
     */
    public static void get(String path,Function<Request,String> fun){
        app.addFunction("GET",path,fun);
    }

    /**
     * Apaga el servidor
     */
    public static void close(){
        app.stop();
    }

}
