package com.qianqi.server.game.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.zip.Inflater;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.qianqi.server.websocket.GBase64;

public class GMapTool {
	
	public static JSONObject init(String mapData)
	{
		JSONObject obj = JSONObject.fromObject(mapData);
		
		JSONObject result = new JSONObject();
		
		int width = obj.getInt("width");
		int height = obj.getInt("height");
		float tilewidth = (float) obj.getDouble("tilewidth");
		float tileheight = (float) obj.getDouble("tileheight");
		
		result.put("tileW", tilewidth);
		result.put("tileH", tileheight);
		
		float posX = 0;
		float posY = 0;
		JSONArray layers = obj.getJSONArray("layers");
		for(int i=0;i<layers.size();i++)
		{
			JSONObject layer = layers.getJSONObject(i);
			if("sprite".equals(layer.getString("name")))
			{
				JSONArray objects = layer.getJSONArray("objects");
				for(int j=0;j<objects.size();j++)
				{
					JSONObject object = objects.getJSONObject(j);
					if("pos".equals(object.getString("name")))
					{
						posX = (float) object.getDouble("x");
						posY = tileheight*height - (float) object.getDouble("y");
						float w = object.getJSONObject("properties").getInt("w")*tilewidth;
						float h = object.getJSONObject("properties").getInt("h")*tileheight;
						
						result.put("x", posX);
						result.put("y", posY);
						result.put("w", w);
						result.put("h", h);
					}
				}
				break;
			}
		}
		
		for(int i=0;i<layers.size();i++)
		{
			JSONObject layer = layers.getJSONObject(i);
			if("zhangai".equals(layer.getString("name")))
			{
				String data = layer.getString("data");
				JSONArray arr = new JSONArray();
				 try {
					 byte[] b = GBase64.decode(data);
					 b = decompress(b);
					 ByteBuffer buff = ByteBuffer.wrap(b);					 
					 IntBuffer in = buff.asIntBuffer();
					 for(int h=0;h<height;h++)
					 {
						 for(int w=0;w<width;w++)
						 {
							 int d = in.get(h*width + w);
							 if(d != 0)
							 {
								 int x = (int) (w * tilewidth - posX + tilewidth/2);
					             int y = (int) ((height-h) * tileheight - posY - tileheight/2);
					             JSONObject o = new JSONObject();
					             o.put("x", x);
					             o.put("y", y);
					             arr.add(o);
							 }
						 }
					 }	
					 
					 result.put("points", arr);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}
		return result;
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
