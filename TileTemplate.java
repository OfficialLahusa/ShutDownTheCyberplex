
/**
 * Vorlage f�r ein Kachel-GameObject in der GridMap
 * 
 * @author Lasse Huber-Saffer
 * @version 04.12.2021
 */
public class TileTemplate
{
    private Mesh _mesh;
    private String _color;

    /**
     * Konstruktor f�r Objekte der Klasse TileTemplate
     * @param mesh Mesh, das f�r die Vorlage verwendet werden soll
     * @param color Farbe, die die Vorlage haben soll
     */
    public TileTemplate(Mesh mesh, String color)
    {
        _mesh = mesh;
        _color = color;
    }
    
    /**
     * Gibt eine neue Instanz des Meshs der Vorlage zur�ck
     * @return neue Instanz des Meshs der Vorlage
     */
    public Mesh getMesh()
    {
        return new Mesh(_mesh);
    }
    
    /**
     * Gibt die Farbe der Vorlage zur�ck
     * @return Farbe der Vorlage
     */
    public String getColor()
    {
        return _color;
    }
}
