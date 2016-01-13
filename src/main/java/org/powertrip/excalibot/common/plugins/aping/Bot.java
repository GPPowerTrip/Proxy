package org.powertrip.excalibot.common.plugins.aping;

import org.powertrip.excalibot.common.com.SubTask;
import org.powertrip.excalibot.common.com.SubTaskResult;
import org.powertrip.excalibot.common.plugins.KnightPlug;
import org.powertrip.excalibot.common.plugins.interfaces.knight.ResultManagerInterface;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by Jaime on 04/01/2016.
 * 04:12
 */
public class Bot extends KnightPlug{
	public Bot(ResultManagerInterface resultManager) {
		super(resultManager);
	}

	@Override
	public boolean run(SubTask subTask) {
		SubTaskResult result = subTask.createResult();
		String address = subTask.getParameter("address");

		InetAddress addr = null;
		try {
			addr = InetAddress.getByName(address);
			long currentTime = System.currentTimeMillis();
			boolean pingOK = addr.isReachable(2000);
			currentTime = System.currentTimeMillis() - currentTime;
			if(pingOK) {
				result
						.setSuccessful(true)
						.setResponse("ping", String.valueOf(currentTime));
			} else {
				result
					.setSuccessful(false)
					.setResponse("stdout", "Ping failed");
			}
		} catch (IOException e) {
			result
					.setSuccessful(false)
					.setResponse("stdout", "Ping failed")
					.setResponse("exception", e.toString());
		}
		try {
			resultManager.returnResult(result);
			return result.isSuccessful();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return false;
	}
}
