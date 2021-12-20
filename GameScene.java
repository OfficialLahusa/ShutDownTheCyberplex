
/**
 * Haupt-Szene des Spiels
 * 
 * @author Lasse Huber-Saffer, Nico H�dicke, Sven Schreiber
 * @version 15.12.2021
 */
public class GameScene extends Scene
{
    private MapHandler _mapHandler;
    private DynamicViewModelGameObject _test, _muzzleFlash, _pistolMain, _primaryMain, _sniperMain, _pistolDetails, _primaryDetails, _sniperDetails, _pistolHandsIdle, _pistolHandsShot, _primaryHandsIdle, _primaryHandsShot;
    
    private Player _player;

    private static final double DEBUG_SPEED_MULT = 3.0;
    
    public GameScene(GameState state)
    {
        super(state);
        
        _mapHandler = new MapHandler(state);
        _muzzleFlash = new DynamicViewModelGameObject(_state.objLoader.loadFromFile("./res/models/guns/muzzleFlash.obj"), "cyan", new Vector3 (-2, -2.2,-11));
        _pistolMain = new DynamicViewModelGameObject(_state.objLoader.loadFromFile("./res/models/guns/new/pistolMain.obj"), "grau", new Vector3 (-1.5, -1,-10));
        _pistolDetails = new DynamicViewModelGameObject(_state.objLoader.loadFromFile("./res/models/guns/new/pistolDetails.obj"), "orange", new Vector3 (-1.5, -1,-10));
        _pistolHandsIdle = new DynamicViewModelGameObject(_state.objLoader.loadFromFile("./res/models/guns/new/pistolHandsIdle.obj"), "weiss", new Vector3 (-1.5, -1,-10));
        _pistolHandsShot = new DynamicViewModelGameObject(_state.objLoader.loadFromFile("./res/models/guns/new/pistolHandsShot.obj"), "weiss", new Vector3 (-1.5, -1,-10));
        _primaryMain = new DynamicViewModelGameObject(_state.objLoader.loadFromFile("./res/models/guns/new/primaryMain.obj"), "grau", new Vector3 (-2, -1,-11));
        _primaryDetails = new DynamicViewModelGameObject(_state.objLoader.loadFromFile("./res/models/guns/new/primaryDetails.obj"), "gelb", new Vector3 (-2, -1,-11));
        _primaryHandsIdle = new DynamicViewModelGameObject(_state.objLoader.loadFromFile("./res/models/guns/new/primaryHandsIdle.obj"), "weiss", new Vector3 (-2, -1,-11));
        _primaryHandsShot = new DynamicViewModelGameObject(_state.objLoader.loadFromFile("./res/models/guns/new/primaryHandsShot.obj"), "weiss", new Vector3 (-2, -1,-11));
        _sniperMain = new DynamicViewModelGameObject(_state.objLoader.loadFromFile("./res/models/guns/new/sniperMain.obj"), "grau", new Vector3 (-1.5, 1,-12));
        _sniperDetails = new DynamicViewModelGameObject(_state.objLoader.loadFromFile("./res/models/guns/new/sniperDetails.obj"), "gruen", new Vector3 (-1.5, 1,-12));
        
        _mapHandler.load("level_1_breakin");
        
        _player = new Player(_mapHandler.getMap().getPlayerSpawn());
        
        _state.soundRegistry.loadSource("music1", "./res/sounds/to_the_front.mp3");
        _state.soundRegistry.loadSource("powerup3", "./res/sounds/Powerup3.wav");
        _state.soundRegistry.loadSource("wooden_door_open", "./res/sounds/wooden_door_open.wav");
        _state.soundRegistry.loadSource("wooden_door_close", "./res/sounds/wooden_door_close.wav");
        
        _state.soundRegistry.playSound("music1", 0.2, true);
        
        _state.inputHandler.setKeepMouseInPlace(true);
    }
    
    /**
     * @see Scene#handleInput()
     */
    public void handleInput(double deltaTime, double runTime)
    {
        Camera playerCam = _player.getCamera();
        
        double deltaX = _state.inputHandler.getAndResetMouseDelta().getX();
        if(_state.inputHandler.getKeepMouseInPlace())
        {
            _player.rotate(new Vector3(0.0, 0.20 * deltaX, 0.0));
        }
        
        if(_state.inputHandler.isKeyPressed(KeyCode.KEY_W))
        {
            _player.move(playerCam.getDirection().multiply(DEBUG_SPEED_MULT * -6.5 * deltaTime));
        }
        if(_state.inputHandler.isKeyPressed(KeyCode.KEY_S))
        {
            _player.move(playerCam.getDirection().multiply(DEBUG_SPEED_MULT * 6.5 * deltaTime));
        }
        if(_state.inputHandler.isKeyPressed(KeyCode.KEY_A))
        {
            _player.move(playerCam.getRight().multiply(DEBUG_SPEED_MULT * -6.0 * deltaTime));
        }
        if(_state.inputHandler.isKeyPressed(KeyCode.KEY_D))
        {
            _player.move(playerCam.getRight().multiply(DEBUG_SPEED_MULT * 6.0 * deltaTime));
        }
        if(_state.inputHandler.isKeyPressed(KeyCode.KEY_ESCAPE))
        {
            _state.inputHandler.setKeepMouseInPlace(false);
        }
        if(_state.inputHandler.isKeyPressed(KeyCode.KEY_SPACE))
        {
            _state.inputHandler.setKeepMouseInPlace(true);
        }
    }
    
    /**
     * @see Scene#update()
     */
    public void update(double deltaTime, double runTime)
    {
        // Updatet die Map
        _mapHandler.getMap().update(deltaTime, runTime, _player.getCamera().getPosition());
        
        // Entfernt bereits durchgelaufene Sounds
        _state.soundRegistry.removeStoppedSounds();
        
        // Kollisionsbehandlung
        CircleCollider playerCollider = _player.getCollider();
        _mapHandler.getMap().handleCollisions(playerCollider);
    }
    
    /**
     * @see Scene#draw()
     */
    public void draw(double deltaTime, double runTime)
    {
        Camera playerCam = _player.getCamera();
        
        // Cleart das Bild
        _state.renderer.clear(0, 0, 0);
        
        // Draw Map
        _mapHandler.getMap().draw(_state.renderer, playerCam);
        
        // Draw Viewmodel
        if(_state.inputHandler.isKeyPressed(KeyCode.MOUSE_BUTTON_LEFT))
        {
            _pistolHandsShot.draw(_state.renderer, playerCam);
            _muzzleFlash.draw(_state.renderer, playerCam);
        }
        else
        {
            _pistolHandsIdle.draw(_state.renderer, playerCam);
        }
        _pistolMain.draw(_state.renderer, playerCam);
        _pistolDetails.draw(_state.renderer, playerCam);
        
        // Draw UI
        _state.renderer.drawCrosshair(8.0, 6.0, "gruen");
        _state.renderer.drawHealthbar(_player);
        
        Vector2i tilePos = MapHandler.worldPosToTilePos(playerCam.getPosition());
        _state.textRenderer.write(new Vector2(10,30), 5, "Pos: X:" + tilePos.getX() + ", Z:" + tilePos.getY(), "rot");
        
        // X-, Y- und Z-Achse zeichnen
        _state.renderer.drawAxis(playerCam);
    }
}
