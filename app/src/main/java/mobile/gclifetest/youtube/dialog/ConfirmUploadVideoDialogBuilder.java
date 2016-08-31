package mobile.gclifetest.youtube.dialog;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;

import mobile.gclifetest.activity.R;
import mobile.gclifetest.youtube.handler.HandlerMessage;
import mobile.gclifetest.youtube.util.MessageUtil;

public class ConfirmUploadVideoDialogBuilder extends Builder {

	private Handler handler = null;

	public ConfirmUploadVideoDialogBuilder(Context context, Handler handler) {
		super(context);
		this.handler = handler;
		this.setCancelable(false);
		this.setMessage(context.getString(R.string.confirmUploadVideo));
		this.setNegativeButton(context.getString(R.string.cancel), null);
		this.setPositiveButton(context.getString(R.string.yes),	new ConfirmUploadVideoDialogOnClickListener());
	}

	private class ConfirmUploadVideoDialogOnClickListener implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			MessageUtil.sendHandlerMessage(handler, HandlerMessage.VIDEO_UPLOAD_START);
		}

	}
}
