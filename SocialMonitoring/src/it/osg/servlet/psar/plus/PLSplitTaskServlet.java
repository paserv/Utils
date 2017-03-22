package it.osg.servlet.psar.plus;

import it.osg.servlet.SplitTaskServlet;
import it.osg.utils.Constants;
import it.osg.utils.DatastoreUtils;

public class PLSplitTaskServlet extends SplitTaskServlet {

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
		return "PLpsarsubtask";
	}


	@Override
	public String getJointask() {
		return "PLpsarjointask";
	}


	@Override
	public String getQueueName() {
		return "default";
	}


}
