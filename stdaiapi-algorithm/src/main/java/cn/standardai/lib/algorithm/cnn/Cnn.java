package cn.standardai.lib.algorithm.cnn;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.lib.algorithm.cnn.Layer.LayerType;
import cn.standardai.lib.algorithm.common.ByteUtil;
import cn.standardai.lib.algorithm.exception.StorageException;

public class Cnn {

	// TODO public
	public List<Layer> layers = new ArrayList<Layer>();

	private List<Integer[][][]> batchData = new ArrayList<Integer[][][]>();

	private List<Integer[]> batchExpect = new ArrayList<Integer[]>();

	private List<JSONObject> batchJSON = new ArrayList<JSONObject>();

	/*
		{
		  "maxTrainingCount" : 10,
		  "layers" : [
		    {"type": "INPUT", "width": 24, "height": 24, "depth": 3 },
		    {"type": "CONV", "depth": 16, "stride": 1, "padding":1,
		      "filter": {"width":3, "height":3}
		    },
		    {"type": "RELU", "function": "max"},
		    {"type": "CONV", "depth": 16, "stride": 1, "padding":1,
		      "filter": {"width":3, "height":3}
		    },
		    {"type": "RELU", "function": "max"},
		    {"type": "POOL", "method": "max", "spatial": 2, "stride": 2},
		    {"type": "FC", "depth": 10 }
		  ]
		}
	 */
	public static Cnn getInstance(JSONObject param) throws CnnException {

		JSONArray layersJSONArray = param.getJSONArray("layers");
		if (layersJSONArray == null || layersJSONArray.size() < 3) return null;
		if (!"INPUT".equalsIgnoreCase(layersJSONArray.getJSONObject(0).getString("type")) ||
			!"FC".equalsIgnoreCase(layersJSONArray.getJSONObject(layersJSONArray.size() - 1).getString("type")))
				return null;

		Cnn instance = new Cnn();
		for (int i = 0; i < layersJSONArray.size(); i++) {
			LayerType type = Layer.parseType(layersJSONArray.getJSONObject(i).getString("type"));
			if (type == null) return null;
			Layer layer1 = null;
			switch (type) {
			case input:
				layer1 = new InputLayer(
						layersJSONArray.getJSONObject(i).getInteger("width"),
						layersJSONArray.getJSONObject(i).getInteger("height"),
						layersJSONArray.getJSONObject(i).getInteger("depth"));
				break;
			case conv:
				layer1 = new ConvLayer(
						layersJSONArray.getJSONObject(i).getInteger("depth"),
						layersJSONArray.getJSONObject(i).getInteger("stride"),
						layersJSONArray.getJSONObject(i).getInteger("padding"),
						layersJSONArray.getJSONObject(i).getJSONObject("filter").getInteger("width"),
						layersJSONArray.getJSONObject(i).getJSONObject("filter").getInteger("height"),
						layersJSONArray.getJSONObject(i).getDouble("learningRate"));
				break;
			case relu:
				layer1 = new ReluLayer(layersJSONArray.getJSONObject(i).getString("function"));
				break;
			case pool:
				layer1 = new PoolLayer(layersJSONArray.getJSONObject(i).getString("method"),
						layersJSONArray.getJSONObject(i).getInteger("spatial"),
						layersJSONArray.getJSONObject(i).getInteger("stride"));
				break;
			case fc:
				layer1 = new FCLayer(layersJSONArray.getJSONObject(i).getInteger("depth"),
						layersJSONArray.getJSONObject(i).getDouble("learningRate"));
				break;
			default:
				break;
			}
			layer1.format(i > 0 ? instance.layers.get(i - 1) : null);
			layer1.setAF(layersJSONArray.getJSONObject(i).getString("aF"));
			instance.layers.add(layer1);
		}

		return instance;
	}

	public void train(Integer batchSize, Integer batchCount) {
		Integer trainingCount = 0;
		do {
			trainingCount++;
			if (this.batchJSON.size() != 0) {
				int[] batchNums = randBatchNums(batchSize, batchJSON.size());
				clearError();
				for (int i = 0; i < batchNums.length; i++) {
					this.get1stLayer().setData(this.batchJSON.get(batchNums[i]).getJSONArray("data"));
					this.getLastLayer().setTarget(this.batchJSON.get(batchNums[i]).getJSONArray("target"));
					forward();
					backward();
				}
				// TODO adjust(1);
				adjust(batchNums.length);
			} else if (this.batchData.size() != 0) {
				int[] batchNums = randBatchNums(batchSize, batchData.size());
				clearError();
				for (int i = 0; i < batchNums.length; i++) {
					this.get1stLayer().setData(this.batchData.get(batchNums[i]));
					this.getLastLayer().setTarget(this.batchExpect.get(batchNums[i]));
					forward();
					backward();
				}
				// TODO adjust(1);
				adjust(batchNums.length);
			}
		} while (trainingCount < batchCount);
	}

	public void forward() {
		for (int i = 0; i < layers.size(); i++) {
			if (i != 0) layers.get(i).exec(layers.get(i - 1));
			//layers.get(i).print();
			//if (layers.get(i) instanceof FCLayer) layers.get(i).printFilter();
		}
	}

