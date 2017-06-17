package cn.standardai.api.math.bean;

import cn.standardai.api.math.exception.OperationException;

public interface Operatable<T2 extends QuestionParam<?>> {

	public void plus(T2 p);

	public void minus(T2 p);

	public void multiply(T2 p);

	public void devide(T2 p) throws OperationException;
}
