package com.example.main.simplemp3_2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.main.simplemp3_2.Adapter.DragListAdapter;


public class DragListView extends ListView {
    private final String TAG = "DragListView";
    private WindowManager windowManager;// Windows窗口控制類
    private WindowManager.LayoutParams windowParams;// 用於控制拖拽項的顯示的參數

    private ImageView dragImageView;// 被拖拽的項(item)，其實就是一個ImageView
    private int dragbeginPosition;// 手指拖動項原始position
    private int dragEndPosition;// 手指點擊準備拖動的時候,當前拖動項在列表中的位置.

    private int dragPoint;// 在當前數據項中的位置
    private int dragYOffset;// 當前視圖和屏幕的距離(這裏只使用了y方向上)

    private int upScrollBounce;// 拖動的時候，開始向上滾動的邊界
    private int downScrollBounce;// 拖動的時候，開始向下滾動的邊界

    private final static int step = 1;// ListView 滑動步伐.

    private int current_Step;// 當前步伐.

    private int temChangId;// 臨時交換id

    private boolean isLock;// 是否上鎖.

    private MyDragListener mMyDragListener;

    public void setLock(boolean isLock) {
        this.isLock = isLock;
    }

    public DragListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 按下
        if (ev.getAction() == MotionEvent.ACTION_DOWN && !isLock) {
            int x = (int) ev.getX();// 獲取相對與ListView的x坐標
            int y = (int) ev.getY();// 獲取相應與ListView的y坐標
            temChangId = dragbeginPosition = dragEndPosition = pointToPosition(x, y);
            // 無效不進行處理
            if (dragEndPosition == AdapterView.INVALID_POSITION) {
                return super.onInterceptTouchEvent(ev);
            }
            // 獲取當前位置的item視圖(可見狀態)
            ViewGroup itemView = (ViewGroup) getChildAt(dragEndPosition - getFirstVisiblePosition());
            // 獲取到的dragPoint其實就是在你點擊指定item項中的高度.
            dragPoint = y - itemView.getTop();
            // 這個值是固定的:其實就是ListView這個控件與屏幕最頂部的距離（一般為標題欄+狀態欄.
            dragYOffset = (int) (ev.getRawY() - y);
            // 獲取可拖拽的圖標
            View draggerView = itemView.findViewById(R.id.imgbtn_song_drag);
            //點擊的x坐標大於移動按鈕的x坐標，就當成是按到了iv_move觸發了移動
            Log.i(TAG, "onInterceptTouchEvent: " + draggerView.getLeft());
            if (draggerView != null && x > draggerView.getLeft() && x < draggerView.getRight()) { //如果想點擊item的任意位置都能進行拖拽，把x > dragger.getLeft()限定去掉就行
                upScrollBounce = getHeight() / 3;// 取得向上滾動的邊際，大概為該控件的1/3
                downScrollBounce = getHeight() * 2 / 3;// 取得向下滾動的邊際，大概為該控件的2/3
                itemView.setBackgroundColor(Color.parseColor("#B4B6B6"));
                Bitmap bitmap = Bitmap.createBitmap(itemView.getWidth(), itemView.getHeight(), Bitmap.Config.ARGB_8888);
                Log.i(TAG, "onInterceptTouchEvent: " + getHeight());
                Canvas c = new Canvas(bitmap);
                itemView.draw(c);
                Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0, itemView.getWidth(), itemView.getHeight());
                startDrag(bm, y);// 初始化影像
            }
            return false;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // item的view不為空，且獲取的dragPosition有效
        if (dragImageView != null && dragEndPosition != INVALID_POSITION && !isLock) {
            int action = ev.getAction();
            switch (action) {
                case MotionEvent.ACTION_UP:
                    Log.i(TAG, "onTouchEvent: " + dragImageView.getLeft());
                    int upY = (int) ev.getY();
                    stopDrag();
                    onDrop(upY);
                    DragListAdapter adapter = (DragListAdapter) getAdapter();
                    adapter.updateSongOrderToFile();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int moveY = (int) ev.getY();
                    onDrag(moveY);
                    break;
                case MotionEvent.ACTION_DOWN:
                    break;
                default:
                    break;
            }
            return true;
        }

