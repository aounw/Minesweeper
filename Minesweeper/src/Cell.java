import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import javalib.worldimages.*;
import tester.Tester;

//constants class for game
interface IConstants {
  int SQUARE_DIMS = 30;
}

//represents a cell in the minesweeper game
class Cell implements IConstants {
  boolean mined;
  boolean revealed;
  boolean flag;
  int mineNeighbours;
  ArrayList<Cell> neighbours;

  //convenience constructor for testing
  Cell(boolean mined, boolean revealed, int mineNeighbours, ArrayList<Cell> neighbours) {
    this.mined = mined;
    this.revealed = revealed;
    this.mineNeighbours = mineNeighbours;
    this.neighbours = neighbours;
  }

  //convenience constructor for testing
  Cell(boolean mined, boolean revealed, ArrayList<Cell> neighbours) {
    this.mined = mined;
    this.revealed = revealed;
    this.mineNeighbours = 0;
    this.neighbours = neighbours;
  }

  // main constructor
  Cell(boolean mined) {
    this.mined = mined;
    this.revealed = false;
    this.flag = false;
    this.mineNeighbours = 0;
    this.neighbours = new ArrayList<Cell>();
  }

  // EFFECT: adds a neighbour to the list
  // adds to the count of mined neighbours
  void addNeighbor(Cell neighbor) {
    this.neighbours.add(neighbor);
    this.initNeighbourMines();
  }

  // counts if all the neighbours are mined
  // EFFECT: changes the mineNeigbours to reflect
  // the amount of surrounding mined neighbours
  void initNeighbourMines() {
    int count = 0;
    for (Cell neighbor : this.neighbours) {
      count += neighbor.countNeighbours();
    }
    this.mineNeighbours = count;
  }

  // returns 1 if a neighbour is mined
  // and 0 if not
  int countNeighbours() {
    if (this.mined) {
      return 1;
    }
    else {
      return 0;
    }
  }

  // determines if a list contains a mined cell
  boolean hasMine() {
    boolean anyMined = false;
    for (Cell neighbor : this.neighbours) {
      anyMined = neighbor.hasMineHelp(anyMined);
    }
    return anyMined;
  }

  // helper for the method hasMine
  // accumulates the boolean for each value
  boolean hasMineHelp(boolean b) {
    return this.mined || b;
  }

  //determines if a number should be displayed on a square or if it should flood
  //EFFECT: changes revealed to true
  //        changes all neighbors revealed to true if they have no mined neighbor
  void displayNumMines() {
    if (this.mineNeighbours > 0) {
      this.revealed = true;
    }
    else { 
      this.revealed = true;
      for (int idx = 0; idx < this.neighbours.size(); idx++) {
        Cell curNeighb = this.neighbours.get(idx);
        if (!curNeighb.revealed //TA endorsed solution
            && !curNeighb.mined 
            && !curNeighb.flag) { 
          curNeighb.displayNumMines();
        }
      }
    }
  }

