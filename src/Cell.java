import java.awt.Color;
import javalib.impworld.WorldScene;
import javalib.worldimages.*;

// represents a cell in the Maze
class Cell {
  Posn posn;
  Color color;

  // main constructor
  Cell(int i, int j, Color color) {
    this.posn = new Posn(i, j);
    this.color = color;
  }

  // draws this Cell onto the given WorldScene
  void addToScene(WorldScene scene) {
    WorldImage image = new RectangleImage(Settings.VERTEX_SIZE, Settings.VERTEX_SIZE,
        OutlineMode.SOLID, this.color);
    int x = this.posn.x * Settings.VERTEX_SIZE + Settings.VERTEX_SIZE / 2;
    int y = this.posn.y * Settings.VERTEX_SIZE + Settings.VERTEX_SIZE / 2;

    scene.placeImageXY(image, x, y);
  }
}