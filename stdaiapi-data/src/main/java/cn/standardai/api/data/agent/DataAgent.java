package cn.standardai.api.data.agent;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.base.AuthAgent;
import cn.standardai.api.core.bean.Context;
import cn.standardai.api.core.exception.AuthException;
import cn.standardai.api.core.exception.StdaiException;
import cn.standardai.api.core.util.MathUtil;
import cn.standardai.api.dao.DataDao;
import cn.standardai.api.dao.DatasetDao;
import cn.standardai.api.dao.bean.Dataset;
import cn.standardai.api.dao.bean.Data;
import cn.standardai.api.data.bean.SharePolicy;
import cn.standardai.api.data.exception.DataException;

public class DataAgent extends AuthAgent {

	private static final String TYPE_FILE = "FILE";

	private static final String TYPE_DATA = "DATA";
	
	private static final String PARAM_EG = "{?}";

	public JSONObject createData(JSONObject request) throws DataException {

		String datasetName = request.getString("dataName");
		if (datasetName == null || "".equals(datasetName)) throw new DataException("缺少数据名");

		String type = request.getString("type");
		if (type == null) type = TYPE_DATA;
		if (!TYPE_FILE.equalsIgnoreCase(type) && !TYPE_DATA.equalsIgnoreCase(type))
			throw new DataException("不支持的数据类型(type=" + type + ")");

		String description = request.getString("description");
		String format = request.getString("format");
		String keywords = request.getString("keywords");
		String titles = request.getString("titles");
		String sharePolicyS = request.getString("sharePolicy");
		Character sharePolicyC = SharePolicy.resolve(sharePolicyS);
		if (sharePolicyC == null) {
			sharePolicyC = SharePolicy.pPublic.key;
		}

		DatasetDao datasetDao = daoHandler.getMySQLMapper(DatasetDao.class);
		Dataset dataset = datasetDao.selectByKey(datasetName, this.userId);
		if (dataset != null) {
			throw new DataException("数据已存在(dataName=" + datasetName + ")");
		}
		String datasetId = MathUtil.random(24);
		insertDataset(datasetId, datasetName, description, this.userId, type, format, keywords, titles, sharePolicyC, datasetDao);

		// save data
		JSONArray dataJ = request.getJSONArray("data");
		if (dataJ != null && dataJ.size() != 0) {
			DataDao dataDao = daoHandler.getMySQLMapper(DataDao.class);
			Integer baseIdx = dataDao.selectCountByDatasetId(datasetId);
			for (int i = 0; i < dataJ.size(); i++) {
				JSONArray data1J = dataJ.getJSONArray(i);
				if (data1J == null) continue;
				String x = data1J.getString(0);
				if (x == null) continue;
				String y = data1J.getString(1);
				if (y == null) y = "";
				insertData(MathUtil.random(32), datasetId, baseIdx + i, "", x, y);
			}
		}

		JSONObject result = new JSONObject();
		result.put("dataId", datasetId);
		return result;
	}