  //draws a cell into a worldImage
  WorldImage makeCell(int cellWidth, int cellHeight) {
    WorldImage aFlag = new EquilateralTriangleImage(cellWidth / 2, OutlineMode.SOLID,
        Color.ORANGE);
    WorldImage aMine = new CircleImage(cellWidth / 2, OutlineMode.SOLID, Color.RED);
    WorldImage unFlippedCell = new RectangleImage(cellWidth, cellHeight, OutlineMode.SOLID, 
        Color.BLUE);
    WorldImage flippedCell = new RectangleImage(cellWidth, cellHeight, OutlineMode.SOLID, 
        Color.GRAY);
    if (!this.revealed && this.flag) {
      return new OverlayImage(aFlag, unFlippedCell);
    }
    if (!this.revealed) {
      return unFlippedCell;
    }
    else { 
      if (this.mined) {
        if (this.flag) {
          WorldImage mineAndFlag = new OverlayImage(aFlag, aMine);
          return new OverlayImage(mineAndFlag, unFlippedCell);
        }
        else {
          return new OverlayImage(aMine, unFlippedCell);
        }
      }
      else {
        String val = Integer.toString(this.mineNeighbours);
        if (this.mineNeighbours == 0) {
          return flippedCell;
        } 
        if (this.mineNeighbours == 1) {
          return new OverlayImage(new TextImage(val, cellWidth / 2, FontStyle.BOLD, Color.BLUE),
              flippedCell);
        }
        if (this.mineNeighbours == 2) {
          return new OverlayImage(new TextImage(val, cellWidth / 2, FontStyle.BOLD, Color.GREEN),
              flippedCell);
        }
        if (this.mineNeighbours == 3) {
          return new OverlayImage(new TextImage(val, cellWidth / 2, FontStyle.BOLD, Color.RED),
              flippedCell);
        }
        else {
          return new OverlayImage(new TextImage(val, cellWidth / 2, FontStyle.BOLD, Color.BLACK),
              flippedCell);
        }
      }
    }
  }

  //reveals all mines at the end of the game
  //EFFECT: revealed to true
  void displayMines() {
    if (this.mined) {
      this.revealed = true;
    }
  }

  //returns whether or not the cell is revealed
  boolean hasRevealed() {
    return this.revealed; //TA Endorsed solution
  }

  //returns whether or not the Cell is flagged
  boolean hasFlag() {
    return this.flag; //TA Endorsed solution
  }

  //creates a flag
  //EFFECT: changes this flag to true
  public void flag() {
    this.flag = true; //TA Endorsed solution
  }

  //removes a flag
  //EFFECT: Changes this flag to false 
  public void unFlag() {
    this.flag = false; //TA Endorsed solution
  }

  //returns whether or not the Cell is a mine 
  boolean isMined() {
    return this.mined; //TA Endorsed solution
  }
}

//examples class for testing 
class ExamplesCell implements IConstants {
  Cell neighbourEx1;
  Cell neighbourEx2;
  Cell neighbourEx3;
  Cell neighbourEx4;
  Cell neighbourEx5;
  Cell neighbourTest;
  Cell neighbourRevealed;
  Cell neighbourRevealedM;
  Cell neighbor0;
  Cell neighbor1;
  Cell neighbor2;
  Cell neighbor3;
  Cell neighbor4;

  //initialises the data for testing
  void initData() {
    this.neighbourEx1 = new Cell(true);
    this.neighbourEx2 = new Cell(false);
    this.neighbourEx3 = new Cell(true);
    this.neighbourEx4 = new Cell(false);
    this.neighbourEx5 = new Cell(true);
    this.neighbourTest = new Cell(false, false, new ArrayList<Cell>(Arrays.asList(this.neighbourEx1,
        this.neighbourEx2, this.neighbourEx3, this.neighbourEx4, this.neighbourEx5)));
    this.neighbourRevealed = new Cell(true, true, new ArrayList<Cell>());
    this.neighbourRevealedM = 
        new Cell(false, true, new ArrayList<Cell>(Arrays.asList(new Cell(true))));
    this.neighbor0 = new Cell(false, true, 0, new ArrayList<Cell>());
    this.neighbor1 = new Cell(false, true, 1, new ArrayList<Cell>());
    this.neighbor2 = new Cell(false, true, 2, new ArrayList<Cell>());
    this.neighbor3 = new Cell(false, true, 3, new ArrayList<Cell>());
    this.neighbor4 = new Cell(false, true, 4, new ArrayList<Cell>());
  }

