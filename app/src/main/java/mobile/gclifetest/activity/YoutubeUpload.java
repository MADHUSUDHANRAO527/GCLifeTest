package mobile.gclifetest.activity;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonFactory;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;
import com.google.common.collect.Lists;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class YoutubeUpload extends AppCompatActivity {
    static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    Button uploadVideobtn;
    private String selectedGoogleAccount;
    public static final String scope = "oauth2:https://www.googleapis.com/auth/youtube";
    String token;
    public JsonFactory JSON_FACTORY = new JsonFactory();
    TextView userNameTxt, tokenTxt, localVideoUrlTxt, youtubeUrlTxt;
    EditText titleEdit;
    private String videoFileName = "/storage/emulated/0/Download/Sample.3gp";
    private Video youtubeVideo = null;
    private YouTubeUploadTask youtubeUploadTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button pickAcc = (Button) findViewById(R.id.chseAcc);
        uploadVideobtn = (Button) findViewById(R.id.upload_video);
        userNameTxt = (TextView) findViewById(R.id.textView);
        tokenTxt = (TextView) findViewById(R.id.textView2);
        titleEdit = (EditText) findViewById(R.id.titleNme);
        localVideoUrlTxt = (TextView) findViewById(R.id.textView3);
        youtubeUrlTxt = (TextView) findViewById(R.id.textView4);
        localVideoUrlTxt.setText("Local video URl:" + videoFileName);
        pickAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickUserAccount();

            }
        });
        uploadVideobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (titleEdit.getText().toString().length() < 1) {
                    Toast.makeText(YoutubeUpload.this, "Enter title", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(YoutubeUpload.this, "Initiation Started : please wait", Toast.LENGTH_LONG).show();
                    new YouTubeUploadTask().execute();
                }
            }
        });
        youtubeUrlTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                redirectAndPlayVideo();
            }
        });
    }


    private void pickUserAccount() {
        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null, accountTypes, false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_ACCOUNT && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
                if (accountName != null) {
                    selectedGoogleAccount = accountName;
                    userNameTxt.setText("Email : " + selectedGoogleAccount);

                    new FetchYouTubeTokenTask().execute();

                } else {
                    //   DialogUtil.showExceptionAlertDialog(this, getString(R.string.googleAccountNotSelected), getString(R.string.googleAccountNotSupported));
                }
            }
        } else if (requestCode == REQUEST_AUTHORIZATION && resultCode == Activity.RESULT_OK) {
            // Account has been chosen and permissions have been granted. You can upload video
            Toast.makeText(this, "APP AUTHORIZED!", Toast.LENGTH_LONG).show();
            new FetchYouTubeTokenTask().execute();
        }
    }


    public class FetchYouTubeTokenTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                token = GoogleAuthUtil.getToken(YoutubeUpload.this, selectedGoogleAccount, scope);
            } catch (UserRecoverableAuthException userAuthEx) {
                // In case Android complains that Access not Configured, refer to comment of this class for how to configure OAuth client ID for this app.
                startActivityForResult(userAuthEx.getIntent(), REQUEST_AUTHORIZATION);
            } catch (GoogleAuthException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (token != null) {
                tokenTxt.setText("Token : " + token);
                Toast.makeText(YoutubeUpload.this, token, Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     /*   * Upload a video to the authenticated user's channel. Use OAuth 2.0 to
     * authorize the request. Note that you must add your video files to the
     * project folder to upload them with this application.
     *
     * @author Jeremy Walker
     *//*
    public class UploadVideo {

        *//**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     *//*
        private  YouTube youtube;

        *//**
     * Define a global variable that specifies the MIME type of the video
     * being uploaded.
     *//*
        private static final String VIDEO_FILE_FORMAT = "video*//*";

        private static final String SAMPLE_VIDEO_FILENAME = "sample-video.mp4";
        public  final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

        *//**
     * Define a global instance of the JSON factory.
     *//*


        */

    /**
     * Upload the user-selected video to the user's YouTube channel. The code
     * looks for the video in the application's project folder and uses OAuth
     * 2.0 to authorize the API request.
     * <p/>
     * //   * @param args command line args (not used).
     *//*
        public  void main(String[] args) {

            // This OAuth 2.0 access scope allows an application to upload files
            // to the authenticated user's YouTube channel, but doesn't allow
            // other types of access.
            List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube.upload");

            try {
                // Authorize the request.
                Credential credential = authorize(scopes, "uploadvideo");

                // This object is used to make YouTube Data API requests.
                youtube = new YouTube.Builder(HTTP_TRANSPORT,JSON_FACTORY, credential).setApplicationName(
                        "youtube-cmdline-uploadvideo-sample").build();

                System.out.println("Uploading: " + SAMPLE_VIDEO_FILENAME);

                // Add extra information to the video before uploading.
                Video videoObjectDefiningMetadata = new Video();

                // Set the video to be publicly visible. This is the default
                // setting. Other supporting settings are "unlisted" and "private."
                VideoStatus status = new VideoStatus();
                status.setPrivacyStatus("public");
                videoObjectDefiningMetadata.setStatus(status);

                // Most of the video's metadata is set on the VideoSnippet object.
                VideoSnippet snippet = new VideoSnippet();

                // This code uses a Calendar instance to create a unique name and
                // description for test purposes so that you can easily upload
                // multiple files. You should remove this code from your project
                // and use your own standard names instead.
                Calendar cal = Calendar.getInstance();
                snippet.setTitle("Test Upload via Java on " + cal.getTime());
                snippet.setDescription(
                        "Video uploaded via YouTube Data API V3 using the Java library " + "on " + cal.getTime());

                // Set the keyword tags that you want to associate with the video.
                List<String> tags = new ArrayList<String>();
                tags.add("test");
                tags.add("example");
                tags.add("java");
                tags.add("YouTube Data API V3");
                tags.add("erase me");
                snippet.setTags(tags);

                // Add the completed snippet object to the video resource.
                videoObjectDefiningMetadata.setSnippet(snippet);

                InputStreamContent mediaContent = new InputStreamContent(VIDEO_FILE_FORMAT,
                        UploadVideo.class.getResourceAsStream("/sample-video.mp4"));

                // Insert the video. The command sends three arguments. The first
                // specifies which information the API request is setting and which
                // information the API response should return. The second argument
                // is the video resource that contains metadata about the new video.
                // The third argument is the actual video content.
                YouTube.Videos.Insert videoInsert = youtube.videos()
                        .insert("snippet,statistics,status", videoObjectDefiningMetadata, mediaContent);

                // Set the upload type and add an event listener.
                MediaHttpUploader uploader = videoInsert.getMediaHttpUploader();

                // Indicate whether direct media upload is enabled. A value of
                // "True" indicates that direct media upload is enabled and that
                // the entire media content will be uploaded in a single request.
                // A value of "False," which is the default, indicates that the
                // request will use the resumable media upload protocol, which
                // supports the ability to resume an upload operation after a
                // network interruption or other transmission failure, saving
                // time and bandwidth in the event of network failures.
                uploader.setDirectUploadEnabled(false);

                MediaHttpUploaderProgressListener progressListener = new MediaHttpUploaderProgressListener() {
                    public void progressChanged(MediaHttpUploader uploader) throws IOException {
                        switch (uploader.getUploadState()) {
                            case INITIATION_STARTED:
                                System.out.println("Initiation Started");
                                break;
                            case INITIATION_COMPLETE:
                                System.out.println("Initiation Completed");
                                break;
                            case MEDIA_IN_PROGRESS:
                                System.out.println("Upload in progress");
                                System.out.println("Upload percentage: " + uploader.getProgress());
                                break;
                            case MEDIA_COMPLETE:
                                System.out.println("Upload Completed!");
                                break;
                            case NOT_STARTED:
                                System.out.println("Upload Not Started!");
                                break;
                        }
                    }
                };
                uploader.setProgressListener(progressListener);

                // Call the API and upload the video.
                Video youtubeVideo = videoInsert.execute();

                // Print data about the newly inserted video from the API response.
                System.out.println("\n================== Returned Video ==================\n");
                System.out.println("  - Id: " + youtubeVideo.getId());
                System.out.println("  - Title: " + youtubeVideo.getSnippet().getTitle());
                System.out.println("  - Tags: " + youtubeVideo.getSnippet().getTags());
                System.out.println("  - Privacy Status: " + youtubeVideo.getStatus().getPrivacyStatus());
                System.out.println("  - Video Count: " + youtubeVideo.getStatistics().getViewCount());

            } catch (GoogleJsonResponseException e) {
                System.err.println("GoogleJsonResponseException code: " + e.getDetails().getCode() + " : "
                        + e.getDetails().getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                System.err.println("IOException: " + e.getMessage());
                e.printStackTrace();
            } catch (Throwable t) {
                System.err.println("Throwable: " + t.getMessage());
                t.printStackTrace();
            }
        }
    }
    public static Credential authorize(List<String> scopes, String credentialDatastore) throws IOException {

        // Load client secrets.
        Reader clientSecretReader = new InputStreamReader(Auth.class.getResourceAsStream("/client_secrets.json"));
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, clientSecretReader);

        // Checks that the defaults have been replaced (Default = "Enter X here").
        if (clientSecrets.getDetails().getClientId().startsWith("Enter")
                || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
            System.out.println(
                    "Enter Client ID and Secret from https://console.developers.google.com/project/_/apiui/credential "
                            + "into src/main/resources/client_secrets.json");
            System.exit(1);
        }

        // This creates the credentials datastore at ~/.oauth-credentials/${credentialDatastore}
        FileDataStoreFactory fileDataStoreFactory = new FileDataStoreFactory(new File(System.getProperty("user.home") + "/" + CREDENTIALS_DIRECTORY));
        DataStore<StoredCredential> datastore = fileDataStoreFactory.getDataStore(credentialDatastore);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, scopes).setCredentialDataStore(datastore)
                .build();

        // Build the local server and bind it to port 8080
        LocalServerReceiver localReceiver = new LocalServerReceiver.Builder().setPort(8080).build();

        // Authorize.
        return new AuthorizationCodeInstalledApp(flow, localReceiver).authorize("user");
    }*/


    public class YouTubeUploadTask extends AsyncTask<Void, Void, Void> {

        private static final String VIDEO_FILE_FORMAT = "video/*";

        public static final String scope = "oauth2:https://www.googleapis.com/auth/youtube";
        private final List<String> scopes = Lists.newArrayList(YouTubeScopes.YOUTUBE);

        //  private String videoFileName = "/storage/sdcard1/DCIM/Camera/VID_20160905_125329.mp4";
        private String appName = "GCLife Test";
        private String title = titleEdit.getText().toString();
        private String description = "";

        private HttpTransport transport = AndroidHttp.newCompatibleTransport();
        private com.google.api.client.json.JsonFactory jsonFactory = new GsonFactory();

        private MediaHttpUploader uploader = null;


        private String errorMessage = null;

        public YouTube.Videos.Insert prepareUpload() {
            try {

                File videoFile = new File(videoFileName);

                // Add extra information to the video before uploading
                Video videoObjectDefiningMetadata = new Video();

                // Set the video to public (default)
                VideoStatus status = new VideoStatus();
                status.setPrivacyStatus("public");
                videoObjectDefiningMetadata.setStatus(status);

                // Set metadata with the VideoSnippet object
                VideoSnippet snippet = new VideoSnippet();

                // Video title and description
                snippet.setTitle(title);
                snippet.setDescription(description);

                // Set keywords
                List<String> tags = new ArrayList<String>();
                tags.add(appName);
                snippet.setTags(tags);

                // Set completed snippet to the video object
                videoObjectDefiningMetadata.setSnippet(snippet);

                InputStreamContent mediaContent = new InputStreamContent(VIDEO_FILE_FORMAT, new BufferedInputStream(new FileInputStream(videoFile)));
                mediaContent.setLength(videoFile.length());

                GoogleAccountCredential credential = buildGoogleAccountCredential();
                YouTube youtube = new YouTube.Builder(transport, jsonFactory, credential).setApplicationName(appName).build();

                YouTube.Videos.Insert videoInsert = youtube.videos().insert("snippet,statistics,status", videoObjectDefiningMetadata, mediaContent);

                uploader = videoInsert.getMediaHttpUploader();

            /*
             * Sets whether direct media upload is enabled or disabled. True = whole media content is
             * uploaded in a single request. False (default) = resumable media upload protocol to upload
             * in data chunks.
             */
                uploader.setDirectUploadEnabled(true);
                MediaHttpUploaderProgressListener progressListener = new MediaHttpUploaderProgressListener() {
                    public void progressChanged(MediaHttpUploader uploader) throws IOException {
                        switch (uploader.getUploadState()) {
                            case INITIATION_STARTED:
                                System.out.println("Initiation Started");
//                                Toast.makeText(MainActivity.this, "Initiation Started", Toast.LENGTH_LONG).show();

                                break;
                            case INITIATION_COMPLETE:
                                System.out.println("Initiation Completed");
//                                Toast.makeText(MainActivity.this, "Initiation Completed", Toast.LENGTH_LONG).show();
                                break;
                            case MEDIA_IN_PROGRESS:
                                System.out.println("Upload in progress");
                                System.out.println("Upload percentage: " + uploader.getProgress());
//                                Toast.makeText(MainActivity.this, "Upload percentage:", Toast.LENGTH_LONG).show();
                          //      if (youtubeUploadTask != null) {
                                    try {
                                        int progress = (int) (getUploader().getProgress() * 100);
                                        if (progress < 10) {
                                            //   activity.getTextViewProgress().setText(" 0" + progress + "%");
                                            Log.d("PROGRESS :", " 0" + progress + "%");
                                        } else if (progress < 100) {
                                            //   activity.getTextViewProgress().setText(" " + progress + "%");
                                            Log.d("PROGRESS :", " " + progress + "%");
                                        } else {
                                            //  activity.getTextViewProgress().setText(progress + "%");
                                            Log.d("PROGRESS :", progress + "%");
                                        }
                                        Log.d("PROGRESS :", progress + "");
                                        //   activity.getProgressBarUploadVideo().setProgress(progress);
                                    } catch (IOException e) {

                                    }
                            //    }
                                break;
                            case MEDIA_COMPLETE:
                                System.out.println("Upload Completed!");
//                                Toast.makeText(MainActivity.this, "Upload Completed!", Toast.LENGTH_LONG).show();
//                                Toast.makeText(YoutubeUpload.this, "videoUploadCompleted", Toast.LENGTH_LONG).show();
                                break;
                            case NOT_STARTED:
                                System.out.println("Upload Not Started!");
                                break;
                        }
                    }
                };
                uploader.setProgressListener(progressListener);
                // Set chunk size. See http://stackoverflow.com/questions/13580109/check-progress-for-upload-download-google-drive-api-for-android-or-java
                uploader.setChunkSize(MediaHttpUploader.MINIMUM_CHUNK_SIZE * 2);

                return videoInsert;
            } catch (GoogleJsonResponseException e) {
                System.err.println("GoogleJsonResponseException code: " + e.getDetails().getCode() + " : "
                        + e.getDetails().getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                System.err.println("IOException: " + e.getMessage());
                e.printStackTrace();
            } catch (Throwable t) {
                System.err.println("Throwable: " + t.getMessage());
                t.printStackTrace();

            }
            return null;
        }

        /**
         * Build the credential to authorize the installed application to access user's protected data.
         */
        private GoogleAccountCredential buildGoogleAccountCredential() throws Exception {
            GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(YoutubeUpload.this, scopes);
            credential.setBackOff(new ExponentialBackOff());
            credential.setSelectedAccountName(selectedGoogleAccount);
            return credential;
        }

        @Override
        protected Void doInBackground(Void... params) {

            YouTube.Videos.Insert videoInsert = prepareUpload();
            if (videoInsert != null) {
                try {
                    youtubeVideo = videoInsert.execute();
                } catch (IOException e) {
                    //  MessageUtil.sendHandlerMessage(uploadProgressHandler, HandlerMessage.VIDEO_UPLOAD_FAILED);
                }
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            if (errorMessage != null) {
                //  DialogUtil.showExceptionAlertDialog(activity, "Exception", errorMessage);
            } else if (youtubeVideo != null) {
                //  MessageUtil.sendHandlerMessage(uploadProgressHandler, HandlerMessage.VIDEO_UPLOAD_COMPLETED);
                // Print data about the newly inserted video from the API response.
                System.out.println("\n================== Returned Video ==================\n");
                System.out.println("  - Id: " + youtubeVideo.getId());
                System.out.println("  - Title: " + youtubeVideo.getSnippet().getTitle());
                System.out.println("  - Tags: " + youtubeVideo.getSnippet().getTags());
                System.out.println("  - Privacy Status: " + youtubeVideo.getStatus().getPrivacyStatus());
                System.out.println("  - Video Count: " + youtubeVideo.getStatistics().getViewCount());

                youtubeUrlTxt.setText("Youtube URL : https://www.youtube.com/watch?v=" + youtubeVideo.getId() + "  \n\n" + "Title: " + youtubeVideo.getSnippet().getTitle());
                titleEdit.setText("");
                uploadVideobtn.setText("Video has been uploaded!");
            }
        }

        public Video getUploadedVideo() {
            return youtubeVideo;
        }

         MediaHttpUploader getUploader() {
            return uploader;
        }

    }

    private void redirectAndPlayVideo() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + youtubeVideo.getId())));
        Log.i("Video", "Video Playing....");
    }
    public class UploadProgressHandler{


    }
}
