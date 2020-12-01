package byow.Core.UserInput;

import byow.Core.Engine;
import byow.Core.Utils.*;
import byow.TileEngine.*;
import edu.princeton.cs.introcs.StdDraw;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A class that allows you to navigate the world and interact with enemies and items.
 * @author Jonathan Atkins, Jake Webster 11/21/20.
 */
public class Interact {

    private TERenderer ter;
    private TETile[][] world;
    private Random random;
    private Position avatar, power, startingPos, heart;
    private String userInput;
    private TETile floorType, wallType;
    private ArrayList<Position> enemies;
    private int lives;
    private boolean powered, play, boosted, togglePaths;
    private ArrayList<Object> objects;
    private WorldGraph myGraph;
    private ArrayList<AStarSolver> solver;
    private ArrayList<List<Position>> enemyPaths;

    /**
     * Initializes the instance variables with their corresponding component in @param lObj.
     * Can take input from the keyboard in the form of @param ui to interact with the world.
     */
    public Interact(ArrayList<Object> lObj, String ui) {
        this.ter = (TERenderer) lObj.get(0);
        this.world = (TETile[][]) lObj.get(1);
        this.avatar = (Position) lObj.get(2);
        this.random = (Random) lObj.get(3);
        this.floorType = (TETile) lObj.get(4);
        this.wallType = (TETile) lObj.get(5);
        if (lObj.get(2) == null) {
            avatar = generateStartingPos(random);
        }
        this.enemies = (ArrayList<Position>) lObj.get(6);
        this.power = (Position) lObj.get(7);
        this.heart = (Position) lObj.get(8);
        this.lives = (int) lObj.get(9);
        this.powered = (boolean) lObj.get(10);
        this.boosted = (boolean) lObj.get(11);
        this.togglePaths = (boolean) lObj.get(12);
        objects = lObj;
        objects.set(2, avatar);
        this.play = true;
        this.startingPos = avatar;
        this.userInput = ui;
        generatePaths();

        // If we started the game from Program arguments, run that and then quit out.
        if (Engine.isFromProgramArguments()) {
            doUserInput();
            return;
        } else {
            // If the string came solely from the keyboard.
            ter.initialize(Engine.WIDTH, Engine.HEIGHT + 8);
            // Create input source and draw first frame - before the avatar has moved
            InputSource inputSource = new KeyboardInputSource();
            drawFrame(avatar);
            boolean getReadyForQuit = false;

            while (inputSource.possibleNextInput()) {
                Position nextPos;
                char c = inputSource.getNextKey();
                if (gameState() != 0) {
                    play = false;
                }
                if (play) {
                    if (c == 'W') {
                        nextPos = new Position(avatar, 0, 1);
                        makeMove(nextPos);
                    } else if (c == 'A') {
                        nextPos = new Position(avatar, -1, 0);
                        makeMove(nextPos);
                    } else if (c == 'S') {
                        nextPos = new Position(avatar, 0, -1);
                        makeMove(nextPos);
                    } else if (c == 'D') {
                        nextPos = new Position(avatar, 1, 0);
                        makeMove(nextPos);
                    } else if (c == 'T') {
                        togglePaths = !togglePaths;
                        objects.set(12, togglePaths);
                    }
                } else {
                    showEnd();
                }

                if (c == ':') {
                    getReadyForQuit = true;
                } else if (c == 'Q' && getReadyForQuit) {
                    new SaveWorld(objects);
                    break;
                } else if (c == '0') {
                    makeMove(null);
                }
            }
        }
    }

