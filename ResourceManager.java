import java.util.*;

/**
 * Verwaltet das Laden von Ressourcendateien
 * 
 * @author Lasse Huber-Saffer
 * @version 24.12.2021
 */
public class ResourceManager
{
    private WavefrontObjectLoader _objLoader;
    
    /**
     * Konstruktor f�r Objekte der Klasse ResourceManager
     */
    public ResourceManager()
    {
        _objLoader = new WavefrontObjectLoader();
    }
    
    /**
     * L�dt alle f�r die Erstellung von Entities ben�tigten Meshes aus Dateien
     * @return HashMap, in der die geladenen Meshes unter ihren jeweiligen Keys registriert sind
     */
    public HashMap<String, Mesh> loadEntityMeshes()
    {
        // Initialisierung der Entity Meshes
        HashMap<String, Mesh> entityMeshes = new HashMap<String, Mesh>();
        
        entityMeshes.put("turret_inactive", _objLoader.loadFromFile(Directory.MODEL + "turret/turret_inactive.obj"));
        entityMeshes.put("turret_active", _objLoader.loadFromFile(Directory.MODEL + "turret/turret_active.obj"));
        entityMeshes.put("turret_muzzle_flash", _objLoader.loadFromFile(Directory.MODEL + "turret/turret_muzzle_flash.obj"));
        entityMeshes.put("drone_active", _objLoader.loadFromFile(Directory.MODEL + "drone/drone.obj"));
        entityMeshes.put("drone_inactive", _objLoader.loadFromFile(Directory.MODEL + "drone/drone.obj"));
        entityMeshes.put("drone_rotor", _objLoader.loadFromFile(Directory.MODEL + "drone/drone_rotor.obj"));
        entityMeshes.put("health_powerup", _objLoader.loadFromFile(Directory.MODEL + "item/health_powerup.obj"));
        
        return entityMeshes;
    }
    
    /**
     * L�dt alle f�r die Konstruktion der Mapgeometrie ben�tigten Meshes aus Dateien
     * @return HashMap, in der die geladenen Meshes unter ihren jeweiligen Keys registriert sind
     */
    public HashMap<String, Mesh> loadTileMeshes()
    {
        // Initialisierung der Tile Meshes
        HashMap<String, Mesh> tileMeshes = new HashMap<String, Mesh>();
        
        tileMeshes.put("dirt_floor_borderless", _objLoader.loadFromFile(Directory.MODEL + "dirt_floor_borderless.obj"));
        tileMeshes.put("dirt_floor_borderless_lod1", _objLoader.loadFromFile(Directory.MODEL + "dirt_floor_borderless_lod1.obj"));
        tileMeshes.put("dirt_floor_borderless_lod2", _objLoader.loadFromFile(Directory.MODEL + "dirt_floor_borderless_lod2.obj"));
        tileMeshes.put("dirt_floor_borderless_lod3", _objLoader.loadFromFile(Directory.MODEL + "dirt_floor_borderless_lod3.obj"));
        tileMeshes.put("brick_wall", _objLoader.loadFromFile(Directory.MODEL + "brick_wall.obj"));
        tileMeshes.put("wooden_door", _objLoader.loadFromFile(Directory.MODEL + "wooden_door.obj"));
        tileMeshes.put("wooden_door_handle", _objLoader.loadFromFile(Directory.MODEL + "wooden_door_handle.obj"));
        tileMeshes.put("wooden_door_open", _objLoader.loadFromFile(Directory.MODEL + "wooden_door_open.obj"));
        tileMeshes.put("wooden_door_handle_open", _objLoader.loadFromFile(Directory.MODEL + "wooden_door_handle_open.obj"));
        tileMeshes.put("dirt_floor_grassdetail", _objLoader.loadFromFile(Directory.MODEL + "dirt_floor_grassdetail.obj"));
        tileMeshes.put("road_markings_x", _objLoader.loadFromFile(Directory.MODEL + "road_markings_x.obj"));
        tileMeshes.put("dirt_floor_grassdetail2", _objLoader.loadFromFile(Directory.MODEL + "dirt_floor_grassdetail2.obj"));
        tileMeshes.put("dirt_floor_stonedetail", _objLoader.loadFromFile(Directory.MODEL + "dirt_floor_stonedetail.obj"));
        tileMeshes.put("road_markings_z", _objLoader.loadFromFile(Directory.MODEL + "road_markings_z.obj"));
        
        return tileMeshes;
    }
    
