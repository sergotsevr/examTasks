package com.tsystems.javaschool.tasks.pyramid;

import java.util.Collections;
import java.util.List;

public class PyramidBuilder {

    /**
     * Builds a pyramid with sorted values (with minumum value at the top line and maximum at the bottom,
     * from left to right). All vacant positions in the array are zeros.
     *
     * @param inputNumbers to be used in the pyramid
     * @return 2d array with pyramid inside
     * @throws {@link CannotBuildPyramidException} if the pyramid cannot be build with given input
     */
    public int[][] buildPyramid(List<Integer> inputNumbers) {
        try {
            Collections.sort(inputNumbers);
            int size = getDemention(inputNumbers);
            if (size==-1){
                throw new CannotBuildPyramidException();
            }
            int array[][] = new int[size / 2][size - 2];
            int[][] pyramida = nullifier(array, size);
            pyramida = fillPyramid(inputNumbers, pyramida);
            return pyramida;
        }
        catch (Error error){
            throw new CannotBuildPyramidException();
        }
        catch (Exception e) {
            throw new CannotBuildPyramidException();
        }
    }

    public static int[][] fillPyramid(List<Integer> numbers, int[][] array) {
        int size = getDemention(numbers);
        int position = 0;
        int middle = (size / 2);
        for (int i = 0; i <= size / 2 - 1; i++) {
            for (int j = 0; j < (i + 1) * 2; j += 2) {
                array[i][middle - i + j - 1] = numbers.get(position++);
            }
        }
        return array;
    }
    //nullifier создает массив заданного размера и обнуляет его элементы
    public static int[][] nullifier( int[][] array, int size) {
        for (int i = 0; i < size / 2; i++) {
            for (int j = 0; j < size - 2; j++) {
                array[i][j] = 0;
            }
        }
        return array;
    }
    //получаем размер матрицы
    public static int getDemention(List<Integer> arr) {
        Double a = 1D;
        int i = 1;
        int size = arr.size();
        while (a < size) {         //пирамида может быть построена только из 1,3,6,10.. элементов.
            a = 0.5 * i * (i + 1); // Это формула i-го элемента последовательности
            i++;
        }
        if (a.intValue() == size) {
            return 2 * i - 1;
        } else {
            return -1;
        }
    }

}
