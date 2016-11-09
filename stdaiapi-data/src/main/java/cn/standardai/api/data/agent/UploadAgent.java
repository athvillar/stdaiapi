package cn.standardai.api.data.agent;

import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.util.MathUtil;
import cn.standardai.api.dao.DataDao;
import cn.standardai.api.dao.DatasetDao;
import cn.standardai.api.dao.TokenDao;
import cn.standardai.api.dao.base.DaoHandler;
import cn.standardai.api.dao.bean.Dataset;
import cn.standardai.api.dao.bean.JsonData;
import cn.standardai.api.dao.bean.Token;
import cn.standardai.api.data.exception.DataException;

public class UploadAgent {

	private DaoHandler daoHandler = new DaoHandler(true);

	public JSONObject saveJSONData(JSONObject dataRequest) throws DataException {

		JSONObject result = new JSONObject();

		String token = dataRequest.getString("token");
		String datasetId = dataRequest.getString("datasetId");
		String datasetName = dataRequest.getString("datasetName");

		// check token
		if (token == null) throw new DataException("认证失败");
		Token tokenParam = new Token();
		tokenParam.setToken(token);
		tokenParam.setExpireTime(new Date());
		TokenDao tokenDao = daoHandler.getMySQLMapper(TokenDao.class);
		List<Token> tokenResult = tokenDao.selectByToken(tokenParam);
		if (tokenResult == null || tokenResult.size() == 0) throw new DataException("认证失败");

		// save dataset
		if (datasetName == null) {
			if (datasetId == null) {
				// 未提供id和name，insert新记录
				datasetId = MathUtil.random(24);
				datasetName = datasetId;
				Dataset datasetParam = new Dataset();
				datasetParam.setDatasetId(datasetId);
				datasetParam.setDatasetName(datasetName);
				datasetParam.setUserId(tokenResult.get(0).getUserId());
				DatasetDao datasetDao = daoHandler.getMySQLMapper(DatasetDao.class);
				datasetDao.insert(datasetParam);
			} else {
				// 提供id，未提供name，检索该id，未检出则报异常
				Dataset datasetParam = new Dataset();
				datasetParam.setDatasetId(datasetId);
				datasetParam.setUserId(tokenResult.get(0).getUserId());
				DatasetDao datasetDao = daoHandler.getMySQLMapper(DatasetDao.class);
				if (datasetDao.selectCountByIdUser(datasetParam) == 0) {
					throw new DataException("no dataset(" + datasetId + ")");
				}
			}
		} else {
			if (datasetId == null) {
				// 提供name，未提供id，按照name检索，检索成功使用检出的id执行后续处理，未检出生成新id执行后续处理，并insert
				Dataset datasetParam = new Dataset();
				datasetParam.setDatasetName(datasetName);
				datasetParam.setUserId(tokenResult.get(0).getUserId());
				DatasetDao datasetDao = daoHandler.getMySQLMapper(DatasetDao.class);
				datasetId = datasetDao.selectIdByKey(datasetParam);
				if (datasetId == null) {
					datasetId = MathUtil.random(24);
					datasetParam.setDatasetId(datasetId);
					datasetDao.insert(datasetParam);
				}
			} else {
				// 提供id与name，按照id检索，未检出报异常，检出更新name
				Dataset datasetParam = new Dataset();
				datasetParam.setDatasetId(datasetId);
				datasetParam.setUserId(tokenResult.get(0).getUserId());
				DatasetDao datasetDao = daoHandler.getMySQLMapper(DatasetDao.class);
				if (datasetDao.selectCountByIdUser(datasetParam) == 0) {
					throw new DataException("no dataset(" + datasetId + ")");
				} else {
					datasetParam.setDatasetName(datasetName);
					datasetDao.updateById(datasetParam);
				}
			}
		}

		// save data
		JSONArray data = dataRequest.getJSONArray("data");
		DataDao dataDao = daoHandler.getMySQLMapper(DataDao.class);
		Integer baseIdx = dataDao.selectCountByDatasetId(datasetId);
		for (int i = 0; i < data.size(); i++) {
			JSONObject data1 = data.getJSONObject(i);
			JsonData param = new JsonData();
			param.setDatasetId(datasetId);
			param.setIdx(baseIdx + i);
			param.setData(data1.toJSONString());
			dataDao.insert(param);
		}

		// make result
		result.put("datasetId", datasetId);
		return result;
	}

	public void done() {
		daoHandler.releaseSession();
	}
}
