/**
 *   JGAAP -- Java Graphical Authorship Attribution Program
 *   Copyright (C) 2009 Patrick Juola
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation under version 3 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 **/
/**
 * 
 */
package com.jgaap.gui.dnd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.awt.GridBagLayout;
import java.awt.Color;
import java.awt.dnd.*;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.jgaap.generics.Document;
import com.jgaap.generics.Document.DocType;
import com.jgaap.gui.stepPanels.CanonicizeStepPanel;

/**
 * This class handles a set of embedded CanonicizerLabel instances.
 * These instances can be moved or copied out of the panel. The panel
 * is also a possible drop location for CanonicizerPacket instances
 * carrying new CanonicizerLabels to place on this panel.
 * 
 * A CanonicizerLabelPanel needs to know where it is in the GUI (Location)
 * and what ActionGroup it affects (single document, set of documents,
 * all documents).
 * 
 * @author Chuck Liddell
 *
 */
public class CanonicizerLabelPanel extends JPanel implements CanonicizerLabelUnpackager {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Used to indicate what set of documents are affected
	 * by packets generated by this packager.
	 */
	public enum ActionGroup { NONE, SINGLE, DOCTYPE, ALL };	
	
	/**
	 * Reference to the DnDManager so we can pass drag and drop
	 * event info to it for processing.
	 */
	private final DnDManager dndManager;
		
	/**
	 * Tracks what set of documents actions will be applied to.
	 */
	private final ActionGroup actionGroup;
	
	/**
	 * Reference to a Document.DocType, which is needed if this
	 * panel is used in the sidebar table.
	 */
	private DocType docType = null;
		
	/**
	 * Reference to a single Document, which is needed if this
	 * panel is used in the main table.
	 */
	private Document doc = null;	
		
	/**
	 * Stores references to any CanonicizerLabel instances
	 * that are embedded in this panel.
	 */
	private ArrayList<CanonicizerLabel> canonLabels;
		
	/**
	 * Simple constructor that accepts a Location where
	 * this panel will be displayed in the GUI.
	 * 
	 * @param dndManager
	 * 		DnDManager instance that we will pass drag / drop results to
	 * @param actionGroup
	 * 		The set of documents this panel represents
	 */
	public CanonicizerLabelPanel( DnDManager dndManager,
								  ActionGroup actionGroup ){
		this( dndManager, actionGroup, false );
	}
	
	/**
	 * Constructor that accepts Location and ActionGroup to define
	 * the attributes of this panel, and also takes a transferhandler
	 * to handle drop events on this panel.
	 * 
	 * @param dndManager
	 * 		DnDManager instance that we will pass drag / drop results to
	 * @param actionGroup
	 * 		The set of documents this panel represents
	 * @param dropTarget
	 * 		Indicates whether this panel can have labels dropped on it
	 */
	public CanonicizerLabelPanel( DnDManager dndManager,
								  ActionGroup actionGroup, boolean dropTarget ){
		this.dndManager = dndManager;
		this.actionGroup = actionGroup;
		if( dropTarget ) 
			this.setDropTarget( new DropTarget(this, new DropListener(this)) );
		this.setLayout( new GridBagLayout() );
		canonLabels = new ArrayList<CanonicizerLabel>();
	}
	
	/**
	 * Add a CanonicizerLabel to be displayed in this panel.
	 * 
	 * @param canonicizerLabel
	 * 		The CanonicizerLabel to be added
	 */
	public void addCanonicizerLabel( CanonicizerLabel canonicizerLabel ){
		// We don't allow duplicate canonicizers
		//if( !canonLabels.contains(canonicizerLabel) ){
			canonLabels.add( canonicizerLabel );
			this.add( canonicizerLabel );
			if( CanonicizeStepPanel.borders )
			canonicizerLabel.setBorder( BorderFactory.createLineBorder( Color.green ) );
		//}	
	}
	
	/**
	 * Add a group of labels at the same time.
	 * 
	 * @param canonicizerLabels
	 * 		A Collection of CanonicizerLabels to be added to this panel.
	 */
	public void addCanonicizerLabels( Collection<CanonicizerLabel> canonicizerLabels ){
		for( CanonicizerLabel label : canonicizerLabels ){
			addCanonicizerLabel( label );
		}
	}
	
