package cz.artique.test.caffeineManager;

import org.glassfish.jersey.server.ResourceConfig;

import cz.artique.test.caffeineManager.resource.CoffeeResource;
import cz.artique.test.caffeineManager.resource.MachineResource;
import cz.artique.test.caffeineManager.resource.StatsResource;
import cz.artique.test.caffeineManager.resource.UserResource;

public class CaffeineManagerApplication extends ResourceConfig {
	public CaffeineManagerApplication() {
		register(UserResource.class);
		register(MachineResource.class);
		register(CoffeeResource.class);
		register(StatsResource.class);
	}
}
