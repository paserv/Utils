package it.osg.servlet.psar.facebook;

import it.osg.servlet.SplitTaskServlet;
import it.osg.utils.DatastoreUtils;

public class FBSplitTaskServlet extends SplitTaskServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	@Override
	public int getStep() {
		String res = DatastoreUtils.getSetting("splitstep");
		return Integer.parseInt((res));
	}


	@Override
	public String getSubtask() {
		return "FBpsarsubtask";
	}


	@Override
	public String getJointask() {
		return "FBpsarjointask";
	}


	@Override
	public String getQueueName() {
		return "psar";
	}


}
