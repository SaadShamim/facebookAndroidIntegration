/* example of facebook android sdk 3.0
using Graph API, FQL, and JSON data parsing 
function: grab the male/female distribution of friends */

package com.fbsampleappintegrate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;

public class MainActivity extends Activity {

	public int maleFriends;
	public int femaleFriends;
	public int totalFriends;
	
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // start Facebook Login
    Session.openActiveSession(this, true, new Session.StatusCallback() {

      // callback when session changes state
      @Override
      public void call(Session session, SessionState state, Exception exception) {
        if (session.isOpened()) {
        	
        	String fqlQuery = "SELECT sex FROM user WHERE uid IN " +
                    "(SELECT uid2 FROM friend WHERE uid1 = me())";
              Bundle params = new Bundle();
              params.putString("q", fqlQuery);
              Request request = new Request(session,
                  "/fql",                         
                  params,                         
                  HttpMethod.GET,                 
                  new Request.Callback(){         
                      public void onCompleted(Response response) {
                          try {
                        	  Log.d("sex",response.toString());
                        	  GraphObject go  = response.getGraphObject();
                              JSONObject  jso = go.getInnerJSONObject();
                              JSONArray   arr = jso.getJSONArray( "data" );
                              for ( int i = 0; i < ( arr.length() ); i++ )
                              {
                            	  totalFriends++;
                                  JSONObject json_obj = arr.getJSONObject( i );

                                  String sex   = json_obj.getString("sex");
                                  

                                  if(sex.equals("male")){
                                	  maleFriends++;
                                  }else{
                                	  femaleFriends++;
                                  }
                               

                              }
                              displayStats();

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                      }                  
              }); 
              Request.executeBatchAsync(request);      
              
        	
        	
          // make request to the /me API
          Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
        	  	
            // callback after Graph API response with user object
            @Override
            public void onCompleted(GraphUser user, Response response) {
              if (user != null) {
                TextView welcome = (TextView) findViewById(R.id.welcome);
                welcome.setText("Hello " + user.getName() + "!");

              }
            }
          });

        }
      }
    });

  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);

  }
  
  private void displayStats(){
	  TextView maleText = (TextView) findViewById(R.id.male);
      maleText.setText("you have " + maleFriends + " male friends");
	  TextView femaleText = (TextView) findViewById(R.id.female);
      femaleText.setText("you have " + femaleFriends + " female friends");
  }
  
  

}