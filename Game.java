
/**
 * Diese Klasse enthält die Kernlogik des Spiels und den Gameloop
 * 
 * @author Lasse Huber-Saffer
 * @version 01.12.2021
 */
public class Game
{
    private Renderer _renderer;
    private TimeManager _timeManager;
    private InputManager _inputManager;
    private Camera _camera;

    /**
     * Konstruktor für Objekte der Klasse Game
     */
    public Game()
    {
        _renderer = new Renderer();
        _timeManager = new TimeManager();
        _inputManager = new InputManager();
        _camera = new Camera(new Vector3(0.0, 0.0, 3.0), 1.0, 90.0);
    }
    
    /**
     * Startmethode des Spiels
     */
    public void start()
    {
        Matrix4 mat = new Matrix4();
        mat.print();
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
            
            System.out.println(_camera.getPosition().getZ());
            
            // Viewport transform
            pA = MatrixGenerator.viewportTransform(pA);
            pB = MatrixGenerator.viewportTransform(pB);
            pC = MatrixGenerator.viewportTransform(pC);
            
            Vector2 pA2 = new Vector2(pA.getX(), pA.getY());
            Vector2 pB2 = new Vector2(pB.getX(), pB.getY());
            Vector2 pC2 = new Vector2(pC.getX(), pC.getY());
            */
            
           Vector4 topLeft = new Vector4(-1.0, -1.0, 0.0, 1.0);
            Vector4 bottomLeft = new Vector4(-1.0, 1.0, 0.0, 1.0);
            Vector4 topRight = new Vector4(1.0, -1.0, 0.0, 1.0);
            Vector4 bottomRight = new Vector4(1.0, 1.0, 0.0, 1.0);
            
            Vector4 pA = transform.multiply(topLeft);
            Vector4 pB = transform.multiply(bottomLeft);
            Vector4 pC = transform.multiply(topRight);
            Vector4 pD = transform.multiply(bottomRight);
            
            Vector3 pA2 = new Vector3(pA.getX(), pA.getY(), pA.getZ());
            Vector3 pB2 = new Vector3(pB.getX(), pB.getY(), pB.getZ());
            Vector3 pC2 = new Vector3(pC.getX(), pC.getY(), pC.getZ());
            Vector3 pD2 = new Vector3(pD.getX(), pD.getY(), pD.getZ());
            
            _renderer.clear();
            
            _renderer.drawStripedQuad(pA2, pB2, pC2, pD2, "rot", _camera); 
           
            
            
            /*
            _renderer.drawLine3D(new Vector3(), new Vector3(1.0, 0.0, 0.0), "rot", _camera);
            _renderer.drawLine3D(new Vector3(), new Vector3(0.0, 1.0, 0.0), "gruen", _camera);
            _renderer.drawLine3D(new Vector3(), new Vector3(0.0, 0.0, 1.0), "blau", _camera);
            
            _renderer.drawLine(pA2, pB2);
            _renderer.drawLine(pB2, pC2);
            _renderer.drawLine(pC2, pA2);
            */
            
        }
    }
}
