package byow.lab12;
import byow.Core.Utils.Position;
import byow.Core.Utils.RandomUtils;
import byow.Core.TileEngine.TETile;
import byow.Core.TileEngine.Tileset;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class Tessellation {
    private static Map<Integer, Integer> COLUMN_SIZES = Map.of(
      0, 3,
      1, 4,
      2, 5,
            3, 4,
            4, 3
    );
    private List<Hexagon> hexagons = new ArrayList<>();
    private static Map<Integer, TETile> TILES = Map.of(
            0, Tileset.FLOWER,
            1, Tileset.WALL,
            2, Tileset.AVATAR,
            3, Tileset.SAND
    );
    private int side;
    public Tessellation(int s) {
        side = s;
        Random random = new Random(42);
        int randomIndex = RandomUtils.uniform(random, 0, 4);
        TETile randomTile = TILES.get(randomIndex);
        Map<Integer, Position> startPos = new HashMap<>();
        Hexagon dummy = new Hexagon(side, new Position(0, 0), null);
        int tessWidth = 2 * side + 3 * dummy.getRowWidth(side);
        int tessHeight = 5 * (2 * side);
        int middle = tessWidth / 2;
        startPos.put(2, new Position(middle, tessHeight));
        Hexagon dummy2 = new Hexagon(side, startPos.get(2), null);
        Position col1upperLeft = new Position(dummy2.getLowLeft(), -side, -1);
        startPos.put(1, col1upperLeft);
        Position col3upperLeft = new Position(dummy2.getLowRight(), 1, -1);
        startPos.put(3, col3upperLeft);
        Hexagon dummy3 = new Hexagon(side, col3upperLeft, null);
        Position col0upperLeft = new Position(dummy.getLowLeft(), -side, -2);
        Position col4upperleft = new Position(dummy3.getLowRight(), 2, -2);
        startPos.put(0, col0upperLeft);
        startPos.put(4, col4upperleft);

        for (int i = 0; i < COLUMN_SIZES.size(); i += 1) {
            addColumn(startPos.get(i), COLUMN_SIZES.get(i));
        }
    }
    private void addColumn(Position start, int numofHex) {
        for (int i = 0; i < numofHex; i += 1) {
            Random random = new Random(42);
            int randomIndex = RandomUtils.uniform(random, 0, 4);
            TETile randomTile = TILES.get(randomIndex);
            Position p = new Position(start.getX(), start.getY() - i * 2 * side);
            Hexagon hex = new Hexagon(side, p, randomTile);
            hexagons.add(hex);
        }
    }

    public List<Hexagon> getHexagons() {
        return hexagons;
    }
}
