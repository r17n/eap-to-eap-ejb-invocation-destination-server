package org.jboss.as.quickstarts.ejb.remote.stateful;

import javax.ejb.Remote;
import javax.ejb.Stateful;



@Stateful
@Remote(RemoteComplimenterEJB.class)
public class ComplimenterEJB implements RemoteComplimenterEJB {
	
	@Override
	public String compliment(String name) {
		return "Looking Good, " + name;
	}
}
