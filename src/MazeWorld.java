import java.awt.Color;
import java.util.*;
import javalib.impworld.*;
import javalib.worldimages.*;
import tester.Tester;

public class Main {
  // for running the game
  public static void main(String[] args) {
    MazeWorld world = new MazeWorld();
    world.bigBang(Settings.WINDOW_WIDTH, Settings.WINDOW_HEIGHT + Settings.VERTEX_SIZE * 2,
        Settings.TICK_RATE);
  }
}

// represents the World class for the Maze game
class MazeWorld extends World {
  Maze maze;
  Player player;
  String mode;

  // main constructor
  MazeWorld() {
    this.maze = new Maze(Settings.BOARD_WIDTH, Settings.BOARD_HEIGHT);
    this.player = new Player(0, 0);
    this.mode = "player";
  }

  // draws all of the elements of the maze
  public WorldScene makeScene() {
    WorldScene scene = this.getEmptyScene();
    this.maze.drawMaze(scene, this.player); // also draws player
    
    // check if either the player or dfs/bfs solved the maze
    // and render the endScene accordingly
    if (this.player.isCompleted()) {
      this.drawEndScene(scene);
    } else if (this.maze.isSolved()) {
      this.drawEndScene(scene);
    }

    return scene;
  }

  // run either dfs or bfs based on the given boolean
  // if true, run dfs
  // if false, run bfs
  void run(boolean type) {
    int size = type ? this.maze.worklistDFS.size() : this.maze.worklistBFS.size();
    if (size > 0 && this.maze.current == null) {
      if (type) {
        this.maze.dfs();
      } else {
        this.maze.bfs();
      }
    } else if (this.maze.current != null
        && !this.maze.current.equals(this.maze.vertices.get(0).get(0))) {
      this.maze.reconstruct(type);
    }
  }

  // handle the key inputs
  @Override
  public void onKeyEvent(String key) {
    if (key.equals("d")) {
      // switch to "dfs" mode
      this.mode = "dfs";
      this.maze.reset();
      this.maze.prep(true);
      this.player = new Player(0, 0);
    } else if (key.equals("b")) {
      // switch to "bfs" mode
      this.mode = "bfs";
      this.maze.reset();
      this.maze.prep(false);
      this.player = new Player(0, 0);
    } else if (key.equals("p")) {
      // switch to "player" mode
      this.mode = "player";
      this.maze.reset();
      this.player = new Player(0, 0);
    } else if (key.equals("n")) {
      // generate a new game
      this.maze = new Maze(Settings.BOARD_WIDTH, Settings.BOARD_HEIGHT);
      this.mode = "player";
      this.player = new Player(0, 0);
      this.maze.prep(true);
      this.maze.prep(false);
    } else if (key.equals("escape")) {
      // for closing the game easily
      System.exit(0);
    } else {
      // for moving the player with up, down, left, right arrows
      this.player.movePlayer(key, this.maze.walls);
    }
  }

  // check if either the player or dfs/bfs solved the maze
  // and render the endScene accordingly
  /*public WorldEnd worldEnds() {
    if (this.player.isCompleted()) {
      return new WorldEnd(true, this.endScene());
    } else if (this.maze.isSolved()) {
      return new WorldEnd(true, this.endScene());
    } else {
      return new WorldEnd(false, this.makeScene());
    }
  }*/

  // draws the "game over" text box onto the given WorldScene
  void drawEndScene(WorldScene scene) {
    int x = Settings.WINDOW_WIDTH / 2;
    int y = Settings.WINDOW_HEIGHT / 2;

    scene.placeImageXY(new RectangleImage(200, 75, OutlineMode.SOLID, Color.WHITE), x, y);
    scene.placeImageXY(new TextImage("Maze Complete!", 20, FontStyle.BOLD, Color.ORANGE), x, y - 5);
    scene.placeImageXY(new TextImage("Press 'n' to start a new game", 12, Color.BLACK), x, y + 15);
  }

  // use the onTick event to animate the search algorithms and player movement
  @Override
  public void onTick() {
    if (this.mode.equals("player")) {
      Vertex currentPlayerSquare = this.maze.vertices.get(this.player.posn.x)
          .get(this.player.posn.y);
      currentPlayerSquare.color = Settings.VISITED_COLOR;
      currentPlayerSquare.visited = true;
    } else if (this.mode.equals("dfs")) {
      this.run(true);
      if (this.maze.current != null && this.maze.current.equals(this.maze.vertices.get(0).get(0))) {
        this.maze.setCompleted();
      }
    } else if (this.mode.equals("bfs")) {
      this.run(false);
      if (this.maze.current != null && this.maze.current.equals(this.maze.vertices.get(0).get(0))) {
        this.maze.setCompleted();
      }
    }
  }
}

// the examples class for the Maze game to test all of our methods
class ExamplesMaze {

  MazeWorld world = new MazeWorld();
  Maze maze = new Maze(10, 10);

