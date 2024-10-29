import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.util.ArrayList;
import java.util.List;

public class QuickSortParalelo implements SortAlgorithm {
    private final ExecutorService threadPool;

    public QuickSortParalelo(int numThreads) {
        this.threadPool = Executors.newFixedThreadPool(numThreads);
    }

    @Override
    public void sort(int[] array) {
        if (array == null || array.length <= 1) {
            return;
        }
        try {
            quickSort(array, 0, array.length - 1);
        } finally {
            threadPool.shutdown();
        }
    }

    private void quickSort(int[] array, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(array, low, high);

            List<Future<Void>> futures = new ArrayList<>();

            futures.add(threadPool.submit(new SortTask(array, low, pivotIndex - 1)));
            futures.add(threadPool.submit(new SortTask(array, pivotIndex + 1, high)));

            for (Future<Void> future : futures) {
                try {
                    future.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private int partition(int[] array, int low, int high) {
        int pivot = array[high];
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            if (array[j] < pivot) {
                i++;
                int temp = array[i];
                array[i] = array[j];
                array[j] = temp;
            }
        }
        int temp = array[i + 1];
        array[i + 1] = array[high];
        array[high] = temp;
        return i + 1;
    }

    private class SortTask implements Callable<Void> {
        private final int[] array;
        private final int low;
        private final int high;

        public SortTask(int[] array, int low, int high) {
            this.array = array;
            this.low = low;
            this.high = high;
        }

        @Override
        public Void call() {
            quickSort(array, low, high);
            return null;
        }
    }
}
