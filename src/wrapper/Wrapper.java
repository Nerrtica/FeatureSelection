package wrapper;

import main.*;

import java.util.ArrayList;

/**
 * @author nerrtica
 * @since 2015/01/28
 */
public class Wrapper {
	private static ArrayList<Double>[] featureList;
	private static double[] accuracy;
	public static ArrayList<String> result = new ArrayList<String>();
	
	@SuppressWarnings("unchecked")
	public static void play () {
		featureList = (ArrayList<Double>[])new ArrayList[Data.dataNum];
		accuracy = new double[Data.featureNum];
		
		for (int i = 0; i < Data.featureNum; i++) {
			makeFeatureList(i);
			addFeature(i);
			accuracy[i] = calculAccuracy();
		}
		printResult();
	}
	
	private static void makeFeatureList (int index) {
		for (int i = 0; i < Data.dataNum; i++) {
			featureList[i] = new ArrayList<Double>();
		}
		for (int i = 0; i < index; i++) {
			addFeature(i);
		}
	}
	
	private static void addFeature (int addFeature) {
		for (int i = 0; i < Data.dataNum; i++) {
			featureList[i].add(Data.feature[i][Data.bestFeature[addFeature]]);
		}
	}
	
	private static double calculAccuracy () {
		double avgAccuracy = 0;
		for (int i = 0; i < Data.dataNum; i++) {
			avgAccuracy += loocv(i);
		}
		avgAccuracy /= Data.dataNum;
		
		return avgAccuracy;
	}
	
	private static double loocv (int index) {
		double[][] distance = new double[Data.dataNum][2];
		
		for (int i = 0; i < Data.dataNum; i++) {
			distance[i][1] = i;
			if (i == index) {
				distance[i][0] = Double.MAX_VALUE;
				continue;
			}
			distance[i][0] = 0;
			for (int j = 0; j < featureList[0].size(); j++) {
				distance[i][0] += Math.pow(featureList[i].get(j) - featureList[index].get(j), 2);
			}
			distance[i][0] = Math.sqrt(distance[i][0]);
		}
		Sort.quickSort(distance, 0, Data.dataNum - 1);

		int[] min = new int[Data.NEIGHBOR_NUM];
		boolean[] labelGuess = new boolean[Data.labelNum];
		
		for (int i = 0; i < Data.NEIGHBOR_NUM; i++) {
			min[i] = (int)distance[Data.dataNum - i - 1][1];
		}
		
		for (int i = 0; i < Data.labelNum; i++) {
			int numof0 = 0, numof1 = 0;
			for (int j = 0; j < Data.NEIGHBOR_NUM; j++) {
				if (!Data.label[min[j]][i]) { numof0++; }
				else { numof1++; }
			}
			if (numof0 > numof1) { labelGuess[i] = false; }
			else { labelGuess[i] = true; }
		}
		
		return multiLabelAccuracy(labelGuess, index);
		//return hammingLoss(labelGuess, index);
	}
	
	private static double multiLabelAccuracy (boolean[] labelGuess, int index) {
		int intersection = 0, unionset = 0;
		
		for (int i = 0; i < Data.labelNum; i++) {
			if (labelGuess[i] && Data.label[index][i]) { intersection++; }
			if (labelGuess[i] || Data.label[index][i]) { unionset++; }
		}
		if (unionset == 0) { return 0; }
		return (double)intersection / (double)unionset;
	}
	
	/*
	private static double hammingLoss (boolean[] labelGuess, int index) {
		int error = 0;
		
		for (int i = 0; i < Data.labelNum; i++) {
			if (labelGuess[i] != Data.label[index][i]) { error++; }
		}
		return (double)error / (double)Data.labelNum;
	}*/
	
	private static void printResult () {
		for (int i = 0; i < Data.featureNum; i++) {
			result.add(String.format("%3d개의 feature 사용 - 정확도 : %.6f", i + 1, accuracy[i]));
			System.out.printf("%3d개의 feature 사용 - 정확도 : %.6f\n", i + 1, accuracy[i]);
		}
	}
}