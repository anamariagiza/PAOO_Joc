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
 * Această versiune încarcă DOAR harta TMX - dacă aceasta eșuează, aplicația va afișa eroarea clar.
 */
public class Map {
    private RefLinks refLink;
    private int width;
    private int height;
    private int[][] tiles;
    private Tile grassTile; // Dală de iarbă creată dinamic
    private boolean mapLoaded = false; // Flag pentru a știi dacă harta s-a încărcat cu succes

    /**
     * Constructor ce primește referința principală și încarcă DOAR harta TMX.
     */
    public Map(RefLinks refLink) {
        this.refLink = refLink;


        // Încearcă să încarce harta TMX - dacă eșuează, nu creăm nimic altceva
        try {
            LoadWorldFromTMX("res\\Mapa\\The_map.tmx");
            mapLoaded = true;
            System.out.println("✓ SUCCES! Harta TMX încărcată corect: " + width + "x" + height + " tile-uri");
        } catch (Exception e) {
            mapLoaded = false;
            System.out.println("EROARE CRITICĂ: Harta TMX nu s-a putut încărca!");
            System.out.println("Motivul: " + e.getMessage());
            System.out.println("Stack trace complet:");
            e.printStackTrace();
            System.out.println("\nVerifică următoarele:");
            System.out.println("1. Fișierul res/Mapa/The_map.tmx există?");
            System.out.println("2. Fișierul este un TMX valid exportat din Tiled?");
            System.out.println("3. Datele sunt în format CSV?");
            System.out.println("4. Calea către fișier este corectă?");
        }
    }

    public void Update() {
        // actualizări ale hărții, dacă este cazul
    }

    /**
     * Desenează harta pe ecran DOAR dacă aceasta s-a încărcat cu succes.
     */
    public void Draw(Graphics g) {
        // Dacă harta nu s-a încărcat, afișează un mesaj de eroare vizual
        if (!mapLoaded || tiles == null || width <= 0 || height <= 0) {
            // Desenează un fundal roșu pentru a indica eroarea
            g.setColor(Color.RED);
            g.fillRect(0, 0, refLink.GetGame().GetWidth(), refLink.GetGame().GetHeight());

            // Afișează mesajul de eroare pe ecran
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("EROARE: Harta TMX nu s-a încărcat!", 50, 100);
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            g.drawString("Verifică consola pentru detalii despre eroare.", 50, 140);
            g.drawString("Fișierul res/Mapa/The_map.tmx trebuie să existe și să fie valid.", 50, 170);
            return;
        }


        // Calculează câte dale încap pe ecran
        int tilesX = (refLink.GetGame().GetWidth() / Tile.TILE_WIDTH) + 1;
        int tilesY = (refLink.GetGame().GetHeight() / Tile.TILE_HEIGHT) + 1;

        // Limitează la dimensiunile hărții
        tilesX = Math.min(tilesX, width);
        tilesY = Math.min(tilesY, height);

        // Desenează harta tile cu tile
        for (int y = 0; y < tilesY; y++) {
            for (int x = 0; x < tilesX; x++) {
                GetTile(x, y).Draw(g, x * Tile.TILE_WIDTH, y * Tile.TILE_HEIGHT);
            }
        }

        // Afișează informații despre hartă în colțul stâng-sus pentru confirmare
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("Hartă TMX: " + width + "x" + height, 10, 20);
    }

    /**
     * Returnează dala la poziția specificată.
     */
    public Tile GetTile(int x, int y) {
        if (!mapLoaded || tiles == null) {
            throw new IllegalStateException("Harta nu este încărcată, nu pot returna tile.");
        }

        if (x < 0 || y < 0 || x >= width || y >= height) {
            throw new IndexOutOfBoundsException("Tile [" + x + "," + y + "] este în afara hărții.");
        }

        int tileId = tiles[x][y];

        if (tileId < 0 || tileId >= Tile.tiles.length) {
            throw new IllegalArgumentException("ID invalid de tile: " + tileId + " la [" + x + "," + y + "]");
        }

        Tile tile = Tile.tiles[tileId];
        if (tile == null) {
            throw new IllegalStateException("Tile-ul cu ID " + tileId + " este null la [" + x + "," + y + "]");
        }

        return tile;
    }

    /**
     * Încarcă harta dintr-un fișier .tmx în format CSV cu logging detaliat.
     */
    private void LoadWorldFromTMX(String path) throws Exception {
        System.out.println("🔄 Începe încărcarea hărții TMX din: " + path);

        // Verifică existența fișierului
        File file = new File(path);
        System.out.println("📁 Calea absolută către fișier: " + file.getAbsolutePath());

        if (!file.exists()) {
            throw new Exception("Fișierul TMX nu există la calea: " + file.getAbsolutePath());
        }

        if (!file.canRead()) {
            throw new Exception("Fișierul TMX nu poate fi citit. Verifică permisiunile.");
        }

        System.out.println("✓ Fișierul TMX găsit și poate fi citit");

        // Parsează XML-ul
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);
        doc.getDocumentElement().normalize();

