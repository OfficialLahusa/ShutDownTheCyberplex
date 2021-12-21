
/**
 * Compiletime-Register aller im Spiel verwendeten TileIDs
 * 
 * @author Lasse Huber-Saffer 
 * @version 13.12.2021
 */
public class Tile
{
    public static final int NONE = -1;
    
    // Geometry Tiles
    public static final int DIRT_FLOOR = 0;
    public static final int BRICK_WALL = 1;
    public static final int WOODEN_DOOR = 2;
    public static final int CYBER_DOOR = 3;
    public static final int CYBER_FLOOR = 4;
    public static final int CYBER_WALL = 5;
    public static final int GLASS = 6;
    public static final int ROAD = 7;
    public static final int INVISIBLE_WALL = 8;
    
    public static final int DIRT_FLOOR_GRASS = 20;
    public static final int CRACKED_BRICK_WALL_DOOR = 21;
    public static final int ROAD_MARKINGS_X = 27;
    
    public static final int DIRT_FLOOR_GRASS2 = 40;
    public static final int ROAD_MARKINGS_Z = 47;
    
    // Function Tiles
    public static final int PLAYER_SPAWN_DOWN = 60;
    public static final int PLAYER_SPAWN_RIGHT = 61;
    public static final int PLAYER_SPAWN_UP = 62;
    public static final int PLAYER_SPAWN_LEFT = 63;
    public static final int MAP_EXIT = 64;
    public static final int ROOM_FLOODFILL = 65;
    public static final int SPAWN_DRONE = 66;
    public static final int SPAWN_TURRET_ACTIVE = 67;
    public static final int SPAWN_TESLA_TOWER = 68;
    public static final int SPAWN_DUMMY_TARGET = 70;
    public static final int SPAWN_GENERIC_KEY = 72;
    public static final int GENERIC_LOCK = 73;
    public static final int SPAWN_RED_KEY = 74;
    public static final int RED_LOCK = 75;
    public static final int SPAWN_GREEN_KEY = 76;
    public static final int GREEN_LOCK = 77;
    public static final int SPAWN_BLUE_KEY = 78;
    public static final int BLUE_LOCK = 79;

    public static final int PLAYER_CHECKPOINT = 80;
    public static final int SPAWN_TURRET_INACTIVE = 87;
    public static final int SPAWN_HEALTH_POWERUP = 90;
    public static final int SPAWN_RED_KEY_DRONE = 94;
    
    public static final int TURRET_FOCUS_POINT = 100;
    public static final int PATROL_END = 101;
    public static final int SPAWN_AMMO_POWERUP = 110;
    
    public static final int PATROL_1 = 120;
    public static final int PATROL_2 = 121;
    public static final int PATROL_3 = 122;
    public static final int PATROL_4 = 123;
    
    public static final int PATROL_5 = 140;
    public static final int PATROL_6 = 141;
    public static final int PATROL_7 = 142;
    public static final int PATROL_8 = 143;
    
    /**
     * Gibt zurück, ob ein Tile-Typ solide ist
     * @return true, wenn Tile-Typ solide ist, sonst false
     */
    public static boolean isSolid(int tileType)
    {
        switch(tileType)
        {
            case BRICK_WALL:
            case CYBER_WALL:
            case INVISIBLE_WALL:
                return true;
            default:
                return false;
        }
    }
    
    /**
     * Gibt zurück, ob ein Tile-Typ semi-solide (also nur bedingt solide) ist
     * @return true, wenn Tile-Typ semi-solide (also nur bedingt solide) ist, sonst false
     */
    public static boolean isSemiSolid(int tileType)
    {
        switch(tileType)
        {
            case WOODEN_DOOR:
            case CYBER_DOOR:
            case CRACKED_BRICK_WALL_DOOR:
            case GLASS:
                return true;
            default:
                return false;
        }
    }
    
    /**
     * Gibt zurück, ob ein Tile-Typ eine Tür ist
     * @return true, wenn Tile-Typ eine normale Tür ist
     */
    public static boolean isDoor(int tileType)
    {
        return isNormalDoor(tileType) || isSecretDoor(tileType);
    }
    
    /**
     * Gibt zurück, ob ein Tile-Typ eine normale Tür ist
     * @return true, wenn Tile-Typ eine normale Tür ist, sonst false
     */
    public static boolean isNormalDoor(int tileType)
    {
        switch(tileType)
        {
            case WOODEN_DOOR:
            case CYBER_DOOR:
                return true;
            default:
                return false;
        }
    }
    
    /**
     * Gibt zurück, ob ein Tile-Typ eine Geheimtür ist
     * @return true, wenn Tile-Typ eine Geheimtür ist, sonst false
     */
    public static boolean isSecretDoor(int tileType)
    {
        switch(tileType)
        {
            case CRACKED_BRICK_WALL_DOOR:
                return true;
            default:
                return false;
        }        
    }
    
    /**
     * Gibt zurück, ob ein Tile-Typ nichts ist
     * @return true, wenn Tile-Typ nichts ist, sonst false
     */
    public static boolean isNone(int tileType)
    {
        return tileType == NONE;
    }
    
    /**
     * Gibt zurück, ob ein Tile-Typ nichts ist
     * @return true, wenn Tile-Typ nichts ist, sonst false
     */
    public static boolean isSolidOrNone(int tileType)
    {
        return isSolid(tileType) || isNone(tileType);
    }
}
