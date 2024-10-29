import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.util.ArrayList;
import java.util.List;

public class MergeSortParalelo implements SortAlgorithm {
    private final ExecutorService threadPool;

    public MergeSortParalelo(int numThreads) {
        this.threadPool = Executors.newFixedThreadPool(numThreads);
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
        if (array.length <= 1) {
            return;
        }
        int mid = array.length / 2;

        int[] left = Arrays.copyOfRange(array, 0, mid);
        int[] right = Arrays.copyOfRange(array, mid, array.length);

        List<Future<Void>> futures = new ArrayList<>();

        futures.add(threadPool.submit(new SortTask(left)));
        futures.add(threadPool.submit(new SortTask(right)));

        for (Future<Void> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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

    private class SortTask implements Callable<Void> {
        private final int[] array;

        public SortTask(int[] array) {
            this.array = array;
        }

        @Override
        public Void call() {
            mergeSort(array);
            return null;
        }
    }
}
