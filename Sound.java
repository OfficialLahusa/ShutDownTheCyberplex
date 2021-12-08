import javafx.scene.media.*;
import javafx.util.*;

// Notiz: Man m�ge denken, dieses Verhalten lie�e sich durch extends MediaPlayer l�sen, doch dies ist ein Trugschluss: MediaPlayer ist final und kann deshalb nicht extended werden, diese Klasse

/**
 * Abspielinstanz eines Sounds
 * Wrapper um MediaPlayer, um Loop-Verhalten einfacher zu erm�glichen
 * 
 * @author Lasse Huber-Saffer
 * @version 08.12.2021
 */
public class Sound
{
    private MediaPlayer _player;
    private boolean _loop;
    private String _sourceKey;
    
    /**
     * Erstellt einen Sound aus einer gegebenen Soundquelle und setzt sein Loop-Verhalten
     * @param media Soundquelle
     * @param sourceKey Key der Soundquelle im SoundRegistry
     * @param volume Lautst�rke [0.0, 1.0]
     * @param autoplay Sound spielt direkt ab, wenn true
     * @param loop Sound wird immer wiederholt, wenn true
     */
    public Sound(Media media, String sourceKey, double volume, boolean autoplay, boolean loop)
    {
        if(media == null)
        {
            throw new IllegalArgumentException("Media was null");
        }
        
        _player = new MediaPlayer(media);
        _sourceKey = sourceKey;
        _player.setVolume(volume);
        setLoop(loop);
        if(autoplay) _player.play();
    }
    
    /**
     * Gibt zur�ck, ob der Sound aktuell im Loop abgespielt werden soll
     * @return true, wenn der Sound im Loop abgespielt wird, sonst false
     */
    public boolean shouldLoop()
    {
        return _loop;
    }
    
    /**
     * Setzt das Loop-Verhalten des Sounds
     * @param loop ob der Sound im Loop abgespielt werden soll
     */
    public void setLoop(boolean loop)
    {
        _loop = loop;
        
        if(loop)
        {
            _player.setOnEndOfMedia(new Runnable() {
                @Override
                public void run() {
                    _player.seek(Duration.ZERO);
                    _player.play();
                }
            });
        }
        else
        {
            _player.setOnEndOfMedia(new Runnable() {
                @Override
                public void run() {
                    _player.stop();
                    System.out.println("Reached end of sound " + _sourceKey);
                }
            });
        }

    }
    
    /**
     * Gibt den Key der Soundquelle im SoundRegistry zur�ck
     * @return Key der Soundquelle im SoundRegistry
     */
    public String getSourceKey()
    {
        return _sourceKey;
    }
    
    /**
     * Spielt den Sound ab
     */
    public void play()
    {
        _player.play();
    }
    
    /**
     * Pausiert die Wiedergabe
     */
    public void pause()
    {
        _player.pause();
    }
    
    /**
     * Stoppt die Wiedergabe
     */
    public void stop()
    {
        _player.stop();
    }
    
    /**
     * Setzt die Lautst�rke
     * @param volume Lautst�rke [0.0, 1.0]
     */
    public void setVolume(double volume)
    {
        _player.setVolume(volume);
    }
    
    /**
     * Gibt die Lautst�rke zur�ck
     * @return Lautst�rke [0.0, 1.0]
     */
    public double getVolume()
    {
        return _player.getVolume();
    }
    
    /**
     * Gibt den MediaPlayer-Status des Sounds zur�ck
     * @return MediaPlayer-Status
     */
    public MediaPlayer.Status getStatus()
    {
        return _player.getStatus();
    }
    
    
}
