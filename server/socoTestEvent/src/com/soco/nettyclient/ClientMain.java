package com.soco.nettyclient;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

public class ClientMain {

    static String HOST = System.getProperty("host", "52.26.115.134");
    //static String HOST = System.getProperty("host", "127.0.0.1");
    static int PORT = Integer.parseInt(System.getProperty("port", "28992"));

    public static void main(String[] args) throws Exception {
        // Configure SSL.
        final SslContext sslCtx = SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE);

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            if (args.length >= 2){
                HOST = args[0];
                PORT = Integer.parseInt(args[1]);
            } 
            
            System.out.println("Usage: Client.jar "+HOST + " : " + PORT);
            
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             .handler(new ClientInitializer(sslCtx));

            // Start the connection attempt.
            Channel ch = b.connect(HOST, PORT).sync().channel();

            // Read commands from the stdin.
            ChannelFuture lastWriteFuture = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            for (;;) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }
                String outStr = line;
                System.out.println("send msg:"+outStr);
                System.out.println("send msg size:"+outStr.length());
                // Sends the received line to the server.
                lastWriteFuture = ch.writeAndFlush(outStr+"\r\n");
                // If user typed the 'bye' command, wait until the server closes
                // the connection.
                if ("bye".equals(line.toLowerCase())) {
                    ch.closeFuture().sync();
                    break;
                }
            }

            // Wait until all messages are flushed before closing the channel.
            if (lastWriteFuture != null) {
                lastWriteFuture.sync();
            }
        } catch(Exception e){
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
        } finally {
            // The connection is closed automatically on shutdown.
            group.shutdownGracefully();
            System.out.println("bye!");
        }
    }
    
    private static String encodeMsgData(String msg){
        String msgStr="";
        

        
        return msgStr;
    }
}
