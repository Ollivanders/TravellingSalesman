import java.util.*;
import java.io.*;

public class Annealing {
    private int[][] distanceMatrix;
    private int size;
    private int[] tour;
    private int currentLength;
    private int[] bestTour;
    private int bestLength;
    private int[] newTour;
    private int cityNumber1;
    private int cityNumber2;
    private int originalLength;
    private Random random;
    private double temperature;
    private int subOptimalMax;


    public Annealing(int[][] matrix, int size) {
        distanceMatrix = matrix;
        this.size = size;
        currentLength = 0;
        random = new Random();
    }

    public void run() throws IOException {
        PrintWriter writer = new PrintWriter("SearchResults.txt", "UTF-8");
        String cities = "";
        double setTemperature = 500;
        double cooling = 0.99999999;
        double end = 0.5;
        double element = 0;
        int iterations = 0;
        originalLength = 0;
        newTour = new int[size];
        tour = new int[size];
        subOptimalMax = 100000;


        // Best first search used to generate initial tour
        ArrayList<Integer> potentialCities = new ArrayList<Integer>();
        for (int city = 1; city < size; city++) {
            potentialCities.add(city);
        }
        tour[0] = 0;
        for (int i = 0; i < size - 1; i++) {
            int currentCity = tour[i];
            int nextCity = potentialCities.get(0);
            int shortestDistance = distanceMatrix[currentCity][potentialCities.get(0)];
            for (int city : potentialCities) {
                if (distanceMatrix[currentCity][city] < shortestDistance) {
                    shortestDistance = distanceMatrix[currentCity][city];
                    nextCity = city;
                }
            }
            potentialCities.remove(Integer.valueOf(nextCity));
            tour[i] = nextCity;
        }

/*
        // Shuffle for initial tour
        tour = createTour();
        tour = shuffle(tour);
        */

        currentLength = findLength(tour);
        bestLength = currentLength;
        bestTour = replicateTour(tour);

        // int randomNum = rand.nextInt((max - min) + 1) + min;
        for (int loop1 = 1; loop1 <= 50; loop1++) { // number of complete restarts
            tour = createTour();
            tour = shuffle(tour);
            currentLength = findLength(tour);

            for (int loop2 = 1; loop2 <= 1; loop2++) { // reset back to best tour
                // int subOptimal = 0;
                currentLength = bestLength;
                tour = replicateTour(bestTour);
                System.out.println("========================================");
                temperature = setTemperature;

                while (temperature > end) {
                    originalLength = currentLength;
                    newTour = replicateTour(tour);

                    cityNumber1 = random.nextInt(size);
                    cityNumber2 = random.nextInt(size);
                    while (cityNumber1 == cityNumber2) {
                        cityNumber2 = random.nextInt(size);
                    }
                    newTour[cityNumber1] = tour[cityNumber2];
                    newTour[cityNumber2] = tour[cityNumber1];

                    updateAfterSwitch();
                    //  currentLength = findLength(newTour);

                    if (currentLength < originalLength) {
                        tour = replicateTour(newTour);
                        if (currentLength < bestLength) {
                            bestTour = replicateTour(tour);
                            bestLength = currentLength;
                            //       subOptimal = 0;
                            printBestTour();
                            makeFile();
                        }
                    } else {
                        element = Math.pow(Math.E, (originalLength - currentLength) / temperature);
                        if (random.nextDouble() < element) {
                            tour = replicateTour(newTour);
                            //   subOptimal = 0;
                        } else {
                            currentLength = originalLength;
                            //     subOptimal++;
                            //    if (subOptimal == subOptimalMax) {
                            //        break;
                            //    }
                        }
                    }
                    iterations++;
                    //  System.out.println(iterations + "    " + currentLength + "     " + temperature + "     " + element
                    //        + "  " + bestLength + "    " + loop1 + "    " + loop2);
                    temperature *= cooling;
                    if (iterations % 1000 == 0) {
                        //      writer.println(iterations + " " + temperature + " " + currentLength + " " + bestLength);
                    }
                }

            }
        }
        writer.close();
    }

