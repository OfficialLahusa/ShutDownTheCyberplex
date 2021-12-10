
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
    public Scene scene;
    
    /**
     * Konstruiert einen neuen GameState.
     * Die Szenenkomponente muss anschließend extern gesetzt werden.
     */
    public GameState()
    {
        renderer = new Renderer();
        timeManager = new TimeManager();
        inputHandler = new InputHandler();
        textRenderer = new TextRenderer(renderer);
        soundRegistry = new SoundRegistry();
        objLoader = new WavefrontObjectLoader();
        scene = null;
    }
}