  //test for the method addNeighbor
  void testAddNeighbor(Tester t) {
    this.initData();
    t.checkExpect(this.neighbourEx1.neighbours, new ArrayList<Cell>());
    t.checkExpect(this.neighbourEx1.mineNeighbours, 0);
    this.neighbourEx1.addNeighbor(neighbourEx2);
    t.checkExpect(this.neighbourEx1.neighbours, 
        new ArrayList<Cell>(Arrays.asList(this.neighbourEx2)));
    t.checkExpect(this.neighbourEx1.mineNeighbours, 0);
    this.neighbourEx1.addNeighbor(neighbourEx3);
    t.checkExpect(this.neighbourEx1.neighbours, 
        new ArrayList<Cell>(Arrays.asList(this.neighbourEx2, this.neighbourEx3)));
    t.checkExpect(this.neighbourEx1.mineNeighbours, 1);
    this.neighbourEx2.addNeighbor(neighbourEx1);
    t.checkExpect(this.neighbourEx1.neighbours, 
        new ArrayList<Cell>(Arrays.asList(this.neighbourEx2, this.neighbourEx3)));
    t.checkExpect(this.neighbourEx1.mineNeighbours, 1);
    t.checkExpect(this.neighbourEx2.neighbours, 
        new ArrayList<Cell>(Arrays.asList(this.neighbourEx1)));
  }

  //test for the method initNeighborMines
  void testInitNeigbourMines(Tester t) {
    this.initData();
    t.checkExpect(this.neighbourTest.mineNeighbours, 0);
    t.checkExpect(this.neighbourEx1.mineNeighbours, 0);
    this.neighbourTest.initNeighbourMines();
    t.checkExpect(this.neighbourTest.mineNeighbours, 3);
    t.checkExpect(this.neighbourEx1.mineNeighbours, 0);
  }

  //test for the method countNeighbor
  void testCountNeighbor(Tester t) {
    this.initData();
    t.checkExpect(this.neighbourEx1.countNeighbours(), 1);
    t.checkExpect(this.neighbourEx2.countNeighbours(), 0);
    t.checkExpect(this.neighbourEx3.countNeighbours(), 1);
    t.checkExpect(this.neighbourEx4.countNeighbours(), 0);
    t.checkExpect(this.neighbourEx5.countNeighbours(), 1);
    t.checkExpect(this.neighbourTest.countNeighbours(), 0);
  }

  //test for the method hasMine
  void hasMine(Tester t) {
    this.initData();
    t.checkExpect(this.neighbourEx1.hasMine(), false);
    t.checkExpect(this.neighbourEx2.hasMine(), false);
    t.checkExpect(this.neighbourTest.hasMine(), true);
    t.checkExpect(this.neighbourRevealedM.hasMine(), true);
  }

  //test for the method hasMineHelp
  void hasMineHelp(Tester t) {
    this.initData();
    t.checkExpect(this.neighbourEx1.hasMineHelp(false), false);
    t.checkExpect(this.neighbourEx1.hasMineHelp(true), true);
    t.checkExpect(this.neighbourEx2.hasMineHelp(true), true);
    t.checkExpect(this.neighbourTest.hasMineHelp(false), true);
  }

