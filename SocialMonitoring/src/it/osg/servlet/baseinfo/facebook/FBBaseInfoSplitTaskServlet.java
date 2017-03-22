package it.osg.servlet.baseinfo.facebook;

import it.osg.servlet.SplitTaskServlet;

public class FBBaseInfoSplitTaskServlet extends SplitTaskServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int getStep() {
		return Integer.parseInt((String.valueOf("1000000000")));
	}

	@Override
	public String getSubtask() {
		return "FBbaseinfosubtask";
	}

	@Override
	public String getJointask() {
		return "FBbaseinfojointask";
	}

	@Override
	public String getQueueName() {
		return "baseinfo";
	}

}
