package com.nearby.app;

import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.nearby.app.Utils.ImageCompressAsyncTask;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.tomergoldst.tooltips.ToolTipsManager;

import java.util.ArrayList;

import static com.nearby.app.Utils.KeyboardUtils.hideKeyboard;
import static com.nearby.app.Utils.KeyboardUtils.showSnackbar;

public class ChatRoomActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private static final int REQUEST_RESOLVE_ERROR = 1002;
    private static final int REQUEST_IMAGE_PICKER = 1003;
    private static final String TAG = ChatRoomActivity.class.getSimpleName();

    private static GoogleApiClient mGoogleApiClient;
    private static Message mPubMessage;
    private static MessageListener mMessageListener;

    private String mUsername;
    private static UserObject sCurrentUser;

    private static ArrayList<MessageObject> mMessageObjects;
    private static ArrayList<UserObject> mUserObjects;
    private static ArrayList<Image> mSelectedImages;
    private static boolean mImagesBeingSent;
    private static ImageButton mSubmitButton;
    private static RecyclerView.Adapter mMessageRecyclerAdapter;
    private static RecyclerView.Adapter mUserRecyclerAdapter;
    private static EditText mTextField;

    public static RelativeLayout mRootContainer;

    public static ToolTipsManager toolTipsManager;

    private static Animation mRotateAnimation = new RotateAnimation(0.0f, 360.0f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
            0.5f);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.ChatRoomActivityTheme);
        setContentView(R.layout.activity_chat_room);

        Intent intent = getIntent();

        mUsername = intent.getStringExtra(ProfileActivity.USERNAME_KEY);
        sCurrentUser = new UserObject(mUsername);


        mMessageObjects = new ArrayList<>();
        mUserObjects = new ArrayList<>();
        mSelectedImages = new ArrayList<>();

        mRootContainer = (RelativeLayout) findViewById(R.id.chatroom_layout);

        toolTipsManager = new ToolTipsManager();

        setUpUsersViews();

        setUpMessagesViews();

        setUpMessageSendViews();

        buildGoogleApiClient();

        buildMessageListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient.isConnected()) {
            if (mPubMessage != null) {
                unpublish();
            }
            unsubscribe();
            mGoogleApiClient.disconnect();
        }
        mUserRecyclerAdapter.notifyItemRangeRemoved(0, mUserObjects.size());
        mUserObjects.clear();
        mSelectedImages.clear();
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_RESOLVE_ERROR:
                if (resultCode == RESULT_OK) {
                    mGoogleApiClient.connect();
                } else {
                    Log.e(TAG, "GoogleApiClient connection failed. Unable to resolve.");
                }
                break;
            case REQUEST_IMAGE_PICKER:
                if (resultCode == RESULT_OK && data !=null){
                    ArrayList<Image> selectedImages = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
                    mSelectedImages.addAll(selectedImages);
                    toggleTextEntryField(false);
                } else {
                    Log.e(TAG, "Unable to get the selected Images from the Image Picker");
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "GoogleApiClient connection failed");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        subscribe();
        if (!sCurrentUser.getmDisplayName().isEmpty() && !sCurrentUser.getmDisplayName().isEmpty()){
            publishHelloMessage(sCurrentUser);
        }
        if (mSelectedImages != null) {
            if (!mSelectedImages.isEmpty()) {
                for (Image image : mSelectedImages) {
                    mImagesBeingSent = true;
                    mSubmitButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_loop_black_24dp));
                    mSubmitButton.startAnimation(mRotateAnimation);

                    ImageCompressAsyncTask imageCompressAsyncTask = new ImageCompressAsyncTask();
                    String[] params = { image.getPath(), mUsername, ImageCompressAsyncTask.TRUE };
                    imageCompressAsyncTask.execute(params);
                }
                mSelectedImages.clear();
            }
        }
    }


    private void setUpUsersViews(){
        RecyclerView mUsersRecyclerView = (RecyclerView) findViewById(R.id.user_view);

        RecyclerView.LayoutManager mUsersLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mUsersRecyclerView.setLayoutManager(mUsersLayoutManager);

        mUserRecyclerAdapter = new UserAdapter(mUserObjects);
        mUsersRecyclerView.setAdapter(mUserRecyclerAdapter);
    }

    private void setUpMessagesViews(){
        RecyclerView mMessagesRecyclerView = (RecyclerView) findViewById(R.id.message_view);
        mMessagesRecyclerView.setHasFixedSize(true);
        mTextField = findViewById(R.id.message_entry_field);

        RecyclerView.LayoutManager mMessagesLayoutManager = new LinearLayoutManager(this);
        mMessagesRecyclerView.setLayoutManager(mMessagesLayoutManager);

        mMessageRecyclerAdapter = new MessageAdapter(mMessageObjects);
        mMessagesRecyclerView.setAdapter(mMessageRecyclerAdapter);
    }

    private void setUpMessageSendViews() {
        ImageView mImagePickerButton = findViewById(R.id.add_image_button);
        mSubmitButton = findViewById(R.id.send_message_button);

        mRotateAnimation.setInterpolator(new LinearInterpolator());
        mRotateAnimation.setDuration(500);
        mRotateAnimation.setRepeatCount(Animation.INFINITE);

        mImagePickerButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Intent intent = new Intent(ChatRoomActivity.this, ImagePickerActivity.class);
                intent.putExtra(ImagePickerActivity.INTENT_EXTRA_MODE, ImagePickerActivity.MODE_SINGLE);
                intent.putExtra(ImagePickerActivity.INTENT_EXTRA_SHOW_CAMERA, false);
                startActivityForResult(intent, REQUEST_IMAGE_PICKER);
                return false;
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageBody = mTextField.getText().toString();

                if (!messageBody.isEmpty() && !messageBody.matches("^(\\s+)$")){
                    mSubmitButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_loop_black_24dp));
                    mSubmitButton.startAnimation(mRotateAnimation);

                    publishMessage(new MessageObject(mUsername, messageBody, MessageObject.MESSAGE_CONTENT_TEXT, true));

                    hideKeyboard(ChatRoomActivity.this, view);
                    mTextField.setText("");
                    mTextField.clearFocus();
                } else {
                    showSnackbar("message is empty");
                }
            }
        });
    }

    private void buildGoogleApiClient() {
        if (mGoogleApiClient != null) {
            return;
        }
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }


    private void buildMessageListener(){
        mMessageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
                if (message.getType().equals(MessageObject.MESSAGE_TYPE)) {
                    displayMessageInChat(message, false);
                } else if(message.getType().equals(UserObject.MESSAGE_TYPE)){
                    addUserToUsersContainer(UserObject.fromNearbyMessage(message));
                }
            }
            @Override
            public void onLost(Message message) {
                if (message.getType().equals(MessageObject.MESSAGE_TYPE)) {
                    removeMessageOnLost(message);
                } else if (message.getType().equals(UserObject.MESSAGE_TYPE)){
                    removeUserFromUsersContainer(UserObject.fromNearbyMessage(message));
                }
            }
        };
    }

    private void subscribe(){
        Nearby.Messages.subscribe(mGoogleApiClient, mMessageListener)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            if (mImagesBeingSent){
                                showSnackbar("fdfdsafsa");
                                mImagesBeingSent = false;
                            } else {
                                showSnackbar("Connected successfully");
                            }
                        } else {
                            showSnackbar("Connection failed");
                        }
                    }
                });
    }

    public static void publishMessage(final MessageObject messageObject) {
        mPubMessage = MessageObject.newNearbyMessage(messageObject);
        toggleTextEntryField(true);
        Nearby.Messages.publish(mGoogleApiClient, mPubMessage)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()){
                            displayMessageInChat(mPubMessage, true);
                        } else {
                            showSnackbar("publish failed");
                        }
                        resetMessageSendAnimation();
                        mSubmitButton.setImageDrawable(ContextCompat
                                .getDrawable(ProfileActivity.mainContext,R.drawable.ic_send_black_24dp));
                    }
                });
    }

    private void publishHelloMessage(final UserObject userObject){
        mPubMessage = UserObject.newNearbyMessage(userObject);
        Nearby.Messages.publish(mGoogleApiClient, mPubMessage)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()){
                            addUserToUsersContainer(userObject);
                        } else {
                            Log.e(TAG, "Could not send user type identifier: " + status);
                        }
                    }
                });
    }

    private void unsubscribe() {
        Nearby.Messages.unsubscribe(mGoogleApiClient, mMessageListener);
    }

    private void unpublish() {
        Nearby.Messages.unpublish(mGoogleApiClient, mPubMessage);
    }

    private static void displayMessageInChat(Message message, boolean fromUser){
        MessageObject receivedMessage = MessageObject.fromNearbyMessage(message);
        receivedMessage.setFromUser(fromUser);
        if (!mMessageObjects.contains(receivedMessage)) {
            mMessageObjects.add(receivedMessage);
            mMessageRecyclerAdapter.notifyItemInserted(mMessageObjects.size() - 1);
        }
    }

    private void removeMessageOnLost(Message message){
        MessageObject lostMessage = MessageObject.fromNearbyMessage(message);
        lostMessage.setFromUser(false);
        mMessageRecyclerAdapter.notifyItemRemoved(mMessageObjects.indexOf(lostMessage));
        mMessageObjects.remove(lostMessage);
    }

    private void removeUserFromUsersContainer(UserObject userObject){
        mUserRecyclerAdapter.notifyItemRemoved(mUserObjects.indexOf(userObject));
        mUserObjects.remove(userObject);
    }

    private void addUserToUsersContainer(UserObject userObject){
        mUserObjects.add(userObject);
        mUserRecyclerAdapter.notifyItemInserted(mUserObjects.size() - 1);

        RecyclerView usersList = mRootContainer.findViewById(R.id.user_view);
        usersList.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
    }

    private static void toggleTextEntryField(boolean enabled){
        mTextField.setFocusable(enabled);
        mTextField.setFocusableInTouchMode(enabled);
    }

    private static void resetMessageSendAnimation(){
        mRotateAnimation.cancel();
        mRotateAnimation.reset();
    }
}
