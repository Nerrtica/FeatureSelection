package filter;

import java.util.ArrayList;

import main.*;

/**
 * @author nerrtica
 * @since 2015/02/09
 */
public class PMU {
	private static double[][] featureTable;
	private static double[] singleFeatureEntropy;
	private static double[] singleLabelEntropy;
	private static double[][] featureLabelEntropy;
	private static double[][] doubleFeatureEntropy;
	private static double[][] doubleLabelEntropy;
	private static double[] J;
	private static boolean[] pickedFeature;
	private static int[] bestFeature;
	private static double min, max;
	public static ArrayList<String> result = new ArrayList<String>();
	
	public static void play () {
		featureTable = new double[Data.featureNum][Data.dataNum];
		singleFeatureEntropy = new double[Data.featureNum];
		singleLabelEntropy = new double[Data.labelNum];
		featureLabelEntropy = new double[Data.featureNum][Data.labelNum];
		doubleFeatureEntropy = new double[Data.featureNum][Data.featureNum];
		doubleLabelEntropy = new double[Data.labelNum][Data.labelNum];
		bestFeature = new int[Data.featureNum];
		J = new double[Data.featureNum];
		pickedFeature = new boolean[Data.featureNum];
		for (int i = 0; i < Data.featureNum; i++) {
			if (Data.isDiscrete) { makeFeatureTable(featureTable[i], i); }
			else {
				double interval = 0;
				Data.setInterval(i);
				if (!Data.isDiscrete) { interval = calculInterval(i); }
				makeFeatureTable(featureTable[i], i, interval);
			}
		}
		makeSingleEntropy();
		makeDoubleEntropy();
		makeInitialJ();
		
		for (int i = 0; i < Data.featureNum; i++) {
			int bestFeatureIndex = pickBestFeature();
			updateJ(bestFeatureIndex);
			bestFeature[i] = bestFeatureIndex;
		}
		Data.setBestFeature(bestFeature);
		printResult();
	}
	
	private static double calculInterval (int index) {
		min = Double.POSITIVE_INFINITY;
		max = Double.NEGATIVE_INFINITY;
		
		for (int i = 0; i < Data.dataNum; i++) {
			if (Data.feature[i][index] < min) { min = Data.feature[i][index]; }
			if (Data.feature[i][index] > max) { max = Data.feature[i][index]; }
		}
		
		return (max - min) / Data.intervalNum;
	}
	
	private static void makeFeatureTable (double[] table, int featureNum) {
		for (int i = 0; i < Data.dataNum; i++) {
			table[i] = Data.feature[i][featureNum];
		}
	}
	
	private static void makeFeatureTable (double[] table, int featureNum, double interval) {
		for (int i = 0; i < Data.dataNum; i++) {
			int value = (int)((Data.feature[i][featureNum] - min) / interval);
			if (value == Data.intervalNum) { value--; }
			table[i] = value;
		}
	}
	
	private static void makeSingleEntropy () {
		for (int i = 0; i < Data.featureNum; i++) {
			singleFeatureEntropy[i] = calculEntropy(featureTable[i]);
		}
		for (int i = 0; i < Data.labelNum; i++) {
			singleLabelEntropy[i] = calculEntropy(i);
		}
	}
	
	private static void makeDoubleEntropy () {
		for (int i = 0; i < Data.featureNum; i++) {
			for (int j = i + 1; j < Data.featureNum; j++) {
				doubleFeatureEntropy[i][j] = calculEntropy(featureTable[i], featureTable[j]);
				doubleFeatureEntropy[j][i] = doubleFeatureEntropy[i][j];
			}
			for (int j = 0; j < Data.labelNum; j++) {
				featureLabelEntropy[i][j] = calculEntropy(featureTable[i], j);
			}
		}
		for (int i = 0; i < Data.labelNum; i++) {
			for (int j = i + 1; j < Data.labelNum; j++) {
				doubleLabelEntropy[i][j] = calculEntropy(i, j);
				doubleLabelEntropy[j][i] = doubleLabelEntropy[i][j];
			}
		}
	}
	
