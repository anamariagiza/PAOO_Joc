package PaooGame.Maps;

import PaooGame.RefLinks;
import PaooGame.Tiles.Tile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;

import java.awt.*;
import java.io.File;

/**
 * Clasa ce reprezintă o hartă de joc încărcată din fișier .tmx (Tiled).
 */
public class Map {
    private RefLinks refLink;
    private int width;
    private int height;
    private int[][] tiles;

    /**
     * Constructor ce primește referința principală și încarcă harta.
     */
    public Map(RefLinks refLink) {
        this.refLink = refLink;
        LoadWorldFromTMX("res/Mapa/The_map.tmx"); // înlocuiește cu calea ta exactă
    }

    public void Update() {
        // actualizări ale hărții, dacă este cazul
    }

    /**
     * Desenează harta pe ecran.
     */
    public void Draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(100, 100, 50, 50);
        for (int y = 0; y < refLink.GetGame().GetHeight() / Tile.TILE_HEIGHT; y++) {
            for (int x = 0; x < refLink.GetGame().GetWidth() / Tile.TILE_WIDTH; x++) {
                GetTile(x, y).Draw(g, x * Tile.TILE_WIDTH, y * Tile.TILE_HEIGHT);
            }
        }
    }

    /**
     * Returnează dala la poziția specificată.
     */
    public Tile GetTile(int x, int y)
    {
        if (x < 0 || y < 0 || x >= width || y >= height)
        {
            return Tile.treeTile; // fallback dacă accesezi ceva în afara hărții
        }

        Tile t = Tile.tiles[tiles[x][y]];

        if (t == null)
        {
            return Tile.treeTile; // fallback dacă nu există tile la acel ID
        }

        return t;
    }

    /**
     * Încarcă harta dintr-un fișier .tmx în format CSV.
     */
    private void LoadWorldFromTMX(String path) {
        try {
            File file = new File(path);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            Element mapElement = doc.getDocumentElement();
            width = Integer.parseInt(mapElement.getAttribute("width"));
            height = Integer.parseInt(mapElement.getAttribute("height"));

            tiles = new int[width][height];

            NodeList layerList = doc.getElementsByTagName("layer");
            Element layer = (Element) layerList.item(0);
            Element data = (Element) layer.getElementsByTagName("data").item(0);

            String[] tileIds = data.getTextContent().trim().replace("\n", "").split(",");

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int index = y * width + x;
                    int id = Integer.parseInt(tileIds[index].trim());
                    tiles[x][y] = id - 1; // în .tmx tile IDs încep de la 1
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void CreateSimpleMap() {
        System.out.println("Se creează o hartă simplă pentru teste...");
        width = 20;
        height = 20;
        tiles = new int[width][height];

        // Umplem harta cu iarba (ID 0)
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tiles[x][y] = 0;
            }
        }

        // Adăugăm câțiva copaci (ID 1) pentru teste
        for (int i = 0; i < width; i += 5) {
            for (int j = 0; j < height; j += 5) {
                tiles[i][j] = 1;
            }
        }
    }
}