	public JSONObject upgradeData(String userId, String datasetName, JSONObject request) throws DataException, AuthException {

		if (!this.userId.equals(userId)) throw new AuthException("没有权限");
		if (datasetName == null || "".equals(datasetName)) throw new DataException("缺少数据名");

		Integer updateBaseIdx = request.getInteger("updateBaseIdx");
		JSONArray dataJ = request.getJSONArray("data");
		JSONObject batchSet = request.getJSONObject("batchSet");

		DatasetDao datasetDao = daoHandler.getMySQLMapper(DatasetDao.class);
		Dataset dataset = datasetDao.selectByKey(datasetName, this.userId);
		if (dataset == null) throw new DataException("数据不存在(dataName=" + datasetName + ")");
		String datasetId = dataset.getDatasetId();

		if (TYPE_FILE.equalsIgnoreCase(dataset.getType()) && updateBaseIdx == null && batchSet == null) {
			throw new DataException("该数据为文件类型，不支持上传数据(dataName=" + datasetName + ")");
		}

		// save data
		DataDao dataDao = daoHandler.getMySQLMapper(DataDao.class);
		Integer baseIdx = dataDao.selectCountByDatasetId(datasetId);
		/*
		 *  "data": [
		 *    ["x1","y1"],
		 *    ["x2","y2"]
		 *  ],
		 */
		if (dataJ != null && dataJ.size() != 0) {
			if (updateBaseIdx != null) {
				// update
				for (int i = 0; i < dataJ.size(); i++) {
					if (i >= baseIdx) break;
					JSONArray data1J = dataJ.getJSONArray(i);
					if (data1J == null) continue;
					String x = data1J.getString(0);
					if (x == null) continue;
					String y = data1J.getString(1);
					if (y == null) y = "";
					updateData(datasetId, updateBaseIdx + i, x, y);
				}
			} else {
				// insert
				for (int i = 0; i < dataJ.size(); i++) {
					JSONArray data1J = dataJ.getJSONArray(i);
					if (data1J == null) continue;
					String x = data1J.getString(0);
					if (x == null) continue;
					String y = data1J.getString(1);
					if (y == null) y = "";
					insertData(MathUtil.random(32), datasetId, baseIdx + i, "", x, y);
				}
				baseIdx += dataJ.size();
			}
		}

		/*
		 *  "batchSet": {
		 *    "label1": { "start": 1, "end": 11},
		 *    "label2": { "start": 12, "end": 22}
		 *  }
		 */
		if (batchSet != null) {
			for (Entry<String, Object> entry : batchSet.entrySet()) {
				String label = entry.getKey();
				JSONObject idxs = (JSONObject)entry.getValue();
				Integer start = idxs.getInteger("start");
				Integer end = idxs.getInteger("end");
				for (int i = start; i <= end; i++) {
					if (i >= baseIdx) break;
					updateData(datasetId, i, "", label);
				}
			}
		}

		JSONObject result = new JSONObject();
		result.put("dataId", datasetId);
		return result;
	}

	private long insertDataset(String datasetId, String datasetName, String description, String userId,
			String type, String format, String keywords, String titles, Character sharePolicy, DatasetDao datasetDao) {
		Dataset datasetParam = new Dataset();
		datasetParam.setDatasetId(datasetId);
		datasetParam.setDatasetName(datasetName);
		datasetParam.setDescription(description);
		datasetParam.setUserId(userId);
		datasetParam.setFormat(format);
		datasetParam.setType(type);
		datasetParam.setKeywords(keywords);
		datasetParam.setTitles(titles);
		datasetParam.setSharePolicy(sharePolicy);
		return datasetDao.insert(datasetParam);
	}

	private long updateData(String datasetId, int idx, String x, String y) {
		DataDao dataDao = daoHandler.getMySQLMapper(DataDao.class);
		Data param = new Data();
		param.setDatasetId(datasetId);
		param.setIdx(idx);
		param.setX(x);
		param.setY(y);
		return dataDao.updateXYByKey(param);
	}

	private long insertData(String dataId, String datasetId, int idx, String ref, String x, String y) {
		DataDao dataDao = daoHandler.getMySQLMapper(DataDao.class);
		Data param = new Data();
		param.setDataId(dataId);
		param.setDatasetId(datasetId);
		param.setIdx(idx);
		param.setRef(ref);
		param.setX(x);
		param.setY(y);
		return dataDao.insert(param);
	}

