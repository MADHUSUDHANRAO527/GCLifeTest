package mobile.gclifetest.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import mobile.gclifetest.MaterialDesign.ProgressBarCircularIndeterminate;
import mobile.gclifetest.PojoGson.ImpContactsPojo;
import mobile.gclifetest.Utils.MyApplication;
import mobile.gclifetest.db.DatabaseHandler;

import org.json.JSONArray;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonRequest;
import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.widgets.SnackBar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ImpContacts extends BaseActivity {
	ButtonFloat addBtn;
	ProgressBarCircularIndeterminate pDialog;
	ListView listviewImp;
	SharedPreferences userPref;
	Typeface typefaceLight;
	String name, phNo, email;
	List<ImpContactsPojo> impContactPojo;
	Gson gson=new Gson();
    SwipeRefreshLayout mSwipeRefreshLayout;
    Runnable run;
    DatabaseHandler db;
    ListImpContsBaseAdapter adapterConts;
	RelativeLayout searchLay;
    EditText searchEdit;
    ProgressBar progressBar;
    String searchStr="";
    boolean progressShow=true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ideas_list);
		setUpActionBar("Imp Contacts");

		addBtn = (ButtonFloat) findViewById(R.id.addBtn);
		pDialog = (ProgressBarCircularIndeterminate) findViewById(R.id.pDialog);
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.activity_main_swipe_refresh_layout);
        searchEdit=(EditText)findViewById(R.id.searchEdit);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
		searchLay=(RelativeLayout)findViewById(R.id.searchLay);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.orange,
                R.color.green, R.color.blue);
		listviewImp = (ListView) findViewById(R.id.listview);
		listviewImp.setClickable(false);
		typefaceLight = Typeface.createFromAsset(getAssets(),
				"fonts/RobotoLight.ttf");
		addBtn.setVisibility(View.GONE);
		userPref = getSharedPreferences("USER", MODE_PRIVATE);
         db = new DatabaseHandler(this);
        mSwipeRefreshLayout
                .setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                impContactPojo = new ArrayList<ImpContactsPojo>();
                                callImpContsList();
                                runOnUiThread(run);
                                mSwipeRefreshLayout
                                        .setRefreshing(false);
                            }
                        }, 2500);
                    }
                });
        run = new Runnable() {
            public void run() {
                if (impContactPojo.toString() == "[]" || impContactPojo.size() == 0) {

                } else {
                    adapterConts.notifyDataSetChanged();
                    listviewImp.invalidateViews();
                }
            }
        };
		listviewImp
		.setOnItemClickListener(new OnItemClickListener() {

            @SuppressWarnings("unchecked")
            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View view, int position, long id) {
                // TODO Auto-generated method stub
                System.out.print(position + " ******** ");
                showPopup(impContactPojo, position);
            }
        });
        searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchStr = searchEdit.getText().toString();
                    progressShow = false;
                    callImpContsList();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchEdit.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        if (db.getEventNews("ImpContacts") != "null") {
            Log.d("DB NOT NULL: " + "ImpContats", db.getEventNews("ImpContacts"));
            pDialog.setVisibility(View.GONE);
            impContactPojo = gson.fromJson(db.getEventNews("ImpContacts"), new TypeToken<List<ImpContactsPojo>>() {
            }.getType());
            adapterConts = new ListImpContsBaseAdapter(
                    ImpContacts.this, impContactPojo);
            listviewImp.setAdapter(adapterConts);
            progressShow=false;
            callImpContsList();
        } else {
            callImpContsList();
            Log.d("DB NULL: " + "ImpContacts", "");
        }
	}

	protected void showPopup(List<ImpContactsPojo> impContsPojo, int position) {
		// TODO Auto-generated method stub
		Dialog m_dialog = new Dialog(ImpContacts.this);
		m_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		m_dialog.setContentView(R.layout.imp_contacts_popup_listview);
		m_dialog.getWindow().getAttributes().windowAnimations = R.style.popup_login_dialog_animation;
		ListView listviewConts = (ListView) m_dialog
				.findViewById(R.id.listview);
		phNo = impContsPojo.get(position).getPhno();

		String[] phNoArray = phNo.split(",");
		// String[]
		// emailArr=list.get(position).get("email").split(",");

		System.out.println(phNoArray
				+ "!!!!!!!!!!!!!!!!!!!!!! PH NO's");
		List<String> phList = Arrays.asList(phNoArray);
		// List<String> emailList = Arrays.asList(emailArr);
		ArrayList<HashMap<String, String>> listConts = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < phNoArray.length; i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("PHNO", phList.get(i));
			map.put("NAME", impContsPojo.get(position).getName());
			map.put("EMAIL", impContsPojo.get(position).getEmail());
			listConts.add(map);
		}
		ListImpPopupBaseAdapter adapter = new ListImpPopupBaseAdapter(
				ImpContacts.this, listConts);
		listviewConts.setAdapter(adapter);

		m_dialog.show();
	}
	private void callImpContsList() {
		try {
            pDialog.setVisibility(View.VISIBLE);
            String hostt = MyApplication.HOSTNAME + "important_contacts.json?search_key="+searchStr;
            hostt=hostt.replace(" ", "%20");
            JsonArrayRequest request = new JsonArrayRequest(JsonRequest.Method.GET, hostt,
                    (String) null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    //    pDialog.hide();
                    if (response != null) {
                        impContactPojo = gson.fromJson(response.toString(), new TypeToken<List<ImpContactsPojo>>() {
                        }.getType());
                        Log.d("Reponse", response.toString());
                        if (response.length() == 0
                                || response.toString() == "[]"
                                || response.toString() == ""
                                || response.toString().equals("")) {
                            pDialog.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            showSnack(ImpContacts.this,
                                    "Oops! There is no contacts!",
                                    "OK");
                            impContactPojo.clear();
                            adapterConts = new ListImpContsBaseAdapter(
                                    ImpContacts.this, impContactPojo);
                            adapterConts.notifyDataSetChanged();
                            listviewImp.setAdapter(adapterConts);
                        } else {

                            adapterConts = new ListImpContsBaseAdapter(
                                    ImpContacts.this, impContactPojo);
                            listviewImp.setAdapter(adapterConts);
                            pDialog.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            db.addEventNews(response, "ImpContacts");
							//for updating new data
							db.updateEventNews(response, "ImpContacts");
                        }
                    }

                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyError.printStackTrace();

                    Log.d("Error = ", volleyError.toString());
                    pDialog.setVisibility(View.GONE);
                    showSnack(ImpContacts.this,
                            "Oops! Something went wrong. Please check internet connection!",
                            "OK");
                    //   pDialog.hide();
                }
            });
            MyApplication.queue.add(request);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*public class ImpContact extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            if(progressShow==true){
                pDialog.setVisibility(View.VISIBLE);
            }else{
                progressBar.setVisibility(View.VISIBLE);
                pDialog.setVisibility(View.GONE);
            }

        }


        @Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				jsonResultArry = EvenstPost.makeRequestImpContList(host
						.globalVariable(),searchStr);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void unused) {

			System.out.println(jsonResultArry
					+ "LIST OF IMP CONTS !!!!!!!!!!!!!!!!!!!!");
            if (jsonResultArry != null) {
			impContactPojo=gson.fromJson(jsonResultArry.toString(),new TypeToken<List<ImpContactsPojo>>() {
			}.getType());

				if (jsonResultArry.length() == 0
						|| jsonResultArry.toString() == "[]"
						|| jsonResultArry.toString() == ""
						|| jsonResultArry.toString().equals("")) {
					pDialog.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
					showSnack(ImpContacts.this,
                            "Oops! There is no contacts!",
                            "OK");
                    impContactPojo.clear();
                    adapterConts = new ListImpContsBaseAdapter(
                            ImpContacts.this, impContactPojo);
                    adapterConts.notifyDataSetChanged();
                    listviewImp.setAdapter(adapterConts);
				} else {

					 adapterConts = new ListImpContsBaseAdapter(
							ImpContacts.this, impContactPojo);
					listviewImp.setAdapter(adapterConts);
					pDialog.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    db.addEventNews(jsonResultArry, "ImpContacts");
				}
			} else {
				pDialog.setVisibility(View.GONE);
				showSnack(ImpContacts.this,
                        "Oops! Something went wrong. Please check internet connection!",
                        "OK");
			}

		}
	}*/

	public class ListImpContsBaseAdapter extends BaseAdapter {
        List<ImpContactsPojo> impContsPojo;
		private LayoutInflater inflator;
		private Context context;

		public ListImpContsBaseAdapter(Activity activity,
                                       List<ImpContactsPojo> impContactsPojo) {
			// TODO Auto-generated constructor stub
			this.context = activity;
			this.impContsPojo = impContactsPojo;
			inflator = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return impContsPojo.size();
		}

		@Override
		public Object getItem(int pos) {
			// TODO Auto-generated method stub
			return impContsPojo.get(pos);
		}

		@Override
		public long getItemId(int pos) {
			// TODO Auto-generated method stub
			return pos;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflator.inflate(R.layout.imp_contacts_row,
						parent, false);
				holder = new ViewHolder();
				holder.titleTxt = (TextView) convertView
						.findViewById(R.id.titleTxt);

				holder.titleTxt.setTypeface(typefaceLight);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}


			holder.titleTxt.setText(impContsPojo.get(position).getName());

			return convertView;
		}

		public class ViewHolder {
			TextView titleTxt;
		}
	}

	public class ListImpPopupBaseAdapter extends BaseAdapter {
		ArrayList<HashMap<String, String>> list;
		private LayoutInflater inflator;
		private Context context;

		public ListImpPopupBaseAdapter(Context context2,
				ArrayList<HashMap<String, String>> listArticles) {
			// TODO Auto-generated constructor stub
			this.context = context2;
			this.list = listArticles;
			inflator = (LayoutInflater) context2
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int pos) {
			// TODO Auto-generated method stub
			return list.get(pos);
		}

		@Override
		public long getItemId(int pos) {
			// TODO Auto-generated method stub
			return pos;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflator.inflate(R.layout.imp_contacts_popup,
						parent, false);
				holder = new ViewHolder();
				holder.titleTxt = (TextView) convertView
						.findViewById(R.id.titleTxt);
				holder.phNumTxt = (TextView) convertView
						.findViewById(R.id.phNumTxt);
				holder.emailTxt = (TextView) convertView
						.findViewById(R.id.emailTxt);
				holder.callImg = (ImageView) convertView
						.findViewById(R.id.phoneImg);
				holder.smsImg = (ImageView) convertView
						.findViewById(R.id.smsImg);
				holder.emailImg = (ImageView) convertView
						.findViewById(R.id.emailImg);
				holder.titleTxt.setTypeface(typefaceLight);
				holder.phNumTxt.setTypeface(typefaceLight);
				holder.emailTxt.setTypeface(typefaceLight);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.callImg.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(Intent.ACTION_DIAL);

					intent.setData(Uri.parse("tel:" + list.get(position).get("PHNO")));
					context.startActivity(intent);
				}
			});
			holder.smsImg.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent smsVIntent = new Intent(Intent.ACTION_VIEW);
					smsVIntent.setType("vnd.android-dir/mms-sms");
					smsVIntent.putExtra("address", list.get(position).get("PHNO").replaceAll("[.0]+$", ""));
					try {
						startActivity(smsVIntent);
					} catch (Exception ex) {
						showSnack(ImpContacts.this,
								"Your sms has failed...!",
								"OK");
						ex.printStackTrace();
					}
				}
			});
			holder.emailImg.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
                            Uri.fromParts("mailto", list.get(position).get("EMAIL"), null));
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                }
            });
			holder.titleTxt.setText(list.get(position).get("NAME"));
			holder.phNumTxt.setText(list.get(position).get("PHNO").replaceAll("[.0]+$", ""));
			holder.emailTxt.setText(list.get(position).get("EMAIL"));
            if(list.get(position).get("EMAIL")==""||list.get(position).get("EMAIL").equals("")){
                holder.emailImg.setVisibility(View.GONE);
            }

			return convertView;
		}

		public class ViewHolder {
			TextView titleTxt, phNumTxt, emailTxt;
			ImageView callImg, smsImg, emailImg;
		}
	}

	void showSnack(ImpContacts flats, String stringMsg, String ok) {
		new SnackBar(ImpContacts.this, stringMsg, ok, new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		}).show();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.search, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

			case android.R.id.home:
				onBackPressed();
				overridePendingTransition(R.anim.slide_right_in,
						R.anim.slide_out_right);
				return true;
			case R.id.search:
				searchLay.setVisibility(View.VISIBLE);
                searchEdit.requestFocus();
                searchEdit.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));
				return true;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}