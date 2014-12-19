/*
 * StringTable.java
 *
 * $Id: StringTable.java,v 1.2 2013/08/04 13:37:52 sjg Exp $
 *
 * (c) Stephen Geary, Jul 2013
 *
 * Generic StringTable class to handle a table of
 * strings addressed by String
 *
 * Want something we can change the implementation of
 * without affecting external code
 */
import java.lang.* ;
import java.util.* ;
import java.io.* ;
public class StringTable
{
    Vector<String[]> pairs = null ;
    public StringTable()
    {
        pairs = new Vector<String[]>() ;
    }
    public void add( String ky, String val )
    {
        String[] s = { ky, val } ;
        this.pairs.add( s ) ;
    }
    public void add( String[] p )
    {
        int i ;
        int k = p.length - ( p.length & 1 ) ;
        for( i = 0 ; i < k ; i += 2 )
        {
            this.add( p[i], p[i+1] ) ;
        }
    }
    public void loadFromFile( String path )
    {
        Scanner sc = null ;
        sc = SJGUtils.openScanner( path ) ;
        if( sc == null )
            return ;
        String[] p = new String[2] ;
        sc.useDelimiter( "\n") ;
        while( sc.hasNext() )
        {
            p[0] = SJGUtils.nextText(sc) ;
            if( p[0] == null )
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
            this.add( p ) ;
        }
        if( sc != null )
        {
            sc.close() ;
        }
    }
    public String getValue( String ky )
    {
        for( String[] p : this.pairs )
        {
            if( p[0].equals( ky ) )
            {
                return p[1] ;
            }
        }
        return null ;
    }
    public String getKey( String val )
    {
        for( String[] p : this.pairs )
        {
            if( p[1].equals( val ) )
            {
                return p[0] ;
            }
        }
        return null ;
    }
    public void remapArray( String[] sa )
    {
        int i = 0 ;
        String v = null ;
        if( sa == null )
            return ;
        for( i = 0 ; i < sa.length ; i++ )
        {
            v = this.getValue( sa[i] ) ;
            if( v != null )
            {
                sa[i] = v ;
            }
        }
    }
    public String toString()
    {
        String s = "StringTable{" ;
        for( String[] p : this.pairs )
        {
            s += "(\"" + p[0] + "\",\"" + p[1] + "\")" ;
        }
        s += "}" ;
        return s ;
    }
}
