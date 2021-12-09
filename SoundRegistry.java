import java.util.*;
import javafx.scene.media.*;
import java.io.*;
import javafx.embed.swing.*;
import java.net.MalformedURLException;

/**
 * Lädt, speichert und spielt Sounddateien sowie Quellen
 * 
 * @author Lasse Huber-Saffer 
 * @version 03.12.2021
 */
public class SoundRegistry
{
    private Map<String, Media> _soundSources;
    private ArrayList<Sound> _currentSounds;

    /**
     * Konstruktor für Objekte der Klasse SoundPlayer
     */
    public SoundRegistry()
    {
        _soundSources = new HashMap<String, Media>();
        _currentSounds = new ArrayList<Sound>();
        
        // Trick: Initialisiert JavaFX Toolkit
        new JFXPanel();
    }
    
    public void playSound(String sourceKey, double volume, boolean loop)
    {
        if(!_soundSources.containsKey(sourceKey))
        {
            System.out.println("[Error]: Tried to play a sound with nonexistant source key \"" + sourceKey + "\"");
            return;
        }
        _currentSounds.add(new Sound(_soundSources.get(sourceKey), sourceKey, volume, true, loop));
    }
    
    public void removeStoppedSounds()
    {
        // Entfernt iterativ die gestoppten Sounds
        for(Iterator<Sound> iter = _currentSounds.iterator(); iter.hasNext(); )
        {
            Sound elem = iter.next();
            if(elem == null || elem.getStatus() == MediaPlayer.Status.STOPPED || elem.getStatus() == null)
            {
                elem.dispose();
                iter.remove();
            }
        }
    }
    
    /**
     * Lädt eine Soundquelle von einem Dateipfad unter einem Namen in das Register
     * @param key Name, unter dem die Soundquelle im Register gespeichert wird
     * @param filePath Dateipfad der Soundquelle
     */
    public void loadSource(String key, String filePath)
    {
        File mediaFile = new File(filePath);
        try
        {
            String url = mediaFile.toURI().toURL().toString();

            Media media = new Media(url);
            _soundSources.put(key, media);
        }
        catch (MalformedURLException e)
        {
            System.out.println("Malformed URL");
        }
    }
    
    /**
     * Entfernt eine bestimmte Soundquelle aus dem Register
     * @param key Name der zu löschenden Soundquelle
     */
    public void clearSource(String key)
    {
        if(_soundSources.containsKey(key))
        {
            _soundSources.remove(key);
        }
    }
    
    /**
     * Gibt zurück, ob eine Soundquelle mit einem bestimmten Schlüssel im Register vorhanden ist
     * @return Wahrheitswert der Aussage "Die Soundquelle mit dem gegebenen Schlüssel ist im Register vorhanden"
     */
    public boolean containsSource(String key)
    {
        return _soundSources.containsKey(key);
    }
    
    /**
     * Leert das Soundregister
     */
    public void clearSources()
    {
        _soundSources.clear();
    }
}
