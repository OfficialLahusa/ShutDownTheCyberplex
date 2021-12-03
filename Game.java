
/**
 * Diese Klasse enthält die Kernlogik des Spiels und den Gameloop
 * 
 * @author Lasse Huber-Saffer, Sven Schreiber, Nico Hädicke
 * @version 01.12.2021
 */
public class Game
{
    private Renderer _renderer;
    private TimeManager _timeManager;
    private TimeManager _frameCapTimeManager;
    private InputManager _inputManager;
    private TextRenderer _textRenderer;
    private SoundRegistry _soundRegistry;
    private WavefrontObjectLoader _objLoader;
    private Camera _camera;
    private double _fps;
    private double fpsTimer = 0.0;
    private StaticGameObject _monkey;
    private StaticGameObject _monkey2;
    
    public static final double FPS_CAP = 60.0;

    /**
     * Konstruktor für Objekte der Klasse Game
     */
    public Game()
    {
        _renderer = new Renderer();
        _timeManager = new TimeManager();
        _frameCapTimeManager = new TimeManager();
        _inputManager = new InputManager();
        _textRenderer = new TextRenderer(_renderer);
        _soundRegistry = new SoundRegistry();
        _objLoader = new WavefrontObjectLoader();
        _camera = new Camera(new Vector3(0.0, 2.0, 10.0), 1.0, 90.0);
        _monkey = new StaticGameObject(_objLoader.loadFromFile("./res/models/dirt_floor.obj"), "orange", new Vector3(0.0, 0.0, 0.0), new Vector3(), new Vector3(1.0, 1.0, 1.0));
        _monkey2 = new StaticGameObject(_objLoader.loadFromFile("./res/models/dirt_floor.obj"), "orange", new Vector3(0.0, 0.0, 8.0), new Vector3(), new Vector3(1.0, 1.0, 1.0));
    }
    
    /**
     * Startmethode des Spiels
     */
    public void start()
    {
        //_soundRegistry.loadSound("test", "D:/Uni/WiSe 2021-2022/SE1/TurtleDoomLike/res/sounds/to_the_front.mp3");
        _soundRegistry.loadSound("test", "./res/sounds/to_the_front.mp3");
        _soundRegistry.sounds.get("test").setVolume(0.2);
        _soundRegistry.sounds.get("test").play();
        
        runGameLoop();
    }
    
    public void runGameLoop()
    {
        double runTime = 0.0, deltaTime = 0.0;
        
        Vector4 mid = new Vector4(0.0, 0.0, 0.0, 0.0);
        double triangleScale = 2.0;
        Vector4 pointA = mid.add(new Vector4(triangleScale*Math.cos(Math.toRadians(0)), triangleScale*Math.sin(Math.toRadians(0)), 0.0, 1.0));
        Vector4 pointB = mid.add(new Vector4(triangleScale*Math.cos(Math.toRadians(120)), triangleScale*Math.sin(Math.toRadians(120)), 0.0, 1.0));
        Vector4 pointC = mid.add(new Vector4(triangleScale*Math.cos(Math.toRadians(240)), triangleScale*Math.sin(Math.toRadians(240)), 0.0, 1.0));

        while(true)
        {
            runTime = _timeManager.getRunTime();
            deltaTime = _timeManager.getDeltaTime();
            
            // reset frameCap Timer
            _frameCapTimeManager.getDeltaTime();
                        
            if(_inputManager.isKeyPressed(KeyCode.KEY_W))
            {
                //_camera.move(new Vector3(0.0, 0.0, -2.0 * deltaTime));
                _camera.move(_camera.getDirection().multiply(-2.0 * deltaTime));
            }
            if(_inputManager.isKeyPressed(KeyCode.KEY_S))
            {
                //_camera.move(new Vector3(0.0, 0.0, 2.0 * deltaTime));
                _camera.move(_camera.getDirection().multiply(2.0 * deltaTime));
            }
            if(_inputManager.isKeyPressed(KeyCode.KEY_A))
            {
                _camera.rotateYaw(-70.0 * deltaTime);
            }
            if(_inputManager.isKeyPressed(KeyCode.KEY_D))
            {
                _camera.rotateYaw(70.0 * deltaTime);
            }
            
            _renderer.clear();
            
            //_renderer.drawStripedQuad(pA2, pB2, pC2, pD2, "rot", _camera);
            
            _monkey.draw(_renderer, _camera);
            _monkey2.draw(_renderer, _camera);
            
            _renderer.drawAxis(_camera);
            
            //render fps
            fpsTimer += deltaTime;
            if(fpsTimer > 1.0)
            {
                fpsTimer = 0;
                _fps = 1.0 / deltaTime;
                
            }
            
            _textRenderer.write(new Vector2(10,10), 5, "fps: " + (int)Math.round(_fps));
            
            // Bildrate auf maximal FPS_CAP (Konstante) begrenzen
            double currentFrameTime = _frameCapTimeManager.getDeltaTime();
            try
            {
                double diff = (1.0 / FPS_CAP) - currentFrameTime;
                if(diff > 0.0)
                {
                    Thread.sleep((long)(1000*diff));
                }
            }
            catch (InterruptedException ie)
            {
                throw new RuntimeException("unhandled interrupt");
            }
        }
    }
}
