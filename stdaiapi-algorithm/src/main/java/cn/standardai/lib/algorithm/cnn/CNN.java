package cn.standardai.lib.algorithm.cnn;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.lib.algorithm.cnn.Layer.LayerType;

public class CNN {

	private final Integer maxTrainingCount = 500;

	private List<Layer> layers;

	private Integer trainingCount = 0;

	/*
		{
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
						layersJSONArray.getJSONObject(i).getJSONObject("filter").getInteger("height"));
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
				layer1 = new FCLayer(layersJSONArray.getJSONObject(i).getInteger("depth"));
				break;
			default:
				break;
			}
			layer1.format(i > 0 ? instance.layers.get(i - 1) : null);
			instance.layers.add(layer1);
		}

		return instance;
	}

	public void train() {
		do {
			this.trainingCount++;
			forward();
			backward();
		} while (!canFinish());
	}

	private void forward() {
		for (int i = 1; i < layers.size(); i++) {
			layers.get(i).exec(layers.get(i - 1).data);
		}
	}

	private void backward() {
		layers.get(layers.size() - 1).calcError();
		for (int i = layers.size() - 2; i >= 1; i--) {
			layers.get(i).calcError(layers.get(i + 1));
		}
	}

	private boolean canFinish() {
		return (this.trainingCount >= this.maxTrainingCount);
	}

	public Double[][][] predict(JSONObject data) {
		loadData(data);
		forward();
		return getLastLayer().data;
	}

	public void save() {
		// TODO
	}

	public void load() {
		// TODO
	}

	/*
		{
		  "data": [
		    [
		      [1,2,3],
		      [1,2,3],
		      [1,2,3]
		    ],
		    [
		      [1,2,3],
		      [1,2,3],
		      [1,2,3]
		    ],
		    [
		      [1,2,3],
		      [1,2,3],
		      [1,2,3]
		    ]
		  ],
		  "target": [0,0,0,1,0]
		}
	 */
	public void loadData(JSONObject param) {
		this.get1stLayer().setData(param.getJSONArray("data"));
		this.getLastLayer().setTarget(param.getJSONArray("target"));
	}

	private InputLayer get1stLayer() {
		if (this.layers == null) return null;
		return (InputLayer)this.layers.get(0);
	}

	private FCLayer getLastLayer() {
		if (this.layers == null) return null;
		return (FCLayer)this.layers.get(this.layers.size() - 1);
	}
}
