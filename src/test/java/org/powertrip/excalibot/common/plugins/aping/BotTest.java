package org.powertrip.excalibot.common.plugins.aping;

import org.junit.Test;
import org.powertrip.excalibot.common.com.SubTask;
import org.powertrip.excalibot.common.com.SubTaskResult;
import org.powertrip.excalibot.common.plugins.interfaces.knight.ResultManagerInterface;

/**
 * Created by Jaime on 04/01/2016.
 * 04:47
 */
public class BotTest {

	@Test
	public void testRun() throws Exception {
		Bot bot = new Bot(new DummyResultManager());
		bot.run(new SubTask().setParameter("address", "192.168.1.54"));
	}
}

//Dummy resource manager, the thing that sends messages back to Arthur (server)
class DummyResultManager implements ResultManagerInterface{
	@Override
	public void returnResult(SubTaskResult subTaskResult) throws InterruptedException {
		System.out.println("[OUTPUT]: " +subTaskResult);
	}
}