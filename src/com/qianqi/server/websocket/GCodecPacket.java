/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qianqi.server.websocket;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * Defines the class whose objects are understood by websocket encoder.
 * 
 * @author DHRUV CHOPRA
 */
public class GCodecPacket {
    private IoBuffer packet;
    
    /*
     * Builds an instance of WebSocketCodecPacket that simply wraps around 
     * the given IoBuffer.
     */
    public static GCodecPacket buildPacket(IoBuffer buffer){
        return new GCodecPacket(buffer);
    }
    
    private GCodecPacket(IoBuffer buffer){
        packet = buffer;
    }
    
    public IoBuffer getPacket(){
        return packet;
    }
}
