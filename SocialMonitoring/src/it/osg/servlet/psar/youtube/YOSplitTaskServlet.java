package it.osg.servlet.psar.youtube;

import it.osg.servlet.SplitTaskServlet;
import it.osg.utils.Constants;
import it.osg.utils.DatastoreUtils;

public class YOSplitTaskServlet extends SplitTaskServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	@Override
	public int getStep() {
		String res = (String) DatastoreUtils.getValue(Constants.SETTINGS_TABLE, "property", "splitstep", "value");
		return Integer.parseInt(res);
	}


	@Override
	public String getSubtask() {
		return "YOpsarsubtask";
	}


	@Override
	public String getJointask() {
		return "YOpsarjointask";
	}


	@Override
	public String getQueueName() {
		return "default";
	}


}
