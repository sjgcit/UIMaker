/*
 * Jdirlist.java
 *
 * $Id: Jdirlist.java,v 1.39 2014/12/18 13:22:22 sjg Exp $
 *
 * (c) Stephen Geary, Aug 2013
 *
 * Directory listing component
 */
import java.lang.* ;
import java.util.* ;
import java.io.* ;
import java.awt.* ;
import java.awt.event.* ;
import javax.swing.* ;
import java.lang.reflect.* ;
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
public class Jdirlist extends JScrollPane implements FilenameFilter, MouseListener
{
    private final static long serialVersionUID = -1L ;
    public static Icon iconDir = UIManager.getIcon( "Tree.openIcon" ) ;
    public static Icon iconFile = UIManager.getIcon( "Tree.leafIcon" ) ;
    public static Icon iconParent = UIManager.getIcon( "FileChooser.upFolderIcon" ) ;
    private File currentdir = null ;
    private String homedir = null ;
    private ArrayList<String> acceptextensions = null ;
    private File selectedfile = null ;
    private int numdirs = 0 ;
    private JLabel selectedlabel = null ;
    private int selectedindex = 0 ;
    private JdirlistCallback onclickobject = null ;
    private LinkedList<String> dirhistory = null ;
    private JPanel jpanel = null ;
    private Component jbox = null ;
    private String[] listdirs = null ;
    private String[] listfiles = null ;
    private int vgap = 20 ;
    public Jdirlist( JdirlistCallback callbackobj )
    {
        super( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
               ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED ) ;
        initJdirlist( callbackobj ) ;
    }
    public void initJdirlist( JdirlistCallback callbackobj )
    {
        this.setBackground( Color.WHITE ) ;
        this.jbox = Box.createVerticalStrut( 20 ) ;
        this.setViewportView( this.jbox ) ;
        ( this.getVerticalScrollBar() ).setUnitIncrement( 20 ) ;
        this.jbox.addMouseListener( this ) ;
        this.setOnClickHandler( callbackobj ) ;
        this.acceptextensions = new ArrayList<String>() ;
        this.homedir = System.getenv( "HOME" ) ;
        this.dirhistory = new LinkedList<String>() ;
        this.currentdir = new File( "." ) ;
        this.repopulate() ;
    }
    public void repopulate()
    {
        if( homedir == null )
        {
            return ;
        }
        // clear the panel first
        // this.jpanel.removeAll() ;
        // now fill the panel
        this.currentdir = new File( this.homedir ) ;
        // add a List to the component
        // first get the files
        this.listOnlyFiles() ;
        this.listfiles = this.currentdir.list(this) ;
        Arrays.sort( this.listfiles ) ;
        // now get the dirs
        this.listOnlyDirs() ;
        this.listdirs = this.currentdir.list(this) ;
        Arrays.sort( this.listdirs ) ;
        // now generate the UI
        // set the size ( height ) of the dummy box
        // which is used to make the scrollbars track the size
        Dimension d = this.getSize() ;
        d.height = ( this.listfiles.length + this.listdirs.length + 1 ) * this.vgap ;
        this.jbox.setPreferredSize( d ) ;
        this.jbox.setBackground( Color.WHITE ) ;
        this.selectedindex = 0 ;
        this.getViewport().revalidate() ;
        this.repaint() ;
    }
    // for the interface FilenameFilter
    public void acceptExtension( String ext )
    {
        if( ext == null )
            return ;
        if( ext.length() == 0 )
            return ;
        this.acceptextensions.add( ext ) ;
    }
    // for FilenameFilter
    // if true  then return ONLY files
    // if false then return ONLY dirs
    private boolean acceptfiles = true ;
    private void listOnlyFiles()
    {
        this.acceptfiles = true ;
    }
    private void listOnlyDirs()
    {
        this.acceptfiles = false ;
    }
    public boolean accept( File f, String name )
    {
        // probably never happens, but just in case
        if( name == null )
            return false ;
        // check for hidden files
        File fn = new File( f, name ) ;
        if( fn.isHidden() )
            return false ;
        if( fn.isDirectory() )
        {
            if( this.acceptfiles )
            {
                // it's a dir and we only want files
                return false ;
            }
            else
            {
                // it's a dir and we want dirs
                // _DEBUG( "Accepting ", name, " as dir" ) ;
                return true ;
            }
        }
        else
        {
            if( !this.acceptfiles )
            {
                // it's a file and we only want dirs
                return false ;
            }
        }
        if( ( this.acceptextensions == null ) || ( this.acceptextensions.size() == 0 ) )
        {
            return true ;
        }
        int i = 0 ;
        for( i = 0 ; i < this.acceptextensions.size() ; i++ )
        {
            if( name.endsWith( this.acceptextensions.get(i) ) )
            {
                return true ;
            }
        }
        return false ;
    }
    // for callback used by mouseListener
    public void setOnClickHandler( JdirlistCallback obj )
    {
        if( obj == null )
        {
            return ;
        }
        this.onclickobject = obj ;
    }
    public void invokeOnClick()
    {
        // called when a file is clicked to open it
        if( this.onclickobject == null )
        {
            ;
            return ;
        }
        onclickobject.fileClicked( this.selectedfile ) ;
    }
    public File getSelectedFile()
    {
        return this.selectedfile ;
    }
    // for interface MouseListener
    public void mouseClicked( MouseEvent me )
    {
        // find out where in the Box was clicked
        Point p = me.getPoint() ;
        // calculate which element of the listing is involved
        int i = p.y / this.vgap ;
        if( i >= this.listdirs.length + this.listfiles.length + 1 )
        {
            return ;
        }
        // report that
        if( i == 0 )
        {
            // _DEBUG( "Parent clicked" ) ;
            File fn = new File( this.currentdir, ".." ) ;
            this.changeDir( fn ) ;
            return ;
        }
        if( i < this.listdirs.length+1 )
        {
            // _DEBUG( this.listdirs[i-1], " clicked" ) ;
            File fn = new File( this.currentdir, this.listdirs[i-1] ) ;
            // _DEBUG( fn ) ;
            this.changeDir( fn ) ;
            return ;
        }
        else
        {
            // _DEBUG( this.listfiles[i-this.listdirs.length-1], " clicked" ) ;
            if( i == this.selectedindex )
            {
                return ;
            }
            File fn = new File( this.currentdir, this.listfiles[i-this.listdirs.length-1] ) ;
            // _DEBUG( fn ) ;
            this.selectedfile = fn ;
            this.selectedindex = i ;
            this.repaint() ;
            this.invokeOnClick() ;
            return ;
        }
    }
    public void selectItem( int i )
    {
        String s = "" ;
        if( i == 0 )
        {
            s = ".." ;
        }
        else if( i < this.listdirs.length+1 )
        {
            s = this.listdirs[i-1] ;
        }
        else
        {
            s = this.listfiles[i-this.listdirs.length-1] ;
        }
        File fn = new File( this.currentdir, s ) ;
        // _DEBUG( fn ) ;
        if( fn.isDirectory() )
        {
            // change directory if possible
            this.changeDir( fn ) ;
        }
        else
        {
            // open the file via callback
            this.selectedfile = fn ;
            this.invokeOnClick() ;
            this.repaint() ;
        }
    }
    public void changeDir( String newdir )
    {
        File f = new File( newdir ) ;
        if( f != null )
        {
            if( f.exists() && f.isDirectory() )
            {
                this.changeDir( f ) ;
            }
        }
    }
    public void changeDir( File fn )
    {
        this.dirhistory.push( this.homedir ) ;
        try
        {
            this.homedir = fn.getCanonicalPath() ;
        }
        catch( IOException ioe )
        {
            ;
        }
        if( this.dirhistory.size() > 10 )
        {
            // drop oldest item
            this.dirhistory.removeLast() ;
        }
        this.repopulate() ;
    }
    public void mouseEntered( MouseEvent me )
    {
    }
    public void mouseExited( MouseEvent me )
    {
    }
    public void mousePressed( MouseEvent me )
    {
    }
    public void mouseReleased( MouseEvent me )
    {
    }
    // useful methods for code using keys to traverse list
    //
    // They ignore directories.
    public void gotoPrev()
    {
        int i = this.selectedindex ;
        if( i < this.listdirs.length + 1 )
        {
            i = 0 ;
        }
        else
        {
            i-- ;
            this.selectedindex = i ;
            if( i > this.listdirs.length )
            {
                this.selectItem( i ) ;
            }
        }
    }
    public void gotoNext()
    {
        int i = this.selectedindex ;
        int n = this.listdirs.length + this.listfiles.length ;
        if( i > n )
        {
            i = n ;
        }
        else
        {
            i++ ;
            if( i > n )
            {
                i = n ;
            }
            this.selectedindex = i ;
            if( i > this.listdirs.length )
            {
                this.selectItem( i ) ;
            }
        }
    }
    // paint methods
    public void paintChildren( Graphics g )
    {
        super.paintChildren( g ) ;
        // paint text for each entry
        FontMetrics fm = g.getFontMetrics() ;
        this.vgap = ( 5 * ( fm.getMaxDescent() + fm.getMaxAscent() ) ) / 4 ;
        Rectangle r = null ;
        r = this.getViewport().getBounds() ;
        g.setColor( Color.WHITE ) ;
        g.clipRect( r.x, r.y, r.width, r.height ) ;
        g.fillRect( r.x, r.y, r.width, r.height ) ;
        Insets ins = this.getViewport().getInsets() ;
        int x = r.x + ins.left ;
        r = this.jbox.getBounds() ;
        int h = this.vgap + r.y + ins.top ;
        g.setColor( Color.BLACK ) ;
        if( g instanceof Graphics2D )
        {
            ( ( Graphics2D )g ).setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,
                                                RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB ) ;
        }
        // list parent
        int iconh = Jdirlist.iconParent.getIconHeight() ;
        int iconw = Jdirlist.iconParent.getIconWidth() ;
        Jdirlist.iconParent.paintIcon( this, g, x, h - iconh ) ;
        g.drawString( ".." , x + iconw , h ) ;
        h += this.vgap ;
        // list directories
        iconh = Jdirlist.iconDir.getIconHeight() ;
        iconw = Jdirlist.iconDir.getIconWidth() ;
        int i = 0 ;
        for( i = 0 ; i < this.listdirs.length ; i++ )
        {
            Jdirlist.iconDir.paintIcon( this, g, x, h - iconh ) ;
            g.drawString( this.listdirs[i] , x + iconw , h ) ;
            h += this.vgap ;
        }
        // list files
        iconh = Jdirlist.iconFile.getIconHeight() ;
        iconw = Jdirlist.iconFile.getIconWidth() ;
        for( i = 0 ; i < this.listfiles.length ; i++ )
        {
            Jdirlist.iconFile.paintIcon( this, g, x, h - iconh ) ;
            if( i == ( this.selectedindex - this.listdirs.length - 1 ) )
            {
                // _DEBUG( "Selected index : ", i, " :: ", this.listfiles[i] ) ;
                g.setColor( Color.BLUE ) ;
                g.fillRect( r.x + iconw, h - ( ( this.vgap * 2 ) / 3 ), r.width - iconw , this.vgap ) ;
                g.setColor( Color.WHITE ) ;
            }
            g.drawString( this.listfiles[i] , x + iconw , h ) ;
            if( i == ( this.selectedindex - this.listdirs.length - 1 ) )
            {
                g.setColor( Color.BLACK ) ;
            }
            h += this.vgap ;
        }
    }
}
