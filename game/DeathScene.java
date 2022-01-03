package game;

import maths.*;
import core.*;

/**
 * Szene, die nach dem Spielertod gezeigt wird
 * 
 * @author Lasse Huber-Saffer
 * @version 03.01.2022
 */
public class DeathScene extends Scene
{
    private String _title;
    private String _deathLine;
    private String _continuePrompt;
    
    public DeathScene(GameState state, String causeOfDeath)
    {
        super(state);
        
        _title = "You have met your demise!";
        _deathLine = "Cause of death : " + causeOfDeath;
        _continuePrompt = "SPACE: My failure shall be forgotten...";
    }

    /**
     * @see Scene#handleInput()
     */
    public void handleInput(double deltaTime, double runTime)
    {
        if(_state.inputHandler.isKeyPressed(KeyCode.KEY_SPACE))
        {
            _state.scene = new TitleScene(_state);
        }
        
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
        
        _state.textRenderer.write(new Vector2(45, 45), 8, _title, TurtleColor.RED);
        _state.textRenderer.write(new Vector2(45, 90), 6, _deathLine, TurtleColor.WHITE);
        _state.textRenderer.write(new Vector2(45, 430), 6, _continuePrompt, TurtleColor.YELLOW);
    }
}
