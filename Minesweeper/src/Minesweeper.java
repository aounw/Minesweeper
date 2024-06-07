import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import tester.*;
import javalib.impworld.*;
import javalib.worldimages.*;
import java.util.Random;


//class for all of the world functions
class MSGameWorld extends World {
  int rows;
  int cols;
  int minesTotal;
  ArrayList<Integer> mineCoord;
  ArrayList<ArrayList<Cell>> grid;
  Random rand;
  boolean gameOver;
  int width = 1000;
  int height = 670;
  int infoWidth = width - height;

  // convenience constructor for testing
  MSGameWorld(int rows, int cols, int minesTotal, int height, int width, boolean gameOver) {
    this.rows = rows;
    this.cols = cols;
    if (minesTotal <= 0) {
      throw new IllegalArgumentException("There must be at least one mine!");
    }
    else {
      this.minesTotal = minesTotal;
    }
    this.rand = new Random(1); 
    this.mineCoord = initGrid();
    this.grid = makeGrid();
    addNeighbors();
    this.height = height;
    this.width = width;
    this.gameOver = gameOver;
  }

  // Main constructor
  MSGameWorld(int rows, int cols, int minesTotal) {
    this.rows = rows;
    this.cols = cols; 
    if (minesTotal <= 0) {
      throw new IllegalArgumentException("There must be at least one mine!");
    }
    else {
      this.minesTotal = minesTotal;
    }
    this.gameOver = false;
    this.rand = new Random();
    this.mineCoord = initGrid();
    this.grid = makeGrid();
    addNeighbors();
    bigBang(width, height);
  }

  //creates a list of random positions for the mines
  ArrayList<Integer> initGrid() {
    ArrayList<Integer> linearList = new ArrayList<Integer>();
    int numCells = this.rows * this.cols;
    for (int x = 1; x <= numCells; x++) {
      linearList.add(x);
    }
    int mineCount = 1;
    ArrayList<Integer> minePos = new ArrayList<Integer>();
    while (mineCount <= this.minesTotal) {
      int next = this.rand.nextInt(numCells);
      minePos.add(linearList.remove(next));
      mineCount = mineCount + 1;
      numCells = numCells - 1;
    }
    return minePos;
  }

  //creates the grid
  ArrayList<ArrayList<Cell>> makeGrid() {
    ArrayList<ArrayList<Cell>> allRows = new ArrayList<ArrayList<Cell>>();
    for (int i = 0; i < this.rows; i++) {
      ArrayList<Cell> row = new ArrayList<Cell>();
      for (int j = 0; j < this.cols; j++) {
        if (this.mineCoord.contains(i * cols + j + 1)) {
          row.add(new Cell(true));
        }
        else {
          row.add(new Cell(false));
        }
      }
      allRows.add(row);
    }
    return allRows;
  }

