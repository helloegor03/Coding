package Coding;

import java.util.*;

class Node {
    char symbol;
    double frequency;
    Node left;
    Node right;

    public Node(char symbol, double frequency) {
        this.symbol = symbol;
        this.frequency = frequency;
        this.left = null;
        this.right = null;
    }

    public boolean isLeaf() {
        return left == null && right == null;
    }

    public List<Integer> traverse(char targetSymbol, List<Integer> path) {
        if (isLeaf()) {
            return symbol == targetSymbol ? path : null;
        } else {
            List<Integer> leftPath = left != null ? left.traverse(targetSymbol, new ArrayList<>(path)) : null;
            if (leftPath != null) {
                leftPath.add(0);
                return leftPath;
            }
            List<Integer> rightPath = right != null ? right.traverse(targetSymbol, new ArrayList<>(path)) : null;
            if (rightPath != null) {
                rightPath.add(1);
                return rightPath;
            }
        }
        return null;
    }
}

class HuffmanTree {
    private List<Node> nodes;
    private Node root;
    private Map<Character, Double> frequencies;

    public HuffmanTree(Map<Character, Double> frequencies) {
        this.frequencies = frequencies;
        this.nodes = new ArrayList<>();
    }

    public void build() {
        for (Map.Entry<Character, Double> entry : frequencies.entrySet()) {
            nodes.add(new Node(entry.getKey(), entry.getValue()));
        }

        while (nodes.size() > 1) {
            nodes.sort(Comparator.comparingDouble(node -> node.frequency));

            Node left = nodes.remove(0);
            Node right = nodes.remove(0);

            Node parent = new Node('\0', left.frequency + right.frequency);
            parent.left = left;
            parent.right = right;

            nodes.add(parent);
        }

        root = nodes.isEmpty() ? null : nodes.get(0);
    }

    public List<Integer> encode(String text) throws Exception {
        List<Integer> encodedText = new ArrayList<>();
        for (char c : text.toUpperCase().toCharArray()) {
            List<Integer> encodedSymbol = root.traverse(c, new ArrayList<>());
            if (encodedSymbol != null) {
                Collections.reverse(encodedSymbol); // Reverse to correct path order
                encodedText.addAll(encodedSymbol);
            } else {
                throw new Exception("Символ '" + c + "' отсутствует в дереве Хаффмана.");
            }
        }
        return encodedText;
    }

    public String decode(List<Integer> bits) {
        StringBuilder decodedText = new StringBuilder();
        Node current = root;

        for (int bit : bits) {
            current = bit == 0 ? current.left : current.right;

            if (current.isLeaf()) {
                decodedText.append(current.symbol);
                current = root;
            }
        }

        return decodedText.toString();
    }

    public static void main(String[] args) {
        // Частоты символов
        Map<Character, Double> frequencies = new HashMap<>();
        frequencies.put('Т', 0.056);
        frequencies.put('Е', 0.074);
        frequencies.put('Л', 0.036);
        frequencies.put('У', 0.020);
        frequencies.put('Ш', 0.010);
        frequencies.put('К', 0.018);
        frequencies.put('И', 0.064);
        frequencies.put('Н', 0.056);
        frequencies.put(' ', 0.145);
        frequencies.put('Г', 0.015);
        frequencies.put('О', 0.095);
        frequencies.put('Р', 0.041);
        frequencies.put('П', 0.030);
        frequencies.put('А', 0.064);
        frequencies.put('В', 0.039);
        frequencies.put('Ч', 0.013);

        HuffmanTree tree = new HuffmanTree(frequencies);
        tree.build();

        String message = "Телушкин Егор Павлович";
        System.out.println("Исходное сообщение: " + message);

        try {
            List<Integer> encodedMessage = tree.encode(message);
            System.out.println("Закодированное сообщение: " + encodedMessage);

            String decodedMessage = tree.decode(encodedMessage);
            System.out.println("Декодированное сообщение: " + decodedMessage);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}