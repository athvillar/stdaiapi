package cn.standardai.lib.algorithm.cnn;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.lib.algorithm.cnn.Layer.LayerType;

public class CNN {

	// TODO public
	public List<Layer> layers;

	private Integer maxTrainingCount;

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
	public static CNN getInstance(JSONObject param) throws CnnException {

		JSONArray layersJSONArray = param.getJSONArray("layers");
		if (layersJSONArray == null || layersJSONArray.size() < 3) return null;
		if (!"INPUT".equalsIgnoreCase(layersJSONArray.getJSONObject(0).getString("type")) ||
			!"FC".equalsIgnoreCase(layersJSONArray.getJSONObject(layersJSONArray.size() - 1).getString("type")))
				return null;

		CNN instance = new CNN();
		instance.maxTrainingCount = param.getInteger("maxTrainingCount");
		instance.layers = new ArrayList<Layer>();
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

	public void train(Integer maxTrainingCount) {
		Integer trainingCount = 0;
		do {
			trainingCount++;
			if (this.batchJSON.size() != 0) {
				int[] batchNums = randBatchNums(batchJSON.size());
				clearError();
				for (int i = 0; i < batchNums.length; i++) {
					this.get1stLayer().setData(this.batchJSON.get(batchNums[i]).getJSONArray("data"));
					this.getLastLayer().setTarget(this.batchJSON.get(batchNums[i]).getJSONArray("target"));
					forward();
					backward();
				}
				adjust(batchNums.length);
				// TODO adjust(batchNums.length);
			} else if (this.batchData.size() != 0) {
				int[] batchNums = randBatchNums(batchData.size());
				clearError();
				for (int i = 0; i < batchNums.length; i++) {
					this.get1stLayer().setData(this.batchData.get(batchNums[i]));
					this.getLastLayer().setTarget(this.batchExpect.get(batchNums[i]));
					forward();
					backward();
				}
				adjust(batchNums.length);
				// TODO adjust(batchNums.length);
			}
		} while (trainingCount < maxTrainingCount);
	}

	public void train() {
		train(this.maxTrainingCount);
	}

	private void forward() {
		for (int i = 0; i < layers.size(); i++) {
			if (i != 0) layers.get(i).exec(layers.get(i - 1));
			//layers.get(i).print();
			//if (layers.get(i) instanceof FCLayer) layers.get(i).printFilter();
		}
	}

	private void backward() {
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
		return (trainingCount >= this.maxTrainingCount);
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

	public void save() {
		// TODO
	}

	public void load() {
		// TODO
	}

	public void loadData(JSONObject param) {
		this.get1stLayer().setData(param.getJSONArray("data"));
		this.getLastLayer().setTarget(param.getJSONArray("target"));
	}

	public void loadData(Integer[][][] input, Integer[] expect) {
		this.get1stLayer().setData(input);
		this.getLastLayer().setTarget(expect);
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

	private int[] randBatchNums(int size) {
		int batchSize = size / 15 + 1;
		//int batchSize = 2;
		int[] batchNums = new int[batchSize];
		for (int i = 0; i < batchSize; i++) {
			batchNums[i] = new Double(Math.random() * size).intValue();
		}
		return batchNums;
	}
}
