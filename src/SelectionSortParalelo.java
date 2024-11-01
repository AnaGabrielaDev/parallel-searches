import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ForkJoinPool;

public class SelectionSortParalelo implements SortAlgorithm {
    private final ForkJoinPool forkJoinPool;

    public SelectionSortParalelo(int numThreads) {
        this.forkJoinPool = new ForkJoinPool(numThreads);
    }

    @Override
    public void sort(int[] array) {
        if (array == null || array.length <= 1) {
            return;
        }
        forkJoinPool.invoke(new SelectionSortTask(array, 0, array.length - 1));
    }

    private static class SelectionSortTask extends RecursiveAction {
        private final int[] array;
        private final int low;
        private final int high;
        private static final int THRESHOLD = 100;

        public SelectionSortTask(int[] array, int low, int high) {
            this.array = array;
            this.low = low;
            this.high = high;
        }

        @Override
        protected void compute() {
            if (high - low < THRESHOLD) {
                selectionSort(array, low, high);
            } else {
                int mid = (low + high) / 2;
                SelectionSortTask leftTask = new SelectionSortTask(array, low, mid);
                SelectionSortTask rightTask = new SelectionSortTask(array, mid + 1, high);
                invokeAll(leftTask, rightTask);
                merge(array, low, mid, high);
            }
        }

        private void selectionSort(int[] array, int low, int high) {
            for (int i = low; i <= high; i++) {
                int minIndex = i;
                for (int j = i + 1; j <= high; j++) {
                    if (array[j] < array[minIndex]) {
                        minIndex = j;
                    }
                }
                int temp = array[minIndex];
                array[minIndex] = array[i];
                array[i] = temp;
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
