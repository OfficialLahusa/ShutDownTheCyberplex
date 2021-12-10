import javafx.scene.media.*;
import javafx.util.*;

// Notiz: Man möge denken, dieses Verhalten ließe sich durch extends MediaPlayer lösen, doch dies ist ein Trugschluss: MediaPlayer ist final und kann deshalb nicht extended werden, diese Klasse

/**
 * Abspielinstanz eines Sounds
 * Wrapper um MediaPlayer, um Loop-Verhalten einfacher zu ermöglichen
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
     * @param volume Lautstärke [0.0, 1.0]
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
        
        _player.setOnError(() -> System.out.println("Error : " + _player.getError().toString()));
        
        if(autoplay)
        {
            _player.setAutoPlay(true);
        }
    }
    
    /**
     * Gibt zurück, ob der Sound aktuell im Loop abgespielt werden soll
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
                }
            });
        }
    }
    
    /**
     * Gibt den Key der Soundquelle im SoundRegistry zurück
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
     * Setzt die Lautstärke
     * @param volume Lautstärke [0.0, 1.0]
     */
    public void setVolume(double volume)
    {
        _player.setVolume(volume);
    }
    
    /**
     * Gibt die Lautstärke zurück
     * @return Lautstärke [0.0, 1.0]
     */
    public double getVolume()
    {
        return _player.getVolume();
    }
    
    /**
     * Gibt den MediaPlayer-Status des Sounds zurück
     * @return MediaPlayer-Status
     */
    public MediaPlayer.Status getStatus()
    {
        return _player.getStatus();
    }
    
    /**
     * Macht die Ressourcen des Sounds frei. Danach darf der Sound nicht mehr benutzt werden.
     * Diese Methode soll nach der abschließenden Verwendung eines Sounds ausgeführt werden.
     */
    public void dispose()
    {
        _player.dispose();
    }
    
    /**
     * Setzt den AudioSpectrumListener des Sounds
     * @param listener zu verwendender AudioSpectrumListener
     */
    public void setAudioSpectrumListener(AudioSpectrumListener listener)
    {
        _player.setAudioSpectrumListener(listener);
    }
    
    /**
     * Gibt den Schwellwert des Audiospektrums zurück
     * @return Schwellwert des Audiospektrums
     */
    public int getAudioSpectrumThreshold()
    {
        return _player.getAudioSpectrumThreshold();
    }
    
    /**
     * Setzt den Schwellwert des Audiospektrums
     * @param threshold Schwellwert des Audiospektrums
     */
    public void setAudioSpectrumThreshold(int threshold)
    {
        _player.setAudioSpectrumThreshold(threshold);
    }
    
    /**
     * Gibt die Bandzahl des Audiospektrums zurück
     * @return Bandzahl des Audiospektrums
     */
    public int getAudioSpectrumNumBands()
    {
        return _player.getAudioSpectrumNumBands();
    }
    
    /**
     * Setzt die Bandzahl des Audiospektrums
     * @param numBands Bandzahl des Audiospektrums
     */
    public void setAudioSpectrumNumBands(int numBands)
    {
        _player.setAudioSpectrumNumBands(numBands);
    }
    
    /**
     * Gibt das Zeitintervall des Audiospektrums zurück
     * @return Zeitintervall des Audiospektrums
     */
    public double getAudioSpectrumInterval()
    {
        return _player.getAudioSpectrumInterval();
    }
    
    /**
     * Setzt das Zeitintervall des Audiospektrums
     * @param interval Zeitintervall des Audiospektrums
     */
    public void setAudioSpectrumInterval(double interval)
    {
        _player.setAudioSpectrumInterval(interval);
    }
    
}
