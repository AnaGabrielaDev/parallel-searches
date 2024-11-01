import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.ArrayList;
import java.util.List;

public class MergeSortParalelo implements SortAlgorithm {
    private final ExecutorService threadPool;
    private final int threshold;

    public MergeSortParalelo(int numThreads) {
        this.threadPool = Executors.newFixedThreadPool(numThreads);
        this.threshold = 1000;
    }

    public MergeSortParalelo(int numThreads, int threshold) {
        this.threadPool = Executors.newFixedThreadPool(numThreads);
        this.threshold = threshold;
    }

    @Override
    public void sort(int[] array) {
        if (array == null || array.length <= 1) {
            return;
        }
        try {
            mergeSort(array);
        } finally {
            threadPool.shutdown();
        }
    }

    private void mergeSort(int[] array) {
        if (array.length <= threshold) {
            sequentialMergeSort(array);
            return;
        }

        int mid = array.length / 2;

        int[] left = Arrays.copyOfRange(array, 0, mid);
        int[] right = Arrays.copyOfRange(array, mid, array.length);

        List<Future<Void>> futures = new ArrayList<>();

        futures.add(threadPool.submit(() -> {
            sequentialMergeSort(left);
            return null;
        }));

        futures.add(threadPool.submit(() -> {
            sequentialMergeSort(right);
            return null;
        }));

        for (Future<Void> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        merge(array, left, right);
    }

    private void sequentialMergeSort(int[] array) {
        if (array.length <= 1) {
            return;
        }

        int mid = array.length / 2;

        int[] left = Arrays.copyOfRange(array, 0, mid);
        int[] right = Arrays.copyOfRange(array, mid, array.length);

        sequentialMergeSort(left);
        sequentialMergeSort(right);
        merge(array, left, right);
    }

    private void merge(int[] array, int[] left, int[] right) {
        int i = 0, j = 0, k = 0;
        while (i < left.length && j < right.length) {
            if (left[i] <= right[j]) {
                array[k++] = left[i++];
            } else {
                array[k++] = right[j++];
            }
        }
        while (i < left.length) {
            array[k++] = left[i++];
        }
        while (j < right.length) {
            array[k++] = right[j++];
        }
    }
}
