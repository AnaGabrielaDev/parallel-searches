import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;

public class SortExecutor {
  private final SortAlgorithm algorithm;

  public SortExecutor(SortAlgorithm algorithm) {
    this.algorithm = algorithm;
  }

  public void executeSerial(int[] array) {
    algorithm.sort(array);
  }

  public void executeParallel(int[] array, int numThreads) throws InterruptedException {
    ExecutorService executor = Executors.newFixedThreadPool(numThreads);
    int chunkSize = array.length / numThreads;
    int[][] subArrays = new int[numThreads][];

    try {
      for (int i = 0; i < numThreads; i++) {
        final int start = i * chunkSize;
        final int end = (i == numThreads - 1) ? array.length : start + chunkSize;
        subArrays[i] = Arrays.copyOfRange(array, start, end);

        final int index = i;
        executor.submit(() -> {
          algorithm.sort(subArrays[index]);  // Cada thread ordena sua parte
        });
      }
    } finally {
      executor.shutdown();
      executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);  // Aguarda todas as threads
    }

    mergeSubArrays(array, subArrays);
  }


  private void mergeSubArrays(int[] array, int[][] subArrays) {
    int pos = 0;
    for (int[] subArray : subArrays) {
      System.arraycopy(subArray, 0, array, pos, subArray.length);
      pos += subArray.length;
    }

    Arrays.sort(array);
  }
}
