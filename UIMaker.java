/*
 * UIMaker.java
 *
 * $Id: UIMaker.java,v 1.58 2014/12/19 11:44:41 sjg Exp $
 *
 * (c) Stephen Geary, Aug 2013
 *
 * Make a UI component with localization support
 */
import java.lang.* ;
import java.util.* ;
import java.awt.* ;
import java.awt.event.* ;
import javax.swing.* ;
import javax.swing.border.* ;
import java.lang.reflect.* ;
import java.util.regex.* ;
import java.io.* ;
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
public abstract class UIMaker implements ActionListener, JdirlistCallback
{
    public static final String TOPNAME = "__TOP" ;
    // used by generateIU() and associated methods
    private Hashtable<String,Object> objtab = null ;
    private StringTable loctab = null ;
    private String[] args = null ;
    public UIMaker()
    {
        initUIMaker() ;
    }
    /* The class is abstract and MUST be in the extended class
     *
     * It is intended that the user use this method to initialize
     * and/or record the details of any named components in the method.
     */
    public abstract void initNamedComponents() ;
    public abstract void actionPerformed( ActionEvent ae ) ;
    /* main() would be abstract if Java allowed me to define it that way !
     */
    public static void main( String[] args )
    {
        System.err.println( "You MUST implement a main() method in your subclass of UIMaker" ) ;
    }
    /* and now the code you can use ...
     */
    public void initUIMaker()
    {
        // _DEBUGLN() ;
        this.args = null ;
        this.loctab = new StringTable() ;
        this.objtab = new Hashtable<String,Object>() ;
    }
    public void setArgs( String[] origargs )
    {
        this.args = origargs ;
    }
    public String[] getArgs()
    {
        return this.args ;
    }
    /*
    public void actionPerformed( ActionEvent ae )
    {
        _DEBUGLN() ;
    }
    */
    public Object getNamed( String name )
    {
        Object obj = null ;
        obj = this.objtab.get( name ) ;
        return obj ;
    }
    public String[] getAllNames()
    {
        String[] retsa = null ;
        { if( (this.objtab) == null ){ return null ; } } ;
        retsa = this.objtab.keySet().toArray( new String[0] ) ;
        return retsa ;
    }
    public void initUI( String file )
    {
        this.initUI( file, true ) ;
    }
    public void initUI( String file, boolean exitonclose )
    {
        { if( (file) == null ){ return ; } } ;
        Scanner sc = null ;
        sc = SJGUtils.openScanner( file ) ;
        if( sc == null )
        {
            // file could be in packaged jar associated with the subclass
            // of UIMaker that is (probably) calling this method
            sc = SJGUtils.openScanner( this.getClass().getResourceAsStream( file ) ) ;
        }
        if( sc == null )
        {
            // One last possibility is that the string is the source
            // and we need to create a scanner directly from it
            sc = new Scanner( file ) ;
            // Note that this ALWAYS works unless the string was null
            // so a syntax error will result if there's some other issue
        }
        this.initUI( sc, exitonclose ) ;
    }
    public void initUIFromString( String data )
    {
        { if( (data) == null ){ return ; } } ;
        Scanner sc = new Scanner( data ) ;
        this.initUI( sc, true ) ;
    }
    public void initUIFromString( String data, boolean exitonclose )
    {
        { if( (data) == null ){ return ; } } ;
        Scanner sc = new Scanner( data ) ;
        this.initUI( sc, exitonclose ) ;
    }
    public void initUI( Scanner sc, boolean exitonclose )
    {
        { if( (sc) == null ){ return ; } } ;
        this.readStringTable( sc ) ;
        SJGUtils.setLAFNimbus() ;
        JFrame f = SJGUtils.createStandardJFrame( "loc.title" ) ;
        { if( (f) == null ){ return ; } } ;
        this.objtab.put( UIMaker.TOPNAME, f ) ;
        f.setResizable( true ) ;
        if( exitonclose )
        {
            f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE ) ;
        }
        this.generateUI( sc, f ) ;
        this.initNamedComponents() ;
        // display the new UI
        f.pack() ;
        f.setVisible( true ) ;
        sc.close() ;
    }
    public void readStringTable( Scanner sc )
    {
        SJGUtils.setStringTable( this.loctab ) ;
        String[] p = new String[2] ;
        Pattern pat = sc.delimiter() ;
        sc.useDelimiter( "\n") ;
        while( sc.hasNext() )
        {
            p[0] = SJGUtils.nextText(sc) ;
            if( p[0] == null )
            {
                break ;
            }
            if( p[0].equals( "end-table" ) )
            {
                break ;
            }
            if( ! sc.hasNext() )
            {
                break ;
            }
            p[1] = SJGUtils.nextText(sc) ;
            if( p[1] == null )
            {
                break ;
            }
            this.loctab.add( p ) ;
        }
        sc.useDelimiter( pat ) ;
    }
    // support method
    //
    // note that parameter s is the full text read by scanner
    // and may include a name
    public void addComponentToUI( Container parent, String layoutparam, Component c, String s )
    {
        /*
        _DEBUG( parent ) ;
        _DEBUG( layoutparam ) ;
        _DEBUG( c ) ;
        */
        { if( (parent) == null ){ return ; } } ;
        int i = 0 ;
        String n = null ;
        i = s.indexOf( '.' ) ;
        if( i > 0 )
        {
            n = s.substring( i+1 ) ;
        }
        if( parent instanceof JTabbedPane )
        {
            parent.add( SJGUtils.localize(n), c ) ;
        }
        else if( parent.getLayout() instanceof CardLayout )
        {
            // use name for layout parameter
            if( ( n == null ) || ( n.length() == 0 ) )
            {
                // no name so construct card name from count
                // of existing cards
                n = "card." + parent.getComponentCount() ;
                parent.add( c, n ) ;
                n = null ;
            }
            else
            {
                parent.add( c, n ) ;
            }
        }
        else
        {
            if( layoutparam == null )
            {
                parent.add( c ) ;
            }
            else
            {
                parent.add( c, layoutparam ) ;
            }
        }
        if( ( n != null ) && ( n.length() != 0 ) )
        {
            // record named components in the objtab
            objtab.put( n, c ) ;
        }
    }
    //
    // generateUI must be able to handle recursive calls
    //
    // for debugging
