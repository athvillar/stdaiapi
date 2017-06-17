package cn.standardai.api.math.bean;

import cn.standardai.api.math.exception.OperationException;

public abstract class QuestionParam<T extends QuestionParam<?>> implements Operatable<T> {

	public void operate(T p2, int operator) throws OperationException {
		switch (operator) {
		case 1:
			this.plus(p2);
			break;
		case 2:
			this.minus(p2);
			break;
		case 3:
			this.multiply(p2);
			break;
		case 4:
			this.devide(p2);
			break;
		default:
			break;
		}
	}

	public abstract boolean negative();
}
