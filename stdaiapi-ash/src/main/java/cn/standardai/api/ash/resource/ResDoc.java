package cn.standardai.api.ash.resource;

import cn.standardai.api.ash.base.AshResource;

public class ResDoc extends AshResource {

	public static final String[][] doc = new String[][] {
		new String[] {
				"LSTM", "循环神经网络（RNN）的一个变体，长短期记忆模型（Long Short Term Memory），"
						+ "可以有效解决传统RNN梯度随时间消失问题。\n"
						+ "与传统RNN类似，LSTM可以接收流输入，并延时输出，根据输入数据的不同，可以训练图像识别、"
						+ "翻译等模型。\n"
						+ "也可以将多个LSTM结合起来组成多层LSTM模型，用于解决更为复杂的问题。\n\n"
						+ "LSTM模型结构\n"
						+ "\t\"structure\": {\n"
						+ "\t  \"layerSize\":[3,4],\n"
						+ "\t  \"inputSize\":100,\n"
						+ "\t  \"outputSize\":100,\n"
						+ "\t  \"delay\": true\n"
						+ "\t}\n"
						+ "\n参数说明\n"
						+ "\tlayerSize\t\t各层深度\n"
						+ "\tinputSize\t\t输入向量维度\n"
						+ "\toutputSize\t\t输出向量维度\n"
						+ "\tdelay\t\t\t是否延迟输出"
		},
		new String[] {
				"CNN", "卷积神经网络（Convolution Neural Network）通过卷积核的卷积操作，在不严重影响识别效果的前提下，"
						+ "大大减少网络中的变量个数，从而提高了训练效率。\n"
						+ "在应用中，CNN被广泛地用于图像识别工作，其可以处理的维度不限于2维，通常的CNN可以处理3维数据，"
						+ "对于图像识别来说，除了长和宽，还可以处理深度数据，即RGB三个通道。\n\n"
						+ "CNN模型结构\n"
						+ "\t\"structure\": {\n"
						+ "\t  \"layers\" : [\n"
						+ "\t    {\"type\": \"INPUT\", \"width\": 100, \"height\": 100, \"depth\": 1 },\n"
						+ "\t    {\"type\": \"POOL\", \"method\": \"max\", \"spatial\": 2, \"stride\": 2},\n"
						+ "\t    {\"type\": \"CONV\", \"depth\": 8, \"stride\": 1, \"padding\":1, \"learningRate\": 1, \"aF\": \"sigmoid\",\n"
						+ "\t      \"filter\": {\"width\":3, \"height\":3}\n"
						+ "\t    },\n"
						+ "\t    {\"type\": \"POOL\", \"method\": \"max\", \"spatial\": 2, \"stride\": 2},\n"
	     				+ "\t    {\"type\": \"FC\", \"depth\": 15, \"learningRate\": 1, \"aF\": \"sigmoid\" }\n"
						+ "\t  ]\n"
						+ "\t}\n"
						+ "\n参数说明\n"
						+ "\tINPUT\t\t\t输入层\n"
						+ "\tPOOL\t\t\tPOOLING层\n"
						+ "\tCONV\t\t卷积层\n"
						+ "\tFC\t\t\t输出层"
		}
	};

	@Override
	public String help() {
		return "doc，文档资源，Athvillar平台相关文档。\n"
				+ "包括系统介绍性文档，专题文档，ash使用文档，sdk文档等资源。"
				+ "与文档资源相关的命令包括cat, ls。";
	}
}