    public int findLength(int[] x) {
        int length = 0;
        for (int i = 0; i < size; i++) {
            if (i != (x.length - 1)) {
                length += distanceMatrix[x[i]][x[i + 1]];
            } else {
                length += distanceMatrix[x[0]][x[i]];
            }
        }
        return length;
    }

    public int[] getBestTour() {
        return bestTour;
    }

    public int getBestLength() {
        return bestLength;
    }

    public int[] createTour() {
        int number = 0;
        int[] set = new int[size];
        for (int i = 0; i < size; i++) {
            set[i] = i;
        }
        return set;
    }

    public int[] replicateTour(int[] x) {
        int[] y = new int[size];
        for (int i = 0; i < x.length; i++) {
            y[i] = x[i];
        }
        return y;
    }

    public void updateAfterSwitch() {
        int originalDistance = 0; // stores original either side distance
        int currentDistance = 0; // stores the new either side

        // Check if next to each other, isNegative returns the other side so if 0 and size-1 are next to each other
        if ((isNegative(cityNumber1 - 1) == cityNumber2) || (isNegative(cityNumber2 - 1) == cityNumber1)) {
            if (cityNumber1 < cityNumber2) { // city 1 smallest
                if (cityNumber1 == 0) {
                    if (cityNumber2 == size - 1) { // 1 is 0 and 2 is the max
                        originalDistance += distanceMatrix[tour[0]][tour[1]];
                        originalDistance += distanceMatrix[tour[size - 1]][tour[size - 2]];
                        currentDistance += distanceMatrix[newTour[0]][newTour[1]];
                        currentDistance += distanceMatrix[newTour[size - 1]][newTour[size - 2]];
                    } else {
                        originalDistance += distanceMatrix[tour[0]][tour[size - 1]];
                        originalDistance += distanceMatrix[tour[cityNumber2]][tour[cityNumber2 + 1]];
                        currentDistance += distanceMatrix[newTour[0]][newTour[size - 1]];
                        currentDistance += distanceMatrix[newTour[cityNumber2]][newTour[cityNumber2 + 1]];
                    }
                } else {
                    if (cityNumber2 == size - 1) {
                        originalDistance += distanceMatrix[tour[cityNumber1]][tour[cityNumber1 - 1]];
                        originalDistance += distanceMatrix[tour[size - 1]][tour[0]];
                        currentDistance += distanceMatrix[newTour[cityNumber1]][newTour[cityNumber1 - 1]];
                        currentDistance += distanceMatrix[newTour[size - 1]][newTour[0]];
                    } else {
                        originalDistance += distanceMatrix[tour[cityNumber1]][tour[cityNumber1 - 1]];
                        originalDistance += distanceMatrix[tour[cityNumber2]][tour[cityNumber2 + 1]];
                        currentDistance += distanceMatrix[newTour[cityNumber1]][newTour[cityNumber1 - 1]];
                        currentDistance += distanceMatrix[newTour[cityNumber2]][newTour[cityNumber2 + 1]];
                    }
                }
            } else { // city 2 smallest
                if (cityNumber2 == 0) {
                    if (cityNumber1 == size - 1) {
                        originalDistance += distanceMatrix[tour[0]][tour[1]];
                        originalDistance += distanceMatrix[tour[size - 1]][tour[size - 2]];
                        currentDistance += distanceMatrix[newTour[0]][newTour[1]];
                        currentDistance += distanceMatrix[newTour[size - 1]][newTour[size - 2]];
                    } else {
                        originalDistance += distanceMatrix[tour[0]][tour[size - 1]];
                        originalDistance += distanceMatrix[tour[cityNumber1]][tour[cityNumber1 + 1]];
                        currentDistance += distanceMatrix[newTour[0]][newTour[size - 1]];
                        currentDistance += distanceMatrix[newTour[cityNumber1]][newTour[cityNumber1 + 1]];
                    }
                } else {
                    if (cityNumber1 == size - 1) {
                        originalDistance += distanceMatrix[tour[cityNumber2]][tour[cityNumber2 - 1]];
                        originalDistance += distanceMatrix[tour[size - 1]][tour[0]];
                        currentDistance += distanceMatrix[newTour[cityNumber2]][newTour[cityNumber2 - 1]];
                        currentDistance += distanceMatrix[newTour[size - 1]][newTour[0]];
                    } else {
                        originalDistance += distanceMatrix[tour[cityNumber2]][tour[cityNumber2 - 1]];
                        originalDistance += distanceMatrix[tour[cityNumber1]][tour[cityNumber1 + 1]];
                        currentDistance += distanceMatrix[newTour[cityNumber2]][newTour[cityNumber2 - 1]];
                        currentDistance += distanceMatrix[newTour[cityNumber1]][newTour[cityNumber1 + 1]];
                    }
                }
            }
        } else { // cities are not next to each other
            if (cityNumber1 < cityNumber2) { // city 1 smallest
                if (cityNumber1 == 0) {
                    originalDistance += distanceMatrix[tour[0]][tour[cityNumber1 + 1]];
                    originalDistance += distanceMatrix[tour[0]][tour[size - 1]];
                    originalDistance += distanceMatrix[tour[cityNumber2]][tour[cityNumber2 - 1]];
                    originalDistance += distanceMatrix[tour[cityNumber2]][tour[cityNumber2 + 1]];
                    currentDistance += distanceMatrix[newTour[0]][newTour[cityNumber1 + 1]];
                    currentDistance += distanceMatrix[newTour[0]][newTour[size - 1]];
                    currentDistance += distanceMatrix[newTour[cityNumber2]][newTour[cityNumber2 - 1]];
                    currentDistance += distanceMatrix[newTour[cityNumber2]][newTour[cityNumber2 + 1]];
                } else if (cityNumber2 == size - 1) {
                    originalDistance += distanceMatrix[tour[size - 1]][tour[0]];
                    originalDistance += distanceMatrix[tour[size - 2]][tour[size - 1]];
                    originalDistance += distanceMatrix[tour[cityNumber1]][tour[cityNumber1 - 1]];
                    originalDistance += distanceMatrix[tour[cityNumber1]][tour[cityNumber1 + 1]];
                    currentDistance += distanceMatrix[newTour[size - 1]][newTour[0]];
                    currentDistance += distanceMatrix[newTour[size - 2]][newTour[size - 1]];
                    currentDistance += distanceMatrix[newTour[cityNumber1]][newTour[cityNumber1 - 1]];
                    currentDistance += distanceMatrix[newTour[cityNumber1]][newTour[cityNumber1 + 1]];
                } else {
                    originalDistance += distanceMatrix[tour[cityNumber1]][tour[cityNumber1 + 1]];
                    originalDistance += distanceMatrix[tour[cityNumber1]][tour[cityNumber1 - 1]];
                    originalDistance += distanceMatrix[tour[cityNumber2]][tour[cityNumber2 + 1]];
                    originalDistance += distanceMatrix[tour[cityNumber2]][tour[cityNumber2 - 1]];
                    currentDistance += distanceMatrix[newTour[cityNumber1]][newTour[cityNumber1 + 1]];
                    currentDistance += distanceMatrix[newTour[cityNumber1]][newTour[cityNumber1 - 1]];
                    currentDistance += distanceMatrix[newTour[cityNumber2]][newTour[cityNumber2 + 1]];
                    currentDistance += distanceMatrix[newTour[cityNumber2]][newTour[cityNumber2 - 1]];
                }
            } else { // city 2 smallest
                if (cityNumber2 == 0) {
                    originalDistance += distanceMatrix[tour[0]][tour[1]];
                    originalDistance += distanceMatrix[tour[0]][tour[size - 1]];
                    originalDistance += distanceMatrix[tour[cityNumber1]][tour[cityNumber1 - 1]];
                    originalDistance += distanceMatrix[tour[cityNumber1]][tour[cityNumber1 + 1]];
                    currentDistance += distanceMatrix[newTour[0]][newTour[1]];
                    currentDistance += distanceMatrix[newTour[0]][newTour[size - 1]];
                    currentDistance += distanceMatrix[newTour[cityNumber1]][newTour[cityNumber1 - 1]];
                    currentDistance += distanceMatrix[newTour[cityNumber1]][newTour[cityNumber1 + 1]];

                } else if (cityNumber1 == size - 1) {
                    originalDistance += distanceMatrix[tour[size - 1]][tour[0]];
                    originalDistance += distanceMatrix[tour[size - 2]][tour[size - 1]];
                    originalDistance += distanceMatrix[tour[cityNumber2]][tour[cityNumber2 - 1]];
                    originalDistance += distanceMatrix[tour[cityNumber2]][tour[cityNumber2 + 1]];
                    currentDistance += distanceMatrix[newTour[size - 1]][newTour[0]];
                    currentDistance += distanceMatrix[newTour[size - 2]][newTour[size - 1]];
                    currentDistance += distanceMatrix[newTour[cityNumber2]][newTour[cityNumber2 - 1]];
                    currentDistance += distanceMatrix[newTour[cityNumber2]][newTour[cityNumber2 + 1]];
                } else {
                    originalDistance += distanceMatrix[tour[cityNumber1]][tour[cityNumber1 + 1]];
                    originalDistance += distanceMatrix[tour[cityNumber1]][tour[cityNumber1 - 1]];
                    originalDistance += distanceMatrix[tour[cityNumber2]][tour[cityNumber2 + 1]];
                    originalDistance += distanceMatrix[tour[cityNumber2]][tour[cityNumber2 - 1]];
                    currentDistance += distanceMatrix[newTour[cityNumber1]][newTour[cityNumber1 + 1]];
                    currentDistance += distanceMatrix[newTour[cityNumber1]][newTour[cityNumber1 - 1]];
                    currentDistance += distanceMatrix[newTour[cityNumber2]][newTour[cityNumber2 + 1]];
                    currentDistance += distanceMatrix[newTour[cityNumber2]][newTour[cityNumber2 - 1]];
                }
            }
        }
        currentLength = originalLength - originalDistance + currentDistance;
    }

