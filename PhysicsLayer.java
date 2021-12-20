/**
 * Unterebene des Physiksystems, die die physikalischen Eigenschaften eines Colliders bestimmt
 * 
 * @author  Lasse Huber-Saffer
 * @version 16.12.2021
 */
public enum PhysicsLayer
{
    // Bsp: Brick Wall
    SOLID,
    
    // Bsp: Glass, Crate
    SEMISOLID,
    
    // Der Spieler
    PLAYER,
    
    // Bsp: Health Pickup
    ITEM,
    
    // Bsp: Drone
    ENEMY,
    
    // Bsp: Reflective Wall
    REFLECTIVE
}
