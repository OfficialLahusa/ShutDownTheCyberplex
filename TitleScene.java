
/**
 * Beschreiben Sie hier die Klasse TitleScene.
 * 
 * @author Lasse Huber-Saffer
 * @version 09.12.2021
 */
public class TitleScene extends Scene
{
    private Camera _camera;
    private BarAudioVisualizer _audioVisualizer;
    private boolean started = false;
    
    public TitleScene(GameState state)
    {
        super(state);
        
        _camera = new Camera();
        _camera.setPosition(new Vector3(0.0, 2, 5.0));
        _camera.setFov(30.0);
        
        _state.soundRegistry.loadSource("music2", "./res/sounds/midnight_drive.mp3");

    }
    
    public void handleInput(double deltaTime, double runTime)
    {
        if(!started && _state.inputHandler.isKeyPressed(KeyCode.KEY_SPACE))
        {
            started = true;
            
            Sound backgroundMusic = _state.soundRegistry.playSound("music2", 0.35, true);
        
            //backgroundMusic.setAudioSpectrumNumBands(256+128);
            backgroundMusic.setAudioSpectrumNumBands(96);
            backgroundMusic.setAudioSpectrumInterval(0.075);
            //backgroundMusic.setAudioSpectrumThreshold(-200);
            backgroundMusic.setAudioSpectrumThreshold(-80);
            
            _audioVisualizer = new BarAudioVisualizer(backgroundMusic);
            backgroundMusic.setAudioSpectrumListener(_audioVisualizer);
        }
    }
    
    public void update(double deltaTime, double runTime)
    {
        if(_audioVisualizer != null) _audioVisualizer.update(deltaTime);
    }
    
    public void draw(double deltaTime, double runTime)
    {
        _state.renderer.clear(10, 10, 10);
        if(_audioVisualizer != null) _audioVisualizer.draw(_state.renderer, _camera);
    }
}
