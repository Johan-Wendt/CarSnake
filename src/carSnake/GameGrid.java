
package carSnake;

/**
 * @author johanwendt
 */
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import javafx.scene.paint.Color;
import java.util.HashSet;
import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

/**
 *This class the grid where the players move around. It also creates the block that
 * make the field smaller and smaller.
 */
public class GameGrid {
    private static double screenHeight;
    private static int blockSize;
    private static int gridSize;
    private static int playerStartpoint;
    
    public static final int SAFE_ZONE_DIAMETER = 4;
    public static final Color GAMEGRID_COLOR = Color.TRANSPARENT;
    public static final Color SAFE_ZONE_COLOR = Color.web("#4D4DFF");
    
    private static final int DEATH_SLOWNESS = Player.PLAYER_START_SLOWNESS * 2;
    //private static final int DEATH_SLOWNESS = 1;
   // private static final HashMap<Integer, BuildingBlock> gridList = new HashMap<>(GameEngine.BRICKS_PER_ROW * GameEngine.BRICKS_PER_ROW + 10);
    private static final HashSet<Integer> safeList = new HashSet<>(SAFE_ZONE_DIAMETER * SAFE_ZONE_DIAMETER);
    private final static BuildingBlock outsideBlock = new BuildingBlock(-100, Color.BLACK);
    
    //This block is returned from the grid if it gets asked about a grid id that it cannot
    //find. This is used for making the player move from one side to another on the 
    //field if no death blocks are in the way.
    
    //This value is used for the blocks that make tha field smaller and smaller.
    //For convinience this gridsize is normalized to number of blocks.
    private static int currentGridSize;
    
    private static int deathCounter = 0;
    private static int deathLocation = 0;
    private static int deathDirection = GameEngine.RIGHT;
    private static int deathPause = 1;
    private static boolean isDeathRunning = false;
    private static boolean firstRound = true;
    
    private static final Random random = new Random();
    
    private static final HashSet<BuildingBlock> deathList = new HashSet<>();
    
    private static Timeline deathLoop = new Timeline();

    /**
     * Constructor for the main game field grid. It creates the field from BuildingBlocks.
     */
    public GameGrid() {
        blockSize = UserInterface.getBlockSize();
        gridSize = UserInterface.getGridSize();
        playerStartpoint = (((gridSize - blockSize) * GameEngine.MULIPLIER_X) / blockSize) / 2 + (((gridSize - blockSize) / blockSize) / 2);
        currentGridSize = gridSize / blockSize;
        createSafeList();

        for(int i = 0; i < gridSize/blockSize; i ++) {
            for(int j = 0; j < gridSize/blockSize; j ++) {
                BuildingBlock block = new BuildingBlock(i * blockSize, j * blockSize, blockSize, (j + i * GameEngine.MULIPLIER_X));
                UserInterface.gameGridPane.getChildren().addAll(block.getRectangle(), block.getCircle());
            }
        }
        BuildingBlock.getBlock(playerStartpoint).setStartBlock(); 
    }
    /**
     * This creates the black death blocks that make the field smaller and smaller.
     * The method is called once every round by the thread that controlls game events
     * such as movement. The method returns the block id of created death block. This 
     * is used for killing players cought in the death builder.
     * @return id for the deathblock if a new one has been created or -1 if nothing happened.
     */
    
