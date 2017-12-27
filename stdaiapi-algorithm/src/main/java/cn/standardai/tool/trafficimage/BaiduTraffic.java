/**
* Literal.java
* Copyright 2017 standardai Co.ltd.
*/
package cn.standardai.tool.trafficimage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import cn.standardai.lib.base.matrix.MatrixUtil;
import cn.standardai.tool.ImageUtil;

public class BaiduTraffic {

	public enum Status {

		畅通("1"), 缓行("2"), 拥挤("3"), 严重拥堵("4");

		String s;

		private Status(String s) {
			this.s = s;
		}

		private static final Map<Status, String> mappings = new HashMap<Status, String>(4);

		static {
			for (Status status : values()) {
				mappings.put(status, status.s);
			}
		}

		public String toString() {
			return mappings.get(this);
		}
	};

	private static String path = "/Users/athvillar/Documents/baidutraffic/hezetrafficimage2/";

	private static String testPath = "/Users/athvillar/Documents/baidutraffic/test/";

	private static String outputFile = "/Users/athvillar/Documents/baidutraffic/output";

	private static String suffix = ".png";

	//private static String[] dateString = {"2017-08-04","2017-08-05","2017-08-06","2017-08-07","2017-08-08"};
	private static String[] dateString = {"2017-08-06","2017-08-07","2017-08-08"};
	//private static String[] dateString = {"2017-08-07"};

	private static int fileCount = 2;

	private static AnkorPair[] roads = new AnkorPair[] {
			new AnkorPair(new Ankor("1", 94, 213), new Ankor("2", 78, 292), 0),
			new AnkorPair(new Ankor("2", 74, 313), new Ankor("3", 56, 398), 0),
			new AnkorPair(new Ankor("3", 14, 510), new Ankor("4", 12, 594), 0),
			new AnkorPair(new Ankor("4", 71, 167), new Ankor("5", 62, 270), 1),
			new AnkorPair(new Ankor("5", 62, 270), new Ankor("6", 56, 372), 1),
			new AnkorPair(new Ankor("6", 56, 372), new Ankor("7", 46, 548), 1),
			new AnkorPair(new Ankor("7", 46, 548), new Ankor("8", 39, 669), 1),
			new AnkorPair(new Ankor("8", 46, 679), new Ankor("9", 307, 700), 1),
			new AnkorPair(new Ankor("9", 318, 701), new Ankor("10", 431, 705), 1),
			new AnkorPair(new Ankor("10", 431, 705), new Ankor("11", 803, 715), 1),
			new AnkorPair(new Ankor("11", 820, 696), new Ankor("12", 885, 446), 1),
			new AnkorPair(new Ankor("12", 897, 367), new Ankor("13", 898, 275), 1),
			new AnkorPair(new Ankor("13", 845, 637), new Ankor("14", 853, 490), 0),
			new AnkorPair(new Ankor("14", 888, 328), new Ankor("15", 754, 291), 0),
			new AnkorPair(new Ankor("15", 712, 275), new Ankor("16", 506, 238), 0),
			new AnkorPair(new Ankor("16", 489, 235), new Ankor("1", 133, 177), 0),
			new AnkorPair(new Ankor("1", 133, 183), new Ankor("16", 494, 245), 0),
			new AnkorPair(new Ankor("16", 494, 245), new Ankor("15", 681, 278), 0),
			new AnkorPair(new Ankor("15", 748, 290), new Ankor("14", 892, 330), 0),
			new AnkorPair(new Ankor("14", 844, 478), new Ankor("13", 838, 641), 0),
			new AnkorPair(new Ankor("13", 892, 271), new Ankor("12", 891, 367), 1),
			new AnkorPair(new Ankor("12", 878, 445), new Ankor("11", 812, 696), 1),
			new AnkorPair(new Ankor("11", 801, 709), new Ankor("10", 437, 699), 1),
			new AnkorPair(new Ankor("10", 437, 699), new Ankor("9", 312, 694), 1),
			new AnkorPair(new Ankor("9", 312, 694), new Ankor("8", 44, 672), 1),
			new AnkorPair(new Ankor("8", 44, 672), new Ankor("7", 51, 574), 1),
			new AnkorPair(new Ankor("7", 51, 574), new Ankor("6", 62, 374), 1),
			new AnkorPair(new Ankor("6", 62, 374), new Ankor("5", 68, 276), 1),
			new AnkorPair(new Ankor("5", 68, 276), new Ankor("4", 76, 166), 1),
			new AnkorPair(new Ankor("4", 20, 596), new Ankor("3", 20, 512), 0),
			new AnkorPair(new Ankor("3", 63, 402), new Ankor("2", 81, 312), 0),
			new AnkorPair(new Ankor("2", 81, 312), new Ankor("1", 104, 206), 0),
	};

	private static String fixTime = "2017-08-07 08:56";

	private static int fixPixel0X = 0;

	private static int fixPixel0Y = -3;

	private static int fixPixel1X = 1;

	private static int fixPixel1Y = -3;

	private static BufferedWriter bufWrite;

