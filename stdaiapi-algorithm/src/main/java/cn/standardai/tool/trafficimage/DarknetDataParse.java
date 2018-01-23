/**
* Literal.java
* Copyright 2017 standardai Co.ltd.
*/
package cn.standardai.tool.trafficimage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class DarknetDataParse {

	public static void main(String[] args) throws IOException {

		FileOutputStream out = null;
		OutputStreamWriter outWriter = null;
		BufferedWriter bufWrite = null;
		BufferedReader br = null;
		try {
			out = new FileOutputStream("/Users/athvillar/Documents/darknet_output_dc");
			outWriter = new OutputStreamWriter(out, "UTF-8");
			bufWrite = new BufferedWriter(outWriter);
			br = new BufferedReader(new InputStreamReader(new FileInputStream("/Users/athvillar/Documents/darknet_output"), "UTF-8"));
			String line = null;

			String filename = null;
			JSONArray outputImage = null;
			Map<String, Integer> classType = new HashMap<String, Integer>();
			classType.put("car", 1);
			classType.put("truck", 2);
			classType.put("bus", 3);
			classType.put("motorbike", 4);
			classType.put("bicycle", 5);
			classType.put("person", 6);
			while ((line = br.readLine()) != null) {
				if ("".equals(line)) {
					continue;
				}
				if (line.startsWith("File")) {
					if (filename != null) {
						JSONObject outputImage1 = new JSONObject();
						outputImage1.put("PictureID", filename);
						outputImage1.put("Cars", outputImage);
						outputImage1.put("CarNo", outputImage.size());
						bufWrite.write(outputImage1.toJSONString());
						bufWrite.write("\r\n");
					}
					filename = line.substring(line.indexOf("/") + 1);
					outputImage = new JSONArray();
					continue;
				} else if (line.startsWith("{")) {
					JSONObject ji = JSONObject.parseObject(line);
					String type = ji.getString("Type");
					if (classType.containsKey(type)) {
						JSONArray jip = ji.getJSONArray("Position");
						int r;

						JSONObject jo = new JSONObject();
						JSONArray jop = new JSONArray();
						r = (int)Math.round((Math.random() - 0.5) * 8);
						jop.add(0, jip.getInteger(0) + r);
						r = (int)Math.round((Math.random() - 0.5) * 8);
						jop.add(1, jip.getInteger(1) + r);
						r = (int)Math.round((Math.random() - 0.5) * 4);
						jop.add(2, jip.getInteger(2) - jip.getInteger(0) + r);
						r = (int)Math.round((Math.random() - 0.5) * 4);
						jop.add(3, jip.getInteger(3) - jip.getInteger(1) + r);
						jo.put("Position", jop);
						jo.put("Type", classType.get(type));
						jo.put("Prob", Integer.parseInt(ji.getString("Prob").substring(0, ji.getString("Prob").length() - 1)) * 1.0 / 100);

						outputImage.add(jo);
						//classType.put(type, classType.get(type) + 1);
					} else {
						continue;
						//classType.put(type, 1);
					}
				} else {
					continue;
				}
			}
			if (filename != null) {
				JSONObject outputImage1 = new JSONObject();
				outputImage1.put("PictureID", filename);
				outputImage1.put("Cars", outputImage);
				outputImage1.put("CarNo", outputImage.size());
				bufWrite.write(outputImage1.toJSONString());
			}
			//for (Entry<String, Integer> entry : classType.entrySet()) {
			//	System.out.println("Class: " + entry.getKey() + ", Num: " + entry.getValue());
			//}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			br.close();
			bufWrite.close();
			outWriter.close();
			out.close();
		}
	}
}
