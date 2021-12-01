
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

    /**
     * Konstruktor für Objekte der Klasse Game
     */
    public Game()
    {
        _renderer = new Renderer();
        _timeManager = new TimeManager();
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
        Vector4 pointA = mid.add(new Vector4(100*Math.cos(Math.toRadians(0)), 100*Math.sin(Math.toRadians(0)), 0.0, 1.0));
        Vector4 pointB = mid.add(new Vector4(100*Math.cos(Math.toRadians(120)), 100*Math.sin(Math.toRadians(120)), 0.0, 1.0));
        Vector4 pointC = mid.add(new Vector4(100*Math.cos(Math.toRadians(240)), 100*Math.sin(Math.toRadians(240)), 0.0, 1.0));

        while(true)
        {
            runTime = _timeManager.getRunTime();
            deltaTime = _timeManager.getDeltaTime();
            
            Matrix4 translation = MatrixGenerator.generateTranslationMatrix(250.0, 250.0, 0.0);
            Matrix4 rotation = MatrixGenerator.generateAxialRotationMatrix(new Vector3(0, 0, 1), 25*runTime);
            Matrix4 scale = MatrixGenerator.generateScaleMatrix(1.0, 1.0+0.4*Math.sin(Math.toRadians(120*runTime)),1.0);
            
            Matrix4 transform = translation.multiply(scale.multiply(rotation));
            
            Vector4 pA = transform.multiply(pointA);
            Vector4 pB = transform.multiply(pointB);
            Vector4 pC = transform.multiply(pointC);
            
            Vector2 pA2 = new Vector2(pA.getX(), pA.getY());
            Vector2 pB2 = new Vector2(pB.getX(), pB.getY());
            Vector2 pC2 = new Vector2(pC.getX(), pC.getY());
            
            _renderer.clear();
            
            _renderer.drawLine(pA2, pB2);
            _renderer.drawLine(pB2, pC2);
            _renderer.drawLine(pC2, pA2);
            
            
        }
    }
}