	public JSONObject saveUploadFiles(String userId, String datasetName, MultipartFile[] uploadfiles) throws StdaiException {

		if (!this.userId.equals(userId)) throw new AuthException("没有权限");
		if (datasetName == null || "".equals(datasetName)) throw new DataException("缺少数据名");

		DatasetDao datasetDao = daoHandler.getMySQLMapper(DatasetDao.class);
		Dataset dataset = datasetDao.selectByKey(datasetName, this.userId);
		String datasetId;
		if (dataset == null) throw new AuthException("找不到该数据集");

		if (!TYPE_FILE.equalsIgnoreCase(dataset.getType())) {
			throw new DataException("该数据非文件类型，不支持上传文件(dataName=" + datasetName + ")");
		}
		datasetId = dataset.getDatasetId();

		boolean hasFailure = false;
		if (uploadfiles != null && uploadfiles.length != 0) {
			DataDao dataDao = daoHandler.getMySQLMapper(DataDao.class);
			Integer baseIdx = dataDao.selectCountByDatasetId(datasetId);
			for (int i = 0; i < uploadfiles.length; i++) {
				JSONObject subResult = new JSONObject();
				String newName = MathUtil.random(64);
				subResult = saveUploadFile(uploadfiles[i], newName);
				if (!"success".equals(subResult.getString("result"))) {
					hasFailure = true;
					continue;
				}
				String path = Context.getProp().getLocal().getUploadTemp() + newName;
				insertData(MathUtil.random(32), datasetId, baseIdx + i, path, "", "");
			}
		}

		JSONObject result = new JSONObject();
		if (hasFailure) {
			result.put("result", "warn");
			result.put("message", "部分文件上传失败");
		} else {
			result.put("result", "success");
		}
		return result;
	}

	private JSONObject saveUploadFile(MultipartFile inputFile, String newName) {
		JSONObject result = new JSONObject();
		// output file buffer
		BufferedOutputStream outputFileBuffer = null;
		// output file
		FileOutputStream outputFile = null;
		// input file name
		//String inputFileName = inputFile.getOriginalFilename();
		// output file path
		String outputFilePath = Context.getProp().getLocal().getUploadTemp() + newName;
		if (!inputFile.isEmpty()) {
			try {
				outputFile = new FileOutputStream(new File(outputFilePath));
				outputFileBuffer = new BufferedOutputStream(outputFile);
				outputFileBuffer.write(inputFile.getBytes());
				result.put("result", "success");
			} catch (Exception e) {
				result.put("result", "failure");
				result.put("message", "Failed to Upload(" + e.getMessage() + ")");
			} finally {
				if (outputFileBuffer != null) {
					try {
						outputFileBuffer.close();
						outputFileBuffer = null;
					} catch (Exception e) {
						outputFileBuffer = null;
						result.put("result", "failure");
						result.put("message", "Failed to Upload(" + e.getMessage() + ")");
					}
				}
				if (outputFile != null) {
					try {
						outputFile.close();
						outputFile = null;
					} catch (Exception e) {
						outputFile = null;
						result.put("result", "failure");
						result.put("message", "Failed to Upload(" + e.getMessage() + ")");
					}
				}
			}
		} else {
			result.put("result", "failure");
			result.put("message", "File was empty");
		}
		return result;
	}

	public JSONObject saveScratchFiles(String userId, String datasetName, JSONObject request) throws StdaiException {

		if (!this.userId.equals(userId)) throw new AuthException("没有权限");
		if (datasetName == null || "".equals(datasetName)) throw new DataException("缺少数据名");

		DatasetDao datasetDao = daoHandler.getMySQLMapper(DatasetDao.class);
		Dataset dataset = datasetDao.selectByKey(datasetName, this.userId);
		String datasetId;
		if (dataset == null) throw new AuthException("找不到该数据集");

		if (!TYPE_FILE.equalsIgnoreCase(dataset.getType())) {
			throw new DataException("该数据非文件类型，不支持上传文件(dataName=" + datasetName + ")");
		}
		datasetId = dataset.getDatasetId();

		JSONObject result = new JSONObject();
		String urlStr = request.getString("url");
		JSONArray paramJ = request.getJSONArray("param");
		if (paramJ != null && paramJ.size() != 0) {
			int paramCount = 0;
			while (true) {
				String wildcard = PARAM_EG.replace("?", String.valueOf(paramCount));
				if (urlStr.indexOf(wildcard) != -1) {
					paramCount = paramCount + 1;
				} else {
					break;
				}
			}

			if (paramJ.size() != paramCount) throw new DataException("参数不匹配");

			DataDao dataDao = daoHandler.getMySQLMapper(DataDao.class);
			Integer baseIdx = dataDao.selectCountByDatasetId(datasetId);
			JSONObject subResult = saveScratchUrl(urlStr, paramJ, 0, datasetId, baseIdx);
			if (!"success".equals(subResult.getString("result"))) {
				result.put("result", "warn");
				result.put("message", "部分文件上传失败");
			} else {
				result.put("result", "success");
			}
		}
		
		return result;
	}

