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

public class GelingshentongDataParse {

	public static void main(String[] args) throws IOException {

		FileOutputStream out = null;
		OutputStreamWriter outWriter = null;
		BufferedWriter bufWrite = null;
		BufferedReader br = null;
		try {
			out = new FileOutputStream("/Users/athvillar/Documents/gelingshentong-dc.csv");
			outWriter = new OutputStreamWriter(out, "UTF-8");
			bufWrite = new BufferedWriter(outWriter);
			br = new BufferedReader(new InputStreamReader(new FileInputStream("/Users/athvillar/Documents/vehicles.csv"), "UTF-8"));
			String line = null;

			Map<String, JSONArray> recogBucket = new HashMap<String, JSONArray>();
			Map<String, String> recogTime = new HashMap<String, String>();
			while ((line = br.readLine()) != null) {
				if ("".equals(line)) {
					continue;
				}
				String[] words = line.split(",");
				if (words == null || words.length < 24) continue;

				String uts = words[1];
				if (uts.substring(0, 10).compareTo("2017-12-18") < 0) continue;
				String imageUrl = words[17];
				if (imageUrl == null) continue;
				String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

				if (recogTime.containsKey(filename)) {
					if (!uts.equals(recogTime.get(filename))) {
						continue;
					}
				} else {
					recogTime.put(filename, uts);
				}

				JSONArray rect = new JSONArray();
				rect.add(words[20]);
				rect.add(words[21]);
				rect.add(words[22]);
				rect.add(words[23]);
				JSONObject outputCar1 = new JSONObject();
				outputCar1.put("Position", rect);
				outputCar1.put("Type", words[11]);

				if (recogBucket.containsKey(filename)) {
					recogBucket.get(filename).add(outputCar1);
				} else {
					JSONArray outputImage = new JSONArray();
					outputImage.add(outputCar1);
					recogBucket.put(filename, outputImage);
				}
			}

			for (Entry<String, JSONArray> entry : recogBucket.entrySet()) {
				JSONObject outputImage1 = new JSONObject();
				outputImage1.put("PictureID", entry.getKey());
				outputImage1.put("Cars", entry.getValue());
				outputImage1.put("CarNo", entry.getValue().size());
				bufWrite.write(outputImage1.toJSONString());
				bufWrite.write("\r\n");
			}
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
