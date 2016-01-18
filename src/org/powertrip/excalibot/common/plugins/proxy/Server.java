package org.powertrip.excalibot.common.plugins.proxy;

import org.powertrip.excalibot.common.com.*;
import org.powertrip.excalibot.common.plugins.ArthurPlug;
import org.powertrip.excalibot.common.plugins.interfaces.arthur.KnightManagerInterface;
import org.powertrip.excalibot.common.plugins.interfaces.arthur.TaskManagerInterface;
import org.powertrip.excalibot.common.utils.logging.Logger;

import java.util.List;
import java.util.Map;

/**
 * Integração de Sistemas
 * Pedro Filipe Dinis Stamm de Matos, 2009116927
 */
public class Server extends ArthurPlug{
    public Server(KnightManagerInterface knightManager, TaskManagerInterface taskManager) {
        super(knightManager, taskManager);
    }

    @Override
    public PluginHelp help() {
        return new PluginHelp().setHelp("::proxy [Proxy] Usage:"+"" +
                "proxy port:<integer> user:<username> password:<password>" +
                "[OPTIONAL] proxy_host:<address> proxy_port:<port> " +
                "proxy_user:<username> proxy_pass:<password>");
    }

    @Override
    public TaskResult check(Task task) {
        TaskResult result = new TaskResult();

        Long total = taskManager.getKnightCount(task.getTaskId());
        Long recev = taskManager.getResultCount(task.getTaskId());

        result
                .setSuccessful(true)
                .setTaskId(task.getTaskId())
                .setResponse("recev", String.valueOf(recev))
                .setResponse("total", total.toString())
                .setResponse("done", recev.toString())
                .setComplete(total.equals(recev));
        return result;
    }

    @Override
    public TaskResult get(Task task) {
        Long total = taskManager.getKnightCount(task.getTaskId());
        Long recev = taskManager.getResultCount(task.getTaskId());

        TaskResult result = new TaskResult()
                .setTaskId(task.getTaskId())
                .setSuccessful(true)
                .setComplete(total.equals(recev));
        List<SubTaskResult> resultsList = taskManager.getAllResults(task.getTaskId());

        if(resultsList.isEmpty()){
            return result.setResponse("stdout", "No results received.");
        }
        for(SubTaskResult res : resultsList){
            result
                    .setResponse("address",res.getResponse("address"))
                    .setResponse("port",res.getResponse("port"))
                    .setResponse("user",res.getResponse("user"))
                    .setResponse("password",res.getResponse("password"));
        }


        return result;
    }

    @Override
    public void handleSubTaskResult(Task task, SubTaskResult subTaskResult) {
        /**
         * Only if I need to do anything when I get a reply.
         */
    }

    @Override
    public TaskResult submit(Task task) {
        //Get my parameter map, could use task.getParameter(String key), but this is shorter.
        Logger.log(task.toString());
        Map args = task.getParametersMap();

        //Must-have Parameters
        String port, user, password;
        //Optional Parameters
        String proxy_host = null, proxy_pass = null, proxy_user = null, proxy_port = null;


        //Create a TaskResult and fill the common fields.
        TaskResult result = new TaskResult()
                .setTaskId(task.getTaskId())
                .setSuccessful(false)
                .setComplete(true);

        //Check if all main parameters were supplied
        if(!args.containsKey("port")|| !args.containsKey("user") || !args.containsKey("password")   ) {
            return result.setResponse("stdout", "Wrong parameters.");
        }

        //Parse parameters
        port = (String) args.get("port");
        //botCount = Long.parseLong((String) args.get("bots"));
        user = (String) args.get("user");
        password = (String) args.get("password");
        if(args.containsKey("proxy_host")) {
            proxy_host = (String) args.get("proxy_host");
            proxy_port = (String) args.get("proxy_port");
            proxy_user = (String) args.get("proxy_user");
            proxy_pass = (String) args.get("proxy_pass");
        }
        //Get bots alive in the last 30 seconds and gets the first listed
        KnightInfo bot = knightManager.getFreeKnightList(30000).get(0);
        knightManager.dispatchToKnight(
                new SubTask(task, bot)
                        .setParameter("port", port)
                        .setParameter("user",user)
                        .setParameter("password",password)
                        .setParameter("proxy_host",proxy_host)
                        .setParameter("proxy_port",proxy_port)
                        .setParameter("proxy_user",proxy_user)
                        .setParameter("proxy_pass",proxy_pass)
        );
        result
                .setSuccessful(true)
                .setResponse("stdout", "Task accepted, setting up Proxy.")
                .setResponse("address", bot.getProperties().get("ipAddress"))
                .setResponse("port",port)
                .setResponse("user",user)
                .setResponse("password",password);
        return result;
    }
}
