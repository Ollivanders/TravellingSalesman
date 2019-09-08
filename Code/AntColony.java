import java.util.*;
import java.io.*;

public class AntColony {
    private int[][] distanceMatrix;
    private int size;
    private int[] bestTour;
    private int bestLength;
    private ArrayList<Ant> ants;
    private double[][] pheromoneMatrix;
    private int numberOfIterations;
    private double a;
    private double b;
    private Random random;
    private double pheromoneDeposit;
    private double initalPheromone;
    private int mutationFrequency;
    private double evaporator;

    public AntColony(int[][] matrix, int size) {
        distanceMatrix = matrix;
        this.size = size;
        ants = new ArrayList<Ant>();
        pheromoneMatrix = new double[size][size];
        random = new Random();

        numberOfIterations = 10000;
        mutationFrequency = numberOfIterations / 2;
        a = 1; // pheromone exponent
        b = 2; // distance exponent
        evaporator = 0.3; // evaporation multiplication

    }

    public void run() throws IOException  {
        bestTour = createListOfCities();
        bestLength = findLength(bestTour);
        double sizeD = size; // size presented as a double
        // pheromoneDeposit = bestLength;
        initalPheromone = 1/sizeD; //inital is a rough calculation of the start tour, 1/sizeD
        pheromoneDeposit = bestLength*0.01;

        for (int it = 1; it <= 25; it++) { // number of complete restarts
            //set pheromone matrix
            for (int row = 0; row < size; row++) {
                for (int column = row; column < size; column++) {
                    if (column != row) {
                        pheromoneMatrix[row][column] = initalPheromone;
                        pheromoneMatrix[column][row] = initalPheromone;
                    }
                }
            }

            //make a list of ants
            for (int i = 0; i < 50; i++) {
                int currentCity = random.nextInt(size - 1); //establishes a start point for each ant
                Ant ant = new Ant(Integer.toString(i), currentCity, size);
                ants.add(ant);
            }

            for (int iterations = 1; iterations <= numberOfIterations; iterations++) {
                //  update inital
                //  initalPheromone = bestLength / (size + 1);

                for (Ant ant : ants) {
                    while (!ant.getUnvisited().isEmpty()) {
                        // distance from the current city
                        HashMap<Integer, Double> weights = new HashMap<Integer, Double>(); // key is city, goes to its weighting

                        for (int city : ant.getUnvisited()) {
                            double distanceFromCurrent = distanceMatrix[ant.getCurrentCity()][city];
                            double pheromoneOnPath = pheromoneMatrix[ant.getCurrentCity()][city];

                            double weighting = ((Math.pow(pheromoneOnPath, a)) * (Math.pow(1 / distanceFromCurrent, b)));
                            // double weighting = ((Math.pow(pheromoneOnPath, a)) * (Math.pow(1 / distanceFromCurrent, b)));
                            weights.put(city, weighting); // make desirability map for all in potential cities
                            // -------------------------------------------------------------------------------------------
                        }
                        int cityChosen = choseCity(weights, ant.getUnvisited());
                        ant.setCurrentCity(cityChosen);
                    }
                }

                updatePheromoneTrail(ants);// update pheromone path
                // maybe some mutation to, stop looping
                if (iterations % 500 == 0) { // evaporates every 500
                    evaporatePheromones();
                }

                //if (iterations % mutationFrequency == 0) {
                if (iterations % 2500 == 0) {
                    pheromoneMutator();
                }

                // wipe the memory of all ants
                for (Ant ant : ants) {
                    ant.resetMemory();
                }
                System.out.println(iterations + "       " + bestLength);
            }
        }
    }

    public int findLength(int[] x) {
        int length = 0;
        for (int i = 0; i < size; i++) {
            if (i != (size - 1)) {
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

    public void evaporatePheromones() {
        for (int row = 0; row < size; row++) {
            for (int column = row; column < size; column++) {
                if (column != row) {
                    pheromoneMatrix[row][column] *= evaporator;
                    pheromoneMatrix[column][row] *= evaporator;
                }
            }
        }
    }

    public void updatePheromoneTrail(ArrayList<Ant> ants) throws IOException  {
        for (Ant ant : ants) {
            int[] tour = new int[size];
            for (int i = 0; i < size; i++) {
                tour[i] = ant.getMemory().get(i);
            }
            int length = findLength(tour);
            checkBest(length, tour);

            double amount = pheromoneDeposit / length;
            //double amount = 0;
            for (int i = 0; i < size; i++) {
                if (i + 1 != size) {
                    pheromoneMatrix[tour[i]][tour[i + 1]] += amount;
                    pheromoneMatrix[tour[i + 1]][tour[i]] += amount;

                } else {
                    pheromoneMatrix[tour[i]][tour[0]] += amount;
                    pheromoneMatrix[tour[0]][tour[i]] += amount;
                }
            }
        }
    }

    public int[] createListOfCities() {
        int number = 0;
        int[] set = new int[size];
        for (int i = 0; i < size; i++) {
            set[i] = i;
        }
        return set;
    }

    public int choseCity(HashMap<Integer, Double> weights, ArrayList<Integer> cities) {
        double weightingSum = 0.0;
        ArrayList<Integer> potentialCities = cities;
        for (double weight : weights.values()) { // might need to check if null
            weightingSum += weight;
        }
        //double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
        double rand = weightingSum * random.nextDouble();
        //uses weighted random
        for (int city : potentialCities) {
            if (rand <= weights.get(city))
                return city;
            rand -= weights.get(city);
        }
        return potentialCities.get(0);
    }

    public void checkBest(int currentLength, int[] currentTour) throws IOException {
        if (currentLength < bestLength) {
            bestTour = currentTour;
            bestLength = currentLength;
            printBestTour();
            makeFile();
            pheromoneDeposit = bestLength*0.01;
        }
    }

    public void pheromoneMutator() {
        // loops through the matrix and multiplies the pheromone by a random number between 0 and 1
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                double reductor = random.nextDouble();
                pheromoneMatrix[x][y] *= reductor;
                pheromoneMatrix[y][x] *= reductor;
            }
        }

        /*
        // code for quartile method
        ArrayList<Double> edges = new ArrayList<Double>();
        for (int row = 0; row < size; row++) {
            for (int column = row; column < size; column++) {
                if (column != row) {
                    edges.add(pheromoneMatrix[row][column]);
                }
            }
        }
        Collections.sort(edges);
        int index = (edges.size() / 100) * 50 - 1;
        double average = edges.get(index);
        // Code for the mean

        int sum = 0;
        int edges = 0;
        for (int row = 0; row < size; row++) {
            for (int column = row; column < size; column++) {
                if (column != row) {
                    sum += pheromoneMatrix[row][column];
                    edges++;
                }
            }
        }
        double average = sum / edges;

        double upper = initalPheromone * 1.2;
        double lower = initalPheromone * 0.8;
        for (int row = 0; row < size; row++) {
            for (int column = row; column < size; column++) {
                if (column != row) {

                    if (pheromoneMatrix[row][column] > average) {
                        pheromoneMatrix[row][column] = upper;
                        pheromoneMatrix[column][row] = upper;
                    } else {
                        pheromoneMatrix[row][column] = lower;
                        pheromoneMatrix[column][row] = lower;
                    }
                }
            }
        }
        */
    }

    public int[] replicateTour(int[] x) {
        int[] y = new int[size];
        for (int i = 0; i < x.length; i++) {
            y[i] = x[i];
        }
        return y;
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