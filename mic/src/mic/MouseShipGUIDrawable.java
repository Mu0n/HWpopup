package mic;

import VASSAL.build.GameModule;
import VASSAL.build.module.Map;
import VASSAL.build.module.map.Drawable;
import VASSAL.counters.Decorator;
import VASSAL.counters.FreeRotator;
import VASSAL.counters.GamePiece;
import VASSAL.tools.DataArchive;
import VASSAL.tools.io.FileArchive;
import com.google.common.collect.Lists;
import javafx.scene.transform.Affine;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.Key;
import java.text.AttributedString;
import java.util.Collection;

/**
 * Created by Mic on 2019-01-17.
 *
 * This class prepares the drawable so that the vassal engine knows when to draw stuff. No encoder is used since the UI is not shared to others
 */
public class MouseShipGUIDrawable implements Drawable {
    GamePiece _shipPiece;
    Map _map;
    XWS2Pilots _pilotShip;
    XWS2Pilots.Pilot2e _pilot;
    int smallGapX = 5;
    int padX = 20;
    int padY = 20;
    int cursorX = padX;
    int cursorY = padY;
    int ulX = 0; //upper left corner of the popup
    int ulY = 0;
    int totalWidth;
    int totalHeight;
    Collection<miElement> listOfInteractiveElements = Lists.newArrayList();
    double scale;

    public MouseShipGUIDrawable(GamePiece shipPiece, Map map, XWS2Pilots pilotShip, XWS2Pilots.Pilot2e pilot){
        _shipPiece = shipPiece;
        _map = map;
        _pilotShip = pilotShip;
        _pilot = pilot;

        scale = _map.getZoom();

        //Define the top left coordinate of the popup outline
        ulX = shipPiece.getPosition().x + 150;
        ulY = shipPiece.getPosition().y - 150;

        //Barrel Roll test
        miElement brIconLeft = new miElement("mi_barrelroll.png", ulX + cursorX, ulY + cursorY,
                KeyStroke.getKeyStroke(KeyEvent.VK_8, KeyEvent.CTRL_DOWN_MASK, false));
        listOfInteractiveElements.add(brIconLeft);

        miElement brIconLeftDown = new miElement("mi_barrelroll.png", ulX + cursorX, ulY + cursorY + smallGapX,
                KeyStroke.getKeyStroke(KeyEvent.VK_8, KeyEvent.CTRL_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK, false));
        listOfInteractiveElements.add(brIconLeftDown);
        cursorX += brIconLeft.image.getWidth() + smallGapX;


        miElement shipGfx = new miElement(getShipImage(pilotShip),ulX+cursorX, ulY+cursorY, null);
        listOfInteractiveElements.add(shipGfx);
        cursorX += shipGfx.image.getWidth() + smallGapX;
        cursorY += shipGfx.image.getHeight();

        miElement brIconRight = new miElement("mi_barrelroll.png", ulX + cursorX, ulY + cursorY,
                KeyStroke.getKeyStroke(KeyEvent.VK_8, KeyEvent.ALT_DOWN_MASK, false));
        miElement brIconRightDown = new miElement("mi_barrelroll.png", ulX + cursorX, ulY + cursorY,
                KeyStroke.getKeyStroke(KeyEvent.VK_8, KeyEvent.ALT_DOWN_MASK+ KeyEvent.SHIFT_DOWN_MASK, false));
        listOfInteractiveElements.add(brIconRight);
        listOfInteractiveElements.add(brIconRightDown);

        cursorX += brIconRight.image.getWidth() + padX;

        totalWidth = cursorX + padX;
        totalHeight = cursorY + padY;
    }

    private String getShipImage(XWS2Pilots pilotShip) {
        StringBuilder sb = new StringBuilder();

        sb.append("S2e_");
        sb.append(pilotShip.getShipXWS());
        if(pilotShip.hasDualBase()){
            sb.append("_");
            sb.append(pilotShip.getBaseImage1Identifier());
        }
        else if(pilotShip.getBaseImage1Identifier() !=null) {
            if (!pilotShip.getBaseImage1Identifier().equals("")) {
                sb.append("-");
                sb.append(pilotShip.getBaseImage1Identifier());
            }

        }
        sb.append(".png");
        Util.logToChat("gonna use " + sb.toString());
        return sb.toString();
    }

