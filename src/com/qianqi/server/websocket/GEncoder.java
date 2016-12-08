/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qianqi.server.websocket;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.textline.TextLineEncoder;

/**
 * Encodes incoming buffers in a manner that makes the receiving client type transparent to the 
 * encoders further up in the filter chain. If the receiving client is a native client then
 * the buffer contents are simply passed through. If the receiving client is a websocket, it will encode
 * the buffer contents in to WebSocket DataFrame before passing it along the filter chain.
 * 
 * Note: you must wrap the IoBuffer you want to send around a WebSocketCodecPacket instance.
 * 
 * @author DHRUV CHOPRA
 */
public class GEncoder extends ProtocolEncoderAdapter{

//	private static final AttributeKey ENCODER = new AttributeKey(GEncoder.class, "encoder");
//	private final Charset charset = Charset.forName("UTF-8") ;
    @Override
    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {  
    	boolean isHandshakeResponse = message instanceof GHandShakeResponse;
        boolean isDataFramePacket = message instanceof GCodecPacket;
        boolean isRemoteWebSocket = session.containsAttribute(GWebSocketUtils.SessionAttribute) && (true==(Boolean)session.getAttribute(GWebSocketUtils.SessionAttribute));
       
//        CharsetEncoder encoder = (CharsetEncoder) session.getAttribute(ENCODER);
//
//        if (encoder == null) {
//            encoder = charset.newEncoder();
//            session.setAttribute(ENCODER, encoder);
//        }
        
        IoBuffer resultBuffer;
        if(isHandshakeResponse){
            GHandShakeResponse response = (GHandShakeResponse)message;
            resultBuffer = GEncoder.buildWSResponseBuffer(response);
        }
        else if(isDataFramePacket){
            GCodecPacket packet = (GCodecPacket)message;
            resultBuffer = isRemoteWebSocket ? GEncoder.buildWSDataFrameBuffer(packet.getPacket()) : packet.getPacket();
        }
        else{
        	resultBuffer = GEncoder.buildWSResponseBuffer(new GHandShakeResponse(message.toString()));
        	GCodecPacket packet = GCodecPacket.buildPacket(resultBuffer);
        	resultBuffer = isRemoteWebSocket ? GEncoder.buildWSDataFrameBuffer(packet.getPacket()) : packet.getPacket();
        	
        }
      
        out.write(resultBuffer);
    }
    
    // Web Socket handshake response go as a plain string.
    private static IoBuffer buildWSResponseBuffer(GHandShakeResponse response) throws UnsupportedEncodingException {                
        IoBuffer buffer = IoBuffer.allocate(response.getResponse().getBytes("UTF-8").length, false);
        buffer.setAutoExpand(true);
        buffer.put(response.getResponse().getBytes("UTF-8"));
        buffer.flip();
        return buffer;
    }
    
    // Encode the in buffer according to the Section 5.2. RFC 6455
    private static IoBuffer buildWSDataFrameBuffer(IoBuffer buf) {
        
        IoBuffer buffer = IoBuffer.allocate(buf.limit() + 2, false);
        buffer.setAutoExpand(true);
        buffer.put((byte) 0x82);
        if(buffer.capacity() <= 125){
            byte capacity = (byte) (buf.limit());
            buffer.put(capacity);
        }
        else{
            buffer.put((byte)126);
            buffer.putShort((short)buf.limit());
        }        
        buffer.put(buf);
        buffer.flip();
        return buffer;
    }
    
}