        Element mapElement = doc.getDocumentElement();
        System.out.println("📋 Element root găsit: " + mapElement.getNodeName());

        // Verifică dacă elementul root este 'map'
        if (!"map".equals(mapElement.getNodeName())) {
            throw new Exception("Fișierul nu este un TMX valid. Elementul root este '" +
                    mapElement.getNodeName() + "' în loc de 'map'");
        }

        // Extrage dimensiunile hărții
        String widthAttr = mapElement.getAttribute("width");
        String heightAttr = mapElement.getAttribute("height");

        if (widthAttr.isEmpty() || heightAttr.isEmpty()) {
            throw new Exception("Atributele 'width' și 'height' lipsesc din elementul 'map'");
        }

        width = Integer.parseInt(widthAttr);
        height = Integer.parseInt(heightAttr);

        System.out.println("📏 Dimensiuni hartă extrase: " + width + " x " + height + " tile-uri");

        if (width <= 0 || height <= 0) {
            throw new Exception("Dimensiunile hărții sunt invalide: " + width + "x" + height);
        }

        // Inițializează matricea de tile-uri
        tiles = new int[width][height];

        // Găsește primul layer
        NodeList layerList = doc.getElementsByTagName("layer");
        System.out.println("🎭 Numărul de layer-uri găsite: " + layerList.getLength());

        if (layerList.getLength() == 0) {
            throw new Exception("Nu s-au găsit layer-uri în fișierul TMX");
        }

        Element layer = (Element) layerList.item(0);
        String layerName = layer.getAttribute("name");
        System.out.println("🎯 Se folosește primul layer: '" + layerName + "'");

        // Găsește datele din layer
        NodeList dataList = layer.getElementsByTagName("data");
        if (dataList.getLength() == 0) {
            throw new Exception("Nu s-au găsit elemente 'data' în layer-ul '" + layerName + "'");
        }

        Element data = (Element) dataList.item(0);
        String encoding = data.getAttribute("encoding");
        String compression = data.getAttribute("compression");

        System.out.println("💾 Encoding date: '" + (encoding.isEmpty() ? "none" : encoding) + "'");
        System.out.println("🗜️ Compresie: '" + (compression.isEmpty() ? "none" : compression) + "'");

        // Verifică encoding-ul (acceptăm doar CSV sau fără encoding)
        if (!encoding.isEmpty() && !"csv".equals(encoding)) {
            throw new Exception("Encoding '" + encoding + "' nu este suportat. Folosește CSV în Tiled.");
        }

        if (!compression.isEmpty()) {
            throw new Exception("Compresia '" + compression + "' nu este suportată. Dezactivează compresia în Tiled.");
        }

        // Extrage și procesează datele CSV
        String csvData = data.getTextContent().trim();
        System.out.println("📊 Lungimea datelor brute: " + csvData.length() + " caractere");

        if (csvData.isEmpty()) {
            throw new Exception("Datele CSV sunt goale în elementul 'data'");
        }

        // Curăță datele CSV (elimină spații, newlines, etc.)
        csvData = csvData.replaceAll("\\s+", "");
        String[] tileIds = csvData.split(",");

        System.out.println("🔍 Numărul de tile-uri procesate: " + tileIds.length);
        System.out.println("🎯 Numărul de tile-uri așteptat: " + (width * height));

        if (tileIds.length != width * height) {
            throw new Exception("Nepotrivire în numărul de tile-uri! Găsite: " + tileIds.length +
                    ", așteptate: " + (width * height) +
                    ". Verifică dimensiunile hărții în Tiled.");
        }

        // Procesează fiecare tile și populează matricea
        System.out.println("⚙️ Se procesează tile-urile...");
        int processedTiles = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = y * width + x;
                try {
                    int id = Integer.parseInt(tileIds[index].trim());

                    // Convertește ID-ul TMX la ID-ul nostru
                    // În Tiled, 0 = gol, 1+ = tile-uri
                    // În codul nostru: 0 = iarbă, 1 = copac
                    tiles[x][y] = id; // păstrează ID-ul exact din TMX

                    processedTiles++;
                } catch (NumberFormatException e) {
                    throw new Exception("ID de tile invalid la poziția [" + x + "," + y +
                            "] (index " + index + "): '" + tileIds[index] + "'");
                }
            }
        }

        System.out.println("✅ Procesare completă! " + processedTiles + " tile-uri procesate cu succes");
        System.out.println("🎉 Harta TMX a fost încărcată complet și este gata de utilizare!");
    }

    /**
     * Returnează true dacă harta s-a încărcat cu succes
     */
    public boolean isMapLoaded() {
        return mapLoaded;
    }

    /**
     * Returnează dimensiunile hărții pentru debugging
     */
    public String getMapInfo() {
        if (mapLoaded) {
            return "Hartă TMX: " + width + "x" + height + " tile-uri";
        } else {
            return "Harta TMX nu este încărcată";
        }
    }
}