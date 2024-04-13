package main.utils.instances;

public class FastStringArrayList {
    private static int size;
    private static String[] elements;
    private int maxSize;

    public FastStringArrayList(int size) {
        this.maxSize = size;
    }

    public FastStringArrayList() {

    }

    public static String get(int find) {
        return elements[find];
    }

    public static String[] get() {
        return elements;
    }

    public void add(String toAdd) {
        if (size++ > maxSize) {
            maxSize *= 2;
            String[] newArray = new String[maxSize];
            newArray[size - 1] = toAdd;
            System.arraycopy(elements, 0, newArray, 0, size);
            elements = newArray;
        }
    }

    public void remove(String toRemove) {
        int index = -1;
        for (String k : elements) {
            index++;
            if (k == toRemove) {
                elements[index] = null;
                break;
            }
        }
    }
}
