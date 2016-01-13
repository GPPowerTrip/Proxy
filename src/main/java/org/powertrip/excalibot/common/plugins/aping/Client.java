
import java.io.*;
import java.net.DatagramPacket;
import java.net.Socket;
import java.net.UnknownHostException;
import socks.Socks5DatagramSocket;
import socks.Socks5Proxy;

import socks.SocksSocket;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author luisx_000
 */

//Maximum datagram size

public class Client {
    /* Example of using a proxy in java */
 
// date: 01.02.2011
 
// Author: Plaguez/Omenz
 
// » java ProxyExample 68.7.119.80 27977 www.host.com 9999
 
// Proxy list: aliveproxy.com/socks5-list/
 
 
 
      static final int MAX_DATAGRAM_SIZE = 1024;
      socks.Proxy proxy=null;
      
      Socket sock=null;
      Socks5DatagramSocket udp_sock;
      InputStream in=null;
      OutputStream out=null;
 
      String proxyHost=null;
 
      int proxyPort=0;
 
      String host=null;
 
      int port=0;
      
      String mode = null;
 
 
 
      public Client(String argz[])
 
      {
 
            try
 
            {
 
                  // read args
 
                proxyHost = argz[0];

                proxyPort = Integer.parseInt(argz[1]);

                mode = argz[2];

                  
                proxy = new Socks5Proxy(proxyHost, proxyPort);

                if (mode.equals("CONNECT_MODE")) {
                    doConnect(proxy, proxyHost, proxyPort);
                    doPipe();
                } else if (mode.equals("UDP_MODE")) {
                    startUDP();
                    doUDPPipe();
                }

                   
                //sendUDP("teste", host, port);
                   
                   
 
 
            }
 
               catch(Exception e)
 
               {
 
                  e.printStackTrace();
 
               }
 
 
 
      }
 
 
 
      public static void main(String argz[])
 
      {
 
            if(argz.length <2) {
 
                  System.err.println("Usage: java ProxyExample <socksHost> <port> <MODE> ");
 
            }
 
         new Client(argz);
 
      }
      
    private void doConnect(socks.Proxy proxy,String host,int port) throws IOException{
        System.out.println("Trying to connect to:"+host+":"+port);
        System.out.println("Using proxy:"+proxy);
        sock = new SocksSocket(proxy,host,port);
        System.out.println("Connected to:"+sock.getInetAddress()+":"+port);
        System.out.println("Connected to: "+sock.getInetAddress().getHostAddress()
                           +":" +port);
        System.out.println("Via-Proxy:"+sock.getLocalAddress()+":"+
                              sock.getLocalPort());

   }
    
   private void doPipe() throws IOException{
      out = sock.getOutputStream();
      in = sock.getInputStream();

      byte[] buf = new byte[1024];
      int bytes_read;
      while((bytes_read = in.read(buf)) > 0){
          write.println(new String(buf,0,bytes_read));
      }

   }
   private void startUDP() throws IOException{
    udp_sock = new Socks5DatagramSocket(proxy,0,null);
    System.out.println("UDP started on "+udp_sock.getLocalAddress()+":"+
                               udp_sock.getLocalPort());
    System.out.println("UDP:"+udp_sock.getLocalAddress().getHostAddress()+":"
                  +udp_sock.getLocalPort());
   }

   private void doUDPPipe() throws IOException{
     DatagramPacket dp = new DatagramPacket(new byte[MAX_DATAGRAM_SIZE],
                                            MAX_DATAGRAM_SIZE);
     while(true){
       udp_sock.receive(dp);
       System.out.println("UDP\n"+
	     "From:"+dp.getAddress()+":"+dp.getPort()+"\n"+
	     "\n"+
             //Java 1.2
             //new String(dp.getData(),dp.getOffset(),dp.getLength())+"\n"
             //Java 1.1
             new String(dp.getData(),0,dp.getLength())+"\n"
            );
       dp.setLength(MAX_DATAGRAM_SIZE);
     }
   }
   
   private void sendUDP(String message,String host,int port){
      if(!udp_sock.isProxyAlive(100)){
          System.out.println("Proxy closed connection");
         //abort_connection();
         return;
      }

      try{
         byte[] data = message.getBytes();
         DatagramPacket dp = new DatagramPacket(data,data.length,null,port);
         udp_sock.send(dp,host);
      }catch(UnknownHostException uhe){
         System.out.println("Host "+host+" has no DNS entry.");
      }catch(IOException ioe){
         System.out.println("IOException:"+ioe);
         //abort_connection();
      }

   }

   private void send(String s){
      try{
        out.write(s.getBytes());
      }catch(IOException io_ex){
        System.out.println("IOException:"+io_ex);
        //abort_connection();
      }
   }
 
 
      DataInputStream din=null;
 
      PrintWriter write=null;
 
      BufferedReader rdr=null;
 
}



