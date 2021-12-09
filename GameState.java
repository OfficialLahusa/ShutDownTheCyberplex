
/**
 * Geteilte Spielressourcen zwischen verschiedenen Szenen
 * 
 * @author Lasse Huber-Saffer
 * @version 09.12.2021
 */
public class GameState
{
    public Renderer renderer;
    public TimeManager timeManager;
    public InputHandler inputHandler;
    public TextRenderer textRenderer;
    public SoundRegistry soundRegistry;
    public WavefrontObjectLoader objLoader;
    
    public GameState()
    {
        renderer = new Renderer();
        timeManager = new TimeManager();
        inputHandler = new InputHandler();
        textRenderer = new TextRenderer(renderer);
        soundRegistry = new SoundRegistry();
        objLoader = new WavefrontObjectLoader();
    }
    
    public GameState(Renderer renderer, TimeManager timeManager, InputHandler inputHandler, TextRenderer textRenderer, SoundRegistry soundRegistry, WavefrontObjectLoader objLoader)
    {
        this.renderer = renderer;
        this.timeManager = timeManager;
        this.inputHandler = inputHandler;
        this.textRenderer = textRenderer;
        this.soundRegistry = soundRegistry;
        this.objLoader = objLoader;
    }
}
