import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Benchmark {
  public static void main(String[] args) throws InterruptedException, IOException {
    int[] sizes = {100, 1000, 10000};  // Diferentes tamanhos de array, falta um zero em cada
    int[] threads = {2, 4, 8, 10};
    String csvFile = "resultados_algoritmos.csv";

    try (FileWriter writer = new FileWriter(csvFile)) {
      writer.append("Algoritmo,Execução,Tamanho,Threads,Tempo(ms)\n");

      SortAlgorithm[] algorithms = {
          new BubbleSort(),
          new QuickSort(),
          new SelectionSort(),
      };

      String[] algorithmNames = {"BubbleSort", "QuickSort", "SelectionSort"};

      for (int size : sizes) {
        int[] array = generateRandomArray(size);  // Gera array aleatório

        for (int algIndex = 0; algIndex < algorithms.length; algIndex++) {
          SortAlgorithm algorithm = algorithms[algIndex];
          String algorithmName = algorithmNames[algIndex];
          SortExecutor executor = new SortExecutor(algorithm);

          // Execução Serial - 5 amostras
          for (int i = 0; i < 5; i++) {
            System.out.println("executando serial -- ");
            int[] arrayCopy = array.clone();  // Copia do array para manter o original intacto
            long startTime = System.nanoTime();
            executor.executeSerial(arrayCopy);  // Executa o algoritmo de forma serial
            long endTime = System.nanoTime();
            double tempo = (endTime - startTime) / 1e6;  // Tempo em milissegundos
            writer.append(algorithmName + ",Serial," + size + ",1," + tempo + "\n");
          }

          // Execução Paralela - 5 amostras por cada configuração de threads
          for (int numThreads : threads) {
            System.out.println("executando parallel -- " + numThreads);
            for (int i = 0; i < 5; i++) {
              int[] arrayCopy = array.clone();  // Copia do array para manter o original intacto
              long startTime = System.nanoTime();
              executor.executeParallel(arrayCopy, numThreads);  // Executa o algoritmo de forma paralela
              long endTime = System.nanoTime();
              double tempo = (endTime - startTime) / 1e6;  // Tempo em milissegundos
              writer.append(algorithmName + ",Paralelo," + size + "," + numThreads + "," + tempo + "\n");
            }
          }
        }
      }
    }

    System.out.println("Resultados salvos em: " + csvFile);
  }

  private static int[] generateRandomArray(int size) {
    Random rand = new Random();
    int[] array = new int[size];
    for (int i = 0; i < size; i++) {
      array[i] = rand.nextInt(1000000);
    }
    return array;
  }
}
