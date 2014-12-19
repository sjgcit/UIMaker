/*
 * JdirlistCallback.java
 *
 * $Id: JdirlistCallback.java,v 1.1 2013/10/08 03:18:35 sjg Exp $
 *
 * (c) Stephen Geary, Oct 2013
 *
 * Callback interface for Jdirlist component
 */
import java.lang.* ;
import java.io.* ;
public interface JdirlistCallback
{
    void fileClicked( File f ) ;
}