	/**
	 * Remove a CanonicizerLabel from this panel.
	 * 
	 * @param label
	 * 		The CanonicizerLabel to remove
	 */
	public void removeCanonicizerLabel( CanonicizerLabel label ){
		canonLabels.remove( label );
		this.remove( label );
	}
	
	/**
	 * This method is necessary when the label removal request
	 * comes from the DnDManager because of global changes.
	 * 
	 * @param foreignLabel
	 * 		The CanonicizerLabel to remove
	 */
	public void removeForeignCanonicizerLabel( CanonicizerLabel foreignLabel ){
		Iterator<CanonicizerLabel> iter = canonLabels.iterator();
		while( iter.hasNext() )
		{
			CanonicizerLabel localLabel = iter.next();
			if( localLabel.equals(foreignLabel) )
			{
				this.remove( localLabel );
				iter.remove();
				break;
			}
		}
	}
	
	/**
	 * Removes all CanonicizerLabels from this panel.
	 *
	 */
	public void removeAllCanonicizerLabels(){
		Iterator<CanonicizerLabel> iter = canonLabels.iterator();
		while( iter.hasNext() )
		{
			CanonicizerLabel label = iter.next();
			this.remove( label );
			iter.remove();
		}
	}
	
	/**
	 * Take a CanonicizerPacket, unpackage it, process the transfer
	 * data, and pull out the CanonicizerLabel.
	 * 
	 * @param packet
	 * 		A CanonicizerPacket to be unpackaged
	 */
	public void unpackageLabel( CanonicizerPacket packet ){
		//System.out.println("unpackageLabel(" + packet.getLabel() + ")");
		
		//Make a new instance of the label so we don't get
		// one label being linked to from all over the GUI
		//Also - reset the Label's CanonicizerLabelPackager
		// reference to point at this panel
		CanonicizerLabel label = new CanonicizerLabel( packet.getLabel(), this );
		
		// Notify the DnDManager of the new label
		switch( actionGroup )
		{
		case ALL:
			dndManager.processAdd( label );
			break;
		case DOCTYPE:
			dndManager.processAdd( label,
								   docType );
			break;
		case SINGLE:
			dndManager.processAdd( label,
								   doc );
			break;
		case NONE:
			//do nothing
			break;
		}
				
		// Add the new label to the visual display
		this.addCanonicizerLabel( label );
	}
	
	/**
	 * Generates a string representation of the CanonicizerLabels it contains.
	 * 
	 * @return
	 * 		String representation of the CanonicizerLabels within this panel
	 */
	public String getTooltipText(){
		String retVal = "";
		for( CanonicizerLabel label : canonLabels ){
			retVal += label.getCanonicizer().displayName() + ", ";
		}
		if( retVal.equals( "" ) )
			return null;
		return retVal.substring(0, retVal.length() - 2);		
	}	

	/**
	 * We override this to control whether components can be removed.
	 * When a CanonicizerLabel successfully drags it requests to
	 * be removed from its parent panel.
	 * @see java.awt.Container#remove(java.awt.Component)
	 */
	/*@Override
	public void remove(Component comp) {
		System.out.println("remove");
		// We ignore remove requests if this is the top panel
		if( !location.equals( Location.TOP ) )
			super.remove(comp);
	}*/
	
	/**
	 * @return the doc
	 */
	public Document getDocument() {
		return doc;
	}

	/**
	 * @param doc the document to set
	 */
	public void setDocument(Document doc) {
		this.doc = doc;
	}

	/**
	 * @return the actionGroup
	 */
	public ActionGroup getActionGroup() {
		return actionGroup;
	}

	/**
	 * @return the docType
	 */
	public DocType getDocType() {
		if( docType == null && doc != null )
			return doc.getDocType();
		return docType;
	}

	/**
	 * @param docType the docType to set
	 */
	public void setDocType(DocType docType) {
		this.docType = docType;
	}
}
