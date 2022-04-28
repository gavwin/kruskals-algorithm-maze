import java.util.ArrayList;

// represents a player in the Maze
class Player extends Cell {

  // main constructor
  Player(int i, int j) {
    super(i, j, Settings.PLAYER_COLOR);
  }

  // move the player based on the key that is pressed
  void movePlayer(String key, ArrayList<Edge> walls) {
    if (key.equals("up") && this.posn.y != 0
        && !this.wallExists(true, this.posn.y, this.posn.y - 1, walls)) {
      this.posn.y--;
    } else if (key.equals("right") && this.posn.x != Settings.BOARD_WIDTH - 1
        && !this.wallExists(false, this.posn.x, this.posn.x + 1, walls)) {
      this.posn.x++;
    } else if (key.equals("down") && this.posn.y != Settings.BOARD_HEIGHT - 1
        && !this.wallExists(true, this.posn.y, this.posn.y + 1, walls)) {
      this.posn.y++;
    } else if (key.equals("left") && this.posn.x != 0
        && !this.wallExists(false, this.posn.x, this.posn.x - 1, walls)) {
      this.posn.x--;
    }
  }

  // is there a wall connecting the start and end indices in the given direction?
  // when direction is true, check vertical
  // when direction is false, check horizontal
  boolean wallExists(boolean direction, int start, int finish, ArrayList<Edge> walls) {
    if (direction) {
      for (Edge e : walls) {
        if (e.from.posn.x == this.posn.x && e.to.posn.x == this.posn.x
            && ((e.from.posn.y == start && e.to.posn.y == finish)
                || (e.to.posn.y == start && e.from.posn.y == finish))) {
          return true;
        }
      }
    } else {
      for (Edge e : walls) {
        if (e.from.posn.y == this.posn.y && e.to.posn.y == this.posn.y
            && ((e.from.posn.x == start && e.to.posn.x == finish)
                || (e.to.posn.x == start && e.from.posn.x == finish))) {
          return true;
        }
      }
    }

    return false;
  }

  // has the maze been completed?
  boolean isCompleted() {
    return this.posn.x == Settings.BOARD_WIDTH - 1 && this.posn.y == Settings.BOARD_HEIGHT - 1;
  }
}