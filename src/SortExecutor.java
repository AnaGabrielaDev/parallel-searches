import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SortExecutor {
  private final SortAlgorithm algorithm;

  public SortExecutor(SortAlgorithm algorithm) {
    this.algorithm = algorithm;
  }

  // Execução Serial
  public void executeSerial(int[] array) {
    algorithm.sort(array);  // Chama o algoritmo diretamente
  }

  // Execução Paralela
  public void executeParallel(int[] array, int numThreads) throws InterruptedException {
    ExecutorService executor = Executors.newFixedThreadPool(numThreads);
    int chunkSize = array.length / numThreads;

    for (int i = 0; i < numThreads; i++) {
      final int start = i * chunkSize;
      final int end = (i == numThreads - 1) ? array.length : start + chunkSize;
      executor.submit(() -> {
        algorithm.sort(sliceArray(array, start, end));  // Cada thread ordena uma parte
      });
    }

    executor.shutdown();
    executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);  // Aguarda todas as threads
  }

  // Método auxiliar para dividir o array
  private int[] sliceArray(int[] array, int start, int end) {
    int[] slice = new int[end - start];
    System.arraycopy(array, start, slice, 0, end - start);
    return slice;
  }
}