    /**
     * L�dt alle ben�tigten Soundquellen aus Dateien in eine gegebene SoundEngine
     * @param soundEngine SoundEngine, in die die Soundquellen hineingeladen werden
     */
    public void loadSoundSources(SoundEngine soundEngine)
    {
        soundEngine.loadSource("music1", Directory.SOUND + "/music/to_the_front.mp3");
        
        soundEngine.loadSource("wooden_door_open", Directory.SOUND + "tile/wooden_door_open.wav");
        soundEngine.loadSource("wooden_door_close", Directory.SOUND + "tile/wooden_door_close.wav");
        
        soundEngine.loadSource("pistol1", Directory.SOUND + "weapon/pistol/Laser_Shoot.wav");
        soundEngine.loadSource("pistol2", Directory.SOUND + "weapon/pistol/Laser_Shoot3.wav");
        soundEngine.loadSource("pistol3", Directory.SOUND + "weapon/pistol/Laser_Shoot4.wav");
        soundEngine.loadSource("pistol4", Directory.SOUND + "weapon/pistol/Laser_Shoot5.wav");
        
        soundEngine.loadSource("heavy_shot1", Directory.SOUND + "turret/shot/Heavy_Shot.wav");
        soundEngine.loadSource("heavy_shot2", Directory.SOUND + "turret/shot/Heavy_Shot2.wav");
        soundEngine.loadSource("heavy_shot3", Directory.SOUND + "turret/shot/Heavy_Shot3.wav");
        
        soundEngine.loadSource("turret_reloading", Directory.SOUND + "turret/reload/reloading.wav");
        soundEngine.loadSource("turret_tactical_reload", Directory.SOUND + "turret/reload/tactical_reload.wav");
        soundEngine.loadSource("turret_restocking_ammunition", Directory.SOUND + "turret/reload/restocking_ammunition.wav");
        
        soundEngine.loadSource("turret_system_failure", Directory.SOUND + "turret/death/system_failure.wav");
        soundEngine.loadSource("turret_offline", Directory.SOUND + "turret/death/turret_offline.wav");
        
        soundEngine.loadSource("health_powerup_collected", Directory.SOUND + "item/health_powerup_collected.wav");
        
        soundEngine.loadSource("pain1", Directory.SOUND + "player/pain1.wav");
        soundEngine.loadSource("pain2", Directory.SOUND + "player/pain2.wav");
        soundEngine.loadSource("pain3", Directory.SOUND + "player/pain3.wav");
        soundEngine.loadSource("pain4", Directory.SOUND + "player/pain4.wav");
        soundEngine.loadSource("pain5", Directory.SOUND + "player/pain5.wav");
        soundEngine.loadSource("pain6", Directory.SOUND + "player/pain6.wav");
        soundEngine.loadSource("die1", Directory.SOUND + "player/die1.wav");
        soundEngine.loadSource("die2", Directory.SOUND + "player/die2.wav");
        
        // Soundgruppen erstellen
        soundEngine.createGroup("pistol_shot", new String[]{"pistol1", "pistol2", "pistol3", "pistol4"});
        soundEngine.createGroup("heavy_shot", new String[]{"heavy_shot1", "heavy_shot2", "heavy_shot3"});
        soundEngine.createGroup("turret_reload", new String[]{"turret_reloading", "turret_reloading", "turret_reloading", "turret_restocking_ammunition", "turret_tactical_reload"});
        soundEngine.createGroup("turret_death", new String[]{"turret_system_failure", "turret_offline", "turret_offline"});
        soundEngine.createGroup("pain", new String[]{"pain1", "pain2", "pain3", "pain4", "pain5", "pain6"});
        soundEngine.createGroup("die", new String[]{"die1", "die2"});
    }
    
    /**
     * L�dt alle f�r das Viewmodel ben�tigten Meshes aus Dateien
     * @return HashMap, in der die geladenen Meshes unter ihren jeweiligen Keys registriert sind
     */
    public HashMap<String, Mesh> loadViewModelMeshes()
    {
        // Initialisierung der Tile Meshes
        HashMap<String, Mesh> viewModelMeshes = new HashMap<String, Mesh>();
        
        viewModelMeshes.put("muzzleFlash", _objLoader.loadFromFile(Directory.MODEL + "gun/muzzleFlash.obj"));
        viewModelMeshes.put("pistolMain", _objLoader.loadFromFile(Directory.MODEL + "gun/new/pistolMain.obj"));
        viewModelMeshes.put("pistolDetails", _objLoader.loadFromFile(Directory.MODEL + "gun/new/pistolDetails.obj"));
        viewModelMeshes.put("pistolHandsIdle", _objLoader.loadFromFile(Directory.MODEL + "gun/new/pistolHandsIdle.obj"));
        viewModelMeshes.put("pistolHandsShot", _objLoader.loadFromFile(Directory.MODEL + "gun/new/pistolHandsShot.obj"));
        viewModelMeshes.put("primaryMain", _objLoader.loadFromFile(Directory.MODEL + "gun/new/primaryMain.obj"));
        viewModelMeshes.put("primaryDetails", _objLoader.loadFromFile(Directory.MODEL + "gun/new/primaryDetails.obj"));
        viewModelMeshes.put("primaryHandsIdle", _objLoader.loadFromFile(Directory.MODEL + "gun/new/primaryHandsIdle.obj"));
        viewModelMeshes.put("primaryHandsShot", _objLoader.loadFromFile(Directory.MODEL + "gun/new/primaryHandsShot.obj"));
        viewModelMeshes.put("sniperMain", _objLoader.loadFromFile(Directory.MODEL + "gun/new/sniperMain.obj"));
        viewModelMeshes.put("sniperDetails", _objLoader.loadFromFile(Directory.MODEL + "gun/new/sniperDetails.obj"));
        
        return viewModelMeshes;
    }
    
    /**
     * L�dt alle f�r den Titelbildschirm ben�tigten Meshes aus Dateien
     * @return HashMap, in der die geladenen Meshes unter ihren jeweiligen Keys registriert sind
     */
    public HashMap<String, Mesh> loadTitleScreenMeshes()
    {
        // Initialisierung der Tile Meshes
        HashMap<String, Mesh> titleMeshes = new HashMap<String, Mesh>();
        
        titleMeshes.put("title", _objLoader.loadFromFile(Directory.MODEL + "title.obj"));
        titleMeshes.put("mauern", _objLoader.loadFromFile(Directory.MODEL + "mauern.obj"));
        
        return titleMeshes;
    }
}