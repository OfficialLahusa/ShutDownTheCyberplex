import java.util.*;
import javafx.util.*;
import javafx.scene.media.*;

/**
 * AudioVisualizer, der passend zu einem laufenden Sound ein Mesh aus S�ulen der Frequenzb�nder generiert
 * 
 * @author Lasse Huber-Saffer
 * @version 10.12.2021
 */
public class BarAudioVisualizer implements AudioSpectrumListener, IGameObject
{
    // Funktionalit�t
    private ArrayList<SimpleDynamicGameObject> _internalObjects;
    private Sound _sound;
    private Object _mutex;
    
    // Rendering
    private Vector3 _position;
    private Vector3 _rotation;
    private Vector3 _scale;
    private String _color;
    
    /**
     * Konstruktor des BarAudioVisualizers
     * @param sound Sound, f�r den beim Ablaufen Meshes erstellt werden sollen
     * @param position Position im Raum
     * @param rotation Rotation entlang der x-, y- und z-Achse
     * @param scale Skalierung
     * @param color Farbe der generierten Meshes
     */
    public BarAudioVisualizer(Sound sound, Vector3 position, Vector3 rotation, Vector3 scale, String color)
    {
        _internalObjects = new ArrayList<SimpleDynamicGameObject>();
        _sound = sound;
        _mutex = new Object();
        
        _position = new Vector3(position);
        _rotation = new Vector3(rotation);
        _scale = new Vector3(scale);
        _color = color;
    }
    
    public void spectrumDataUpdate(double timestamp, double duration, float[] magnitudes, float[] phases)
    {
        int bands = magnitudes.length;
        double[] correctedMagnitudes = new double[bands];
        ArrayList<Vector3> vertices = new ArrayList<Vector3>();
        ArrayList<Pair<Integer, Integer>> lineIndices = new ArrayList<Pair<Integer, Integer>>();
        
        int c = 1;
        
        for(int i = 0; i < bands; i++)
        {
            correctedMagnitudes[i] = magnitudes[i] - _sound.getAudioSpectrumThreshold();
            
            vertices.add(new Vector3(-(4.0/(double)bands)*i, 0, 0.0));
            vertices.add(new Vector3(-(4.0/(double)bands)*i, correctedMagnitudes[i] * 0.02, 0.0));
            
            lineIndices.add(new Pair<Integer, Integer>(c, c + 1));
            c += 2;
        }
        
        for(int i = 0; i < bands; i++)
        {
            correctedMagnitudes[i] = magnitudes[i] - _sound.getAudioSpectrumThreshold();

            vertices.add(new Vector3((4.0/(double)bands)*i, 0, 0.0));
            vertices.add(new Vector3((4.0/(double)bands)*i, correctedMagnitudes[i] * 0.02, 0.0));
            
            lineIndices.add(new Pair<Integer, Integer>(c, c + 1));
            c += 2;
        }
        
        Mesh mesh = new Mesh();
        mesh.vertices = vertices;
        mesh.lineIndices = lineIndices;
        
        synchronized(_mutex)
        {
            if(_internalObjects.size() > 1)
            {
                _internalObjects.remove(0);
            }
            
            _internalObjects.add(new SimpleDynamicGameObject(mesh, "magenta", new Vector3(0.0, 0.0, 0.0), new Vector3(), new Vector3(1.0, 1.0, 1.0)));
            
            for(SimpleDynamicGameObject obj : _internalObjects)
            {
                obj.move(new Vector3(0.0, 2.0, -3.0));
            }
        }
    }
    
    /**
     * @see IGameObject#draw()
     */
    public void draw(Renderer renderer, Camera camera)
    {
        synchronized(_mutex)
        {
            for(int i = _internalObjects.size() - 1; i > 0; i--)
            {
                _internalObjects.get(i).draw(renderer, camera);
            }
        }
    }
    
    /**
     * @see IGameObject#update()
     */
    public void update(double deltaTime, double runTime, Vector3 cameraPosition)
    {
        return;
    }
    
    /**
     * @see IGameObject#getCollider()
     */
    public ICollider getCollider()
    {
        return null;
    }
    
    /**
     * @see IGameObject#getPosition()
     */
    public Vector3 getPosition()
    {
        return new Vector3(_position);
    }
    
    /**
     * @see IGameObject#getRotation()
     */
    public Vector3 getRotation()
    {
        return new Vector3(_rotation);
    }
    
    /**
     * @see IGameObject#getScale()
     */
    public Vector3 getScale()
    {
        return new Vector3(_scale);
    }
    
    /**
     * @see IGameObject#getColor()
     */
    public String getColor()
    {
        return _color;
    }
    
    /**
     * @see IGameObject#setColor()
     */
    public void setColor(String color)
    {
        _color = color;
    }
}
