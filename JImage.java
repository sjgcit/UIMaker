/*
 * JImage.java
 *
 * $Id: JImage.java,v 1.8 2013/09/02 02:26:13 sjg Exp $
 *
 * (c) Stephen Geary, Aug 2013
 *
 * Component for displaying an image which supports scaled views
 * and a default cached painting image to speedup display.
 */
import java.lang.* ;
import java.io.* ;
import java.awt.* ;
import java.awt.event.* ;
import java.awt.image.* ;
import javax.imageio.* ;
import javax.swing.* ;
public class JImage extends JComponent implements MouseListener, MouseMotionListener, ComponentListener
{
    private final static long serialVersionUID = -1L ;
    public BufferedImage image = null ;
    public BufferedImage scaledimage = null ;
    static int SCALE_NORM = 100 ;
    static int SCALE_MIN = 1 ;
    static int SCALE_MAX = 1000 ;
    public int scale = 100 ;
    static int SCALE_STATE_FIT = 0 ;
    static int SCALE_STATE_FREE = 1 ;
    public int scalestate = SCALE_STATE_FIT ;
    public JImage()
    {
        super() ;
        this.image = null ;
        this.scalestate = JImage.SCALE_STATE_FIT ;
        this.addMouseListener( this ) ;
        this.addMouseMotionListener( this ) ;
        this.addComponentListener( this ) ;
    }
    public void setImage( BufferedImage img )
    {
        this.image = img ;
        this.scaledimage = null ;
    }
    public Dimension getImageDimension()
    {
        Dimension retd = new Dimension(0,0) ;
        if( this.image == null )
        {
            return retd ;
        }
        retd = new Dimension( this.image.getWidth(null), this.image.getHeight(null) ) ;
        // debug.debug( retd ) ;
        return retd ;
    }
    public void setScale( int val )
    {
        int newval = val ;
        if( val < JImage.SCALE_MIN )
        {
            newval = JImage.SCALE_MIN ;
        }
        else
        {
            if( val > JImage.SCALE_MAX )
            {
                newval = JImage.SCALE_MAX ;
            }
            else
            {
                newval = val ;
            }
        }
        this.scale = newval ;
        // debug.debug( this.scale ) ;
        this.scaledimage = null ;
        this.repaint() ;
    }
    public int getScale()
    {
        return this.scale ;
    }
    public void zoomOut()
    {
        this.setScale( this.getScale() / 2 ) ;
        this.scalestate = JImage.SCALE_STATE_FREE ;
    }
    public void zoomIn()
    {
        this.setScale( this.getScale() * 2 ) ;
        this.scalestate = JImage.SCALE_STATE_FREE ;
    }
    public void zoomFit()
    {
        Dimension di = this.getImageDimension() ;
        if( di == null )
        {
            return ;
        }
        int w = di.width ;
        int h = di.height ;
        if( w*h == 0 )
        {
            return ;
        }
        Dimension d = null ;
        d = this.getSize() ;
        // debug.debug( d, di ) ;
        w = ( SCALE_NORM * d.width ) / w ;
        h = ( SCALE_NORM * d.height ) / h ;
        this.scalestate = JImage.SCALE_STATE_FIT ;
        this.setScale( Math.min( w, h ) ) ;
    }
    public void paint( Graphics g )
    {
        // debug.debug() ;
        super.paint(g) ;
        if( g == null )
        {
            return ;
        }
        if( this.image == null )
        {
            return ;
        }
        // always paint a cached scaled image to avoid having to
        // scale large images on the fly all the time.
        Dimension di = this.getImageDimension() ;
        int h = di.height ;
        int w = di.width ;
        int w1 = ( this.scale * w ) / SCALE_NORM ;
        int h1 = ( this.scale * h ) / SCALE_NORM ;
        // debug.debug( h, w, h1, w1 ) ;
        if( this.scaledimage == null )
        {
            // this.scaledimage = this.image.getScaledInstance( w1, h1, Image.SCALE_SMOOTH ) ;
            this.scaledimage = new BufferedImage( w1, h1, this.image.getType() ) ;
            Graphics2D g2 = this.scaledimage.createGraphics() ;
            g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR ) ;
            g2.drawImage( this.image, 0, 0, w1, h1, 0, 0, w, h, null ) ;
            // this.waitForImage( this.scaledimage ) ;
        }
        g.drawImage( this.scaledimage, 0, 0, this ) ;
    }
    // co-ordinate conversions
    public Point pos2img( Point p )
    {
        Point p1 = new Point(0,0) ;
        p1.x = ( SCALE_NORM * p.x ) / this.scale ;
        p1.y = ( SCALE_NORM * p.y ) / this.scale ;
        Dimension d = this.getImageDimension() ;
        if( ( p1.x >= d.width ) ||
            ( p1.y >= d.height ) ||
            ( p1.x < 0 ) ||
            ( p1.y < 0 )
          )
        {
            return null ;
        }
        return p1 ;
    }
    public Point img2pos( Point p )
    {
        Point p1 = new Point(0,0) ;
        p1.x = ( this.scale * p.x ) / SCALE_NORM ;
        p1.y = ( this.scale * p.y ) / SCALE_NORM ;
        return p1 ;
    }
    // for mouseListener
    private Cursor savedcursor = null ;
    public void mouseClicked( MouseEvent me )
    {
        if( this.image == null )
        {
            return ;
        }
        if( me.getButton() != MouseEvent.BUTTON1 )
        {
            // only accept left clicks at present
            return ;
        }
        Point p = me.getPoint() ;
        Point p2 = this.pos2img( p ) ;
        if( p2 == null )
        {
            return ;
        }
        debug.debug( p2 ) ;
    }
    public void mouseEntered( MouseEvent me )
    {
        // change the mouse pointer to show it's editable
        Component c = me.getComponent() ;
        if( c != null )
        {
            this.savedcursor = c.getCursor() ;
            c.setCursor( Cursor.getPredefinedCursor( Cursor.CROSSHAIR_CURSOR ) ) ;
        }
    }
    public void mouseExited( MouseEvent me )
    {
        // restore the mouse pointer to whatever it was
        if( savedcursor != null )
        {
            Component c = me.getComponent() ;
            if( c != null )
            {
                c.setCursor( this.savedcursor ) ;
                this.savedcursor = null ;
            }
        }
    }
    public void mousePressed( MouseEvent me )
    {
    }
    public void mouseReleased( MouseEvent me )
    {
    }
    // for mouseMotionListener
    public void mouseDragged( MouseEvent me )
    {
    }
    public void mouseMoved( MouseEvent me )
    {
    }
    // for ComponentListener
    public void componentMoved( ComponentEvent ce )
    {
    }
    public void componentResized( ComponentEvent ce )
    {
        if( this.scalestate == SCALE_STATE_FIT )
        {
            this.zoomFit() ;
        }
    }
    public void componentHidden( ComponentEvent ce )
    {
    }
    public void componentShown( ComponentEvent ce )
    {
    }
}
