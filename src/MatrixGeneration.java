import java.io.FileWriter;
import java.io.IOException;

public class MatrixGeneration {
    private static final int LIMIT = 12;
    public static void main(String[] args) throws IOException {
        int [][] arrA = new int[LIMIT][LIMIT];

        FileWriter writerMatrixA = new FileWriter("12.txt");

        for (int i = 0; i < arrA.length; i++) {
            for (int j = 0; j < arrA.length; j++) {
                arrA[i][j] = (int) ((Math.random()*51)-25);
                writerMatrixA.write(arrA[i][j] + " ");
            }
            writerMatrixA.write("\n");
        }

        writerMatrixA.close();
    }
}
