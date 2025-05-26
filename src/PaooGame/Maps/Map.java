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
    private Tile grassTile; // Dală de iarbă creată dinamic

    /**
     * Constructor ce primește referința principală și încarcă harta.
     */
    public Map(RefLinks refLink) {
        this.refLink = refLink;

        // Creează dala de iarbă
        grassTile = Tile.GetGrassTile();

        // Încearcă să încarce harta TMX, dacă nu reușește, creează o hartă simplă
        try {
            LoadWorldFromTMX("res/Mapa/The_map.tmx");
            System.out.println("✓ Hartă TMX încărcată cu succes: " + width + "x" + height);
        } catch (Exception e) {
            System.out.println("⚠ Nu s-a putut încărca TMX, se creează hartă simplă: " + e.getMessage());
            CreateSimpleMap();
        }
    }

    public void Update() {
        // actualizări ale hărții, dacă este cazul
    }

    /**
     * Desenează harta pe ecran.
     */
    public void Draw(Graphics g) {
        // Calculează câte dale încap pe ecran
        int tilesX = (refLink.GetGame().GetWidth() / Tile.TILE_WIDTH) + 1;
        int tilesY = (refLink.GetGame().GetHeight() / Tile.TILE_HEIGHT) + 1;

        // Limitează la dimensiunile hărții
        tilesX = Math.min(tilesX, width);
        tilesY = Math.min(tilesY, height);

        for (int y = 0; y < tilesY; y++) {
            for (int x = 0; x < tilesX; x++) {
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

        int tileId = tiles[x][y];

        // ID 0 sau invalid = iarbă (dala creată dinamic)
        if (tileId == 0 || tileId >= Tile.tiles.length || Tile.tiles[tileId] == null)
        {
            return grassTile;
        }

        return Tile.tiles[tileId];
    }

    /**
     * Încarcă harta dintr-un fișier .tmx în format CSV.
     */
    private void LoadWorldFromTMX(String path) throws Exception {
        File file = new File(path);
        if (!file.exists()) {
            throw new Exception("Fișierul TMX nu există: " + path);
        }

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);
        doc.getDocumentElement().normalize();

        Element mapElement = doc.getDocumentElement();
        width = Integer.parseInt(mapElement.getAttribute("width"));
        height = Integer.parseInt(mapElement.getAttribute("height"));

        tiles = new int[width][height];

        NodeList layerList = doc.getElementsByTagName("layer");
        if (layerList.getLength() == 0) {
            throw new Exception("Nu s-au găsit layer-uri în fișierul TMX");
        }

        Element layer = (Element) layerList.item(0);
        NodeList dataList = layer.getElementsByTagName("data");
        if (dataList.getLength() == 0) {
            throw new Exception("Nu s-au găsit date în layer-ul TMX");
        }

        Element data = (Element) dataList.item(0);
        String[] tileIds = data.getTextContent().trim().replace("\n", "").split(",");

        if (tileIds.length != width * height) {
            throw new Exception("Numărul de tile-uri nu corespunde cu dimensiunile hărții");
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = y * width + x;
                int id = Integer.parseInt(tileIds[index].trim());
                // Convertește ID-ul TMX la ID-ul nostru
                // 0 sau 1 din TMX = 0 (iarbă din grassTile)
                // 2+ din TMX = 1 (copac din treeTile)
                if (id <= 1) {
                    tiles[x][y] = 0; // iarbă
                } else {
                    tiles[x][y] = 1; // copac
                }
            }
        }
    }

    private void CreateSimpleMap() {
        System.out.println("Se creează o hartă simplă pentru teste...");
        width = 20;
        height = 15;
        tiles = new int[width][height];

        // Umplem harta cu iarba (ID 0)
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tiles[x][y] = 0; // iarbă (grassTile)
            }
        }

        // Adăugăm câțiva copaci (ID 1) pentru teste
        // Bordură de copaci
        for (int x = 0; x < width; x++) {
            tiles[x][0] = 1; // treeTile sus
            tiles[x][height - 1] = 1; // treeTile jos
        }
        for (int y = 0; y < height; y++) {
            tiles[0][y] = 1; // treeTile stânga
            tiles[width - 1][y] = 1; // treeTile dreapta
        }

        // Câțiva copaci împrăștiați
        for (int i = 2; i < width - 2; i += 3) {
            for (int j = 2; j < height - 2; j += 3) {
                if (Math.random() > 0.5) {
                    tiles[i][j] = 1;
                }
            }
        }

        System.out.println("✓ Hartă simplă creată: " + width + "x" + height);
    }
}