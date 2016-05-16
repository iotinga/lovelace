package it.netgrid.lovelace;

public interface PersistenceTestHandler {

	void setup();
	void destroy();
	void loadData();
	
}