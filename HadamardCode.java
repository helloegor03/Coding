import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class HadamardCode {

    public static int[][] matrixMult(int[][] A, int[][] B) {
        int nA = A.length;
        int mA = A[0].length;
        int nB = B.length;
        int mB = B[0].length;

        int[][] C = new int[nA][mB];
        for (int i = 0; i < nA; i++) {
            for (int j = 0; j < mB; j++) {
                for (int k = 0; k < mA; k++) {
                    C[i][j] += A[i][k] * B[k][j];
                }
            }
        }
        return C;
    }

    public static int[][] makeNextMatrix(int[][] H) {
        int size = H.length;
        int[][] HNew = new int[2 * size][2 * size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                HNew[i][j] = H[i][j];
                HNew[i][j + size] = H[i][j];
                HNew[i + size][j] = H[i][j];
                HNew[i + size][j + size] = -H[i][j];
            }
        }
        return HNew;
    }

    public static String encodeBlock(int[][] H, String block) {
        int blockN = Integer.parseInt(block, 2);
        int[] encodedRow = H[blockN];

        // Вывод вектора перед кодированием
        System.out.println("Исходный блок: " + block);
        System.out.print("Вектор до кодирования: ");
        for (int val : encodedRow) {
            System.out.print(val == -1 ? "0 " : "1 ");
        }
        System.out.println();

        StringBuilder encodedBlock = new StringBuilder();
        for (int val : encodedRow) {
            encodedBlock.append(val == -1 ? "0" : "1");
        }

        return encodedBlock.toString();
    }

    public static String decodeBlock(int[][] H, String block, List<Integer> errorIndexes) {
        int[] blockToDecode = new int[block.length()];
        for (int i = 0; i < block.length(); i++) {
            blockToDecode[i] = block.charAt(i) == '0' ? -1 : 1;
        }


        System.out.println("Блок для декодирования: " + block);
        System.out.print("Вектор для декодирования: ");
        for (int val : blockToDecode) {
            System.out.print(val == -1 ? "0 " : "1 ");
        }
        System.out.println();

        int maxIndex = 0;
        int maxValue = Integer.MIN_VALUE;
        for (int i = 0; i < H.length; i++) {
            int dotProduct = 0;
            for (int j = 0; j < H[0].length; j++) {
                dotProduct += H[i][j] * blockToDecode[j];
            }


            if (dotProduct > maxValue) {
                maxValue = dotProduct;
                maxIndex = i;
            }
        }


        if (maxValue < H[0].length / 2) {
            errorIndexes.add(maxIndex);
        }


        String decodedBlock = Integer.toBinaryString(maxIndex);
        while (decodedBlock.length() < (int) (Math.log(H.length) / Math.log(2))) {
            decodedBlock = "0" + decodedBlock;
        }


        int originalIndex = Integer.parseInt(decodedBlock, 2);
        if (originalIndex != maxIndex) {
            System.out.println("Ошибка в декодировании! Индекс отличается: " + originalIndex + " != " + maxIndex);
            errorIndexes.add(originalIndex); // Добавляем ошибку, если индексы не совпали
        }

        return decodedBlock;
    }


    public static String corruptBlock(String block, int maxN) {
        Random random = new Random();
        char[] corrupted = block.toCharArray();


        for (int i = 0; i < maxN; i++) {
            int index = random.nextInt(block.length());
            corrupted[index] = corrupted[index] == '0' ? '1' : '0';
        }
        return new String(corrupted);
    }

    public static String textToBinary(String text) {
        StringBuilder binaryString = new StringBuilder();
        for (char c : text.toCharArray()) {
            binaryString.append(String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0'));
        }
        return binaryString.toString();
    }

    public static String binaryToText(String binary) {
        // Дополнение строки нулями до длины, кратной 8
        while (binary.length() % 8 != 0) {
            binary = "0" + binary;
        }
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < binary.length(); i += 8) {
            String byteStr = binary.substring(i, i + 8);
            text.append((char) Integer.parseInt(byteStr, 2));
        }
        return text.toString();
    }

    public static void main(String[] args) {
        int N = 4;
        String message = "ONE TWO THREE";
        int errorsCnt = 2;

        int[][] H = {{1}};
        for (int i = 0; i < N; i++) {
            H = makeNextMatrix(H);
        }

        int[][] H16x8 = new int[16][8];
        for (int i = 0; i < 16; i++) {
            System.arraycopy(H[i], 0, H16x8[i], 0, 8);
        }

        System.out.println("Матрица Адамара (16x8):");
        for (int[] row : H16x8) {
            System.out.println(Arrays.toString(row));
        }

        String messageBin = textToBinary(message);
        while (messageBin.length() % N != 0) {
            messageBin = "0" + messageBin;
        }

        System.out.println("Бинарное сообщение:");
        System.out.println(messageBin);

        int blocksCnt = messageBin.length() / N;
        String[] messageBlocks = new String[blocksCnt];
        for (int i = 0; i < blocksCnt; i++) {
            messageBlocks[i] = messageBin.substring(i * N, (i + 1) * N);
        }

        System.out.println("Блоки сообщения:");
        System.out.println(Arrays.toString(messageBlocks));

        List<String> encodedBlocks = new ArrayList<>();
        for (String block : messageBlocks) {
            encodedBlocks.add(encodeBlock(H16x8, block));
        }

        System.out.println("Закодированные блоки:");
        System.out.println(encodedBlocks);

        List<String> corruptedBlocks = new ArrayList<>();
        for (String block : encodedBlocks) {
            corruptedBlocks.add(corruptBlock(block, errorsCnt));
        }

        System.out.println("Искажённые блоки:");
        System.out.println(corruptedBlocks);

        List<String> decodedBlocks = new ArrayList<>();
        List<Integer> errorIndexes = new ArrayList<>(); // Список для хранения индексов ошибок
        for (String block : corruptedBlocks) {
            decodedBlocks.add(decodeBlock(H16x8, block, errorIndexes));
        }

        System.out.println("Декодированные блоки:");
        System.out.println(decodedBlocks);

        // Вывод ошибок
        System.out.println("Ошибки найдены в блоках с индексами: " + errorIndexes);

        StringBuilder recoveredMessageBin = new StringBuilder();
        for (String block : decodedBlocks) {
            recoveredMessageBin.append(block);
        }

        String recoveredMessageBinary = recoveredMessageBin.toString().replaceAll("^0+", "");
        String recoveredMessage = binaryToText(recoveredMessageBinary);

        System.out.println("Восстановленное сообщение: " + recoveredMessage);
    }

}
