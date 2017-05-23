package cn.standardai.api.data.agent;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.StringUtil;

import cn.standardai.api.core.base.AuthAgent;
import cn.standardai.api.core.bean.Context;
import cn.standardai.api.core.exception.AuthException;
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

	private static final String FORMAT_JSON = "JSON";

	private static final String FORMAT_CSV = "CSV";

	private static final String FORMAT_JPG = "JPG";

	private static final String FORMAT_TEXT = "TEXT";

	public JSONObject saveJSONData(JSONObject dataRequest) throws DataException {

		JSONObject result = new JSONObject();

		String datasetId = dataRequest.getString("datasetId");
		String datasetName = dataRequest.getString("datasetName");
		String sharePolicyType = dataRequest.getString("sharePolicyType");

		// save dataset
		JSONObject datasetResult = saveDateset(datasetId, datasetName, TYPE_DATA, FORMAT_JSON, sharePolicyType);
		if (!StringUtil.isEmpty(datasetResult.getString("msg"))) {
			throw new DataException("Failed to JsonUpload:" + datasetResult);
		}
		datasetId = datasetResult.getString("datasetId");

		// save data
		JSONArray data = dataRequest.getJSONArray("data");
		DataDao dataDao = daoHandler.getMySQLMapper(DataDao.class);
		Integer baseIdx = dataDao.selectCountByDatasetId(datasetId);
		for (int i = 0; i < data.size(); i++) {
			JSONObject dataJson = data.getJSONObject(i);
			if (insertData(MathUtil.random(32), datasetId, baseIdx + i, "", dataJson.getString("features"),
					dataJson.getString("label")) == 0) {
				throw new DataException("Failed to JsonUpload:" + "no insert data(" + datasetId + ")");
			}
		}

		// make result
		result.put("datasetId", datasetId);
		return result;
	}

	private JSONObject saveDateset(String datasetId, String datasetName, String type, String format,
			String sharePolicyType) {
		DatasetDao datasetDao = daoHandler.getMySQLMapper(DatasetDao.class);
		JSONObject result = new JSONObject();
		Character sharePolicy = SharePolicy.resolve(sharePolicyType);
		if (sharePolicy == null) {
			result.put("msg", "error sharePolicyType(" + datasetId + ")");
			return result;
		}
		if (datasetName == null) {
			if (datasetId == null) {
				// 未提供id和name，insert新记录
				datasetId = MathUtil.random(24);
				if (insertDataset(datasetId, datasetId, userId, type, format, sharePolicy) == 0) {
					result.put("msg", "no insert dataset(" + datasetId + ")");
				}
			} else {
				// 提供id，未提供name，检索该id，未检出则报异常
				Dataset datasetParam = new Dataset();
				datasetParam.setDatasetId(datasetId);
				datasetParam.setUserId(userId);
				if (datasetDao.selectCountByIdUser(datasetParam) == 0) {
					result.put("msg", "no dataset(" + datasetId + ")");
				}
			}
		} else {
			if (datasetId == null) {
				// 提供name，未提供id，按照name检索，检索成功使用检出的id执行后续处理，未检出生成新id执行后续处理，并insert
				Dataset dataset = datasetDao.selectByKey(datasetName, userId);
				if (dataset == null) {
					datasetId = MathUtil.random(24);
					if (insertDataset(datasetId, datasetName, userId, type, format, sharePolicy) == 0) {
						result.put("msg", "no insert dataset(" + datasetId + ")");
					}
				} else {
					datasetId = dataset.getDatasetId();
				}
			} else {
				// 提供id与name，按照id检索，未检出报异常，检出更新name
				Dataset datasetParam = new Dataset();
				datasetParam.setDatasetId(datasetId);
				datasetParam.setUserId(userId);
				if (datasetDao.selectCountByIdUser(datasetParam) == 0) {
					result.put("msg", "no dataset(" + datasetId + ")");
				} else {
					datasetParam.setDatasetName(datasetName);
					datasetDao.updateById(datasetParam);
				}
			}
		}
		result.put("datasetId", datasetId);
		return result;
	}

	private long insertDataset(String datasetId, String datasetName, String userId, String type, String format,
			Character sharePolicy) {
		DatasetDao datasetDao = daoHandler.getMySQLMapper(DatasetDao.class);
		Dataset datasetParam = new Dataset();
		datasetParam.setDatasetId(datasetId);
		datasetParam.setDatasetName(datasetName);
		datasetParam.setUserId(userId);
		datasetParam.setType(type);
		datasetParam.setFormat(format);
		datasetParam.setSharePolicy(sharePolicy);
		return datasetDao.insert(datasetParam);
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

	public JSONObject saveUploadFiles(MultipartFile[] uploadfiles) {
		JSONObject result = new JSONObject();

		boolean hasFailure = false;
		for (int i = 0; i < uploadfiles.length; i++) {
			JSONObject subResult = new JSONObject();
			subResult = saveUploadFile(uploadfiles[i]);
			if (!"success".equals(subResult.getString("result")))
				hasFailure = true;
		}

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
