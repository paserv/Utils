package it.osg.runnable;

import java.util.Hashtable;

import it.osg.data.PSAR;
import it.osg.utils.FacebookUtils;
import it.queue.RunnableQueue;

public class RunnableQueueBaseInfoImpl extends RunnableQueue {

	private PSAR data;

	public RunnableQueueBaseInfoImpl(PSAR data) {
		super();
		this.data = data;
	}

	@Override
	public void executeJob() throws Exception {

		Hashtable<String, Object> baseInfo = FacebookUtils.getBaseInfoFromJson(this.data.getId());
		if (baseInfo.get("likes") != null && !((String) baseInfo.get("likes")).equals("")) {
			this.data.setFan((String) baseInfo.get("likes"));
		} else {
			this.data.setFan("0");
		}
		
		if (baseInfo.get("name") != null && !((String) baseInfo.get("name")).equals("")) {
			this.data.setNome((String) baseInfo.get("name"));
		} else {
			this.data.setNome("NotFound");
		}

	}

	@Override
	public void rollback() {
		try {
			Thread.sleep(600000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
