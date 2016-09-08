package com.example.gamesugar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class GameView extends SurfaceView
{
    /**Объект класса GameLoopThread */
    private GameThread mThread;

    public int shotX;
    public int shotY;

    private Bullet Dbullet;
    private Layer layer1;

    Bitmap scene;

    /**Переменная запускающая поток рисования*/
    private boolean running = false;


    //-------------Start of GameThread--------------------------------------------------\\

    public class GameThread extends Thread
    {
        /**Объект класса*/
        private GameView view;

        /**Конструктор класса*/
        public GameThread(GameView view)
        {
            this.view = view;
        }

        /**Задание состояния потока*/
        public void setRunning(boolean run)
        {
            running = run;
        }

        /** Действия, выполняемые в потоке */
        public void run()
        {
            while (running)
            {
                Canvas canvas = null;
                try
                {
                    // подготовка Canvas-а
                    canvas = view.getHolder().lockCanvas();
                    synchronized (view.getHolder())
                    {
                        //отлов коллизий
                        testCollision();
                        // собственно рисование
                        onDraw(canvas);
                    }
                }
                catch (Exception e) { }
                finally
                {
                    if (canvas != null)
                    {
                        view.getHolder().unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }

    //-------------End of GameThread--------------------------------------------------\\

    public GameView(Context context)
    {
        super(context);

        mThread = new GameThread(this);

        Dbullet = createSprite(R.drawable.image);
        layer1 = new Layer(this);


        /*Рисуем все наши объекты и все все все*/
        getHolder().addCallback(new SurfaceHolder.Callback()
        {
            /*** Уничтожение области рисования */
            public void surfaceDestroyed(SurfaceHolder holder)
            {
                boolean retry = true;
                mThread.setRunning(false);
                while (retry)
                {
                    try
                    {
                        // ожидание завершение потока
                        mThread.join();
                        retry = false;
                    }
                    catch (InterruptedException e) { }
                }
            }

            /** Создание области рисования */
            public void surfaceCreated(SurfaceHolder holder)
            {
                mThread.setRunning(true);
                mThread.start();
            }

            /** Изменение области рисования */
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
            {
            }

        });

    }

    /**Создаем персонажа */
    public Bullet createSprite(int resouce) {
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), resouce);
        return new Bullet(this, bmp);
    }


    /**Прикосновение к экрану */
    public boolean onTouchEvent(MotionEvent e)
    {
        shotX = (int) e.getX();
        shotY = (int) e.getY();

        if(e.getAction() == MotionEvent.ACTION_DOWN)
            Dbullet.newTarget(shotX, shotY);
        return true;
    }

    /** Получить сдвиг по х*/
    public int getShiftX(){
        return layer1.shiftX;
    }

    /** Получить сдвиг по y*/
    public int getShiftY(){
        return layer1.shiftY;
    }

    /**Проверка на столкновения*/
    private void testCollision() {

        if (layer1.hasColl(Dbullet.x, Dbullet.y) == 1){
            //чего делаем когда столкнулись
            Dbullet.stepBack();}
        if (layer1.hasColl(Dbullet.x, Dbullet.y) == 2){
            //чего делаем когда чуть задели
            Dbullet.normPos(Dbullet.x+16, Dbullet.y+16);//нормализуем
            if (layer1.hasColl(Dbullet.x, Dbullet.y) == 1){
            //снова проверяем
                Dbullet.stepBack();
                }
            else {
                Dbullet.normPos(Dbullet.x+16, Dbullet.y+16);//нормализуем}
            }
        }
        	/*else

        		if (layer1.hasColl(Dbullet.x, Dbullet.y) == 2){
        			Dbullet.x = (Dbullet.x/32+1)*32;
        	        Dbullet.y = (Dbullet.y/32+1)*32;}
        		if (layer1.hasColl(Dbullet.x, Dbullet.y) == 3){
        			Dbullet.x = (Dbullet.x/32)*32;
        		    Dbullet.y = (Dbullet.y/32+1)*32;}
        	    if (layer1.hasColl(Dbullet.x, Dbullet.y) == 4){
            		Dbullet.x = (Dbullet.x/32+1)*32;
            		Dbullet.y = (Dbullet.y/32)*32;}
            	if (layer1.hasColl(Dbullet.x, Dbullet.y) == 5){
            		Dbullet.x = (Dbullet.x/32)*32;
            		Dbullet.y = (Dbullet.y/32)*32;}	*/
    }

    /**Функция рисующая все спрайты и фон*/
    protected void onDraw(Canvas canvas) {

        int width = getWidth();///32;
        int height = getHeight();///32;

        canvas.drawColor(Color.WHITE);
        //448 704
        if (Dbullet.y<getHeight()-32 && Dbullet.x<getWidth()-32 && Dbullet.y>0 && Dbullet.x>0 ){
            layer1.onDraw(canvas);
            Dbullet.onDraw(canvas);}
        else
        {if (Dbullet.y>=getHeight()-32)
        { layer1.setViewPort(0,getShiftY()+1);
            layer1.onDraw(canvas);
            Dbullet.onDraw(canvas);
        }
            if (Dbullet.y == 0)
            { layer1.setViewPort(getShiftY()-1,0);
                layer1.onDraw(canvas);
                Dbullet.onDraw(canvas);
            }
            if (Dbullet.x>=getWidth()-32)
            { layer1.setViewPort(0,getShiftX()+1);
                layer1.onDraw(canvas);
                Dbullet.onDraw(canvas);
            }
            if (Dbullet.x == 0)
            { layer1.setViewPort(0,getShiftX()-1);
                layer1.onDraw(canvas);
                Dbullet.onDraw(canvas);
            }
        }

        //player.onDraw(canvas);
    }

}
