package wrapper;

import main.*;

import java.util.Vector;

/**
 * @author nerrtica
 * @since 2015/01/28
 */
public class Wrapper {
	public String[] result = new String[Data.featureNum];
	protected int index;
	protected Vector[] featureList = new Vector[Data.dataNum];

	public void play () {
		for (int i = 0; i < Data.featureNum; i++) {
			this.index = i;
			printResult();
		}
		for (int i = 0; i < Data.featureNum / 5; i++) {
			//result[i] = String.format("%2d순위 : %3d번째 feature\n", i + 1, Data.bestFeature[i] + 1);
		}
	}
	
	protected void printResult () {
		makeFeatureList();
		addFeatureList(index);
		//result[index] = String.format("%2d개의 feature 사용 - 정확도 : %.6f\n", index + 1, calculAccuracy());
		System.out.printf("%2d개의 feature 사용 - 정확도 : %.6f\n", index + 1, calculAccuracy());
	}
	
	protected void makeFeatureList () {
		for (int i = 0; i < Data.dataNum; i++) {
			featureList[i] = new Vector<Double>(index, 1);
		}
		for (int i = 0; i < index; i++) {
			addFeatureList(i);
		}
	}
	
	protected void addFeatureList (int addFeature) {
		for (int i = 0; i < Data.dataNum; i++) {
			featureList[i].addElement(Data.feature[i][Data.bestFeature[addFeature]]);
		}
	}
	
	protected void deleteFeatureList (int deleteFeature) {
		for (int i = 0; i < Data.dataNum; i++) {
			featureList[i].remove(deleteFeature);
		}
	}
	
	protected double calculAccuracy () {
		double avgAccuracy = 0;
		for (int i = 0; i < Data.dataNum; i++) {
			avgAccuracy += loocv(i);
		}
		avgAccuracy /= Data.dataNum;
		
		return avgAccuracy;
	}
	
	protected double loocv (int index) {
		double[][] distance = new double[Data.dataNum][2];
		
		for (int i = 0; i < Data.dataNum; i++) {
			distance[i][1] = i;
			if (i == index) {
				distance[i][0] = Double.MAX_VALUE;
				continue;
			}
			distance[i][0] = 0;
			for (int j = 0; j < featureList[0].size(); j++) {
				distance[i][0] += Math.pow(Double.parseDouble(featureList[i].elementAt(j).toString()) - Double.parseDouble(featureList[index].elementAt(j).toString()), 2);
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
	
	protected double multiLabelAccuracy (boolean[] labelGuess, int index) {
		int intersection = 0, unionset = 0;
		
		for (int i = 0; i < Data.labelNum; i++) {
			if (labelGuess[i] && Data.label[index][i]) { intersection++; }
			if (labelGuess[i] || Data.label[index][i]) { unionset++; }
		}
		if (unionset == 0) { return 0; }
		return (double)intersection / (double)unionset;
	}
	
	protected double hammingLoss (boolean[] labelGuess, int index) {
		int error = 0;
		
		for (int i = 0; i < Data.labelNum; i++) {
			if (labelGuess[i] != Data.label[index][i]) { error++; }
		}
		return (double)error / (double)Data.labelNum;
	}
}
