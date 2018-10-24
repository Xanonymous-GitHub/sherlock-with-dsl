package uk.ac.warwick.dcs.sherlock.launch;

import org.springframework.boot.builder.SpringApplicationBuilder;
import uk.ac.warwick.dcs.sherlock.api.annotations.EventHandler;
import uk.ac.warwick.dcs.sherlock.api.annotations.SherlockModule;
import uk.ac.warwick.dcs.sherlock.api.annotations.SherlockModule.Instance;
import uk.ac.warwick.dcs.sherlock.api.event.EventInitialisation;
import uk.ac.warwick.dcs.sherlock.api.event.EventPostInitialisation;
import uk.ac.warwick.dcs.sherlock.api.event.EventPreInitialisation;
import uk.ac.warwick.dcs.sherlock.api.util.Side;
import uk.ac.warwick.dcs.sherlock.engine.SherlockEngine;
import uk.ac.warwick.dcs.sherlock.module.web.LocalDashboard;

@SherlockModule (side = Side.CLIENT)
public class SherlockClient {

	@Instance
	public static SherlockClient instance;

	private LocalDashboard dash;

	public static void main(String[] args) {
		SherlockServer.engine = new SherlockEngine(Side.CLIENT);
		new SpringApplicationBuilder(SherlockServer.class).headless(false).run(args);
	}

	@EventHandler
	public void initialisation(EventInitialisation event) {
		this.dash = new LocalDashboard();
	}

	@EventHandler
	public void postInitialisation(EventPostInitialisation event) {
		this.dash.setVisible(true);
	}

	@EventHandler
	public void preInitialisation(EventPreInitialisation event) {
	}
}