import java.util.*;
import javafx.util.*;
import javafx.scene.media.*;

/**
 * Beschreiben Sie hier die Klasse BarAudioVisualizer.
 * 
 * @author Lasse Huber-Saffer
 * @version 10.12.2021
 */
public class BarAudioVisualizer implements AudioSpectrumListener
{
    private ArrayList<SimpleDynamicGameObject> _internalObjects;
    private Sound _sound;
    private Object _mutex;
    
    public BarAudioVisualizer(Sound sound)
    {
        _internalObjects = new ArrayList<SimpleDynamicGameObject>();
        _sound = sound;
        _mutex = new Object();
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
                obj.move(new Vector3(0.0, 0.0, -3.0));
            }
        }
    }
    
    public void update(double deltaTime)
    {

    }
    
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
}
