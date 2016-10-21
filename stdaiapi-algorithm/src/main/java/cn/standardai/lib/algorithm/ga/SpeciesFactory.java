/**
* SpeciesFactory.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.algorithm.ga;

import java.io.PrintStream;
import java.lang.reflect.Constructor;  
import java.lang.reflect.InvocationTargetException;

/**
 * 种群工厂
 * @author 韩晴
 *
 */
public class SpeciesFactory {

	public static Object getInstance(String className){
		Object instance = null;
		try {
			Class<?> cls = Class.forName(className);
			instance = cls.newInstance();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return instance;
	}

	public static Object getInstance(String className, Object... agrs) {
		Class<?> cls = null;
		try {
			cls = Class.forName(className);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		return instance;
	}

	public static Object getInstance(
			int basePopulation,
			Class<?> unitClass,
			Integer geneLen,
			double mutateRate,
			int maxGenerationNum,
			Selector.ChooseMethod chooseMethod,
			Generator.GeneratorType generatorType,
			Generator.CrossRule crossRule,
			int parentsNum,
			PrintStream ps) {

		GAUnit[] units = new GAUnit[basePopulation];
		for (int i = 0; i < units.length; i++) {
			units[i] = GAUnitFactory.getInstance(unitClass, geneLen);
			units[i].setPs(ps);
		}

		// 选择器
		Selector selector = null;
		switch (chooseMethod) {
		case ROULETTE:
			selector = new RouletteSelector();
			break;
		default:
			selector = new RouletteSelector();
			break;
		}

		// 生成器
		Generator generator = null;
		switch (generatorType) {
		case CROSS_CODE:
			generator = new CrossCodeGenerator(parentsNum, crossRule, geneLen);
			break;
		default:
			generator = new CrossCodeGenerator(parentsNum, crossRule, geneLen);
			break;
		}
		Species instance = new Species(
				units,
				geneLen,
				mutateRate,
				maxGenerationNum,
				selector,
				generator);

		// 设置输出流
		instance.setPs(ps);

		return instance;
	}
}
