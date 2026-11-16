package id.aldi.life;
import android.app.*; import android.content.*; import android.os.Build;
public class ReminderReceiver extends BroadcastReceiver {
 @Override public void onReceive(Context c, Intent i) {
  String title=i.getStringExtra("title"); if(title==null) title="Pengingat Aldi Life";
  NotificationManager nm=(NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);
  if(Build.VERSION.SDK_INT>=26) nm.createNotificationChannel(new NotificationChannel("aldi_reminders","Pengingat",NotificationManager.IMPORTANCE_HIGH));
  Notification n=new Notification.Builder(c,"aldi_reminders").setSmallIcon(android.R.drawable.ic_lock_idle_alarm).setContentTitle(title).setContentText("Waktunya menjalankan jadwalmu.").setAutoCancel(true).build();
  nm.notify((int)(System.currentTimeMillis()%100000),n);
 }
}
