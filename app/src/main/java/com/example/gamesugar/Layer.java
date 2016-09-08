package com.example.gamesugar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;

public class Layer {
	/**Карта коллизий видимая */
	public int mapColl[][] = new int [15][23];
	/**карта пола видимая*/
	public int mapFlor[][] = new int[15][23];

	/** сдвиг по х и y*/
	public int shiftX;
	public int shiftY;

	/** Полные карты уровня*/
	private int mapCollAll[][] = new int [30][23];
	private int mapFlorAll[][] = new int [30][23];

	/** Размеры уровня в пикселях*/
	public int height;
	public int width;

	/** Массив текстур */
	private Bitmap textrList[] = new Bitmap[5];

	public GameView gameView;

	/**Конструктор*/
	public Layer(GameView gameView){
		//текстуры
		textrList[2] = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.flor1);
		textrList[1] = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.wall1);

		//размеры уровня
		height=30*32;
		width=23*32;

	/*сдвиг по х и y*/
		shiftX=0;
		shiftY=0;

		//пол
		int k = 0; //элемент в массиве ресурса
		int[] resflor = gameView.getResources().getIntArray(R.array.mapFlor1); //чтение карты из ресурса
		for (int i=0;i<30;i++)
			for (int j=0;j<23;j++){
				mapFlorAll[i][j] = resflor[k];
				k++;
			}

		//стены и непроходимые объекты
		k =0;
		int[] rescoll = gameView.getResources().getIntArray(R.array.mapColl1); //чтение карты из ресурса
		for (int i=0;i<30;i++)
			for (int j=0;j<23;j++){
				mapCollAll[i][j] = rescoll[k];
				k++;
			}
		//устанавливаем виев порт
		setViewPort(0,0);
	}

	public int hasColl(int x,int y){
		//левый верхний угол спрайта
		int i1 = (y+16)/32;
		int j1 = (x+1)/32;
		//правый верхний угол спрайта
		int i2 = (y+16)/32;
		int j2 = (x+31)/32;
		//левый нижний угол спрайта
		int i3 = (y+31)/32;
		int j3 = (x+1)/32;
		//правый нижний угол спрайта
		int i4 = (y+31)/32;
		int j4 = (x+31)/32;

		//не задели
		if (mapColl[i1][j1] == 0 && mapColl[i2][j2] == 0 && mapColl[i3][j3] == 0 && mapColl[i4][j4] == 0)
			return 0;
		else
			//шкрябаем углом
			if (mapColl[i1][j1] == 1 && (mapColl[i3][j3]+mapColl[i2][j2]+mapColl[i4][j4] == 0 ))
				return 2;
		if (mapColl[i2][j2] ==1  && (mapColl[i3][j3]+mapColl[i1][j1]+mapColl[i4][j4] == 0 ))
			return 2;
		if (mapColl[i3][j3] == 1 && (mapColl[i1][j1]+mapColl[i2][j2]+mapColl[i4][j4] == 0 ))
			return 2;
		if (mapColl[i4][j4] == 1 && (mapColl[i3][j3]+mapColl[i2][j2]+mapColl[i1][j1] == 0 ))
			return 2;
		//конкретно вляпались
		return 1;
	}

	/** Устоновка области видимости */
	public void setViewPort(int starti, int startj){
		//ограничения не прокручивать за пределы экрана
		//движение по y getWidth() getHeight()
		if (starti<0)
			starti=0;
		if (starti>22)
			starti=22;

		System.arraycopy(mapFlorAll,starti, mapFlor,0,15);
		System.arraycopy(mapCollAll,starti, mapColl,0,15);
		shiftY = startj;

		//движение по x
		if (startj<0)
			startj=0;
		if (startj>16)
			startj=16;

		for (int i = 0;i<mapColl.length;i++){
			for (int j = 0;j<mapColl[i].length;j++){
				mapColl[i][j]=mapCollAll[i+starti][j+startj];
			}
		}

		shiftX = starti;

	}

	/**Рисуем наш уровень*/
	public void onDraw(Canvas canvas) {
		Bitmap txtr;

		//рисуем пол
		for (int i = 0;i<mapFlor.length;i++){
			for (int j = 0;j<mapFlor[i].length;j++){
				txtr = textrList[(mapFlor[i][j])];
				canvas.drawBitmap(txtr, j*32, i*32, null);
			}
		}
		//рисуем стены
		Rect src;
		Paint paint = new Paint();
		paint.setColor(Color.GREEN);
		paint.setStrokeWidth(1);
		paint.setStyle(Style.STROKE);

		for (int i = 0;i<mapColl.length;i++){
			for (int j = 0;j<mapColl[i].length;j++){
				if (mapColl[i][j] != 0){
					txtr = textrList[(mapColl[i][j])];
					canvas.drawBitmap(txtr, j*32, i*32, null);
					src = new Rect(j*32, i*32, 32, 32);
					canvas.drawRect(src, paint);}
			}
		}

	}
}