	private static double calculEntropy (double[] feature) {
		double[] data = new double[Data.dataNum];
		data = feature.clone();
		Sort.quickSort(data, 0, data.length - 1);
		
		double value = data[0];
		int count = 0;
		double result = 0;
		for (int i = 0; i < Data.dataNum; i++) {
			if (data[i] != value || i == Data.dataNum - 1) {
				if (i == Data.dataNum - 1) { count++; }
				double p = (double)count / (double)Data.dataNum;
				result += p * Math.log(p);
				value = data[i];
				count = 0;
			}
			count++;
		}
		return result * -1;
	}
	
	private static double calculEntropy (int labelNum) {
		int numof0 = 0, numof1 = 0;
		
		for (int i = 0; i < Data.dataNum; i++) {
			if (Data.label[i][labelNum]) { numof1++; }
			else { numof0++; }
		}
		
		double p0 = (double)numof0 / (double)Data.dataNum;
		double p1 = (double)numof1 / (double)Data.dataNum;
		
		double result = 0;
		if (p0 != 0) { result += p0 * Math.log(p0); }
		if (p1 != 0) { result += p1 * Math.log(p1); }
		
		return result * -1;
	}
	
	private static double calculEntropy (double[] feature, int labelNum) {
		double[][] data = new double[Data.dataNum][2];
		for (int i = 0; i < Data.dataNum; i++) {
			data[i][0] = feature[i];
			data[i][1] = i;
		}
		Sort.quickSort(data, 0, Data.dataNum - 1);
		
		double value = data[0][0];
		int numof0 = 0, numof1 = 0;
		double result = 0;
		for (int i = 0; i < Data.dataNum; i++) {
			if (data[i][0] != value || i == Data.dataNum - 1) {
				if (i == Data.dataNum - 1) {
					if (Data.label[(int)data[i][1]][labelNum]) { numof1++; }
					else { numof0++; }
				}
				double p = (double)numof0 / (double)Data.dataNum;
				if (p != 0) { result += p * Math.log(p); }
				p = (double)numof1 / (double)Data.dataNum;
				if (p != 0) { result += p * Math.log(p); }
				value = data[i][0];
				numof0 = 0;
				numof1 = 0;
			}
			if (Data.label[(int)data[i][1]][labelNum]) { numof1++; }
			else { numof0++; }
		}
		return result * -1;
	}
	
	private static double calculEntropy (double[] feature1, double[] feature2) {
		double[][] data = new double[Data.dataNum][3];
		for (int i = 0; i < Data.dataNum; i++) {
			data[i][0] = feature1[i];
			data[i][1] = feature2[i];
		}
		Sort.quickSort(data, 0, Data.dataNum - 1, true);
		
		double value1 = data[0][0], value2 = data[0][1];
		int count = 0;
		double result = 0;
		for (int i = 0; i < Data.dataNum; i++) {
			if (data[i][0] != value1 || data[i][1] != value2 || i == Data.dataNum - 1) {
				if (i == Data.dataNum - 1) { count++; }
				double p = (double)count / (double)Data.dataNum;
				result += p * Math.log(p);
				value1 = data[i][0];
				value2 = data[i][1];
				count = 0;
			}
			count++;
		}
		return result * -1;
	}
	
	private static double calculEntropy (int labelNum1, int labelNum2) {
		double[] label = new double[Data.dataNum];
		for (int i = 0; i < Data.dataNum; i++) {
			label[i] = 0;
			if (Data.label[i][labelNum1]) { label[i] += 10; }
			if (Data.label[i][labelNum2]) { label[i] += 1; }
		}
		
		Sort.quickSort(label, 0, Data.dataNum - 1);
		
		double value = label[0];
		int count = 0;
		double result = 0;
		for (int i = 0; i < Data.dataNum; i++) {
			if (label[i] != value || i == Data.dataNum - 1) {
				if (i == Data.dataNum - 1) { count++; }
				double p = (double)count / (double)Data.dataNum;
				result += p * Math.log(p);
				value = label[i];
				count = 0;
			}
			count++;
		}
		return result * -1;
	}
	
