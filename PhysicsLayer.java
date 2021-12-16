/**
 * Unterebene des Physiksystems, die die physikalischen Eigenschaften eines Colliders bestimmt
 * 
 * @author  Lasse Huber-Saffer
 * @version 16.12.2021
 */
public enum PhysicsLayer
{
    // Bsp: Brick Wall
    Solid,
    
    // Bsp: Glass, Crate
    Semisolid,
    
    // Der Spieler
    Player,
    
    // Bsp: Health Pickup
    Item,
    
    // Bsp: Drone
    Enemy,
    
    // Bsp: Reflective Wall
    Reflective
}
