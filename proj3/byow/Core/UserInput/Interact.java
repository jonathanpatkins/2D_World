package byow.Core.UserInput;

import byow.Core.Engine;

import byow.Core.Utils.*;
import byow.TileEngine.*;
import byow.Core.WorldComponents.*;
import edu.princeton.cs.introcs.StdDraw;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Interact {

    TERenderer ter;
    TETile[][] world;
    Random random;
    Position avatar, power, startingPos;
    String userInput;
    private TETile floorType, wallType;
    private ArrayList<Position> enemies;
    private int lives;
    private boolean powered, play;
    private ArrayList<Object> objects;

    // now you can take input from keyboard to interact with world
    public Interact(ArrayList<Object> loadedObjects, String userInput) {
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
        this.lives = (int) loadedObjects.get(8);
        this.powered = (boolean) loadedObjects.get(9);
        objects = loadedObjects;
        objects.set(2, avatar);
        this.play = true;
        this.startingPos = avatar;


        // returns if the program should quit or not
        if (!doUserInput(userInput)) {
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
                //TODO: Make motion less glitchy/more intelligent
                if (play) {
                    //move();
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
                    }
                } else {
                    StdDraw.setPenColor(Color.WHITE);
                    Font result = new Font("Monaco", Font.BOLD, 48);
                    StdDraw.setFont(result);
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
                    objects.set(8, lives);
                    drawFrame(ter, world, avatar);
                    return;
                }
            } else if (checkPowerCollision(next)) {
                world[next.getX()][next.getY()] = floorType;
                powered = true;
                objects.set(9, powered);
                for (int i = 0; i < enemies.size(); i += 1) {
                    Position pos = enemies.get(i);
                    world[pos.getX()][pos.getY()] = Tileset.SCARED_ENEMY;
                }
            }
            if (Engine.inBounds(next) && isFloor(next, world)) {
                avatar = next;
                objects.set(2, avatar);
                drawFrame(ter, world, avatar);
            }
        } else {
            drawFrame(ter, world, avatar);
        }
    }

    /**
     * This method is for the autograder when it tests the code using "Program argements"
     * Essentially, this means that if given a seed N***SWWSSD or LDDD:Q it will be able to compile
     * For N***SWWSSD, if will use the seed *** and then it will use the moves WWSSD before displaying
     * the rendered world.
     * Fro LDDD:Q, this means it will load the world, move the avatar to the right three times and then save
     * and quit.
     * @param userInput this is the string put into program arguments - this is found and tested through
     *                  Run >> Edit Configurations >> Program arguments
     * @return boolean value for true if the program should quit and false to keep it going
     */
    private boolean doUserInput(String userInput) {
        boolean quit = true;
        if (userInput != null) {
            char[] charArray = userInput.toCharArray();
            Position nextPos = avatar;
            boolean getReadyForQuit = false;
            int sCounter = 1;

            if (userInput.charAt(0) == 'N') {
                sCounter = 0;
            }

            for (char c : charArray) {
                if (c == 'W') {
                    nextPos = new Position(avatar, 0, 1);
                    makeMoveFromInput(nextPos, c);
                } else if (c == 'A') {
                    nextPos = new Position(avatar, -1, 0);
                    makeMoveFromInput(nextPos, c);
                } else if (c == 'S' && sCounter > 0) {
                    nextPos = new Position(avatar, 0, -1);
                    makeMoveFromInput(nextPos, c);
                } else if (c == 'S') {
                    sCounter += 1;
                } else if (c == 'D') {
                    nextPos = new Position(avatar, 1, 0);
                    makeMoveFromInput(nextPos, c);
                } else if (c == ':') {
                    getReadyForQuit = true;
                } else if (c == 'Q' && getReadyForQuit) {
                    new SaveWorld(objects);
                    quit = true;
                }
            }
        }
        return true;
    }

    private void makeMoveFromInput(Position next, char c) {
        if (next != null) {
            if (checkEnemyCollision(next)) {
                if (powered) {
                    world[next.getX()][next.getY()] = floorType;
                    enemies.remove(next);
                } else {
                    lives -= 1;
                    avatar = startingPos;
                    objects.set(8, lives);
                    return;
                }
            } else if (checkPowerCollision(next)) {
                world[next.getX()][next.getY()] = floorType;
                powered = true;
                objects.set(9, powered);
                for (int i = 0; i < enemies.size(); i += 1) {
                    Position pos = enemies.get(i);
                    world[pos.getX()][pos.getY()] = Tileset.SCARED_ENEMY;
                }
            }
            if (Engine.inBounds(next) && isFloor(next, world)) {
                avatar = next;
                objects.set(2, avatar);

            }
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
            StdDraw.show();
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
        for (int i = 0; i < enemies.size(); i += 1) {
            if (pos.equals(enemies.get(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * @Return whether the Avatar at Position @param pos is colliding with any enemies.
     */
    private boolean checkPowerCollision(Position pos) {
        return !powered && pos.equals(power);
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
     * Moves the enemies randomly.
     * 1 = Move left
     * 2 = Move right
     * 3 = Move up
     * 4 = Move down
     * TODO: Make the movement somewhat intelligent, though not perfect!
     */
    private void move() {
        for (int i = 0; i < enemies.size(); i += 1) {
            int direction = RandomUtils.uniform(random, 1, 5);
            Position p = enemies.get(i);
            Position nextP = null;
            if (direction == 1) {
                nextP = new Position(p, -1, 0);
            } else if (direction == 2) {
                nextP = new Position(p, 1, 0);
            } else if (direction == 3) {
                nextP = new Position(p, 0, 1);
            } else {
                nextP = new Position(p, 0, -1);
            }
            if (Engine.inBounds(nextP) && (isFloor(nextP, world) || nextP.equals(avatar))) {
                world[p.getX()][p.getY()] = floorType;
                if (powered) {
                    world[nextP.getX()][nextP.getY()] = Tileset.SCARED_ENEMY;
                } else {
                    world[nextP.getX()][nextP.getY()] = Tileset.ENEMY;
                }
                drawFrame(ter, world, avatar);
            }
        }
    }

    public TETile[][] getWorld() {
        return world;
    }
}
