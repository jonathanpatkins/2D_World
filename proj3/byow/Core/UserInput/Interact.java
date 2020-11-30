package byow.Core.UserInput;

import byow.Core.Engine;

import byow.Core.Utils.*;
import byow.TileEngine.*;
import byow.Core.WorldComponents.*;
import edu.princeton.cs.introcs.StdDraw;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Interact {

    TERenderer ter;
    TETile[][] world;
    Random random;
    Position avatar, power, startingPos, heart;
    String userInput;
    private TETile floorType, wallType;
    private ArrayList<Position> enemies;
    private int lives;
    private boolean powered, play, boosted, togglePaths;
    private ArrayList<Object> objects;
    private WorldGraph myGraph;
    private ArrayList<AStarSolver> solver;
    private ArrayList<List<Position>> enemyPaths;

    // now you can take input from keyboard to interact with world
    public Interact(ArrayList<Object> loadedObjects, String userIn) {
        this.ter = (TERenderer) loadedObjects.get(0);
        this.world = (TETile[][]) loadedObjects.get(1);
        this.avatar = (Position) loadedObjects.get(2);
        this.random = (Random) loadedObjects.get(3);
        this.floorType = (TETile) loadedObjects.get(4);
        this.wallType = (TETile) loadedObjects.get(5);
        if (loadedObjects.get(2) == null) {
            avatar = generateStartingPos(world, random);
        }
        this.enemies = (ArrayList<Position>) loadedObjects.get(6);
        this.power = (Position) loadedObjects.get(7);
        this.heart = (Position) loadedObjects.get(8);
        this.lives = (int) loadedObjects.get(9);
        this.powered = (boolean) loadedObjects.get(10);
        this.boosted = (boolean) loadedObjects.get(11);
        this.togglePaths = (boolean) loadedObjects.get(12);
        objects = loadedObjects;
        objects.set(2, avatar);
        this.play = true;
        this.startingPos = avatar;
        this.userInput = userIn;
        generatePaths();

        // if we started the game from Program arguments, run that and then quit out
        if (Engine.getFromProgramArguments()) {
            doUserInput(userInput);
        }
        // if the string came solely from the keyboard then we want to continue interacting with the keyboard
        else {
            ter.initialize(Engine.WIDTH, Engine.HEIGHT);
            // create input source and draw first frame - before the avatar has moved
            InputSource inputSource = new KeyboardInputSource();
            drawFrame(ter, world, avatar);
            boolean getReadyForQuit = false;
            // change frame due to keyboard input

            while (inputSource.possibleNextInput()) {
                Position nextPos;
                char c = inputSource.getNextKey();
                if (gameState() != 0) {
                    play = false;
                }
                if (play) {
                    if (c == 'W') {
                        nextPos = new Position(avatar, 0, 1);
                        makeMove(nextPos, c);
                    } else if (c == 'A') {
                        nextPos = new Position(avatar, -1, 0);
                        makeMove(nextPos, c);
                    } else if (c == 'S') {
                        nextPos = new Position(avatar, 0, -1);
                        makeMove(nextPos, c);
                    } else if (c == 'D') {
                        nextPos = new Position(avatar, 1, 0);
                        makeMove(nextPos, c);
                    } else if (c == 'T') {
                        togglePaths = !togglePaths;
                        objects.set(12, togglePaths);
                    }
                } else {
                    StdDraw.setPenColor(Color.WHITE);
                    //Font result = new Font("Monaco", Font.BOLD, 48);
                    //StdDraw.setFont(result);
                    if (gameState() == -1) {
                        StdDraw.text(Engine.WIDTH / 2, Engine.HEIGHT / 2, "You Lose!");
                    } else {
                        StdDraw.text(Engine.WIDTH / 2, Engine.HEIGHT / 2, "You Win!");
                    }
                    StdDraw.show();
                }

                if (c == ':') {
                    getReadyForQuit = true;
                } else if (c == 'Q' && getReadyForQuit) {
                    SaveWorld saveWorld = new SaveWorld(objects);
                    break;
                }
                // this is triggered if you are not taking in new keyboard input
                // in this case you want to display the new mouse position if it has changed, but do nothing else
                else if (c == '0') {
                    makeMove(null, c);
                }
            }
        }
    }
    /**
     * Moves the avatar to Position @param next if possible- Valid position/character @param c.
     * Also calculates if the avatar may be about to collide with an enemy or the power up.
     * However, if the @param next is null, then it tries to display the new mouse position on the hud
     * without doing anything else
     */
    private void makeMove(Position next, char c) {
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
                    generateEnemies(s);
                    objects.set(6, enemies);
                    generatePaths();
                    drawFrame(ter, world, avatar);
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
                move();
                drawFrame(ter, world, avatar);
            }
        } else {
            drawFrame(ter, world, avatar);
        }
    }

    /**
     * This method is for the autograder when it tests the code using "Program arguments"
     * Essentially, this means that if given a seed N***SWWSSD or LDDD:Q it will be able to compile
     * For N***SWWSSD, if will use the seed *** and then it will use the moves WWSSD before displaying
     * the rendered world.
     * Fro LDDD:Q, this means it will load the world, move the avatar to the right three times and then save
     * and quit.
     * @param userInput this is the string put into program arguments - this is found and tested through
     *                  Run >> Edit Configurations >> Program arguments
     * @return boolean value for true if the program should quit and false to keep it going
     */
    private void doUserInput(String userInput) {
        if (userInput != null) {
            char[] charArray = userInput.toCharArray();
            Position nextPos = avatar;
            boolean getReadyForQuit = false;
            int sCounter = 1;

            // this is so if the first thing is n, then we know the seed ends at s so we want to record
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
                    generateEnemies(s);
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
                move();
            }
        }
    }

    /**
     * @Return whether the player could hypothetically move onto the Position @param p.
     */
    private boolean checkValid(Position p) {
        TETile tile = world[p.getX()][p.getY()];
        return tile != wallType && tile != Tileset.NOTHING;
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
     * If no starting pos of the avatar is given, generate one
     * @param world
     * @param r
     * @return
     */
    private Position generateStartingPos(TETile[][] world, Random r) {
        while (true) {
            int x = RandomUtils.uniform(r, 0, Engine.WIDTH);
            int y = RandomUtils.uniform(r, 0, Engine.HEIGHT);
            Position temp = new Position(x, y);
            if (isFloor(temp, world)) {
                return temp;
            }
        }
    }

    /**
     * @Return the type of tile at Position @param i in the world.
     */
    private static TETile getWorldTile(Position i, TETile[][] world) {
        return world[i.getX()][i.getY()];
    }

    /**
     * Draws the world state given a new position of the avatar @param i
     * @param ter
     * @param world
     * @param i
     */
    public void drawFrame(TERenderer ter, TETile[][] world, Position i) {

        // edu.princeton.cs.introcs.StdDraw
        if (Engine.inBounds(i)) {
            StdDraw.clear();
            TETile orgTile = world[i.getX()][i.getY()];
            world[i.getX()][i.getY()] = Tileset.AVATAR;

            ter.renderFrame(world);
            world[i.getX()][i.getY()] = orgTile;

            // basic structure for hud
            // it should be this in some sort of continuous loop
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
                StdDraw.text(4, 1, temp.toString());
            }
            StdDraw.text(2, Engine.HEIGHT - 1, "Lives: " + lives);
            StdDraw.text(Engine.WIDTH - 5, Engine.HEIGHT - 1, "Paths (T) " + togglePaths);
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
                            if (!powered && p.equals(power)) {
                                world[j][k] = Tileset.POWER;
                            } else if (!boosted && p.equals(heart)) {
                                world[j][k] = Tileset.HEART;
                            } else {
                                world[j][k] = floorType;
                            }
                        }
                    }
                }
            }
        } else {
            for (int j = 0; j < world.length; j += 1) {
                for (int k = 0; k < world[j].length; k += 1) {
                    if (world[j][k] == Tileset.PATH_TILE) {
                        Position p = new Position(j, k);
                        if (!powered && p.equals(power)) {
                            world[j][k] = Tileset.POWER;
                        } else if (!boosted && p.equals(heart)) {
                            world[j][k] = Tileset.HEART;
                        } else {
                            world[j][k] = floorType;
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns if the @param nextPos is a floor tile
     * @param nextPos
     * @param world
     * @return
     */
    private boolean isFloor(Position nextPos, TETile[][] world) {
        return getWorldTile(nextPos, world) == floorType;
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
     * -1 = Game over, player loses.
     * 0 = Game ongoing.
     * 1 = Game over, player wins!
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
     */
    private void move() {
        if (powered) {
            return;
        }
        for (int i = 0; i < enemies.size(); i += 1) {
            Position p = enemies.get(i);
            List<Position> sol = solver.get(i).solution();
            Position nextP = sol.get(1);
            if (!enemies.contains(nextP)) {
                int x = p.getX();
                int y = p.getY();
                if (!powered && p.equals(power)) {
                    world[x][y] = Tileset.POWER;
                } else if (!boosted && p.equals(heart)) {
                    world[x][y] = Tileset.HEART;
                } else {
                    world[x][y] = floorType;
                }
                enemies.set(i, nextP);
                if (nextP.equals(avatar)) {
                    lives -= 1;
                    avatar = startingPos;
                    objects.set(2, avatar);
                    objects.set(9, lives);
                    int s = enemies.size();
                    generateEnemies(s);
                    objects.set(6, enemies);
                    generatePaths();
                } else {
                    world[nextP.getX()][nextP.getY()] = Tileset.ENEMY;
                    enemyPaths.get(i).remove(p);
                }
            }
        }
    }

    public TETile[][] getWorld() {
        return world;
    }

    /**
     * Generate @param num enemies to random floor tiles.
     * Also clears the world of enemies in previous locations, if they existed.
     */
    private void generateEnemies(int num) {
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
                if (Engine.inBounds(enemyPos) && (world[x][y] == floorType
                        || world[x][y] == Tileset.PATH_TILE)) {
                    world[x][y] = Tileset.ENEMY;
                    enemies.add(enemyPos);
                    valid = true;
                }
            }
        }
        drawFrame(ter, world, avatar);
    }
}
