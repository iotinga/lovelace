package it.netgrid.lovelace;

import java.util.Properties;

public interface Configuration {
	public String getBindAddress();
	public int getBindPort();
	public String getJdbcConnectionUrl();
	public Properties getProperties();
	public String getJdbcUsername();
	public String getJdbcPassword();
}
