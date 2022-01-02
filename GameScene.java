import java.util.*;

/**
 * Haupt-Szene des Spiels
 * 
 * @author Lasse Huber-Saffer, Nico Hädicke, Sven Schreiber
 * @version 02.01.2022
 */
public class GameScene extends Scene
{
    private MapHandler _mapHandler;
    private DynamicViewModelGameObject _test, _muzzleFlash, _pistolMain, _primaryMain, _sniperMain, _pistolDetails, _primaryDetails, _sniperDetails, _pistolHandsIdle, _pistolHandsShot, _primaryHandsIdle, _primaryHandsShot;
    
    private Player _player;
    private double _timeSinceLastShot;
    private ArrayList<RaycastHit> _raycast;
    private Vector3 _raycastSource;
    private Vector3 _raycastTarget;
    
    private LinkedList<PathNode> _path;
    private Vector2i _pathTarget;

    private static final double PISTOL_SHOT_COOLDOWN = 0.3;
    private static final double DEBUG_SPEED_MULT = 3.0;
    private static final boolean DEBUG_SHOW_RAYCAST = false;
    
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
        
        _mapHandler.load("TestMap4");
        
        _player = new Player(_mapHandler.getMap().getPlayerSpawn(), new Vector3(), _state.soundEngine);
        _mapHandler.getMap().globalColliders.add(_player.getCollider());
        _mapHandler.getMap().setPlayer(_player);
    
        _raycast = null;
        _raycastSource = null;
        _raycastTarget = null;
        
        _path = null;
        _pathTarget = null;
        
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
                _raycast = Physics.raycast(source, dir, dist, _mapHandler.getMap(), EnumSet.of(PhysicsLayer.SOLID, PhysicsLayer.ENEMY), EnumSet.of(PhysicsLayer.PLAYER));
                
                if(_raycast.size() > 0 && _raycast.get(_raycast.size() - 1).collider.getLayer() == PhysicsLayer.ENEMY)
                {
                    ICollisionListener listener = _raycast.get(_raycast.size() - 1).collider.getListener();
                    
                    if(listener instanceof ILivingEntity)
                    {
                        ILivingEntity livingEntity = (ILivingEntity)listener;
                        livingEntity.damage(12, "laser pistol shot");
                    }
                }
                
                
                // debug
                _raycastSource = new Vector3(_player.getPosition());
                Vector2 target = source.add(dir.normalize().multiply(dist));
                _raycastTarget = new Vector3(target.getX(), 0.0, target.getY());
                
                _state.soundEngine.playSoundFromGroup("pistol_shot", 0.7, false);
            }
            
            // Tastenbelegungen
            if(_state.inputHandler.isKeyPressed(KeyCode.KEY_W))
            {
                _player.move(camDir.multiply(DEBUG_SPEED_MULT * -6.5 * deltaTime));
            }
            if(_state.inputHandler.isKeyPressed(KeyCode.KEY_S))
            {
                _player.move(camDir.multiply(DEBUG_SPEED_MULT * 6.5 * deltaTime));
            }
            if(_state.inputHandler.isKeyPressed(KeyCode.KEY_A))
            {
                _player.move(camRight.multiply(DEBUG_SPEED_MULT * -6.0 * deltaTime));
            }
            if(_state.inputHandler.isKeyPressed(KeyCode.KEY_D))
            {
                _player.move(camRight.multiply(DEBUG_SPEED_MULT * 6.0 * deltaTime));
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
        
        if(_pathTarget != null)
        {
            _path = AStarPathSolver.solvePath(MapHandler.worldPosToTilePos(_player.getPosition()), _pathTarget, _mapHandler.getMap().rooms.get(_mapHandler.getMap().activeRoom));
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
        
        // Debug Raycast Drawing
        if(DEBUG_SHOW_RAYCAST && _raycast != null)
        {
            for(RaycastHit hit : _raycast)
            {
                _state.renderer.drawLine3D(new Vector3(hit.position.getX(), 0.0, hit.position.getY()), new Vector3(hit.position.getX(), 4.0, hit.position.getY()), playerCam);
            }
            
            _state.renderer.drawLine3D(_raycastSource, _raycastTarget, TurtleColor.CYAN, playerCam);
        }
        
        if(_path != null)
        {
            // Alle Indizes bis auf den letzten durchiterieren
            for(int i = 0; i < _path.size() - 1; i++)
            {
                PathNode current = _path.get(i);
                PathNode next = _path.get(i + 1);
                
                Vector3 pos1 = MapHandler.tilePosToWorldPos(current.getPosition());
                Vector3 pos2 = MapHandler.tilePosToWorldPos(next.getPosition());
                
                _state.renderer.drawLine3D(pos1, pos2, TurtleColor.RED, playerCam);
            }
        }
        
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
        
        // X-, Y- und Z-Achse zeichnen
        _state.renderer.drawAxis(playerCam);
    }
}
