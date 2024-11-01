import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ForkJoinPool;

public class BubbleSortParalelo implements SortAlgorithm {
    private final ForkJoinPool forkJoinPool;

    public BubbleSortParalelo(int numThreads) {
        this.forkJoinPool = new ForkJoinPool(numThreads);
    }

    @Override
    public void sort(int[] array) {
        if (array == null || array.length <= 1) {
            return;
        }
        forkJoinPool.invoke(new BubbleSortTask(array, 0, array.length - 1));
    }

    private static class BubbleSortTask extends RecursiveAction {
        private final int[] array;
        private final int low;
        private final int high;
        private static final int THRESHOLD = 100;

        public BubbleSortTask(int[] array, int low, int high) {
            this.array = array;
            this.low = low;
            this.high = high;
        }

        @Override
        protected void compute() {
            if (high - low < THRESHOLD) {
                bubbleSort(array, low, high);
            } else {
                int mid = (low + high) / 2;
                BubbleSortTask leftTask = new BubbleSortTask(array, low, mid);
                BubbleSortTask rightTask = new BubbleSortTask(array, mid + 1, high);
                invokeAll(leftTask, rightTask);
                merge(array, low, mid, high);
            }
        }

        private void bubbleSort(int[] array, int low, int high) {
            for (int i = low; i <= high; i++) {
                boolean swapped = false;
                for (int j = low; j < high - i + low; j++) {
                    if (array[j] > array[j + 1]) {
                        int temp = array[j];
                        array[j] = array[j + 1];
                        array[j + 1] = temp;
                        swapped = true;
                    }
                }
                if (!swapped) {
                    break;
                }
            }
        }

        private void merge(int[] array, int low, int mid, int high) {
            int[] temp = new int[high - low + 1];
            int i = low, j = mid + 1, k = 0;

            while (i <= mid && j <= high) {
                if (array[i] <= array[j]) {
                    temp[k++] = array[i++];
                } else {
                    temp[k++] = array[j++];
                }
            }

            while (i <= mid) {
                temp[k++] = array[i++];
            }

            while (j <= high) {
                temp[k++] = array[j++];
            }

            System.arraycopy(temp, 0, array, low, temp.length);
        }
    }
}
