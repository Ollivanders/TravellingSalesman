
import java.util.*;

public class AntAnnealing {
    private int[][] distanceMatrix;
    private int size;
    private int[] tour;
    private int currentLength;
    private int[] bestTour;
    private int bestLength;
    private int[] newTour;
    private int cityNumber1;
    private int cityNumber2;
    private Random random;

    public AntAnnealing(int[][] matrix, int size, int[] antTour, int antLength) {
        distanceMatrix = matrix;
        this.size = size;
        currentLength = 0;
        bestTour = replicateTour(antTour);
        bestLength = antLength;
        random = new Random();
    }

    public void run() {
        double temperature = 400;
        double cooling = 0.999999;
        double end = 0.5;
        double element = 0;
        int originalLength = 0;
        newTour = new int[size];
        tour = createTour();
        tour = shuffle(tour);
        currentLength = findLength(tour);

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

            currentLength = findLength(newTour);

            if (currentLength < originalLength) {
                tour = replicateTour(newTour);
                if (currentLength < bestLength) {
                    bestTour = replicateTour(tour);
                    bestLength = currentLength;
                }
            } else {
                //              element = Math.pow(Math.E, (originalLength - currentLength) / temperature);
                //              rand = random.nextDouble();
                //              if (rand < element) {
                //                 tour = replicateTour(newTour);
                //             } else {
                currentLength = originalLength;
            }
            System.out.println(bestLength + "    " + currentLength + "     " + temperature + "     " + element);
            temperature *= cooling;
        }
        //}
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
}