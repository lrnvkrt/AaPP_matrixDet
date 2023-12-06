import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class Main {
    private static final String filename = "11.txt";
    private static final int LIMIT = 11;

    public static void main(String[] args) {
        int[][] matrix = readNumbersFromFile(filename);

        //считаем матрицу на одном потоке
        long timeOne = System.currentTimeMillis();
        System.out.println(determinant(matrix));
        timeOne = System.currentTimeMillis() - timeOne;
        System.out.println("It worked " + timeOne + "ms on one thread.");

        //считаем матрицу на многих потоках
        long timeMany = System.currentTimeMillis();
        ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
        System.out.println(pool.invoke(new DeterminantTask(matrix)));
        pool.shutdown();
        timeMany = System.currentTimeMillis() - timeMany;
        System.out.println("It worked " + timeMany + "ms on many threads.");
    }

    //метод для чтения матрицы из файла
    private static int[][] readNumbersFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            int[][] result = new int[LIMIT][LIMIT];
            String[] numbers;
            for (int i = 0; i < LIMIT; i++) {
                numbers = (reader.readLine()).split(" ");
                for (int j = 0; j < LIMIT; j++) {
                    result[i][j] = Integer.parseInt(numbers[j]);
                }
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //метод для получения минора матрицы
    public static int [][] getMinor(int[][] matrix, int line, int column) {
        int [][] minorMatrix = new int[matrix.length-1][matrix.length-1];
        int withoutLine = 0;
        for (int i = 0; i < matrix.length-1; i++) {
            if (i == line) {
                withoutLine = 1;
            }
            int withoutColumn = 0;
            for (int j = 0; j < matrix.length - 1; j++) {
                if (j == column) {
                    withoutColumn = 1;
                }
                minorMatrix[i][j] = matrix[i + withoutLine][j + withoutColumn];
            }
        }
        return minorMatrix;
    }

    //метод для расчёта определителя на одном потоке
    private static Long determinant(int[][] matrix) {
        double det = 0;
        if (matrix.length == 1) {
            return (long) matrix[0][0];
        }
        if (matrix.length == 2) {
            return (long) matrix[0][0]*matrix[1][1]- (long) matrix[0][1]*matrix[1][0];
        }
        else {
            for (int j = 0; j < matrix.length; j++) {
                det = det + Math.pow(-1, j) * matrix[0][j] * determinant(getMinor(matrix, 0, j));
            }
            return (long) det;
        }
    }

    ///Класс DeterminantTask представляет собой задачу для вычисления
    // определителя матрицы методом миноров с использованием параллельных вычислений
    // с помощью Fork/Join.
    private static class DeterminantTask extends RecursiveTask<Long> {
        private int[][] matrix;

        public DeterminantTask(int[][] matrix) {
            this.matrix = matrix;
        }

        @Override
        protected Long compute() {
//            System.out.println("Thread: " + Thread.currentThread().getName() + " is working on matrix size: " + matrix.length);
            if (matrix.length == 1) {
                return (long) matrix[0][0];
            }
            if (matrix.length == 2) {
                return (long) matrix[0][0] * matrix[1][1] - (long) matrix[0][1] * matrix[1][0];
            } else {
                double det = 0;
                DeterminantTask[] tasks = new DeterminantTask[matrix.length];
                for (int j = 0; j < matrix.length; j++) {
                    tasks[j] = new DeterminantTask(getMinor(matrix, 0, j));
                    tasks[j].fork();
                }
                for (int i = 0; i < matrix.length; i++) {
                    det = det + Math.pow(-1, i) * matrix[0][i] * tasks[i].join();
                }
                return (long) det;
            }
        }
    }
}