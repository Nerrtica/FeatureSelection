package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Random;

/**
 * @author nerrtica
 * @since 2015/01/27
 */
public class Data {
	public static double[][] feature;
	public static boolean[][] label;
	public static int featureNum, labelNum, dataNum, intervalNum;
	public static String featureFileName, labelFileName;
	public static String featureFileRoute, labelFileRoute;
	public static boolean isDiscrete;
	public static int[] bestFeature;
	
	private static int featureDataNum = -1, labelDataNum = -1;
	
	//set best feature in numeral order
	public static void setBestFeature () {
		bestFeature = new int[featureNum];
		for (int i = 0; i < featureNum; i++) {
			bestFeature[i] = i;
		}
	}
	
	//set best feature cloning featureList parameter
	public static void setBestFeature (int[] featureList) {
		bestFeature = new int[featureNum];
		bestFeature = featureList.clone();
	}
	
	//initialize setting about feature
	public static void setFeatureDefault () {
		featureNum = -1;
		featureFileName = null;
		featureFileRoute = null;
	}
	
	//read feature data file
	public static void readFeature (String featureRoute, boolean isDiscrete) throws Exception {
		BufferedReader bfreader = new BufferedReader(new FileReader(featureRoute));
		int index = 0;
		
		while (true) {
			String temp = bfreader.readLine();
			if (temp == null) {
				if (labelDataNum != -1 && labelDataNum != index) {
					bfreader.close();
					throw new Exception("Feature 파일과 Label 파일의 데이터 개수가 다릅니다.");
				}
				featureDataNum = index;
				dataNum = index;
				index = 0;
				break;
			}
			index++;
		}
		bfreader.close();
		
		featureFileRoute = featureRoute;
		String[] nameTemp = featureRoute.split("/");
		featureFileName = nameTemp[nameTemp.length - 1];
		Data.isDiscrete = isDiscrete;
		bfreader = new BufferedReader(new FileReader(featureRoute));
		while (true) {
			String temp = bfreader.readLine();
			if (temp == null) { break; }
			if (index == 0) {
				String[] data = temp.split(",");
				featureNum = data.length;
				feature = new double[dataNum][featureNum];
			}
			int length = splitFeature (temp, index++);
			if (length != featureNum) {
				bfreader.close();
				setFeatureDefault();
				throw new Exception("Feature 파일 내 feature의 개수가 일정하지 않습니다.");
			}
		}
		bfreader.close();
	}
	
	private static int splitFeature (String line, int index) {
		String[] data = line.split(",");
		for (int i = 0; i < data.length; i++) {
			feature[index][i] = Double.parseDouble(data[i]);
		}
		return data.length;
	}
	
	//initialize setting about feature
	public static void setLabelDefault () {
		labelNum = -1;
		labelFileName = null;
		labelFileRoute = null;
	}
	
	//read feature data file
	public static void readLabel (String labelRoute) throws Exception {
		BufferedReader bfreader = new BufferedReader(new FileReader(labelRoute));
		int index = 0;
		
		while (true) {
			String temp = bfreader.readLine();
			if (temp == null) {
				if (featureDataNum != -1 && featureDataNum != index) {
					bfreader.close();
					throw new Exception("Feature 파일과 Label 파일의 데이터 개수가 다릅니다.");
				}
				labelDataNum = index;
				dataNum = index;
				index = 0;
				break;
			}
			index++;
		}
		bfreader.close();
		
		labelFileRoute = labelRoute;
		String[] nameTemp = labelRoute.split("/");
		labelFileName = nameTemp[nameTemp.length - 1];
		bfreader = new BufferedReader(new FileReader(labelRoute));
		while (true) {
			String temp = bfreader.readLine();
			if (temp == null) { break; }
			if (index == 0) {
				String[] data = temp.split(",");
				labelNum = data.length;
				label = new boolean[dataNum][labelNum];
			}
			int length = splitLabel (temp, index++);
			if (length != labelNum) {
				bfreader.close();
				setLabelDefault();
				throw new Exception("Feature 파일 내 feature의 개수가 일정하지 않습니다.");
			}
		}
		bfreader.close();
	}
	
	private static int splitLabel (String line, int index) {
		String[] data = line.split(",");
		for (int i = 0; i < data.length; i++) {
			if (Integer.parseInt(data[i]) == 1) { label[index][i] = true; }
			else { label[index][i] = false; }
		}
		return data.length;
	}
	
	public static void randomizeData () {
		Random rand = new Random();
		
		featureNum = rand.nextInt(100) + 100;
		labelNum = rand.nextInt(100) + 2;
		dataNum = rand.nextInt(500) + 500;
		featureDataNum = dataNum;
		labelDataNum = dataNum;
		featureFileName = "randomFeatureFile.csv";
		labelFileName = "randomLabelFile.csv";
		featureFileRoute = "/randomFeatureFile.csv";
		labelFileRoute = "/randomLabelFile.csv";
		feature = new double[dataNum][featureNum];
		label = new boolean[dataNum][labelNum];
		isDiscrete = false;
		
		for (int i = 0; i < dataNum; i++) {
			for (int j = 0; j < featureNum; j++) {
				feature[i][j] = rand.nextDouble() * 10;
				int temp = rand.nextInt(2);
				if (temp == 0) {
					feature[i][j] = -feature[i][j];
				}
			}
			for (int j = 0; j < labelNum; j++) {
				int temp = rand.nextInt(5);
				if (temp == 0) {
					label[i][j] = true;
				} else {
					label[i][j] = false;
				}
			}
		}
	}
	
	public static void setInterval (int featureIndex) {
		if (!isDiscrete) {
			intervalNum = 3;
			return;
		}
		
		double[] featureTemp = new double[dataNum];
		for (int i = 0; i < dataNum; i++) {
			featureTemp[i] = feature[i][featureIndex];
		}
		Arrays.sort(featureTemp);
		
		int index = 0;
		double previous = featureTemp[0];
		for (int i = 0; i < dataNum; i++) {
			if (featureTemp[i] != previous) {
				index++;
			}
		}
		intervalNum = index + 1;
	}
}
