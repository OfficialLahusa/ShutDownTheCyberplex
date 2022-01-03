package core;

import maths.*;
import physics.*;

/**
 * Ein dreidimensionales Objekt in der Spielwelt
 * 
 * @author Lasse Huber-Saffer
 * @version 01.01.2022
 */
public class StaticGameObject implements IGameObject
{
    private Mesh _mesh;
    private Vector3 _position;
    private Vector3 _rotation;
    private Vector3 _scale;
    private TurtleColor _color;
    private Matrix4 _model;
    
    /**
     * Konstruktor für GameObjects mit gegebenem Mesh (3D-Modell), Position, Rotation und Skalierung
     * @param mesh Mesh (3D-Modell)
     * @param color Farbe des Meshs
     * @param position Position
     * @param rotation Rotation
     * @param scale Skalierung
     */
    public StaticGameObject(Mesh mesh, TurtleColor color, Vector3 position, Vector3 rotation, Vector3 scale)
    {
        _mesh = new Mesh(mesh);
        _color = color;
        _position = new Vector3(position);
        _rotation = new Vector3(rotation);
        _scale = new Vector3(scale);
        _model = null;
    }
    
    /**
     * @see IGameObject#update()
     */
    public void update(double deltaTime, double runTime, Vector3 cameraPosition)
    {
        return;
    }
    
    /**
     * @see IGameObject#draw()
     */
    public void draw(Renderer renderer, Camera camera)
    {
        renderer.drawMesh(_mesh, getModelMatrix(), _color, camera);
    }
    
    /**
     * Generiert die Modelmatrix aller Transformationen auf dem GameObject
     * @return Modelmatrix
     */
    private Matrix4 getModelMatrix()
    {
        if(_model == null)
        {
            Matrix4 translation = MatrixGenerator.generateTranslationMatrix(_position);
            Matrix4 rotationX = MatrixGenerator.generateAxialRotationMatrix(new Vector3(1, 0, 0), _rotation.getX());
            Matrix4 rotationY = MatrixGenerator.generateAxialRotationMatrix(new Vector3(0, 1, 0), _rotation.getY());
            Matrix4 rotationZ = MatrixGenerator.generateAxialRotationMatrix(new Vector3(0, 0, 1), _rotation.getZ());
            Matrix4 scale = MatrixGenerator.generateScaleMatrix(_scale);
                
            _model = translation.multiply(scale.multiply(rotationZ.multiply(rotationY.multiply(rotationX))));
        }

        return _model;
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
