package filter;

import java.util.ArrayList;
import java.util.Arrays;

import main.*;

/**
 * @author nerrtica
 * @since 2015/01/29
 */
public class LPChi {
	private static double[][] chiSquare;
	private static int[][] chiTable;
	private static boolean[][] alignedLabel;
	private static int[] labelIndex;
	private static int[] labelPowerset;
	private static double max, min;
	private static int[] bestFeature;
	public static ArrayList<String> result = new ArrayList<String>();
	
	public static void play () {
		chiSquare = new double[Data.featureNum][2];
		alignedLabel = new boolean[Data.dataNum][Data.labelNum];
		labelIndex = new int[Data.dataNum];
		labelPowerset = new int[Data.dataNum];
		bestFeature = new int[Data.featureNum];
		for (int i = 0; i < Data.dataNum; i++) {
			for (int j = 0; j < Data.labelNum; j++) {
				alignedLabel[i][j] = Data.label[i][j];
			}
			labelIndex[i] = i;
		}
		
		alignLabel(0, Data.dataNum - 1, 0);
		int count = makeLabelPowerset();
		
		for (int i = 0; i < Data.featureNum; i++) {
			double interval = 0;
			Data.setInterval(i);
			if (!Data.isDiscrete) { interval = calculInterval(i); }
			
			chiTable = new int[Data.intervalNum][count];
			if (Data.isDiscrete) { makeChiTable(i, count); }
			else { makeChiTable(i, count, interval); }
			chiSquare[i][0] = calculChisq(count);
			chiSquare[i][1] = i;
		}
		
		Sort.quickSort(chiSquare, 0, Data.featureNum - 1);
		for (int i = 0; i < Data.featureNum; i++) {
			bestFeature[i] = (int)chiSquare[i][1];
		}
		Data.setBestFeature(bestFeature);
		printResult();
	}
	
	private static void alignLabel (int start, int end, int index) {
		if (index >= Data.labelNum) { return; }
		if (start == end) { return; }
		
		int numofOne = 0;
		for (int i = start; i <= (end - numofOne); i++) {
	        if (alignedLabel[i][index]) {
	            int temp = labelIndex[i];
	            for (int j = i; j < end; j++) {
	                for (int k = 0; k < Data.labelNum; k++) {
	                	alignedLabel[j][k] = alignedLabel[j + 1][k];
	                }
	                labelIndex[j] = labelIndex[j + 1];
	            }
	            alignedLabel[end][index] = true;
	            labelIndex[end] = temp;
	            numofOne++;
	            i--;
	        }
	    }
	    if (numofOne != end - start + 1) {
	        alignLabel (start, end - numofOne, index + 1);
	    }
	    if (numofOne != 0) {
	        alignLabel(end - numofOne + 1, end, index + 1);
	    }
	}
	
	private static int makeLabelPowerset () {
		int count = 1, border = 0;
		labelPowerset[labelIndex[0]] = count - 1;
		for (int i = 1, j; i < Data.dataNum; i++) {
			for (j = 0; j < Data.labelNum; j++) {
				if (alignedLabel[i][j] != alignedLabel[border][j]) { break; }
			}
			if (j != Data.labelNum) {
				border = i;
				count++;
			}
			labelPowerset[labelIndex[i]] = count - 1;
		}
		return count;
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
	
	private static void makeChiTable (int featureNum, int labelCount) {
		for (int i = 0; i < Data.intervalNum; i++) {
			for (int j = 0; j < labelCount; j++) {
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
			chiTable[index][labelPowerset[labelIndex[i]]]++;
		}
	}
	
	private static void makeChiTable (int featureNum, int labelCount, double interval) {
		for (int i = 0; i < Data.intervalNum; i++) {
			for (int j = 0; j < labelCount; j++) {
				chiTable[i][j] = 0;
			}
		}
		//distribute data to proper table location
		for (int i = 0; i < Data.dataNum; i++) {
			int dataFeatureLoca = (int)((Data.feature[i][featureNum] - min) / interval);
			if (dataFeatureLoca == Data.intervalNum) { dataFeatureLoca--; }
			chiTable[dataFeatureLoca][labelPowerset[labelIndex[i]]]++;
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
	
	private static void printResult () {
		for (int i = 0; i < Data.featureNum; i++) {
			result.add(String.format("%3d순위 : %3d번째 feature, LPChi : %.6f\n", i + 1, bestFeature[i] + 1, chiSquare[i][0]));
			System.out.printf("%3d순위 : %3d번째 feature, LPChi : %.6f\n", i + 1, bestFeature[i] + 1, chiSquare[i][0]);
		}
	}
}
