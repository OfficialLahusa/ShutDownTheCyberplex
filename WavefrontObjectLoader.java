import java.io.*;
import javafx.util.*;

/**
 * Diese Klasse lädt 3D-Modelle im offenen Wavefront Object (.obj) Format als Line Meshes in die Engine
 * 
 * @author Lasse Huber-Saffer 
 * @version 02.12.2021
 */
public class WavefrontObjectLoader
{
    public WavefrontObjectLoader()
    {
        
    }

    public Mesh loadFromFile(String filePath)
    {
        // "D:/Uni/WiSe 2021-2022/SE1/TurtleDoomLike/res/models/monkey.obj"
        
        Mesh result = new Mesh();
        
        try
        {
            FileInputStream fstream = new FileInputStream(filePath);

            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;

            while ((strLine = br.readLine()) != null)
            {
                
                String[] segments = strLine.split(" ");
                if(segments[0].equals("v")) {
                    result.vertices.add(new Vector3(Double.parseDouble(segments[1]), Double.parseDouble(segments[2]), Double.parseDouble(segments[3])));
                }
                else if(segments[0].equals("f"))
                {
                    // Triangle
                    if(segments.length == 4)
                    {
                        result.lineIndices.add(new Pair<Integer, Integer>(Integer.parseInt(segments[1]), Integer.parseInt(segments[2])));
                        result.lineIndices.add(new Pair<Integer, Integer>(Integer.parseInt(segments[2]), Integer.parseInt(segments[3])));
                        result.lineIndices.add(new Pair<Integer, Integer>(Integer.parseInt(segments[3]), Integer.parseInt(segments[1])));
                    }
                    // Quad
                    else if(segments.length == 5)
                    {
                        result.lineIndices.add(new Pair<Integer, Integer>(Integer.parseInt(segments[1]), Integer.parseInt(segments[2])));
                        result.lineIndices.add(new Pair<Integer, Integer>(Integer.parseInt(segments[2]), Integer.parseInt(segments[3])));
                        result.lineIndices.add(new Pair<Integer, Integer>(Integer.parseInt(segments[3]), Integer.parseInt(segments[4])));
                        result.lineIndices.add(new Pair<Integer, Integer>(Integer.parseInt(segments[4]), Integer.parseInt(segments[1])));
                    }
                }
                else if(segments[0].equals("l"))
                {
                    // Edge
                    if(segments.length == 3)
                    {
                        result.lineIndices.add(new Pair<Integer, Integer>(Integer.parseInt(segments[1]), Integer.parseInt(segments[2])));
                    }
                }
            }

            in.close();
        }
        catch (Exception e)
        {
            System.err.println("Error: " + e.getMessage());
        }
        
        return result;
    }
}
