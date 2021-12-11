
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
    private Renderer _renderer;
    private TextRenderer _textRenderer;
    private StaticGameObject _title;
    private StaticGameObject _mauern;
    private boolean started = false;
    
    
    public TitleScene(GameState state)
    {
        super(state);
        
        _camera = new Camera();
        _camera.setPosition(new Vector3(0.0, 2, 5.0));
        _camera.setFov(30.0);
        
        _state.soundRegistry.loadSource("music2", "./res/sounds/midnight_drive.mp3");
        
        _renderer = new Renderer();
        _textRenderer = new TextRenderer(_renderer);
        _title = new StaticGameObject(_state.objLoader.loadFromFile("./res/models/title.obj"), "cyan", new Vector3 (0.0, 5, -14));
        _mauern = new StaticGameObject(_state.objLoader.loadFromFile("./res/models/mauern.obj"), "cyan", new Vector3 (-1.48, 0.34, -2.5));
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
        
        if(_state.inputHandler.isKeyPressed(KeyCode.KEY_PLUS))
        {
            _state.soundRegistry.removeAllSounds();
            _state.scene = new GameScene(_state);
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
        _title.draw(_renderer, _camera);
        _mauern.draw(_renderer, _camera);
        
        //draw Start Button
        drawButton("Start");
        
        //draw Credits Button
        drawButton("Credits");
    }
    
    public void drawButton(String text)
    {
        Vector2 mousePos = _state.inputHandler.getLocalMousePos();
        int p = 0;
        
        //draw button text
        if(text == "Start")
        {
            _textRenderer.write(new Vector2(220.9,372), 6, text, "weiss");
        }
        else
        {
            p = 42;
            _textRenderer.write(new Vector2(250-37.65,414), 6,text, "weiss");
        }
        
        //draw button box
        if(mousePos.getX() > 196 && mousePos.getX() < 304 && mousePos.getY() > 364+p && mousePos.getY() < 396+p)
        {
            _renderer.drawLine(new Vector2(231, 364+p), new Vector2(269, 364+p), "magenta");
            _renderer.drawLine(new Vector2(269, 364+p), new Vector2(304, 370+p), "magenta");
            _renderer.drawLine(new Vector2(304, 370+p), new Vector2(304, 390+p), "magenta");
            _renderer.drawLine(new Vector2(304, 390+p), new Vector2(269, 396+p), "magenta");
            _renderer.drawLine(new Vector2(269, 396+p), new Vector2(231, 396+p), "magenta");
            _renderer.drawLine(new Vector2(231, 396+p), new Vector2(196, 390+p), "magenta");
            _renderer.drawLine(new Vector2(196, 390+p), new Vector2(196, 370+p), "magenta");
            _renderer.drawLine(new Vector2(196, 370+p), new Vector2(231, 364+p), "magenta");
        }
    }
}
