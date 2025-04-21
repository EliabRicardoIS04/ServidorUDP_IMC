package eliabJarabaServer.imc.servidor;

import eliabJarabaServer.imc.vistas.VentanaPrincipal;
import java.awt.Color;
import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
 /*import EliabRJaraba.imc.servidor.subProcesoCliente;*/
import eliabJarabaServer.imc.modelo.calculoimc;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ServidorUDP extends Thread {

    private Boolean estado;
    /* public static Map<String, subProcesoCliente> listaDeClientes;*/
    private Integer puerto = 9007;
    private byte[] buffer = new byte[64];
    private byte[] buffer2 = new byte[64];
    DatagramSocket socketudp;
    DatagramPacket peticion;
    int puertoCliente;
    calculoimc imc;
    List<String> DatosUsuario = new ArrayList<>() ;
    List<String> resultado = new ArrayList<>();
    
    private VentanaPrincipal ventana;

    public ServidorUDP(Integer puerto, VentanaPrincipal v) {
        if (puerto != null || puerto != 0) {
            this.puerto = puerto;
        }
        ventana = v;
        
    }

    
    public void run() {
        super.run();
        try {
            iniciarServicio();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServidorUDP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void iniciarServicio() throws ClassNotFoundException {
        try {
            socketudp = new DatagramSocket(puerto);
            estado = true;
            ventana.getBtnIniciar().setText("DETENER");
            ventana.getTxtEstado().setText("ONLINE");
            ventana.getTxtEstado().setForeground(Color.green);
            ventana.getBtnIniciar().setForeground(Color.RED);

            String msg = log() + "Servidor disponible en el puerto " + puerto;
            System.out.println(msg);
            ventana.getCajaLog().append(msg + "\n");
            while (estado) {

                msg = log() + "Cliente " + socketudp.getLocalSocketAddress() + " conectado";
                System.out.println(msg + "\n");
                ventana.getCajaLog().append(msg + "\n");
                String ipUsusario = new String("sc");
                ventana.getCampoIP().setText( String.valueOf(socketudp.getLocalSocketAddress()));
                String msg1 = log() + "¡Hola mundo desde el cliente!";
                System.out.println(msg1 + "\n");
                //Preparo la respuesta
                DatagramPacket peticion = new DatagramPacket(buffer, buffer.length);
                ipUsusario = peticion.getSocketAddress().toString();
                System.out.println("-usuario: " + ipUsusario + "\n" + "-");
                //Recibo el datagrama
                socketudp.receive(peticion);
                String msg2 = log() + "Recibo la informacion del cliente";
                System.out.println(msg2 + "\n");

                //Convierto lo recibido y mostrar el mensaje
               DatosUsuario = bytesToList(peticion.getData());
                String msg3 = log() + "petivion.getData(): "+ DatosUsuario ;
                System.out.println(msg3 + "\n");
                imc= new calculoimc(Float.parseFloat(DatosUsuario.get(0)),Float.parseFloat(DatosUsuario.get(1)));
                

                imc.getImc();
                
                resultado.add(0,imc.imc.mensaje);
                resultado.add(1,String.valueOf(imc.imc.resultado));
                //Obtengo el puerto y la direccion de origen
                //Sino se quiere responder, no es necesario
                int puertoCliente = peticion.getPort();
                InetAddress direccion = peticion.getAddress();

                /*mensaje = "¡Hola mundo desde el servidor!";
                buffer = mensaje.getBytes();*/
                System.out.println("resultado: " + resultado);
                buffer2 = listToBytes(resultado);

                //creo el datagrama
                DatagramPacket respuesta = new DatagramPacket(buffer2, buffer2.length, direccion, puertoCliente);

                //Envio la información
                System.out.println("Envio la informacion del cliente");
                socketudp.send(respuesta);

                /*subProcesoCliente atencion = new subProcesoCliente(cliente, ventana);
                ServidorTcp.listaDeClientes.put(ip, atencion);
                atencion.start();*/
            }

        } catch (IOException e) {
            String msg = log() + "ERROR al abrir el puerto" + puerto;
            System.out.println(msg);
            ventana.getCajaLog().append(msg + "\n");
            ventana.getBtnIniciar().setText("INICIAR");
            ventana.getTxtEstado().setText("OFF LINE");
        }
    }

    public void detenerServicio() {
        if (estado) {
            estado = false;
            ventana.getBtnIniciar().setText("INICIAR");
            ventana.getBtnIniciar().setForeground(Color.GREEN);
            ventana.getTxtEstado().setText("OFF LINE");
            ventana.getTxtEstado().setForeground(Color.RED);
            /*ServidorTcp.listaDeClientes.entrySet().stream().map(new Function<Map.Entry<String, subProcesoCliente>, String>() {
                @Override
                public String apply(Map.Entry<String, subProcesoCliente> elemento) {
                    String ip = elemento.getKey();
                    subProcesoCliente cliente = elemento.getValue();
                    String msg = log() + "Desconectando cliente " + ip;
                    System.out.println(msg);
                    ventana.getCajaLog().append(msg + "\n");
                    try {
                        cliente.getCliente().close();
                        cliente = null;
                        ServidorTcp.listaDeClientes.remove(elemento);
                        msg = log() + "Cliente desconectado" + ip;
                        System.out.println(msg);
                        ventana.getCajaLog().append(msg + "\n");
                    } catch (IOException ex) {
                        cliente = null;
                        ServidorTcp.listaDeClientes.remove(elemento);
                        msg = log() + "Cliente desconectado" + ip;
                        System.out.println(msg);
                        ventana.getCajaLog().append(msg + "\n");
                    }
                    return ip;
                }
            }).forEachOrdered(ip -> {
                System.out.println("cliente " + ip + " Desconectado");
            });*/
            socketudp.close();
        }

    }
    
     public byte[] listToBytes(List<String> stringList) {

        String joined = String.join("|||", stringList);

        return joined.getBytes(StandardCharsets.UTF_8);
    }

    public List<String> bytesToList(byte[] bytes) {
        String joined = new String(bytes, StandardCharsets.UTF_8);
        return Arrays.asList(joined.split("\\|\\|\\|"));
    }

    public String log() {
        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
        return f.format(new Date()) + " - ";
    }

}
