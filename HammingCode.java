package Coding;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HammingCode {

    public static String calcBits(String srcMsg) {
        List<Character> msg = new ArrayList<>();
        for (char c : srcMsg.toCharArray()) {
            msg.add(c);
        }
        List<Character> newMsg = new ArrayList<>();

        int lenMsg = msg.size();
        int bitsAdded = 0;
        int nextBitNumber = 1;
        int i = 1;

        while (i <= lenMsg + bitsAdded) {
            if (i == nextBitNumber) {
                newMsg.add('1');
                bitsAdded++;
                nextBitNumber *= 2;
            } else {
                newMsg.add(msg.get(i - 1 - bitsAdded));
            }
            i++;
        }

        int newLen = lenMsg + bitsAdded;
        int bitNumber = 1;

        while (bitNumber <= newLen) {
            nextBitNumber = bitNumber;
            int bitValue = 0;
            for (i = 1; i <= newLen - bitNumber; i++) {
                // Пропуск контрольных бит
                if (i + bitNumber == nextBitNumber) {
                    nextBitNumber *= 2;
                    continue;
                }
                // Запись группы из N бит и пропуск следующих за ними N бит
                if ((i % (2 * bitNumber)) < bitNumber) {
                    bitValue += Character.getNumericValue(newMsg.get(bitNumber + i - 1));
                }
            }
            bitValue %= 2;
            newMsg.set(bitNumber - 1, Character.forDigit(bitValue, 10));
            bitNumber *= 2;
        }

        StringBuilder result = new StringBuilder();
        for (char c : newMsg) {
            result.append(c);
        }
        return result.toString();
    }

    public static String recalcBits(String msg) {
        List<Character> cleanMsg = new ArrayList<>();
        int nextControl = 1;
        for (int i = 0; i < msg.length(); i++) {
            if (i + 1 == nextControl) {
                nextControl *= 2;
            } else {
                cleanMsg.add(msg.charAt(i));
            }
        }

        StringBuilder cleanMsgString = new StringBuilder();
        for (char c : cleanMsg) {
            cleanMsgString.append(c);
        }
        return calcBits(cleanMsgString.toString());
    }

    public static String restoreMsg(String corrupted, String recalced) {
        int errorBitNumber = 0;
        for (int i = 1; i <= corrupted.length(); i++) {
            if (corrupted.charAt(i - 1) != recalced.charAt(i - 1)) {
                errorBitNumber += i;
            }
        }

        StringBuilder restoredMsg = new StringBuilder();
        for (int i = 0; i < recalced.length(); i++) {
            if (i != errorBitNumber - 1) {
                restoredMsg.append(corrupted.charAt(i));
            } else {
                restoredMsg.append(corrupted.charAt(i) == '1' ? '0' : '1');
            }
        }
        return restoredMsg.toString();
    }

    public static void main(String[] args) {
        String msg = "0100010000111101";
        String encoded = calcBits(msg);

        Random random = new Random();
        int n = random.nextInt(encoded.length());

        StringBuilder corrupted = new StringBuilder(encoded);
        corrupted.setCharAt(n, encoded.charAt(n) == '1' ? '0' : '1');

        String recalced = recalcBits(encoded);
        String restored = restoreMsg(corrupted.toString(), recalced);

        System.out.println("Исходное сообщение:        " + msg);
        System.out.println("С корректирующими битами:  " + encoded);
        System.out.println("Искажён один бит:          " + corrupted);
        System.out.println("Восстановленное сообщение: " + restored);
        System.out.println(restored.equals(encoded));
    }
}
