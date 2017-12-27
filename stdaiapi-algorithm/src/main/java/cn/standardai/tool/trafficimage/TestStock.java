/**
* Literal.java
* Copyright 2017 standardai Co.ltd.
*/
package cn.standardai.tool.trafficimage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;

public class TestStock {


	private static String inputFile = "/Users/athvillar/Documents/aichallenger/股票/ai_challenger_stock_test_20170910/data/20170910/ai_challenger_stock_test_20170910/stock_test_data_20170910.csv";

	private static String outputFile = "/Users/athvillar/Documents/stock/week2/result1.csv";

	private static BufferedWriter bufWrite;

	public static void main(String[] args) {

		try {
			FileOutputStream out = new FileOutputStream(outputFile);
			OutputStreamWriter outWriter = new OutputStreamWriter(out, "UTF-8");
			bufWrite = new BufferedWriter(outWriter);
			writeResult();
			bufWrite.close();
			outWriter.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void writeResult() {

		// print title
		String title = "id,proba\n";
		print(title);

		/*
		BufferedReader bf = null;
		try {
			FileReader fr = null;
			fr = new FileReader(inputFile);
			bf = new BufferedReader(fr);
			String line;
			int i = 321674;
			bf.readLine();
			while ((line = bf.readLine()) != null) {
				if (Integer.parseInt(line.split(",")[0]) != i) {
					System.out.println("ERROR:" + Integer.parseInt(line.split(",")[0]) + "/" + i);
				} else {
					//System.out.println("RITEHT" + line);
				}
				i++;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
		for (int i = 321674; i <= 524430; i++) {
			print("" + i + "," + Math.round(Math.random() * 1000000) * 1.0 / 1000000);
			if (i != 524430) {
				print("\n");
			}
		}
	}

	private static void print(String s) {
		try {
			bufWrite.write(s);
			//bufWrite.newLine();
			//System.out.println(s);
			bufWrite.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
