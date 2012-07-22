package net.networksaremadeofstring.cyllell;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ViewRoles_Handler extends Handler 
{
	Dialog dialog;
	ProgressDialog progress;
	AlertDialog alert;
	Context context;

	public ViewRoles_Handler(Dialog _dialog)
	{
		this.dialog = _dialog;
	}

	public ViewRoles_Handler(Dialog _dialog, Context _context)
	{
		this.dialog = _dialog;
		this.context = _context;
	}

	public ViewRoles_Handler(Dialog _dialog, AlertDialog _alert)
	{
		this.dialog = _dialog;
		this.alert = _alert;
	}

	public ViewRoles_Handler(Dialog _dialog, ProgressDialog _progress, AlertDialog _alert, Context _context)
	{
		this.dialog = _dialog;
		this.progress = _progress;
		this.alert = _alert;
		this.context = _context;
	}

	public void handleMessage(Message msg) 
	{	
		switch(msg.what)
		{
		case R.integer.show_dialog:
		{
			dialog.show();
		}

		case R.integer.update_edit_dialog:
		{
			try
			{
				final JSONObject RoleDetails = new JSONObject(msg.getData().getString("RawJSON"));
				try
				{
					((TextView) dialog.findViewById(R.id.RoleDescription)).setText(RoleDetails.getString("description"));
				}
				catch(Exception e)
				{
					Toast.makeText(context, "There was a problem processing the description attribute", Toast.LENGTH_SHORT).show();
					((TextView) dialog.findViewById(R.id.RoleDescription)).setText(".......");
				}

				try
				{
					((TextView) dialog.findViewById(R.id.DefaultAttributes)).setText(RoleDetails.getString("default_attributes"));
				}
				catch(Exception e)
				{
					Toast.makeText(context, "There was a problem processing the default attributes.", Toast.LENGTH_SHORT).show();
					((TextView) dialog.findViewById(R.id.DefaultAttributes)).setText("{}");
				}

				try
				{
					((TextView) dialog.findViewById(R.id.OverrideAttributes)).setText(RoleDetails.getString("override_attributes"));
				}
				catch(Exception e)
				{
					Toast.makeText(context, "There was a problem processing override attributes", Toast.LENGTH_SHORT).show();
					((TextView) dialog.findViewById(R.id.OverrideAttributes)).setText("{}");
				}

				try
				{
					((TextView) dialog.findViewById(R.id.RunList)).setText(RoleDetails.getString("run_list"));
				}
				catch(Exception e)
				{
					Toast.makeText(context, "There was a problem processing Run List attributes", Toast.LENGTH_SHORT).show();
					((TextView) dialog.findViewById(R.id.RunList)).setText("{}");
				}

				((Button) dialog.findViewById(R.id.EditRoleButton)).setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) 
					{
						((ProgressBar) dialog.findViewById(R.id.gettingJSONProgressBar)).setVisibility(4);

						try
						{
							//TODO Potentially show a real progress dialog 
							Thread ProcessRequest = new Thread() 
							{  
								public void run() 
								{
									try 
									{
										JSONObject newRole = RoleDetails;
										newRole.put("description", ((TextView) dialog.findViewById(R.id.RoleDescription)).getText().toString());
										newRole.put("override_attributes", new JSONObject(((TextView) dialog.findViewById(R.id.OverrideAttributes)).getText().toString()));
										newRole.put("default_attributes", new JSONObject(((TextView) dialog.findViewById(R.id.DefaultAttributes)).getText().toString()));
										newRole.put("run_list", new JSONArray(((TextView) dialog.findViewById(R.id.RunList)).getText().toString()));
										Cuts Cut = new Cuts(context);
										if(Cut.UpdateRolewithRawJSON(RoleDetails.getString("name"),newRole))
										{
											sendEmptyMessage(R.integer.update_role_successful);
										}
										else
										{
											sendEmptyMessage(R.integer.update_role_unsuccessful);
										}
									}
									catch (org.apache.http.client.HttpResponseException e)
									{
										Log.e("StatusCode",Integer.toString(e.getStatusCode()));
										if(e.getStatusCode() == 401 || e.getStatusCode() == 403)
										{
											sendEmptyMessage(R.integer.http_forbidden);
										}
										else
										{
											e.printStackTrace();
											sendEmptyMessage(R.integer.http_bad_request);
										}
									}
									catch (Exception e) 
									{
										e.printStackTrace();
										sendEmptyMessage(R.integer.http_bad_request);
									}
								}

							};
							ProcessRequest.start();
						}
						catch(Exception e)
						{

						}
					}});
			}
			catch(Exception e)
			{
				e.printStackTrace();
				Toast.makeText(context, "There was a problem processing the JSON return value", Toast.LENGTH_SHORT).show();
				dialog.dismiss();
			}

			((ProgressBar) dialog.findViewById(R.id.gettingJSONProgressBar)).setVisibility(8);
		}
		break;

		case R.integer.update_role_successful:
		{
			Toast.makeText(context, "Role successfully updated!", Toast.LENGTH_SHORT).show();
			dialog.dismiss();
		}
		break;

		case R.integer.http_bad_request:
		{
			Toast.makeText(context, "Chef request failed with: Bad Request", Toast.LENGTH_SHORT).show();
		}
		break;

		case R.integer.http_forbidden:
		{
			Toast.makeText(context, "Chef request failed with the HTTP message; Forbidden", Toast.LENGTH_SHORT).show();
		}
		break;

		case R.integer.http_unauthorized:
		{
			Toast.makeText(context, "Chef request failed with due to authorization failure. (Hosted Chef role based access?)", Toast.LENGTH_SHORT).show();
		}
		break;

		default:
		{
			Toast.makeText(context, "There was a problem", Toast.LENGTH_SHORT).show();
			Log.i("ViewRoles_Handler", "What'ch you lookin' at?");
		}
		}
	}
}
