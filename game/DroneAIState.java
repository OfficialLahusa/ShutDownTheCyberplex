package game;


/**
 * Zustand der Drohnen-AI
 * 
 * @author  Lasse Huber-Saffer
 * @version 25.12.2021
 */
public enum DroneAIState
{
    // Zufälliges Herumfliegen innerhalb des Raumes
    WANDERING,
    
    // Abfliegen einer vorgebenen Patroillenroute im Raum
    PATROL,
    
    // Verfolgt einen Spieler
    CHASING,
    
    // Beschießt einen Spieler
    ATTACKING
}
