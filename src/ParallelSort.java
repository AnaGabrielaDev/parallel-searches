import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParallelSort {
  private final SortAlgorithm algorithm;

  public ParallelSort(SortAlgorithm algorithm) {
    this.algorithm = algorithm;
  }

  public void parallelSort(int[] array, int numThreads) {
    ExecutorService executor = Executors.newFixedThreadPool(numThreads);

    int chunkSize = array.length / numThreads;
    for (int i = 0; i < numThreads; i++) {
      final int start = i * chunkSize;
      final int end = (i == numThreads - 1) ? array.length : start + chunkSize;
      executor.submit(() -> algorithm.sort(sliceArray(array, start, end)));
    }

    executor.shutdown();
  }

  private int[] sliceArray(int[] array, int start, int end) {
    int[] slice = new int[end - start];
    System.arraycopy(array, start, slice, 0, end - start);
    return slice;
  }
}
