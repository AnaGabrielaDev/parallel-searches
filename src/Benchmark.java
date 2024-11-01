import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Benchmark {
  public static void main(String[] args) throws InterruptedException, IOException {
    int[] sizes = {100, 1000, 10000};
    int[] threads = {2, 4, 8, 10};
    String csvFile = "resultados_algoritmos.csv";

    try (FileWriter writer = new FileWriter(csvFile)) {
      writer.append("Algoritmo,Execução,Tamanho,Threads,Tempo(ms)\n");

      for (int size : sizes) {
        System.out.println("em " + size);
        int[] array = generateRandomArray(size);

        for (int algIndex = 0; algIndex < 3; algIndex++) {
          String algorithmName;
          SortExecutor executor;

          switch (algIndex) {
            case 0:
              algorithmName = "BubbleSort";
              executor = new SortExecutor(new BubbleSort());
              break;
            case 1:
              algorithmName = "QuickSort";
              executor = new SortExecutor(new QuickSort());
              break;
            case 2:
              algorithmName = "SelectionSort";
              executor = new SortExecutor(new SelectionSort());
              break;
            case 3:
              algorithmName = "MergeSort";
              executor = new SortExecutor(new MergeSort());
              break;
            default:
              throw new IllegalStateException("Unexpected value: " + algIndex);
          }

          // Execução Serial
          for (int i = 0; i < 5; i++) {
            int[] arrayCopy = array.clone();
            long startTime = System.nanoTime();
            executor.executeSerial(arrayCopy);
            long endTime = System.nanoTime();
            double tempo = (endTime - startTime) / 1e6;
            writer.append(algorithmName + ",Serial," + size + ",1," + tempo + "\n");
          }

          // Execução Paralela
          for (int numThreads : threads) {
            for (int i = 0; i < 5; i++) {
              SortAlgorithm parallelAlgorithm;
              switch (algIndex) {
                case 0:
                  parallelAlgorithm = new BubbleSortParalelo(numThreads);
                  break;
                case 1:
                  parallelAlgorithm = new QuickSortParalelo(numThreads);
                  break;
                case 2:
                  parallelAlgorithm = new SelectionSortParalelo(numThreads);
                  break;
                case 3:
                  parallelAlgorithm = new MergeSortParalelo(numThreads);
                  break;
                default:
                  throw new IllegalStateException("Unexpected value: " + algIndex);
              }
              System.out.println("executando " + parallelAlgorithm);

              int[] arrayCopy = array.clone();
              long startTime = System.nanoTime();
              new SortExecutor(parallelAlgorithm).executeParallel(arrayCopy, numThreads);
              long endTime = System.nanoTime();
              double tempo = (endTime - startTime) / 1e6;
              writer.append(algorithmName + ",Paralelo," + size + "," + numThreads + "," + tempo + "\n");
            }
        }
        }
      }
    } catch (Exception e) {
      System.out.println(e);
    }


    System.out.println("Resultados salvos em: " + csvFile);
  }

  private static int[] generateRandomArray(int size) {
    Random rand = new Random();
    int[] array = new int[size];
    for (int i = 0; i < size; i++) {
      array[i] = rand.nextInt(100000); //diminui em 3
    }
    return array;
  }
}
