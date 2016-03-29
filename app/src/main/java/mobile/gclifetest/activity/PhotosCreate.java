package mobile.gclifetest.activity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mobile.gclifetest.MaterialDesign.ProgressBarCircularIndeterminate;
import mobile.gclifetest.Utils.MyApplication;
import mobile.gclifetest.custom.CustomGallery;
import mobile.gclifetest.custom.CustomImgGalleryActivity;
import mobile.gclifetest.custom.CustomVideoGalleryActivity;
import mobile.gclifetest.custom.GalleryImgAdapter;
import mobile.gclifetest.http.EvenstPost;
import mobile.gclifetest.http.SocietyNameGet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.widgets.SnackBar;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

public class PhotosCreate extends BaseActivity {
	ButtonFloat addBtn;
	EditText titleEdit;
	JSONObject jsonResult;
	String  ideaTitle,
			avenueName = "All", societyName = "All", memberType = "All",
			selectedPath1, ext, fileName, mediaUrl = "", eventName,	selectedAven="All", selectedSoci="All", selectedMem="All";
	TextView finishTxt, selectedMediaTxt;
	SharedPreferences userPref;
	Map<String, ArrayList<String>> societyMap = new HashMap<String, ArrayList<String>>();
	JSONArray jsonResultArry;
	ProgressBarCircularIndeterminate pDialog, pDialogImg;
	ImageView attachImg;
	int SELECT_FILE1 = 1;
	byte[] bytes;
	ViewSwitcher viewSwitcher;
	GalleryImgAdapter adapter;
	RelativeLayout avenueLay, sociLay, memLay;
	JSONArray eventImages = new JSONArray();
	ListView listviewAvenue, listviewSoci, listviewMem;
	Dialog dialogAvenue, dialogSoci, dialogMems;
	Typeface typefaceLight;
	ListAvenBaseAdapter aveAdapter;
	ArrayList<String> selectedAveNames = new ArrayList<String>();
	ArrayList<String> selectedSociNames = new ArrayList<String>();
	ArrayList<String> selectedMemsNames = new ArrayList<String>();
	boolean mems = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photos_create);

		userPref = getSharedPreferences("USER", MODE_PRIVATE);
		addBtn = (ButtonFloat) findViewById(R.id.addBtn);
		titleEdit = (EditText) findViewById(R.id.titleEdit);
		finishTxt = (TextView) findViewById(R.id.finishTxt);
		selectedMediaTxt = (TextView) findViewById(R.id.selectedMedia);
		avenueLay = (RelativeLayout) findViewById(R.id.avenueLay);
		sociLay = (RelativeLayout) findViewById(R.id.societyLay);
		memLay = (RelativeLayout) findViewById(R.id.memberLay);
		pDialog = (ProgressBarCircularIndeterminate) findViewById(R.id.pDialog);
		pDialogImg = (ProgressBarCircularIndeterminate) findViewById(R.id.pDialogImg);
		finishTxt = (TextView) findViewById(R.id.finishTxt);
		attachImg = (ImageView) findViewById(R.id.attachImg);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		eventName = bundle.getString("EventName");
		if (eventName == "Photos" || eventName.equals("Photos")) {
			setUpActionBar("Create Photos");
		}
		if (eventName == "Videos" || eventName.equals("Videos")) {
			setUpActionBar("Create Videos");
		}
		dialogAvenue = new Dialog(PhotosCreate.this);
		dialogAvenue.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogAvenue.setContentView(R.layout.avenues_listview_check);
		dialogAvenue.getWindow().getAttributes().windowAnimations = R.style.popup_login_dialog_animation;
		listviewAvenue = (ListView) dialogAvenue
				.findViewById(R.id.listviewAven);

		dialogSoci = new Dialog(PhotosCreate.this);
		dialogSoci.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogSoci.setContentView(R.layout.soci_listview_check);
		dialogSoci.getWindow().getAttributes().windowAnimations = R.style.popup_login_dialog_animation;
		listviewSoci = (ListView) dialogSoci.findViewById(R.id.listviewSoci);

		dialogMems = new Dialog(PhotosCreate.this);
		dialogMems.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogMems.setContentView(R.layout.mems_listview_check);
		dialogMems.getWindow().getAttributes().windowAnimations = R.style.popup_login_dialog_animation;
		listviewMem = (ListView) dialogMems.findViewById(R.id.listviewAven);

		ArrayList<String> membsList = new ArrayList<String>();
		membsList.add("All");
		membsList.add("Member");
		membsList.add("Committee member");
		membsList.add("Secretary");
		membsList.add("Chairman");
		membsList.add("Treasurer");
		membsList.add("Non member");

		ListMemsBaseAdapter memsAdapter = new ListMemsBaseAdapter(
				PhotosCreate.this, membsList);
		listviewMem.setAdapter(memsAdapter);

		TextView noTxtave = (TextView) dialogAvenue.findViewById(R.id.noTxt);
		TextView yesTxtave = (TextView) dialogAvenue.findViewById(R.id.yesTxt);

		TextView noTxtSoc = (TextView) dialogSoci.findViewById(R.id.noTxt);
		TextView yesTxtSoc = (TextView) dialogSoci.findViewById(R.id.yesTxt);

		TextView noTxtmem = (TextView) dialogMems.findViewById(R.id.noTxt);
		TextView yesTxtmem = (TextView) dialogMems.findViewById(R.id.yesTxt);
		noTxtave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogAvenue.dismiss();
			}
		});
		yesTxtave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectedAven = selectedAveNames.toString().replaceAll(
						"[\\[\\](){}]", "");
				selectedAven = selectedAven.replaceAll("\\s", "");
				System.out.println(selectedAven);
				dialogAvenue.dismiss();
			}
		});
		noTxtSoc.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogSoci.dismiss();
			}
		});
		yesTxtSoc.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogSoci.dismiss();
				selectedSoci = selectedSociNames.toString().replaceAll(
						"[\\[\\](){}]", "");
				selectedSoci = selectedSoci.replaceAll("\\s", "");
				System.out.println(selectedSoci);
			}
		});
		noTxtmem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogMems.dismiss();
			}
		});
		yesTxtmem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogMems.dismiss();
				selectedMem = selectedMemsNames.toString().replaceAll(
						"[\\[\\](){}]", "");
				selectedMem = selectedMem.replaceAll("\\s", "");
				System.out.println(selectedMem);
			}
		});
		avenueLay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				/*
				 * adapterUnames = new ListUnamesBaseAdapter( getActivity(),
				 * listUnames);
				 */
				// listviewUsernames.setAdapter(adapterUnames);
				dialogAvenue.show();
			}
		});
		sociLay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogSoci.show();
			}
		});
		memLay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogMems.show();
			}
		});

		new SocietyNames().execute();

		finishTxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				ideaTitle = titleEdit.getText().toString();

				if (ideaTitle == null || ideaTitle == "null" || ideaTitle == ""
						|| ideaTitle.length() == 0) {
					showSnack(PhotosCreate.this,
							"Please enter title!",
							"OK");
				} else if (eventImages == null || eventImages.toString() == "null"
						|| eventImages.toString() == "" || eventImages.length() == 0) {
					if (eventName == "Photos" || eventName.equals("Photos")) {
						showSnack(PhotosCreate.this,
								"Please select atleast one image!",
								"OK");
					}else{
						showSnack(PhotosCreate.this,
								"Please select atleast one video!",
								"OK");
					}
				
				} else {
					new CreatePhotosVideos().execute();
				}
			}
		});

		attachImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// openGallery(SELECT_FILE1);
				if (eventName == "Photos" || eventName.equals("Photos")) {
					Intent i = new Intent(PhotosCreate.this,CustomImgGalleryActivity.class);
					startActivityForResult(i, 200);
				}else{
					Intent i = new Intent(PhotosCreate.this, CustomVideoGalleryActivity.class);
					startActivityForResult(i, 200);
				}
				
			}
		});
	}

	public class CreatePhotosVideos extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			pDialog.setVisibility(View.VISIBLE);
			finishTxt.setVisibility(View.INVISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			JSONObject jsonIdeas = new JSONObject();
			try {
				jsonIdeas.put("event_type", eventName);
				jsonIdeas.put("title", ideaTitle);
				jsonIdeas.put("user_id", userPref.getString("USERID", "NV"));
				jsonIdeas.put("association_name", selectedAven);
				jsonIdeas.put("society_name", selectedSoci);
				jsonIdeas.put("member_type", selectedMem);


				System.out.println(eventImages + "!!!!!!!!!!!!!!!!!!!!!!!");

				if (eventImages.toString() == "[]"
						|| eventImages.toString().equals("[]")
						|| eventImages.length() == 0) {
					// jsonIdeas.put("EventImages", "[]");
				} else {
					jsonIdeas.put("EventImages", eventImages);
				}

				JSONObject eventJson = new JSONObject();
				eventJson.put("event", jsonIdeas);
				try {
					jsonResult = EvenstPost.makeRequest(eventJson, MyApplication.HOSTNAME);
					System.out.println("RESPONSE :::::::::::::::::::::"
							+ jsonResult);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void unused) {
			if (jsonResult != null) {
				if (jsonResult.has("id")) {
					pDialog.setVisibility(View.GONE);
					Intent i = new Intent(PhotosCreate.this, PhotosList.class);
					i.putExtra("EventName", eventName);
					startActivity(i);
				}
			} else {
				pDialog.setVisibility(View.GONE);
				finishTxt.setVisibility(View.VISIBLE);
				showSnack(PhotosCreate.this,
						"Oops! Something went wrong. Please wait a moment!",
						"OK");
			}
		}
	}

	public class SocietyNames extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			// pDialog.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				jsonResultArry = SocietyNameGet.callSocietyList(MyApplication.HOSTNAME);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void unused) {

			System.out.println(jsonResultArry
					+ "LIST OF MEMS !!!!!!!!!!!!!!!!!!!!");
			if (jsonResultArry != null) {
				try {
					ArrayList<String> listAssociation = new ArrayList<String>();
					listAssociation.add("All");
					for (int i = 0; i < jsonResultArry.length(); i++) {
						ArrayList<String> listSociety = new ArrayList<String>();
						listSociety.add("All");
						JSONObject json = jsonResultArry.getJSONObject(i);

						String associationName = json
								.getString("associationname");

						listAssociation.add(associationName);

						JSONArray societyArray;
						societyArray = json.getJSONArray("society_masters");
						ArrayList<String> listSocietydata = new ArrayList<String>();
						ArrayList<String> listbuilddata = null;

						System.out.println(societyArray);
						final int numberOfItemsInResp = societyArray.length();
						for (int j = 0; j < numberOfItemsInResp; j++) {

							JSONObject societyjson = societyArray
									.getJSONObject(j);

							String societyName = societyjson
									.getString("societyname");

							listSociety.add(societyName);
							listSocietydata.add(societyArray.get(j).toString());

							System.out.println(listSociety);

							System.out.println(listSocietydata);

							JSONArray buildingArray;
							buildingArray = societyjson
									.getJSONArray("building_masters");
							listbuilddata = new ArrayList<String>();
							for (int k = 0; k < buildingArray.length(); k++) {
								JSONObject jsonBuild = buildingArray
										.getJSONObject(k);
								String buildingName = jsonBuild
										.getString("buildinname");
								BigDecimal number = new BigDecimal(buildingName);
								buildingName = number.stripTrailingZeros()
										.toPlainString();

								listbuilddata.add(buildingName);

								// listbuilddata.add(buildingArray.get(k).toString());
							}

						}
						ArrayList<String> associationList = new ArrayList<String>(
								listSociety);
						societyMap.put(associationName, associationList);

						System.out.println(societyMap);

					}
					aveAdapter = new ListAvenBaseAdapter(PhotosCreate.this,
							listAssociation);
					listviewAvenue.setAdapter(aveAdapter);
					
					
					ArrayList<String> societylist = societyMap
							.get(listAssociation.get(1));
					ListSociBaseAdapter sociAdapter = new ListSociBaseAdapter(
							PhotosCreate.this, societylist);
					listviewSoci.setAdapter(sociAdapter);
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {

				showSnack(PhotosCreate.this,
						"Oops! Something went wrong. Please wait a moment!",
						"OK");
			}
		}
	}
	// gallery

	public void openGallery(int req_code) {

		Intent intent = new Intent();
		intent.setType("video/*, image/*");
		intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
		intent.setAction(Intent.ACTION_GET_CONTENT);

		startActivityForResult(Intent.createChooser(intent, "Select Picture"),
				1);

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
			final String[] all_path = data.getStringArrayExtra("all_path");
			if (all_path.length > 0) {
				pDialogImg.setVisibility(View.VISIBLE);
				if (eventName == "Photos" || eventName.equals("Photos")) {
					selectedMediaTxt.setText("Attaching images....");
				}else{
					selectedMediaTxt.setText("Attaching videos....");
				}
				
			}
			System.out.println(all_path);
			if (eventName == "Photos" || eventName.equals("Photos")) {
				showSnack(PhotosCreate.this,
						"You have Selected " + all_path.length + " images",
						"OK");
			}else{
				showSnack(PhotosCreate.this,
						"You have Selected " + all_path.length + " videos",
						"OK");
			}
		
			ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();

			for (String selectedPath1 : all_path) {
				CustomGallery item = new CustomGallery();
				item.sdcardPath = selectedPath1;

				dataT.add(item);

				// parsing
				ext = selectedPath1
						.substring(selectedPath1.lastIndexOf(".") + 1);
				fileName = selectedPath1.replaceFirst(".*/(\\w+).*", "$1");

				// selectedMediaTxt.setText(fileName+"."+ext);

				File filee = new File(selectedPath1);
				int size = (int) filee.length();
				bytes = new byte[size];
				try {
					BufferedInputStream buf = new BufferedInputStream(
							new FileInputStream(filee));
					buf.read(bytes, 0, bytes.length);
					buf.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(bytes + " !!!!!!!!!!!!!!!!!!!!");

				ext = ext.toLowerCase();
				System.out.println("File name : " + fileName + "   "
						+ "Image extension : " + ext);
				final ParseFile file = new ParseFile(fileName + "." + ext,
						bytes);
				file.saveInBackground(new SaveCallback() {

					@Override
					public void done(ParseException arg0) {
						// TODO Auto-generated method stub
						final ParseObject jobApplication = new ParseObject(
								"Files");
						if (ext == "png" || ext.equals("png")
								|| ext.equals("ico") || ext == "ico"
								|| ext == "jpg" || ext.equals("jpg")
								|| ext.equals("jpeg") || ext == "jpeg"
								|| ext == "gif" || ext.equals("gif")
								|| ext.equals("thm") || ext == "thm"
								|| ext == "tif" || ext.equals("tif")
								|| ext.equals("yuv") || ext == "yuv"
								|| ext.equals("bmp") || ext == "bmp") {
							jobApplication.put("mediatype", "image");
						} else {
							jobApplication.put("mediatype", "video");
						}
						// jobApplication.put("ticklecreatedid","Mayura");
						jobApplication.put("file", file);
						jobApplication.saveInBackground();
						// file = jobApplication.getParseFile(file);
						mediaUrl = file.getUrl();// live url
						System.err.println(mediaUrl
								+ "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

						try {
							JSONObject jsonMedia = new JSONObject();
							jsonMedia.put("image_type", "image");
							jsonMedia.put("image_url", mediaUrl);
							System.out.println(jsonMedia
									+ " +++++++++++++++++++++++ ");
							eventImages.put(jsonMedia);
							System.out.println(eventImages
									+ " ******************* ");

							if (all_path.length == eventImages.length()) {
								pDialogImg.setVisibility(View.GONE);
								
								if (eventName == "Photos" || eventName.equals("Photos")) {
									selectedMediaTxt.setText("Attached "
											+ all_path.length + " images");
								}else{
									selectedMediaTxt.setText("Attached "
											+ all_path.length + " videos");
								}
								
								
								
								finishTxt.setClickable(true);
							} else {
								pDialogImg.setVisibility(View.VISIBLE);
								

								if (eventName == "Photos" || eventName.equals("Photos")) {
									selectedMediaTxt.setText("Attaching images...."
											+ (String.valueOf(eventImages.length())));
								}else{
									selectedMediaTxt.setText("Attaching videos...."
											+ (String.valueOf(eventImages.length())));
								}
								
								
								finishTxt.setClickable(false);
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}

			// adapter.addAll(dataT);

		}
	}

	public String getPath(Uri uri) {

		String[] projection = { MediaStore.Images.Media.DATA };

		Cursor cursor = managedQuery(uri, projection, null, null, null);

		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

		cursor.moveToFirst();

		return cursor.getString(column_index);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			onBackPressed();
			overridePendingTransition(R.anim.slide_right_in,
					R.anim.slide_out_right);
			return true;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onBackPressed() {
		Intent soc = new Intent(PhotosCreate.this, PhotosList.class);
		soc.putExtra("EventName", eventName);
		startActivity(soc);
	}
	void showSnack(PhotosCreate login, String stringMsg, String ok) {
		new SnackBar(PhotosCreate.this, stringMsg, ok, new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		}).show();
	}


	public class ListAvenBaseAdapter extends BaseAdapter {
		ArrayList<String> list;
		private LayoutInflater inflator;
		private Context context;

		public ListAvenBaseAdapter(Activity activity,
				ArrayList<String> listArticles) {
			// TODO Auto-generated constructor stub
			this.context = activity;
			this.list = listArticles;
			inflator = (LayoutInflater) activity
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
			final ViewHolder holder;
			if (convertView == null) {
				convertView = inflator.inflate(R.layout.avenues_adapter_row,
						parent, false);
				holder = new ViewHolder();
				holder.titleTxt = (TextView) convertView
						.findViewById(R.id.titleTxt);

				holder.checkBox = (CheckBox) convertView
						.findViewById(R.id.checked);
				holder.checkBox.setVisibility(View.VISIBLE);
				holder.titleTxt.setTypeface(typefaceLight);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.titleTxt.setText(list.get(position));
			holder.checkBox.setChecked(true);

			holder.checkBox
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// TODO Auto-generated method stub
							if(isChecked){
								holder.checkBox.setChecked(true);
								}else{
									holder.checkBox.setChecked(true);
								}
							/*if (isChecked == true) {
								avenueName = list.get(position);
								if (avenueName == "All"
										|| avenueName.equals("All")) {
									selectedAveNames.add("All");
								} else {
									ArrayList<String> societylist = societyMap
											.get(avenueName);
									ListSociBaseAdapter sociAdapter = new ListSociBaseAdapter(
											IdeasCreate.this, societylist);
									listviewSoci.setAdapter(sociAdapter);
									selectedAveNames.add(list.get(position));
								}

							} else {
								selectedAveNames.remove(list.get(position));
							}*/
						}
					});
			
		//	selectedAveNames.add(list.get(position));

			return convertView;
		}

		public class ViewHolder {
			TextView titleTxt;
			CheckBox checkBox;
		}
	}

	public class ListSociBaseAdapter extends BaseAdapter {
		ArrayList<String> list;
		private LayoutInflater inflator;
		private Context context;

		public ListSociBaseAdapter(Activity activity,
				ArrayList<String> listArticles) {
			// TODO Auto-generated constructor stub
			this.context = activity;
			this.list = listArticles;
			inflator = (LayoutInflater) activity
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
			final ViewHolder holder;
			if (convertView == null) {
				convertView = inflator.inflate(R.layout.avenues_adapter_row,
						parent, false);
				holder = new ViewHolder();
				holder.titleTxt = (TextView) convertView
						.findViewById(R.id.titleTxt);
				holder.checkBox = (CheckBox) convertView
						.findViewById(R.id.checked);
				holder.checkBox.setVisibility(View.VISIBLE);
				holder.titleTxt.setTypeface(typefaceLight);
				holder.checkBox
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {
								// TODO Auto-generated method stub

								if(isChecked){
									holder.checkBox.setChecked(true);
									}else{
										holder.checkBox.setChecked(true);
									}
								/*if (isChecked) {
									selectedSociNames.add(list.get(position));
								} else {
									selectedSociNames.remove(list.get(position));
								}*/
							}
						});
				convertView.setTag(holder);
				convertView.setTag(holder);
				convertView.setTag(R.id.checked, holder.checkBox);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.checkBox.setTag(position);
			holder.titleTxt.setText(list.get(position));
			holder.checkBox.setChecked(true);
			return convertView;
		}

		public class ViewHolder {
			TextView titleTxt;
			CheckBox checkBox;
		}
	}

	public class ListMemsBaseAdapter extends BaseAdapter {
		ArrayList<String> list;
		private LayoutInflater inflator;
		private Context context;

		public ListMemsBaseAdapter(Activity activity,
				ArrayList<String> listArticles) {
			// TODO Auto-generated constructor stub
			this.context = activity;
			this.list = listArticles;
			inflator = (LayoutInflater) activity
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

		/*public void updateAdapter(ArrayList<String> list) {
			this.list = list;
			// and call notifyDataSetChanged
			notifyDataSetChanged();
		}*/

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			final ViewHolder holder;
			if (convertView == null) {
				convertView = inflator.inflate(R.layout.avenues_adapter_row,
						parent, false);
				holder = new ViewHolder();
				holder.titleTxt = (TextView) convertView
						.findViewById(R.id.titleTxt);
				holder.checkBox = (CheckBox) convertView
						.findViewById(R.id.checked);
				holder.checkBox.setVisibility(View.VISIBLE);
				holder.titleTxt.setTypeface(typefaceLight);
				holder.checkBox
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {
								
								if(isChecked){
									holder.checkBox.setChecked(true);
									}else{
										holder.checkBox.setChecked(true);
									}
								
								
								
								// TODO Auto-generated method stub
									//holder.checkBox.getText().toString();
									
									/*if (list.get(position) == "All"
											|| list.get(position).equals("All")) {
										if (isChecked) {
											mems = true;
											selectedMemsNames=new ArrayList<String>();
											selectedMemsNames.add(list.get(position));
										}else{
											selectedMemsNames=new ArrayList<String>();
											mems = false;
										}
										updateAdapter(list);
										
									}else{
										
										if(mems == true){
											//show message
											Toast.makeText(getApplicationContext(), "Please Uncheck All To Select Individual", 2000).show();
											holder.checkBox.setChecked(true);
											
										}else{
											if(isChecked){
											//	selectedMemsNames=new ArrayList<String>();
												selectedMemsNames.add(list.get(position));
											}else{
												selectedMemsNames.remove(list.get(position));
											}
										}
									}*/
							}
						});
				convertView.setTag(holder);
				convertView.setTag(holder);
				convertView.setTag(R.id.checked, holder.checkBox);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.checkBox.setTag(position);
			holder.titleTxt.setText(list.get(position));
			holder.checkBox.setChecked(true);
			/*if((selectedMemsNames.size()==0) || (selectedMemsNames.size()== 1 && selectedMemsNames.contains("All"))){
				
				
				if (mems == true) {
					holder.checkBox.setChecked(true);
				} else {
					holder.checkBox.setChecked(false);
				}
			}*/
			

			return convertView;
		}

		public class ViewHolder {
			TextView titleTxt;
			CheckBox checkBox;
	}
}

}
