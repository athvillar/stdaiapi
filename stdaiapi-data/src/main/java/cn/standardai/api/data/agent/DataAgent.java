package cn.standardai.api.data.agent;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

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

		DatasetDao datasetDao = daoHandler.getMySQLMapper(DatasetDao.class);
		Dataset dataset = datasetDao.selectByKey(datasetName, this.userId);
		if (dataset == null) throw new DataException("数据不存在(dataName=" + datasetName + ")");
		String datasetId = dataset.getDatasetId();

		if (TYPE_FILE.equalsIgnoreCase(dataset.getType()) && updateBaseIdx == null) {
			throw new DataException("该数据为文件类型，不支持上传数据(dataName=" + datasetName + ")");
		}

		// save data
		if (dataJ != null && dataJ.size() != 0) {
			DataDao dataDao = daoHandler.getMySQLMapper(DataDao.class);
			Integer baseIdx = dataDao.selectCountByDatasetId(datasetId);
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
			}
		}

		JSONObject result = new JSONObject();
		result.put("dataId", datasetId);
		return result;
	}

	private long insertDataset(String datasetId, String datasetName, String description, String userId,
			String format, String type, String keywords, String titles, Character sharePolicy, DatasetDao datasetDao) {
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
				subResult = saveUploadFile(uploadfiles[i]);
				if (!"success".equals(subResult.getString("result"))) {
					hasFailure = true;
					continue;
				}
				String path = Context.getProp().getLocal().getUploadTemp() + uploadfiles[i].getOriginalFilename();
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

	private JSONObject saveUploadFile(MultipartFile inputFile) {
		JSONObject result = new JSONObject();
		// output file buffer
		BufferedOutputStream outputFileBuffer = null;
		// output file
		FileOutputStream outputFile = null;
		// input file name
		String inputFileName = inputFile.getOriginalFilename();
		// output file path
		String outputFilePath = Context.getProp().getLocal().getUploadTemp() + inputFileName;
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

	public JSONObject saveScratchFiles(JSONObject request) {
		// TODO Auto-generated method stub
		return null;
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
