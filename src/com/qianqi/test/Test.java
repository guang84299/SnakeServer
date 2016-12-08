package com.qianqi.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.IntBuffer;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import com.qianqi.server.GServerConfig;
import com.qianqi.server.websocket.GBase64;
import com.qianqi.web.tools.GTools;



public class Test {
	 
	 public static void main(String[] args) {
		
		 String res = GServerConfig.initConfig("names.txt");
		 System.out.println(res);
		 res = res.replaceAll("\\s", "");
		 String[] arr = res.split(",");
		 String name = arr[GTools.getRand(0, arr.length-1)];
		System.out.println(name.replace("\"", ""));
	}
	 
	 private static String getString(byte[] data) {

		 	IoBuffer in = IoBuffer.allocate(data.length);   
		 	in.put(data, 0, data.length);   
		 	in.flip();  
	        IoBuffer resultBuffer = null;
	        do{
	              
	            int frameLen = (in.get() & (byte) 0x7F);
	            if(frameLen == 126){
	                frameLen = in.getShort();
	            }
  
	            byte mask[] = new byte[4];
	            for (int i = 0; i < 4; i++) {
	                mask[i] = in.get();
	            }

	            
	            byte[] unMaskedPayLoad = new byte[frameLen];
	            for (int i = 0; i < frameLen; i++) {
	                byte maskedByte = in.get();
	                unMaskedPayLoad[i] = (byte) (maskedByte ^ mask[i % 4]);
	            }
	            
	            if(resultBuffer == null){
	                resultBuffer = IoBuffer.wrap(unMaskedPayLoad);
	                resultBuffer.position(resultBuffer.limit());
	                resultBuffer.setAutoExpand(true);
	            }
	            else{
	                resultBuffer.put(unMaskedPayLoad);
	            }
	        }
	        while(in.hasRemaining());
	        
	        resultBuffer.flip();
	        return new String(resultBuffer.array());

	    }    
	 
	 public static byte[] decompress(byte[] data) {  
	        byte[] output = new byte[0];  
	  
	        Inflater decompresser = new Inflater();  
	        decompresser.reset();  
	        decompresser.setInput(data);  
	  
	        ByteArrayOutputStream o = new ByteArrayOutputStream(data.length);  
	        try {  
	            byte[] buf = new byte[1024];  
	            while (!decompresser.finished()) {  
	                int i = decompresser.inflate(buf);  
	                o.write(buf, 0, i);  
	            }  
	            output = o.toByteArray();  
	        } catch (Exception e) {  
	            output = data;  
	            e.printStackTrace();  
	        } finally {  
	            try {  
	                o.close();  
	            } catch (IOException e) {  
	                e.printStackTrace();  
	            }  
	        }  
	  
	        decompresser.end();  
	        return output;  
	    }  
}
