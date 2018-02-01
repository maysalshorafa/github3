package com.pos.leaders.leaderspossystem;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.pos.leaders.leaderspossystem.Tools.SESSION;
import com.pos.leaders.leaderspossystem.Tools.SETTINGS;
import com.pos.leaders.leaderspossystem.syncposservice.Enums.ApiURL;
import com.pos.leaders.leaderspossystem.syncposservice.Enums.MessageKey;
import com.pos.leaders.leaderspossystem.syncposservice.MessageTransmit;
import com.pos.leaders.leaderspossystem.syncposservice.MessagesCreator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.acl.LastOwnerException;

public class AccessToken extends AsyncTask<Context,Void,String> {
    MessageTransmit messageTransmit;
    Context context;
    ProgressDialog progressDialog;
    public AccessToken(Context context){
        messageTransmit = new MessageTransmit(SETTINGS.BO_SERVER_URL);
        this.context = context;
        this.progressDialog = new ProgressDialog(context);
    }


    @Override
    protected String doInBackground(Context... contexts) {
        Context context = contexts[0];
        SharedPreferences prefs = context.getSharedPreferences(SetupNewPOSOnlineActivity.BO_CORE_ACCESS_AUTH, Context.MODE_PRIVATE);
        String posID = prefs.getString(MessageKey.PosID, null);
        String posPass = prefs.getString(MessageKey.PosPass, null);

        if (posID != null && posPass != null) {
            //call api auth
            try {
                String authRes = messageTransmit.post(ApiURL.Authentication, MessagesCreator.authentication(posID, posPass));
                Log.i("result token", authRes);
                JSONObject jsonObject = new JSONObject(authRes);
                String token = jsonObject.getString(MessageKey.Token);
                SharedPreferences sharedpreferences = context.getSharedPreferences(SetupNewPOSOnlineActivity.BO_CORE_ACCESS_TOKEN, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(MessageKey.Token, token);
                editor.apply();
                onPostExecute(token);
                cancel(true);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setTitle("Getting Access.");
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        progressDialog.cancel();
        if( !s.equals("") )
            SESSION.token = s;
    }
}
