import java.util.*;
import javafx.scene.media.*;
import java.io.*;
import javafx.embed.swing.*;
import java.net.MalformedURLException;

/**
 * L�dt, speichert und spielt Sounddateien sowie Quellen
 * 
 * @author Lasse Huber-Saffer 
 * @version 23.12.2021
 */
public class SoundEngine
{
    private HashMap<String, Media> _soundSources;
    private HashMap<String, String[]> _soundGroups;
    private ArrayList<Sound> _currentSounds;
    private Random _random;

    /**
     * Konstruktor f�r Objekte der Klasse SoundPlayer
     */
    public SoundEngine()
    {
        _soundSources = new HashMap<String, Media>();
        _soundGroups = new HashMap<String, String[]>();
        _currentSounds = new ArrayList<Sound>();
        _random = new Random();
        
        // Trick: Initialisiert JavaFX Toolkit
        new JFXPanel();
    }
    
    /**
     * Spielt einen Sound mit einem bestimmten Key, einer gegebenen Lautst�rke und wiederholt ihn optional
     * @param sourceKey Bezeichner der Soundquelle (muss vorhanden sein)
     * @param volume Lautst�rke [0.0, 1.0]
     * @param loop wenn true, wird der Sound unendlich wiederholt, sonst nicht
     */
    public Sound playSound(String sourceKey, double volume, boolean loop)
    {
        if(!_soundSources.containsKey(sourceKey))
        {
            throw new IllegalArgumentException("Tried to play a sound with nonexistant source key \"" + sourceKey + "\"");
        }
        
        // Sound instanziieren
        Sound sound = new Sound(_soundSources.get(sourceKey), sourceKey, volume, true, loop);
        
        _currentSounds.add(sound);
        
        return sound;
    }
    
    /**
     * Spielt einen zuf�lligen Sound aus einer Gruppe mit einer gegebenen Lautst�rke und wiederholt ihn optional
     * @param groupKey Bezeichner der Gruppe (muss vorhanden sein)
     * @param volume Lautst�rke [0.0, 1.0]
     * @param loop wenn true, wird der Sound unendlich wiederholt, sonst nicht
     */
    public Sound playSoundFromGroup(String groupKey, double volume, boolean loop)
    {
        if(!_soundGroups.containsKey(groupKey))
        {
            throw new IllegalArgumentException("Tried to play a sound with from nonexistant group with key \"" + groupKey + "\"");
        }
        
        String[] soundKeys = _soundGroups.get(groupKey);
        
        // Zuf�lligen Sound ausw�hlen
        int chosenID = _random.nextInt(soundKeys.length);
        
        // Sound abspielen
        return playSound(soundKeys[chosenID], volume, loop);
    }
    
    /**
     * Entfernt alle gestoppten Sounds aus dem internen Speicher
     */
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
     * Entfernt alle Sounds aus dem internen Speicher
     */
    public void removeAllSounds()
    {
        // Entfernt iterativ alle Sounds
        for(Iterator<Sound> iter = _currentSounds.iterator(); iter.hasNext(); )
        {
            Sound elem = iter.next();
            elem.dispose();
            iter.remove();
        }
    }
    
    /**
     * Entfernt alle registrierten Gruppen
     */
    public void clearGroups()
    {
        _soundGroups.clear();
    }
    
    /**
     * Erstellt eine Sound-Gruppe, aus der Sounds zuf�llig gespielt werden k�nnen
     * @param key Bezeichner der Gruppe
     * @param subkeys Array der jeweiligen Sound-Quellschl�ssel (nicht null, Gr��e > 0)
     */
    public void createGroup(String key, String[] subkeys)
    {
        if(subkeys == null)
        {
            throw new IllegalArgumentException("Group subkeys were null when creating group " + key);
        }
        
        if(subkeys.length == 0)
        {
            throw new IllegalArgumentException("Group subkeys were empty when creating group " + key);
        }
        
        if(_soundGroups.containsKey(key))
        {
            System.out.println("[Error] Soundregistry already contains a group with key " + key);
            return;
        }
        
        _soundGroups.put(key, (String[])subkeys.clone());
    }
    
    /**
     * L�dt eine Soundquelle von einem Dateipfad unter einem Namen in das Register
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
     * @param key Name der zu l�schenden Soundquelle
     */
    public void clearSource(String key)
    {
        if(_soundSources.containsKey(key))
        {
            _soundSources.remove(key);
        }
    }
    
    /**
     * Gibt zur�ck, ob eine Soundquelle mit einem bestimmten Schl�ssel im Register vorhanden ist
     * @return Wahrheitswert der Aussage "Die Soundquelle mit dem gegebenen Schl�ssel ist im Register vorhanden"
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
