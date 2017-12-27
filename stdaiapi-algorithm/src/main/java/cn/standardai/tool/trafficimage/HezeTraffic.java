/**
* Literal.java
* Copyright 2017 standardai Co.ltd.
*/
package cn.standardai.tool.trafficimage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

public class HezeTraffic {

	public enum Status {

		畅通("1"), 缓行("2"), 拥挤("3"), 严重拥堵("4");

		String s;

		private Status(String s) {
			this.s = s;
		}

		private static final Map<Status, String> mappings = new HashMap<Status, String>(4);

		static {
			for (Status status : values()) {
				mappings.put(status, status.s);
			}
		}

		public String toString() {
			return mappings.get(this);
		}
	};

	private static String path = "/Users/athvillar/Documents/traffic567/";

	private static String outputFile = "/Users/athvillar/Documents/traffic567/output";

	private static String[] dateString = {"2017-08-05","2017-08-06","2017-08-07"};

	private static BufferedWriter bufWrite;

	public static void main(String[] args) {

		try {
			FileOutputStream out = new FileOutputStream(outputFile);
			OutputStreamWriter outWriter = new OutputStreamWriter(out, "UTF-8");
			bufWrite = new BufferedWriter(outWriter);
			readTraffic();
			bufWrite.close();
			outWriter.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void readTraffic() {

		// print title
		String title = "time,position,direction,carnum\n";
		print(title);

		for (int i = 0; i < dateString.length; i++) {
			for (int j = 0; j <= 23; j++) {
				String hString = j <= 9 ? "0" + j : "" + j;
				readTraffic1(path + dateString[i] + "/" + "20.0.51.56-tvc-" + dateString[i] + "-" + hString);
			}
		}
	}

	private static void readTraffic1(String path) {

		FileReader fr = null;
		BufferedReader bf = null;
		try {
			fr = new FileReader(path);
			bf = new BufferedReader(fr);
			String line;
			while ((line = bf.readLine()) != null) {

				int idx = line.indexOf("PASSTIME=");
				if (idx == -1) continue;
				String outputLine = line.substring(idx + 9, idx + 9 + 19);

				idx = line.indexOf("TGSID=");
				if (idx == -1) continue;
				String temp = line.substring(idx + 6, idx + 6 + 7).trim();
				int spaceIdx = temp.indexOf("\t");
				outputLine += "," + (spaceIdx == -1 ? temp : temp.substring(0, spaceIdx));

				idx = line.indexOf("DRIVEDIR=");
				if (idx == -1) continue;
				outputLine += "," + line.substring(idx + 9, idx + 9 + 2).trim();

				idx = line.indexOf("CARPLATE=");
				if (idx == -1) continue;
				temp = line.substring(idx + 9, idx + 9 + 8).trim();
				spaceIdx = temp.indexOf("\t");
				outputLine += "," + (spaceIdx == -1 ? temp : temp.substring(0, spaceIdx));

				outputLine += "\n";
				print(outputLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bf != null) {
				try {
					bf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void print(String s) {
		try {
			bufWrite.write(s);
			System.out.println(s);
			bufWrite.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
