package core;


/**
 * Der TimeManager verwaltet die Zeitberechnung
 * 
 * @author Lasse Huber-Saffer
 * @version 01.12.2021
 */
public class TimeManager
{
    private final long _startingMeasure;
    private long _currentRuntimeMeasure;
    private long _currentDeltaMeasure;

    /**
     * Parameterloser Konstruktor, der die Zeitmessung startet
     */
    public TimeManager()
    {
        _currentRuntimeMeasure = _currentDeltaMeasure = _startingMeasure = System.nanoTime();
    }
    
    /**
     * Gibt die vergangene Zeit seit Erstellung der Instanz in Sekunden zurück
     * @return vergangene Zeit in Sekunden seit Erstellung
     */
    public double getRunTime()
    {
        _currentRuntimeMeasure = System.nanoTime();
        return (_currentRuntimeMeasure - _startingMeasure) / 1e9;
    }
    
    /**
     * Gibt die vergangene Zeit seit der letzten Ausführung dieser Methode in Sekunden zurück
     * @return vergangene Zeit in Sekunden seit letztem Aufruf
     */
    public double getDeltaTime()
    {
        long newDeltaMeasure = System.nanoTime();
        double deltaTime = (newDeltaMeasure - _currentDeltaMeasure) / 1e9;
        _currentDeltaMeasure = newDeltaMeasure;
        return deltaTime;

    }
}
