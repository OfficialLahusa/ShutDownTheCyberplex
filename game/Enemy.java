package game;

import maths.*;
import core.*;
import physics.*;
import java.util.*;

/**
 * Abstrakte Gegnervorlage
 * 
 * @author Lasse Huber-Saffer
 * @version 25.12.2021
 */
public abstract class Enemy
{
    /**
     * Position des Gegners
     */
    protected Vector3 _position;
    
    /**
     * Rotation des Gegners entlang der x, y und z-Achse
     */
    protected Vector3 _rotation;
    
    /**
     * Referenz zum Raum, indem sich der Gegner befindet
     */
    protected Room _room;
    
    /**
     * Gibt an, ob die Model Matrix des Gegners vor der nächsten Verwendung neu berechnet werden muss.
     * Dies ist nach Transformationen der Fall.
     */
    protected boolean _recalculateModelMatrix;

    /**
     * Gibt den Winkel zwischen der aktuellen Sichtrichtung und der Richtung zu einem bestimmten Zielpunkt zurück
     * @param target Zielpunkt, zu dem der Winkel berechnet werden soll
     * @return Winkel zum Zielpunkt [-180, 180]
     */
    protected double getSightAngleTo(Vector3 target)
    {
        if(target == null)
        {
            throw new IllegalArgumentException("target was null when calculating sight angle");
        }
        
        Vector2 currentDirection = getDirection();
        Vector2 idealDirection = new Vector2(target.getX() - _position.getX(), target.getZ() - _position.getZ());
        return idealDirection.getAngleBetween(currentDirection);
    }
    
    /**
     * Richtet den Gegner zu einem bestimmten Zielpunkt aus
     * @param target Zielpunkt
     */
    protected void lookAt(Vector3 target)
    {
        if(target == null)
        {
            throw new IllegalArgumentException("target was null when calculating lookAt");
        }
        
        setAngle(getAngleTo(target));
    }
    
    /**
     * Richtet den Gegner mit einem sanften Übergang zu einem bestimmten Zielpunkt aus
     * @param target Zielpunkt
     * @param inertia Trägheit der Drehung
     */
    protected void lookAtFade(Vector3 target, double inertia)
    {
        if(target == null)
        {
            throw new IllegalArgumentException("target was null when calculating lookAt");
        }
        
        double angleToTarget = getAngleTo(target);
        double prevAngle = _rotation.getY();
        
        // 360°-Flip bei 360° zu 0°-Transition und umgekehrt verhindern
        if(prevAngle > 270.0 && angleToTarget < 90.0)       angleToTarget += 360.0;
        else if(angleToTarget > 270.0 && prevAngle < 90.0)  prevAngle += 360.0;
        
        // Neuen Winkel setzen (Langsamer Übergang)
        double fadeAngle = ((angleToTarget + inertia * prevAngle) / (inertia + 1.0)) % 360.0;
        setAngle(fadeAngle);
    }
    
    /**
     * Gibt die Distanz (Luftlinie) des Gegner zu einem bestimmten Punkt zurück
     * @param target Punkt, zu dem die Distanz berechnet werden soll
     * @return Distanz (Luftlinie) des Punktes zum Gegner
     */
    protected double getDistanceTo(Vector3 target)
    {
        if(target == null)
        {
            throw new IllegalArgumentException("target was null when calculating distance");
        }
        
        Vector2 position2D = new Vector2(_position.getX(), _position.getZ());
        Vector2 target2D = new Vector2(target.getX(), target.getZ());
        
        return target2D.subtract(position2D).getLength();
    }
    
    /**
     * Prüft, ob der Gegner eine direkte Line-of-Sight zum Spieler hat.
     * @return true, wenn es eine Line-of-Sight zum Spieler gibt, sonst false
     */
    protected boolean hasLineOfSight(double maxDistance)
    {
        Player player = _room.getMap().getPlayer();
        if(player == null)
        {
            throw new IllegalArgumentException("player was null when calculating line-of-sight");
        }
        
        // Raycast vorbereiten
        Vector2 source = new Vector2(_position.getX(), _position.getZ());
        Vector2 target = new Vector2(player.getPosition().getX(), player.getPosition().getZ());
        Vector2 direction = target.subtract(source).normalize();
        EnumSet<PhysicsLayer> terminationFilter = EnumSet.of(PhysicsLayer.PLAYER, PhysicsLayer.SOLID);
        EnumSet<PhysicsLayer> exclusionFilter = EnumSet.of(PhysicsLayer.ENEMY);
        ArrayList<RaycastHit> raycast = Physics.raycast(source, direction, maxDistance, _room.getMap(), terminationFilter, exclusionFilter);
        
        // Sichtbarkeit berechnen (Bdg.: der letzte Treffer des Raycasts muss der Spieler sein)
        return raycast.size() > 0 && raycast.get(raycast.size() - 1).collider.getLayer() == PhysicsLayer.PLAYER;
    }
    
    /**
     * Gibt den Richtungsvektor des Gegners zurück
     * @return Richtungsvektor des Gegners
     */
    protected Vector2 getDirection()
    {
        Vector2 currentDirection = new Vector2(Math.cos(Math.toRadians(-_rotation.getY())), Math.sin(Math.toRadians(-_rotation.getY())));
        return currentDirection;
    }
    
    /**
     * Gibt den Winkel zu einem Zielpunkt zurück
     * @param target Zielpunkt
     * @return Ausrichtungswinkel, in dem der Gegner genau auf den Zielpunkt ausgerichtet ist
     */
    protected double getAngleTo(Vector3 target)
    {
        if(target == null)
        {
            throw new IllegalArgumentException("target was null when calculating angle");
        }
        
        Vector2 baseDirection = new Vector2(1.0, 0.0);
        Vector2 target2D = new Vector2(target.getX(), target.getZ());
        Vector2 position2D = new Vector2(_position.getX(), _position.getZ());
        
        Vector2 toTarget2D = target2D.subtract(position2D);
        
        // Kleinstmöglicher Winkel zwischen baseDirection und toTarget
        double resultingAngle = baseDirection.getAngleBetween(toTarget2D);
        
        // Winkel umkehren, wenn z-Koordinate des Zielpunkts größer ist, als die eigene
        if(target.getZ() > _position.getZ())
        {
            resultingAngle = 360.0 - resultingAngle;
        }
        
        return resultingAngle;
    }
    
    /**
     * Setzt den Ausrichtungswinkel des Gegners ausgerichtet ist
     * @param angle neuer Ausrichtungswinkel
     */
    protected void setAngle(double angle)
    {
        _rotation.setY(angle);
        _recalculateModelMatrix = true;
    }
}
