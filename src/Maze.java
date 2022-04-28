import java.util.*;
import javalib.impworld.WorldScene;

// represents a Maze
class Maze {
  ArrayList<ArrayList<Vertex>> vertices;
  ArrayList<Edge> edges;
  ArrayList<Edge> walls;
  HashMap<Integer, Edge> cameFromEdgeDFS;
  HashMap<Integer, Edge> cameFromEdgeBFS;
  Stack<Vertex> worklistDFS;
  Queue<Vertex> worklistBFS;
  Vertex current;

  // main constructor that uses the given Random
  Maze(int width, int height, Random rand) {
    this.vertices = new ArrayList<ArrayList<Vertex>>();
    int counter = 0;

    // initialize all of the vertices
    for (int i = 0; i < width; i++) {
      ArrayList<Vertex> sublist = new ArrayList<Vertex>();
      counter++;
      for (int j = 0; j < height; j++) {
        sublist.add(new Vertex(i, j, counter));
        counter++;
      }

      this.vertices.add(sublist);
    }

    // set the color of the start and finish
    this.vertices.get(0).get(0).color = Settings.START_COLOR;
    this.vertices.get(width - 1).get(height - 1).color = Settings.FINISH_COLOR;

    // generate all of the edges
    this.edges = new ArrayList<Edge>();
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        this.vertices.get(i).get(j).addEdges(this.vertices, rand);
        this.edges.addAll(vertices.get(i).get(j).outsideEdges);
      }
    }

    this.walls = new ArrayList<Edge>();

    // run kruskal's algorithm
    this.kruskals();

    // set this.walls to the actual walls of the Maze
    this.walls = this.getFinalEdges(this.edges, this.vertices);

    // make sure the Maze is solvable (using dfs)
  }

  // convenience constructor
  Maze(int width, int height) {
    this(width, height, new Random());
  }

  // is the given edge contained in any of the vertices' outsideEdges?
  boolean containedInOutsideEdges(Edge e, ArrayList<ArrayList<Vertex>> vertices) {
    boolean result = false;
    for (ArrayList<Vertex> list : vertices) {
      for (Vertex v : list) {
        if (v.outsideEdges.contains(e)) {
          result = true;
        }
      }
    }

    return result;
  }

  // returns the edges of the final Maze
  ArrayList<Edge> getFinalEdges(ArrayList<Edge> edges, ArrayList<ArrayList<Vertex>> vertices) {
    ArrayList<Edge> walls = new ArrayList<Edge>();
    for (Edge e : edges) {
      if (!this.containedInOutsideEdges(e, vertices)) {
        walls.add(e);
      }
    }

    return walls;
  }

  // runs kruskal's algorithm
  void kruskals() {
    HashMap<Integer, Integer> reps = new HashMap<>();
    ArrayList<Edge> edges = new ArrayList<Edge>();

    for (ArrayList<Vertex> list : vertices) {
      for (Vertex v : list) {
        for (Edge e : v.outsideEdges) {
          edges.add(e);
        }
      }
    }

    this.sortEdges(edges);

    // fill the HashMap
    for (ArrayList<Vertex> list : this.vertices) {
      for (Vertex v : list) {
        v.outsideEdges = new ArrayList<Edge>();
        reps.put(v.id, v.id);
      }
    }

    // kruskal's
    while (numTrees(reps) > 1) {
      Edge edge = edges.get(0);
      if (this.find(reps, edge.from.id) == this.find(reps, edge.to.id)) {
        edges.remove(0);
      } else {
        this.union(reps, find(reps, edge.from.id), this.find(reps, edge.to.id));
        edge.from.outsideEdges.add(edge);
        edge.to.outsideEdges.add(new Edge(edge.to, edge.from, edge.weight));
      }
    }
  }

  // runs depth-first search
  void dfs() {
    Vertex next = this.worklistDFS.pop();

    if (next.posn.x == Settings.BOARD_WIDTH - 1 && next.posn.y == Settings.BOARD_HEIGHT - 1) {
      // reconstruct
      next.makeVisited();
      this.current = this.vertices.get(Settings.BOARD_WIDTH - 1).get(Settings.BOARD_HEIGHT - 1);
    } else if (!next.visited) {
      next.makeVisited();
      for (Edge e : next.outsideEdges) {
        if (!e.from.equals(next) && !e.from.visited) {
          this.worklistDFS.push(e.from);
          this.cameFromEdgeDFS.put(e.from.id, e);
        } else if (!e.to.equals(next) && !e.to.visited) {
          this.worklistDFS.push(e.to);
          this.cameFromEdgeDFS.put(e.to.id, e);
        }
      }
    }
  }

  // runs breadth-first search
  void bfs() {
    Vertex next = this.worklistBFS.poll();

    if (next.posn.x == Settings.BOARD_WIDTH - 1 && next.posn.y == Settings.BOARD_HEIGHT - 1) {
      // reconstruct
      next.makeVisited();
      this.current = this.vertices.get(Settings.BOARD_WIDTH - 1).get(Settings.BOARD_HEIGHT - 1);
    } else if (!next.visited) {
      next.makeVisited();
      for (Edge e : next.outsideEdges) {
        if (!e.from.equals(next) && !e.from.visited) {
          this.worklistBFS.add(e.from);
          this.cameFromEdgeBFS.put(e.from.id, e);
        } else if (!e.to.equals(next) && !e.to.visited) {
          this.worklistBFS.add(e.to);
          this.cameFromEdgeBFS.put(e.to.id, e);
        }
      }
    }
  }

  // reset class members for either dfs or bfs based on the given type
  // if true, .push to worklist for dfs
  // if false, .add to worklist for dfs
  void prep(boolean type) {
    if (type) {
      this.cameFromEdgeDFS = new HashMap<Integer, Edge>();
      this.worklistDFS = new Stack<Vertex>();

      this.prepHelper();

      this.worklistDFS.push(this.vertices.get(0).get(0));
      this.current = null;
    } else {
      this.cameFromEdgeBFS = new HashMap<Integer, Edge>();
      this.worklistBFS = new LinkedList<Vertex>();

      this.prepHelper();

      this.worklistBFS.add(this.vertices.get(0).get(0));
      this.current = null;
    }
  }

  // helper for the prep method to abstract the loops through vertices
  void prepHelper() {
    for (ArrayList<Vertex> list : this.vertices) {
      for (Vertex v : list) {
        v.visited = false;
      }
    }
  }

  // reconstruct for either dfs or bfs based on the given boolean
  // if true, reconstruct for dfs
  // if false, reconstruct for bfs
  void reconstruct(boolean type) {
    this.current.color = Settings.SOLUTION_COLOR;
    this.current.onPath = true;
    Edge e = type ? this.cameFromEdgeDFS.get(this.current.id)
        : this.cameFromEdgeBFS.get(this.current.id);
    this.current = e.from;
  }

  // return the number of trees in the HashMap
  int numTrees(HashMap<Integer, Integer> reps) {
    int count = 0;

    for (Map.Entry<Integer, Integer> entry : reps.entrySet()) {
      if (entry.getValue() == entry.getKey()) {
        count++;
      }
    }

    return count;
  }

  // join the two keys into the same minimum spanning tree
  void union(HashMap<Integer, Integer> representatives, int key1, int key2) {
    representatives.put(key2, key1);
  }

  // return the representative of the given key that refers to a Vertex
  int find(HashMap<Integer, Integer> representatives, int key) {
    return key == (representatives.get(key)) ? key
        : this.find(representatives, representatives.get(key));
  }

  // runs selection sort on the given ArrayList
  void sortEdges(ArrayList<Edge> edges) {
    for (int i = 0; i < edges.size() - 1; i++) {
      int min = i;
      for (int j = i + 1; j < edges.size(); j++) {
        if (edges.get(j).weight < edges.get(min).weight) {
          min = j;
        }
      }

      Edge tempEdge = edges.get(i);
      edges.set(i, edges.get(min));
      edges.set(min, tempEdge);
    }
  }

  // draw all of the vertices' and edges' images on the given WorldScene
  void drawMaze(WorldScene scene, Player player) {
    for (ArrayList<Vertex> list : this.vertices) {
      for (Vertex v : list) {
        v.addToScene(scene);
      }
    }

    if (player != null) {
      player.addToScene(scene);
    }

    for (Edge e : this.walls) {
      e.addToScene(scene);
    }
  }

  // reset the Maze tile colors
  void reset() {
    for (ArrayList<Vertex> list : this.vertices) {
      for (Vertex v : list) {
        v.color = Settings.CELL_COLOR;
      }
    }
    this.vertices.get(0).get(0).color = Settings.START_COLOR;
    this.vertices.get(Settings.BOARD_WIDTH - 1)
        .get(Settings.BOARD_HEIGHT - 1).color = Settings.FINISH_COLOR;
  }

  // mark that the Maze is completed
  void setCompleted() {
    this.current.color = Settings.VISITED_COLOR;
    this.current.onPath = true;
  }

  // has this Maze been solved?
  boolean isSolved() {
    return this.current != null && this.current.equals(this.vertices.get(0).get(0));
  }
}