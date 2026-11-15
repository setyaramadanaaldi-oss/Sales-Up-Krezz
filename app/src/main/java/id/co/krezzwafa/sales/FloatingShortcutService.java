package id.co.krezzwafa.sales;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

/** Persistent GoFood-style draggable KrezzWafa shortcut bubble. */
public class FloatingShortcutService extends Service {
    private static final String CHANNEL_ID = "krezzwafa_floating";
    private WindowManager windowManager;
    private TextView bubble;
    private WindowManager.LayoutParams params;

    @Override public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        Intent open = new Intent(this, MainActivity.class);
        open.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(this, 701, open,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Notification notification = new Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle("KrezzWafa shortcut aktif")
            .setContentText("Tombol mengambang siap membuka dashboard Sales.")
            .setContentIntent(pi)
            .setOngoing(true)
            .build();
        startForeground(701, notification);
        showBubble();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                "KrezzWafa floating shortcut", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Menjaga pintasan KrezzWafa tetap tersedia di layar.");
            NotificationManager nm = getSystemService(NotificationManager.class);
            nm.createNotificationChannel(channel);
        }
    }

    private void showBubble() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            stopSelf();
            return;
        }
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        bubble = new TextView(this);
        bubble.setText("KW");
        bubble.setTextColor(Color.WHITE);
        bubble.setTextSize(15);
        bubble.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        bubble.setGravity(Gravity.CENTER);
        bubble.setContentDescription("Buka KrezzWafa Sales");
        GradientDrawable bg = new GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            new int[] { Color.rgb(245, 35, 43), Color.rgb(150, 0, 16) });
        bg.setShape(GradientDrawable.OVAL);
        bg.setStroke(dp(2), Color.rgb(255, 211, 56));
        bubble.setBackground(bg);
        bubble.setElevation(dp(9));

        int overlayType = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            : WindowManager.LayoutParams.TYPE_PHONE;
        params = new WindowManager.LayoutParams(dp(58), dp(58), overlayType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = dp(12);
        params.y = dp(180);

        bubble.setOnTouchListener(new View.OnTouchListener() {
            private int startX, startY;
            private float downX, downY;
            private long downTime;
            private boolean moved;
            @Override public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = params.x; startY = params.y;
                        downX = event.getRawX(); downY = event.getRawY();
                        downTime = System.currentTimeMillis(); moved = false;
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        int dx = (int)(event.getRawX() - downX);
                        int dy = (int)(event.getRawY() - downY);
                        if (Math.abs(dx) > dp(5) || Math.abs(dy) > dp(5)) moved = true;
                        params.x = Math.max(0, startX + dx);
                        params.y = Math.max(0, startY + dy);
                        try { windowManager.updateViewLayout(bubble, params); } catch (Exception ignored) {}
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (!moved && System.currentTimeMillis() - downTime < 450) {
                            Intent launch = new Intent(FloatingShortcutService.this, MainActivity.class);
                            launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(launch);
                        } else {
                            int screenWidth = getResources().getDisplayMetrics().widthPixels;
                            params.x = params.x + dp(29) < screenWidth / 2 ? dp(5) : screenWidth - dp(63);
                            try { windowManager.updateViewLayout(bubble, params); } catch (Exception ignored) {}
                        }
                        return true;
                }
                return false;
            }
        });
        try { windowManager.addView(bubble, params); } catch (Exception ignored) {}
    }

    private int dp(int value) {
        return (int)(value * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override public void onDestroy() {
        if (bubble != null && windowManager != null) {
            try { windowManager.removeView(bubble); } catch (Exception ignored) {}
            bubble = null;
        }
        super.onDestroy();
    }

    @Override public IBinder onBind(Intent intent) { return null; }
}