    public int isNegative(int value) { // makes sure when swapping cities, result is not negative, returns other side
        if (value < 0) {
            return size - 1; // remove negative
        }
        return value;
    }

    public int[] shuffle(int[] shuffleArray) {
        int buffer = 0;
        for (int i = 0; i < size; i++) {
            int newPosition = i + random.nextInt(size - i);
            buffer = shuffleArray[i];
            shuffleArray[i] = shuffleArray[newPosition];
            shuffleArray[newPosition] = buffer;
        }
        return shuffleArray;
    }

    public void printBestTour() {
        System.out.println("TOURSIZE = " + size + ",");
        System.out.println("LENGTH = " + bestLength + ",");
        String cities = "";
        int[] tourProcess = convertTour(replicateTour(bestTour));
        for (int city : tourProcess) {
            cities += city + ",";
        }
        cities = cities.substring(0, cities.length() - 1);
        System.out.println(cities);
    }

    public int[] convertTour(int[] tourProcess) {
        for (int i = 0; i < size; i++) {
            tourProcess[i] = tourProcess[i] + 1;
        }
        return tourProcess;
    }

    public void makeFile() throws IOException {
        PrintWriter writer = new PrintWriter("tourAISearchfile" + size + ".txt", "UTF-8");
        writer.println("NAME = " + "AISearchfile" + size + ",");
        writer.println("TOURSIZE = " + size + ",");
        writer.println("LENGTH = " + bestLength + ",");
        String cities = "";
        for (int city : convertTour(replicateTour(bestTour))) {
            cities += city + ",";
        }
        cities = cities.substring(0, cities.length() - 1);
        writer.println(cities);
        writer.close();
    }

}