/*
 * SJGFrame.java
 *
 * $Id: SJGFrame.java,v 1.5 2013/08/08 11:46:49 sjg Exp $
 *
 * (c) Stephen Geary, 
 */
import java.lang.* ;
import java.util.* ;
import java.io.* ;
import java.awt.* ;
import java.awt.event.* ;
import javax.swing.* ;
import javax.swing.border.* ;
/*
 * minsize.gpp
 *
 * $Id: minsize.gpp,v 1.2 2013/08/08 11:46:49 sjg Exp $
 *
 * (c) Stephen Geary, Aug 2013
 *
 * Include file to include support for minimum size restrictions for components
 */
public class SJGFrame extends JFrame implements ComponentListener
{
    private final static long serialVersionUID = -1L ;
    public SJGFrame()
    {
        super() ;
        { this.addComponentListener( this ) ; this.setPreferredSizeAsMinimum( true ) ; } ;
    }
    private boolean usepreferredsizeasminimum = true ; public void setPreferredSizeAsMinimum( boolean flag ) { this.usepreferredsizeasminimum = flag ; } public boolean getPreferredSizeAsMinimum() { return this.usepreferredsizeasminimum ; } public void componentResized( ComponentEvent ce ) { if( ! this.getPreferredSizeAsMinimum() ) { return ; } Component c = ce.getComponent() ; SJGUtils.forcePreferredSize( c ) ; } public void componentMoved( ComponentEvent ce ) { } public void componentHidden( ComponentEvent ce ) { } public void componentShown( ComponentEvent ce ) { }
}
