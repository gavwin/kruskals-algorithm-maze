import java.util.Objects;
import java.util.Random;
import javalib.impworld.WorldScene;
import javalib.worldimages.*;

// represents an Edge in the Maze
class Edge {
  Vertex from;
  Vertex to;
  int weight;

  // constructor with random weight
  Edge(Vertex from, Vertex to) {
    this.from = from;
    this.to = to;
    Random rand = new Random();
    this.weight = rand.nextInt(Settings.BOARD_WIDTH * Settings.BOARD_HEIGHT * 25);
  }

  // constructor with Random object
  Edge(Vertex from, Vertex to, Random rand) {
    this.from = from;
    this.to = to;
    this.weight = rand.nextInt(Settings.BOARD_WIDTH * Settings.BOARD_HEIGHT * 25);
  }

  // constructor with given weight
  Edge(Vertex from, Vertex to, int weight) {
    this.from = from;
    this.to = to;
    this.weight = weight;
  }

  // override the default equals method
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(this instanceof Edge)) {
      return false;
    }

    Edge e = (Edge) o;
    // the || statement allows us to ignore edge weight so we can properly generate
    // the Maze
    return (this.from.equals(e.from) && this.to.equals(e.to) && this.weight == e.weight)
        || (this.to == e.from && this.from == e.to);
  }

  // override the default hashCode method
  @Override
  public int hashCode() {
    return Objects.hash(this.from.posn.x, this.from.posn.y);
  }

  // draws this Edge onto the given WorldScene
  void addToScene(WorldScene scene) {
    if (this.from.posn.x != this.to.posn.x) {
      // vertical line
      scene.placeImageXY(this.getLine(true),
          (this.to.posn.x + this.from.posn.x) * Settings.VERTEX_SIZE / 2 + Settings.VERTEX_SIZE / 2,
          this.from.posn.y * Settings.VERTEX_SIZE + Settings.VERTEX_SIZE / 2);
    } else {
      // horizontal line
      scene.placeImageXY(this.getLine(false),
          this.from.posn.x * Settings.VERTEX_SIZE + Settings.VERTEX_SIZE / 2,
          (this.to.posn.y + this.from.posn.y) * Settings.VERTEX_SIZE / 2
              + Settings.VERTEX_SIZE / 2);
    }
  }

  // returns a WorldImage that represents either a vertical or horizontal line
  // when direction is true, return a vertical line
  // when direction is false, return a horizontal line
  WorldImage getLine(boolean direction) {
    Posn posn = direction ? new Posn(0, Settings.VERTEX_SIZE) : new Posn(Settings.VERTEX_SIZE, 0);
    return new LineImage(posn, Settings.EDGE_COLOR);
  }
}