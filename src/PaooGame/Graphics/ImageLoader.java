package PaooGame.Graphics;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;

/*! \class public class ImageLoader
    \brief Clasa ce contine o metoda statica pentru incarcarea unei imagini in memorie.
 */
public class ImageLoader
{
    /*! \fn  public static BufferedImage loadImage(String path)
        \brief Incarca o imagine intr-un obiect BufferedImage si returneaza o referinta catre acesta.

        \param path Calea relativa pentru localizarea fisierul imagine.
     */
    public static BufferedImage LoadImage(String path)
    {
        try
        {
            // Încercăm mai întâi să încărcăm resursa din classpath
            BufferedImage img = ImageIO.read(ImageLoader.class.getResourceAsStream("/" + path));

            // Dacă resursa nu există în classpath, încercăm să o încărcăm ca fișier
            if (img == null) {
                img = ImageIO.read(new File(path));
            }

            if (img == null) {
                System.err.println("Nu s-a putut încărca imaginea: " + path);
            }

            return img;
        }
        catch(IOException e)
        {
            System.err.println("Eroare la încărcarea imaginii: " + path);
            e.printStackTrace();
        }
        return null;
    }}

