/**
 * Abstrakte Klasse für Spielszenen, die Input verarbeiten und Rendering durchführen
 * 
 * @author Lasse Huber-Saffer
 * @version 09.12.2021
 */
public abstract class Scene
{
    protected GameState _state;
    
    public Scene(GameState state)
    {
        _state = state;
    }
    
    public abstract void handleInput(double deltaTime, double runTime);
    
    public abstract void update(double deltaTime, double runTime);
    
    public abstract void draw(double deltaTime, double runTime);
}