    /**
     * If the game has been completed, show the victory/defeat menu.
     */
    private void showEnd() {
        int x = Engine.WIDTH / 2;
        int y = Engine.HEIGHT / 2;
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.filledRectangle(x, y, 15, 5);
        Font font = new Font("Monaco", Font.BOLD, 40);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.black);
        if (gameState() == -1) {
            StdDraw.text(x, y, "You Lose!");
        } else {
            StdDraw.text(x, y, "You Win!");
        }
        StdDraw.show();
        font = new Font("Monaco", Font.BOLD, 15);
        StdDraw.setFont(font);
        StdDraw.pause((int) Double.POSITIVE_INFINITY);
    }

    /**
     * Moves avatar to Position @param next if possible.
     * Also calculates if the avatar may be about to collide
     * with an enemy or the power up. However, if the @param
     * next is null, then it tries to display the new mouse
     * position on the hud without doing anything else
     */
    private void makeMove(Position next) {
        if (next != null) {
            if (checkEnemyCollision(next)) {
                if (powered) {
                    world[next.getX()][next.getY()] = floorType;
                    enemies.remove(next);
                } else {
                    lives -= 1;
                    avatar = startingPos;
                    objects.set(9, lives);
                    int s = enemies.size();
                    generateEnemies(s, true);
                    objects.set(6, enemies);
                    generatePaths();
                    drawFrame(avatar);
                    return;
                }
            } else if (checkPowerCollision(next)) {
                world[next.getX()][next.getY()] = floorType;
                powered = true;
                objects.set(10, powered);
                for (int i = 0; i < enemies.size(); i += 1) {
                    Position pos = enemies.get(i);
                    world[pos.getX()][pos.getY()] = Tileset.SCARED_ENEMY;
                }
            } else if (checkHeartCollision(next)) {
                world[next.getX()][next.getY()] = floorType;
                lives += 1;
                boosted = true;
                objects.set(11, boosted);
                objects.set(9, lives);
            }
            if (Engine.inBounds(next) && checkValid(next)) {
                avatar = next;
                objects.set(2, avatar);
                generatePaths();
                // Whenever the avatar moves, the enemies move.
                move(true);
                drawFrame(avatar);
            }
        } else {
            drawFrame(avatar);
        }
    }

    /**
     * For the AG when it tests the code using "Program arguments."
     * Essentially, this means that if given a seed N***SWWSSD
     * or LDDD:Q it will be able to compile.
     * For N***SWWSSD, if will use the seed *** and then
     * it will use the moves WWSSD before displaying the rendered
     * world.
     * For LDDD:Q, this means it will load the world, move the
     * avatar to the right three times and then save and quit.
     * userInput - this is the string put into program arguments
     *                  - this is found and tested through
     *                  Run >> Edit Configurations >> Program arguments.
     */
    // mark
    private void doUserInput() {
        if (userInput != null) {
            char[] charArray = userInput.toCharArray();
            Position nextPos = avatar;
            boolean getReadyForQuit = false;
            int sCounter = 1;

            // this is so if the first thing is n, then we know the seed
            // ends at s so we want to record
            // everything after that
            if (userInput.charAt(0) == 'N') {
                sCounter = 0;
            }

            for (char c : charArray) {
                if (c == 'W') {
                    nextPos = new Position(avatar, 0, 1);
                    makeMoveFromInput(nextPos);
                } else if (c == 'A') {
                    nextPos = new Position(avatar, -1, 0);
                    makeMoveFromInput(nextPos);
                } else if (c == 'S' && sCounter > 0) {
                    nextPos = new Position(avatar, 0, -1);
                    makeMoveFromInput(nextPos);
                } else if (c == 'S') {
                    sCounter += 1;
                } else if (c == 'D') {
                    nextPos = new Position(avatar, 1, 0);
                    makeMoveFromInput(nextPos);
                } else if (c == ':') {
                    getReadyForQuit = true;
                } else if (c == 'Q' && getReadyForQuit) {
                    new SaveWorld(objects);
                }
            }
        }
    }

    /**
     * Makes a move to @param next from input. Used for the autograder.
     */
    // mark
    private void makeMoveFromInput(Position next) {
        if (next != null) {
            if (checkEnemyCollision(next)) {
                if (powered) {
                    world[next.getX()][next.getY()] = floorType;
                    enemies.remove(next);
                } else {
                    lives -= 1;
                    avatar = startingPos;
                    objects.set(9, lives);
                    int s = enemies.size();
                    generateEnemies(s, false);
                    objects.set(6, enemies);
                    generatePaths();
                    return;
                }
            } else if (checkPowerCollision(next)) {
                world[next.getX()][next.getY()] = floorType;
                powered = true;
                objects.set(10, powered);
                for (int i = 0; i < enemies.size(); i += 1) {
                    Position pos = enemies.get(i);
                    world[pos.getX()][pos.getY()] = Tileset.SCARED_ENEMY;
                }
            } else if (checkHeartCollision(next)) {
                world[next.getX()][next.getY()] = floorType;
                lives += 1;
                boosted = true;
                objects.set(11, boosted);
                objects.set(9, lives);
            }
            if (Engine.inBounds(next) && checkValid(next)) {
                avatar = next;
                objects.set(2, avatar);
                generatePaths();
                // Whenever the avatar moves, the enemies move.
                move(false);
            }
        }
    }

    /**
     * @Return whether the player could hypothetically move onto the Position @param p.
     */
    private boolean checkValid(Position p) {
        TETile tile = world[p.getX()][p.getY()];
        return !tile.equals(wallType) && !tile.equals(Tileset.NOTHING);
    }

    /**
     * Uses the A* Algorithm to find the closest path between each enemy and the avatar.
     */
    private void generatePaths() {
        this.myGraph = new WorldGraph(world, wallType);
        this.solver = new ArrayList<>();
        this.enemyPaths = new ArrayList<>();
        for (int i = 0; i < enemies.size(); i += 1) {
            this.solver.add(new AStarSolver(myGraph, enemies.get(i), avatar, 5.0));
            this.enemyPaths.add(solver.get(i).solution());
        }
    }

    /**
     * If no starting pos of the avatar is given, generate one using Random @param r.
     * @Return this position once an appropriate one is calculated.
     */
    private Position generateStartingPos(Random r) {
        int counter = 0;
        while (true) {
            int x = RandomUtils.uniform(r, 0, Engine.WIDTH);
            int y = RandomUtils.uniform(r, 0, Engine.HEIGHT);
            Position temp = new Position(x, y);
            if (isFloor(temp)) {
                return temp;
            }
            if (counter == 400) {
                showNonValidWorld();
            }
            counter += 1;
        }
    }

    /**
     * If the custom-built world cannot generate the game, inform the user it is non-valid.
     */
    private void showNonValidWorld() {
        int x = Engine.WIDTH / 2;
        int y = Engine.HEIGHT / 2;
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.filledRectangle(x, y, 15, 5);
        Font font = new Font("Monaco", Font.BOLD, 40);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.black);

        StdDraw.text(x, y, "Non-valid World");

        StdDraw.show();
        font = new Font("Monaco", Font.BOLD, 15);
        StdDraw.setFont(font);
        StdDraw.pause((int) Double.POSITIVE_INFINITY);
    }

    /**
     * @Return the type of tile at Position @param i in @param world.
     */
    private static TETile getWorldTile(Position i, TETile[][] world) {
        return world[i.getX()][i.getY()];
    }

    /**
     * Draws the world state given a new position of the avatar @param i.
     * Uses the StdDraw library from Princeton.
     */
    public void drawFrame(Position i) {
        if (Engine.inBounds(i)) {
            StdDraw.clear();
            TETile orgTile = world[i.getX()][i.getY()];
            world[i.getX()][i.getY()] = Tileset.AVATAR;

            ter.renderFrame(world);
            world[i.getX()][i.getY()] = orgTile;

            // The HUD. Wherever the play mouse is, calculate/display the world tile.
            int x = (int) StdDraw.mouseX();
            int y = (int) StdDraw.mouseY();
            Position nextMouse = new Position(x, y);
            if (Engine.inBounds(nextMouse)) {
                TETile mouseTile;
                if (nextMouse.equals(i)) {
                    mouseTile = Tileset.AVATAR;
                } else {
                    mouseTile = getWorldTile(nextMouse, world);
                }
                StringBuilder temp = new StringBuilder();
                temp.append(mouseTile.description());
                temp.append(" (");
                temp.append(x);
                temp.append(", ");
                temp.append(y);
                temp.append(")");
                StdDraw.setPenColor(Color.GREEN);
                StdDraw.text(10, Engine.HEIGHT + 2, temp.toString());
            } else {
                StdDraw.setPenColor(Color.GREEN);
                StdDraw.text(10, Engine.HEIGHT + 2, "hud display");
            }
            StdDraw.text(10, Engine.HEIGHT + 6, "Lives: " + lives);
            StdDraw.text(10, Engine.HEIGHT + 4, "Paths (T) " + togglePaths);
            StdDraw.line(0, Engine.HEIGHT, Engine.WIDTH, Engine.HEIGHT);
            StdDraw.show();
        }
        if (togglePaths) {
            for (int j = 0; j < world.length; j += 1) {
                for (int k = 0; k < world[j].length; k += 1) {
                    boolean pathway = false;
                    for (int l = 0; l < enemyPaths.size(); l += 1) {
                        Position p = new Position(j, k);
                        if (enemyPaths.get(l).contains(p) && !enemies.contains(p)) {
                            world[j][k] = Tileset.PATH_TILE;
                            pathway = true;
                        } else if (!pathway && world[j][k] == Tileset.PATH_TILE) {
                            fixTile(p, j, k);
                        }
                    }
                }
            }
        } else {
            for (int j = 0; j < world.length; j += 1) {
                for (int k = 0; k < world[j].length; k += 1) {
                    if (world[j][k] == Tileset.PATH_TILE) {
                        Position p = new Position(j, k);
                        fixTile(p, j, k);
                    }
                }
            }
        }
    }

    /**
     * @Return if the @param nextPos is a floor tile
     */
    private boolean isFloor(Position nextPos) {
        return getWorldTile(nextPos, world).equals(floorType);
    }

    /**
     * @Return whether the Avatar at Position @param pos is colliding with any enemies.
     */
    private boolean checkEnemyCollision(Position pos) {
        for (Position enemy : enemies) {
            if (pos.equals(enemy)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @Return whether the Avatar at Position @param pos is colliding with the power up.
     */
    private boolean checkPowerCollision(Position pos) {
        return !powered && pos.equals(power);
    }

    /**
     * @Return whether the Avatar at Position @param pos is colliding with the heart.
     */
    private boolean checkHeartCollision(Position pos) {
        return !boosted && pos.equals(heart);
    }

    /**
     * @Return whether the game is ongoing, or what the outcome is.
     *  -1 = Game over, player loses.
     *   0 = Game ongoing.
     *   1 = Game over, player wins!
     */
    private int gameState() {
        if (lives <= 0) {
            return -1;
        } else if (enemies.size() == 0) {
            return 1;
        }
        return 0;
    }

    /**
     * Moves the enemies in the most optimal way towards the avatar.
     * Uses the A* algorithm for chasing enemies.
     * When running from enemies, it employs an "anti-A*" algorithm.
     *  However, this algorithm does not look too ahead as A* because otherwise
     *  the game drags on for far too long.
     *  Nonetheless, it is a fairly smart algorithm when the avatar is 2-10 tiles away, making
     *  for some fun chase scenes.
     * @param show is used for visualization purposes.
     */
    private void move(boolean show) {
        for (int i = 0; i < enemies.size(); i += 1) {
            Position p = enemies.get(i);
            List<Position> sol = solver.get(i).solution();
            Position nextP;
            if (sol.size() < 2) {
                int direction = RandomUtils.uniform(random, 1, 5);
                if (direction == 1) {
                    nextP = new Position(p, -1, 0);
                } else if (direction == 2) {
                    nextP = new Position(p, 1, 0);
                } else if (direction == 3) {
                    nextP = new Position(p, 0, 1);
                } else {
                    nextP = new Position(p, 0, -1);
                }
                if (getWorldTile(nextP, world) != floorType) {
                    nextP = p;
                }
            } else {
                nextP = sol.get(1);
            }
            if (powered) {
                List<WeightedEdge> neighbors = myGraph.neighbors(p);
                for (WeightedEdge e: neighbors) {
                    Position move = e.to();
                    // You don't want to move onto an enemy or closer to the player.
                    // Stops moving once you get a tile away because otherwise the game drags out.
                    if (!nextP.equals(move) && myGraph.estimatedDistanceToGoal(move, avatar) > 2
                        && !enemies.contains(move)) {
                        fixTile(p, p.getX(), p.getY());
                        enemies.set(i, move);
                        world[move.getX()][move.getY()] = Tileset.SCARED_ENEMY;
                        enemyPaths.get(i).remove(p);
                        generatePaths();
                        break;
                    }
                }
            } else if (!enemies.contains(nextP)) {
                fixTile(p, p.getX(), p.getY());
                enemies.set(i, nextP);
                if (nextP.equals(avatar)) {
                    lives -= 1;
                    avatar = startingPos;
                    objects.set(2, avatar);
                    objects.set(9, lives);
                    int s = enemies.size();
                    generateEnemies(s, show);
                    objects.set(6, enemies);
                    generatePaths();
                } else {
                    world[nextP.getX()][nextP.getY()] = Tileset.ENEMY;
                    enemyPaths.get(i).remove(p);
                }
            }
        }
    }

    /**
     * When moving an enemy/floor tile/avatar off of @param p, being at position
     * @param x, @param y in the world, get the world tile to go back to the proper state.
     */
    private void fixTile(Position p, int x, int y) {
        if (!powered && p.equals(power)) {
            world[x][y] = Tileset.POWER;
        } else if (!boosted && p.equals(heart)) {
            world[x][y] = Tileset.HEART;
        } else {
            world[x][y] = floorType;
        }
    }

    /**
     * @Return the world state as shown by the world array.
     */
    public TETile[][] getWorld() {
        return world;
    }

    /**
     * Generate @param num enemies to random floor tiles.
     * Also clears the world of enemies in previous locations, if they existed.
     * Updates the frame if @param show is true.
     */
    private void generateEnemies(int num, boolean show) {
        for (int i = 0; i < enemies.size(); i += 1) {
            Position p = enemies.get(i);
            world[p.getX()][p.getY()] = floorType;
        }
        enemies = new ArrayList<>();
        for (int i = 0; i < num; i += 1) {
            boolean valid = false;
            while (!valid) {
                int x = RandomUtils.uniform(random, 0, Engine.WIDTH);
                int y = RandomUtils.uniform(random, 0, Engine.HEIGHT);
                Position enemyPos = new Position(x, y);
                if (Engine.inBounds(enemyPos) && (world[x][y].equals(floorType)
                        || world[x][y] == Tileset.PATH_TILE)) {
                    world[x][y] = Tileset.ENEMY;
                    enemies.add(enemyPos);
                    valid = true;
                }
            }
        }
        if (show) {
            drawFrame(avatar);
        }
    }
}
