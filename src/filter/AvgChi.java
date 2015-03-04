package filter;

import main.*;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author nerrtica
 * @since 2015/01/29
 */

public class AvgChi {
	private static double[][] chiSquare;
	private static int[][] chiTable;
	private static double[][] avgChisq;
	private static double max, min;
	private static int[] bestFeature;
	public static ArrayList<String> result = new ArrayList<String>();

	public static void play() {
		chiSquare = new double[Data.featureNum][Data.labelNum];
		avgChisq = new double[Data.featureNum][2];
		bestFeature = new int[Data.featureNum];
		
		for (int i = 0; i < Data.featureNum; i++) {
			double interval = 0;
			Data.setInterval(i);
			if (!Data.isDiscrete) { interval = calculInterval(i); }
			
			for (int j = 0; j < Data.labelNum; j++) {
				chiTable = new int[Data.intervalNum][2];
				if (Data.isDiscrete) { makeChiTable(i, j); }
				else { makeChiTable(i, j, interval); }
				chiSquare[i][j] = calculChisq(2);
			}
		}
		calculAvgChisq();
		Sort.quickSort(avgChisq, 0, Data.featureNum - 1);
		for (int i = 0; i < Data.featureNum; i++) {
			bestFeature[i] = (int)avgChisq[i][1];
		}
		Data.setBestFeature(bestFeature);
		printResult();
	}
	
	private static double calculInterval (int index) {
		min = Double.MAX_VALUE;
		max = Double.MIN_VALUE;
		
		for (int i = 0; i < Data.dataNum; i++) {
			if (Data.feature[i][index] < min) { min = Data.feature[i][index]; }
			if (Data.feature[i][index] > max) { max = Data.feature[i][index]; }
		}
		
		return (max - min) / Data.intervalNum;
	}
	
	private static void makeChiTable (int featureNum, int labelNum) {
		for (int i = 0; i < Data.intervalNum; i++) {
			for (int j = 0; j < 2; j++) {
				chiTable[i][j] = 0;
			}
		}
		double[] featureTemp = new double[Data.dataNum];
		for (int i = 0; i < Data.dataNum; i++) {
			featureTemp[i] = Data.feature[i][featureNum];
		}
		Arrays.sort(featureTemp);
		
		int index = 0;
		double previous = featureTemp[0];
		for (int i = 0; i < Data.dataNum; i++) {
			if (featureTemp[i] != previous) {
				index++;
			}
			if (Data.label[i][labelNum]) {
				chiTable[index][1]++;
			} else {
				chiTable[index][0]++;
			}
		}
	}
	
	private static void makeChiTable (int featureNum, int labelNum, double interval) {
		for (int i = 0; i < Data.intervalNum; i++) {
			for (int j = 0; j < 2; j++) {
				chiTable[i][j] = 0;
			}
		}
		//distribute data to proper table location
		for (int i = 0; i < Data.dataNum; i++) {
			int dataFeatureLoca = (int)((Data.feature[i][featureNum] - min) / interval);
			if (dataFeatureLoca == Data.intervalNum) { dataFeatureLoca--; }
			if (Data.label[i][labelNum]) {
				chiTable[dataFeatureLoca][1]++;
			} else {
				chiTable[dataFeatureLoca][0]++;
			}
		}
	}
	
	private static double calculChisq (int labelUnit) {
		double a, r, c, n, e, result = 0;
		/**
		 * a : no. patterns in the ith interval(feature), jth class(label)
	     * r : no. patterns in the ith interval
	     * c : no. patterns in the jth class
	     * n : total no. patterns
	     * e : expected frequency of a == r * c / n
	     */
		n = Data.dataNum;
		
		for (int i = 0; i < Data.intervalNum; i++) {
			r = 0;
			for (int j = 0; j < labelUnit; j++) {
				r += chiTable[i][j];
			}
			for (int j = 0; j < labelUnit; j++) {
				a = chiTable[i][j];
				c = 0;
				for (int k = 0; k < Data.intervalNum; k++) {
					c += chiTable[k][j];
				}
				e = r * c / n;
				
				if (r == 0 || c == 0) { e = 0.1; }
				
				result += Math.pow(a - e, 2) / e;
			}
		}
		return result;
	}
	
	private static void calculAvgChisq() {
		for (int i = 0; i < Data.featureNum; i++) {
			avgChisq[i][0] = 0;
			avgChisq[i][1] = i;
			for (int j = 0; j < Data.labelNum; j++) {
				avgChisq[i][0] += chiSquare[i][j];
			}
			avgChisq[i][0] /= Data.labelNum;
		}
	}
	
	private static void printResult () {
		for (int i = 0; i < Data.featureNum; i++) {
			result.add(String.format("%3d순위 : %3d번째 feature, AvgChi : %.6f\n", i + 1, bestFeature[i] + 1, avgChisq[i][0]));
			System.out.printf("%3d순위 : %3d번째 feature, AvgChi : %.6f\n", i + 1, bestFeature[i] + 1, avgChisq[i][0]);
		}
	}
}