	private static double calculEntropy (double[] feature1, double[] feature2, int labelNum) {
		double[][] data = new double[Data.dataNum][3];
		for (int i = 0; i < Data.dataNum; i++) {
			data[i][0] = feature1[i];
			data[i][1] = feature2[i];
			data[i][2] = i;
		}
		Sort.quickSort(data, 0, Data.dataNum - 1, true);
		
		double value1 = data[0][0], value2 = data[0][1];
		int numof0 = 0, numof1 = 0;
		double result = 0;
		for (int i = 0; i < Data.dataNum; i++) {
			if (data[i][0] != value1 || data[i][1] != value2 || i == Data.dataNum - 1) {
				if (i == Data.dataNum - 1) { 
					if (Data.label[(int)data[i][2]][labelNum]) { numof1++; }
					else { numof0++; }
				}
				double p = (double)numof0 / (double)Data.dataNum;
				if (p != 0) { result += p * Math.log(p); }
				p = (double)numof1 / (double)Data.dataNum;
				if (p != 0) { result += p * Math.log(p); }
				value1 = data[i][0];
				value2 = data[i][1];
				numof0 = 0;
				numof1 = 0;
			}
			if (Data.label[(int)data[i][2]][labelNum]) { numof1++; }
			else { numof0++; }
		}
		return result * -1;
	}
	
	private static double calculEntropy (double[] feature, int labelNum1, int labelNum2) {
		double[][] data = new double[Data.dataNum][3];
		for (int i = 0; i < Data.dataNum; i++) {
			data[i][0] = feature[i];
			data[i][1] = 0;
			if (Data.label[i][labelNum1]) { data[i][1] += 10; }
			if (Data.label[i][labelNum2]) { data[i][1] += 1; }
		}
		Sort.quickSort(data, 0, Data.dataNum - 1, true);
		
		double value1 = data[0][0], value2 = data[0][1];
		int count = 0;
		double result = 0;
		for (int i = 0; i < Data.dataNum; i++) {
			if (data[i][0] != value1 || data[i][1] != value2 || i == Data.dataNum - 1) {
				if (i == Data.dataNum - 1) { count++; }
				double p = (double)count / (double)Data.dataNum;
				result += p * Math.log(p);
				value1 = data[i][0];
				value2 = data[i][1];
				count = 0;
			}
			count++;
		}
		return result * -1;
	}
	
	private static void makeInitialJ () {
		for (int i = 0; i < Data.featureNum; i++) {
			double result = 0;
			for (int j = 0; j < Data.labelNum; j++) {
				result += singleFeatureEntropy[i] + singleLabelEntropy[j] - featureLabelEntropy[i][j];
			}
			for (int j = 0; j < Data.labelNum; j++) {
				for (int k = 0; k < Data.labelNum; k++) {
					result -= singleFeatureEntropy[i] + singleLabelEntropy[j];
					if (j != k) { result -= singleLabelEntropy[k]; }
					result += featureLabelEntropy[i][j];
					if (j != k) {
						result += featureLabelEntropy[i][k] + doubleLabelEntropy[j][k];
						result -= calculEntropy(featureTable[i], j, k);
					}
				}
			}
			J[i] = result;
			pickedFeature[i] = false;
		}
	}
	
	private static void updateJ (int bestFeature) {
		for (int i = 0; i < Data.featureNum; i++) {
			if (pickedFeature[i]) { continue; }
			for (int j = 0; j < Data.labelNum; j++) {
				J[i] -= singleFeatureEntropy[i] + singleFeatureEntropy[bestFeature] + singleLabelEntropy[j];
				J[i] += doubleFeatureEntropy[i][bestFeature] + featureLabelEntropy[i][j] + featureLabelEntropy[bestFeature][j];
				J[i] -= calculEntropy(featureTable[i], featureTable[bestFeature], j);
			}
		}
	}
	
	private static int pickBestFeature () {
		double max = Double.NEGATIVE_INFINITY;
		int bestFeature = 0;
		
		for (int i = 0; i < Data.featureNum; i++) {
			if (pickedFeature[i]) { continue; }
			if (J[i] > max) {
				max = J[i];
				bestFeature = i;
			}
		}
		pickedFeature[bestFeature] = true;
		
		return bestFeature;
	}
	
	private static void printResult () {
		for (int i = 0; i < Data.featureNum; i++) {
			result.add(String.format("%3d순위 : %3d번째 feature\n", i + 1, bestFeature[i] + 1));
			System.out.printf("%3d순위 : %3d번째 feature\n", i + 1, bestFeature[i] + 1);
		}
	}
}