    public void draw(Graphics g, Map map) {
        Graphics2D g2d = (Graphics2D) g;

        Rectangle outline = new Rectangle(totalWidth,totalHeight);

        //Object prevAntiAliasing = g2d.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        //g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        scale = _map.getZoom();

        AffineTransform scaler = AffineTransform.getScaleInstance(scale, scale);
        scaler.translate(ulX,ulY);
        g2d.setPaint(Color.WHITE);
        Shape transformedOutline = scaler.createTransformedShape(outline);
        g2d.fill(transformedOutline);

        Line2D.Double firstLine = new Line2D.Double(new Point2D.Double(-150,150),
                new Point(0,  0));
        Line2D.Double secondLine = new Line2D.Double(new Point2D.Double(-150,150),
                new Point(0,  outline.height));

        g2d.draw(scaler.createTransformedShape(firstLine));
        g2d.draw(scaler.createTransformedShape(secondLine));

        g2d.setPaint(new Color(0,0,255, 150));
        for(miElement elem : listOfInteractiveElements){
            AffineTransform af;
            if(_pilotShip.getSize().equals("Large") || _pilotShip.getSize().equals("large")) af = elem.getTransformForDraw(scale, 0.5);
            else af = elem.getTransformForDraw(scale);
            g2d.drawImage(elem.image, af, new ImageObserver() {
                public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
                    return false;
                }
            });
            Rectangle rawR = elem.image.getData().getBounds();
            Shape s = af.createTransformedShape(rawR);
            g2d.fillRect(s.getBounds().x, s.getBounds().y, s.getBounds().width, s.getBounds().height);
        }

        /*  piece of code that can fetch the maneuver icons as seen on the dials
        try{
            int i = 0;
            for(String move : _pilotShip.getDial()){
                String imageNameToLoad = StemDial2e.dialHeadingImages.get(move.substring(1,3));

                InputStream inputstream = new BufferedInputStream(fileArchive.getInputStream("images/"+imageNameToLoad));
                image = ImageIO.read(inputstream);
                inputstream.close();

                AffineTransform translateNScale = new AffineTransform();
                translateNScale.scale(scale, scale);
                translateNScale.translate(60+ _x + i* 80,_y+50);
                i++;
                if(image!=null) g2d.drawImage(image, translateNScale, new ImageObserver() {
                    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
                        return false;
                    }
                });
            }

        }catch(Exception e){}


        try{

            fileArchive.close();
            dataArchive.close();
        }catch(IOException ioe){
            Util.logToChat("can't close the xwd2 files " + ioe.getMessage());
        }

        drawText(_pilotShip.getDial().toString(),scale,_x + 30, _y + 50, g2d);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, prevAntiAliasing);
        */

    }

    public boolean drawAboveCounters() {
        return false;
    }


    private static void drawText(String text, double scale, double x, double y, Graphics2D graphics2D) {
        AttributedString attstring = new AttributedString(text);
        attstring.addAttribute(TextAttribute.FONT, new Font("Arial", 0,32));
        attstring.addAttribute(TextAttribute.RUN_DIRECTION, TextAttribute.RUN_DIRECTION_LTR);
        FontRenderContext frc = graphics2D.getFontRenderContext();
        TextLayout t = new TextLayout(attstring.getIterator(), frc);
        Shape textShape = t.getOutline(null);

        textShape = AffineTransform.getTranslateInstance(x, y)
                .createTransformedShape(textShape);
        textShape = AffineTransform.getScaleInstance(scale, scale)
                .createTransformedShape(textShape);
        graphics2D.setColor(Color.white);
        graphics2D.fill(textShape);

        if (scale > 0.60) {
            // stroke makes it muddy at low scale
            graphics2D.setColor(Color.black);
            graphics2D.setStroke(new BasicStroke(0.8f));
            graphics2D.draw(textShape);
        }
    }


    protected static class miElement {
        BufferedImage image;
        int x;
        int y;

        KeyStroke associatedKeyStroke;

        public miElement(String fileName, int wantedX, int wantedY, KeyStroke wantedKeyStroke){
            x = wantedX;
            y = wantedY;
            associatedKeyStroke = wantedKeyStroke;

            //load the image
            try {
                GameModule gameModule = GameModule.getGameModule();
                DataArchive dataArchive = gameModule.getDataArchive();
                FileArchive fileArchive = dataArchive.getArchive();

                InputStream inputstream = new BufferedInputStream(fileArchive.getInputStream("images/" + fileName));
                image = ImageIO.read(inputstream);
                inputstream.close();
            }
            catch(Exception e){
                Util.logToChat("Failed to load GUI image " + fileName);
            }
        }

        public AffineTransform getTransformForClick(double scale){
            AffineTransform affineTransform = new AffineTransform();
            affineTransform.translate(x, y);
            return affineTransform;
        }

        public AffineTransform getTransformForDraw(double scale){
            AffineTransform affineTransform = new AffineTransform();
            affineTransform.scale(scale, scale);
            affineTransform.translate(x, y);
            return affineTransform;
        }

        public AffineTransform getTransformForDraw(double scale, double scale2){
            AffineTransform affineTransform = new AffineTransform();
            affineTransform.scale(scale*scale2, scale*scale2);
            affineTransform.translate(x, y);
            return affineTransform;
        }

    }
}