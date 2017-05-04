package cn.standardai.api.biz.agent;

import java.io.UnsupportedEncodingException;
import java.util.List;

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
			rawData = CsvParser.parse(new String[] { basePath + rawFile4 }, null, null, null, null, false, false);
			for (int i = 0; i < rawData.length; i++) {
				dao.insertAction(rawData[i][0], rawData[i][1], DateUtil.parse(rawData[i][2], DateUtil.YYYY_MM_DD),
						rawData[i][3], rawData[i][4], rawData[i][5], rawData[i][6]);
			}
			break;
		case "action2":
			rawData = CsvParser.parse(new String[] { basePath + rawFile5 }, null, null, null, null, false, false);
			for (int i = 0; i < rawData.length; i++) {
				dao.insertAction(rawData[i][0], rawData[i][1], DateUtil.parse(rawData[i][2], DateUtil.YYYY_MM_DD),
						rawData[i][3], rawData[i][4], rawData[i][5], rawData[i][6]);
			}
			break;
		case "action3":
			rawData = CsvParser.parse(new String[] { basePath + rawFile6 }, null, null, null, null, false, false);
			for (int i = 0; i < rawData.length; i++) {
				dao.insertAction(rawData[i][0], rawData[i][1], DateUtil.parse(rawData[i][2], DateUtil.YYYY_MM_DD),
						rawData[i][3], rawData[i][4], rawData[i][5], rawData[i][6]);
			}
			break;
		}
	}

	public void done() {
		if (daoHandler != null) daoHandler.releaseSession();
	}
}
