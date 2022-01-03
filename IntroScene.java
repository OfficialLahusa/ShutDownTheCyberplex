
/**
 * Szene, in der dem Spieler seine Mission vorgestellt wird
 * 
 * @author Lasse Huber-Saffer
 * @version 03.01.2022
 */
public class IntroScene extends Scene
{
    private String _missionStatement;
    private String _continuePrompt;
    
    public IntroScene(GameState state)
    {
        super(state);
        
        _missionStatement = "Mission Statement:\n"
            + "Break into the Cyberplex.\n"
            + "Deactivate any corrupted\nartificial intelligence.\n"
            + "Destroy the reactor core.\n";
            
        _continuePrompt = "SPACE: I came prepared!";
        
        // Mauszeiger zentrieren
        _state.inputHandler.setKeepMouseInPlace(true);
    }

    /**
     * @see Scene#handleInput()
     */
    public void handleInput(double deltaTime, double runTime)
    {
        if(_state.inputHandler.isKeyPressed(KeyCode.KEY_SPACE))
        {
            _state.scene = new GameScene(_state);
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
        
        _state.textRenderer.write(new Vector2(45, 45), 6, _missionStatement, TurtleColor.WHITE);
        _state.textRenderer.write(new Vector2(45, 430), 6, _continuePrompt, TurtleColor.YELLOW);
    }
}
