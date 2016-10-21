/**
* Test.java
* Copyright 2015 standardai Co.ltd.
*/
package cn.standardai.app.vchess;

/**
 * 测试Chess程序
 * @author 韩晴
 *
 */
public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			test2AI();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 测试
	 * @throws Exception
	 */
	public static void test2AI() throws Exception {
		// 建立棋手
		Player p1 = new Player("p1", 0.001);
		Player p2 = new Player("p2", 0.002);
		Game game = new Game(19, p1, p2);
		game.run();
	}
}
