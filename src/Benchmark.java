import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Benchmark {
    public static void main(String[] args) throws InterruptedException, IOException {
        int[] sizesNlogN = { 50000, 500000, 5000000 };
        int[] sizesQuadratico = { 500, 5000, 8000 };
        int[] threads = { 2, 5, 10, 50 };
        String csvFile = "resultados_algoritmos.csv";

        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.append("Algoritmo,Execução,Tamanho,Threads,Tempo(ms)\n");

            for (int size : sizesQuadratico) {
                System.out.println("em " + size);
                int[] array = generateRandomArray(size);
                String[] algorithms = { "BubbleSort", "SelectionSort" };

                for (int algIndex = 0; algIndex < 2; algIndex++) { // 0 - BubbleSort, 1 - SelectionSort

                    double mean = 0.0;

                    // Execução Serial
                    SortAlgorithm alg;
                    if (algIndex == 0) {
                        alg = new BubbleSort();
                    } else {
                        alg = new SelectionSort();
                    }

                    for (int i = 0; i < 5; i++) {
                        int[] arrayCopy = array.clone();
                        long startTime = System.nanoTime();
                        alg.sort(arrayCopy);
                        long endTime = System.nanoTime();
                        checkSortedArray(arrayCopy);
                        double tempo = (endTime - startTime) / 1e6;
                        mean += tempo;
                        writer.append(algorithms[algIndex] + ",Serial," + size + ",1," + tempo + "\n");
                    }

                    mean /= 5;
                    writer.append(algorithms[algIndex] + ",Media serial," + size + ",1," + mean + "\n");

                    // Execução Paralela
                    for (int numThreads : threads) {
                        mean = 0.0;
                        for (int i = 0; i < 5; i++) {
                            SortAlgorithm algParalelo;
                            if (algIndex == 0) {
                                algParalelo = new BubbleSortParalelo(numThreads);
                            } else {
                                algParalelo = new SelectionSortParalelo(numThreads);
                            }
                            System.out.println("executando " + algParalelo);

                            int[] arrayCopy = array.clone();
                            long startTime = System.nanoTime();
                            algParalelo.sort(arrayCopy);
                            long endTime = System.nanoTime();
                            checkSortedArray(arrayCopy);
                            double tempo = (endTime - startTime) / 1e6;
                            mean += tempo;
                            writer.append(
                                    algorithms[algIndex] + ",Paralelo," + size + "," + numThreads + "," + tempo + "\n");
                        }

                        mean /= 5;
                        writer.append(algorithms[algIndex] + ",Media paralelo," + size + "," + numThreads + "," + mean
                                + "\n");
                    }
                }
            }

            // Teste para QuickSort e MergeSort com tamanhos NlogN
            for (int size : sizesNlogN) {
                System.out.println("em " + size);
                int[] array = generateRandomArray(size);

                for (int algIndex = 0; algIndex < 2; algIndex++) { // 0 - QuickSort, 1 - MergeSort
                    String[] algorithms = { "QuickSort", "MergeSort" };
                    String algorithmName = algorithms[algIndex];

                    double mean = 0.0;

                    SortAlgorithm alg;
                    if (algIndex == 0) {
                        alg = new QuickSort();
                    } else {
                        alg = new MergeSort();
                    }

                    // Execução Serial
                    for (int i = 0; i < 5; i++) {
                        int[] arrayCopy = array.clone();
                        long startTime = System.nanoTime();
                        alg.sort(arrayCopy);
                        long endTime = System.nanoTime();
                        checkSortedArray(arrayCopy);
                        double tempo = (endTime - startTime) / 1e6;
                        mean += tempo;
                        writer.append(algorithmName + ",Serial," + size + ",1," + tempo + "\n");
                    }

                    mean /= 5;
                    writer.append(algorithmName + ",Media serial," + size + ",1," + mean + "\n");

                    // Execução Paralela
                    for (int numThreads : threads) {
                        mean = 0.0;
                        for (int i = 0; i < 5; i++) {
                            SortAlgorithm parallelAlgorithm;
                            if (algIndex == 0) {
                                parallelAlgorithm = new QuickSortParalelo(numThreads);
                            } else {
                                parallelAlgorithm = new MergeSortParalelo(numThreads);
                            }
                            System.out.println("executando " + parallelAlgorithm);

                            int[] arrayCopy = array.clone();
                            long startTime = System.nanoTime();
                            parallelAlgorithm.sort(arrayCopy);
                            long endTime = System.nanoTime();
                            checkSortedArray(arrayCopy);
                            double tempo = (endTime - startTime) / 1e6;
                            mean += tempo;
                            writer.append(algorithmName + ",Paralelo," + size + "," + numThreads + "," + tempo + "\n");
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
        createAndShowCharts(csvFile);
    }

    private static void checkSortedArray(int[] array) {
        for (int i = 1; i < array.length; i++) {
            if (array[i] < array[i - 1]) {
                throw new IllegalStateException("Array não está ordenado!");
            }
        }
    }

    private static int[] generateRandomArray(int size) {
        Random rand = new Random();
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = rand.nextInt(100000);
        }
        return array;
    }

    private static void createAndShowCharts(String csvFile) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Benchmark Results");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

            frame.add(createChartPanel(csvFile, "BubbleSort"));
            frame.add(createChartPanel(csvFile, "SelectionSort"));
            frame.add(createChartPanel(csvFile, "QuickSort"));
            frame.add(createChartPanel(csvFile, "MergeSort"));

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static JPanel createChartPanel(String csvFile, String algorithmFilter) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            br.readLine(); // Ignore the header
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String algorithm = values[0];
                String executionType = values[1];
                String size = values[2];
                String threads = values[3];
                double time = Double.parseDouble(values[4]);

                if (algorithm.equals(algorithmFilter) && executionType.contains("Media")) {
                    String category = executionType + " - Size: " + size + " - Threads: " + threads;
                    dataset.addValue(time, executionType + " (Size: " + size + ")", category);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                algorithmFilter + " Benchmark",
                "Configurações",
                "Tempo (ms)",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        return new ChartPanel(barChart);
    }
}
