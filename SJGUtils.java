/*
 * SJGUtils.java
 *
 * $Id: SJGUtils.java,v 1.34 2014/12/18 14:19:51 sjg Exp $
 *
 * (c) Stephen Geary, Jul 2013
 *
 * General purpose utility code that could be used outside of a
 * specific project.
 */
import java.lang.* ;
import java.util.* ;
import java.io.* ;
import java.net.* ;
import java.awt.* ;
import java.awt.image.* ;
import java.awt.event.* ;
import javax.swing.* ;
import javax.swing.border.* ;
import java.util.regex.* ;
import javax.imageio.* ;
import java.nio.* ;
import java.nio.file.* ;
import java.nio.channels.FileChannel.MapMode ;
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
public class SJGUtils
{
    // Random Number generation
    private static Random randgen = null ;
    public static int rndint( int max )
    {
        if( SJGUtils.randgen == null )
        {
            SJGUtils.randgen = new Random() ;
        }
        return SJGUtils.randgen.nextInt( max ) ;
    }
    // Image Table management code
    //
    // Maintains one static internal table if needed.
    private static Hashtable<String,Image> internalimagetable = null ;
    public static void addImage( String name, String path )
    {
        if( SJGUtils.internalimagetable == null )
        {
            SJGUtils.internalimagetable = new Hashtable<String,Image>() ;
        }
        SJGUtils.addImage( name, path, null ) ;
    }
    public static BufferedImage addImage( String name, String path, Hashtable<String,Image> imagetable )
    {
        BufferedImage img = null ;
        if( imagetable == null )
        {
            if( SJGUtils.internalimagetable == null )
            {
                SJGUtils.internalimagetable = new Hashtable<String,Image>() ;
            }
            imagetable = SJGUtils.internalimagetable ;
            // _DEBUGLN() ;
        }
        { if( (imagetable) == null ){ return null ; } } ;
        img = SJGUtils.loadImage( path ) ;
        if( img != null )
        {
            imagetable.put( name, img ) ;
            // _DEBUG( name ) ;
        }
        return img ;
    }
    public static BufferedImage loadImage( String path )
    {
        BufferedImage img = null ;
        { if( (path) == null ){ return null ; } } ;
        // _DEBUG( path ) ;
        if( path.endsWith( ".jpg" ) || path.endsWith( ".JPG" ) || path.endsWith( ".jpeg" ) || path.endsWith( ".JPEG" ) )
        {
            // use the custom JNI fast method
            // _DEBUGLN() ;
            img = JLibJPEG.loadImage( path ) ;
            if( img != null )
            {
                return img ;
            }
            // _DEBUGLN() ;
        }
        File f = null ;
        f = new File( path ) ;
        img = SJGUtils.loadImage( f ) ;
        return img ;
    }
    public static BufferedImage loadImage( File f )
    {
        BufferedImage img = null ;
        { if( (f) == null ){ return null ; } } ;
        boolean usecache = ImageIO.getUseCache() ;
        ImageIO.setUseCache( false ) ;
        try
        {
            img = ImageIO.read( f ) ;
        }
        catch( IOException _ex ) { ; return null ; }
        // _DEBUG( img ) ;
        ImageIO.setUseCache( usecache ) ;
        // _DEBUGLN() ;
        return img ;
    }
    public static BufferedImage loadImage( URL u )
    {
        BufferedImage img = null ;
        { if( (u) == null ){ return null ; } } ;
        try
        {
            img = ImageIO.read( u ) ;
        }
        catch( IOException _ex ) { ; return null ; }
        return img ;
    }
    public static Image getImage( String name )
    {
        return SJGUtils.getImage( name, SJGUtils.internalimagetable ) ;
    }
    public static Image getImage( String name, Hashtable<String,Image> imagetable )
    {
        Image img = null ;
        { if( (imagetable) == null ){ return null ; } } ;
        { if( (name) == null ){ return null ; } } ;
        img = imagetable.get( name ) ;
        return img ;
    }
    // Convenience method for code needing to open a Scanner
    public static Scanner openScanner( InputStream is )
    {
        { if( (is) == null ){ return null ; } } ;
        Scanner sc = null ;
        sc = new Scanner( is ) ;
        return sc ;
    }
    public static Scanner openScanner( String path )
    {
        File f = null ;
        f = new File(path) ;
        return SJGUtils.openScanner( f ) ;
    }
    public static Scanner openScanner( File f )
    {
        { if( (f) == null ){ return null ; } } ;
        Scanner sc = null ;
        try
        {
            sc = new Scanner( f ) ;
        }
        catch( java.io.FileNotFoundException _ex ) { ; return null ; }
        // _DEBUG( "Scanner delimiters : [ " + sc.delimiter() + " ]" ) ;
        return sc ;
    }
    public static String nextLine( Scanner sc )
    {
        if( ! sc.hasNext() )
        {
            return null ;
        }
        String s = sc.nextLine() ;
        s = s.trim() ;
        return s ;
    }
    // a method that reads the next input from a Scanner
    // and trims whitespace automatically
    //
    // - note this uses Java String's trim() method which
    // is not, in a strict lexical sense, a whitespace cleaner
    public static String nextText( Scanner sc )
    {
        return SJGUtils.nextText( sc, true ) ;
    }
    public static String nextText( Scanner sc, boolean ignorecomments )
    {
        String s = null ;
        if( ! sc.hasNext() )
        {
            return null ;
        }
        boolean commentfound = false ;
        while( ( s = sc.next() ) != null )
        {
            s = s.trim() ;
            if( ( s.length() == 0 ) || ( commentfound = ( ignorecomments && ( s.charAt(0) == '#' ) ) ) )
            {
                // is a comment or a zero length string
                // so try again
                if( sc.hasNext() )
                {
                    if( commentfound )
                    {
                        // read rest of line to EOL
                        sc.nextLine() ;
                    }
                    continue ;
                }
                else
                {
                    s = null ;
                    break ;
                }
            }
            else
            {
                // valid string to return
                // check if it's a string starting with q quote
                // in which case we need to keep reading until
                // we find the next non-escaped quote that ends
                // this text.
                if( s.startsWith( "\"" ) )
                {
                    // quoted
                    // _DEBUG( "initial quote detected :: [" + s + "]" ) ;
                    // trim the leading quote
                    s = s.substring(1) ;
                    // save the delimiter pattern
                    Pattern pat = sc.delimiter() ;
                    sc.useDelimiter( "\"" ) ;
                    String t = null ;
                    while( ( t = sc.next() ) != null )
                    {
                        s = s + t ;
                        // _DEBUG( "Adding [" + t + "] to s" ) ;
                        // does t end in an escaping slash ?
                        if( ! t.endsWith( "\\" ) )
                        {
                            // not escaped so we can finish
                            break ;
                        }
                        // escaped so we have to replace the last
                        // escape slash with a quotation
                        char[] ca = s.toCharArray() ;
                        ca[ca.length-1] = '"' ;
                        s = new String( ca ) ;
                    }
                    // we're finished the quoted section ( or have
                    // run out of text ) so we restore the original
                    // delimiter pattern
                    sc.useDelimiter( pat ) ;
                    // Scanner's work slightly perversly ( it seems )
                    // in that the delimiter we last stopped at is STILL
                    // the next thing that will be tested.
                    //
                    // This means that having switched delimiter again
                    // it will read the ending quote.  We have to take
                    // steps to avoid that or we'll end up with a string
                    // that starts with a quote next time which will
                    // incorrectly trigger the quoted text loop.
                    //
                    // the fix is happily simple : we skip the next quote
                    sc.skip("\"") ;
                }
                break ;
            }
        }
        // _DEBUG( "returning [" + s + "]" ) ;
        return s ;
    }
    // return an integer array by scanning a String for all numbers in it
    public static int[] getIntArray( String s )
    {
        int[] retarr = null ;
        int len = s.length() ;
        char[] ca = s.toCharArray() ;
        int count = 0 ;
        int i = 0 ;
        int j = 0 ;
        // count number of numbers in string
        while( i < len )
        {
            if( ! Character.isDigit( ca[i] ) )
            {
                i++ ;
            }
            else
            {
                count++ ;
                while( Character.isDigit( ca[i] ) )
                {
                    i++ ;
                    if( i >= len )
                    {
                        break ;
                    }
                }
            }
        }
        // create and fill return array
        retarr = new int[ count ] ;
        i = 0 ;
        while( i < len )
        {
            if( ! Character.isDigit( ca[i] ) )
            {
                i++ ;
            }
            else
            {
                retarr[j] = 0 ;
                while( Character.isDigit( ca[i] ) )
                {
                    retarr[j] *= 10 ;
                    retarr[j] += Character.digit( ca[i], 10 ) ;
                    i++ ;
                    if( i >= len )
                    {
                        break ;
                    }
                }
                j++ ;
            }
        }
        return retarr ;
    }
    public static StringTable localstringtable = null ;
    public static void setStringTable( StringTable stab )
    {
        if( stab != null )
        {
            SJGUtils.localstringtable = stab ;
        }
    }
    public static String localize( String text )
    {
        String retstr = null ;
        retstr = SJGUtils.localstringtable.getValue( text ) ;
        if( retstr == null )
        {
            retstr = text ;
        }
        // _DEBUG( text, retstr ) ;
        return retstr ;
    }
    public static String[] localizeArray( String[] ta )
    {
        return SJGUtils.localizeArray( ta, null ) ;
    }
    public static String[] localizeArray( String[] ta, String prefix )
    {
        { if( (ta) == null ){ return null ; } } ;
        if( ta.length == 0 )
        {
            return null ;
        }
        String[] reta = new String[ ta.length ] ;
        for( int i = 0 ; i < ta.length ; i++ )
        {
            if( prefix == null )
            {
                reta[i] = SJGUtils.localize( ta[i] ) ;
            }
            else
            {
                reta[i] = SJGUtils.localize( prefix + "." + ta[i] ) ;
            }
            // _DEBUG( "[", ta[i], "] = [", reta[i], "]" ) ;
        }
        return reta ;
    }
    public static JLabel newJLabel( String text )
    {
        return SJGUtils.newJLabel( text, false ) ;
    }
    public static JLabel newJLabel( String text, boolean rightjustify )
    {
        JLabel jl = null ;
        String loctext = null ;
        loctext = SJGUtils.localize( text ) ;
        jl = new JLabel( loctext ) ;
        if( rightjustify )
        {
            jl.setHorizontalAlignment( SwingConstants.RIGHT ) ;
        }
        else
        {
            jl.setHorizontalAlignment( SwingConstants.LEFT ) ;
        }
        return jl ;
    }
    public static JRadioButton newJRadioButton( String text )
    {
        JRadioButton jb = null ;
        String loctext = null ;
        loctext = SJGUtils.localize( text ) ;
        jb = new JRadioButton( loctext ) ;
        return jb ;
    }
    public static JCheckBox newJCheckBox( String text )
    {
        JCheckBox jb = null ;
        String loctext = null ;
        loctext = SJGUtils.localize( text ) ;
        jb = new JCheckBox( loctext ) ;
        return jb ;
    }
    public static JButton newJButton( ActionListener listener, String text, String command )
    {
        if( ( text == null ) || ( command == null ) || ( listener == null ) )
        {
            return null ;
        }
        JButton jb = null ;
        String loctext = null ;
        loctext = SJGUtils.localize( text ) ;
        jb = new JButton( loctext ) ;
        jb.addActionListener( listener ) ;
        jb.setActionCommand( command ) ;
        return jb ;
    }
// #define _PANEL_TYPE SJGPanel
    public static JPanel newJPanel()
    {
        JPanel jp = null ;
        jp = new JPanel() ;
        if( jp != null )
        {
            jp.setLayout( new BorderLayout() ) ;
        }
        return jp ;
    }
    public static JPanel newGrid( int rows, int columns )
    {
        JPanel jp = null ;
        jp = new JPanel() ;
        if( jp != null )
        {
            jp.setLayout( new GridLayout( rows, columns ) ) ;
        }
        return jp ;
    }
    public static void addTitledBorder( JComponent c, String text )
    {
        if( c == null )
        {
            return ;
        }
        String loc = null ;
        loc = SJGUtils.localize( text ) ;
        TitledBorder tb = BorderFactory.createTitledBorder( loc ) ;
        // Not all look and feels place the title on the border line
        // notable amoung these is Nimbus.
        // This code forces that behavior.
        tb.setTitlePosition( TitledBorder.TOP ) ;
        c.setBorder( tb ) ;
    }
    public static JPanel[] createColumnPanels( Container jc, int columns )
    {
        // create the given number of panels and return them.
        //
        // Add all of them to the supplied container.
        JPanel[] jpa = new JPanel[columns] ;
        jc.setLayout( new FlowLayout( FlowLayout.LEFT ) ) ;
        for( int i = 0 ; i < jpa.length ; i++ )
        {
            jpa[i] = new JPanel() ;
            jpa[i].setLayout( new BorderLayout() ) ;
            jc.add( jpa[i] ) ;
        }
        return jpa ;
    }
// #define _FRAME_TYPE JFrame
    public static SJGFrame createStandardJFrame( String titlecode )
    {
        SJGFrame frame = null ;
        frame = new SJGFrame() ;
        { if( (frame) == null ){ return null ; } } ;
        frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE ) ;
        frame.setResizable( false ) ;
        // set up the position of it
        Rectangle r = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds() ;
        r.width = r.width / 2 ;
        r.height = r.height / 2 ;
        r.x = r.width / 2 ;
        r.y = r.height / 2 ;
        frame.setBounds( r.x, r.y, r.width, r.height + 50 ) ;
        frame.setTitle( SJGUtils.localize( titlecode ) ) ;
        frame.setLayout( new BorderLayout() ) ;
        return frame ;
    }
    public static JLabel makeJLabel3( Container c, String txt1, String txt2, String txt3 )
    {
        JLabel jl = null ;
        JLabel retlab = null ;
        jl = SJGUtils.newJLabel( txt1 ) ;
        c.add( jl ) ;
        retlab = SJGUtils.newJLabel( txt2, true ) ;
        c.add( retlab ) ;
        if( txt3 != null )
        {
            jl = SJGUtils.newJLabel( txt3 ) ;
            c.add( jl ) ;
        }
        return retlab ;
    }
    public static JComboBox<String> newJComboBox( ActionListener listener, String menustr, String cmd )
    {
        String[] locmenulist = null ;
        String[] menulist = null ;
        if( menustr != null )
        {
            menulist = menustr.split( " " ) ;
            locmenulist = SJGUtils.localizeArray( menulist ) ;
            // prune list of multi-line entries ( i.e. using HTML )
            int i = 0 ;
            int j = 0 ;
            for( i = 0 ; i < locmenulist.length ; i++ )
            {
                j = locmenulist[i].indexOf( "<br>" ) ;
                if( j >= 0 )
                {
                    locmenulist[i] = locmenulist[i].substring( 0, j ) ;
                }
            }
        }
        else
        {
            locmenulist = new String[]{ "" } ;
        }
        // _DEBUGLN() ;
        JComboBox<String> menu = new JComboBox<String>( locmenulist ) ;
        if( menu == null )
        {
            ;
            return null ;
        }
        menu.addActionListener( listener ) ;
        menu.setActionCommand( cmd ) ;
        return menu ;
    }
    public static void forcePreferredSize( Component c )
    {
        // used by component listeners that want to prevent the
        // prevent the component being resized below the
        // preferred size
        Dimension ps = c.getPreferredSize() ;
        Dimension sz = c.getSize() ;
        boolean resize = false ;
        if( ps.width > sz.width )
        {
            sz.width = ps.width ;
            resize = true ;
        }
        if( ps.height > sz.height )
        {
            sz.height = ps.height ;
            resize = true ;
        }
        if( resize )
        {
            c.setSize( sz ) ;
        }
    }
    public static void setLAFNimbus()
    {
        try
        {
            UIManager.setLookAndFeel( "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel" ) ;
        }
        catch( ClassNotFoundException _ex ) { ; }
        catch( InstantiationException _ex ) { ; }
        catch( IllegalAccessException _ex ) { ; }
        catch( UnsupportedLookAndFeelException _ex ) { ; }
    }
    public static void setCard( Container c, String name )
    {
        { if( (c) == null ){ return ; } } ;
        { if( (name) == null ){ return ; } } ;
        LayoutManager ly = c.getLayout() ;
        if( ! ( ly instanceof CardLayout ) )
        {
            return ;
        }
        ((CardLayout)ly).show( c, name ) ;
    }
}
