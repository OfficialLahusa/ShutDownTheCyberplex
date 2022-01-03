import java.util.*;

/**
 * Viewmodel des Spielers.
 * Enthält die Hände und Waffen.
 * 
 * @author Lasse Huber-Saffer
 * @version 03.01.2022
 */
public class PlayerViewModel implements IGameObject
{
    private GameState _state;
    private MapHandler _mapHandler;
    private DynamicViewModelGameObject _test, _muzzleFlash, _pistolMain, _primaryMain, _sniperMain, _pistolDetails, _primaryDetails, _sniperDetails, _pistolHandsIdle, _pistolHandsShot, _primaryHandsIdle, _primaryHandsShot;
    
    private double _timeSinceLastShot;
    private boolean _hasEverClicked;

    private static final double PISTOL_SHOT_COOLDOWN = 0.3;

    /**
     * Konstruktor für GameObjects mit gegebenem Mesh (3D-Modell), Position, Rotation und Skalierung
     * @param viewModelMeshes Register, aus dem die Meshes für das ViewModel bezogen werden
     * @param state GameState, das das ViewModel benutzt
     * @param mapHandler Handler der aktuellen Map
     */
    public PlayerViewModel(HashMap<String, Mesh> viewModelMeshes, GameState state, MapHandler mapHandler)
    {
        _state = state;
        _mapHandler = mapHandler;
        
        _hasEverClicked = false;
        
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
    }
    
    /**
     * @see IGameObject#update()
     */
    public void update(double deltaTime, double runTime, Vector3 cameraPosition)
    {
        _timeSinceLastShot += deltaTime;
        
        // Muzzleflash skalieren
        double scaleValue = Math.max(0.0, 1.0 - _timeSinceLastShot / PISTOL_SHOT_COOLDOWN);
        _muzzleFlash.setScale(new Vector3(scaleValue, scaleValue, scaleValue));
        
        Player player = _mapHandler.getMap().getPlayer();
        
        // Schießen
        if(_state.inputHandler.isKeyPressed(KeyCode.MOUSE_BUTTON_LEFT) && _timeSinceLastShot >= PISTOL_SHOT_COOLDOWN)
        {
            // Timer zurücksetzen
            _timeSinceLastShot = 0.0;
            
            _hasEverClicked = true;
            
            Vector3 camDir = player.getCamera().getDirection();
            
            // Raycast vorbereiten
            Vector2 source = new Vector2(player.getPosition().getX(), player.getPosition().getZ());
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
    }
    
    /**
     * @see IGameObject#draw()
     */
    public void draw(Renderer renderer, Camera camera)
    {
        if(_hasEverClicked)
        {
            _muzzleFlash.draw(renderer, camera);    
        }
        
        if(_state.inputHandler.isKeyPressed(KeyCode.MOUSE_BUTTON_LEFT))
        {
            _pistolHandsShot.draw(renderer, camera);
        }
        else
        {
            _pistolHandsIdle.draw(renderer, camera);
        }
        
        _pistolMain.draw(renderer, camera);
        _pistolDetails.draw(renderer, camera);
    }
    
    /**
     * @see IGameObject#getCollider()
     */
    public ICollider getCollider()
    {
        return null;
    }
    
    /**
     * @see IGameObject#getPosition()
     */
    public Vector3 getPosition()
    {
        return new Vector3();
    }
    
    /**
     * @see IGameObject#getRotation()
     */
    public Vector3 getRotation()
    {
        return new Vector3();
    }
    
    /**
     * @see IGameObject#getScale()
     */
    public Vector3 getScale()
    {
        return new Vector3(1.0, 1.0, 1.0);
    }
    
    /**
     * @see IGameObject#getColor()
     */
    public TurtleColor getColor()
    {
        return null;
    }
    
    /**
     * @see IGameObject#setColor()
     */
    public void setColor(TurtleColor color)
    {
        return;
    }
}
