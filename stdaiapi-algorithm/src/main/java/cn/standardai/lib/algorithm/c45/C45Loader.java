/**
* C45Loader.java
* Copyright 2015 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.c45;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * C4.5Loader
 * @author 韩晴
 *
 */
public class C45Loader {

	public static C45 getInstance(String dataFile) throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile), "UTF-8"));

		// 读取元数据
		String lineAttr = br.readLine();
		String lineProp = br.readLine();
		String[] attrs;
		String[] props;
		MetaData metaData = null;
		if (lineAttr != null && lineProp != null) {
			attrs = lineAttr.split(",");
			props = lineProp.split(",");
			metaData = new MetaData(attrs, props);
		} else {
			if (br != null) {
				br.close();
			}
			return null;
		}

		// 读取数据
		String line;
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		while ((line = br.readLine()) != null) {
			String[] dataInfo = line.split(",");
			Map<String, String> data = new HashMap<String, String>();
			for (int i = 0; i < dataInfo.length; i++) {
				data.put(attrs[i], dataInfo[i]);
			}
			dataList.add(data);
		}

		if (br != null) {
			br.close();
		}

		return new C45(metaData, dataList, 1);
	}
}
