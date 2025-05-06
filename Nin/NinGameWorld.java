package Nin;

import engine.GameWorld;
import engine.UIkit.ViewPort;
import engine.gameobjects.*;
import engine.mapgeneration.DungeonLoader;
import engine.mapgeneration.Tile;
import engine.support.Vec2d;
import engine.systems.SaveAndLoadSystem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class NinGameWorld extends GameWorld {

    private gameCharacter knight; // the knight to be displayed
    private gameCharacter goblin;
    private animationPackage animationPackageNin = new animationPackage();
    // Define a gravity constant (adjust based on your game scale)
    private static final Vec2d GRAVITY = new Vec2d(0, 9.8); // Gravity force vector (downward direction)
    private DungeonLoader dungeonLoader = null;

    Tile[][] grid;
    private Vec2d startPosition;
    private Vec2d endPosition;
    private double rescaleAverage = 1;

    // Define where the map will start generating
    Vec2d mapOrigin = new Vec2d(0, 0);  // Starting point for the map generation (in pixels)

    // Define tile size (in pixels)
    private double TILE_SIZE_X = 32.0;
    private double TILE_SIZE_Y = 32.0;
    private Vec2d originalStageSize = new Vec2d(960, 540);
    private boolean reachDestination = false;
    private List<NinGameObject> gameObjects = new ArrayList<>();
    private List<GameObject> savedGameObjects = new ArrayList<>();
    List<GameObject> gameObjectsToRemove = new ArrayList<>();
    private double timer = 0;

    private Map<String, KeyCode> keyMappings = new HashMap<>();

    private SaveAndLoadSystem saveAndLoadSystem;

    private static final double FRICTION = 20; // Friction coefficient, adjustable as needed

    private int blockCounter = 1;

    public NinGameWorld(ViewPort viewPort) {
        super(viewPort);
    }

    @Override
    public void onStartUp(Vec2d currentStageSize) {
        super.onStartUp(currentStageSize);
        collisionSystem = new GameCollisionSystem(renderSystem, physicsSystem, currentStageSize, this);
        saveAndLoadSystem = new SaveAndLoadSystem(rescaleAverage);
        dungeonGenerator();
        setUpKnight();
        knight.setPosition(getStartPosition());
        saveGame("Nin/start.xml");
        keyMappings.put("Jump", KeyCode.W);
        keyMappings.put("Attack", KeyCode.SPACE);
    }


    @Override
    public void onTick(long nanosSincePreviousTick) {
        super.onTick(nanosSincePreviousTick);
        double deltaTime = nanosSincePreviousTick / 1_000_000_000.0;

        for (NinGameObject obj : gameObjects) {
            PhysicsComponent physics = obj.getComponent(PhysicsComponent.class);

            if (!obj.isGrounded()) {
                // Apply gravity when the object is in the air
                Vec2d gravityForce = new Vec2d(0, 9.8).smult(physics.getMass() * 5);
                physics.applyForce(gravityForce);
            } else {
                // Apply friction when the object is on the ground
                Vec2d currentVelocity = physics.getVelocity();

                // Calculate friction force in the opposite direction of velocity
                Vec2d frictionForce = currentVelocity.normalize().smult(-FRICTION * physics.getMass()); // 0.5 is the friction coefficient
                physics.applyForce(frictionForce);

                // Set vertical velocity to zero to prevent downward movement on the ground
                physics.setVelocity(new Vec2d(currentVelocity.x, 0));

                // Stop horizontal movement if it is close to zero to avoid jitter
                if (Math.abs(currentVelocity.x) < 0.1) {
                    physics.setVelocity(new Vec2d(0, 0));
                }
            }
        }

        applyPhysics(knight.getPhysics(), knight.isGrounded());
        applyPhysics(goblin.getPhysics(), goblin.isGrounded());

        // 更新定时器
        timer += deltaTime;

        // 当计时器超过 1 秒时，清除所有待删除的对象
        if (timer >= 0.5f) {
            for (GameObject obj : gameObjectsToRemove) {
                removeFromSystems(obj);
                gameObjects.remove(obj);
            }
            gameObjectsToRemove.clear();
            timer = 0; // 重置计时器
        }

    }

    public void applyPhysics(PhysicsComponent physicsComponent, boolean isGrounded) {
        if (isGrounded) {
            // Apply friction when grounded
            Vec2d currentVelocity = physicsComponent.getVelocity();
            Vec2d frictionForce = currentVelocity.normalize().smult(-FRICTION * physicsComponent.getMass());
            physicsComponent.applyForce(frictionForce);

            // Set vertical velocity to zero
            physicsComponent.setVelocity(new Vec2d(currentVelocity.x, 0));

            // Stop horizontal movement if it is close to zero
            if (Math.abs(currentVelocity.x) < 0.1) {
                physicsComponent.setVelocity(new Vec2d(0, 0));
            }
        } else {
            // Apply gravity when airborned
            Vec2d gravityForce = new Vec2d(0, 9.8).smult(physicsComponent.getMass() * 5);
            physicsComponent.applyForce(gravityForce);
        }
    }

    @Override
    public void onKeyPressed(KeyEvent e) {
        super.onKeyPressed(e);

        PhysicsComponent knightPhysics = knight.getPhysics();
        double movementSpeed = knight.getMovementSpeed();

        switch (e.getCode()) {
            case A:
                knightPhysics.setVelocity(new Vec2d(-movementSpeed, knightPhysics.getVelocity().y));
                knight.setFacingLeft(true);
                knight.setCurrentAnimation(animationPackageNin.knightRunAnimation);
                break;

            case D:
                knightPhysics.setVelocity(new Vec2d(movementSpeed, knightPhysics.getVelocity().y));
                knight.setFacingLeft(false);
                knight.setCurrentAnimation(animationPackageNin.knightRunAnimation);
                break;

            default:
                // use mapping to change key pressed
                if (e.getCode() == keyMappings.get("Jump")) {
                    if (knight.isGrounded()) {
                        // Apply a jump force instead of directly setting the velocity
                        Vec2d jumpForce = knight.getJumpForce();
                        Vec2d newJumpForce = new Vec2d(jumpForce.x * rescaleAverage, jumpForce.y * rescaleAverage);
                        knightPhysics.applyImpulse(newJumpForce);
                        knight.setGrounded(false);
                        knight.startBuffering(); // Start buffering when jumping
                    }
                } else if (e.getCode() == keyMappings.get("Attack")) {
                    fireRayFromKnight();
                }
                break;
        }

    }

    @Override
    public void onKeyReleased(KeyEvent e) {
        super.onKeyReleased(e);
        PhysicsComponent knightPhysics = knight.getPhysics();

        switch (e.getCode()) {
            case A:
            case D:
                // Stop horizontal movement when A or D is released
                knightPhysics.setVelocity(new Vec2d(0, knightPhysics.getVelocity().y));
                knight.setCurrentAnimation(animationPackageNin.knightIdleAnimation);
                break;
            case W:
                // No specific action needed for releasing jump (W).
                // Gravity will naturally bring the knight down.
                break;
            default:
                break;
        }

    }

    public void fireRayFromKnight() {
        // get the transform component of knight
        TransformComponent knightTransform = knight.getComponent(TransformComponent.class);
        if (knightTransform != null) {
            Vec2d knightPosition = knightTransform.getPosition();
            Vec2d knightSize = knightTransform.getSize();

            // get the knight facing
            boolean isFacingLeft = knight.isFacingLeft();

            // set the ray src and dir
            Vec2d rayStartPosition;
            Vec2d rayDirection;

            if (isFacingLeft) {
                // if face left
                rayStartPosition = knightPosition.minus(new Vec2d(1, - knightSize.y / 2));
                rayDirection = new Vec2d(-1, 0); // 射线向左
            } else {
                // if face right
                rayStartPosition = knightPosition.plus(new Vec2d(knightSize.x + 1, knightSize.y / 2));
                rayDirection = new Vec2d(1, 0); //a 射线向右
            }

            // create a ray
            TransformComponent rayTransform = new TransformComponent(rayStartPosition, new Vec2d(1, 5)); // 初始长度设置为1，宽度根据需求调整
            RayColliderComponent ray = new RayColliderComponent(
                    rayTransform,
                    rayDirection, // 使用动态设置的方向
                    false,
                    0.1, // 弹性系数
                    null // 射线不需要 PhysicsComponent
            );

            SpriteComponent raySprite = new SpriteComponent("/Nin/gameassets/fireball.png", rayTransform); // 替换为你的射线图像路径
            GameObject rayObject = new GameObject(-1); // 创建一个 GameObject 来持有射线组件
            rayObject.setZIndex(2);
            rayObject.addComponent(rayTransform);
            rayObject.addComponent(raySprite);
            rayObject.addComponent(ray);

            // reset the ray
            ray.resetClosestT();

            // use collisionsystem to get the closet object hit by the ray
            GameObject closestObject = collisionSystem.rayHitClosestObject(ray);
            if (closestObject != null) {
                TagComponent tagComponent = closestObject.getComponent(TagComponent.class);
                if (tagComponent != null && tagComponent.hasTag("destructible")) {
                    TransformComponent transformComponent = closestObject.getComponent(TransformComponent.class);
                    if (transformComponent != null) {
                        transformComponent.setPosition(new Vec2d(0, 1000)); // 移动对象到场景外，模拟破坏
                        System.out.println("Destructible block hit and moved.");
                    }
                }
            }

            // update the transform size
            double closestT = ray.getClosestT();
            if (closestT != -1) {
                Vec2d newSize = isFacingLeft ? new Vec2d(-closestT, rayTransform.getSize().y) : new Vec2d(closestT, rayTransform.getSize().y);
                rayTransform.setSize(newSize);
                knight.setCurrentAnimation(animationPackageNin.knightAttackAnimation);

                addToRenderSystem(rayObject);

                // apply impulse to the object hit by the ray
                double impulseStrength = 500; // 冲力大小
                ray.applyImpulseToHitObject(impulseStrength);

                // Add the rayObject to remove
                gameObjectsToRemove.add(rayObject);
                System.out.println("Hit object at t = " + closestT);
            } else {
                System.out.println("No collision detected.");
            }
        }
    }

    @Override
    public void onMousePressed(MouseEvent e) {
        super.onMousePressed(e);
    }

    @Override
    public void onMouseReleased(MouseEvent e) {
        super.onMouseReleased(e);
    }


    private void setUpKnight(){
        // Load the knightIdle and knightRun sprite sheets
        String idleImagePath = "/Nin/gameassets/knightIdle.png";
        String runImagePath = "/Nin/gameassets/knightRun.png";
        String attackImagePath = "/Nin/gameassets/knightAttack.png";

        // Create the TransformComponent for the knight
        TransformComponent knightTransform = new TransformComponent(getStartPosition(), new Vec2d(32, 32));
        TagComponent tag = new TagComponent();
        tag.addTag("knight");

        PhysicsComponent physicsComponent = new PhysicsComponent(50, knightTransform);

        // Create the knight idle and run animations
        int idleTotalFrames = 15, runTotalFrames = 8, attackTotalFrames = 13;
        double frameDuration = 0.1;
        animationPackageNin.knightIdleAnimation = new AnimationComponent(idleImagePath, knightTransform, idleTotalFrames, frameDuration, new Vec2d(30, 32));
        animationPackageNin.knightRunAnimation = new AnimationComponent(runImagePath, knightTransform, runTotalFrames, frameDuration, new Vec2d(30, 32));
        animationPackageNin.knightAttackAnimation = new AnimationComponent(attackImagePath, knightTransform, attackTotalFrames, frameDuration, new Vec2d(30, 32));
        // Create the knight character
        // Create the knight character
        knight = new gameCharacter(0, knightTransform, 100, 100.0, animationPackageNin.knightIdleAnimation, new Vec2d(32, 32), physicsComponent);
        knight.addComponent(tag);
        knight.addComponent(physicsComponent);

        // add center viewPort component
        CenterViewComponent centerView = new CenterViewComponent(knightTransform, viewPort);
        knight.addComponent(centerView);
        // Add knight to systems
        addToSystems(knight);
        savedGameObjects.add(knight);
    }

    public void setUpMonster(Vec2d bornPosition){
        // Load the goblinIdle and goblinRun sprite sheets
        String idleImagePath = "/Nin/gameassets/goblinIdle.png";
        String runImagePath = "/Nin/gameassets/goblinRun.png";
        String takeHitImagePath = "/Nin/gameassets/goblinTakeHit.png";

        // Create the TransformComponent for the knight
        TransformComponent goblinTransform = new TransformComponent(new Vec2d(bornPosition.x * rescaleAverage, bornPosition.y * rescaleAverage), new Vec2d(32, 32));
        TagComponent tag = new TagComponent();
        tag.addTag("goblin");
        tag.addTag("monsters");

        PhysicsComponent physicsComponent = new PhysicsComponent(50, goblinTransform);

        // Create the knight idle and run animations
        int idleTotalFrames = 4, runTotalFrames = 8, takeHitTotalFrames = 4;
        double frameDuration = 0.1;
        animationPackageNin.goblinIdleAnimation = new AnimationComponent(idleImagePath, goblinTransform, idleTotalFrames, frameDuration, new Vec2d(45, 40));
        animationPackageNin.goblinRunAnimation = new AnimationComponent(runImagePath, goblinTransform, runTotalFrames, frameDuration, new Vec2d(45, 40));
        animationPackageNin.goblinTakeHitAnimation = new AnimationComponent(takeHitImagePath, goblinTransform, takeHitTotalFrames, frameDuration, new Vec2d(45, 40));
        // Create the knight character
        goblin = new gameCharacter(1, goblinTransform, 100, 100.0, animationPackageNin.goblinIdleAnimation, new Vec2d(32, 32), physicsComponent);
        goblin.addComponent(tag);
        goblin.addComponent(physicsComponent);
        // Add knight to systems
        addToSystems(goblin);
        savedGameObjects.add(goblin);
    }


    private void dungeonGenerator(){
        // Load the level from a text file
        try {
            dungeonLoader = new DungeonLoader("Nin/dMap.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        grid = dungeonLoader.getGrid();
        int width = dungeonLoader.getWidth();
        int height = dungeonLoader.getHeight();

        // Iterate over the grid and generate game objects based on tile types
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile tile = grid[y][x];
                Vec2d tilePosition = new Vec2d(mapOrigin.x + x * TILE_SIZE_X, mapOrigin.y + y * TILE_SIZE_Y);
                switch (tile.getType()) {
                    case WALL:
                        createWall(tilePosition);
                        break;
                    case ROOM:
                        break;
                    case CORRIDOR:
                        break;
                    case START:
                        createStart(tilePosition);
                        startPosition = new Vec2d(x, y);
                        break;
                    case END:
                        createEnd(tilePosition);
                        endPosition = new Vec2d(x, y);
                        break;
                    case BORN:
                        setUpMonster(tilePosition);
                        break;
                    case AIR: // do nothing here
                        break;
                    case DESTRUCTIBLE:
                        blockCounter ++;
                        createDestructibleBlock(tilePosition);
                        break;
                    default:
                        // Empty space, do nothing
                        break;
                }
            }
        }
    }


    // Create wall game objects based on position
    private void createWall(Vec2d position) {
        NinGameObject wall = new NinGameObject(-1);
        TransformComponent transform = new TransformComponent(position, new Vec2d(TILE_SIZE_X, TILE_SIZE_Y));
        String wallImagePath = "/Nin/gameassets/wallTile.png";
        SpriteComponent sprite = new SpriteComponent(wallImagePath, transform);
        PhysicsComponent physicsComponent = new PhysicsComponent(Double.POSITIVE_INFINITY, transform);
        BoxColliderComponent collider = new BoxColliderComponent(transform, transform.getSize(), true, 0.1, physicsComponent);
        TagComponent tag = new TagComponent();
        tag.addTag("wall");
        wall.addComponent(transform);
        wall.addComponent(sprite);
        wall.addComponent(collider);
        wall.addComponent(tag);
        wall.addComponent(physicsComponent);
        wall.setZIndex(2);  // Wall ZIndex is 2
        addToSystems(wall);
    }

    // Create corridor game objects based on position
    private void createStart(Vec2d position) {
        NinGameObject start = new NinGameObject(-1);
        TransformComponent transform = new TransformComponent(position, new Vec2d(TILE_SIZE_X, TILE_SIZE_Y));
        String corridorImagePath = "/Nin/gameassets/startTile.png";
        SpriteComponent sprite = new SpriteComponent(corridorImagePath, transform);
        start.addComponent(transform);
        start.addComponent(sprite);
        start.setZIndex(1);
        addToRenderSystem(start);
    }

    // Create corridor game objects based on position
    private void createEnd(Vec2d position) {
        NinGameObject end = new NinGameObject(-1);
        TransformComponent transform = new TransformComponent(position, new Vec2d(TILE_SIZE_X, TILE_SIZE_Y));
        String corridorImagePath = "/Nin/gameassets/targetTile.png";
        SpriteComponent sprite = new SpriteComponent(corridorImagePath, transform);
        PhysicsComponent physicsComponent = new PhysicsComponent(Double.POSITIVE_INFINITY, transform);
        BoxColliderComponent collider = new BoxColliderComponent(transform, transform.getSize(), true, 0, physicsComponent);
        TagComponent tag = new TagComponent();
        tag.addTag("destination");
        end.addComponent(transform);
        end.addComponent(sprite);
        end.addComponent(collider);
        end.addComponent(tag);
        end.addComponent(physicsComponent);
        end.setZIndex(2);
        addToCollisionSystem(end);
        addToRenderSystem(end);
    }

    private void createDestructibleBlock(Vec2d position) {
        NinGameObject destructibleBlock = new NinGameObject(blockCounter);
        TransformComponent transform = new TransformComponent(position, new Vec2d(TILE_SIZE_X, TILE_SIZE_Y));
        String blockImagePath = "/Nin/gameassets/roomTile.png";
        SpriteComponent sprite = new SpriteComponent(blockImagePath, transform);
        PhysicsComponent physicsComponent = new PhysicsComponent(50, transform);
        BoxColliderComponent collider = new BoxColliderComponent(transform, transform.getSize(), true, 0.1, physicsComponent);
        TagComponent tag = new TagComponent();
        tag.addTag("destructible");
        destructibleBlock.addComponent(transform);
        destructibleBlock.addComponent(sprite);
        destructibleBlock.addComponent(collider);
        destructibleBlock.addComponent(tag);
        destructibleBlock.addComponent(physicsComponent);
        destructibleBlock.setZIndex(2);
        addToCollisionSystem(destructibleBlock);
        addToRenderSystem(destructibleBlock);
        gameObjects.add(destructibleBlock);
        savedGameObjects.add(destructibleBlock);
    }

    @Override
    public void onResize(Vec2d newSize, Vec2d oldSize) {
        super.onResize(newSize, oldSize);

        if (oldSize.x == 0 || oldSize.y == 0) {
            return;
        }
        double rescaleX = newSize.x / originalStageSize.x;
        double rescaleY = newSize.y / originalStageSize.y;
        rescaleAverage = (rescaleX + rescaleY) / 2;
        knight.onResize(rescaleAverage);
    }

    public Vec2d getStartPosition() {
        return new Vec2d(startPosition.x * rescaleAverage * 32, startPosition.y * rescaleAverage * 32);
    }

    public Vec2d getEndPosition() {
        return new Vec2d(endPosition.x * rescaleAverage * 32, endPosition.y * rescaleAverage * 32);
    }

    public Tile[][] getTerrainData() {
        return grid;
    }

    public gameCharacter getKnight() {
        return knight;
    }

    public List<NinGameObject> getGameObjectsWithTag(String tag) {
        List<NinGameObject> result = new ArrayList<>();
        for (NinGameObject obj : gameObjects) {
            TagComponent tagComponent = obj.getComponent(TagComponent.class);
            if (tagComponent != null && tagComponent.hasTag(tag)) {
                result.add(obj);
            }
        }
        return result;
    }

    // Method to add game object and store its initial position
    private void addGameObject(NinGameObject obj) {
        gameObjects.add(obj);
    }

    public void saveGame(String filePath) {
        saveAndLoadSystem.saveGame(filePath, savedGameObjects);
    }

    public void loadGame(String filePath) {
        saveAndLoadSystem.loadGame(filePath, savedGameObjects, rescaleAverage);
    }

    public void showKeyMappingOptions() {
        KeyCode currentJumpKey = keyMappings.getOrDefault("Jump", KeyCode.W);
        KeyCode currentAttackKey = keyMappings.getOrDefault("Attack", KeyCode.SPACE);

        // check, if the current is space and j, and then we change it to the other set
        if (currentJumpKey == KeyCode.SPACE && currentAttackKey == KeyCode.J) {
            keyMappings.put("Jump", KeyCode.W);
            keyMappings.put("Attack", KeyCode.SPACE);
            System.out.println("Key mappings updated: Jump -> W, Attack -> SPACE");
        } else {
            // otherwise, we change it to this set
            keyMappings.put("Jump", KeyCode.SPACE);
            keyMappings.put("Attack", KeyCode.J);
            System.out.println("Key mappings updated: Jump -> SPACE, Attack -> J");
        }
    }

    public KeyCode getKeyMapping(String action) {
        // if the current key contains mapping
        if (keyMappings.containsKey(action)) {
            return keyMappings.get(action);
        } else {
            // if no, just ignore it
            System.out.println("Key mapping for action '" + action + "' not found.");
            return null;
        }
    }

    public void setReachDestination(boolean reachDestination) {
        knight.setCurrentAnimation(animationPackageNin.knightIdleAnimation);
        this.reachDestination = reachDestination;
    }

    public boolean reachDestination() {
        return reachDestination;
    }

}
