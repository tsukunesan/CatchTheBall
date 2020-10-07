package com.example.catchtheball;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private TextView scoreLabel;
    private TextView startLabel;
    private ImageView box;
    private ImageView orange;
    private ImageView pink;
    private ImageView black;
    private ImageView ghost;

    //座標
    private float boxX;
    private float boxY;
    private float orangeX;
    private float orangeY = 2000;
    private float pinkX;
    private float pinkY = 2000;
    private float blackX;
    private float blackY = 2000;
    private float ghostX;
    private float ghostY = 2000;


    // スピード
    private int boxSpeed;
    private int orangeSpeed;
    private int pinkSpeed;
    private int blackSpeed;
    private int ghostSpeed;

    //Handler&Timer
    private Handler handler = new Handler();
    private Timer timer = new Timer();

    // Status
    private boolean action_flg = false;
    private boolean start_flg = false;

    // サイズ
    private int frameHeight;
    private int frameWidth;
    private int boxSize;
    private int screenWidth;
    private int screenHeight;

    // Score
    private int score = 0;

    // Sound
    private SoundPlayer soundPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        soundPlayer = new SoundPlayer(this);

        //各idを取得
        scoreLabel = findViewById(R.id.highScoreLabel);
        startLabel = findViewById(R.id.startLabel);
        box = findViewById(R.id.box);
        orange = findViewById(R.id.orange);
        pink = findViewById(R.id.pink);
        black = findViewById(R.id.black);
        ghost = findViewById(R.id.ghost);

        //screenSize
        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        //スピード設定
        boxSpeed = Math.round(screenWidth / 60f);
        orangeSpeed = Math.round(screenHeight / 60f);
        pinkSpeed = Math.round(screenHeight / 36f);
        blackSpeed = Math.round(screenHeight / 45f);
        ghostSpeed = Math.round(screenHeight / 45f);

        //初期位置を画面外に設定
        orange.setX(500);
        orange.setY(2000);
        pink.setX(500);
        pink.setY(2000);
        black.setX(500);
        black.setY(2000);
        box.setY(1500);
        ghost.setX(500);
        ghost.setY(2000);

        //スコアを0点で初期化
        scoreLabel.setText("Score : 0");
    }

    //backボタン無効
    @Override
    public void onBackPressed() {
    }

    //ゲームをY方向で動かすとき
    public void changePosY() {
        // Orange
        orangeX -= orangeSpeed;
        if (orangeX < 0) {
            orangeX = screenWidth + 20;
            orangeY = randomY(orange.getHeight());
        }
        orange.setX(orangeX);
        orange.setY(orangeY);

        // Black
        blackX -= blackSpeed;
        if (blackX < 0) {
            blackX = screenWidth + 10;
            blackY = randomY(black.getHeight());
        }
        black.setX(blackX);
        black.setY(blackY);

        // Pink
        pinkX -= pinkSpeed;
        if (pinkX < 0) {
            pinkX = screenWidth + 5000;
            pinkY = randomY(pink.getHeight());
        }
        pink.setX(pinkX);
        pink.setY(pinkY);

        //box
        if (action_flg) {
            boxX -= boxSpeed;
        } else {
            boxX += boxSpeed;
        }
        if (boxX < 0) boxX = 0;
        if (boxX > frameWidth - boxSize) boxX = frameHeight - boxSize;
        box.setY(boxY);

        //スコア更新
        scoreLabel.setText("Score : " + score);
    }

    //ゲームをX方向で動かすとき
    public void changePosX() {
        // Orange
        orangeY += orangeSpeed;
        if (orangeY > frameHeight) {
            orangeX = randomX(orange.getWidth());
            orangeY = -20;
        }
        orange.setX(orangeX);
        orange.setY(orangeY);

        // Black
        blackY += blackSpeed;
        if (blackY > frameHeight) {
            blackX = randomX(black.getWidth());
            blackY = -10;
        }
        black.setX(blackX);
        black.setY(blackY);

        // Pink
        pinkY += pinkSpeed;
        if (pinkY > frameHeight) {
            pinkX = randomX(pink.getWidth());
            pinkY = -5000;
        }
        pink.setX(pinkX);
        pink.setY(pinkY);

        //ghost
        ghostY += ghostSpeed;
        if (ghostY > frameHeight) {
            ghostX = randomX(ghost.getWidth());
            ghostY = -10;
        }
        pink.setX(ghostX);
        pink.setY(ghostY);

        //box
        if (action_flg) {
            boxX -= boxSpeed;
        } else {
            boxX += boxSpeed;
        }
        boxY = box.getY();
        if (boxX < 0) boxX = 0;
        if (boxX > frameWidth - boxSize) boxX = frameWidth - boxSize;
        box.setX(boxX);

        Log.d("boxX", String.valueOf(boxX));
        Log.d("boxY", String.valueOf(boxY));

        //スコア更新
        scoreLabel.setText("Score : " + score);
    }

    //衝突判定
    public void hitCheck() {
        // Orange
        float orangeCenterX = orangeX + orange.getWidth() / 2;
        float orangeCenterY = orangeY + orange.getHeight() / 2;
        if (hitStatusX(orangeCenterX, orangeCenterY)) {
            orangeY = 2000.0f;
            score += 10;
            soundPlayer.playHitSound();
        }

        // Pink
        float pinkCenterX = pinkX + pink.getWidth() / 2;
        float pinkCenterY = pinkY + pink.getHeight() / 2;
        if (hitStatusX(pinkCenterX, pinkCenterY)) {
            pinkY = 2000.0f;
            score += 30;
            soundPlayer.playHitSound();
        }

        // ghost
        float ghostCenterX = ghostX + ghost.getWidth() / 2;
        float ghostCenterY = ghostY + ghost.getHeight() / 2;
        if (hitStatusX(ghostCenterX, ghostCenterY)) {
            ghostY = 2000.0f;
            score += 100;
            soundPlayer.playHitSound();
        }

        // Black
        float blackCenterX = blackX + black.getWidth() / 2;
        float blackCenterY = blackY + black.getHeight() / 2;
        if (hitStatusX(blackCenterX, blackCenterY)) {
            soundPlayer.playOverSound();
            // Game Over!
            if (timer != null) {
                timer.cancel();
                timer = null;
            }

            // 結果画面へ
            Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
            intent.putExtra("SCORE", score);
            startActivity(intent);
        }
    }

    //ヒット判定Y
    public boolean hitStatusY(float centerX, float centerY) {
        return (0 <= centerX && centerX <= boxSize &&
                boxY <= centerY && centerY <= boxY + boxSize) ? true : false;
    }

    //ヒット判定X
    public boolean hitStatusX(float centerX, float centerY) {
        return (boxX <= centerX && centerX <= boxX + boxSize &&
                boxY <= centerY && centerY <= screenHeight) ? true : false;
    }

    //ランダムなY座標の取得
    public float randomY(float height) {
        return (float) Math.floor(Math.random() * (frameHeight - height));
    }

    //ランダムなX座標の取得
    public float randomX(float width) {
        return (float) Math.floor(Math.random() * (frameWidth - width));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (start_flg == false) {
            start_flg = true;

            //frameとboxのサイズを取得（onCreateではビューの描写が完了していないのでここで取得）
            FrameLayout frame = findViewById(R.id.frame);
            frameHeight = frame.getHeight();
            frameWidth = frame.getWidth();
            Log.d("frameHeight", String.valueOf(frameHeight));
            Log.d("frameWidth", String.valueOf(frameWidth));
            Log.d("screenHeight", String.valueOf(screenHeight));
            Log.d("screenWidth", String.valueOf(screenWidth));

            boxSize = box.getHeight();
            boxY = frameHeight - boxSize;
            box.setY(frameHeight - boxSize);

            //スタートの文字を完全に消す
            startLabel.setVisibility(View.GONE);

            //box位置の更新タイマー
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            hitCheck();
                            changePosX();
                        }
                    });
                }
            }, 0, 20);
//        } else {
//            if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                action_flg = true;
//            } else if (event.getAction() == MotionEvent.ACTION_UP) {
//                action_flg = false;
//            }
        }
        return true;
    }

    public void rightButton(View view){
        action_flg = false;
    }
    public void leftButton(View view){
        action_flg = true;
    }
}