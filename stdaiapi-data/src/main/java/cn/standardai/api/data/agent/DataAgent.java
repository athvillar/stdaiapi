package cn.standardai.api.data.agent;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.base.AuthAgent;
import cn.standardai.api.core.bean.Context;
import cn.standardai.api.core.util.MathUtil;
import cn.standardai.api.dao.DataDao;
import cn.standardai.api.dao.DatasetDao;
import cn.standardai.api.dao.bean.Dataset;
import cn.standardai.api.dao.bean.Data;
import cn.standardai.api.data.exception.DataException;

public class DataAgent extends AuthAgent {

	public JSONObject saveJSONData(JSONObject dataRequest) throws DataException {

		JSONObject result = new JSONObject();

		String datasetId = dataRequest.getString("datasetId");
		String datasetName = dataRequest.getString("datasetName");
		String format = dataRequest.getString("format");

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

		// save data
		JSONArray data = dataRequest.getJSONArray("data");
		DataDao dataDao = daoHandler.getMySQLMapper(DataDao.class);
		Integer baseIdx = dataDao.selectCountByDatasetId(datasetId);
		for (int i = 0; i < data.size(); i++) {
			String data1 = data.getString(i);
			Data param = new Data();
			param.setDatasetId(datasetId);
			param.setIdx(baseIdx + i);
			param.setX(data1);
			dataDao.insert(param);
		}

		// make result
		result.put("datasetId", datasetId);
		return result;
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
}
