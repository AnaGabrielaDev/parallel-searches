import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Benchmark {
    public static void main(String[] args) throws InterruptedException, IOException {
        int[] sizesNlogN = { 50_000, 500_000, 5_000_000 };
        int[] sizesQuadratico = { 500, 5_000, 8_000 };
        int[] threads = { 2, 5, 50, 500 };
        String csvFile = "resultados_algoritmos.csv";

        Map<String, Double> totalTimes = new HashMap<>();
        Map<String, Integer> countTimes = new HashMap<>();

        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.append("Algoritmo,Execução,Tamanho,Threads,Tempo(ms)\n");

            // Teste para BubbleSort e SelectionSort com tamanhos quadráticos
            for (int size : sizesQuadratico) {
                System.out.println("em " + size);
                int[] array = generateRandomArray(size);

                for (int algIndex = 0; algIndex < 2; algIndex++) { // 0 - BubbleSort, 1 - SelectionSort
                    String algorithmName;
                    SortExecutor executor;

                    switch (algIndex) {
                        case 0:
                            algorithmName = "BubbleSort";
                            executor = new SortExecutor(new BubbleSort());
                            break;
                        case 1:
                            algorithmName = "SelectionSort";
                            executor = new SortExecutor(new SelectionSort());
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + algIndex);
                    }

                    double mean = 0.0;

                    // Execução Serial
                    for (int i = 0; i < 5; i++) {
                        int[] arrayCopy = array.clone();
                        long startTime = System.nanoTime();
                        executor.executeSerial(arrayCopy);
                        long endTime = System.nanoTime();
                        double tempo = (endTime - startTime) / 1e6;
                        mean += tempo;
                        writer.append(algorithmName + ",Serial," + size + ",1," + tempo + "\n");

                        String key = algorithmName + ",Serial," + size + ",1";
                        totalTimes.put(key, totalTimes.getOrDefault(key, 0.0) + tempo);
                        countTimes.put(key, countTimes.getOrDefault(key, 0) + 1);
                    }

                    mean /= 5;
                    writer.append(algorithmName + ",Media serial," + size + ",1," + mean + "\n");

                    // Execução Paralela
                    for (int numThreads : threads) {
                        for (int i = 0; i < 5; i++) {
                            mean = 0.0;
                            SortAlgorithm parallelAlgorithm;
                            switch (algIndex) {
                                case 0:
                                    parallelAlgorithm = new BubbleSortParalelo(numThreads);
                                    break;
                                case 1:
                                    parallelAlgorithm = new SelectionSortParalelo(numThreads);
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
                            mean += tempo;
                            writer.append(algorithmName + ",Paralelo," + size + "," + numThreads + "," + tempo + "\n");

                            String key = algorithmName + ",Paralelo," + size + "," + numThreads;
                            totalTimes.put(key, totalTimes.getOrDefault(key, 0.0) + tempo);
                            countTimes.put(key, countTimes.getOrDefault(key, 0) + 1);
                        }

                        mean /= 5;
                        writer.append(algorithmName + ",Media paralelo," + size + "," + numThreads + "," + mean + "\n");
                    }
                }
            }

            // Teste para QuickSort e MergeSort com tamanhos NlogN
            for (int size : sizesNlogN) {
                System.out.println("em " + size);
                int[] array = generateRandomArray(size);

                for (int algIndex = 0; algIndex < 2; algIndex++) { // 0 - QuickSort, 1 - MergeSort
                    String algorithmName;
                    SortExecutor executor;

                    switch (algIndex) {
                        case 0:
                            algorithmName = "QuickSort";
                            executor = new SortExecutor(new QuickSort());
                            break;
                        case 1:
                            algorithmName = "MergeSort";
                            executor = new SortExecutor(new MergeSort());
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + algIndex);
                    }

                    double mean = 0.0;

                    // Execução Serial
                    for (int i = 0; i < 5; i++) {
                        int[] arrayCopy = array.clone();
                        long startTime = System.nanoTime();
                        executor.executeSerial(arrayCopy);
                        long endTime = System.nanoTime();
                        double tempo = (endTime - startTime) / 1e6;
                        mean += tempo;
                        writer.append(algorithmName + ",Serial," + size + ",1," + tempo + "\n");

                        String key = algorithmName + ",Serial," + size + ",1";
                        totalTimes.put(key, totalTimes.getOrDefault(key, 0.0) + tempo);
                        countTimes.put(key, countTimes.getOrDefault(key, 0) + 1);
                    }

                    mean /= 5;
                    writer.append(algorithmName + ",Media serial," + size + ",1," + mean + "\n");


                    // Execução Paralela
                    for (int numThreads : threads) {
                        mean = 0.0;
                        for (int i = 0; i < 5; i++) {
                            SortAlgorithm parallelAlgorithm;
                            switch (algIndex) {
                                case 0:
                                    parallelAlgorithm = new QuickSortParalelo(numThreads);
                                    break;
                                case 1:
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
                            mean += tempo;
                            writer.append(algorithmName + ",Paralelo," + size + "," + numThreads + "," + tempo + "\n");

                            String key = algorithmName + ",Paralelo," + size + "," + numThreads;
                            totalTimes.put(key, totalTimes.getOrDefault(key, 0.0) + tempo);
                            countTimes.put(key, countTimes.getOrDefault(key, 0) + 1);
                        }
                        mean /= 5;
                        writer.append(algorithmName + ",Media paralelo," + size + "," + numThreads + "," + mean + "\n");
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
            array[i] = rand.nextInt(100000); // diminui em 3
        }
        return array;
    }
}
