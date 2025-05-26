package PaooGame.Tiles;

import java.awt.*;
import java.awt.image.BufferedImage;

/*! \class public class Tile
    \brief Retine toate dalele intr-un vector si ofera posibilitatea regasirii dupa un id.
 */
public class Tile
{
    private static final int NO_TILES   = 32;
    public static Tile[] tiles          = new Tile[NO_TILES];       /*!< Vector de referinte de tipuri de dale.*/

    /// De remarcat ca urmatoarele dale sunt statice si publice. Acest lucru imi permite sa le am incarcate
    /// o singura data in memorie

    public static Tile treeTile         = new TreeTile(1);      /*!< Dala de tip copac*/

    public static final int TILE_WIDTH  = 48;                       /*!< Latimea unei dale.*/
    public static final int TILE_HEIGHT = 48;                       /*!< Inaltimea unei dale.*/

    protected BufferedImage img;                                    /*!< Imaginea aferenta tipului de dala.*/
    protected final int id;                                         /*!< Id-ul unic aferent tipului de dala.*/

    /*! \fn public Tile(BufferedImage texture, int id)
        \brief Constructorul aferent clasei.

        \param image Imaginea corespunzatoare dalei.
        \param id Id-ul dalei.
     */
    public Tile(BufferedImage image, int idd)
    {
        img = image;
        id = idd;

        tiles[id] = this;
    }

    /*! \fn public void Update()
        \brief Actualizeaza proprietatile dalei.
     */
    public void Update()
    {

    }

    /*! \fn public void Draw(Graphics g, int x, int y)
        \brief Deseneaza in fereastra dala.

        \param g Contextul grafic in care sa se realizeze desenarea
        \param x Coordonata x in cadrul ferestrei unde sa fie desenata dala
        \param y Coordonata y in cadrul ferestrei unde sa fie desenata dala
     */
    public void Draw(Graphics g, int x, int y)
    {
        /// Desenare dala
        g.drawImage(img, x, y, TILE_WIDTH, TILE_HEIGHT, null);
    }

    /*! \fn public boolean IsSolid()
        \brief Returneaza proprietatea de dala solida (supusa coliziunilor) sau nu.
     */
    public boolean IsSolid()
    {
        return false;
    }

    /*! \fn public int GetId()
        \brief Returneaza id-ul dalei.
     */
    public int GetId()
    {
        return id;
    }

    /*! \fn public static Tile GetGrassTile()
        \brief Returneaza o dala simpla pentru iarba (desenata direct, nu incarcata din fisier).
     */
    public static Tile GetGrassTile()
    {
        return new Tile(createGrassImage(), 0) {
            @Override
            public boolean IsSolid() {
                return false;
            }
        };
    }

    /*! \fn private static BufferedImage createGrassImage()
        \brief Creaza o imagine simpla pentru iarba.
     */
    private static BufferedImage createGrassImage()
    {
        BufferedImage grassImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = grassImg.createGraphics();
        g2d.setColor(new Color(34, 139, 34));
        g2d.fillRect(0, 0, 16, 16);
        g2d.setColor(new Color(0, 100, 0));
        g2d.drawRect(0, 0, 15, 15);
        g2d.setColor(new Color(50, 205, 50));
        for (int i = 0; i < 8; i++) {
            int x = (int)(Math.random() * 14) + 1;
            int y = (int)(Math.random() * 14) + 1;
            g2d.fillRect(x, y, 1, 2);
        }
        g2d.dispose();
        return grassImg;
    }
}