// #define DEBUG_GENERATEUI
    public boolean generateUI( Scanner sc, Container parent )
    {
        boolean retb = true ;
        String s = null ;
        String n = null ;
        int i = 0 ;
        int j = 0 ;
        JPanel p = null ;
        Box b = null ;
        JLabel jl = null ;
        JButton jb = null ;
        JRadioButton jrb = null ;
        JCheckBox jcbx = null ;
        ButtonGroup bg = null ;
        String layoutparam = null ;
// ******************************        
//
// GPP Macros used in the main loop
//
//***********************************
        while( retb && ( s = SJGUtils.nextText(sc) ) != null )
        {
            ;
            n = null ;
            if( s.equals("north" ) ) { layoutparam = BorderLayout.NORTH ; ; continue ; }
            if( s.equals("south" ) ) { layoutparam = BorderLayout.SOUTH ; ; continue ; }
            if( s.equals("east" ) ) { layoutparam = BorderLayout.EAST ; ; continue ; }
            if( s.equals("west" ) ) { layoutparam = BorderLayout.WEST ; ; continue ; }
            if( s.equals("center" ) ) { layoutparam = BorderLayout.CENTER ; ; continue ; }
            if( s.equals("}") )
            {
                // end of subsection
                break ;
            }
            if( s.startsWith( "box-x" ) )
            {
                b = new Box( BoxLayout.X_AXIS ) ;
                this.addComponentToUI( parent, layoutparam, b, s ) ;
                { s = SJGUtils.nextText(sc) ; if( s.equals("{") ) { retb = this.generateUI( sc, b ) ; if( ! retb ) { retb = false ; continue ; } } else { ; retb = false ; continue ; } } ;
                continue ;
            }
            if( s.startsWith( "box-y" ) )
            {
                b = new Box( BoxLayout.Y_AXIS ) ;
                this.addComponentToUI( parent, layoutparam, b, s ) ;
                { s = SJGUtils.nextText(sc) ; if( s.equals("{") ) { retb = this.generateUI( sc, b ) ; if( ! retb ) { retb = false ; continue ; } } else { ; retb = false ; continue ; } } ;
                continue ;
            }
            if( s.startsWith( "jpanel" ) )
            {
                p = SJGUtils.newJPanel() ;
                this.addComponentToUI( parent, layoutparam, p, s ) ;
                { s = SJGUtils.nextText(sc) ; if( s.equals("{") ) { retb = this.generateUI( sc, p ) ; if( ! retb ) { retb = false ; continue ; } } else { ; retb = false ; continue ; } } ;
                continue ;
            }
            if( s.startsWith( "jcard" ) )
            {
                p = SJGUtils.newJPanel() ;
                p.setLayout( new CardLayout() ) ;
                this.addComponentToUI( parent, layoutparam, p, s ) ;
                { s = SJGUtils.nextText(sc) ; if( s.equals("{") ) { retb = this.generateUI( sc, p ) ; if( ! retb ) { retb = false ; continue ; } } else { ; retb = false ; continue ; } } ;
                continue ;
            }
            if( s.startsWith( "jtabs" ) )
            {
                JTabbedPane jtp = new JTabbedPane() ;
                this.addComponentToUI( parent, layoutparam, jtp, s ) ;
                { s = SJGUtils.nextText(sc) ; if( s.equals("{") ) { retb = this.generateUI( sc, jtp ) ; if( ! retb ) { retb = false ; continue ; } } else { ; retb = false ; continue ; } } ;
                continue ;
            }
            if( s.startsWith( "jscrollpane" ) )
            {
                p = SJGUtils.newJPanel() ;
                JScrollPane jsp = new JScrollPane( p ) ;
                this.addComponentToUI( parent, layoutparam, jsp, s ) ;
                { s = SJGUtils.nextText(sc) ; if( s.equals("{") ) { retb = this.generateUI( sc, p ) ; if( ! retb ) { retb = false ; continue ; } } else { ; retb = false ; continue ; } } ;
                continue ;
            }
            if( s.startsWith( "-split", 1 ) )
            {
                JSplitPane jsp = null ;
                if( s.startsWith( "v-split" ) )
                {
                    jsp = new JSplitPane( JSplitPane.VERTICAL_SPLIT ) ;
                }
                if( s.startsWith( "h-split" ) )
                {
                    jsp = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT ) ;
                }
                if( jsp == null )
                    { ; retb = false ; continue ; }
                this.addComponentToUI( parent, layoutparam, jsp, s ) ;
                { s = SJGUtils.nextText(sc) ; if( s.equals("{") ) { retb = this.generateUI( sc, jsp ) ; if( ! retb ) { retb = false ; continue ; } } else { ; retb = false ; continue ; } } ;
                continue ;
            }
            if( s.startsWith( "jgrid" ) )
            {
                int rows = 0 ;
                int cols = 0 ;
                String m = null ;
                m = SJGUtils.nextText(sc) ;
                if( m == null )
                    { ; retb = false ; continue ; } ;
                rows = Integer.parseInt( m ) ;
                m = SJGUtils.nextText(sc) ;
                if( m == null )
                    { ; retb = false ; continue ; } ;
                cols = Integer.parseInt( m ) ;
                ;
                p = SJGUtils.newGrid( rows, cols ) ;
                if( p == null )
                    { ; retb = false ; continue ; }
                this.addComponentToUI( parent, layoutparam, p, s ) ;
                { s = SJGUtils.nextText(sc) ; if( s.equals("{") ) { retb = this.generateUI( sc, p ) ; if( ! retb ) { retb = false ; continue ; } } else { ; retb = false ; continue ; } } ;
                continue ;
            }
            if( s.equals( "titleborder" ) )
            {
                String t = SJGUtils.nextLine(sc) ;
                if( ( t == null ) || ( t.length() == 0 ) )
                {
                    t = "Error : missing title" ;
                }
                if( parent instanceof JComponent )
                {
                    ;
                    SJGUtils.addTitledBorder( (JComponent)parent, t ) ;
                }
                continue ;
            }
            if( s.equals("size") )
            {
                { int w = 0 ; int h = 0 ; String m = null ; m = SJGUtils.nextText(sc) ; if( m == null ) { ; retb = false ; continue ; } w = Integer.parseInt( m ) ; m = SJGUtils.nextText(sc) ; if( m == null ) { ; retb = false ; continue ; } h = Integer.parseInt( m ) ; parent.setPreferredSize( new Dimension( w, h ) ) ; }
                continue ;
            }
            if( s.equals("maxsize") )
            {
                { int w = 0 ; int h = 0 ; String m = null ; m = SJGUtils.nextText(sc) ; if( m == null ) { ; retb = false ; continue ; } w = Integer.parseInt( m ) ; m = SJGUtils.nextText(sc) ; if( m == null ) { ; retb = false ; continue ; } h = Integer.parseInt( m ) ; parent.setMaximumSize( new Dimension( w, h ) ) ; }
                continue ;
            }
            if( s.equals("minsize") )
            {
                { int w = 0 ; int h = 0 ; String m = null ; m = SJGUtils.nextText(sc) ; if( m == null ) { ; retb = false ; continue ; } w = Integer.parseInt( m ) ; m = SJGUtils.nextText(sc) ; if( m == null ) { ; retb = false ; continue ; } h = Integer.parseInt( m ) ; parent.setMinimumSize( new Dimension( w, h ) ) ; }
                continue ;
            }
            if( s.startsWith("jlabel") )
            {
                String t = SJGUtils.nextText(sc) ;
                if( t == null )
                    { ; retb = false ; continue ; }
                jl = SJGUtils.newJLabel( t ) ;
                this.addComponentToUI( parent, layoutparam, jl, s ) ;
                continue ;
            }
            if( s.startsWith("jbutton") )
            {
                String t = SJGUtils.nextText(sc) ;
                if( t == null )
                    { ; retb = false ; continue ; }
                String cmd = SJGUtils.nextText(sc) ;
                if( cmd == null )
                    { ; retb = false ; continue ; }
                jb = SJGUtils.newJButton( this, t, cmd ) ;
                this.addComponentToUI( parent, layoutparam, jb, s ) ;
                continue ;
            }
            if( s.startsWith( "jradio" ) )
            {
                String t = SJGUtils.nextText(sc) ;
                if( t == null )
                    { ; retb = false ; continue ; }
                jrb = SJGUtils.newJRadioButton( t ) ;
                if( bg != null )
                {
                    bg.add( jrb ) ;
                }
                this.addComponentToUI( parent, layoutparam, jrb, s ) ;
                continue ;
            }
            if( s.startsWith( "jcheckbox" ) )
            {
                String t = SJGUtils.nextText(sc) ;
                if( t == null )
                    { ; retb = false ; continue ; }
                jcbx = SJGUtils.newJCheckBox( t ) ;
                if( bg != null )
                {
                    bg.add( jcbx ) ;
                }
                this.addComponentToUI( parent, layoutparam, jcbx, s ) ;
                continue ;
            }
            if( ( s.startsWith( "bg-start") ) || ( s.startsWith( "buttongroup-start" ) ) )
            {
                bg = new ButtonGroup() ;
                // button groups can be named as users may
                // want access to the button group and it's
                // methods
                n = null ;
                i = s.indexOf( '.' ) ;
                if( i >= 8 )
                {
                    n = s.substring( i+1 ) ;
                    this.objtab.put( n, bg ) ;
                }
                continue ;
            }
            if( ( s.equals( "bg-end" ) ) || ( s.equals( "buttongroup-end" ) ) )
            {
                bg = null ;
                continue ;
            }
            if( s.startsWith( "jcombobox" ) )
            {
                String cmd = SJGUtils.nextText(sc) ;
                if( cmd == null )
                    { ; retb = false ; continue ; }
                String t = SJGUtils.nextLine(sc) ;
                if( t == null )
                    { ; retb = false ; continue ; }
                JComboBox<String> jcb = SJGUtils.newJComboBox( this, t, cmd ) ;
                this.addComponentToUI( parent, layoutparam, jcb, s ) ;
                continue ;
            }
            if( s.startsWith( "jimage" ) )
            {
                JImage ji = new JImage() ;
                this.addComponentToUI( parent, layoutparam, ji, s ) ;
                continue ;
            }
            if( s.startsWith( "jdirlist" ) )
            {
                String t = SJGUtils.nextLine(sc) ;
                if( t == null )
                    { ; retb = false ; continue ; }
                Jdirlist jdl = new Jdirlist( this ) ;
                this.addComponentToUI( parent, layoutparam, jdl, s ) ;
                continue ;
            }
            if( s.equals( "flow" ) )
            {
                parent.setLayout( new FlowLayout() ) ;
                continue ;
            }
            if( s.equals( "menubar" ) )
            {
                JMenuBar mbar = null ;
                mbar = generateJMenuBar( sc ) ;
                if( mbar == null )
                {
                    retb = false ;
                }
                else
                {
                    Class<?> cls = parent.getClass() ;
                    Class<?>[] classes = new Class<?>[1] ;
                    classes[0] = mbar.getClass() ;
                    Method m = null ;
                    try
                    {
                        m = cls.getMethod( "setJMenuBar", classes ) ;
                    }
                    catch( NoSuchMethodException nsme )
                    {
                        m = null ;
                    }
                    if( m == null )
                    {
                        continue ;
                    }
                    Object[] objs = new Object[1] ;
                    objs[0] = mbar ;
                    try
                    {
                        m.invoke( parent, objs ) ;
                    }
                    catch( IllegalAccessException iae )
                    {
                    }
                    catch( InvocationTargetException ite )
                    {
                    }
                }
                continue ;
            }
        }
        return true ;
    }
    public JMenuBar generateJMenuBar( Scanner sc )
    {
        JMenuBar mbar = new JMenuBar() ;
        String s = null ;
        s = SJGUtils.nextText(sc) ;
        if( ! s.equals("{") )
        {
            ;
            return null ;
        }
        JMenu mnu = null ;
        while( ( s = SJGUtils.nextText(sc) ) != null )
        {
            mnu = null ;
            if( s.equals( "}" ) )
            {
                break ;
            }
            // read text for each menu
            mnu = new JMenu( SJGUtils.localize( s ) ) ;
            if( mnu == null )
            {
                // an error
                ;
                return null ;
            }
            s = SJGUtils.nextText(sc) ;
            if( ! s.equals( "{" ) )
            {
                // an error - brace required for start of menu entry list
                ;
                return null ;
            }
            JMenuItem mi = null ;
            String text = null ;
            String act = null ;
            while( ( s = SJGUtils.nextText(sc) ) != null )
            {
                if( s.equals( "}" ) )
                {
                    break ;
                }
                act = SJGUtils.nextText(sc) ;
                if( act == null )
                {
                    // an error
                    ;
                    return null ;
                }
                mi = new JMenuItem( SJGUtils.localize( s ) ) ;
                if( mi == null )
                {
                    // an error
                    ;
                    return null ;
                }
                mi.setActionCommand( act ) ;
                mi.addActionListener( this ) ;
                mnu.add( mi ) ;
            }
            if( mnu == null )
            {
                // an error
                ;
                return null ;
            }
            mbar.add( mnu ) ;
        }
        if( s == null )
        {
            // indicates an early exit ( not on a close brace )
            // this is an error
            return null ;
        }
        return mbar ;
    }
    public void fileClicked( File f )
    {
        ;
    }
}
