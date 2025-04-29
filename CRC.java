package Coding;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CRC {

    public static int[] crc(int[] srcMsg, int[] checksum) {
        int[] poly = {1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1};
        int W = poly.length - 1;
        int[] reg = new int[W];

        List<Integer> msgList = new ArrayList<>();
        for (int bit : srcMsg) {
            msgList.add(bit);
        }
        if (checksum == null || checksum.length == 0) {
            for (int i = 0; i < W; i++) {
                msgList.add(0);
            }
        } else {
            for (int bit : checksum) {
                msgList.add(bit);
            }
        }

        int[] msg = msgList.stream().mapToInt(i -> i).toArray();
        int pos = 0;

        while (pos < msg.length) {
            int popBit = reg[0];
            int msgBit = msg[pos];

            // Shift left
            System.arraycopy(reg, 1, reg, 0, W - 1);
            reg[W - 1] = msgBit;

            if (popBit == 1) {
                for (int i = 0; i < W; i++) {
                    reg[i] ^= poly[poly.length - i - 1];
                }
            }

            pos++;
        }

        return reg;
    }

    public static void main(String[] args) {
        String message = "1234";
        int messageInt = Integer.parseInt(message, 10);
        String messageBin = Integer.toBinaryString(messageInt);

        int[] srcMsg = new int[messageBin.length()];
        for (int i = 0; i < messageBin.length(); i++) {
            srcMsg[i] = Character.getNumericValue(messageBin.charAt(i));
        }

        System.out.println("Исх. :\t" + message);
        for (int bit : srcMsg) {
            System.out.print(bit);
        }
        System.out.println();

        int[] check = crc(srcMsg, null);
        System.out.print("CRC:  ");
        for (int bit : check) {
            System.out.print(bit);
        }
        System.out.println();

        Random random = new Random();
        int corrId = random.nextInt(srcMsg.length);
        int[] corruptMsg = new int[srcMsg.length];
        for (int i = 0; i < srcMsg.length; i++) {
            corruptMsg[i] = random.nextInt(3) != 0 ? srcMsg[i] : srcMsg[i] ^ 1;
        }

        int corrInt = 0;
        for (int bit : corruptMsg) {
            corrInt = (corrInt << 1) | bit;
        }
        String corrStr = new String(new byte[] {(byte) corrInt}, java.nio.charset.StandardCharsets.UTF_8);

        System.out.println("Повр. :\t" + corrStr);
        for (int bit : corruptMsg) {
            System.out.print(bit);
        }
        System.out.println();

        int[] checkCorr = crc(corruptMsg, check);
        System.out.print("\nCRC':  ");
        for (int bit : checkCorr) {
            System.out.print(bit);
        }
        System.out.println();

        boolean hasErrors = false;
        for (int bit : checkCorr) {
            if (bit != 0) {
                hasErrors = true;
                break;
            }
        }

        if (hasErrors) {
            System.out.println("Остаток ненулевой, сообщение повреждено");
        } else {
            System.out.println("Сообщение передано нормально");
        }

        StringBuilder hexBuilder = new StringBuilder();
        for (int bit : check) {
            hexBuilder.append(bit);
        }
        int hexValue = Integer.parseInt(hexBuilder.toString(), 2);
        System.out.println(Integer.toHexString(hexValue));
    }
}
