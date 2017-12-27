/**
* Literal.java
* Copyright 2017 standardai Co.ltd.
*/
package cn.standardai.tool.trafficimage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ShenmoDataParse {

	public static void main(String[] args) throws IOException {

		FileOutputStream out = null;
		OutputStreamWriter outWriter = null;
		BufferedWriter bufWrite = null;
		BufferedReader br = null;
		try {
			out = new FileOutputStream("/Users/athvillar/Documents/shenmo-recog-dc");
			outWriter = new OutputStreamWriter(out, "UTF-8");
			bufWrite = new BufferedWriter(outWriter);
			br = new BufferedReader(new InputStreamReader(new FileInputStream("/Users/athvillar/Documents/shenmo-recog"), "UTF-8"));
			String line = null;

			while ((line = br.readLine()) != null) {
				if ("".equals(line)) {
					continue;
				}
				int idx1 = line.indexOf(" ");
				if (!"200 ".equals(line.substring(idx1 + 1, idx1 + 5))) {
					System.out.print("WRONG:(" + line.substring(0, idx1));
					continue;
				}
				JSONObject inputJson = JSONObject.parseObject(line.substring(idx1 + 5));

				JSONObject outputImage1 = new JSONObject();
				JSONArray outputCars = new JSONArray();
				String fullFileName = line.substring(0, idx1);
				outputImage1.put("PictureID", fullFileName.substring(fullFileName.lastIndexOf("/") + 1));
				JSONArray inputJsonImageResults = inputJson.getJSONArray("ImageResults");
				if (inputJsonImageResults == null || inputJsonImageResults.size() == 0) {
					outputImage1.put("Cars", outputCars);
					outputImage1.put("CarNo", 0);
					continue;
				}

				for (int i = 0; i < inputJsonImageResults.size(); i++) {
					JSONArray inputJsonImageResultsVehicles = inputJsonImageResults.getJSONObject(i).getJSONArray("Vehicles");
					if (inputJsonImageResultsVehicles == null || inputJsonImageResultsVehicles.size() == 0) {
						continue;
					}
					for (int j = 0; j < inputJsonImageResultsVehicles.size(); j++) {
						JSONObject detect = inputJsonImageResultsVehicles.getJSONObject(j).getJSONObject("Detect");
						if (detect == null) continue;
						JSONObject car = detect.getJSONObject("Car");
						if (car == null) continue;
						JSONArray rect = car.getJSONArray("Rect");
						if (rect == null) continue;

						JSONObject outputCar1 = new JSONObject();
						outputCar1.put("Position", rect);
						outputCar1.put("Type", "未识别");
						outputCars.add(outputCar1);

						JSONObject recog = inputJsonImageResultsVehicles.getJSONObject(j).getJSONObject("Recognize");
						if (recog == null) continue;
						JSONObject type = recog.getJSONObject("Type");
						if (type == null) continue;
						if (!"succ".equals(type.getString("Message"))) continue;
						JSONArray typeTopList = type.getJSONArray("TopList");
						if (typeTopList == null || typeTopList.size() == 0) continue;
						outputCar1.put("Type", typeTopList.getJSONObject(0).getString("Name"));
					}
				}
				outputImage1.put("Cars", outputCars);
				outputImage1.put("CarNo", outputCars.size());
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
