package main;

/**
 * @author nerrtica
 * @since 2015/01/28
 */
public class Sort {
	public static void quickSort (double[][] arr, int left, int right) {
		if (left == right) { return; }
		double pivotValue = arr[left][0];
		int pivot = (int)arr[left][1], lHold = left, rHold = right;
		
		while (left < right) {
			while ((arr[right][0] <= pivotValue) && (left < right)) { right--; }
			if (left != right) {
				arr[left][0] = arr[right][0];
				arr[left][1] = arr[right][1];
				left++;
			}
			while ((arr[left][0] >= pivotValue) && (left < right)) { left++; }
			if (left != right) {
				arr[right][0] = arr[left][0];
				arr[right][1] = arr[left][1];
				right--;
			}
		}
		arr[left][0] = pivotValue;
		arr[left][1] = pivot;
		pivot = left;
		left = lHold;
		right = rHold;
		if (left < pivot) { quickSort(arr, left, pivot - 1); }
		if (right > pivot) { quickSort(arr, pivot + 1, right); }
	}
	
	public static void quickSort (double[][] arr, int left, int right, boolean isFirst) {
		if (isFirst) {
			if (left == right) { return; }
			double pivotValue = arr[left][0];
			int pivot = (int)arr[left][1], temp = (int)arr[left][2], lHold = left, rHold = right;
			
			while (left < right) {
				while ((arr[right][0] <= pivotValue) && (left < right)) { right--; }
				if (left != right) {
					arr[left][0] = arr[right][0];
					arr[left][1] = arr[right][1];
					arr[left][2] = arr[right][2];
					left++;
				}
				while ((arr[left][0] >= pivotValue) && (left < right)) { left++; }
				if (left != right) {
					arr[right][0] = arr[left][0];
					arr[right][1] = arr[left][1];
					arr[right][2] = arr[left][2];
					right--;
				}
			}
			arr[left][0] = pivotValue;
			arr[left][1] = pivot;
			arr[left][2] = temp;
			pivot = left;
			left = lHold;
			right = rHold;
			if (left < pivot) { quickSort(arr, left, pivot - 1); }
			if (right > pivot) { quickSort(arr, pivot + 1, right); }
			
			double value = arr[left][0];
			int start = left, end;
			for (int i = left; i <= right; i++) {
				if (value != arr[i][0] || i == right) {
					if (i == right) { end = i; }
					else {
						end = i - 1;
						value = arr[i][0];
					}
					quickSort(arr, start, end, false);
					start = i;
				}
			}
		} else {
			if (left == right) { return; }
			double pivotValue = arr[left][1], temp = arr[left][0];
			int pivot = (int)arr[left][2], lHold = left, rHold = right;
			
			while (left < right) {
				while ((arr[right][1] <= pivotValue) && (left < right)) { right--; }
				if (left != right) {
					arr[left][0] = arr[right][0];
					arr[left][1] = arr[right][1];
					arr[left][2] = arr[right][2];
					left++;
				}
				while ((arr[left][1] >= pivotValue) && (left < right)) { left++; }
				if (left != right) {
					arr[right][0] = arr[left][0];
					arr[right][1] = arr[left][1];
					arr[right][2] = arr[left][2];
					right--;
				}
			}
			arr[left][1] = pivotValue;
			arr[left][0] = temp;
			arr[left][2] = pivot;
			pivot = left;
			left = lHold;
			right = rHold;
			if (left < pivot) { quickSort(arr, left, pivot - 1, false); }
			if (right > pivot) { quickSort(arr, pivot + 1, right, false); }
		}
	}
	
	public static void quickSort (double[] arr, int left, int right) {
		if (left == right) { return; }
		double pivotValue = arr[left];
		int pivot, lHold = left, rHold = right;
		
		while (left < right) {
			while ((arr[right] <= pivotValue) && (left < right)) { right--; }
			if (left != right) {
				arr[left] = arr[right];
				left++;
			}
			while ((arr[left] >= pivotValue) && (left < right)) { left++; }
			if (left != right) {
				arr[right] = arr[left];
				right--;
			}
		}
		arr[left] = pivotValue;
		pivot = left;
		left = lHold;
		right = rHold;
		if (left < pivot) { quickSort(arr, left, pivot - 1); }
		if (right > pivot) { quickSort(arr, pivot + 1, right); }
	}
	/*
	public static void alignBestFeature (int src, int dest) {
		if (src > dest) {
	        int temp1 = Data.bestFeature[dest];
	        Data.bestFeature[dest] = Data.bestFeature[src];
	        for (int i = dest + 1; i <= src; i++) {
	            int temp2 = Data.bestFeature[i];
	            Data.bestFeature[i] = temp1;
	            temp1 = temp2;
	        }
	    } else {
	        int temp1 = Data.bestFeature[dest];
	        Data.bestFeature[dest] = Data.bestFeature[src];
	        for (int i = dest - 1; i >= src; i--) {
	            int temp2 = Data.bestFeature[i];
	            Data.bestFeature[i] = temp1;
	            temp1 = temp2;
	        }
	    }
	}*/
}