    public static void startDeathBuilder() {
        deathLoop.setCycleCount( Timeline.INDEFINITE );
        
        final long timeStart = System.currentTimeMillis();
        
        KeyFrame kf = new KeyFrame( Duration.millis(DEATH_SLOWNESS), new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                double t = (System.currentTimeMillis() - timeStart) / 1000.0; 
                deathBuilder();
            }
        });
        deathLoop.getKeyFrames().add( kf );
        deathLoop.play();
        GameEngine.addToOngoingTimelines(deathLoop);
    }
    
    public static void deathBuilder() {
        
        
        if(isInSafeZone(deathLocation) == true) {
            isDeathRunning = false;
            GameEngine.gameOver();
        }
        if(isDeathRunning) {
                if(!(deathCounter < currentGridSize -1)) {
                    changeDeathDirection();
                }
                BuildingBlock deathReturn = BuildingBlock.getBlock(deathLocation);
                handleCrash(deathReturn);
                deathReturn.revertBlock();
                deathReturn.setDeathBlock();
                deathCounter ++;
                deathLocation += deathDirection;
        }
    }
    
    //Checks if the end of the grid has been reached redirects the deathbuilder.
    private static void changeDeathDirection() {
        switch (deathDirection) {
            case GameEngine.RIGHT:
                deathDirection = GameEngine.DOWN;
                deathCounter = 0;
                if (!firstRound) {
                    currentGridSize--;
                }
                else firstRound = false;
                break;
            case GameEngine.DOWN:
                deathDirection = GameEngine.LEFT;
                deathCounter = 0;
                break;
            case GameEngine.LEFT:
                deathDirection = GameEngine.UP;
                deathCounter = 0;
                currentGridSize--;
                break;
            case GameEngine.UP:
                deathDirection = GameEngine.RIGHT;
                deathCounter = 0;
                break;
        }
    }
    private static void handleCrash(BuildingBlock block) {
        if(block.hasOccupant()) {
            VisibleObject crash = block.getOccupant();
            if(crash instanceof Player) {
                block.explodePlayer();
            }
            else {
                block.explode();
            }
        }
    }
    public static void reset() {
        /*
        HashSet<BuildingBlock> resetGrid = new HashSet<>(gridList.values());
        for(BuildingBlock block: resetGrid) {
            block.resetBlock();
        }
        */
        BuildingBlock.getBlock(playerStartpoint).setStartBlock(); 
        deathCounter = 0;
        deathLocation = 0;
        deathDirection = GameEngine.RIGHT;
        deathPause = 1;
        currentGridSize = gridSize / blockSize;
        isDeathRunning = true;
        firstRound = true;
        deathList.clear();
    }
    /***
    public static boolean killedByDeath(Collection body) {
        return !Collections.disjoint(body, deathList);
    }
    **/
    /**
     * Returns true if the death builder is still running and false if not.
     * @return true if death builder is still running.
     */
    public static boolean isDeathRunning() {
        return isDeathRunning;
    }
    /**
     * Returns the start position for the players.
     * @return start position given as the Buildiing Block id
     */
    public static int getStartPosition() {
        return playerStartpoint;
    }
    /**
     * Returns the size of the grid
     * @return The size of the grid in pixels
     */
    public static int getGridSize() {
        return gridSize;
    }
    /**
     * Returns the size of the BuildingBlocks the grid is made up of.
     * @return the size of the BuildingBlocks in pixels.
     */
    public static int getBlockSize() {
        return blockSize;
    }
    public void createSafeList() {
        int startPoint = playerStartpoint - SAFE_ZONE_DIAMETER - SAFE_ZONE_DIAMETER * GameEngine.MULIPLIER_X; 
        for(int i = 0; i < 2 * SAFE_ZONE_DIAMETER + 1; i++) {
            for(int j = 0; j < 2 * SAFE_ZONE_DIAMETER + 1; j++) {
                if(startPoint + i + j * GameEngine.MULIPLIER_X != playerStartpoint) {
                    safeList.add(startPoint + i + j * GameEngine.MULIPLIER_X);
                }
            }
        }
    }
    /**
     * Returns true if a block id belongs to a block that is in the zone
     * in the middle of the game field.
     * @param blockId id of block to be tested.
     * @return true if the block is in the safe zone
     */
    public static boolean isInSafeZone(int blockId) {
        return safeList.contains(blockId);
    }
    /**
     * Returns the block with the given block id or the outside block, that has block id -1, if
     * the block is not found.
     * @param blockId id of the requested block.
     * @return the BuildingBlock with the given id.
     */
    /*
    public static BuildingBlock getBlock(int blockId) {
        if(gridList.containsKey(blockId)) {
            return gridList.get(blockId);
        }
        return outsideBlock;
    }
    */
    /**
     * Used for getting a random block from the field, e.g. to place a bonus in.
     * It does not return blocks that have been occupied by the death builder.
     * @return a random BuildingBlock in the playable field.
     */
    
    public static BuildingBlock getRandomBlock() {
        //int adjustToMiddle = ((gridSize / blockSize) - currentGridSize)/2;
        Random random = new Random();
        int randomY = random.nextInt(GameEngine.BRICKS_PER_ROW);
        int randomX = random.nextInt(GameEngine.BRICKS_PER_ROW) * GameEngine.MULIPLIER_X;
        BuildingBlock randomBlock = BuildingBlock.getBlock(randomY + randomX);
        if(randomBlock.getBlockColor().equals(Color.TRANSPARENT) && !isInSafeZone(randomBlock.getBlockId())) {
            return randomBlock;
        }
        return outsideBlock;
    }
    
}
