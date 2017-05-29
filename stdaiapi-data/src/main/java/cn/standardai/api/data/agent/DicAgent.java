package cn.standardai.api.data.agent;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.base.AuthAgent;
import cn.standardai.api.core.exception.AuthException;
import cn.standardai.api.core.util.MathUtil;
import cn.standardai.api.dao.DataDicDao;
import cn.standardai.api.dao.DataDicDataDao;
import cn.standardai.api.dao.bean.DataDic;
import cn.standardai.api.dao.bean.DataDicData;
import cn.standardai.api.data.bean.SharePolicy;
import cn.standardai.api.data.exception.DataException;

public class DicAgent extends AuthAgent {

	/*
	 * {
	 *   "dicName": "gender",
	 *   "description": "xxxxx",
	 *   "sharePolicy": "public",
	 *   "dicData": [
	 *     {"key": 1, "value": "男"},
	 *     {"key": 2, "value": "女"}
	 *   ]
	 * }
	 */
	public JSONObject createDic(JSONObject request) throws DataException {

		String dicName = request.getString("dicName");
		if (dicName == null || "".equals(dicName)) throw new DataException("缺少字典名");

		String description = request.getString("description");
		String sharePolicyS = request.getString("sharePolicy");
		Character sharePolicyC = SharePolicy.resolve(sharePolicyS);
		if (sharePolicyC == null) {
			sharePolicyC = SharePolicy.pPublic.key;
		}

		DataDicDao dicDao = daoHandler.getMySQLMapper(DataDicDao.class);
		if (dicDao.selectByKey(dicName, this.userId) != null) throw new DataException("数据字典已存在");

		DataDic dataDic = new DataDic();
		dataDic.setDataDicId(MathUtil.random(24));
		dataDic.setDataDicName(dicName);
		dataDic.setDescription(description);
		dataDic.setUserId(this.userId);
		dataDic.setSharePolicy(sharePolicyC);
		dicDao.insert(dataDic);

		JSONArray dicDataJ = request.getJSONArray("dicData");
		if (dicDataJ != null) {
			List<DataDicData> dicData = new ArrayList<DataDicData>();
			for (int i = 0; i < dicDataJ.size(); i++) {
				Integer key = dicDataJ.getJSONObject(i).getInteger("key");
				String value = dicDataJ.getJSONObject(i).getString("value");
				if (key == null || value == null) continue;
				DataDicData dicData1 = new DataDicData();
				dicData1.setDataDicId(dataDic.getDataDicId());
				dicData1.setKii(key);
				dicData1.setValue(value);
				dicData.add(dicData1);
			}
			if (dicData.size() != 0) {
				DataDicDataDao dicDataDao = daoHandler.getMySQLMapper(DataDicDataDao.class);
				dicDataDao.insert(dicData);
			}
		}

		JSONObject result = new JSONObject();
		result.put("dicId", dataDic.getDataDicId());

		return result;
	}

	public JSONObject listDic() {

		DataDicDao dao = daoHandler.getMySQLMapper(DataDicDao.class);
		List<DataDic> dataDic = dao.selectByPrivilege(this.userId);

		JSONArray dicsJ = new JSONArray();
		for (int i = 0; i < dataDic.size(); i++) {
			JSONObject dicJ = new JSONObject();
			dicJ.put("dicId", dataDic.get(i).getDataDicId());
			if (this.userId.equals(dataDic.get(i).getUserId())) {
				dicJ.put("dicName", dataDic.get(i).getDataDicName());
			} else {
				dicJ.put("dicName", dataDic.get(i).getUserId() + "/" + dataDic.get(i).getDataDicName());
			}
			dicJ.put("description", dataDic.get(i).getDescription());
			dicJ.put("sharePolicy", SharePolicy.parse(dataDic.get(i).getSharePolicy()));
			dicsJ.add(dicJ);
		}
		JSONObject result = new JSONObject();
		result.put("dic", dicsJ);

		return result;
	}

	public JSONObject listDic(String userId) {

		DataDicDao dao = daoHandler.getMySQLMapper(DataDicDao.class);
		List<DataDic> dataDic;
		if (userId.equals(this.userId)) {
			dataDic = dao.selectByUserId(userId);
		} else {
			dataDic = dao.selectByUserIdPrivilege(userId);
		}

		JSONArray dicsJ = new JSONArray();
		for (int i = 0; i < dataDic.size(); i++) {
			JSONObject dicJ = new JSONObject();
			dicJ.put("dicId", dataDic.get(i).getDataDicId());
			if (this.userId.equals(dataDic.get(i).getUserId())) {
				dicJ.put("dicName", dataDic.get(i).getDataDicName());
			} else {
				dicJ.put("dicName", dataDic.get(i).getUserId() + "/" + dataDic.get(i).getDataDicName());
			}
			dicJ.put("description", dataDic.get(i).getDescription());
			dicJ.put("sharePolicy", SharePolicy.parse(dataDic.get(i).getSharePolicy()));
			dicsJ.add(dicJ);
		}
		JSONObject result = new JSONObject();
		result.put("dic", dicsJ);

		return result;
	}

	public JSONObject viewDic(String userId, String dicName) throws AuthException {

		DataDicDao dao = daoHandler.getMySQLMapper(DataDicDao.class);
		DataDic dataDic;
		if (userId.equals(this.userId)) {
			dataDic = dao.selectByKey(dicName, userId);
		} else {
			dataDic = dao.selectByKeyPrivilege(dicName, userId);
		}

		JSONObject dicJ = new JSONObject();
		dicJ.put("dicId", dataDic.getDataDicId());
		dicJ.put("dicName", dataDic.getDataDicName());
		dicJ.put("description", dataDic.getDescription());
		dicJ.put("sharePolicy", SharePolicy.parse(dataDic.getSharePolicy()));

		JSONObject result = new JSONObject();
		result.put("dic", dicJ);

		return result;
	}

	public void removeDic(String userId, String dicName) throws AuthException {
		if (!userId.equals(this.userId)) throw new AuthException("没有权限");
		DataDicDao dao = daoHandler.getMySQLMapper(DataDicDao.class);
		dao.deleteByKey(dicName, userId);
	}
}