  //determines the boundaries of the cells
  //EFFECT: adds neighbours to grid
  void addNeighbors() {
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) { 
        Cell current = this.grid.get(i).get(j);
        int xMin = i - 1; 
        if (xMin < 0) {
          xMin = 0; 
        }
        int xMax = i + 1;
        if (xMax > rows - 1) {
          xMax = rows - 1;
        }
        int yMin = j - 1;
        if (yMin < 0) {
          yMin = 0;
        }
        int yMax = j + 1; 
        if (yMax > cols - 1) {
          yMax = cols - 1;
        }
        for (int x = xMin; x <= xMax; x++) {
          for (int y = yMin; y <= yMax; y++) { 
            Cell neighbor = this.grid.get(x).get(y); 
            if (!(x == i && y == j)) { 
              current.addNeighbor(neighbor); 
            }
          }
        }
      }
    }
  }

  //creates the grid using the javalib library
  public WorldScene makeScene() {
    int cellWidth = (width - infoWidth) / rows;
    int cellHeight = height / cols;
    WorldScene w = new WorldScene(width, height);
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        Cell current = this.grid.get(i).get(j);
        int xPos = (i * cellWidth) + (cellWidth / 2);
        int yPos = (j * cellHeight) + (cellHeight / 2);
        w.placeImageXY(new RectangleImage(cellWidth, cellHeight, OutlineMode.SOLID, Color.BLACK),
            xPos, yPos);
        w.placeImageXY(current.makeCell(cellWidth - 2, cellHeight - 2), xPos, yPos);
      }
    }
    WorldImage gameStats = this.gameInfo();
    w.placeImageXY(gameStats, width - (infoWidth / 2), height / 2);
    return w;
  }

  //displays all the game information
  public WorldImage gameInfo() {
    WorldImage bckgr = new RectangleImage(infoWidth, height, OutlineMode.OUTLINE, 
        Color.BLACK);
    int numcellsRemaining = cellsRemaining();
    WorldImage info = new AboveImage(
        new RectangleImage(1, 80, OutlineMode.OUTLINE, Color.white),
        new TextImage("Cells left:", 16, FontStyle.REGULAR, Color.BLACK),
        new TextImage(Integer.toString(numcellsRemaining) + " / " 
            + Integer.toString(rows * cols), 16, FontStyle.REGULAR, Color.BLACK));
    WorldImage gameStats = new OverlayImage(info.movePinholeTo(new Posn(0, 120)), bckgr);
    WorldImage gameOver = new TextImage("GAMEOVER", 20, FontStyle.BOLD, Color.RED);
    WorldImage youWon = new TextImage("YOU WON", 20, FontStyle.BOLD, 
        Color.ORANGE);
    WorldImage restart = new TextImage("Close this window to restart", 15, 
        FontStyle.REGULAR, Color.BLACK);
    if (this.gameOver) {
      return new OverlayImage(
          new AboveImage(gameOver, restart).movePinhole(0, -30), gameStats);
    }
    if (!this.gameOver && this.cellsRemaining() == 0) {
      return new OverlayImage(
          new AboveImage(youWon, restart).movePinhole(0, -30), gameStats);
    }
    return gameStats;
  }

  //counts remaining cells
  public int cellsRemaining() {
    int numUncovered = 0;
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        Cell current = this.grid.get(i).get(j);
        if (!current.isMined() && current.hasRevealed()) {
          numUncovered = numUncovered + 1;
        }
      }
    }
    int cellsNeededToBeUncovered = rows * cols - this.minesTotal;
    if (cellsNeededToBeUncovered - numUncovered < 0) {
      return 0;
    }
    else {
      return cellsNeededToBeUncovered - numUncovered;
    }
  }

  //Allows right and left button clicks
  //EFFECT: handles all the mouse clicks
  public void onMouseClicked(Posn pos, String buttonName) {
    if (!this.gameOver) {
      for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
          if (pos.x > (width - infoWidth) / rows * i 
              && 
              pos.x < (width - infoWidth) / rows * (i + 1) 
              && 
              pos.y > (height / cols * j) 
              && 
              pos.y < (height / cols * (j + 1))) {
            Cell current = this.grid.get(i).get(j);
            if (buttonName.equals("LeftButton")) {
              if (!current.isMined() && !current.hasRevealed()) {
                current.displayNumMines();
              }
              else {
                if (!current.hasFlag()) {
                  this.makeGameOver();
                }
              }
            }
            if (buttonName.equals("RightButton")) {
              if (!current.hasFlag() && !current.hasRevealed()) {
                current.flag();
              }
              else {
                current.unFlag();
              }
            }
          }
        }
      }
    }
  }

  // Adjusts gameOver field to true after game is lost
  //EFFECT: turns gameOver to true
  public void makeGameOver() {
    this.gameOver = true;
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        Cell current = this.grid.get(i).get(j);
        current.displayMines();
      }
    }
  }

  //returns whether game is over
  public boolean lostGame() {
    return this.gameOver;
  }

}

//represents the main menu where we select level
class Main extends World {
  int height = 670;
  int width = 1000;

  Main() {
    bigBang(width, height, .1);
  }

  // makeCells the welcome page of the game
  public WorldScene makeScene() {
    WorldScene w = new WorldScene(width, height);
    WorldImage e = new OverlayImage(new TextImage("Easy (3mines in a 5*5 grid)", 15,
        FontStyle.BOLD, Color.BLACK), new RectangleImage(250, 30, OutlineMode.SOLID, Color.WHITE));
    WorldImage m = new OverlayImage(new TextImage("Medium (20mines in a 15*15 grid)", 15,
        FontStyle.BOLD, Color.BLACK), new RectangleImage(250, 30, OutlineMode.SOLID, Color.WHITE));
    WorldImage h = new OverlayImage(new TextImage("Hard (100mines in a 40*40 grid)", 15,
        FontStyle.BOLD, Color.BLACK), new RectangleImage(250, 30, OutlineMode.SOLID, Color.WHITE));
    WorldImage playerChoices = new AboveImage(new TextImage("Select Level:", 16, FontStyle.BOLD, 
        Color.BLACK), e, m, h);
    w.placeImageXY(playerChoices, width / 2, height / 2);
    return w;
  }

  // Launches the game depending on which option the user clicks
  //EFFECT: handles all the mouse clicks
  public void onMouseClicked(Posn pos) {
    if (pos.x > 330 && pos.x < 580) {
      if (pos.y > 262 && pos.y < 315) {
        new MSGameWorld(5, 5, 3);
      }
      if (pos.y > 315 && pos.y < 345) {
        new MSGameWorld(15, 15, 20);
      }
      if (pos.y > 345 && pos.y < 375) {
        new MSGameWorld(40, 40, 100);
      }
    }
  }
}

