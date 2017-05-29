package cn.standardai.lib.algorithm.cnn;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.lib.algorithm.base.Dnn;
import cn.standardai.lib.algorithm.cnn.Layer.LayerType;
import cn.standardai.lib.algorithm.common.ByteUtil;
import cn.standardai.lib.algorithm.exception.StorageException;
import cn.standardai.lib.algorithm.exception.UsageException;
import cn.standardai.lib.base.function.Statistic;
import cn.standardai.lib.base.matrix.MatrixException;
import cn.standardai.lib.base.matrix.MatrixUtil;

public class Cnn extends Dnn<CnnData> {

	// TODO public
	public List<Layer> layers = new ArrayList<Layer>();

	//private List<Integer[][][]> batchData = new ArrayList<Integer[][][]>();

	//private List<Integer[]> batchExpect = new ArrayList<Integer[]>();

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

	public void train() throws UsageException, MatrixException {

		long startTime = new Date().getTime();
		int epochCount = 0;
		boolean needBreak = false;
		List<Integer> indice = initIndice(getTrainDataCnt());

		while (true) {
			epochCount++;

			List<Integer> indiceCopy = new LinkedList<Integer>();
			indiceCopy.addAll(indice);
			while (indiceCopy.size() != 0) {

				Integer[] batchIndice = getNextBatchIndex(indiceCopy, batchSize);
				clearError();
				for (int i = 0; i < batchIndice.length; i++) {
					this.get1stLayer().setData(this.data[batchIndice[i]].x);
					this.getLastLayer().setTarget(this.data[batchIndice[i]].y);
					forward();
					backward();
				}
				// TODO adjust(1);
				adjust(batchIndice.length);

				if (trainMillisecond != null && (new Date().getTime() - startTime) >= trainMillisecond) {
					needBreak = true;
					break;
				}
			}
			if (needBreak) break;

			if (watchEpoch != null && epochCount % watchEpoch == 0) {
				synchronized (this.indicator) {
					if (this.containCatalog("trainLoss")) {
						record("trainLoss", epochCount, MatrixUtil.sumAbs(this.layers.get(this.layers.size() - 1).error));
					}
					if (this.containCatalog("testLoss")) {
						//record("testLoss", epochCount, 0.0);
					}
					this.indicator.notify();
					System.out.println("epoch:" + epochCount + " trainLoss" + MatrixUtil.sumAbs(this.layers.get(this.layers.size() - 1).error));
				}
			}
			if (epoch != null && epochCount >= epoch) break;
		}

		// Finish indicator, tell monitor to stop monitoring
		synchronized (this.indicator) {
			finish();
			this.indicator.notify();
		}
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

	public Integer[] predictY(Integer[][][] data) {

		Double[][][] y1 = predict(data);
		Double[] y2 = new Double[y1[0][0].length];
		for (int i = 0; i < y2.length; i++) {
			y2[i] = y1[0][0][i];
		}

		/*
		Roulette r = new Roulette(y2);
		int idx = r.getY();
		*/
		Integer idx = Statistic.maxIndex(y2);
		Integer[] y3 = new Integer[y2.length];
		for (int i = 0; i < y3.length; i++) {
			if (i == idx) {
				y3[i] = 1;
			} else {
				y3[i] = 0;
			}
		}

		return y3;
	}

	@Override
	public byte[] getBytes() {

		int size = 0;
		int layerNum = this.layers.size();
		byte[] layersSerial = new byte[layerNum];
		Integer[] layersLength = new Integer[layerNum];
		List<byte[]> layersBytes = new ArrayList<byte[]>();
		for (int i = 0; i < this.layers.size(); i++) {
			layersSerial[i] = this.layers.get(i).getSerial();
			layersBytes.add(this.layers.get(i).getBytes());
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
		if (this.data != null && this.data.length != 0) return this.data.length;
		return 0;
	}

	public void clearData() {
		if (this.data != null) {
			this.data = null;
		}
		if (this.batchJSON != null) {
			this.batchJSON.clear();
		}
	}

	public void addData(JSONObject param) {
		this.batchJSON.add(param);
	}
/*
	public void addData(Integer[][][] input, Integer[] expect) {
		this.batchData.add(input);
		this.batchExpect.add(expect);
	}
*/
	private InputLayer get1stLayer() {
		if (this.layers == null) return null;
		return (InputLayer)this.layers.get(0);
	}

	private FCLayer getLastLayer() {
		if (this.layers == null) return null;
		return (FCLayer)this.layers.get(this.layers.size() - 1);
	}

	@Override
	public void setDth(Double dth) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLearningRate(Double η) {
		for (int i = 0; i < this.layers.size(); i++) {
			// TODO
			//this.layers.get(i).setLearningRate(η);
		}
	}
}