	public static void main(String[] args) {

		//drawAnkor();
		try {
			FileOutputStream out = new FileOutputStream(outputFile);
			OutputStreamWriter outWriter = new OutputStreamWriter(out, "UTF-8");
			bufWrite = new BufferedWriter(outWriter);
			readTraffic();
			bufWrite.close();
			outWriter.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void drawAnkor() {

		Integer[][][][] rgb = new Integer[fileCount][][][];

		for (int i = 0; i < dateString.length; i++) {
			for (int j = 0; j <= 23; j++) {
				String currentHour = j < 10 ? "0" + j : "" + j;
				for (int k = 0; k < 60; k++) {
					String currentMinute = k < 10 ? "0" + k : "" + k;
					for (int l = 0; l < 60; l++) {
						String currentSecond = l < 10 ? "0" + l : "" + l;

						for (int m = 0; m < fileCount; m++) {
							String fileName = path + dateString + " " + currentHour + ":" + currentMinute + ":" + currentSecond + "_" + m + suffix;
							File file = new File(fileName);
							if (!file.exists()) {
								// System.out.println("缺少" + fileName);
								continue;
							}
							try {
								rgb[i] = ImageUtil.getRGB(fileName);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						/*
						for (int i = 0; i < roads.length; i++) {
							for (int j = 0; j < 3; j++) {
								rgb[roads[i].index][roads[i].from.x][roads[i].from.y][j] = 0;
								rgb[roads[i].index][roads[i].to.x][roads[i].to.y][j] = 0;
							}
						}
						try {
							ImageUtil.drawRGB(path + "0.png", rgb[0]);
							ImageUtil.drawRGB(path + "1.png", rgb[1]);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						*/
					}
				}
			}
		}
	}

	private static void readTraffic() {

		// print title
		String title = "time,";
		for (int i = 0; i < roads.length; i++) {
			title += roads[i].from.name + "-" + roads[i].to.name;
			if (i != roads.length - 1) {
				title += ",";
			}
		}
		print(title);

		for (int i = 0; i < dateString.length; i++) {
			for (int j = 0; j <= 23; j++) {
				//for (int j = 8; j <= 8; j++) {
				//for (int k = 57; k < 58; k++) {
				for (int k = 0; k < 60; k++) {
					boolean needFix = false;
					String timeString = dateString[i] + " " + j + ":" + k;
					if (timeString.compareTo(fixTime) >= 0) needFix = true;
					readTraffic1(path, dateString[i], j, k, needFix);
				}
			}
		}
	}

	private static void readTraffic1(String path, String date, int hour, int minute, boolean needFix) {

		String hString = (hour < 10 ? "0" + hour : "" + hour);
		String mString = (minute < 10 ? "0" + minute : "" + minute);
		try {
			Integer[][][][] rgb = new Integer[fileCount][][][];
			for (int i = 0; i < fileCount; i++) {
				for (int j = 0; j < 60; j++) {
					String sString = (j < 10 ? "0" + j : "" + j);
					String timeString = date + " " + hString + ":" + mString + ":" + sString;
					String fileName = path + timeString + "_" + i + suffix;
					//print(fileName);
					File file = new File(fileName);
					if (!file.exists()) {
						// System.out.println("缺少" + fileName);
						continue;
					}
					rgb[i] = ImageUtil.getRGB(fileName);
					break;
				}
			}

			if (rgb == null || rgb.length != 2 || rgb[0] == null || rgb[1] == null) return;

			// print contents
			String roadStatus = date + " " + hString + ":" + mString + ",";
			for (int i = 0; i < roads.length; i++) {
				Status status = getStatus(rgb[roads[i].index], roads[i].from, roads[i].to, i, needFix, roads[i].index);
				if (status == null) {
					roadStatus += 0;
				} else {
					roadStatus += status.toString();
				}
				if (i != roads.length - 1) {
					roadStatus += ",";
				}
			}
			print(roadStatus);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Status getStatus(Integer[][][] rgb, Ankor from, Ankor to, int index, boolean needFix, int imageIdx) {

		Integer[][] points = getPoints(from, to, needFix, imageIdx);
		Map<Status, Integer> pointStatus = new HashMap<Status, Integer>();

		//if (index == 16) {
		//	int i = 1;
		//}
		//Integer[][][] newRGB = MatrixUtil.create(rgb.length, rgb[0].length, 3, 0);
		for (int i = 0; i < points.length; i++) {
			//if (i == points.length - 9) {
			//	int sss = 9;
			//}
			//for (int k = 0; k < 3; k++) {
			//	newRGB[points[i][0]][points[i][1]][k] = rgb[points[i][0]][points[i][1]][k];
			//}
			Status status = getStatus(rgb[points[i][0]][points[i][1]]);
			if (status == null) continue;
			if (pointStatus.containsKey(status)) {
				pointStatus.put(status, pointStatus.get(status) + 1);
			} else {
				pointStatus.put(status, 1);
			}
		}
		//try {
		//	ImageUtil.drawRGB(testPath + "road" + index + ".png", newRGB);
		//} catch (IOException e) {
		//	// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}

		int maxCount = Integer.MIN_VALUE;
		Status maxStatus = null;
		for (Entry<Status, Integer> entry : pointStatus.entrySet()) {
			if (entry.getValue() > maxCount) {
				maxCount = entry.getValue();
				maxStatus = entry.getKey();
			}
		}

		//if (maxStatus == null) {
		//	return null;
		//}
		return maxStatus;
	}

	private static Integer[][] getPoints(Ankor from, Ankor to, boolean needFix, int imageIdx) {
		int fx, fy, tx, ty;
		if (needFix) {
			if (imageIdx == 0) {
				fx = from.x + fixPixel0X;
				fy = from.y + fixPixel0Y;
				tx = to.x + fixPixel0X;
				ty = to.y + fixPixel0Y;
			} else {
				fx = from.x + fixPixel1X;
				fy = from.y + fixPixel1Y;
				tx = to.x + fixPixel1X;
				ty = to.y + fixPixel1Y;
			}
		} else {
			fx = from.x;
			fy = from.y;
			tx = to.x;
			ty = to.y;
		}
		int distance = Math.abs(fx - tx) + Math.abs(fy - ty);
		int count = (int)Math.round(Math.log(distance));
		//int count = distance / 2;
		Integer[][] points = new Integer[count * 9][];
		float unitX = (0.0F + tx - fx) / (count + 1);
		float unitY = (0.0F + ty - fy) / (count + 1);
		for (int i = 0; i < count; i++) {
			points[i * 9] = new Integer[2];
			points[i * 9][0] = fx + Math.round(unitX * (i + 1));
			points[i * 9][1] = fy + Math.round(unitY * (i + 1));
			points[i * 9 + 1] = new Integer[2];
			points[i * 9 + 1][0] = points[i * 9][0] + 2;
			points[i * 9 + 1][1] = points[i * 9][1];
			points[i * 9 + 2] = new Integer[2];
			points[i * 9 + 2][0] = points[i * 9][0] + 2;
			points[i * 9 + 2][1] = points[i * 9][1] + 2;
			points[i * 9 + 3] = new Integer[2];
			points[i * 9 + 3][0] = points[i * 9][0];
			points[i * 9 + 3][1] = points[i * 9][1] + 2;
			points[i * 9 + 4] = new Integer[2];
			points[i * 9 + 4][0] = points[i * 9][0] - 2;
			points[i * 9 + 4][1] = points[i * 9][1] + 2;
			points[i * 9 + 5] = new Integer[2];
			points[i * 9 + 5][0] = points[i * 9][0] - 2;
			points[i * 9 + 5][1] = points[i * 9][1];
			points[i * 9 + 6] = new Integer[2];
			points[i * 9 + 6][0] = points[i * 9][0] - 2;
			points[i * 9 + 6][1] = points[i * 9][1] - 2;
			points[i * 9 + 7] = new Integer[2];
			points[i * 9 + 7][0] = points[i * 9][0];
			points[i * 9 + 7][1] = points[i * 9][1] - 2;
			points[i * 9 + 8] = new Integer[2];
			points[i * 9 + 8][0] = points[i * 9][0] + 2;
			points[i * 9 + 8][1] = points[i * 9][1] - 2;
		}
		return points;
	}

	private static Status getStatus(Integer[] rgb) {
		if (rgb == null || rgb.length != 3) return null;
		/*
		if (rgb[0] >= 139 && rgb[0] <= 235 && rgb[1] <= 48 && rgb[2] <= 48) {
			return Status.严重拥堵;
		} else if (rgb[0] >= 194 && rgb[1] <= 96 && rgb[2] <= 96) {
			return Status.拥挤;
		} else if (rgb[0] >= 207 && rgb[1] >= 111 && rgb[1] <= 207 && rgb[2] <= 73) {
			return Status.缓行;
		} else if (rgb[0] <= 71 && rgb[1] >= 143 && rgb[1] <= 239 && rgb[2] <= 48) {
			return Status.畅通;
		} else {
			return null;
		}
		*/
		int th1 = 50;
		int th2 = 80;
		if (rgb[0] - rgb[1] > th1 && rgb[0] - rgb[2] > th1 && Math.abs(rgb[1] - rgb[2]) < th1) {
			double gray = rgb[0] * 0.299 + rgb[1] * 0.587 + rgb[2] * 0.114;
			if (gray >= 192) {
				return Status.严重拥堵;
			} else {
				return Status.拥挤;
			}
		} else if (rgb[0] - rgb[1] > th1 && rgb[1] - rgb[2] > th2) {
			return Status.缓行;
		} else if (rgb[1] - rgb[0] > th1 && rgb[1] - rgb[2] > th1 && Math.abs(rgb[0] - rgb[2]) < th1) {
			return Status.畅通;
		} else {
			return null;
		}
	}

	private static void print(String s) {
		try {
			bufWrite.write(s);
			bufWrite.newLine();
			System.out.println(s);
			bufWrite.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
