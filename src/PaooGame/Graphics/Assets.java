package PaooGame.Graphics;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
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
    public static BufferedImage heroFront;  /*!< Sprite pentru fata eroului (S) */
    public static BufferedImage heroBack;   /*!< Sprite pentru spatele eroului (W) */
    public static BufferedImage backgroundMenu; /*!< Imaginea de fundal pentru meniu*/

    /*! \fn public static void Init()
        \brief Functia initializeza referintele catre elementele grafice utilizate.

        Aceasta functie poate fi rescrisa astfel incat elementele grafice incarcate/utilizate
        sa fie parametrizate. Din acest motiv referintele nu sunt finale.
     */
    public static void Init()
    {
        System.out.println("Initializare Assets...");

        // SOLUtIE: incearca sa incarce imaginea ta, daca nu merge creeaza fundal frumos
        System.out.println("incarcare fundal personalizat...");
        backgroundMenu = ImageLoader.LoadImage("res/textures/FUNDAL.jpg");

        if (backgroundMenu == null || backgroundMenu.getWidth() <= 16) {
            // Daca nu gaseste imaginea ta, creeaza fundal tematic cu padure
            System.out.println("Se creeaza fundal tematic cu padure...");
            backgroundMenu = createForestBackground();
        } else {
            System.out.println("✓ Fundal personalizat incarcat: " + backgroundMenu.getWidth() + "x" + backgroundMenu.getHeight());
        }

        // incarca sprite sheet-ul pentru personaje
        loadCharacterSprites();

        // incarca sau creeaza dale pentru harta
        loadMapTiles();

        System.out.println("✓ Assets initializate complet");
    }

    /*! \fn private static void loadCharacterSprites()
        \brief incarca sprite-urile pentru personaje din sprite sheet
     */
    private static void loadCharacterSprites()
    {
        // incearca sa incarce sprite sheet-ul cu personaje
        BufferedImage characterSheet = ImageLoader.LoadImage("res/textures/characters.png");

        if (characterSheet != null && characterSheet.getWidth() > 32 && characterSheet.getHeight() > 32) {
            System.out.println("✓ Character sprite sheet gasit: " + characterSheet.getWidth() + "x" + characterSheet.getHeight());

            // Calculeaza dimensiunea unei sprite (presupunand ca sunt aranjate in grid)
            // Din imaginea ta pare sa fie 9 coloane si 4 randuri
            int spriteWidth = characterSheet.getWidth() / 9;   // 9 personaje pe latime
            int spriteHeight = characterSheet.getHeight() / 4; // 4 randuri

            System.out.println("Dimensiune sprite calculata: " + spriteWidth + "x" + spriteHeight);

            try {
                // MAPARE CORECTa bazata pe organizarea ta:
                // Rand 1 = W (spate), Rand 2 = A (stanga), Rand 3 = S (fata), Rand 4 = D (dreapta)
                // Dar indexarea incepe de la 0, deci:
                // Rand 0 = W, Rand 1 = A, Rand 2 = S, Rand 3 = D

                heroBack = characterSheet.getSubimage(0, 0, spriteWidth, spriteHeight);                    // Randul 0 - W (spate)
                heroLeft = characterSheet.getSubimage(0, spriteHeight * 1, spriteWidth, spriteHeight);     // Randul 1 - A (stanga)
                heroFront = characterSheet.getSubimage(0, spriteHeight * 2, spriteWidth, spriteHeight);    // Randul 2 - S (fata)
                heroRight = characterSheet.getSubimage(0, spriteHeight * 3, spriteWidth, spriteHeight);    // Randul 3 - D (dreapta)

                System.out.println("✓ Sprite-uri mapate corect:");
                System.out.println("heroBack (W - randul 0): " + heroBack.getWidth() + "x" + heroBack.getHeight());
                System.out.println("heroLeft (A - randul 1): " + heroLeft.getWidth() + "x" + heroLeft.getHeight());
                System.out.println("heroFront (S - randul 2): " + heroFront.getWidth() + "x" + heroFront.getHeight());
                System.out.println("heroRight (D - randul 3): " + heroRight.getWidth() + "x" + heroRight.getHeight());

            } catch (Exception e) {
                System.err.println("⚠ Eroare la extragerea sprite-urilor: " + e.getMessage());
                createFallbackHeroImages();
            }
        } else {
            System.out.println("⚠ Nu s-a gasit character sprite sheet, se creaza imagini temporare");
            createFallbackHeroImages();
        }
    }

    /*! \fn private static void loadMapTiles()
        \brief incarca dale pentru harta
     */
    private static void loadMapTiles()
    {
        // incearca sa incarce sprite sheet-ul pentru harta (dale)
        BufferedImage mapSheetImage = ImageLoader.LoadImage("res/textures/gentle forest v01.png");

        if (mapSheetImage != null && mapSheetImage.getWidth() > 32 && mapSheetImage.getHeight() > 16) {
            SpriteSheet mapSheet = new SpriteSheet(mapSheetImage);
            try {
                grass = mapSheet.crop(0, 0); // tile (0,0)
                tree = mapSheet.crop(2, 0);  // tile (2,0)
                System.out.println("✓ Dale extrase din sprite sheet cu succes");
            } catch (Exception e) {
                System.err.println(" Eroare la extragerea dalelor: " + e.getMessage());
                createFallbackTiles();
            }
        } else {
            System.out.println("⚠ Nu s-a gasit map sprite sheet, se creaza dale temporare");
            createFallbackTiles();
        }
    }

    /*! \fn private static void createFallbackHeroImages()
        \brief Creeaza imagini temporare pentru erou
     */
    private static void createFallbackHeroImages()
    {
        heroLeft = createHeroImage(Color.BLUE, false);
        heroRight = createHeroImage(Color.CYAN, true);
        heroFront = createHeroImage(Color.GREEN, true);   // Verde pentru fata
        heroBack = createHeroImage(Color.ORANGE, false);  // Orange pentru spate
        System.out.println("✓ Imagini temporare create pentru toate directiile eroului");
    }

    /*! \fn private static BufferedImage createForestBackground()
        \brief Creeaza un fundal tematic cu padure pentru meniu
     */
    private static BufferedImage createForestBackground()
    {
        BufferedImage bg = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bg.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Gradient de fundal (verde inchis la verde deschis - tematic padure)
        GradientPaint gradient = new GradientPaint(
                0, 0, new Color(34, 49, 31),         // Verde foarte inchis
                1280, 720, new Color(76, 105, 68)   // Verde mediu
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, 1280, 720);

        // Adauga "copaci" stilizati in fundal
        g2d.setColor(new Color(45, 33, 20, 150)); // Maro transparent pentru trunchiuri
        for (int i = 0; i < 8; i++) {
            int x = i * 160 + 40;
            int height = 300 + (int)(Math.random() * 200);
            g2d.fillRect(x, 720 - height, 30, height);

            // Coroana copacului
            g2d.setColor(new Color(34, 69, 34, 120)); // Verde transparent
            g2d.fillOval(x - 40, 720 - height - 60, 110, 80);
        }

        // Adauga particule de lumina (ca razele de soare prin copaci)
        g2d.setColor(new Color(255, 255, 200, 80));
        for (int i = 0; i < 15; i++) {
            int x = (int)(Math.random() * 1280);
            int y = (int)(Math.random() * 400) + 100;
            int width = 3 + (int)(Math.random() * 4);
            int height = 80 + (int)(Math.random() * 120);
            g2d.fillRect(x, y, width, height);
        }

        // Adauga puncte de lumina mici (fireflies/licurici)
        g2d.setColor(new Color(255, 255, 150, 200));
        for (int i = 0; i < 25; i++) {
            int x = (int)(Math.random() * 1280);
            int y = (int)(Math.random() * 720);
            g2d.fillOval(x, y, 3, 3);
        }

        // Bordura decorativa in stil natural
        g2d.setColor(new Color(139, 115, 85)); // Maro deschis
        g2d.setStroke(new BasicStroke(6));
        g2d.drawRect(10, 10, 1260, 700);

        g2d.setColor(new Color(160, 133, 98)); // Maro mai deschis
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(15, 15, 1250, 690);

        g2d.dispose();
        System.out.println("✓ Fundal tematic cu padure creat: 1280x720");
        return bg;
    }

    /*! \fn private static BufferedImage flipImageHorizontally(BufferedImage image)
        \brief Oglindeste o imagine pe orizontala

        \param image Imaginea de oglindit
        \return Imaginea oglindita
     */
    private static BufferedImage flipImageHorizontally(BufferedImage image)
    {
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-image.getWidth(null), 0);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(image, null);
    }

    /*! \fn private static void createFallbackTiles()
        \brief Creeaza dale temporare in caz ca imaginile nu pot fi incarcate
     */
    private static void createFallbackTiles()
    {
        System.out.println("⚠ Creez dale temporare...");

        // Creeaza o dala de iarba (verde)
        grass = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = grass.createGraphics();
        g2d.setColor(new Color(34, 139, 34)); // Forest Green
        g2d.fillRect(0, 0, 16, 16);
        g2d.setColor(new Color(0, 100, 0)); // Dark Green pentru bordura
        g2d.drawRect(0, 0, 15, 15);
        // Adauga niste "iarba"
        g2d.setColor(new Color(50, 205, 50));
        for (int i = 0; i < 10; i++) {
            int x = (int)(Math.random() * 14) + 1;
            int y = (int)(Math.random() * 14) + 1;
            g2d.fillRect(x, y, 1, 2);
        }
        g2d.dispose();

        // Creeaza o dala de copac (maro cu verde)
        tree = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        g2d = tree.createGraphics();
        // Trunchiulcopacului (maro)
        g2d.setColor(new Color(139, 69, 19)); // Saddle Brown
        g2d.fillRect(6, 8, 4, 8);
        // Coroana copacului (verde inchis)
        g2d.setColor(new Color(0, 100, 0)); // Dark Green
        g2d.fillOval(2, 2, 12, 10);
        // Frunze suplimentare
        g2d.setColor(new Color(34, 139, 34));
        g2d.fillOval(3, 3, 10, 8);
        g2d.dispose();
    }

    /*! \fn private static BufferedImage createHeroImage(Color color, boolean facingRight)
        \brief Creeaza o imagine temporara pentru erou

        \param color Culoarea imaginii eroului
        \param facingRight Daca eroul se uita spre dreapta
     */
    private static BufferedImage createHeroImage(Color color, boolean facingRight)
    {
        BufferedImage heroImage = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = heroImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Corp
        g2d.setColor(color);
        g2d.fillOval(12, 16, 24, 32);

        // Cap
        g2d.setColor(new Color(255, 220, 177)); // Flesh color
        g2d.fillOval(16, 8, 16, 16);

        // Ochi (pozitionati in functie de directie)
        g2d.setColor(Color.BLACK);
        if (facingRight) {
            g2d.fillOval(22, 12, 2, 2);
            g2d.fillOval(26, 12, 2, 2);
        } else {
            g2d.fillOval(20, 12, 2, 2);
            g2d.fillOval(24, 12, 2, 2);
        }

        // Maini
        g2d.setColor(new Color(255, 220, 177));
        g2d.fillOval(8, 20, 8, 8);
        g2d.fillOval(32, 20, 8, 8);

        // Picioare
        g2d.setColor(color.darker());
        g2d.fillOval(16, 40, 6, 8);
        g2d.fillOval(26, 40, 6, 8);

        // Bordura
        g2d.setColor(Color.BLACK);
        g2d.drawOval(12, 16, 24, 32);
        g2d.drawOval(16, 8, 16, 16);

        g2d.dispose();
        return heroImage;
    }
}