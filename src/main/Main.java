package main;

import filter.AvgChi;
import filter.LPChi;

/**
 * @author nerrtica
 * @since 2015/01/26
 * @version 2.0
 */
public class Main {

	public static void main(String[] args) {
		//GUI gui = new GUI();
		//gui.mainMenu();
		
		try {
			Data.readFeature("/Users/nerrtica/Documents/Study/3rdYear/PE/Data/CAL500/feature.csv", false);
			Data.readLabel("/Users/nerrtica/Documents/Study/3rdYear/PE/Data/CAL500/label.csv");
		} catch (Exception e) {
			
		}
		Data.setBestFeature();
		
		AvgChi.play();
		LPChi.play();
	}

}
