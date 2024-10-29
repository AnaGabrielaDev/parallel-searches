import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.util.ArrayList;
import java.util.List;

public class BubbleSortParalelo implements SortAlgorithm {
    private final ExecutorService threadPool;

    public BubbleSortParalelo(int numThreads) {
        this.threadPool = Executors.newFixedThreadPool(numThreads);
    }

    @Override
    public void sort(int[] array) {
        if (array == null || array.length <= 1) {
            return;
        }
        try {
            bubbleSort(array);
        } finally {
            threadPool.shutdown();
        }
    }

    private void bubbleSort(int[] array) {
        int n = array.length;
        boolean swapped;
        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            List<Future<Void>> futures = new ArrayList<>();

            for (int j = 0; j < n - i - 1; j++) {
                final int index = j;
                futures.add(threadPool.submit(() -> {
                    if (array[index] > array[index + 1]) {
                        swap(array, index, index + 1);
                    }
                    return null;
                }));
            }

            for (Future<Void> future : futures) {
                try {
                    future.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for (int j = 0; j < n - i - 1; j++) {
                if (array[j] > array[j + 1]) {
                    swapped = true;
                }
            }

            if (!swapped) break;
        }
    }

    private void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
