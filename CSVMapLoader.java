import java.io.*;
import java.util.*;

/**
 * Beschreiben Sie hier die Klasse CSVMapLoader.
 * 
 * @author Lasse Huber-Saffer
 * @version 03.12.2021
 */
public class CSVMapLoader
{
    /**
     * Lädt eine GridMap aus zwei .csv-Dateien (Comma Separated Values)
     * @param filePath Pfad der zu ladenden Datei
     * @return Aus der Datei geladenes Mesh
     */
    public GridMap loadFromFile(String tileFilePath, String functionFilePath)
    {
        ArrayList<ArrayList<Integer>> tileMap = readOneLayerFromFile(tileFilePath);
        ArrayList<ArrayList<Integer>> functionMap = readOneLayerFromFile(functionFilePath);
        
        return new GridMap(tileMap, functionMap);
    }
    
    
    private ArrayList<ArrayList<Integer>> readOneLayerFromFile(String filePath)
    {
        ArrayList<ArrayList<Integer>> mapLayer = new ArrayList<ArrayList<Integer>>();
        
        // Versucht die Datei beim gegebenen Pfad zu finden und ein Mesh daraus auszulesen
        try
        {
            FileInputStream fstream = new FileInputStream(filePath);

            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;

            while ((strLine = br.readLine()) != null)
            {
                ArrayList<Integer> line = new ArrayList<Integer>();
                String[] segments = strLine.split(",");
                for(int i = 0; i < segments.length; i++)
                {
                    line.add(Integer.parseInt(segments[i]));
                }
                mapLayer.add(line);
            }
            
            in.close();
        }
        catch (Exception e)
        {
            System.err.println("Error: " + e.getMessage());
        }
        
        return mapLayer;
    }
}
