import java.io.*;

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
        Mesh result = new Mesh();
        
        try
        {
            FileInputStream fstream = new FileInputStream("/res/models/monkey.obj");

            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;

            while ((strLine = br.readLine()) != null)
            {
                System.out.println (strLine);
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
