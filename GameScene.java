
/**
 * Haupt-Szene des Spiels
 * 
 * @author Lasse Huber-Saffer, Nico Hädicke, Sven Schreiber
 * @version 15.12.2021
 */
public class GameScene extends Scene
{
    private MapHandler _mapHandler;
    private Camera _camera;
    private DynamicViewModelGameObject _test, _muzzleFlash, _pistolMain, _primaryMain, _sniperMain, _pistolDetails, _primaryDetails, _sniperDetails, _pistolHandsIdle, _pistolHandsShot, _primaryHandsIdle, _primaryHandsShot;
    
    private Player _player;
    
    private double roomChangeCooldown = 0.0;
    private static final double DEBUG_SPEED_MULT = 3.0;
    
    public GameScene(GameState state)
    {
        super(state);
        
        _mapHandler = new MapHandler(state);
        _camera = new Camera(new Vector3(0.0, 2.0, 10.0), 1.0, 90.0);
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
        
        _player = new Player();
        
        _mapHandler.load("level_1_breakin");
        _camera.setPosition(_mapHandler.getMap().getPlayerSpawn());
        
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
        double deltaX = _state.inputHandler.getAndResetMouseDelta().getX();
        if(_state.inputHandler.getKeepMouseInPlace())
        {
            _camera.rotateYaw(0.20 * deltaX);
        }
        
        if(_state.inputHandler.isKeyPressed(KeyCode.KEY_W))
        {
            _camera.move(_camera.getDirection().multiply(DEBUG_SPEED_MULT * -6.5 * deltaTime));
        }
        if(_state.inputHandler.isKeyPressed(KeyCode.KEY_S))
        {
            _camera.move(_camera.getDirection().multiply(DEBUG_SPEED_MULT * 6.5 * deltaTime));
        }
        if(_state.inputHandler.isKeyPressed(KeyCode.KEY_A))
        {
            //_camera.rotateYaw(-120.0 * deltaTime);
            _camera.move(_camera.getRight().multiply(DEBUG_SPEED_MULT * -6.0 * deltaTime));
        }
        if(_state.inputHandler.isKeyPressed(KeyCode.KEY_D))
        {
            //_camera.rotateYaw(120.0 * deltaTime);
            _camera.move(_camera.getRight().multiply(DEBUG_SPEED_MULT * 6.0 * deltaTime));
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
        _mapHandler.getMap().update(deltaTime, runTime, _camera.getPosition());
        // Entfernt bereits durchgelaufene Sounds
        _state.soundRegistry.removeStoppedSounds();
        
        roomChangeCooldown -= deltaTime;
        if(roomChangeCooldown <= 0.0) roomChangeCooldown = 0.0;
    }
    
    /**
     * @see Scene#draw()
     */
    public void draw(double deltaTime, double runTime)
    {
        // Cleart das Bild
        _state.renderer.clear(0, 0, 0);
        
        // Draw Map
        _mapHandler.getMap().draw(_state.renderer, _camera);
        
        // Draw Viewmodel
        if(_state.inputHandler.isKeyPressed(KeyCode.MOUSE_BUTTON_LEFT))
        {
            _pistolHandsShot.draw(_state.renderer, _camera);
            _muzzleFlash.draw(_state.renderer, _camera);
        }
        else
        {
            _pistolHandsIdle.draw(_state.renderer, _camera);
        }
        _pistolMain.draw(_state.renderer, _camera);
        _pistolDetails.draw(_state.renderer, _camera);
        
        // Draw UI
        _state.renderer.drawHealthbar(_player);
        
        Vector2i tilePos = MapHandler.worldPosToTilePos(_camera.getPosition());
        _state.textRenderer.write(new Vector2(10,30), 5, "Pos: X:" + tilePos.getX() + ", Z:" + tilePos.getY(), "rot");
        
        // X-, Y- und Z-Achse zeichnen
        _state.renderer.drawAxis(_camera);
    }
}
