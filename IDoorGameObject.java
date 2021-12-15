import javafx.util.*;

/**
 * Interface f�r GameObjects mit T�r-Funktionalit�t
 * 
 * @author Lasse Huber-Saffer
 * @version 15.12.2021
 */

public interface IDoorGameObject
{
    /**
     * Updated das T�robjekt
     * @param deltaTime Deltazeit des aktuellen Frames
     * @param runTime Laufzeit des Programms
     * @param cameraPosition Position der Kamera im dreidimensionalen Raum
     */
    public void update(double deltaTime, double runTime, Vector3 cameraPosition);
    
    /**
     * Gibt zur�ck, ob die T�r offen ist
     * @return Offenheit der T�r
     */
    public boolean isOpen();
    
    /**
     * Gibt zur�ck, ob die z-Achse durch die �ffnung dieser T�r hindurchgeht
     * @return Ob die z-Achse durch die �ffnung dieser T�r hindurchgeht
     */
    public boolean isFacingZ();
    
    /**
     * Setzt die Offenheit der T�r
     * @param isOpen Offenheit der T�r
     */
    public void setOpen(boolean isOpen);
    
    /**
     * Gibt die Position der T�r-Tile im Grid zur�ck
     * @return Position der T�r-Tile im Grid
     */
    public Vector2i getTilePosition();
    
    /**
     * Gibt die IDs der verbundenen R�ume der T�r zur�ck
     * @return Paar zweier Integer, die jeweils eine Raum-ID oder null, d.h. kein Raum, sind
     */
    public Pair<Integer, Integer> getConnectedRoomIDs();
    
    /**
     * Setzt die IDs der verbundenen R�ume der T�r
     * @param first erster verbundener Raum, null -> kein erster Raum (Reihenfolge irrelevant)
     * @param second zweiter verbundener Raum, null -> kein zweiter Raum (Reihenfolge irrelevant)
     */
    public void setConnectedRoomIDs(Integer first, Integer second);
    
    /**
     * Setzt das Soundregister und die Soundschl�ssel f�r das �ffnen und Schlie�en der T�r.
     * Parameter mit dem Wert null werden nicht verwendet.
     * @param soundReg setzt das Soundregister, indem die nachfolgenden Schl�ssel enthalten sind (nullable)
     * @param openSoundKey Schl�ssel des Sounds f�r das �ffnen (nullable)
     * @param closeSoundKey Schl�ssel des Sounds f�r das Schlie�en (nullable)
     */
    public void setSound(SoundRegistry soundReg, String openSoundKey, String closeSoundKey);
}