  //Test for the method displayCornerMines
  void testDisplayNumMines(Tester t) {
    this.initData();
    //When the current cell has no mine neighbors and is not flagged
    Cell cellNoMineNeighbors = new Cell(false);
    cellNoMineNeighbors.neighbours = new ArrayList<>(Arrays.asList(
        new Cell(false),
        new Cell(false)));
    cellNoMineNeighbors.displayNumMines();
    t.checkExpect(cellNoMineNeighbors.revealed, true);
    t.checkExpect(cellNoMineNeighbors.neighbours.get(0).revealed, true);
    t.checkExpect(cellNoMineNeighbors.neighbours.get(1).revealed, true);
    //When the current cell has mine neighbors
    Cell cellWithMineNeighbors = new Cell(false);
    cellWithMineNeighbors.neighbours = new ArrayList<>(Arrays.asList(
        new Cell(true),
        new Cell(true)));
    cellWithMineNeighbors.displayNumMines();
    t.checkExpect(cellWithMineNeighbors.revealed, true);
    t.checkExpect(cellWithMineNeighbors.neighbours.get(0).revealed, false);
    t.checkExpect(cellWithMineNeighbors.neighbours.get(1).revealed, false);
    //When the current cell has no mine neighbors but some of its neighbors are flagged
    Cell cellWithFlaggedNeighbors = new Cell(false);
    cellWithFlaggedNeighbors.neighbours = new ArrayList<>(Arrays.asList(
        new Cell(false),
        new Cell(false)));
    cellWithFlaggedNeighbors.neighbours.get(0).flag();
    cellWithFlaggedNeighbors.displayNumMines();
    t.checkExpect(cellWithFlaggedNeighbors.revealed, true);
    t.checkExpect(cellWithFlaggedNeighbors.neighbours.get(0).revealed, false);
    t.checkExpect(cellWithFlaggedNeighbors.neighbours.get(1).revealed, true);
    //When the current cell has no mine neighbors and some of 
    //its neighbors are already revealed
    Cell cellWithRevealedNeighbors = new Cell(false);
    cellWithRevealedNeighbors.neighbours = new ArrayList<>(Arrays.asList(
        new Cell(false),
        new Cell(false)));
    cellWithRevealedNeighbors.neighbours.get(0).revealed = true;
    cellWithRevealedNeighbors.displayNumMines();
    t.checkExpect(cellWithRevealedNeighbors.revealed, true);
    t.checkExpect(cellWithRevealedNeighbors.neighbours.get(0).revealed, true);
    t.checkExpect(cellWithRevealedNeighbors.neighbours.get(1).revealed, true);
  }

  //Test for the method makeCell
  void testMakeCell(Tester t) {
    this.initData();
    WorldImage expectedImageNoRevealed = 
        new RectangleImage(SQUARE_DIMS, SQUARE_DIMS, OutlineMode.SOLID, Color.BLUE);
    t.checkExpect(this.neighbourEx1.makeCell(SQUARE_DIMS, SQUARE_DIMS), expectedImageNoRevealed);
    WorldImage expectedImageRevealedM = new OverlayImage(
        new CircleImage(15, OutlineMode.SOLID, Color.RED),
        new RectangleImage(SQUARE_DIMS, SQUARE_DIMS, OutlineMode.SOLID, Color.BLUE));
    t.checkExpect(this.neighbourRevealed.makeCell(SQUARE_DIMS, SQUARE_DIMS), 
        expectedImageRevealedM);
    WorldImage expectedImageRevealedNM = 
        new RectangleImage(SQUARE_DIMS, SQUARE_DIMS, OutlineMode.SOLID, Color.GRAY);
    t.checkExpect(this.neighbourRevealedM.makeCell(SQUARE_DIMS, SQUARE_DIMS), 
        expectedImageRevealedNM);
    this.neighbourEx1.flag();
    WorldImage expectedImageFlaggedNoMine = new OverlayImage(
        new EquilateralTriangleImage(SQUARE_DIMS / 2, OutlineMode.SOLID, Color.ORANGE),
        new RectangleImage(SQUARE_DIMS, SQUARE_DIMS, OutlineMode.SOLID, Color.BLUE));
    t.checkExpect(this.neighbourEx1.makeCell(SQUARE_DIMS, SQUARE_DIMS), expectedImageFlaggedNoMine);
    this.neighbourEx1.mined = true;
    WorldImage expectedImageFlaggedMine = new OverlayImage(
        new EquilateralTriangleImage(SQUARE_DIMS / 2, OutlineMode.SOLID, Color.ORANGE),
        new RectangleImage(SQUARE_DIMS, SQUARE_DIMS, OutlineMode.SOLID, Color.BLUE));
    t.checkExpect(this.neighbourEx1.makeCell(SQUARE_DIMS, SQUARE_DIMS), expectedImageFlaggedMine);
    WorldImage neighbour0Image = 
        new RectangleImage(SQUARE_DIMS, SQUARE_DIMS, OutlineMode.SOLID, Color.GRAY);
    t.checkExpect(this.neighbor0.makeCell(SQUARE_DIMS, SQUARE_DIMS), neighbour0Image);
    WorldImage neighbour1Image = new OverlayImage(
        new TextImage("1", SQUARE_DIMS / 2, FontStyle.BOLD, Color.BLUE),
        new RectangleImage(SQUARE_DIMS, SQUARE_DIMS, OutlineMode.SOLID, Color.GRAY));
    t.checkExpect(this.neighbor1.makeCell(SQUARE_DIMS, SQUARE_DIMS), neighbour1Image);
    WorldImage neighbour2Image = new OverlayImage(
        new TextImage("2", SQUARE_DIMS / 2, FontStyle.BOLD, Color.GREEN),
        new RectangleImage(SQUARE_DIMS, SQUARE_DIMS, OutlineMode.SOLID, Color.GRAY));
    t.checkExpect(this.neighbor2.makeCell(SQUARE_DIMS, SQUARE_DIMS), neighbour2Image);
    WorldImage neighbour3Image = new OverlayImage(
        new TextImage("3", SQUARE_DIMS / 2, FontStyle.BOLD, Color.RED),
        new RectangleImage(SQUARE_DIMS, SQUARE_DIMS, OutlineMode.SOLID, Color.GRAY));
    t.checkExpect(this.neighbor3.makeCell(SQUARE_DIMS, SQUARE_DIMS), neighbour3Image);
    WorldImage neighbour4Image = new OverlayImage(
        new TextImage("4", SQUARE_DIMS / 2, FontStyle.BOLD, Color.BLACK),
        new RectangleImage(SQUARE_DIMS, SQUARE_DIMS, OutlineMode.SOLID, Color.GRAY));
    t.checkExpect(this.neighbor4.makeCell(SQUARE_DIMS, SQUARE_DIMS), neighbour4Image);
  }

