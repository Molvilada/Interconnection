package model.data_structures;

public class Edge<K extends Comparable<K>, V extends Comparable<V>> implements Comparable<Edge<K, V>> {
    private final Vertex<K, V> source;
    private final Vertex<K, V> destination;
    private float weight;

    public Edge(Vertex<K, V> source, Vertex<K, V> destination, float weight) {
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    public Vertex<K, V> getSource() {
        return source;
    }

    public Vertex<K, V> getDestination() {
        return destination;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    @Override
    public int compareTo(Edge o) {
        // TODO Auto-generated method stub
        return 0;
    }
}