	public void backward() {
		layers.get(layers.size() - 1).calcError();
		for (int i = layers.size() - 1; i >= 1; i--) {
			if (i >= 2) layers.get(i).calcPrevError(layers.get(i - 1));
			//layers.get(i).print();
			//if (layers.get(i) instanceof ConvLayer) layers.get(i).printError();
		}
	}

	private void clearError() {
		for (int i = 1; i < layers.size(); i++) {
			layers.get(i).initError();
		}
	}

	private void adjust(int batchNum) {
		for (int i = layers.size() - 1; i >= 1; i--) {
			if (layers.get(i) instanceof ConvLayer) {
				layers.get(i).upgrade(layers.get(i - 1), batchNum);
				//layers.get(i).printFilter();
			}
		}
	}

	private boolean canFinish(Integer trainingCount) {
		return true;
	}

	public Double[][][] predict(JSONObject data) {
		loadData(data);
		forward();
		// TODO no backward
		//backward();
		//printSome();
		return getLastLayer().data;
	}

	public Double[][][] predict(Integer[][][] data) {
		this.get1stLayer().setData(data);
		//this.get1stLayer().printData();
		forward();
		// TODO no backward
		//backward();
		//printSome();
		return getLastLayer().data;
	}

	private void printSome() {

		for (int l = 0; l < this.layers.size(); l++) {
			System.out.println("\n------" + l + "th--- " + this.layers.get(l).getClass().toString().substring(this.layers.get(l).getClass().toString().lastIndexOf(".")+1) + "'s data-");
			for (int i = 0; i < this.layers.get(l).data[0][0].length; i++) {
				System.out.print(this.layers.get(l).data[0][0][i] + ",\t");
			}
			/*
			if (this.layers.get(l) instanceof ConvLayer) {
				System.out.println("\nand the filter:");
				for (int i = 0; i < ((ConvLayer)this.layers.get(l)).filters.get(0).w[0][0].length; i++) {
					System.out.print(((ConvLayer)this.layers.get(l)).filters.get(0).w[0][0][i] + ",\t");
				}
			}
			*/
		}
	}

	public static byte[] getBytes(Cnn cnn) {

		int size = 0;
		int layerNum = cnn.layers.size();
		byte[] layersSerial = new byte[layerNum];
		Integer[] layersLength = new Integer[layerNum];
		List<byte[]> layersBytes = new ArrayList<byte[]>();
		for (int i = 0; i < cnn.layers.size(); i++) {
			layersSerial[i] = cnn.layers.get(i).getSerial();
			layersBytes.add(cnn.layers.get(i).getBytes());
			layersLength[i] = layersBytes.get(i).length;
			size += layersLength[i];
			size += Integer.BYTES;
			size++;
		}

		byte[] bytes = new byte[size];
		int index = 0;
		for (int i = 0; i < layerNum; i++) {
			bytes[index++] = layersSerial[i];
			index += ByteUtil.putInt(bytes, layersLength[i], index);
			System.arraycopy(layersBytes.get(i), 0, bytes, index, layersLength[i]);
			index += layersLength[i];
		}

		return bytes;
	}

	public static Cnn getInstance(byte[] bytes) throws StorageException {

		Cnn cnn = new Cnn();
		int index = 0;
		while (index < bytes.length) {
			byte layerSirial = bytes[index++];
			Layer layer = Layer.getInstance(layerSirial);
			int layerLength = ByteUtil.getInt(bytes, index);
			index += Integer.BYTES;
			byte[] layerBytes = new byte[layerLength];
			System.arraycopy(bytes, index, layerBytes, 0, layerLength);
			index += layerLength;
			layer.load(layerBytes);
			cnn.layers.add(layer);
		}

		return cnn;
	}

	public void loadData(JSONObject param) {
		this.get1stLayer().setData(param.getJSONArray("data"));
		this.getLastLayer().setTarget(param.getJSONArray("target"));
	}

	public void loadData(Integer[][][] input, Integer[] expect) {
		this.get1stLayer().setData(input);
		this.getLastLayer().setTarget(expect);
	}

	public int dataCount() {
		if (this.batchJSON != null && this.batchJSON.size() != 0) return this.batchJSON.size();
		if (this.batchData != null && this.batchData.size() != 0) return this.batchData.size();
		return 0;
	}

	public void clearData() {
		if (this.batchData != null) {
			this.batchData.clear();
			this.batchExpect.clear();
		}
		if (this.batchJSON != null) {
			this.batchJSON.clear();
		}
	}

	public void addData(JSONObject param) {
		this.batchJSON.add(param);
	}

	public void addData(Integer[][][] input, Integer[] expect) {
		this.batchData.add(input);
		this.batchExpect.add(expect);
	}

	private InputLayer get1stLayer() {
		if (this.layers == null) return null;
		return (InputLayer)this.layers.get(0);
	}

	private FCLayer getLastLayer() {
		if (this.layers == null) return null;
		return (FCLayer)this.layers.get(this.layers.size() - 1);
	}

	private int[] randBatchNums(int count, int maxSize) {
		int[] batchNums = new int[count];
		for (int i = 0; i < count; i++) {
			batchNums[i] = new Double(Math.random() * maxSize).intValue();
		}
		return batchNums;
	}
}
