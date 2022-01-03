import java.util.*;

/**
 * Haupt-Szene des Spiels
 * 
 * @author Lasse Huber-Saffer, Nico Hädicke, Sven Schreiber
 * @version 03.01.2022
 */
public class GameScene extends Scene
{
    private MapHandler _mapHandler;
    private DynamicViewModelGameObject _test, _muzzleFlash, _pistolMain, _primaryMain, _sniperMain, _pistolDetails, _primaryDetails, _sniperDetails, _pistolHandsIdle, _pistolHandsShot, _primaryHandsIdle, _primaryHandsShot;
    
    private Player _player;    
    private double _timeSinceLastShot;

    private static final double PISTOL_SHOT_COOLDOWN = 0.3;
    
    public GameScene(GameState state)
    {
        super(state);
        
        // Meshes laden
        HashMap<String, Mesh> entityMeshes = _state.resourceManager.loadEntityMeshes();
        HashMap<String, Mesh> particleMeshes = _state.resourceManager.loadParticleMeshes();
        HashMap<String, Mesh> tileMeshes = _state.resourceManager.loadTileMeshes();
        HashMap<String, Mesh> viewModelMeshes = _state.resourceManager.loadViewModelMeshes();
        
        _mapHandler = new MapHandler(tileMeshes, entityMeshes, particleMeshes, _state.soundEngine);
        
        // Viewmodel-Objekte initialisieren
        _muzzleFlash        = new DynamicViewModelGameObject(viewModelMeshes.get("muzzleFlash"),        TurtleColor.CYAN,   new Vector3 (-2, -2.2,-11), new Vector3(), new Vector3(1.0, 1.0, 1.0));
        _pistolMain         = new DynamicViewModelGameObject(viewModelMeshes.get("pistolMain"),         TurtleColor.GRAY,   new Vector3 (-1.5, -1,-10), new Vector3(), new Vector3(1.0, 1.0, 1.0));
        _pistolDetails      = new DynamicViewModelGameObject(viewModelMeshes.get("pistolDetails"),      TurtleColor.ORANGE, new Vector3 (-1.5, -1,-10), new Vector3(), new Vector3(1.0, 1.0, 1.0));
        _pistolHandsIdle    = new DynamicViewModelGameObject(viewModelMeshes.get("pistolHandsIdle"),    TurtleColor.WHITE,  new Vector3 (-1.5, -1,-10), new Vector3(), new Vector3(1.0, 1.0, 1.0));
        _pistolHandsShot    = new DynamicViewModelGameObject(viewModelMeshes.get("pistolHandsShot"),    TurtleColor.WHITE,  new Vector3 (-1.5, -1,-10), new Vector3(), new Vector3(1.0, 1.0, 1.0));
        _primaryMain        = new DynamicViewModelGameObject(viewModelMeshes.get("primaryMain"),        TurtleColor.GRAY,   new Vector3 (-2, -1,-11),   new Vector3(), new Vector3(1.0, 1.0, 1.0));
        _primaryDetails     = new DynamicViewModelGameObject(viewModelMeshes.get("primaryDetails"),     TurtleColor.YELLOW, new Vector3 (-2, -1,-11),   new Vector3(), new Vector3(1.0, 1.0, 1.0));
        _primaryHandsIdle   = new DynamicViewModelGameObject(viewModelMeshes.get("primaryHandsIdle"),   TurtleColor.WHITE,  new Vector3 (-2, -1,-11),   new Vector3(), new Vector3(1.0, 1.0, 1.0));
        _primaryHandsShot   = new DynamicViewModelGameObject(viewModelMeshes.get("primaryHandsShot"),   TurtleColor.WHITE,  new Vector3 (-2, -1,-11),   new Vector3(), new Vector3(1.0, 1.0, 1.0));
        _sniperMain         = new DynamicViewModelGameObject(viewModelMeshes.get("sniperMain"),         TurtleColor.GRAY,   new Vector3 (-1.5, 1,-12),  new Vector3(), new Vector3(1.0, 1.0, 1.0));
        _sniperDetails      = new DynamicViewModelGameObject(viewModelMeshes.get("sniperDetails"),      TurtleColor.GREEN,  new Vector3 (-1.5, 1,-12),  new Vector3(), new Vector3(1.0, 1.0, 1.0));
        
        _mapHandler.load("TestMap5");
        
        _player = new Player(_mapHandler.getMap().getPlayerSpawn(), new Vector3(), _state.soundEngine);
        _mapHandler.getMap().globalColliders.add(_player.getCollider());
        _mapHandler.getMap().setPlayer(_player);
        
        // Sounds aus Dateien laden
        _state.resourceManager.loadSoundSources(_state.soundEngine);
        
        // Musik-Loop starten
        _state.soundEngine.playSound("music1", 0.2, true);
        
        // Maus in Fenstermitte zentrieren
        _state.inputHandler.setKeepMouseInPlace(true);
    }
    
