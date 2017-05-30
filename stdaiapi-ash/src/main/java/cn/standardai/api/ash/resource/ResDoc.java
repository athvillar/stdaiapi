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
						+ "\t{\n"
						+ "\t  \"layerSize\":[3,4],\n"
						+ "\t  \"inputSize\":100,\n"
						+ "\t  \"outputSize\":100,\n"
						+ "\t  \"delay\": true\n"
						+ "\t}\n"
						+ "\n参数说明\n"
						+ "\tlayerSize\t\t\t各层深度\n"
						+ "\tinputSize\t\t\t输入向量维度\n"
						+ "\toutputSize\t\t输出向量维度\n"
						+ "\tdelay\t\t\t是否延迟输出"
		},
		new String[] {
				"CNN", "卷积神经网络（Convolution Neural Network）通过卷积核的卷积操作，在不严重影响识别效果的前提下，"
						+ "大大减少网络中的变量个数，从而提高了训练效率。\n"
						+ "在应用中，CNN被广泛地用于图像识别工作，其可以处理的维度不限于2维，通常的CNN可以处理3维数据，"
						+ "对于图像识别来说，除了长和宽，还可以处理深度数据，即RGB三个通道。\n\n"
						+ "CNN模型结构\n"
						+ "\t{\n"
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
						+ "\tCONV\t\t\t卷积层\n"
						+ "\tFC\t\t\t\t输出层"
		},
		new String[] {
				"tutorial", "为了让每个人都可以快速地生成自己的深度学习模型，深度学习平台Athvillar为用户提供通用的深度学习模型全生命周期管理服务。"
						+ "平台由网页版、ash和SDK组成。三者中的任何一个都包含全部功能，现在您看到的ash，即命令行界面。\n"
						+ "\nash是Athvillar shell的缩写，是模仿linux命令行的平台操作系统，ash命令有两大类，一类是通用命令，"
						+ "通常命令格式为\"命令名 [参数]\"，具体命令格式可以通过\"man 命令名\"查看。另一类是资源命令，资源命令的主要"
						+ "目的是操作某一类资源，所以命令格式通常为\"命令名 [资源类别] [参数]\"，可以通过\"man 命令名\"查看使用说明。"
						+ "平台的资源包括数据(data)、模型(model)、文档(dic)等，可以通过help命令查看详细。查看某一资源的详细说明，"
						+ "请输入\"help 资源类别\"\n"
						+ "\n命令输入框前的提示符显示了当前在哪类资源下，例如\"$model>\"代表当前在模型目录下，输入资源命令时，可以省略资源类别"
						+ "，默认操作当前资源，例如如果在model目录下，输入\"ls\"(省略资源类别model)，即可查看model下的资源列表。\n"
						+ "\n使用平台训练自己的神经网络，一般流程是登陆(login)或注册(mk user)，上传数据(mk data)，建立模型(mk model)，"
						+ "训练模型(call model -t)，使用模型(call model -p)，之后可以按照提示进行操作。\n"
						+ "\n作为v0.1版本，平台仍然有许多问题，欢迎使用msg命令与管理员交流。\n"
						+ "\n更多文档，请输入\"ls doc\"或\"cat doc 文档名\"查看\n"
		},
		new String[] {
				"contact", "欢迎使用msg命令与管理员交流，或者通过\n"
						+ "\t微信\t\tipulsplus\n"
						+ "\t邮箱\t\tathvillar@hotmail.com\n"
						+ "\t电话\t\t18910532582\n"
						+ "与管理员联系。"
		}
	};

	@Override
	public String help() {
		return "doc，文档资源，Athvillar平台相关文档。\n"
				+ "包括系统介绍性文档，专题文档，ash使用文档，sdk文档等资源。"
				+ "与文档资源相关的命令包括cat, ls。";
	}
}
