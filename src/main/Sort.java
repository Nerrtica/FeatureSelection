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
}
