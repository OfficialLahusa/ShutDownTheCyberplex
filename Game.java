
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
    private InputManager _inputManager;
    private TextRenderer _textRenderer;
    private WavefrontObjectLoader _objLoader;
    private Camera _camera;
    private double _fps;
    private double fpsTimer = 0.0;
    private GameObject _monkey;
    
    public static final double FPS_CAP = 60.0;

    /**
     * Konstruktor für Objekte der Klasse Game
     */
    public Game()
    {
        _renderer = new Renderer();
        _timeManager = new TimeManager();
        _inputManager = new InputManager();
        _textRenderer = new TextRenderer(_renderer);
        _objLoader = new WavefrontObjectLoader();
        _camera = new Camera(new Vector3(0.0, 0.0, 3.0), 1.0, 90.0);
        _monkey = new GameObject(_objLoader.loadFromFile("D:/Uni/WiSe 2021-2022/SE1/TurtleDoomLike/res/models/monkey.obj"));
    }
    
    /**
     * Startmethode des Spiels
     */
    public void start()
    {
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
                        
            if (_inputManager.isKeyPressed(KeyCode.KEY_W))
            {
                //_camera.move(new Vector3(0.0, 0.0, -2.0 * deltaTime));
                _camera.move(_camera.getDirection().multiply(-2.0 * deltaTime));
            }
            if (_inputManager.isKeyPressed(KeyCode.KEY_S))
            {
                //_camera.move(new Vector3(0.0, 0.0, 2.0 * deltaTime));
                _camera.move(_camera.getDirection().multiply(2.0 * deltaTime));
            }
            if (_inputManager.isKeyPressed(KeyCode.KEY_A))
            {
                _camera.rotateYaw(-70.0 * deltaTime);
            }
            if (_inputManager.isKeyPressed(KeyCode.KEY_D))
            {
                _camera.rotateYaw(70.0 * deltaTime);
            }
            
            Matrix4 translation = MatrixGenerator.generateTranslationMatrix(new Vector3(0.0, 0.0, 0.0));
            Matrix4 rotation = MatrixGenerator.generateAxialRotationMatrix(new Vector3(0, 0, 1), 25*runTime);
            Matrix4 scale = MatrixGenerator.generateScaleMatrix(new Vector3(1.0, 1.0 + 0.4*Math.sin(Math.toRadians(120*runTime)), 1.0));
            
            Matrix4 transform = translation.multiply(scale.multiply(rotation));
            transform = _camera.getProjectionMatrix().multiply(_camera.getViewMatrix().multiply(transform));
            
            /* 
            Vector4 pA = transform.multiply(pointA);
            Vector4 pB = transform.multiply(pointB);
            Vector4 pC = transform.multiply(pointC);
            
            // Viewport transform
            pA = MatrixGenerator.viewportTransform(pA);
            pB = MatrixGenerator.viewportTransform(pB);
            pC = MatrixGenerator.viewportTransform(pC);
            
            Vector2 pA2 = new Vector2(pA.getX(), pA.getY());
            Vector2 pB2 = new Vector2(pB.getX(), pB.getY());
            Vector2 pC2 = new Vector2(pC.getX(), pC.getY());
            */
            
            /*Vector4 topLeft = new Vector4(-1.0, -1.0, 0.0, 1.0);
            Vector4 bottomLeft = new Vector4(-1.0, 1.0, 0.0, 1.0);
            Vector4 topRight = new Vector4(1.0, -1.0, 0.0, 1.0);
            Vector4 bottomRight = new Vector4(1.0, 1.0, 0.0, 1.0);
            
            Vector4 pA = topLeft;
            Vector4 pB = bottomLeft;
            Vector4 pC = topRight;
            Vector4 pD = bottomRight;
            
            Vector3 pA2 = new Vector3(pA.getX(), pA.getY(), pA.getZ());
            Vector3 pB2 = new Vector3(pB.getX(), pB.getY(), pB.getZ());
            Vector3 pC2 = new Vector3(pC.getX(), pC.getY(), pC.getZ());
            Vector3 pD2 = new Vector3(pD.getX(), pD.getY(), pD.getZ());*/
            
            _renderer.clear();
            
            //_renderer.drawStripedQuad(pA2, pB2, pC2, pD2, "rot", _camera);
            
            _renderer.drawGameObject(_monkey, "orange", _camera);
            
            _renderer.drawAxis(_camera);            

                /*
            _renderer.drawLine(pA2, pB2);
            _renderer.drawLine(pB2, pC2);
            _renderer.drawLine(pC2, pA2);
            */
            
            //render fps
            fpsTimer += deltaTime;
            if(fpsTimer > 1.0)
            {
                fpsTimer = 0;
                _fps = 1.0 / deltaTime;
                
            }
            
            _textRenderer.write(new Vector2(10,10), 5, "fps: " + (int)Math.round(_fps));
            
            // Bildrate auf maximal FPS_CAP (Konstante) begrenzen
            try
            {
                double diff = (1.0 / FPS_CAP) - deltaTime;
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
