package game;

import maths.*;
import core.*;
import physics.*;
import java.util.*;
import javafx.util.*;
import javafx.scene.media.*;

/**
 * Einfacher AudioVisualizer, der passend zu einem laufenden Sound Meshes generiert
 * 
 * @author Lasse Huber-Saffer
 * @version 01.01.2022
 */
public class AudioVisualizer implements AudioSpectrumListener, IGameObject
{
    // Funktionalität
    private ArrayList<SimpleDynamicGameObject> _internalObjects;
    private Sound _sound;
    private Object _mutex;
    
    // Rendering
    private Vector3 _position;
    private Vector3 _rotation;
    private Vector3 _scale;
    private TurtleColor _color;
    
    /**
     * Konstruktor des AudioVisualizers
     * @param sound Sound, für den beim Ablaufen Meshes erstellt werden sollen
     * @param position Position im Raum
     * @param rotation Rotation entlang der x-, y- und z-Achse
     * @param scale Skalierung
     * @param color Farbe der generierten Meshes
     */
    public AudioVisualizer(Sound sound, Vector3 position, Vector3 rotation, Vector3 scale, TurtleColor color)
    {
        _internalObjects = new ArrayList<SimpleDynamicGameObject>();
        _sound = sound;
        _mutex = new Object();
        
        _position = new Vector3(position);
        _rotation = new Vector3(rotation);
        _scale = new Vector3(scale);
        _color = color;
    }
    
    /**
     * @see AudioSpectrumListener#spectrumDataUpdate()
     */
    public void spectrumDataUpdate(double timestamp, double duration, float[] magnitudes, float[] phases)
    {
        int bands = magnitudes.length;
        double[] correctedMagnitudes = new double[bands];
        ArrayList<Vector3> vertices = new ArrayList<Vector3>();
        ArrayList<Pair<Integer, Integer>> lineIndices = new ArrayList<Pair<Integer, Integer>>();
        
        for(int i = 0; i < bands; i++)
        {
            correctedMagnitudes[i] = magnitudes[i] - _sound.getAudioSpectrumThreshold();

            vertices.add(new Vector3(-(4.0/(double)bands)*i, correctedMagnitudes[i] * 0.005, 0.0));
            
            if(i != magnitudes.length - 1)
            {
                lineIndices.add(new Pair<Integer, Integer>(i + 1, i + 2));
            }
        }
        
        for(int i = 0; i < bands; i++)
        {
            correctedMagnitudes[i] = magnitudes[i] - _sound.getAudioSpectrumThreshold();

            vertices.add(new Vector3((4.0/(double)bands)*i, correctedMagnitudes[i] * 0.005, 0.0));
            
            if(i != magnitudes.length - 1)
            {
                lineIndices.add(new Pair<Integer, Integer>(bands + i + 1, bands + i + 2));
            }
        }
        
        Mesh mesh = new Mesh();
        mesh.vertices = vertices;
        mesh.lineIndices = lineIndices;
        
        synchronized(_mutex)
        {
            if(_internalObjects.size() > 10)
            {
                _internalObjects.remove(0);
            }
            
            _internalObjects.add(new SimpleDynamicGameObject(mesh, _color, new Vector3(_position), new Vector3(_rotation), new Vector3(_scale)));
            
            for(SimpleDynamicGameObject obj : _internalObjects)
            {
                obj.move(new Vector3(0.0, 0.0, -3.0));
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
    public TurtleColor getColor()
    {
        return _color;
    }
    
    /**
     * @see IGameObject#setColor()
     */
    public void setColor(TurtleColor color)
    {
        _color = color;
    }
}
