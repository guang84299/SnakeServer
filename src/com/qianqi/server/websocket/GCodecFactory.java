/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qianqi.server.websocket;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * Codec Factory used for creating websocket filter.
 * 
 * @author DHRUV CHOPRA
 */
public class GCodecFactory implements ProtocolCodecFactory{
    private ProtocolEncoder encoder;
    private ProtocolDecoder decoder;

    public GCodecFactory() {
            encoder = new GEncoder();
            decoder = new GDecoder();
    }

    @Override
    public ProtocolEncoder getEncoder(IoSession ioSession) throws Exception {
        return encoder;
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession ioSession) throws Exception {
        return decoder;
    }    
}
