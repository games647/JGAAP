package com.jgaap.eventDrivers;

import static org.junit.Assert.*;

import java.util.Vector;

import com.jgaap.jgaapConstants;
import com.jgaap.generics.Document;
import com.jgaap.generics.DocumentSet;
import com.jgaap.generics.Event;
import com.jgaap.generics.EventSet;

import org.junit.Test;

import com.jgaap.generics.Document;

public class NGramEventDriverTest {

	@Test
	public void testCreateEventSetDocumentSet() {
		Document doc = new Document();
		
		doc.readStringText("Testing sentence. My name is Joe. run jump, game-start?  Hello!");
		DocumentSet docSet = new DocumentSet(doc);
		EventSet sampleEventSet = new NGramEventDriver().createEventSet(docSet);
		EventSet expectedEventSet = new EventSet();
		Vector<Event> tmp = new Vector<Event>();
		
		
		
		tmp.add(new Event("(Testing)-(sentence.)"));
		tmp.add(new Event("(sentence.)-(My)"));
		tmp.add(new Event("(My)-(name)"));
		tmp.add(new Event("(name)-(is)"));
		tmp.add(new Event("(is)-(Joe.)"));
		tmp.add(new Event("(Joe.)-(run)"));
		tmp.add(new Event("(run)-(jump,)"));
		tmp.add(new Event("(jump,)-(game-start?)"));
		tmp.add(new Event("(game-start?)-(Hello!)"));
		
		
		
		expectedEventSet.events.addAll(tmp);
		
//System.out.println(expectedEventSet.toString());
//System.out.println(sampleEventSet.toString());

		
		assertTrue(expectedEventSet.equals(sampleEventSet));
	}

}
