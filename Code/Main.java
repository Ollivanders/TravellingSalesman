import java.io.IOException;
import java.util.*;
import java.io.*;

public class Main {
    private static int[][] distanceMatrix;
    private static int size;
    private static int length;
    private static int[] tour;

    public static void main(String[] args) {
        String file = "535";
        try {
            makeDistanceMatrix(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
          //  runAnnealing();
        } catch (Exception e) {
            e.printStackTrace();

        }

        try {
            runAntColony();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //runAA();
        convertTour();

        printToConsole(file);
        try

        {
            makeFile(file);
        } catch (Exception e)

        {
            e.printStackTrace();
        }

    }

    public static void makeDistanceMatrix(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("Data/AISearchfile" + filename + ".txt"));
        String line = "";
        String completeText = "";

        while ((line = reader.readLine()) != null) {
            completeText = completeText + line;
        }
        ArrayList<String> extractedText = new ArrayList<String>(Arrays.asList(completeText.split(",")));
        //splits each word (seperating by a space) and adds it to an array, then converts it into an arraylist

        ArrayList<Integer> numericalData = new ArrayList<Integer>();
        for (String text : extractedText) {
            numericalData.add(getDigits(text));
        }
        size = numericalData.get(1);
        numericalData.remove(0); // removes the name
        numericalData.remove(0); // removes the size
        distanceMatrix = new int[size][size];
        int index = 0;

        for (int row = 0; row < size; row++) {
            for (int column = row; column < size; column++) {
                if (column != row) {
                    distanceMatrix[row][column] = numericalData.get(index);
                    distanceMatrix[column][row] = numericalData.get(index);
                    index++;
                }
            }
        }
    }

    // https://stackoverflow.com/questions/14974033/extract-digits-from-string-stringutils-java
    public static int getDigits(String x) {
        String numberOnly = x.replaceAll("[^0-9]", "");
        return Integer.parseInt(numberOnly);
    }

    /*
     Tours are return with the first city=0, this method adds 1 to each to meet the format needed
     */
    public static void convertTour() {
        for (int i = 0; i < size; i++) {
            tour[i] = tour[i] + 1;
        }
    }

    public static void runAnnealing() throws IOException {
        Annealing anneal = new Annealing(distanceMatrix, size);
        anneal.run();
        tour = anneal.getBestTour();
        length = anneal.getBestLength();

    }

    public static void printToConsole(String fileName) {
        System.out.println("NAME = " + "AISearchfile" + fileName + ".txt" + ",");
        System.out.println("TOURSIZE = " + size + ",");
        System.out.println("LENGTH = " + length + ",");
        String cities = "";
        for (int city : tour) {
            cities += city + ",";
        }
        cities = cities.substring(0, cities.length() - 1);
        System.out.println(cities);
    }

    public static void makeFile(String fileName) throws IOException {
        PrintWriter writer = new PrintWriter("tourAISearchfile" + fileName + ".txt", "UTF-8");
        writer.println("NAME = " + "AISearchfile" + fileName + ",");
        writer.println("TOURSIZE = " + size + ",");
        writer.println("LENGTH = " + length + ",");
        String cities = "";
        for (int city : tour) {
            cities += city + ",";
        }
        cities = cities.substring(0, cities.length() - 1);
        writer.println(cities);
        writer.close();
    }

    public static void runAntColony() throws IOException {
        AntColony antColony = new AntColony(distanceMatrix, size);
        antColony.run();
        tour = antColony.getBestTour();
        length = antColony.getBestLength();
    }

    public static void runAA() {
        AntAnnealing aa = new AntAnnealing(distanceMatrix, size, tour, length);
        aa.run();
        tour = aa.getBestTour();
        length = aa.getBestLength();
    }

}