  //Test for the displayMines method
  void testDisplayMines(Tester t) {
    this.initData();
    // Test when the cell is mined and not revealed
    t.checkExpect(this.neighbourEx1.hasRevealed(), false);
    this.neighbourEx1.displayMines();
    t.checkExpect(this.neighbourEx1.hasRevealed(), true);
    // Test when the cell is mined and revealed
    t.checkExpect(this.neighbourRevealedM.hasRevealed(), true);
    this.neighbourRevealedM.displayMines();
    t.checkExpect(this.neighbourRevealedM.hasRevealed(), true);
  }

  //Test for the hasRevealed method
  void testHasRevealed(Tester t) {
    this.initData();
    // Test when the cell is not revealed
    t.checkExpect(this.neighbourEx1.hasRevealed(), false);
    // Test when the cell is revealed
    t.checkExpect(this.neighbourRevealed.hasRevealed(), true);
  }

  //Test for the hasFlag method
  void testHasFlag(Tester t) {
    this.initData();
    // Test when the cell has no flag
    t.checkExpect(this.neighbourEx1.hasFlag(), false);
    // Test when the cell has a flag
    this.neighbourEx1.flag();
    t.checkExpect(this.neighbourEx1.hasFlag(), true);
  }

  //Test for the flag method
  void testFlag(Tester t) {
    this.initData();
    t.checkExpect(this.neighbourEx1.hasFlag(), false);
    this.neighbourEx1.flag();
    t.checkExpect(this.neighbourEx1.hasFlag(), true);
  }

  //Test for the unFlag method
  void testUnFlag(Tester t) {
    this.initData();
    // Test unflagging a cell
    this.neighbourEx1.flag();
    t.checkExpect(this.neighbourEx1.hasFlag(), true);
    this.neighbourEx1.unFlag();
    t.checkExpect(this.neighbourEx1.hasFlag(), false);
  }

  //Test for the isMined method
  void testIsMined(Tester t) {
    this.initData();
    // Test when the cell is not mined
    t.checkExpect(this.neighbourEx1.isMined(), true);
    // Test when the cell is mined
    t.checkExpect(this.neighbourRevealedM.isMined(), false);
  }


}

