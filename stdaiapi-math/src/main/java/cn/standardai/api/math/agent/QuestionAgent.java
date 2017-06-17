package cn.standardai.api.math.agent;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.math.bean.DoubleParam;
import cn.standardai.api.math.bean.FractionParam;
import cn.standardai.api.math.bean.IntegerParam;
import cn.standardai.api.math.bean.QuestionParam;
import cn.standardai.api.math.exception.OperationException;

public class QuestionAgent {

	public JSONObject generate(int max, int min, int num, int chain, int level, int type, int round) {

		JSONArray questions = new JSONArray();
		for (int i = 0; i < num; i++) {
			JSONObject qa = new JSONObject();
			QuestionParam baseValue = newParam(max, min, type, round);
			String opString = baseValue.toString();
			int opLevel = 2;
			for (int j = 1; j < chain; j++) {
				int operator = getOperator(level);
				if (type == 1 && operator == 4) operator = 3;
				QuestionParam opValue = newParam(baseValue, operator, max, min, round, type);
				try {
					baseValue.operate(opValue, operator);
				} catch (OperationException e) {
					continue;
				}
				switch (operator) {
				case 1:
					opString += " + " + (opValue.negative() ? ("(" + opValue + ")") : opValue);
					opLevel = 1;
					break;
				case 2:
					opString += " - " + (opValue.negative() ? ("(" + opValue + ")") : opValue);
					opLevel = 1;
					break;
				case 3:
					if (opLevel == 1) {
						opString = "(" + opString + ") × " + (opValue.negative() ? ("(" + opValue + ")") : opValue);
					} else {
						opString += " × " + (opValue.negative() ? ("(" + opValue + ")") : opValue);
					}
					opLevel = 2;
					break;
				case 4:
					if (opLevel == 1) {
						opString = "(" + opString + ") ÷ " + (opValue.negative() ? ("(" + opValue + ")") : opValue);
					} else {
						opString += " ÷ " + (opValue.negative() ? ("(" + opValue + ")") : opValue);
					}
					opLevel = 2;
					break;
				}
			}
			opString += " ＝ ";
			qa.put("q", opString);
			qa.put("a", baseValue.toString());
			questions.add(qa);
		}

		JSONObject result = new JSONObject();
		result.put("qa", questions);
		return result;
	}

	private int getOperator(int level) {
		switch (level) {
		case 1:
			if (Math.random() > 0.5) {
				return 1;
			} else {
				return 2;
			}
		case 2:
			if (Math.random() > 0.5) {
				return 3;
			} else {
				return 4;
			}
		case 3:
			if (Math.random() > 0.75) {
				return 1;
			} else if (Math.random() > 0.5) {
				return 2;
			} else if (Math.random() > 0.25) {
				return 3;
			} else {
				return 4;
			}
		default:
			return 0;
		}
	}

	private QuestionParam newParam(int max, int min, int type, int round) {
		switch (type) {
		case 1:
			return new IntegerParam(max, min);
		case 2:
			return new DoubleParam(max, min, round);
		case 3:
			return new FractionParam(max, min);
		default:
			return null;
		}
	}

	private QuestionParam newParam(QuestionParam baseValue, int operator, int max, int min, int round, int type) {
		switch (type) {
		case 1:
			switch (operator) {
			case 1:
				return new IntegerParam(max - ((IntegerParam)baseValue).p, 0);
			case 2:
				return new IntegerParam(((IntegerParam)baseValue).p, 0);
			case 3:
			case 4:
			default:
				return new IntegerParam(max, min);
			}
		case 2:
			return new DoubleParam(max, min, round);
		case 3:
			return new FractionParam(max, min);
		default:
			return null;
		}
	}

	public void done() {
		// Nothing to do
	}
}
