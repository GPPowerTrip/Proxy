package org.powertrip.excalibot.common.plugins.proxy;

import org.powertrip.excalibot.common.com.SubTask;
import org.powertrip.excalibot.common.com.SubTaskResult;
import org.powertrip.excalibot.common.plugins.KnightPlug;
import org.powertrip.excalibot.common.plugins.interfaces.knight.ResultManagerInterface;
import org.powertrip.excalibot.common.utils.logging.Logger;
import socks.CProxy;
import socks.InetRange;
import socks.ProxyServer;
import socks.server.IdentAuthenticator;

import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Proxy Plugin
 *
 * This plugin creates a SOCKS5 proxy server on a specified port
 *
 */
public class Bot extends KnightPlug {

    public Bot(ResultManagerInterface resultManager) {
        super(resultManager);
    }

    @Override
    public boolean run(SubTask subTask) {
        SubTaskResult result = subTask.createResult();
        String user = null, password = null,
                proxy_host=null, proxy_pass=null, proxy_user=null, proxy_port=null;
        int port;
        Logger.log("Starting proxy setup");
        Map args = subTask.getParametersMap();
        Logger.log("Parameters received.");

        //Receive parameters
        port = Integer.parseInt((String)args.get("port"));
        //Check if user is specified
        if(args.containsKey("user") && args.containsKey("password")) {
            user = (String) args.get("user");
            password = (String) args.get("password");
        }
        //Check if external proxy is specified
        if(args.containsKey("proxy_host") && args.containsKey("proxy_port") && args.containsKey("proxy_pass") && args.containsKey("proxy_user")) {
            proxy_host = (String) args.get("proxy_host");
            proxy_port = (String) args.get("proxy_port");
            proxy_pass = (String) args.get("proxy_pass");
            proxy_user = (String) args.get("proxy_user");
        }
        Logger.log("Parameters filled");

        //Process parameters
        IdentAuthenticator auth = new IdentAuthenticator();
        Properties pr = new Properties();
        pr.setProperty("log","-");
        if(user!=null) {
            pr.setProperty("users", user + ":" + password);
        }
        if(proxy_host!=null)
            pr.setProperty("org/powertrip/excalibot/common/plugins/proxy",proxy_host+":"+proxy_port+":"+proxy_user+":"+proxy_pass);

        //Initialize Proxy
        addAuth(auth,pr);
        proxyInit(pr);
        ProxyServer server = new ProxyServer(auth);

        result
            .setSuccessful(true)
            .setResponse("address",subTask.getKnightInfo().getProperties().get("ipAddress"))
            .setResponse("port",""+port)
            .setResponse("user",user)
            .setResponse("password",password);
        try {
            resultManager.returnResult(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Start Proxy Server
        Logger.log("Starting Proxy server on port "+port+".");
        server.start(port,5,null);
        server.stop();
        //If something causes Proxy to shut down
        Logger.log("Proxy shutdown.");


        return false;
    }



    static boolean addAuth(IdentAuthenticator ident,Properties pr){

        InetRange irange;
        String range = (String) pr.get("range");
        if(range == null) range=".";
        irange = parseInetRange(range);


        String users = (String) pr.get("users");

        if(users == null){
            ident.add(irange,null);
            return true;
        }

        Hashtable uhash = new Hashtable();

        StringTokenizer st = new StringTokenizer(users,";");
        while(st.hasMoreTokens())
            uhash.put(st.nextToken(),"");

        ident.add(irange,uhash);
        return true;
    }

    /**
     Inits range from the string of semicolon separated ranges.
     */
    static InetRange parseInetRange(String source){
        InetRange irange = new InetRange();

        StringTokenizer st = new StringTokenizer(source,";");
        while(st.hasMoreTokens())
            irange.add(st.nextToken());

        return irange;
    }

    /**
     Initialises proxy, if any specified.
     */
    static void proxyInit(Properties props){
        String proxy_list;
        CProxy proxy = null;
        StringTokenizer st;

        proxy_list = (String) props.get("org/powertrip/excalibot/common/plugins/proxy");
        if(proxy_list == null) return;

        st = new StringTokenizer(proxy_list,";");
        while(st.hasMoreTokens()){
            String proxy_entry = st.nextToken();


            CProxy p = CProxy.parseProxy(proxy_entry);

            if(p == null)
                Logger.log("Can't parse proxy entry:" + proxy_entry);


            Logger.log("Adding Proxy:"+p);

            if(proxy != null)
                p.setChainProxy(proxy);

            proxy = p;

        }
        if(proxy == null) return;  //Empty list

        String direct_hosts = (String) props.get("directHosts");
        if(direct_hosts!=null){
            InetRange ir = parseInetRange(direct_hosts);
            Logger.log("Setting direct hosts:"+ir);
            proxy.setDirect(ir);
        }


        ProxyServer.setProxy(proxy);
    }


}