    /**
     * @see Scene#handleInput()
     */
    public void handleInput(double deltaTime, double runTime)
    {
        Camera playerCam = _player.getCamera();
        
        // Nicht-lebensgebundene Keybinds
        if(_state.inputHandler.isKeyPressed(KeyCode.KEY_ESCAPE))
        {
            _state.scene = new PauseScene(_state, this);
            _state.inputHandler.setKeepMouseInPlace(false);
        }
        if(_state.inputHandler.isKeyPressed(KeyCode.KEY_SPACE))
        {
            _state.inputHandler.setKeepMouseInPlace(true);
        }
        
        // Lebensgebundene Controls
        if(_player.isAlive())
        {
            // Maus-Bewegung zu Kamera-Yaw umwandeln
            double deltaX = _state.inputHandler.getAndResetMouseDelta().getX();
            if(_state.inputHandler.getKeepMouseInPlace())
            {
                _player.rotate(new Vector3(0.0, 0.20 * deltaX, 0.0));
            }
            
            Vector3 camDir = playerCam.getDirection();
            Vector3 camRight = playerCam.getRight();
            
            // Schießen
            if(_state.inputHandler.isKeyPressed(KeyCode.MOUSE_BUTTON_LEFT) && _timeSinceLastShot >= PISTOL_SHOT_COOLDOWN)
            {
                // Timer zurücksetzen
                _timeSinceLastShot = 0.0;
                
                // TODO: Energie abziehen
                
                // Raycast vorbereiten
                Vector2 source = new Vector2(_player.getPosition().getX(), _player.getPosition().getZ());
                Vector2 dir = new Vector2(camDir.getX(), camDir.getZ()).invert();
                double dist = 500.0;
                ArrayList<RaycastHit> raycast = Physics.raycast(source, dir, dist, _mapHandler.getMap(), EnumSet.of(PhysicsLayer.SOLID, PhysicsLayer.ENEMY), EnumSet.of(PhysicsLayer.PLAYER));
                
                if(raycast.size() > 0 && raycast.get(raycast.size() - 1).collider.getLayer() == PhysicsLayer.ENEMY)
                {
                    ICollisionListener listener = raycast.get(raycast.size() - 1).collider.getListener();
                    
                    if(listener instanceof ILivingEntity)
                    {
                        ILivingEntity livingEntity = (ILivingEntity)listener;
                        livingEntity.damage(12, "laser pistol shot");
                    }
                }
                
                _state.soundEngine.playSoundFromGroup("pistol_shot", 0.7, false);
            }
            
            // Tastenbelegungen
            if(_state.inputHandler.isKeyPressed(KeyCode.KEY_W))
            {
                _player.move(camDir.multiply(3.0 * -6.5 * deltaTime));
            }
            if(_state.inputHandler.isKeyPressed(KeyCode.KEY_S))
            {
                _player.move(camDir.multiply(3.0 * 6.5 * deltaTime));
            }
            if(_state.inputHandler.isKeyPressed(KeyCode.KEY_A))
            {
                _player.move(camRight.multiply(3.0 * -6.0 * deltaTime));
            }
            if(_state.inputHandler.isKeyPressed(KeyCode.KEY_D))
            {
                _player.move(camRight.multiply(3.0 * 6.0 * deltaTime));
            }
        }
    }
    
    /**
     * @see Scene#update()
     */
    public void update(double deltaTime, double runTime)
    {        
        _timeSinceLastShot += deltaTime;
        
        // Updatet die Map
        _mapHandler.getMap().update(deltaTime, runTime, _player.getCamera().getPosition());
        
        // Entfernt bereits durchgelaufene Sounds
        _state.soundEngine.removeStoppedSounds();
        
        // Abschluss des Spiels prüfen und zu Outro übergehen
        if(_mapHandler.getMap().isCompleted)
        {
            _state.soundEngine.clear();
            _state.scene = new OutroScene(_state);
        }
        
        // Spielertod prüfen und zu Todesbildschirm übergehen
        if(_player.hasFallen())
        {
            _state.soundEngine.clear();
            _state.scene = new DeathScene(_state, _player.getCauseOfDeath());
        }
        
        // Spieler updaten
        _player.update(deltaTime, runTime, _player.getCamera().getPosition());
        
        // Kollisionsbehandlung
        CircleCollider playerCollider = _player.getCollider();
        _mapHandler.getMap().handleCollisions(playerCollider);
        
        for(IGameObject entity : _mapHandler.getMap().rooms.get(_mapHandler.getMap().activeRoom).getEntities())
        {
            if(entity.getCollider() != null)
            {
                _mapHandler.getMap().handleCollisions(entity.getCollider());
            }
        }
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
        _state.renderer.drawCrosshair(8.0, 6.0, TurtleColor.GREEN);
        _state.renderer.drawHealthbar(_player);
        
        Vector2i tilePos = MapHandler.worldPosToTilePos(playerCam.getPosition());
        _state.textRenderer.write(new Vector2(10,30), 5, "Pos: X:" + tilePos.getX() + ", Z:" + tilePos.getY(), TurtleColor.RED);
        
        // (Debug) X-, Y- und Z-Achse zeichnen
        //_state.renderer.drawAxis(playerCam);
    }
}
