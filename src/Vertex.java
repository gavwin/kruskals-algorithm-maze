import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

// represents a vertex in the Maze
class Vertex extends Cell {
  ArrayList<Edge> outsideEdges;
  boolean visited;
  boolean onPath;
  int id;

  // main constructor
  Vertex(int i, int j, int id) {
    super(i, j, Settings.CELL_COLOR);
    this.outsideEdges = new ArrayList<Edge>();
    this.visited = false;
    this.onPath = false;
    this.id = id;
  }

  // override the default equals method so that we can compare the two Posns
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(this instanceof Vertex)) {
      return false;
    }

    Vertex v = (Vertex) o;
    return this.posn.equals(v.posn);
  }

  // override the default hashCode method so that we can return the id of this
  // Vertex (each Vertex will have a unique id)
  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  // add all of the given Edges to this.outsideEdges
  void addEdges(ArrayList<ArrayList<Vertex>> vertices) {
    this.addEdges(vertices, new Random());
  }

  // add all of the given Edges to this.outsideEdges with a given Random object
  void addEdges(ArrayList<ArrayList<Vertex>> vertices, Random rand) {
    int i = this.posn.x;
    int j = this.posn.y;

    if (i != 0) {
      this.outsideEdges.add(new Edge(this, vertices.get(i - 1).get(j), rand));
    }
    if (i != vertices.size() - 1) {
      this.outsideEdges.add(new Edge(this, vertices.get(i + 1).get(j), rand));
    }
    if (j != 0) {
      this.outsideEdges.add(new Edge(this, vertices.get(i).get(j - 1), rand));
    }
    if (j != vertices.get(0).size() - 1) {
      this.outsideEdges.add(new Edge(this, vertices.get(i).get(j + 1), rand));
    }
  }

  // makes this Vertex "visited"
  void makeVisited() {
    this.visited = true;
    this.color = Settings.VISITED_COLOR;
  }
}