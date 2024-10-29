import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.ArrayList;
import java.util.List;

public class SelectionSortParalelo implements SortAlgorithm {
    private final ExecutorService threadPool;

    public SelectionSortParalelo(int numThreads) {
        this.threadPool = Executors.newFixedThreadPool(numThreads);
    }

    @Override
    public void sort(int[] array) {
        if (array == null || array.length <= 1) {
            return;
        }
        try {
            selectionSort(array);
        } finally {
            threadPool.shutdown();
        }
    }

    private void selectionSort(int[] array) {
        int n = array.length;
        for (int i = 0; i < n - 1; i++) {
            final int currentMinIndex = i;
            List<Future<Integer>> futures = new ArrayList<>();

            for (int j = i + 1; j < n; j++) {
                final int index = j;
                futures.add(threadPool.submit(() -> {
                    if (array[index] < array[currentMinIndex]) {
                        return index;
                    } else {
                        return currentMinIndex;
                    }
                }));
            }

            int minIndex = currentMinIndex;
            for (Future<Integer> future : futures) {
                try {
                    int result = future.get();
                    if (array[result] < array[minIndex]) {
                        minIndex = result;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (minIndex != i) {
                swap(array, i, minIndex);
            }
        }
    }

    private void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
