import java.util.*;

/**
 * Haupt-Szene des Spiels
 * 
 * @author Lasse Huber-Saffer, Nico Hädicke, Sven Schreiber
 * @version 23.12.2021
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

    private static final double PISTOL_SHOT_COOLDOWN = 0.3;
    private static final double DEBUG_SPEED_MULT = 3.0;
    private static final boolean DEBUG_SHOW_RAYCAST = false;
    
    public GameScene(GameState state)
    {
        super(state);
        
        _mapHandler = new MapHandler(state);
        _muzzleFlash = new DynamicViewModelGameObject(_state.objLoader.loadFromFile(Directory.MODEL + "guns/muzzleFlash.obj"), "cyan", new Vector3 (-2, -2.2,-11));
        _pistolMain = new DynamicViewModelGameObject(_state.objLoader.loadFromFile(Directory.MODEL + "guns/new/pistolMain.obj"), "grau", new Vector3 (-1.5, -1,-10));
        _pistolDetails = new DynamicViewModelGameObject(_state.objLoader.loadFromFile(Directory.MODEL + "guns/new/pistolDetails.obj"), "orange", new Vector3 (-1.5, -1,-10));
        _pistolHandsIdle = new DynamicViewModelGameObject(_state.objLoader.loadFromFile(Directory.MODEL + "guns/new/pistolHandsIdle.obj"), "weiss", new Vector3 (-1.5, -1,-10));
        _pistolHandsShot = new DynamicViewModelGameObject(_state.objLoader.loadFromFile(Directory.MODEL + "guns/new/pistolHandsShot.obj"), "weiss", new Vector3 (-1.5, -1,-10));
        _primaryMain = new DynamicViewModelGameObject(_state.objLoader.loadFromFile(Directory.MODEL + "guns/new/primaryMain.obj"), "grau", new Vector3 (-2, -1,-11));
        _primaryDetails = new DynamicViewModelGameObject(_state.objLoader.loadFromFile(Directory.MODEL + "guns/new/primaryDetails.obj"), "gelb", new Vector3 (-2, -1,-11));
        _primaryHandsIdle = new DynamicViewModelGameObject(_state.objLoader.loadFromFile(Directory.MODEL + "guns/new/primaryHandsIdle.obj"), "weiss", new Vector3 (-2, -1,-11));
        _primaryHandsShot = new DynamicViewModelGameObject(_state.objLoader.loadFromFile(Directory.MODEL + "guns/new/primaryHandsShot.obj"), "weiss", new Vector3 (-2, -1,-11));
        _sniperMain = new DynamicViewModelGameObject(_state.objLoader.loadFromFile(Directory.MODEL + "guns/new/sniperMain.obj"), "grau", new Vector3 (-1.5, 1,-12));
        _sniperDetails = new DynamicViewModelGameObject(_state.objLoader.loadFromFile(Directory.MODEL + "guns/new/sniperDetails.obj"), "gruen", new Vector3 (-1.5, 1,-12));
        
        _mapHandler.load("TestMap2");
        
        _player = new Player(_mapHandler.getMap().getPlayerSpawn(), new Vector3(), _state.soundEngine);
        _mapHandler.getMap().globalColliders.add(_player.getCollider());
        _mapHandler.getMap().setPlayer(_player);
    
        _raycast = null;
        _raycastSource = null;
        _raycastTarget = null;
        
        // Soundquellen laden
        _state.soundEngine.loadSource("music1", Directory.SOUND + "/music/to_the_front.mp3");
        _state.soundEngine.loadSource("powerup3", Directory.SOUND + "Powerup3.wav");
        
        _state.soundEngine.loadSource("wooden_door_open", Directory.SOUND + "tile/wooden_door_open.wav");
        _state.soundEngine.loadSource("wooden_door_close", Directory.SOUND + "tile/wooden_door_close.wav");
        
        _state.soundEngine.loadSource("pistol1", Directory.SOUND + "weapon/pistol/Laser_Shoot.wav");
        _state.soundEngine.loadSource("pistol2", Directory.SOUND + "weapon/pistol/Laser_Shoot3.wav");
        _state.soundEngine.loadSource("pistol3", Directory.SOUND + "weapon/pistol/Laser_Shoot4.wav");
        _state.soundEngine.loadSource("pistol4", Directory.SOUND + "weapon/pistol/Laser_Shoot5.wav");
        
        _state.soundEngine.loadSource("heavy_shot1", Directory.SOUND + "turret/shot/Heavy_Shot.wav");
        _state.soundEngine.loadSource("heavy_shot2", Directory.SOUND + "turret/shot/Heavy_Shot2.wav");
        _state.soundEngine.loadSource("heavy_shot3", Directory.SOUND + "turret/shot/Heavy_Shot3.wav");
        
        _state.soundEngine.loadSource("turret_reloading", Directory.SOUND + "turret/reload/reloading.wav");
        _state.soundEngine.loadSource("turret_tactical_reload", Directory.SOUND + "turret/reload/tactical_reload.wav");
        _state.soundEngine.loadSource("turret_restocking_ammunition", Directory.SOUND + "turret/reload/restocking_ammunition.wav");
        
        _state.soundEngine.loadSource("turret_system_failure", Directory.SOUND + "turret/death/system_failure.wav");
        _state.soundEngine.loadSource("turret_offline", Directory.SOUND + "turret/death/turret_offline.wav");
        
        _state.soundEngine.loadSource("pain1", Directory.SOUND + "player/pain1.wav");
        _state.soundEngine.loadSource("pain2", Directory.SOUND + "player/pain2.wav");
        _state.soundEngine.loadSource("pain3", Directory.SOUND + "player/pain3.wav");
        _state.soundEngine.loadSource("pain4", Directory.SOUND + "player/pain4.wav");
        _state.soundEngine.loadSource("pain5", Directory.SOUND + "player/pain5.wav");
        _state.soundEngine.loadSource("pain6", Directory.SOUND + "player/pain6.wav");
        _state.soundEngine.loadSource("die1", Directory.SOUND + "player/die1.wav");
        _state.soundEngine.loadSource("die2", Directory.SOUND + "player/die2.wav");
        
        // Soundgruppen erstellen
        _state.soundEngine.createGroup("pistol_shot", new String[]{"pistol1", "pistol2", "pistol3", "pistol4"});
        _state.soundEngine.createGroup("heavy_shot", new String[]{"heavy_shot1", "heavy_shot2", "heavy_shot3"});
        _state.soundEngine.createGroup("turret_reload", new String[]{"turret_reloading", "turret_reloading", "turret_reloading", "turret_restocking_ammunition", "turret_tactical_reload"});
        _state.soundEngine.createGroup("turret_death", new String[]{"turret_system_failure", "turret_offline", "turret_offline"});
        _state.soundEngine.createGroup("pain", new String[]{"pain1", "pain2", "pain3", "pain4", "pain5", "pain6"});
        _state.soundEngine.createGroup("die", new String[]{"die1", "die2"});
        
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
        _timeSinceLastShot += deltaTime;
        
        // Updatet die Map
        _mapHandler.getMap().update(deltaTime, runTime, _player.getCamera().getPosition());
        
        // Entfernt bereits durchgelaufene Sounds
        _state.soundEngine.removeStoppedSounds();
        
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
        
        // Debug Raycast Drawing
        if(DEBUG_SHOW_RAYCAST && _raycast != null)
        {
            for(RaycastHit hit : _raycast)
            {
                _state.renderer.drawLine3D(new Vector3(hit.position.getX(), 0.0, hit.position.getY()), new Vector3(hit.position.getX(), 4.0, hit.position.getY()), playerCam);
            }
            
            _state.renderer.drawLine3D(_raycastSource, _raycastTarget, "cyan", playerCam);
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
        _state.renderer.drawCrosshair(8.0, 6.0, "gruen");
        _state.renderer.drawHealthbar(_player);
        
        Vector2i tilePos = MapHandler.worldPosToTilePos(playerCam.getPosition());
        _state.textRenderer.write(new Vector2(10,30), 5, "Pos: X:" + tilePos.getX() + ", Z:" + tilePos.getY(), "rot");
        
        // X-, Y- und Z-Achse zeichnen
        _state.renderer.drawAxis(playerCam);
    }
}
