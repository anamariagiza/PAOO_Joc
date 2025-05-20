package PaooGame.Graphics;

import java.awt.image.BufferedImage;

/*! \class public class Assets
    \brief Clasa incarca fiecare element grafic necesar jocului.

    Game assets include tot ce este folosit intr-un joc: imagini, sunete, harti etc.
 */
public class Assets
{
        /// Referinte catre elementele grafice (dale) utilizate in joc.
        public static BufferedImage grass;
        public static BufferedImage tree;
        public static BufferedImage heroLeft;
        public static BufferedImage heroRight;
    /*! \fn public static void Init()
        \brief Functia initializaza referintele catre elementele grafice utilizate.

        Aceasta functie poate fi rescrisa astfel incat elementele grafice incarcate/utilizate
        sa fie parametrizate. Din acest motiv referintele nu sunt finale.
     */
    public static void Init()
    {
            /// Se creaza temporar un obiect SpriteSheet initializat prin intermediul clasei ImageLoader
        SpriteSheet sheet = new SpriteSheet(ImageLoader.LoadImage("res/textures/gentle forest v01.png"));


        heroLeft = ImageLoader.LoadImage("res/textures/heroLeft.png");
        heroRight = ImageLoader.LoadImage("res/textures/heroRight.png");

            /// Se obtin subimaginile corespunzatoare elementelor necesare.
        grass = sheet.crop(0, 0); // tile (0,0)
        tree = sheet.crop(2, 0);
    }
}
