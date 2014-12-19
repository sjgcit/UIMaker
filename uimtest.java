/*
 * uimtest.java
 *
 * $Id: uimtest.java,v 1.26 2014/12/18 17:55:40 sjg Exp $
 *
 * (c) Stephen Geary, Aug 2013
 *
 * Test app for UIMaker
 */
import java.lang.* ;
import java.util.* ;
import java.io.* ;
import java.awt.* ;
import java.awt.event.* ;
import java.awt.image.* ;
import javax.swing.* ;
/*
 * An include file for macros used in some Java code.
 *
 * intended for consumption by a C preprocessor.
 *
 * $Id: stdinc.jh,v 1.13 2014/12/18 18:16:53 sjg Exp $
 */
/* __JAVA_FILE__ is added by javacx to the input stream it creates.
 *
 * This allows for some simple C-like macro behavior in debug messages
 */
/* Java changed JComboBox to a generic version which is rather stupid
 * as it makes source code incompatible with older compilers.
 *
 * As I sometimes need to write for different compilers I use a define
 * to bypass the issue and simply reference this pseudo class instead
 */
public class uimtest extends UIMaker
{
    public JPanel deck = null ;
    public JImage img = null ;
    private static uimtest uim = null ;
    public static void main( String[] args )
    {
        uimtest.uim = new uimtest() ;
        uimtest.uim.initUI( "sjgpanel.in", true ) ;
        // ( (SJGFrame)( uimtest.uim.getNamed( UIMaker.TOPNAME ) ) ).setPreferredSizeAsMinimum( false ) ;
        String[] names = null ;
        names = uim.getAllNames() ;
        Object obj = null ;
        for( String n : names )
        {
            obj = uim.getNamed( n ) ;
            ;
        }
        test_nullcheck() ;
    }
    public static void jdlclick( File f )
    {
        ;
    }
    public static void test_nullcheck()
    {
        JPanel jp = null ;
        { if( (jp) == null ){ return ; } } ;
        ;
    }
    public void initNamedComponents()
    {
        this.deck = (JPanel)( this.getNamed("cardpanel") ) ;
        this.img = (JImage)( this.getNamed("img") ) ;
        BufferedImage bi = null ;
        bi = SJGUtils.loadImage( this.getClass().getResource("resources/chalet.jpg") ) ;
        // _DEBUG( this.getClass().getResource("resources/chalet.jpg") ) ;
        if( bi == null )
        {
            ;
        }
        else
        {
            this.img.setImage( bi ) ;
            this.img.zoomFit() ;
        }
    }
    public void actionPerformed( ActionEvent ae )
    {
        String cmd = ae.getActionCommand() ;
        ;
        { if( (deck) == null ){ return ; } } ;
        if( cmd.equals( "m-act-exit" ) )
        {
            System.exit(0) ;
            return ;
        }
        if( cmd.startsWith( "card-" ) )
        {
            ;
            SJGUtils.setCard( deck, cmd ) ;
            return ;
        }
        if( cmd.equals( "act-load-image" ) )
        {
            this.loadImage() ;
            SJGUtils.setCard( deck, "card-5" ) ;
            return ;
        }
    }
    public void loadImage()
    {
        ;
        JImage ji = null ;
        ji = (JImage)( this.getNamed( "img" ) ) ;
        { if( (ji) == null ){ return ; } }
        JFileChooser fc = new JFileChooser( "." ) ; { if( (fc) == null ){ return ; } } int retval = fc.showOpenDialog( (JFrame)( this.getNamed( UIMaker.TOPNAME ) ) ) ; if( retval != JFileChooser.APPROVE_OPTION ) { ; return ; } ; ;
        ;
        BufferedImage bi = null ;
        bi = SJGUtils.loadImage( fc.getSelectedFile() ) ;
        { if( (bi) == null ){ return ; } }
        ji.setImage( bi ) ;
        ji.zoomFit() ;
    }
}
