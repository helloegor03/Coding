package Coding;

import java.util.*;

class ShannonFanoTree {
    private ShannonFanoTree left;
    private ShannonFanoTree right;
    private Map<Character, String> codeDict;
    private List<Character> chars;

    public ShannonFanoTree(Map<Character, Double> freq) {
        this(freq, "", false);
    }

    public ShannonFanoTree(Map<Character, Double> freq, String nodeName, boolean isLast) {
        left = null;
        right = null;
        codeDict = new HashMap<>();
        chars = new ArrayList<>(freq.keySet());

        if (!isLast) {
            process(freq);
        }
        generateCodes(freq);
    }

    public String encodeMessage(String message) {
        StringBuilder result = new StringBuilder();
        for (char c : message.toUpperCase().toCharArray()) {
            if (codeDict.containsKey(c)) {
                result.append(codeDict.get(c));
            }
        }
        return result.toString();
    }

    public String decodeMessage(String message) {
        StringBuilder result = new StringBuilder();
        String code = "";
        Collection<String> codes = codeDict.values();

        for (char c : message.toCharArray()) {
            code += c;
            if (codes.contains(code)) {
                char key = getKeyByValue(codeDict, code);
                result.append(key);
                code = "";
            }
        }
        return result.toString();
    }

    private void process(Map<Character, Double> freq) {
        double totalSum = freq.values().stream().mapToDouble(Double::doubleValue).sum();
        double idealPartSum = totalSum / 2;
        double error = Double.MAX_VALUE;
        Character separatorChar = null;
        double partSum = 0.0;

        for (Character key : freq.keySet()) {
            double maybePartSum = partSum + freq.get(key);
            double maybeError = Math.abs(maybePartSum - idealPartSum);

            if (maybeError < error) {
                error = maybeError;
                partSum = maybePartSum;
                separatorChar = key;
            } else {
                break;
            }
        }

        Map<Character, Double> leftPart = new LinkedHashMap<>();
        Map<Character, Double> rightPart = new LinkedHashMap<>();
        boolean isLeftPart = true;

        for (Character key : freq.keySet()) {
            if (isLeftPart) {
                leftPart.put(key, freq.get(key));
            } else {
                rightPart.put(key, freq.get(key));
            }

            if (key.equals(separatorChar)) {
                isLeftPart = false;
            }
        }

        if (!leftPart.isEmpty()) {
            boolean nextLast = leftPart.size() < 2;
            left = new ShannonFanoTree(leftPart, "0", nextLast);
        }

        if (!rightPart.isEmpty()) {
            boolean nextLast = rightPart.size() < 2;
            right = new ShannonFanoTree(rightPart, "1", nextLast);
        }
    }

    private void generateCodes(Map<Character, Double> freq) {
        for (Character c : freq.keySet()) {
            codeDict.put(c, generateCodeToChar(c));
        }
    }

    private String generateCodeToChar(Character ch) {
        StringBuilder result = new StringBuilder();
        ShannonFanoTree curr = this;

        while (true) {
            if (curr.left != null && curr.left.chars.contains(ch)) {
                result.append("0");
                curr = curr.left;
            } else if (curr.right != null && curr.right.chars.contains(ch)) {
                result.append("1");
                curr = curr.right;
            } else {
                break;
            }
        }
        return result.toString();
    }

    private static char getKeyByValue(Map<Character, String> map, String value) {
        for (Map.Entry<Character, String> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        throw new NoSuchElementException("Value not found in map.");
    }

    public static void main(String[] args) {
        // Частоты символов
        Map<Character, Double> freq = new HashMap<>();
        freq.put('Т', 0.056);
        freq.put('Е', 0.074);
        freq.put('Л', 0.036);
        freq.put('У', 0.020);
        freq.put('Ш', 0.010);
        freq.put('К', 0.018);
        freq.put('И', 0.064);
        freq.put('Н', 0.056);
        freq.put(' ', 0.145);
        freq.put('Г', 0.015);
        freq.put('О', 0.095);
        freq.put('Р', 0.041);
        freq.put('П', 0.030);
        freq.put('А', 0.064);
        freq.put('В', 0.039);
        freq.put('Ч', 0.013);

        ShannonFanoTree tree = new ShannonFanoTree(freq);
        String message = "Телушкин Егор Павлович";
        System.out.println("Исходное сообщение: " + message);

        String encodedMessage = tree.encodeMessage(message);
        System.out.println("Закодированное сообщение: " + encodedMessage);

        String decodedMessage = tree.decodeMessage(encodedMessage);
        System.out.println("Декодированное сообщение: " + decodedMessage);
    }
}
