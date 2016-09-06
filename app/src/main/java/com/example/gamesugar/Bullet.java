package com.example.gamesugar;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;

public class Bullet
{
    /**Картинка*/
    private Bitmap bmp;

    /**Координаты цели*/
    public int xTarget;
    public int yTarget;

    /**Позиция*/
    public int x;
    public int y;

    /**Координаты цели абсолютные*/
    public int xTargetAbs;
    public int yTargetAbs;

    /**Позиция абсолютная*/
    public int xAbs;
    public int yAbs;

    /**Скорость */
    private int mSpeed=5;

    public double angle;

    /**Текущий кадр = 0*/
    private int currentFrame = 0;
    /**Текущие направление взгляда*/
    private int currentRow = 2;

    /**Ширина*/
    public int width;

    /**Ввыоста*/
    public  int height;

    /**Рядков в спрайте = 4*/
    private static final int BMP_ROWS = 4;

    /**Колонок в спрайте = 3*/
    private static final int BMP_COLUMNS = 3;

    /**кусок спрайта */
    Rect src;
    Rect dst;

    /**Предыдущее положение */
    private int prevX;
    private int prevY;
    private int prevRow = 2;

    public GameView gameView;

    /**Конструктор*/
    public Bullet(GameView gameView, Bitmap bmp) {
        this.gameView=gameView;
        this.bmp=bmp;

        this.x = 416;          //позиция по Х
        this.y = 224;          //позиция по У
        this.xAbs = 416;
        this.yAbs = 224;
        this.xTarget = this.x; //цель по х
        this.yTarget = this.y; //цель по y
        this.xTargetAbs = this.xAbs;
        this.yTargetAbs = this.yAbs;
        this.width = bmp.getWidth()  / BMP_COLUMNS;
        this.height = bmp.getHeight()  / BMP_ROWS;

        this.setPrev();
        //угол полета пули в зависипости от координаты косания к экрану
        //angle = Math.atan((double)(y - gameView.shotY) / (x - gameView.shotX));

    }

    /**Перемещение объекта, его направление*/
    private void update() {
        angle = Math.atan((double)(Math.abs(yAbs - yTargetAbs)) / (Math.abs(xAbs - xTargetAbs)));
        if (Math.abs(xTargetAbs - xAbs) > mSpeed * Math.cos(angle) || Math.abs(yTargetAbs - yAbs) > mSpeed * Math.sin(angle)  ){

            if (xAbs != xTargetAbs){
                if (xAbs<xTargetAbs )
                {xAbs += mSpeed * Math.cos(angle);  // движение по Х со скоростью mSpeed и углу заданном координатой angle
                    // currentFrame = ++currentFrame % BMP_COLUMNS;
                }
                else
                {xAbs -= mSpeed * Math.cos(angle);
                    //currentFrame = ++currentFrame % BMP_COLUMNS;
                }
            }
            if (yAbs != yTargetAbs){
                if (yAbs<yTargetAbs )
                {yAbs += mSpeed * Math.sin(angle);  // движение по У -//-
                }
                else
                {yAbs -= mSpeed * Math.sin(angle);
                }
                //currentFrame = ++currentFrame % BMP_COLUMNS;
            }   currentFrame = ++currentFrame % BMP_COLUMNS;
        } // если мы достаточно близко просто встаем на цель)
        else
        {xAbs = xTargetAbs;
            yAbs = yTargetAbs;
        };

        //переход от бсолютных координат к локальным
        absToLocal();

        //анимация спрайта
        int srcX = currentFrame * width;
        int srcY = currentRow * height;
        src = new Rect(srcX, srcY, srcX + width, srcY + height);
        dst = new Rect(x, y, x + width, y + height);
    }


    /** переход от бсолютных координат к локальным*/
    private void absToLocal(){
        x = xAbs-(gameView.getShiftX()*32);
        y = yAbs-(gameView.getShiftY()*32);
    }

    /** переход от Локальных координат к абсолютным*/
    private void localToAbs(){
        xAbs = x+(gameView.getShiftX()*32);
        yAbs = y+(gameView.getShiftY()*32);
    }

    /**Анимация спрайта */
    /*   private void animSprite(Canvas canvas){
    	 //шаг раз
    	int srcX = currentFrame * width;
        int srcY = currentRow * height;
        src = new Rect(srcX, srcY, srcX + width, srcY + height);
        dst = new Rect(x, y, x + width, y + height);
        canvas.drawBitmap(bmp, src, dst, null);

    	   if (stopAnim == 0) {
           //шаг два
            currentFrame = ++currentFrame % BMP_COLUMNS;
            srcX = currentFrame * width;
            src = new Rect(srcX, srcY, srcX + width, srcY + height);
            canvas.drawBitmap(bmp, src, dst, null);}
       }*/

    /**Запоминаем предыдущее положение спрайта */
    private void setPrev(){
        prevX = this.xAbs;
        prevY = this.yAbs;
        prevRow = this.currentRow;
    }

    /**Отступаем назад */
    public void stepBack(){
        this.xAbs = this.prevX;
        this.yAbs = this.prevY;
        this.xTargetAbs = this.prevX;
        this.yTargetAbs = this.prevY;
        this.currentRow = this.prevRow  ;
        this.currentFrame = ++currentFrame % BMP_COLUMNS;
        absToLocal();

    }

    /**Нормализовать координаты цели по сетке */
    private void normTarget(int inX, int inY){
        int outX;
        int outY;

        if (inX%32 == 0)
            outX = this.xAbs;
        else{
            inX = inX/32;
            outX = inX*32;
        }

        if (inY%32 == 0)
            outY = this.yAbs;
        else{
            inY = inY/32;
            outY = inY*32;
        }

        this.xTargetAbs = outX+(gameView.getShiftX()*32);
        this.yTargetAbs = outY+(gameView.getShiftY()*32);
    }

    /**Нормализовать координаты по сетке */
    public void normPos(int inX, int inY){
        int outX;
        int outY;

        if (inX%32 == 0)
            outX = this.x;
        else{
            inX = inX/32;
            outX = inX*32;
        }

        if (inY%32 == 0)
            outY = this.y;
        else{
            inY = inY/32;
            outY = inY*32;
        }

        this.x = outX;
        this.y = outY;
        localToAbs();
    }

    /**Установка новой цели движения */
    public void newTarget(int newX, int newY){
        this.prevRow = currentRow;

        normTarget(newX, newY);

        if (Math.abs(xTargetAbs - (xAbs+16)) > width/2) //вычисляем куда повернут головой спрайт
        {if (xAbs<xTargetAbs )
            currentRow = 2;//влево
        else
            currentRow = 1;  //вправо
        }
        else
        {if (yAbs<yTargetAbs )
            currentRow = 0; //лицом
        else
            currentRow = 3;  // спиной
        }
    }

    /**Рисуем наши спрайты*/
    public void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(1);
        paint.setStyle(Style.STROKE);

        setPrev();
        update();  //говорим что эту функцию нам нужно вызывать для работы класса
        //animSprite(canvas);
        canvas.drawBitmap(bmp, src, dst, null);
        canvas.drawRect(dst, paint);
        //canvas.drawBitmap(bmp, x, y, null);
    }
}
