package game;

import maths.*;
import core.*;
import physics.*;
import java.util.*;

/**
 * Haupt-Szene des Spiels
 * 
 * @author Lasse Huber-Saffer, Nico Hädicke, Sven Schreiber
 * @version 03.01.2022
 */
public class GameScene extends Scene
{
    private MapHandler _mapHandler;    
    private Player _player;
    private PlayerViewModel _viewModel;
    
    public GameScene(GameState state)
    {
        super(state);
        
        // Meshes laden
        HashMap<String, Mesh> entityMeshes = _state.resourceManager.loadEntityMeshes();
        HashMap<String, Mesh> particleMeshes = _state.resourceManager.loadParticleMeshes();
        HashMap<String, Mesh> tileMeshes = _state.resourceManager.loadTileMeshes();
        HashMap<String, Mesh> viewModelMeshes = _state.resourceManager.loadViewModelMeshes();
        
        _mapHandler = new MapHandler(tileMeshes, entityMeshes, particleMeshes, _state.soundEngine);
        
        _viewModel = new PlayerViewModel(viewModelMeshes, state, _mapHandler);
        
        _mapHandler.load("Level");
        
        _player = new Player(_mapHandler.getMap().getPlayerSpawn(), new Vector3(0.0, _mapHandler.getMap().getPlayerSpawnAngle(), 0.0), _state.soundEngine);
        _mapHandler.getMap().globalColliders.add(_player.getCollider());
        _mapHandler.getMap().setPlayer(_player);
        
        // Sounds aus Dateien laden
        _state.resourceManager.loadSoundSources(_state.soundEngine);
        
        // Musik-Loop starten
        _state.soundEngine.playSound("music1", 0.2, true);
        
        // Maus in Fenstermitte zentrieren
        _state.inputHandler.setKeepMouseInPlace(true);
    }
    
    /**
     * @see Scene#handleInput()
     */
    public void handleInput(double deltaTime, double runTime)
    {
        Camera playerCam = _player.getCamera();
        
        // Nicht-lebensgebundene Keybinds
        if(_state.inputHandler.isKeyPressed(KeyCode.KEY_ESCAPE))
        {
            _state.inputHandler.setKeepMouseInPlace(false);
            _state.scene = new PauseScene(_state, this);
        }
        if(_state.inputHandler.isKeyPressed(KeyCode.KEY_SPACE))
        {
            _state.inputHandler.setKeepMouseInPlace(true);
        }
        
        // Lebensgebundene Controls
        if(_player.isAlive())
        {
            // Maus-Bewegung zu Kamera-Yaw umwandeln
            double deltaX = _state.inputHandler.getAndResetMouseDelta().getX();
            if(_state.inputHandler.getKeepMouseInPlace())
            {
                _player.rotate(new Vector3(0.0, 0.20 * deltaX, 0.0));
            }
            
            Vector3 camDir = playerCam.getDirection();
            Vector3 camRight = playerCam.getRight();
            
            
            
            // Tastenbelegungen
            if(_state.inputHandler.isKeyPressed(KeyCode.KEY_W))
            {
                _player.move(camDir.multiply(3.0 * -6.5 * deltaTime));
            }
            if(_state.inputHandler.isKeyPressed(KeyCode.KEY_S))
            {
                _player.move(camDir.multiply(3.0 * 6.5 * deltaTime));
            }
            if(_state.inputHandler.isKeyPressed(KeyCode.KEY_A))
            {
                _player.move(camRight.multiply(3.0 * -6.0 * deltaTime));
            }
            if(_state.inputHandler.isKeyPressed(KeyCode.KEY_D))
            {
                _player.move(camRight.multiply(3.0 * 6.0 * deltaTime));
            }
        }
    }
    
    /**
     * @see Scene#update()
     */
    public void update(double deltaTime, double runTime)
    {                
        // Updatet die Map
        _mapHandler.getMap().update(deltaTime, runTime, _player.getCamera().getPosition());
        
        _viewModel.update(deltaTime, runTime, _player.getCamera().getPosition());
        
        // Entfernt bereits durchgelaufene Sounds
        _state.soundEngine.removeStoppedSounds();
        
        // Abschluss des Spiels prüfen und zu Outro übergehen
        if(_mapHandler.getMap().isCompleted)
        {
            _state.soundEngine.clear();
            _state.scene = new OutroScene(_state);
        }
        
        // Spielertod prüfen und zu Todesbildschirm übergehen
        if(_player.hasFallen())
        {
            _state.soundEngine.clear();
            _state.scene = new DeathScene(_state, _player.getCauseOfDeath());
        }
        
        // Spieler updaten
        _player.update(deltaTime, runTime, _player.getCamera().getPosition());
        
        // Kollisionsbehandlung
        CircleCollider playerCollider = _player.getCollider();
        _mapHandler.getMap().handleCollisions(playerCollider);
        
        for(IGameObject entity : _mapHandler.getMap().rooms.get(_mapHandler.getMap().activeRoom).getEntities())
        {
            if(entity.getCollider() != null)
            {
                _mapHandler.getMap().handleCollisions(entity.getCollider());
            }
        }
    }
    
    /**
     * @see Scene#draw()
     */
    public void draw(double deltaTime, double runTime)
    {
        Camera playerCam = _player.getCamera();
        
        // Cleart das Bild
        _state.renderer.clear(0, 0, 0);
        
        // Map zeichnen
        _mapHandler.getMap().draw(_state.renderer, playerCam);
        
        // Viewmodel (Hände, Waffe) zeichnen
        _viewModel.draw(_state.renderer, playerCam);
        
        // User Interface zeichnen
        _state.renderer.drawCrosshair(8.0, 6.0, TurtleColor.GREEN);
        _state.renderer.drawHealthbar(_player.getHealth());
    }
}
