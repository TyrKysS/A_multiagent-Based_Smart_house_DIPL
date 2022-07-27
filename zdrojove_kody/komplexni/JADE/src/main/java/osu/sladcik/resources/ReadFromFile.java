package osu.sladcik.resources;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class ReadFromFile {
    public static int[][] readMatrixFromFile(String path, int rows, int colls) throws FileNotFoundException {
        Scanner sc = new Scanner(new BufferedReader(new FileReader(path)));
        int[][] array = new int[rows][colls];
        while (sc.hasNextLine()){
            for (int i = 0; i < array.length; i++) {
                String[] line = sc.nextLine().trim().split(" ");
                for (int j = 0; j < line.length; j++) {
                    array[i][j] = Integer.parseInt(line[j]);
                }
            }
        }
        return array;
    }
}
