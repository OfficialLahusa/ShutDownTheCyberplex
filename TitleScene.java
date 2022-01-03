import java.util.*;

/**
 * Titelbildschirm und Hauptmenü des Spiels
 * 
 * @author Lasse Huber-Saffer, Nico Hädicke
 * @version 01.01.2022
 */
public class TitleScene extends Scene
{
    private Camera _camera;
    private BarAudioVisualizer _audioVisualizer;
    private StaticGameObject _title;
    private StaticGameObject _mauern;
    
    
    public TitleScene(GameState state)
    {
        super(state);
           
        _camera = new Camera();
        _camera.setPosition(new Vector3(0.0, 2, 5.0));
        _camera.setFov(30.0);
        
        _state.soundEngine.loadSource("music2", Directory.SOUND + "music/midnight_drive.mp3");
        
        Sound backgroundMusic = _state.soundEngine.playSound("music2", 0.35, true);
        
        // Musik konfigurieren
        backgroundMusic.setAudioSpectrumNumBands(96);
        backgroundMusic.setAudioSpectrumInterval(0.075);
        backgroundMusic.setAudioSpectrumThreshold(-80);
        
        _audioVisualizer = new BarAudioVisualizer(backgroundMusic, new Vector3(), new Vector3(), new Vector3(1.0, 0.65, 1.0), TurtleColor.MAGENTA);
        backgroundMusic.setAudioSpectrumListener(_audioVisualizer);
        
        HashMap<String, Mesh> titleScreenMeshes = _state.resourceManager.loadTitleScreenMeshes();
        
        _title = new StaticGameObject(titleScreenMeshes.get("title"), TurtleColor.CYAN, new Vector3 (0.0, 5, -14), new Vector3(), new Vector3(1.0, 1.0, 1.0));
        _mauern = new StaticGameObject(titleScreenMeshes.get("mauern"), TurtleColor.CYAN, new Vector3 (-1.48, 0.34, -2.5), new Vector3(), new Vector3(1.0, 1.0, 1.0));
        
        // Mauszeiger nicht zentrieren
        _state.inputHandler.setKeepMouseInPlace(false);
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
        if(_audioVisualizer != null) _audioVisualizer.update(deltaTime, runTime, _camera.getPosition());
        return;
    }
    
    /**
     * @see Scene#draw()
     */
    public void draw(double deltaTime, double runTime)
    {
        _state.renderer.clear(10, 10, 10);
        if(_audioVisualizer != null) _audioVisualizer.draw(_state.renderer, _camera);
        _title.draw(_state.renderer, _camera);
        _mauern.draw(_state.renderer, _camera);
        
        //draw Start Button
        if (drawButton("Start", new Vector2(220.9,372), TurtleColor.WHITE)) 
        {
            _state.soundEngine.removeAllSounds();
            _state.scene = new IntroScene(_state);    
        }
        
        //draw Credits Button
        if (drawButton("Credits", new Vector2(250-37.65,414), TurtleColor.WHITE))
        {
            _state.soundEngine.removeAllSounds();
            _state.scene = new CreditScene(_state);
        }
    }
    
    /**
     * Zeichnet einen Knopf an gegebener Position und gibt zurück ob gedrückt wurde.
     */
    private boolean drawButton(String text, Vector2 pos, TurtleColor color)
    {
        Vector2 mousePos = _state.inputHandler.getLocalMousePos();
        
        // Das offset von der Box beim drüberhovern
        int p = (int) pos.getY() - 372;
        
        final int textSize = 6;
        
        // Knopftext zeichnen
        _state.textRenderer.write(new Vector2(pos), textSize, text, color); // BUG?: write() verändert den position vektor
        
        // Box um den Text zeichnen
        if(mousePos.getX() > (pos.getX() - 10) && mousePos.getX() < (pos.getX() + text.length() * 14) && mousePos.getY() > (pos.getY() - 10) && mousePos.getY() < (pos.getY() + 25))
        {
            _state.renderer.drawLine(new Vector2(231, 364+p), new Vector2(269, 364+p), TurtleColor.MAGENTA);
            _state.renderer.drawLine(new Vector2(269, 364+p), new Vector2(304, 370+p), TurtleColor.MAGENTA);
            _state.renderer.drawLine(new Vector2(304, 370+p), new Vector2(304, 390+p), TurtleColor.MAGENTA);
            _state.renderer.drawLine(new Vector2(304, 390+p), new Vector2(269, 396+p), TurtleColor.MAGENTA);
            _state.renderer.drawLine(new Vector2(269, 396+p), new Vector2(231, 396+p), TurtleColor.MAGENTA);
            _state.renderer.drawLine(new Vector2(231, 396+p), new Vector2(196, 390+p), TurtleColor.MAGENTA);
            _state.renderer.drawLine(new Vector2(196, 390+p), new Vector2(196, 370+p), TurtleColor.MAGENTA);
            _state.renderer.drawLine(new Vector2(196, 370+p), new Vector2(231, 364+p), TurtleColor.MAGENTA);
            
            if (_state.inputHandler.isKeyPressed(KeyCode.MOUSE_BUTTON_LEFT))
            {
                return true;
            }
        }
        
        return false;
    }
}
