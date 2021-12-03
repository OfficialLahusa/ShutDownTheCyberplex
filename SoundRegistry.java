import java.util.*;
import javafx.scene.media.*;
import java.io.*;
import javafx.embed.swing.*;
import java.net.MalformedURLException;

/**
 * L�dt, speichert und spielt Sounddateien
 * 
 * @author Lasse Huber-Saffer 
 * @version 03.12.2021
 */
public class SoundRegistry
{
    public Map<String, MediaPlayer> sounds;

    /**
     * Konstruktor f�r Objekte der Klasse SoundPlayer
     */
    public SoundRegistry()
    {
        sounds = new HashMap<String, MediaPlayer>();
        // Trick: Initialisiert JavaFX Toolkit
        new JFXPanel();
    }
    
    /**
     * L�dt einen Sound von einem Dateipfad unter einem Namen in das Register
     * @param key Name, unter dem der Sound im Register gespeichert wird
     * @param filePath Dateipfad der Soundquelle
     */
    public void loadSound(String key, String filePath)
    {
        File mediaFile = new File(filePath);
        try
        {
            String url = mediaFile.toURI().toURL().toString();

            Media media = new Media(url);
            sounds.put(key, new MediaPlayer(media));
        }
        catch (MalformedURLException e)
        {
            System.out.println("Malformed URL");
        }
    }
    
    /**
     * Entfernt einen bestimmten Sound aus dem Register
     * @param key Name des zu l�schenden Sounds
     */
    public void clearSound(String key)
    {
        if(sounds.containsKey(key))
        {
            sounds.remove(key);
        }
    }
    
    /**
     * Gibt zur�ck, ob ein Sound mit einem bestimmten Schl�ssel im Register vorhanden ist
     * @return Wahrheitswert der Aussage "Der Sound mit dem gegebenen Schl�ssel ist im Register vorhanden"
     */
    public boolean contains(String key)
    {
        return sounds.containsKey(key);
    }
    
    /**
     * Leert das Soundregister
     */
    public void clear()
    {
        sounds.clear();
    }
}
