import java.util.*;

/**
 * Credits des Spiels
 * 
 * @author Nico Hädicke
 * @version 01.01.2022
 */
public class CreditScene extends Scene
{
    private Camera _camera;
    private StaticGameObject _credits;
    private StaticGameObject _title;
    
    private double _time;
    
    public CreditScene(GameState state)
    {
        super(state);
        
        _camera = new Camera();
        _camera.setPosition(new Vector3(0.0, 2, 5.0));
        _camera.setFov(30.0);
        
        HashMap<String, Mesh> titleScreenMeshes = _state.resourceManager.loadTitleScreenMeshes();
        
        _title = new StaticGameObject(titleScreenMeshes.get("title"), TurtleColor.CYAN, new Vector3 (0.0, 5, -14), new Vector3(), new Vector3(1.0, 1.0, 1.0));
        
        _time = 0;
    }

    /**
     * @see Scene#handleInput()
     */
    public void handleInput(double deltaTime, double runTime)
    {
        return;
    }
    
    /**
     * @see Scene#update()
     */
    public void update(double deltaTime, double runTime)
    {
        return;
    }
    
    /**
     * @see Scene#draw()
     */
    public void draw(double deltaTime, double runTime)
    {
        _state.renderer.clear(10, 10, 10);
        
        _title.draw(_state.renderer, _camera);
        _camera.setPosition(new Vector3(0.0, 14-_time*0.81, 5.0));
        _title.update(deltaTime, runTime, _camera.getPosition());
        
        _time += deltaTime;
        _state.textRenderer.write(new Vector2(45, 500-_time*40), 6, "Aufgrund eines Wettbewerbes mit der\nAufgabe: Malt etwas mit dem Projekt\nTurtleGraphics\nkamen wir auf die Idee\nein Spiel zu programmieren...", TurtleColor.WHITE);
        _state.textRenderer.write(new Vector2(150, 800-_time*40), 10, "Created by", TurtleColor.WHITE);
        _state.textRenderer.write(new Vector2(120, 850-_time*40), 8, "Lasse Huber-Saffer\n  Sven Schreiber\n  Nico Haedicke", TurtleColor.WHITE);

        if(_time > 24)
        {
            _state.scene = new TitleScene(_state);
        }
    }
}
