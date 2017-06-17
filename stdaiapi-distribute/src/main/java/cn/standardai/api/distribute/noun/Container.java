package cn.standardai.api.distribute.noun;

public abstract class Container<T1, T2> {

	public abstract T2 get(T1 k);

	public abstract void put(T1 k, T2 v);

	protected abstract T2 caluculate(T1 v);
}
