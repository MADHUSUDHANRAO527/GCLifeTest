package mobile.gclifetest.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;

import mobile.gclifetest.http.SignUpPost;
import mobile.gclifetest.materialDesign.ProgressBarCircularIndeterminate;
import mobile.gclifetest.pojoGson.UserDetailsPojo;
import mobile.gclifetest.utils.Constants;
import mobile.gclifetest.utils.MyApplication;

public class EditProfile extends BaseActivity {
	RelativeLayout dateToLay;
	static final int DATE_DIALOG_FROMID = 0;
	String userName, email, mobileNum, genderName = "Male", emeNum,
			occupation, dob, privacy, ext, fileName, mediaUrl="";
	TextView submitTxt,uploadingTxt;
	SharedPreferences userPref;
	ProgressBarCircularIndeterminate pDialog;
	EditText userNameEdit, emailEdit, mobileNumEdit, emeContaNum,
			occupatioEdit, dobEdit;
	JSONObject jsonSignupResult;
	Editor editor;
	UserDetailsPojo user;
	Switch privacySwitch;
	String checkPatternId = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    de.hdodenhof.circleimageview.CircleImageView profileImg;
    byte[] bytes;
	ImageLoader imageLoader;
	DisplayImageOptions options;
	RelativeLayout snackLay;
	RadioGroup genderRadio;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_profile);
		dateToLay = (RelativeLayout) findViewById(R.id.dateToLay);
		pDialog = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBarCircularIndetermininate);
		dobEdit = (EditText) findViewById(R.id.dobEdit);
		submitTxt = (TextView) findViewById(R.id.submitTxt);
		uploadingTxt = (TextView) findViewById(R.id.uploadingTxt);
        profileImg = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.imageProfile);
        userNameEdit = (EditText) findViewById(R.id.userName);
		emailEdit = (EditText) findViewById(R.id.mailEdit);
		mobileNumEdit = (EditText) findViewById(R.id.mobileNumEdit);
		snackLay = (RelativeLayout) findViewById(R.id.snackLay);
		emeContaNum = (EditText) findViewById(R.id.emeContaNum);
		occupatioEdit = (EditText) findViewById(R.id.occupationNumEdit);
		dobEdit = (EditText) findViewById(R.id.dobEdit);
		privacySwitch = (Switch) findViewById(R.id.privacySwitch);
		genderRadio = (RadioGroup) findViewById(R.id.radioGrp);
		setUpActionBar("Update Profile");
		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisc(true).resetViewBeforeLoading(true)
				.showImageForEmptyUri(R.drawable.no_media)
				.showImageOnFail(R.drawable.no_media)
				.showImageOnLoading(R.drawable.no_media).build();


		userPref = getSharedPreferences("USER", MODE_PRIVATE);
		editor = userPref.edit();

		Gson gson = new Gson();
		String jsonUser = userPref.getString("USER_DATA", "NV");
		user = gson.fromJson(jsonUser, UserDetailsPojo.class);
		imageLoader.displayImage(user.getProfile_url(), profileImg, options);
		userNameEdit.setText(user.getUsername());
		emailEdit.setText(user.getEmail());
		mobileNumEdit.setText(user.getMobile());

		emeContaNum.setText(user.getEmeNum());
		occupatioEdit.setText(user.getOccupation());
		dobEdit.setText(user.getDob());

		privacySwitch.setChecked(user.getPrivacy());

        getSupportActionBar().setElevation(0);
		dobEdit.setOnTouchListener(new OnTouchListener() {
			@SuppressWarnings("deprecation")
			public boolean onTouch(View v, MotionEvent event) {
				if (v == dobEdit)
					showDialog(DATE_DIALOG_FROMID);
				return false;
			}
		});
		profileImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
				final int ACTIVITY_SELECT_IMAGE = 1234;
				startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
			}
		});
		submitTxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				userName = userNameEdit.getText().toString();
				email = emailEdit.getText().toString();
				mobileNum = mobileNumEdit.getText().toString();
				if (userName == null || userName == "null" || userName == ""
						|| userName.length() == 0) {
					Constants.showSnack(v,
							"Please enter a valid username!",
							"OK");
				} else if (mobileNum == null || mobileNum == "null"
						|| mobileNum == "" || mobileNum.length() == 0) {
					Constants.showSnack(v,
							"Please enter mobile number!",
							"OK");
				} else if (mobileNum.length() < 10) {
					Constants.showSnack(v,
							"Please enter proper mobile number!",
							"OK");
				} else if (email == null || email == "null" || email == ""
						|| email.length() == 0) {
					Constants.showSnack(v,
							"Please enter your email!",
							"OK");
				} else if (email != null && !email.isEmpty()
						&& !email.matches(checkPatternId)) {
					Constants.showSnack(v,
							"Please enter a valid email address!",
							"OK");
				} else {
					emeNum = emeContaNum.getText().toString();
					occupation = occupatioEdit.getText().toString();
					dob = dobEdit.getText().toString();
					emeNum = emeContaNum.getText().toString();
					occupation = occupatioEdit.getText().toString();
					dob = dobEdit.getText().toString();
					new UpdateProfile().execute();
				}

			}
		});
		privacySwitch
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// do something, the isChecked will be
						// true if the switch is in the On position
						if (isChecked) {
							privacy = "true";
						} else {
							privacy = "false";
						}
					}
				});
		genderRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				RadioButton rb = (RadioButton) findViewById(checkedId);
				genderName = (String) rb.getText();
			}
		});

	}

	private DatePickerDialog.OnDateSetListener dobListnder = new DatePickerDialog.OnDateSetListener() {
		// onDateSet method
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			String day = String.valueOf(dayOfMonth);
			String month = String.valueOf(monthOfYear + 1);
			System.out.println(month);
			if (day.length() == 1) {
				day = "0" + day;
			}
			if (month.length() == 1) {
				month = "0" + month;
			}
			dobEdit.setText(day + "-" + month + "-" + String.valueOf(year));
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		Calendar c = Calendar.getInstance();
		int cyear = c.get(Calendar.YEAR);
		int cmonth = c.get(Calendar.MONTH);
		int cday = c.get(Calendar.DAY_OF_MONTH);

		switch (id) {
		case DATE_DIALOG_FROMID:
			return new DatePickerDialog(this, dobListnder, cyear, cmonth, cday);
		}

		return null;
	}

	public class UpdateProfile extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			pDialog.setVisibility(View.VISIBLE);
			submitTxt.setVisibility(View.INVISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			JSONObject jsonSignUp = new JSONObject();
			try {
				jsonSignUp.put("user_id", user.getId());
				jsonSignUp.put("mobile", mobileNum);
				jsonSignUp.put("email", email);
				jsonSignUp.put("username", userName);
				jsonSignUp.put("emergency_contct_no", emeNum);
				jsonSignUp.put("occupation", occupation);
				jsonSignUp.put("gender", genderName);
				jsonSignUp.put("dob", dob);
				jsonSignUp.put("privacy", privacy);
				if (mediaUrl.equals("") || mediaUrl == null) {
					jsonSignUp.put("profile_url", user.getProfile_url());
				} else {
					jsonSignUp.put("profile_url", mediaUrl);
				}

				try {
					jsonSignupResult = SignUpPost.updateProfile(jsonSignUp,
							MyApplication.HOSTNAME);

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
            System.out.println(jsonSignupResult +" @@@@@@@@@@@@@@@@");
			if (jsonSignupResult != null) {

				if (jsonSignupResult.has("errors")) {

					Constants.showSnack(snackLay,
							"email has already been taken!",
							"OK");
					pDialog.setVisibility(View.GONE);
					submitTxt.setVisibility(View.VISIBLE);

				} else {
					pDialog.setVisibility(View.GONE);
					submitTxt.setVisibility(View.VISIBLE);

					Gson gson = new GsonBuilder().create();
					MyApplication.user = gson.fromJson(
							jsonSignupResult.toString(), UserDetailsPojo.class);

					Gson gsonn = new Gson();
					String json = gsonn.toJson(MyApplication.user);

					editor.putString("USER_DATA", json);
					editor.commit();

                    Intent intent = new Intent();
                    intent.putExtra("EACH_USER_DET", json);
                    setResult(1, intent);

                    overridePendingTransition(R.anim.slide_in_left,
							R.anim.slide_out_left);
					Constants.showSnack(snackLay,
							"Profile has been updated!",
							"OK");
					Intent ii = new Intent(EditProfile.this, UserProfile.class);
					ii.putExtra("EACH_USER_DET", "");
					startActivity(ii);
				}

			} else {
				pDialog.setVisibility(View.GONE);
				submitTxt.setVisibility(View.VISIBLE);

				Constants.showSnack(snackLay,
						"Oops! Something went wrong. Please wait a moment!",
						"OK");
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
            finish();
            onBackPressed();
			overridePendingTransition(R.anim.slide_right_in,
					R.anim.slide_out_right);
			return true;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case 1234:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
					if(!selectedImage.equals(null)||selectedImage!=null){
						uploadingTxt.setVisibility(View.VISIBLE);
					}
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();
					//to resolve out of memory error
					final BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 8;
					Bitmap bm = BitmapFactory.decodeFile(filePath,options);
                    profileImg.setImageBitmap(bm);

                    ext = filePath
                            .substring(filePath.lastIndexOf(".") + 1);
                    fileName = filePath.replaceFirst(".*/(\\w+).*", "$1");
                    //send image to parse
                    File filee = new File(filePath);
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
                            jobApplication.put("mediatype", "image");
                            jobApplication.put("file", file);
                            jobApplication.saveInBackground();
                            // file = jobApplication.getParseFile(file);
                            mediaUrl = file.getUrl();// live url
                            System.err.println(mediaUrl
                                    + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
							uploadingTxt.setVisibility(View.GONE);
                        }
                        });

                }
        }

    };
}