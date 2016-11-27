package cn.standardai.api.data.agent;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.util.MathUtil;
import cn.standardai.api.dao.DataDao;
import cn.standardai.api.dao.DatasetDao;
import cn.standardai.api.dao.TokenDao;
import cn.standardai.api.dao.base.DaoHandler;
import cn.standardai.api.dao.bean.Dataset;
import cn.standardai.api.dao.bean.Data;
import cn.standardai.api.dao.bean.Token;
import cn.standardai.api.data.exception.DataException;

public class ScratchAgent {

	// TODO 未完成
	private DaoHandler daoHandler = new DaoHandler(true);

	public JSONObject saveJSONData(String userId, JSONObject request) throws DataException {

		String datasetId = request.getString("datasetId");
		String datasetName = request.getString("datasetName");
		String format = request.getString("format");

		// TODO 改datasetName的功能可以不要，一旦命名，不可更改更合理，并且下段需要共通化
		// save dataset
		if (datasetName == null) {
			if (datasetId == null) {
				// 未提供id和name，insert新记录
				datasetId = MathUtil.random(24);
				datasetName = datasetId;
				Dataset datasetParam = new Dataset();
				datasetParam.setDatasetId(datasetId);
				datasetParam.setDatasetName(datasetName);
				datasetParam.setUserId(userId);
				datasetParam.setFormat(format);
				DatasetDao datasetDao = daoHandler.getMySQLMapper(DatasetDao.class);
				datasetDao.insert(datasetParam);
			} else {
				// 提供id，未提供name，检索该id，未检出则报异常
				Dataset datasetParam = new Dataset();
				datasetParam.setDatasetId(datasetId);
				datasetParam.setUserId(userId);
				DatasetDao datasetDao = daoHandler.getMySQLMapper(DatasetDao.class);
				if (datasetDao.selectCountByIdUser(datasetParam) == 0) {
					throw new DataException("no dataset(" + datasetId + ")");
				}
			}
		} else {
			if (datasetId == null) {
				// 提供name，未提供id，按照name检索，检索成功使用检出的id执行后续处理，未检出生成新id执行后续处理，并insert
				DatasetDao datasetDao = daoHandler.getMySQLMapper(DatasetDao.class);
				Dataset dataset = datasetDao.selectByKey(datasetName, userId);
				if (dataset == null) {
					datasetId = MathUtil.random(24);
					Dataset datasetParam = new Dataset();
					datasetParam.setDatasetName(datasetName);
					datasetParam.setUserId(userId);
					datasetParam.setDatasetId(datasetId);
					datasetParam.setFormat(format);
					datasetDao.insert(datasetParam);
				} else {
					datasetId = dataset.getDatasetId();
				}
			} else {
				// 提供id与name，按照id检索，未检出报异常，检出更新name
				Dataset datasetParam = new Dataset();
				datasetParam.setDatasetId(datasetId);
				datasetParam.setUserId(userId);
				DatasetDao datasetDao = daoHandler.getMySQLMapper(DatasetDao.class);
				if (datasetDao.selectCountByIdUser(datasetParam) == 0) {
					throw new DataException("no dataset(" + datasetId + ")");
				} else {
					datasetParam.setDatasetName(datasetName);
					datasetDao.updateById(datasetParam);
				}
			}
		}

		JSONArray urls = request.getJSONArray("urls");
		for (int i = 0; i < urls.size(); i++) {

			try {
				URI uri = new URI(urls.getString(i));
				SimpleClientHttpRequestFactory schr = new SimpleClientHttpRequestFactory();
				ClientHttpRequest chr = schr.createRequest(uri, HttpMethod.GET);
				ClientHttpResponse res = chr.execute();
				InputStream is = res.getBody();
				File file = new File("/tempfile/" + MathUtil.random(12));
                //item.write(file);
				int len;
				byte[] bytes = new byte[1024];
				while ((len= is.read(bytes)) != -1) {
					
				}

			} catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// save data
		JSONArray data = request.getJSONArray("data");
		DataDao dataDao = daoHandler.getMySQLMapper(DataDao.class);
		Integer baseIdx = dataDao.selectCountByDatasetId(datasetId);
		for (int i = 0; i < data.size(); i++) {
			String data1 = data.getString(i);
			Data param = new Data();
			param.setDatasetId(datasetId);
			param.setIdx(baseIdx + i);
			param.setData(data1);
			dataDao.insert(param);
		}

		// make result
		JSONObject result = new JSONObject();
		result.put("datasetId", datasetId);
		return result;
	}

	public JSONObject uploadLocalImages(String userId, JSONObject request) throws DataException, IOException {

		String datasetId = request.getString("datasetId");
		String datasetName = request.getString("datasetName");
		String format = request.getString("format");

		// TODO 改datasetName的功能可以不要，一旦命名，不可更改更合理，并且下段需要共通化
		// save dataset
		if (datasetName == null) {
			if (datasetId == null) {
				// 未提供id和name，insert新记录
				datasetId = MathUtil.random(24);
				datasetName = datasetId;
				Dataset datasetParam = new Dataset();
				datasetParam.setDatasetId(datasetId);
				datasetParam.setDatasetName(datasetName);
				datasetParam.setUserId(userId);
				datasetParam.setFormat(format);
				DatasetDao datasetDao = daoHandler.getMySQLMapper(DatasetDao.class);
				datasetDao.insert(datasetParam);
			} else {
				// 提供id，未提供name，检索该id，未检出则报异常
				Dataset datasetParam = new Dataset();
				datasetParam.setDatasetId(datasetId);
				datasetParam.setUserId(userId);
				DatasetDao datasetDao = daoHandler.getMySQLMapper(DatasetDao.class);
				if (datasetDao.selectCountByIdUser(datasetParam) == 0) {
					throw new DataException("no dataset(" + datasetId + ")");
				}
			}
		} else {
			if (datasetId == null) {
				// 提供name，未提供id，按照name检索，检索成功使用检出的id执行后续处理，未检出生成新id执行后续处理，并insert
				DatasetDao datasetDao = daoHandler.getMySQLMapper(DatasetDao.class);
				Dataset dataset = datasetDao.selectByKey(datasetName, userId);
				if (dataset == null) {
					datasetId = MathUtil.random(24);
					Dataset datasetParam = new Dataset();
					datasetParam.setDatasetName(datasetName);
					datasetParam.setUserId(userId);
					datasetParam.setDatasetId(datasetId);
					datasetParam.setFormat(format);
					datasetDao.insert(datasetParam);
				} else {
					datasetId = dataset.getDatasetId();
				}
			} else {
				// 提供id与name，按照id检索，未检出报异常，检出更新name
				Dataset datasetParam = new Dataset();
				datasetParam.setDatasetId(datasetId);
				datasetParam.setUserId(userId);
				DatasetDao datasetDao = daoHandler.getMySQLMapper(DatasetDao.class);
				if (datasetDao.selectCountByIdUser(datasetParam) == 0) {
					throw new DataException("no dataset(" + datasetId + ")");
				} else {
					datasetParam.setDatasetName(datasetName);
					datasetDao.updateById(datasetParam);
				}
			}
		}

		List<JSONObject> datalist = new ArrayList<JSONObject>();
		for (int i = 1; i <= 165; i++) {
			Integer[][][] data = Image2Data.getGray("~/Documents/yale/s" + i + ".bmp");
			Integer[] target = new Integer[165];
			for (int j = 0; j < target.length; j++) {
				if (j == (i - 1) / 11) {
					target[j] = 1;
				} else {
					target[j] = 0;
				}
			}
			JSONObject data1JSONObject = new JSONObject();
			JSONArray data1 = new JSONArray();
			for (int i2 = 0; i2 < data.length; i2++) {
				JSONArray data2 = new JSONArray();
				for (int j2 = 0; j2 < data[i2].length; j2++) {
					JSONArray data3 = new JSONArray();
					for (int k2 = 0; k2 < data[i2][j2].length; k2++) {
						data3.add(data[i2][j2][k2]);
					}
					data2.add(data3);
				}
				data1.add(data2);
			}
			data1JSONObject.put("data", data1);
			JSONArray targetJSONArray = new JSONArray();
			for (int i2 = 0; i2 < target.length; i2++) {
				targetJSONArray.add(target[i2]);
			}
			data1JSONObject.put("data", data1);
			data1JSONObject.put("target", targetJSONArray);
			datalist.add(data1JSONObject);
		}

		DataDao dataDao = daoHandler.getMySQLMapper(DataDao.class);
		Integer baseIdx = dataDao.selectCountByDatasetId(datasetId);
		for (int i = 0; i < datalist.size(); i++) {
			String data1 = datalist.get(i).toJSONString();
			Data param = new Data();
			param.setDatasetId(datasetId);
			param.setIdx(baseIdx + i);
			param.setData(data1);
			param.setType("json");
			dataDao.insert(param);
		}

		// make result
		JSONObject result = new JSONObject();
		result.put("datasetId", datasetId);
		return result;
	}

	public void done() {
		daoHandler.releaseSession();
	}
}
