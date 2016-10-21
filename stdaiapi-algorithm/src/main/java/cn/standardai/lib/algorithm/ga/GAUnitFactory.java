/**
* GAUnitFactory.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.ga;

import java.lang.reflect.Constructor;  
import java.lang.reflect.InvocationTargetException;

/**
 * 个体工厂
 * @author 韩晴
 *
 */
public class GAUnitFactory {

	public static GAUnit getInstance(Class<?> cls, Object... agrs) {
		Constructor<?>[] constructors = cls.getConstructors();
		Object instance = null;
		for (Constructor<?> cons : constructors) {
			Class<?>[] clses = cons.getParameterTypes();
			if (clses.length > 0) {
				boolean isThisConstructor = true;
				for (int i = 0; i < clses.length; i++) {
					Class<?> c = clses[i]; 
					if (!c.isInstance(agrs[i])) {
						isThisConstructor = false;
						break;
					}
				}
				if (isThisConstructor) {
					try {
						instance = cons.newInstance(agrs);
						break;
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					continue;
				}
			}
		}
		return (GAUnit)instance;
	}
}
