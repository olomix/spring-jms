package sample;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 *
 */
public class App {

	private boolean optionStartServer = false;
	private boolean optionStartClient = false;

	private App(String[] args) {
		parseOptions(args);

	}

	private void parseOptions(String[] args) {
		Option server = new Option("server", "Start server");
		Option client = new Option("client", "Start client");
		Option all = new Option("all", "Start server and client");
		Option help = new Option("help", "Show options");
		Option brokerUrl = new Option("brokerUrl", true,
				"JMS broker URL. Default: ssl://localhost:61616");

		Options options = new Options();
		options.addOption(server);
		options.addOption(client);
		options.addOption(all);
		options.addOption(help);
		options.addOption(brokerUrl);

		CommandLineParser parser = new GnuParser();
		CommandLine line = null;
		try {
			line = parser.parse(options, args);
		} catch (ParseException e) {
			System.err.println("Command line options error: " + e.getMessage());
			e.printStackTrace();
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("App", options);
			System.exit(1);
		}

		if (line.hasOption("server")) {
			optionStartServer = true;
		}
		if (line.hasOption("client")) {
			optionStartClient = true;
		}
		if (line.hasOption("all")) {
			optionStartServer = true;
			optionStartClient = true;
		}
		if (line.hasOption("help")
				|| (!optionStartServer && !optionStartClient)) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("App [-all] | [-client] | [-server]", options);
			System.exit(0);
		}
		String bUrl = line.getOptionValue("brokerUrl");
		if(bUrl != null) {
			System.setProperty("broker.url", bUrl);
		}
	}

	public static void main(String[] args) {
		App app = new App(args);

		List<String> springConfigs = new ArrayList<String>();
		springConfigs.add("spring/common.xml");
		if (app.optionStartServer) {
			springConfigs.add("spring/broker.xml");
			springConfigs.add("spring/jms-server.xml");
		}
		if (app.optionStartClient) {
			springConfigs.add("spring/jms-client.xml");
		}

		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
				springConfigs.toArray(new String[] {}));
		ctx.registerShutdownHook();

		System.out.println("Hit ctrl-c to stop an application.");
		
		if(app.optionStartServer) {
			Server s = ctx.getBean(Server.class);
			s.sendMessages();
		}

		while (ctx.isActive()) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
