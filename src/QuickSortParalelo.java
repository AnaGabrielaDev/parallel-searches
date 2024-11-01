import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ForkJoinPool;

public class QuickSortParalelo implements SortAlgorithm {
    private final ForkJoinPool forkJoinPool;

    public QuickSortParalelo(int numThreads) {
        this.forkJoinPool = new ForkJoinPool(numThreads);
    }

    @Override
    public void sort(int[] array) {
        if (array == null || array.length <= 1) {
            return;
        }
        forkJoinPool.invoke(new QuickSortTask(array, 0, array.length - 1));
    }

    private static class QuickSortTask extends RecursiveAction {
        private final int[] array;
        private final int low;
        private final int high;

        public QuickSortTask(int[] array, int low, int high) {
            this.array = array;
            this.low = low;
            this.high = high;
        }

        @Override
        protected void compute() {
            if (low < high) {
                int pivotIndex = partition(array, low, high);

                QuickSortTask leftTask = new QuickSortTask(array, low, pivotIndex - 1);
                QuickSortTask rightTask = new QuickSortTask(array, pivotIndex + 1, high);

                invokeAll(leftTask, rightTask);
            }
        }

        private int partition(int[] array, int low, int high) {
            int pivot = array[high];
            int i = low - 1;
            for (int j = low; j < high; j++) {
                if (array[j] < pivot) {
                    i++;
                    // Swap
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
    }
}