        return super.onTouchEvent(ev);
    }

    private void startDrag(Bitmap bm, int y) {
        windowParams = new WindowManager.LayoutParams();
        windowParams.gravity = Gravity.TOP;
        windowParams.x = 0;
        windowParams.y = dragPoint + dragYOffset; //  dragPoint = 控件跟listView距離  +  dragYOffeset  =  狀態列高度 +  Toolbar高度
        windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE// 不需獲取焦點
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE// 不需接受觸摸事件
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON// 保持設備常開，並保持亮度不變。
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        // 窗口占滿整個屏幕，忽略周圍的裝飾邊框（例如狀態欄）。此窗口需考慮到裝飾邊框的內容。

        ImageView imageView = new ImageView(getContext());
        imageView.setImageBitmap(bm);
        windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(imageView, windowParams);
        dragImageView = imageView;
    }

    public void onDrag(int y) {
        int drag_top = y - dragPoint;// 拖拽view的top值不能＜0，否則則出界.
        if (dragImageView != null && drag_top >= 0) {
            windowParams.alpha = 0.65f;
            windowParams.y = y - dragPoint + dragYOffset;
            windowManager.updateViewLayout(dragImageView, windowParams);// 時時移動(拖拽移動的核心)
        }
        // 為了避免滑動到分割線的時候，返回-1的問題
        int tempPosition = pointToPosition(0, y);
        if (tempPosition != INVALID_POSITION) {
            dragEndPosition = tempPosition;
        }

        onChange(y);// 時時交換

        doScroller(y);// listview移動.
    }

    public void doScroller(int y) {
        // ListView需要下滑
        if (y < upScrollBounce) {
            current_Step = step + (upScrollBounce - y) / 10;// 時時步伐
        }// ListView需要上滑
        else if (y > downScrollBounce) {
            current_Step = -(step + (y - downScrollBounce)) / 10;// 時時步伐
        } else {
            current_Step = 0;
        }

        // 獲取你拖拽滑動到位置及顯示item相應的view上（註：可顯示部分）（position）
        View view = getChildAt(dragEndPosition - getFirstVisiblePosition());
        // 真正滾動的方法setSelectionFromTop()
        if (view != null)
            setSelectionFromTop(dragEndPosition, view.getTop() + current_Step);
    }

    public void stopDrag() {
        if (dragImageView != null) {
            windowManager.removeView(dragImageView);
            dragImageView = null;
        }
    }

    private void onChange(int y) {
        // 數據交換
        if (dragEndPosition < getAdapter().getCount()) {
            DragListAdapter adapter = (DragListAdapter) getAdapter();
            if (dragEndPosition != temChangId) {
                adapter.update(temChangId, dragEndPosition);
                temChangId = dragEndPosition;// 將點擊最初所在位置position付給臨時的，用於判斷是否換位.
            }
        }

        // 為了避免滑動到分割線的時候，返回-1的問題
        int tempPosition = pointToPosition(0, y);
        if (tempPosition != INVALID_POSITION) {
            dragEndPosition = tempPosition;
        }

        // 超出邊界處理(如果向上超過第二項Top的話，那麽就放置在第一個位置)
        if (y < getChildAt(0).getTop()) {
            // 超出上邊界
            dragEndPosition = 0;
            // 如果拖動超過最後一項的最下邊那麽就防止在最下邊
        } else if (y > getChildAt(getChildCount() - 1).getBottom()) {
            // 超出下邊界
            dragEndPosition = getAdapter().getCount() - 1;
        }

    }

    public void onDrop(int y) {
        // 數據交換
        if (dragEndPosition < getAdapter().getCount()) {
            DragListAdapter adapter = (DragListAdapter) getAdapter();
            adapter.notifyDataSetChanged();// 刷新.
            Log.d("wbl", "dragEndPosition :" + dragEndPosition);
            Log.d("wbl", "dragbeginPosition :" + dragbeginPosition);
            //換位成功後的回調
            if (mMyDragListener != null) {
                mMyDragListener.onDragFinish(dragbeginPosition, dragEndPosition);
            }
        }
    }

    //換位成功後的回調接口
    interface MyDragListener {
        void onDragFinish(int srcPositon, int finalPosition);
    }

    //設置換位成功後的回調接口
    public void setMyDragListener(MyDragListener listener) {
        mMyDragListener = listener;
    }
}
