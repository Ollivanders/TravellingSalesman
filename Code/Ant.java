import java.lang.reflect.Array;
import java.util.ArrayList;

public class Ant {
    private String name;
    private ArrayList<Integer> memory;
    private ArrayList<Integer> unvisited;
    private int currentCity;
    private int size; // number of cities

    public Ant(String name, int currentCity, int size) {
        this.name = name;
        this.size = size;
        resetMemory();
        setCurrentCity(currentCity);
    }

    public void resetMemory() {
        memory = new ArrayList<Integer>();
        setUnvisited();
    }

    public ArrayList<Integer> getMemory() {
        return memory;
    }

    public int getCurrentCity() {
        return currentCity;
    }

    public void setCurrentCity(int currentCity) {
        this.currentCity = currentCity;
        memory.add(currentCity); // adds to where it has been
        unvisited.remove(Integer.valueOf(currentCity)); // takes away from where it could go
    }

    public ArrayList<Integer> getUnvisited() {
        return unvisited;
    }

    public void setUnvisited() {
        unvisited = new ArrayList<Integer>();
        for (int i = 0; i < size; i++) {
            unvisited.add(i);
        }
    }

    public String getName() {
        return name;
    }
}