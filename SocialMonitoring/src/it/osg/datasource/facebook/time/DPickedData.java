package it.osg.datasource.facebook.time;

import it.osg.datasource.GraphSourceGenerator;
import it.osg.model.Graph;

import java.util.ArrayList;

/* 
 * Preleva i dati direttamente da Facebook ed in base ai dati che si vuole tirar fuori restituisce un ArrayList<GraphData>
 */

public class DPickedData extends GraphSourceGenerator {


	@Override
	public ArrayList<Graph> getGraphData(Object[] objects) {

		ArrayList<Graph> result = new ArrayList<Graph>();
		
		ArrayList<Graph> likes = new ArrayList<Graph>();
		PickedData myLikes = new PickedData();
		likes = myLikes.getGraphData(objects);
		
		for (int i = 0; i < likes.size() - 1; i++) {
			 Graph currGraph = likes.get(i);
			 Graph currGraph2 = likes.get(i + 1);
			 Graph newGraph = new Graph(currGraph2.getAxis(), currGraph2.getOrdinate() - currGraph.getOrdinate());
			 result.add(newGraph);
		}
		

		return result;

	}


}
