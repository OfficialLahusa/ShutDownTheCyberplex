package core;

/**
 * Abstrakte Klasse für Spielszenen, die Input verarbeiten und Rendering durchführen
 * 
 * @author Lasse Huber-Saffer
 * @version 16.12.2021
 */
public abstract class Scene
{
    protected GameState _state;
    
    public Scene(GameState state)
    {
        _state = state;
    }
    
    /**
     * Verarbeitet den Input des Nutzers (Maus / Tastatur)
     * @param deltaTime Deltazeit des vorigen Frames
     * @param runTime Laufzeit des Programms
     */
    public abstract void handleInput(double deltaTime, double runTime);
    
    /**
     * Updated die Szene
     * @param deltaTime Deltazeit des vorigen Frames
     * @param runTime Laufzeit des Programms
     */
    public abstract void update(double deltaTime, double runTime);
    
    /**
     * Zeichnet die Szene
     * @param deltaTime Deltazeit des vorigen Frames
     * @param runTime Laufzeit des Programms
     */
    public abstract void draw(double deltaTime, double runTime);
}
