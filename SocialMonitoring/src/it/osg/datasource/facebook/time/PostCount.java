package it.osg.datasource.facebook.time;

import facebook4j.Post;
import it.osg.datasource.GraphSourceGenerator;
import it.osg.model.Graph;
import it.osg.utils.DateUtils;
import it.osg.utils.FacebookUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;


/* 
 * Preleva i dati direttamente da Facebook ed in base ai dati che si vuole tirar fuori restituisce un ArrayList<GraphData>
 */

public class PostCount extends GraphSourceGenerator {


	@Override
	public ArrayList<Graph> getGraphData(Object[] objects) {
		ArrayList<Graph> result = new ArrayList<Graph>();

		String transmission = (String) objects[0];
		
		Date f = null;
		Date t = null;
		try {
			if (objects[1] != null && objects[2] != null){
				f = DateUtils.parseDateAndTime((String) objects[1]);
				t = DateUtils.parseDateAndTime((String)objects[2]);
			}

					
			ArrayList<Post> posts = FacebookUtils.getAllPosts(transmission, f, t, new String[]{"id", "created_time"});
			
			//TODO Ordinare per data crescente i post in maniera meno pezzotta
			for (int i = posts.size() - 1; i >= 0; i--) {
				Post curr = posts.get(i);
				Graph gd = new Graph(DateUtils.formatDateAndTime(curr.getCreatedTime()), 1L);
				result.add(gd);
			}
			
			
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return result;
	}


}
