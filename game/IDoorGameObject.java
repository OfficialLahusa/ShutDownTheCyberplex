package game;

import maths.*;
import core.*;
import physics.*;
import javafx.util.*;
import java.util.*;

/**
 * Interface für GameObjects mit Tür-Funktionalität
 * 
 * @author Lasse Huber-Saffer
 * @version 03.01.2022
 */

public interface IDoorGameObject extends IGameObject
{
    /**
     * Updated das Türobjekt
     * @param deltaTime Deltazeit des vorigen Frames
     * @param runTime Laufzeit des Programms
     * @param cameraPosition Position der Kamera im dreidimensionalen Raum
     */
    public void update(double deltaTime, double runTime, Vector3 cameraPosition);
    
    /**
     * Gibt zurück, ob die Tür offen ist
     * @return Offenheit der Tür
     */
    public boolean isOpen();
    
    /**
     * Gibt zurück, ob die z-Achse durch die Öffnung dieser Tür hindurchgeht
     * @return Ob die z-Achse durch die Öffnung dieser Tür hindurchgeht
     */
    public boolean isFacingZ();
    
    /**
     * Setzt die Offenheit der Tür
     * @param isOpen Offenheit der Tür
     */
    public void setOpen(boolean isOpen);
    
    /**
     * Setzt die Map, in der sich das Türobjekt befindet.
     * Diese Map-Referenz wird benötigt, damit das Türschloss funktioniert
     * @param map GridMap, in der sich die Tür befindet
     */
    public void setMap(GridMap map);
    
    /**
     * Gibt die Position der Tür-Tile im Grid zurück
     * @return Position der Tür-Tile im Grid
     */
    public Vector2i getTilePosition();
    
    /**
     * Gibt die IDs der verbundenen Räume der Tür zurück
     * @return Paar zweier Integer, die jeweils eine Raum-ID oder null, d.h. kein Raum, sind
     */
    public Pair<Integer, Integer> getConnectedRoomIDs();
    
    /**
     * Setzt die IDs der verbundenen Räume der Tür
     * @param first erster verbundener Raum, null -> kein erster Raum (Reihenfolge irrelevant)
     * @param second zweiter verbundener Raum, null -> kein zweiter Raum (Reihenfolge irrelevant)
     */
    public void setConnectedRoomIDs(Integer first, Integer second);
}