//Examples class 
class ExamplesMSGameWorld {
  Cell testmine; 
  Cell testmine2;
  Cell testcell;
  Cell testcell2;
  Cell testcell3;
  Cell testcell4;


  MSGameWorld gameState;

  Main m = new Main();

  //Initialize data
  void initData() {
    this.testmine = new Cell(true);
    this.testmine2 = new Cell(true);
    this.testcell = new Cell(false);
    this.testcell2 = new Cell(false);
    this.testcell3 = new Cell(false);
    this.testcell4 = new Cell(false);
    this.gameState = new MSGameWorld(3, 3, 3, 670, 1000, false);
  }

  //Test for addNeighbor in Cell class
  void testAddNeighbor(Tester t) {
    initData();
    t.checkExpect(testcell.neighbours, new ArrayList<Cell>());
    testcell.addNeighbor(testcell2);
    t.checkExpect(testcell.neighbours, new ArrayList<Cell>(
        Arrays.asList(this.testcell2)));
  }

  //Tests for initGrid
  void tesinitGrid(Tester t) {
    this.initData();
    ArrayList<Integer> answer = new ArrayList<Integer>(Arrays.asList(7, 1, 3));
    t.checkExpect(gameState.mineCoord, answer);
  }

  //Tests for constructor
  void testConstructor(Tester t) {
    this.initData();
    t.checkConstructorException(new IllegalArgumentException("There must be at least one mine!"),
        "MSGameWorld", 3, 3, 0);
    t.checkConstructorException(new IllegalArgumentException("There must be at least one mine!"),
        "MSGameWorld", 3, 3, 0, 400, 200, false);
  }

  //Tests for makeCell
  void testmakeCell(Tester t) {
    this.initData();
    //Cell class makeCell method:
    t.checkExpect(this.testcell.makeCell(10, 10), new RectangleImage(10, 10, OutlineMode.SOLID,
        Color.BLUE));
    t.checkExpect(this.testmine.makeCell(10, 10), new RectangleImage(10, 10, OutlineMode.SOLID,
        Color.BLUE));
    //Test the makeCelling of cells once they've been flipped
    this.testcell.revealed = true;
    this.testmine.revealed = true;
    t.checkExpect(this.testcell.makeCell(10, 10), new RectangleImage(10, 10, OutlineMode.SOLID,
        Color.GRAY));
    t.checkExpect(this.testmine.makeCell(10, 10), new OverlayImage(new CircleImage(10 / 2, 
        OutlineMode.SOLID, Color.RED), new RectangleImage(10, 10, OutlineMode.SOLID, 
            Color.BLUE)));
    this.testcell2.flag = true;
    t.checkExpect(this.testcell2.makeCell(10, 10), new OverlayImage(
        new EquilateralTriangleImage(10 / 2, OutlineMode.SOLID, Color.ORANGE),
        new RectangleImage(10, 10, OutlineMode.SOLID, Color.BLUE)));

  }

  //Tests for makeScene
  void testMakeScene(Tester t) {
    this.initData();
    // Cell class makeCell method checks...

    WorldScene w = new WorldScene(1000, 670);
    int cellWidth = ((1000 - 300) / 3) - 10; // Assuming 300px for infoWidth
    int cellHeight = 670 / 3;
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        Cell curCell = gameState.grid.get(i).get(j);
        // Direct calculation of positions without offsets
        int scaleXLoc = (i * cellWidth) + (cellWidth / 2);
        int scaleYLoc = (j * cellHeight) + (cellHeight / 2);
        w.placeImageXY(new RectangleImage(cellWidth, cellHeight, OutlineMode.SOLID, Color.BLACK), 
            scaleXLoc, scaleYLoc);
        w.placeImageXY(curCell.makeCell(cellWidth - 2, cellHeight - 2), 
            scaleXLoc, scaleYLoc);
      }
    }
    WorldImage stats = this.gameState.gameInfo();
    // Adjust these coordinates based on your actual gameInfo placement logic
    w.placeImageXY(stats, 835, 335); 
    t.checkExpect(gameState.makeScene(), w);
  }


  //testing cellremaining method
  void testcellsremaining(Tester t) {
    this.initData();
    t.checkExpect(this.gameState.cellsRemaining(), 6);
    this.gameState.grid.get(0).get(1).revealed = true;
    t.checkExpect(this.gameState.cellsRemaining(), 5);
  }

  //Tests for makeGameOver in Game class
  void testGameOver(Tester t) {
    this.initData();
    t.checkExpect(this.gameState.grid.get(0).get(0).revealed, false);
    t.checkExpect(this.gameState.gameOver, false);
    this.gameState.makeGameOver();
    t.checkExpect(this.gameState.grid.get(0).get(0).revealed, true);
    t.checkExpect(this.gameState.gameOver, true);
  }
}
