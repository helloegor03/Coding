import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PolynomialDivision {

    // Метод для выполнения деления с остатком полиномов
    public static PolyResult performDivision(List<Integer> numerator, List<Integer> denominator) {
        if (numerator.size() < denominator.size()) {
            return new PolyResult(new ArrayList<>(), new ArrayList<>(numerator));
        }

        List<Integer> currentBuffer = new ArrayList<>(numerator.subList(0, denominator.size()));
        List<Integer> remainingElements = new ArrayList<>(numerator.subList(denominator.size(), numerator.size()));
        List<Integer> divisionResult = new ArrayList<>();

        while (true) {
            divisionResult.add(currentBuffer.get(0));
            if (currentBuffer.get(0) == 1) {
                for (int i = 1; i < currentBuffer.size(); i++) {
                    currentBuffer.set(i - 1, currentBuffer.get(i) ^ denominator.get(i));
                }
            } else {
                for (int i = 1; i < currentBuffer.size(); i++) {
                    currentBuffer.set(i - 1, currentBuffer.get(i));
                }
            }

            if (!remainingElements.isEmpty()) {
                currentBuffer.set(currentBuffer.size() - 1, remainingElements.remove(0));
            } else {
                currentBuffer.remove(currentBuffer.size() - 1);
                break;
            }
        }

        divisionResult.add(currentBuffer.get(0));

        removeLeadingZeros(divisionResult);
        removeLeadingZeros(currentBuffer);

        return new PolyResult(divisionResult, currentBuffer);
    }

    // Удаление ведущих нулей
    private static void removeLeadingZeros(List<Integer> values) {
        while (!values.isEmpty() && values.get(0) == 0) {
            values.remove(0);
        }
    }

    public static void main(String[] args) {
        List<Integer> inputData = Arrays.asList(0, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 1, 0);
        List<Integer> generator = Arrays.asList(1, 0, 0, 1, 1, 1, 1);
        int generatorDegree = generator.size() - 1;
        int totalLength = 15;
        int messageLength = 9;
        int maxErrors = 3;

        PolyResult initialResult = performDivision(inputData, generator);
        List<Integer> checksum = initialResult.remainder;
        System.out.println("Синдром (контрольная сумма): " + checksum);

        int iteration = 0;
        List<Integer> currentPolynomial = new ArrayList<>(checksum);

        while (iteration < totalLength && (currentPolynomial.size() - 1) > maxErrors) {
            List<Integer> expandedPolynomial = new ArrayList<>(currentPolynomial);
            expandedPolynomial.add(0);

            if (expandedPolynomial.size() - 1 >= generatorDegree) {
                PolyResult nextResult = performDivision(expandedPolynomial, generator);
                currentPolynomial = nextResult.remainder;
            } else {
                currentPolynomial = expandedPolynomial;
            }

            iteration++;
            System.out.println("Шаг " + iteration + ": " + currentPolynomial);
        }

        if (iteration < totalLength) {
            System.out.println("Обнаружен паттерн ошибок.");
            List<Integer> errorVector = new ArrayList<>(currentPolynomial);
            for (int i = 0; i < totalLength - iteration; i++) errorVector.add(0);
            while (errorVector.size() < inputData.size()) errorVector.add(0, 0);
            System.out.println("Вектор ошибок: " + errorVector);

            List<Integer> correctedData = new ArrayList<>();
            for (int i = 0; i < inputData.size(); i++) {
                correctedData.add(inputData.get(i) ^ errorVector.get(i));
            }

            PolyResult validationResult = performDivision(correctedData, generator);
            List<Integer> recoveredMessage = validationResult.quotient;
            List<Integer> validationRemainder = validationResult.remainder;

            System.out.println("Восстановленное сообщение: " + recoveredMessage);
            System.out.println("Проверка контрольной суммы: " + (validationRemainder.stream().noneMatch(x -> x == 1)) + " Остаток: " + validationRemainder);

            if (validationRemainder.stream().anyMatch(x -> x == 1)) {
                System.out.println("Ошибка: контрольная сумма после восстановления не совпадает.");
            }
        } else {
            System.out.println("Слишком много ошибок (превышает " + maxErrors + "), восстановление невозможно.");
        }
    }

    static class PolyResult {
        List<Integer> quotient;
        List<Integer> remainder;

        public PolyResult(List<Integer> quotient, List<Integer> remainder) {
            this.quotient = quotient;
            this.remainder = remainder;
        }
    }
}
