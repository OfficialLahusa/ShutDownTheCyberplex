
/**
 * Haupt-Szene des Spiels
 * 
 * @author Lasse Huber-Saffer
 * @version 09.12.2021
 */
public class GameScene extends Scene
{
    private MapHandler _mapHandler;
    private Camera _camera;
    private DynamicViewModelGameObject _test, _muzzleFlash, _pistolMain, _primaryMain, _sniperMain, _pistolDetails, _primaryDetails, _sniperDetails, _pistolHandsIdle, _pistolHandsShot, _primaryHandsIdle, _primaryHandsShot;
    
    public GameScene(GameState state)
    {
        super(state);
        
        _mapHandler = new MapHandler();
        _camera = new Camera(new Vector3(0.0, 2.0, 10.0), 1.0, 90.0);
        _muzzleFlash = new DynamicViewModelGameObject(_state.objLoader.loadFromFile("./res/models/guns/muzzleFlash.obj"), "blau", new Vector3 (-2, -2.2,-11));
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
        
        _mapHandler.load("TestMap");
        _camera.setPosition(_mapHandler.getMap().getPlayerSpawn());
        
        _state.soundRegistry.loadSource("music1", "./res/sounds/to_the_front.mp3");
        _state.soundRegistry.loadSource("powerup3", "./res/sounds/Powerup3.wav");
        
        _state.soundRegistry.playSound("music1", 0.2, true);
        
        _state.inputHandler.setKeepMouseInPlace(true);
    }
    
    public void handleInput(double deltaTime, double runTime)
    {
        double deltaX = _state.inputHandler.getAndResetMouseDelta().getX();
        if(_state.inputHandler.getKeepMouseInPlace())
        {
            _camera.rotateYaw(0.20 * deltaX);
        }
        
        if(_state.inputHandler.isKeyPressed(KeyCode.KEY_W))
        {
            _camera.move(_camera.getDirection().multiply(-6.5 * deltaTime));
        }
        if(_state.inputHandler.isKeyPressed(KeyCode.KEY_S))
        {
            _camera.move(_camera.getDirection().multiply(6.5 * deltaTime));
        }
        if(_state.inputHandler.isKeyPressed(KeyCode.KEY_A))
        {
            //_camera.rotateYaw(-120.0 * deltaTime);
            _camera.move(_camera.getRight().multiply(-6.0 * deltaTime));
        }
        if(_state.inputHandler.isKeyPressed(KeyCode.KEY_D))
        {
            //_camera.rotateYaw(120.0 * deltaTime);
            _camera.move(_camera.getRight().multiply(6.0 * deltaTime));
        }
        if(_state.inputHandler.isKeyPressed(KeyCode.KEY_ESCAPE))
        {
            _state.inputHandler.setKeepMouseInPlace(false);
        }
        if(_state.inputHandler.isKeyPressed(KeyCode.KEY_SPACE))
        {
            _state.inputHandler.setKeepMouseInPlace(true);
        }
        if(_state.inputHandler.isKeyPressed(KeyCode.KEY_PLUS))
        {
            _state.soundRegistry.playSound("powerup3", 0.2, false);
        }
    }
    
    public void update(double deltaTime, double runTime)
    {
        // Berechnet Map-LOD-Stufen in Abhängigkeit von der Kameraposition neu
        _mapHandler.getMap().updateLOD(_camera.getPosition());
        // Sortiert Mapgeometrie so, dass die Objekte in der Reihenfolge ihrer Distanz zur Kamera sortiert sind.
        _mapHandler.getMap().reorderAroundCamera(_camera.getPosition());
        
        // Entfernt bereits durchgelaufene Sounds
        _state.soundRegistry.removeStoppedSounds();
    }
    
    public void draw(double deltaTime, double runTime)
    {
        // Cleart das Bild
        _state.renderer.clear(0, 0, 0);
        
        Vector2i tilePos = MapHandler.worldPosToTilePos(_camera.getPosition());
        _state.textRenderer.write(new Vector2(10,30), 5, "Pos: X:" + tilePos.getX() + ", Z:" + tilePos.getY(), "rot");
            
        // Rendering
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
        
        _mapHandler.getMap().draw(_state.renderer, _camera);
        
        // X-, Y- und Z-Achse zeichnen
        _state.renderer.drawAxis(_camera);
    }
}