	private JSONObject saveScratchUrl(String urlStr, JSONArray paramArray, int index, String datasetId, int baseIdx) throws DataException {

		JSONObject result = new JSONObject();
		int nowBaseIdx = baseIdx;
		String wildcard = PARAM_EG.replace("?", String.valueOf(index));
		JSONObject paramJson = paramArray.getJSONObject(index);
		if (paramJson.get("start") != null) {
			Integer start = paramJson.getInteger("start");
			Integer end = paramJson.getInteger("end");
			for (int i = start; i <= end; i++) {
				String urlAfter = changeFirst(urlStr, wildcard, String.valueOf(i));
				if (urlAfter.indexOf(wildcard) != -1) throw new DataException("替换字符" + wildcard + "个数与对应参数不符");
				if (index + 1 != paramArray.size()) {
					result = saveScratchUrl(urlAfter, paramArray, index + 1, datasetId, nowBaseIdx);
					nowBaseIdx = result.getInteger("nowBaseIdx");
				} else {
					String newName = MathUtil.random(64);
					result = saveScratchFile(urlAfter, newName);
					if (!"success".equals(result.getString("result"))) {
						continue;
					}
					String path = Context.getProp().getLocal().getUploadTemp() + newName;
					nowBaseIdx = nowBaseIdx + 1;
					insertData(MathUtil.random(32), datasetId, nowBaseIdx, path, "", "");
				}
			}
		} else {
			JSONArray jsonArray = paramJson.getJSONArray("item");
			for (int j = 0; j < jsonArray.size(); j++) {
				String urlAfter = changeFirst(urlStr, wildcard, jsonArray.getString(j));
				if (urlAfter.indexOf(wildcard) != -1) throw new DataException("替换字符" + wildcard + "个数与对应参数不符");
				if (index + 1 != paramArray.size()) {
					result = saveScratchUrl(urlAfter, paramArray, index + 1, datasetId, nowBaseIdx);
					nowBaseIdx = result.getInteger("nowBaseIdx");
				} else {
					String newName = MathUtil.random(64);
					result = saveScratchFile(urlAfter, newName);
					if (!"success".equals(result.getString("result"))) {
						continue;
					}
					String path = Context.getProp().getLocal().getUploadTemp() + newName;
					nowBaseIdx = nowBaseIdx + 1;
					insertData(MathUtil.random(32), datasetId, nowBaseIdx, path, "", "");
				}
			}
		}

		result.put("nowBaseIdx", nowBaseIdx);
		return result;
	}
	
	private String changeFirst(String urlStr, String wildcard, String change) {
		int index = urlStr.indexOf(wildcard);
		return urlStr.substring(0, index) + change + urlStr.substring(index + 3);
	}

