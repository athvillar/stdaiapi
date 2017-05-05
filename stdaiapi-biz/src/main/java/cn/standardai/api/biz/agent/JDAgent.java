package cn.standardai.api.biz.agent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.util.CryptUtil;
import cn.standardai.api.core.util.DateUtil;
import cn.standardai.api.dao.JDDao;
import cn.standardai.api.dao.UserDao;
import cn.standardai.api.dao.base.DaoHandler;
import cn.standardai.api.dao.bean.User;

public class JDAgent {

	private static String basePath = "/";
	//private static String basePath = "/Users/athvillar/Documents/JData/";

	private static String rawFile1 = "raw/JData_User.csv";

	private static String rawFile2 = "raw/JData_Product.csv";

	private static String rawFile3 = "raw/JData_Comment.csv";

	private static String rawFile4 = "raw/JData_Action_201602.csv";

	private static String rawFile5 = "raw/JData_Action_201603.csv";

	private static String rawFile6 = "raw/JData_Action_201604.csv";

	private DaoHandler daoHandler = new DaoHandler();

	public void insert(String id) throws UnsupportedEncodingException {
		JDDao dao = daoHandler.getMySQLMapper(JDDao.class);
		String[][] rawData;
		switch (id) {
		case "user":
			rawData = CsvParser.parse(new String[] { basePath + rawFile1 }, null, null, null, new int[] {0, 3}, false, false);
			for (int i = 0; i < rawData.length; i++) {
				dao.insertUser(rawData[i][0], rawData[i][1]);
			}
			break;
		case "sku":
			rawData = CsvParser.parse(new String[] { basePath + rawFile2 }, null, null, null, null, false, false);
			for (int i = 0; i < rawData.length; i++) {
				dao.insertSku(rawData[i][0], rawData[i][1], rawData[i][2], rawData[i][3], rawData[i][4], rawData[i][5]);
			}
			break;
		case "comment":
			rawData = CsvParser.parse(new String[] { basePath + rawFile3 }, null, null, null, new int[] {1,2,3,4}, false, false);
		for (int i = 0; i < rawData.length; i++) {
				dao.insertComment(rawData[i][0], rawData[i][1], rawData[i][2], Double.parseDouble(rawData[i][3]));
			}
			break;
		case "action1":
			parse(dao, basePath + rawFile4);
			break;
		case "action2":
			parse(dao, basePath + rawFile5);
			break;
		case "action3":
			parse(dao, basePath + rawFile6);
			break;
		}
	}

	public void done() {
		if (daoHandler != null) daoHandler.releaseSession();
	}

	public static void parse(JDDao dao, String inFileName) {

		FileReader fr = null;
		BufferedReader br = null;

		try {
			fr = new FileReader(inFileName);
			br = new BufferedReader(fr);

			br.readLine();
	
			String line;
			boolean finish = false;
			String[][] result = null;
			while (true) {
				result = new String[500][];
				for (int i = 0; i < 500; i++) {
					if ((line = br.readLine()) == null) {
						finish = true;
						break;
					}
					String[] items = line.split(",");
					String[] result1 = new String[items.length];

					for (int j = 0; j < items.length; j++) {
						result1[j] = items[j];
					}
					result[i] = result1;
				}
				//Executor exec = Executors.newFixedThreadPool(500);
				for (int i = 0; i < result.length; i++) {
					if (result[i] == null) break; 
			        Thread1 t1=new Thread1(dao, result[i]); 
			        t1.run();
			        //exec.execute(t1);
				}
				//Thread.sleep(3000);
				if (finish) break;
			}

			closeReader(fr, br);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeReader(fr, br);
		}
	}

	private static void closeReader(FileReader fr, BufferedReader br) {

		if (br != null) {
			try {
				br.close();
				br = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (fr != null) {
			try {
				fr.close();
				fr = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
