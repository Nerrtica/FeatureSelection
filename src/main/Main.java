package main;

import wrapper.Wrapper;
import filter.*;

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
			Data.readFeature("/Users/nerrtica/Documents/Study/3rdYear/PE/Data/scene/feature.csv", false);
			Data.readLabel("/Users/nerrtica/Documents/Study/3rdYear/PE/Data/scene/label.csv");
		} catch (Exception e) {
			
		}
		Data.setBestFeature();
		
		//AvgChi.play();
		//LPChi.play();
		//AMI.play();
		PMU.play();
		
		Wrapper wrap = new Wrapper();
		wrap.play();
	}

}
