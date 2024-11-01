import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ForkJoinPool;

public class MergeSortParalelo implements SortAlgorithm {
    private final ForkJoinPool forkJoinPool;

    public MergeSortParalelo(int numThreads) {
        this.forkJoinPool = new ForkJoinPool(numThreads);
    }

    @Override
    public void sort(int[] array) {
        if (array == null || array.length <= 1) {
            return;
        }
        forkJoinPool.invoke(new MergeSortTask(array, 0, array.length - 1));
    }

    private static class MergeSortTask extends RecursiveAction {
        private final int[] array;
        private final int low;
        private final int high;

        public MergeSortTask(int[] array, int low, int high) {
            this.array = array;
            this.low = low;
            this.high = high;
        }

        @Override
        protected void compute() {
            if (low < high) {
                int mid = (low + high) / 2;

                MergeSortTask leftTask = new MergeSortTask(array, low, mid);
                MergeSortTask rightTask = new MergeSortTask(array, mid + 1, high);

                invokeAll(leftTask, rightTask);

                merge(array, low, mid, high);
            }
        }

        private void merge(int[] array, int low, int mid, int high) {
            int n1 = mid - low + 1;
            int n2 = high - mid;

            int[] leftArray = new int[n1];
            int[] rightArray = new int[n2];

            System.arraycopy(array, low, leftArray, 0, n1);
            System.arraycopy(array, mid + 1, rightArray, 0, n2);

            int i = 0, j = 0;
            int k = low;

            while (i < n1 && j < n2) {
                if (leftArray[i] <= rightArray[j]) {
                    array[k] = leftArray[i];
                    i++;
                } else {
                    array[k] = rightArray[j];
                    j++;
                }
                k++;
            }

            while (i < n1) {
                array[k] = leftArray[i];
                i++;
                k++;
            }

            while (j < n2) {
                array[k] = rightArray[j];
                j++;
                k++;
            }
        }
    }
}
