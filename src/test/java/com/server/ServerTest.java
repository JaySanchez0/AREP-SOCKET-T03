package com.server;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;


import static org.junit.Assert.assertEquals;

public class ServerTest {
    /**
     * Inicializa el servidor para pruebas (Antes de ejecutar todos los test)
     */
    @BeforeClass
    public static void startServer(){
        System.out.println("Start");
        Server.port(getPort());
        Server.staticFiles("static");
    }

    static int getPort() {
        return 3032;
    }

    /**
     *  Valida que el html respondido corresponda al esperado
     * @throws IOException
     */
    @Test
    public void wouldBeResolveIndexHtml() throws IOException {
        URL url = new URL("http://localhost:"+getPort()+"/index.html");
        URLConnection con = url.openConnection();
        assertEquals(con.getHeaderField("Content-Type"),"text/html");
        FileResolve fs = new FileResolve();
        assertEquals("HTTP/1.1 200 OK",con.getHeaderField(0));
        String resp = fs.readFile(fs.getFile("static/index.html"));
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String exp = "";
        String line;
        while((line=in.readLine())!=null) exp = exp+line+"\n";
        in.close();
        assertEquals(resp+"\n",exp);

    }

    /**
     * Valida que el javascript solicitado corresponda al esperado
     * @throws Exception
     */
    @Test
    public void ShouldBeResolveJsFile() throws Exception{
        URL url = new URL("http://localhost:"+getPort()+"/js/app.js");
        URLConnection con = url.openConnection();
        assertEquals(con.getHeaderField("Content-Type"),"text/javascript");
        assertEquals("HTTP/1.1 200 OK",con.getHeaderField(0));
        FileResolve fs = new FileResolve();
        String resp = fs.readFile(fs.getFile("static/js/app.js"));
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String exp = "";
        String line;
        while((line=in.readLine())!=null) exp = exp+line+"\n";
        in.close();
        assertEquals(resp+"\n",exp);

    }

    /**
     * Valida que el css solicitado corresponda al esperado
     * @throws Exception
     */
    @Test
    public void ShouldBeResolveCssFile() throws Exception{
        URL url = new URL("http://localhost:"+getPort()+"/style.css");
        URLConnection con = url.openConnection();
        assertEquals(con.getHeaderField("Content-Type"),"text/css");
        assertEquals("HTTP/1.1 200 OK",con.getHeaderField(0));
        FileResolve fs = new FileResolve();
        String resp = fs.readFile(fs.getFile("static/style.css"));
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String exp = "";
        String line;
        while((line=in.readLine())!=null) exp = exp+line+"\n";
        in.close();
        assertEquals(resp+"\n",exp);
    }

    /**
     * Valida que al escribir un metodo get la aplicacion al pedir al url efectivamente reponda con la respuesta esperada
     * @throws Exception
     */
    @Test
    public void shouldBeResponseAnGet() throws Exception{
        Server.get("/suma",(request)->{
            int a = Integer.parseInt(request.getParameter("a"));
            int b = Integer.parseInt(request.getParameter("b"));
            return String.valueOf(a+b);
        });
        URL url = new URL("http://localhost:"+getPort()+"/suma?a=2&&b=3");
        URLConnection con = url.openConnection();
        assertEquals(con.getHeaderField(0),"HTTP/1.1 200 OK");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        int suma = Integer.parseInt(in.readLine());
        assertEquals(2+3,suma);
    }

    /**
     * Valida si no existe un recurso, reponda con un estado 404
     * @throws Exception
     */
    @Test
    public void ShouldBeSend404IfNotFound() throws Exception{
        URL url = new URL("http://localhost:"+getPort()+"/workup");
        String header = url.openConnection().getHeaderField(0);
        assertEquals(header,"HTTP/1.1 404 NOT FOUND");
    }

    /**
     * Valida que la imagen recibida corresponda a la esperada byte a byte
     * @throws Exception
     */
    @Test
    public void shouldBeResponseAnImage() throws Exception{
        URL url = new URL("http://localhost:"+getPort()+"/porter2.PNG");
        URLConnection con = url.openConnection();
        assertEquals(con.getHeaderField(0),"HTTP/1.1 200 OK");
        BufferedImage img = ImageIO.read(con.getInputStream());
        BufferedImage save = ImageIO.read(getClass().getClassLoader().getResource("static/porter2.PNG"));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        ImageIO.write(img,"png",out);
        ImageIO.write(save,"png",out2);
        byte[] arr = out.toByteArray();
        byte[] arr2 = out2.toByteArray();
        assertEquals(arr.length,arr2.length);
        for(int i=0;i<arr.length;i++) assertEquals(arr[i],arr2[i]);
    }

    @Test
    public void shouldBeChangeFolderFromStatic() throws Exception{
        URL url = new URL("http://localhost:"+getPort()+"/find.html");
        URLConnection con = url.openConnection();
        assertEquals(con.getHeaderField(0),"HTTP/1.1 404 NOT FOUND");
        Server.staticFiles("appstatic");
        url = new URL("http://localhost:"+getPort()+"/find.html");
        con = url.openConnection();
        assertEquals(con.getHeaderField(0),"HTTP/1.1 200 OK");



    }

    /**
     * Al finalizar la ejecucion de las pruebas termina el servidor de pruebas
     */
    @AfterClass
    public static void stop(){
        System.out.println("Stop");
        Server.close();
    }

}
