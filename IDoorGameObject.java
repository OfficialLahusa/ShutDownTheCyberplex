import javafx.util.*;

/**
 * Interface für GameObjects mit Tür-Funktionalität
 * 
 * @author Lasse Huber-Saffer
 * @version 15.12.2021
 */

public interface IDoorGameObject
{
    /**
     * Updated das Türobjekt
     * @param deltaTime Deltazeit des aktuellen Frames
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
    
    /**
     * Setzt das Soundregister und die Soundschlüssel für das Öffnen und Schließen der Tür.
     * Parameter mit dem Wert null werden nicht verwendet.
     * @param soundReg setzt das Soundregister, indem die nachfolgenden Schlüssel enthalten sind (nullable)
     * @param openSoundKey Schlüssel des Sounds für das Öffnen (nullable)
     * @param closeSoundKey Schlüssel des Sounds für das Schließen (nullable)
     */
    public void setSound(SoundRegistry soundReg, String openSoundKey, String closeSoundKey);
}
