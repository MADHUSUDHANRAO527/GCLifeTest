package mobile.gclife.http;

import android.app.Application;

public class Hostname extends Application {
	private String globalVariable = "http://54.69.56.12:4000/";

	
	public String globalVariable() {
		return globalVariable;
	}

	public void setglobalVariableArticlesList(String globalVariable) {
		this.globalVariable = globalVariable;
	}

	@Override
	public void onCreate() {
		// reinitialize variable

	}

}