  HashMap<Integer, Integer> map1;
  HashMap<Integer, Integer> map2;

  Vertex testVertex1;
  Vertex testVertex2;

  Edge testEdge;

  // to initialize our conditions/data
  void initConditions() {
    this.world = new MazeWorld();
    this.world.maze = new Maze(Settings.BOARD_WIDTH, Settings.BOARD_HEIGHT, new Random(5));

    this.maze = new Maze(10, 10);

    this.testVertex1 = new Vertex(0, 0, 123);
    this.testVertex2 = new Vertex(1, 1, 456);

    this.testEdge = new Edge(this.testVertex1, this.testVertex2);

    this.map1 = new HashMap<>();
    int counter = 0;
    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < 10; j++) {
        this.map1.put(counter, counter);
        counter++;
      }
    }

    this.map2 = new HashMap<>();
    int counter2 = 0;
    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < 10; j++) {
        this.map2.put(counter2, counter2 + 1);
        counter2++;
      }
    }
  }

  // run the game
  void testGame(Tester t) {
    MazeWorld world = new MazeWorld();
    world.bigBang(Settings.WINDOW_WIDTH, Settings.WINDOW_HEIGHT + Settings.VERTEX_SIZE * 2,
        Settings.TICK_RATE);
  }

  // -- Tests for methods in the MazeWorld class --

  // tests the onKeyEvent method
  void testOnKeyEvent(Tester t) {
    Maze oldMaze = this.world.maze;

    // dfs mode tests
    this.initConditions();
    t.checkExpect(this.world.mode, "player");

    this.world.onKeyEvent("d");
    t.checkExpect(this.world.mode, "dfs");

    oldMaze = this.world.maze;
    this.world.onKeyEvent("n");
    t.checkExpect(this.world.mode, "player");
    t.checkFail(this.world.maze, oldMaze);

    // bfs mode tests
    this.initConditions();
    t.checkExpect(this.world.mode, "player");

    this.world.onKeyEvent("b");
    t.checkExpect(this.world.mode, "bfs");

    oldMaze = this.world.maze;
    this.world.onKeyEvent("n");
    t.checkExpect(this.world.mode, "player");
    t.checkFail(this.world.maze, oldMaze);

    // player mode tests
    this.initConditions();
    t.checkExpect(this.world.mode, "player");

    this.world.onKeyEvent("n");
    t.checkExpect(this.world.mode, "player");
    t.checkFail(this.world.maze, oldMaze);

    // test some invalid inputs
    this.initConditions();
    t.checkExpect(this.world.mode, "player");
    t.checkExpect(this.world.player.posn, new Posn(0, 0));

    this.world.onKeyEvent("y");
    this.world.onKeyEvent("a");
    this.world.onKeyEvent("%");
    this.world.onKeyEvent("shift");

    // make sure those random key events didn't move the player
    // or change the "game mode"
    t.checkExpect(this.world.mode, "player");
    t.checkExpect(this.world.player.posn, new Posn(0, 0));
  }

  // -- Tests for methods in the Maze class --

  // tests the containedInOutEdges method
  void testContainedInOutEdges(Tester t) {
    ArrayList<ArrayList<Vertex>> vertices = new ArrayList<ArrayList<Vertex>>();
    for (int i = 0; i < 10; i++) {
      ArrayList<Vertex> sublist = new ArrayList<Vertex>();
      for (int j = 0; j < 10; j++) {
        Vertex v = new Vertex(i, j, 0);
        v.outsideEdges.add(new Edge(v, v, 0));
        sublist.add(v);
      }
      vertices.add(sublist);

    }

    t.checkExpect(this.maze.containedInOutsideEdges(
        new Edge(vertices.get(0).get(0), vertices.get(0).get(0), 0), vertices), true);
    t.checkExpect(this.maze.containedInOutsideEdges(
        new Edge(vertices.get(1).get(1), vertices.get(0).get(0), 0), vertices), false);
    t.checkExpect(this.maze.containedInOutsideEdges(
        new Edge(vertices.get(5).get(5), vertices.get(5).get(5), 0), vertices), true);
    // ignore weights
    t.checkExpect(this.maze.containedInOutsideEdges(
        new Edge(vertices.get(0).get(0), vertices.get(0).get(0), 5), vertices), true);
  }

  // tests the getFinalEdges method
  void testGetFinalEdges(Tester t) {
    ArrayList<ArrayList<Vertex>> vertices = new ArrayList<ArrayList<Vertex>>();
    ArrayList<Edge> empty = new ArrayList<Edge>();
    ArrayList<Edge> edges1 = new ArrayList<Edge>();

    for (int i = 0; i < 10; i++) {
      ArrayList<Vertex> sublist = new ArrayList<Vertex>();
      for (int j = 0; j < 10; j++) {
        Vertex v = new Vertex(i, j, 0);
        v.outsideEdges.add(new Edge(v, v, 0));
        sublist.add(v);
      }
      vertices.add(sublist);

    }

    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < 10; j++) {
        edges1.add(new Edge(vertices.get(i).get(j), vertices.get(i).get(j), 0));
      }
    }

    t.checkExpect(this.maze.getFinalEdges(edges1, vertices), empty);
    // clear all outEdges
    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < 10; j++) {
        vertices.get(i).get(j).outsideEdges.clear();
      }
    }
    t.checkExpect(this.maze.getFinalEdges(empty, vertices), empty);
  }

  // tests the prep method
  void testPrep(Tester t) {
    // test prep on dfs
    this.initConditions();
    Maze maze1 = new Maze(10, 10);
    maze1.prep(true);
    t.checkExpect(maze1.current, null);

    // test prep on bfs
    this.initConditions();
    Maze maze2 = new Maze(10, 10);
    maze2.prep(true);
    t.checkExpect(maze2.current, null);
  }

  // tests the numTrees method
  void testNumTrees(Tester t) {
    this.initConditions();
    t.checkExpect(this.maze.numTrees(this.map1), 100);

    this.initConditions();
    t.checkExpect(this.maze.numTrees(this.map2), 0);
  }

  // tests the union method
  void testUnion(Tester t) {
    this.initConditions();
    this.maze.union(this.map1, 0, 0);
    t.checkExpect(this.map1.get(0), 0);

    this.initConditions();
    this.maze.union(this.map1, 50, 0);
    t.checkExpect(this.map1.get(0), 50);
  }

  // tests the find method
  void testFind(Tester t) {
    this.initConditions();
    t.checkExpect(this.maze.find(this.map1, 0), 0);
    t.checkExpect(this.maze.find(this.map1, 50), 50);
  }

  // tests the sortEdges method
  void testSortEdges(Tester t) {
    ArrayList<Edge> edges1 = new ArrayList<Edge>();
    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < 10; j++) {
        Vertex v = new Vertex(0, 0, 0);
        edges1.add(new Edge(v, v, i));
      }
    }
    this.maze.sortEdges(edges1);

    t.checkExpect(edges1.get(0).weight, 0);
    t.checkExpect(edges1.get(edges1.size() - 1).weight, 9);
  }

  // -- Tests for methods in the Vertex class --

  // test custom Edge equals method
  void testEdgeEquals(Tester t) {
    Vertex v1 = new Vertex(0, 0, 0);
    Vertex v2 = new Vertex(0, 1, 1);
    Vertex v3 = new Vertex(0, 1, 0);
    Vertex v4 = new Vertex(0, 1, 2);
    Edge e1 = new Edge(v1, v2);
    Edge e2 = new Edge(v1, v3);
    Edge e3 = new Edge(v2, v3);
    Edge e4 = new Edge(v2, v4);
    t.checkExpect(e1.equals(e2), false);
    t.checkExpect(e2.equals(e3), false);
    t.checkExpect(e1.equals(e3), false);
    t.checkExpect(e3.equals(e4), false);
    t.checkExpect(e4.equals(e4), true);
  }

  // tests the addEdges method
  void testAddEdges(Tester t) {
    ArrayList<ArrayList<Vertex>> vertices = new ArrayList<ArrayList<Vertex>>();
    for (int i = 0; i < 10; i++) {
      ArrayList<Vertex> sublist = new ArrayList<Vertex>();
      for (int j = 0; j < 10; j++) {
        Vertex v = new Vertex(i, j, 0);
        sublist.add(v);
      }
      vertices.add(sublist);
    }

    t.checkExpect(vertices.get(0).get(0).outsideEdges.size(), 0);
    vertices.get(0).get(0).addEdges(vertices);
    t.checkExpect(vertices.get(0).get(0).outsideEdges.size(), 2);

    t.checkExpect(vertices.get(5).get(5).outsideEdges.size(), 0);
    vertices.get(5).get(5).addEdges(vertices);
    t.checkExpect(vertices.get(5).get(5).outsideEdges.size(), 4);
  }

  // tests the makeVisited method
  void testMakeVisited(Tester t) {
    this.initConditions();
    t.checkExpect(this.testVertex1.visited, false);
    t.checkExpect(this.testVertex1.color, Settings.CELL_COLOR);
    this.testVertex1.makeVisited();
    t.checkExpect(this.testVertex1.visited, true);
    t.checkExpect(this.testVertex1.color, Settings.VISITED_COLOR);
  }

  // -- Tests for methods in the Edge class --

  // tests the getLine method
  void testGetLine(Tester t) {
    this.initConditions();
    // vertical line
    t.checkExpect(this.testEdge.getLine(true),
        new LineImage(new Posn(0, Settings.VERTEX_SIZE), Settings.EDGE_COLOR));

    this.initConditions();
    // horizontal line
    t.checkExpect(this.testEdge.getLine(false),
        new LineImage(new Posn(Settings.VERTEX_SIZE, 0), Settings.EDGE_COLOR));
  }