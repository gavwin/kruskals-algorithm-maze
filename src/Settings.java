import java.awt.Color;

interface Settings {
  // size of the "board" for the Maze
  static final int BOARD_WIDTH = 30;
  static final int BOARD_HEIGHT = 20;

  // size of a Vertex in the Maze
  static final int VERTEX_SIZE = BOARD_WIDTH;

  // size of the window
  static final int WINDOW_WIDTH = BOARD_WIDTH * VERTEX_SIZE;
  static final int WINDOW_HEIGHT = (BOARD_HEIGHT * VERTEX_SIZE) - VERTEX_SIZE * 2;

  // tick rate of the World
  static final double TICK_RATE = 0.025;

  // the colors of the different Cell types
  static final Color CELL_COLOR = Color.GRAY;
  static final Color START_COLOR = new Color(100, 100, 255);
  static final Color FINISH_COLOR = Color.MAGENTA;
  static final Color EDGE_COLOR = Color.BLACK;
  static final Color VISITED_COLOR = new Color(231, 203, 255);
  static final Color SOLUTION_COLOR = Color.ORANGE;
  static final Color PLAYER_COLOR = new Color(100, 100, 255);
}