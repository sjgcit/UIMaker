<!--
$Id: README.md,v 1.1 2014/12/19 11:57:16 sjg Exp $
-->

UIMaker - A simple to use Java UI Library
=========================================

What is it ?
============

UIMaker is a library that supplies a support framework for building application UIs. It's really quite simple to implement an application using it and the UI is defined is a manner that allows easy localisation and is simple to modify. Unlike every other UI library it does **not** rely on a Layout Manager or require you to learn a lot of complex functionality. It's designed to make it easy to build a UI and easy to maintain it.

Which all sounds too good to be true, right ? So let's look at a complex UI and what's it's definition looks like bit by bit :

Comments in the file use a hash at the start of a line.

~~~~ {.shell}
# panel.in
#
# Test application UImaker file
~~~~

Text items have an identifier, so we can easily connect them into the UI and modify them without worrying about the structure of the UI ( which is after all these definitions. Each text entry has an identifiewr on one line followed by the text. The text can often by HTML, as Java supports HTML for text in buttons and labels. So some examples from a full file :

~~~~ {.text}
loc.title
UIM Test Application

Card-1
Show Card One

Card-2
Show Card Two
alpha
Open UIM Script

beta
<html>Beta ( &beta; )

three
<html><font color="blue">Three</font>

four
<html><font color="#DB7093">Four</font>

rad-1
One - Not in button group

rad-2
Two - First in button group

rad-3
Three - Second in button group

check-1
One - Not in button group

check-2
Two - First in button group

tab-1
Tab One

tab-2
Tab Two
~~~~

The labels are your choice, so don't think you're stuck with those type of label names. You could call them anything.

Here's an example of an item being referred to by it's path *inside* the application's final jar :

~~~~ {.html}
htmlimage
<html><img src='jar:file:uimtest.jar!/resources/chalet.jpg'>
~~~~

Labels for everything are defined in the same way. These are going to be used to build the menubar.

~~~~ {.text}
m-file
File

m-open
Open File

m-image
Load an image
~~~~

And we do need to tell UIMaker that the stream of lable data is finished before defining the rest of the UI :

~~~~ {.text}
end-table
~~~~

We'd like to tell it what title to use for the window :

~~~~ {.text}
titledborder UIM-test-rig
~~~~

That's the text stuff done. Quite painless and quite easy to localize.

Now the menubar :

~~~~ {.text}
menubar
{
    m-file
    {
        m-image act-load-image
        m-close m-act-close
        m-exit m-act-exit 
    }
    
    m-wibble
    {
        m-wibble-1 m-act-1
        m-wibble-2 m-act-2
        "Just a wibble" m-act-3
        "Just \"wibble\" me" m-act-4
    }
    
    m-help
    {
        m-help m-act-help
        m-about m-act-about
    }
}
~~~~

We start off defining a menubar with the magic keyword "menubar" and we use braces ( as we do throughout the UI definition ) to structure the UI in hierarchies.

Each subment has just a reference to it's text id label.

Each menu entry proper has a label id *and* a simple command string that your subclass of UIMaker will implement and interpret ( dcide what to do about ). Clikc a menu item and the command is passed to your application to be acted on. Can't make that simpler.

Now the UI and compenents :

Everything is based on the very friendly notion of panels having a north, south, east, west and center component, which can be yet another complex panel. UIMaker does some behind the scenes things to make that all work sensibly, but you don;t have to worry about that. It's all designed to be intuitive to use. You don't need to define every north, south, east, west or center element - just the ones you want.

The border elements cling to the appropriate edge of their parent. The center one is flexible.

Note one thing : minimum sizes are respected as far as is possible. It's all done for you. This is fire and forget with no need to worry about resizing or that stuff - let the computer worry about that.

~~~~ {.text}
north

    jpanel
    {
        west
        
            jgrid 3 0
            {
                jbutton Card-1 card-1
                jbutton Card-2 card-2
                jbutton Card-3 card-3
                jbutton Card-4 card-4
                jbutton Card-5 card-5
                jbutton Card-6 card-6
                jbutton Card-7 card-7
            }
    }
~~~~

That's a section of the UI. We're building the top level north panel. ( 'north' ) and then we insert a panel in that to contain a 'west' element. We'd do that to make sure the components in that subpanel cling to the correct borders. Inside that panel we add a grid containing buttons. As far as it can it will respect those grid dimensions ( 3 x anything ( = 0 ) ). Each button has an action command. These you just add here. YOu can of course reuse action codes to make two things cause the same effect.

~~~~ {.text}
center

    jcard.cardpanel
    {
        size 500 500

        jpanel.card-1
        {
            east
~~~~

Here we see a *named* subpanel or unit being created by simply adding a '.' and name to it's component.

You also see a default size being declared. Again as far as possible that will be respected by UIMaker at runtime.

~~~~ {.text}
        jpanel.card-3
        {
            north
            
            jgrid 0 1
            {
                jradio rad-1
            
                bg-start.radiogroup
            
                jradio rad-2
                jradio rad-3
                jradio rad-4
                
                bg-end
                
                jradio rad-5
            }
        }
~~~~

Here we have some radio buttons being defined. UIMaker lets you group radio buttons ( and checkboxes ) so that they obey mutual exclusivity rules. Any radio button defined in the group acts independently of the elements outside the group, so it's easy to create buttons that turn off some options, but not everything.

Note that you can add an action to the radion buttons so if they're clicked you get told. This is optional so you can build more complex interactions yourself.

Here's a similar checkbox example :

~~~~ {.text}
        jpanel.card-4
        {
            north
            
            jgrid 0 1
            {
                jcheckbox check-1
            
                bg-start.checkboxgroup
            
                jcheckbox check-2
                jcheckbox check-3
                jcheckbox check-4
                
                bg-end
                
                jcheckbox check-5
            }
        }
~~~~

Want an image ( initialize the image at runtime ) ?

~~~~ {.text}
                jimage.img
~~~~

A directory listing :

~~~~ {.text}
            jdirlist jdlclick
~~~~

Tabbed sub-panels just use the jtabs keyword and give it a label. Each sub panel in the jtabs { } section will be treated as a seperate tab.

~~~~ {.text}
        jtabs.card-7
        {
            jpanel.tab-1
            {
                north
                
                jgrid 0 1
                {
                    jbutton "Tab Button 1" act-13
                    jbutton "Tab Button 2" act-14
                    jbutton "Tab Button 3" act-15
                }
            }

            jpanel.tab-2
            {
                north
                
                jgrid 0 1
                {
                    jbutton "Tab Button 4" act-16
                    jbutton "Tab Button 5" act-17
                    jbutton "Tab Button 6" act-18
                }
            }
~~~~

Here one of the tabs is simply holfing an image :

~~~~ {.text}
            jlabel.tab-4 htmlimage
~~~~

This is a pretty simple way to build a complex UI. There's a needlessly complex example in the repository.

How do you interact with all this ?
-----------------------------------

There is one required method ( UIMaker is an abstract class and your MUST subclass it and define the required method ).

~~~~ {.Java}
public abstract void initNamedComponents() ;

public abstract void actionPerformed( ActionEvent ae ) ;

public static void main( String[] args ) ;
    
~~~~

Actually it's not impossible to go without the main routine, but it makes little sense in practice. I'd have made it *abstract* and forced a user definition in a subclass except that the Java language forbids static methids from being abstracted ( a rather idiotic limitation, as subclassing does not require an instance, but out of my hands ). The default main() method just prints an error message telling you to subclass and implement main)\_ yourself !

You would start main by simply creating an instance of your UIMaker subclass. For example :

~~~~ {.Java}
        uimtest.uim = new uimtest() ;
        
        uimtest.uim.initUI( "sjgpanel.in", true ) ;
~~~~

To get a component by name do this ( and note the need to cast to whatever type you expect ! ) :

~~~~ {.Java}
        this.deck = (JPanel)( this.getNamed("cardpanel") ) ;
~~~~

The *getNamed()* method is defined in UIMaker.java and just fetches the named component for you. What you do with it is up to you.