	private JSONObject saveScratchFile(String urlStr, String newName) {

		System.out.println(urlStr);
		JSONObject result = new JSONObject();
		InputStream inputStream = null;
		ByteArrayOutputStream bos = null;
		BufferedOutputStream outputFileBuffer = null;
		FileOutputStream outputFile = null;
		String outputFilePath = Context.getProp().getLocal().getUploadTemp() + newName;
		try {
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// 防止屏蔽程序抓取而返回403错误
			conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

			inputStream = conn.getInputStream();
			bos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = inputStream.read(buffer)) != -1) {
				bos.write(buffer, 0, len);
			}
			byte[] getData = bos.toByteArray();
			outputFile = new FileOutputStream(new File(outputFilePath));
			outputFileBuffer = new BufferedOutputStream(outputFile);
			outputFileBuffer.write(getData);
			result.put("result", "success");
		} catch (Exception e) {
			result.put("result", "failure");
			result.put("message", "Failed to Upload(" + e.getMessage() + ")");
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
					inputStream = null;
				} catch (Exception e) {
					inputStream = null;
					result.put("result", "failure");
					result.put("message", "Failed to Upload(" + e.getMessage() + ")");
				}
			}
			if (bos != null) {
				try {
					bos.close();
					bos = null;
				} catch (Exception e) {
					bos = null;
					result.put("result", "failure");
					result.put("message", "Failed to Upload(" + e.getMessage() + ")");
				}
			}
			if (outputFileBuffer != null) {
				try {
					outputFileBuffer.close();
					outputFileBuffer = null;
				} catch (Exception e) {
					outputFileBuffer = null;
					result.put("result", "failure");
					result.put("message", "Failed to Upload(" + e.getMessage() + ")");
				}
			}
			if (outputFile != null) {
				try {
					outputFile.close();
					outputFile = null;
				} catch (Exception e) {
					outputFile = null;
					result.put("result", "failure");
					result.put("message", "Failed to Upload(" + e.getMessage() + ")");
				}
			}
		}

		return result;
	}

	public JSONObject listData() {

		DatasetDao dao = daoHandler.getMySQLMapper(DatasetDao.class);
		List<Dataset> dataset = dao.selectByUserId(userId);

		JSONArray dataJ = new JSONArray();
		for (int i = 0; i < dataset.size(); i++) {
			JSONObject data1J = new JSONObject();
			data1J.put("dataId", dataset.get(i).getDatasetId());
			data1J.put("dataName", dataset.get(i).getDatasetName());
			data1J.put("description", dataset.get(i).getDescription());
			data1J.put("userId", dataset.get(i).getUserId());
			data1J.put("type", dataset.get(i).getType());
			data1J.put("format", dataset.get(i).getFormat());
			data1J.put("keywords", dataset.get(i).getKeywords());
			data1J.put("titles", dataset.get(i).getTitles());
			data1J.put("sharePolicy", SharePolicy.parse(dataset.get(i).getSharePolicy()));
			data1J.put("createTime", dataset.get(i).getCreateTime());
			dataJ.add(data1J);
		}
		JSONObject result = new JSONObject();
		result.put("data", dataJ);

		return result;
	}

	public JSONObject viewData(String userId, String dataName) throws AuthException {

		if (!userId.equals(this.userId)) throw new AuthException("没有权限");
		DatasetDao dao = daoHandler.getMySQLMapper(DatasetDao.class);
		Dataset dataset = dao.selectByKey(dataName, userId);

		JSONObject dataJ = new JSONObject();
		dataJ.put("dataId", dataset.getDatasetId());
		dataJ.put("dataName", dataset.getDatasetName());
		dataJ.put("description", dataset.getDescription());
		dataJ.put("userId", dataset.getUserId());
		dataJ.put("type", dataset.getType());
		dataJ.put("format", dataset.getFormat());
		dataJ.put("keywords", dataset.getKeywords());
		dataJ.put("titles", dataset.getTitles());
		dataJ.put("sharePolicy", SharePolicy.parse(dataset.getSharePolicy()));
		dataJ.put("createTime", dataset.getCreateTime());

		DataDao dao2 = daoHandler.getMySQLMapper(DataDao.class);
		Integer count = dao2.selectCountByDatasetId(dataset.getDatasetId());
		Data data1 = dao2.select1ByDatasetId(dataset.getDatasetId());

		dataJ.put("count", count);
		if (data1 != null) {
			dataJ.put("ref", data1.getRef());
			dataJ.put("x", data1.getX());
			dataJ.put("y", data1.getY());
		}

		JSONObject result = new JSONObject();
		result.put("data", dataJ);

		return result;
	}

	public void removeData(String userId, String dataName) throws AuthException {
		if (!userId.equals(this.userId)) throw new AuthException("没有权限");
		DatasetDao dao = daoHandler.getMySQLMapper(DatasetDao.class);
		dao.deleteByKey(dataName, userId);
	}
}
