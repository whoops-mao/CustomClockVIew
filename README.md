# CustomClockVIew

![img](https://github.com/whoops-mao/CustomClockVIew/blob/master/image-folder/img.gif)

##MeasureSpace
1. MeasureSpec.UNSPECIFIED -> 未指定尺寸
1. MeasureSpec.EXACTLA -> 精确尺寸，控件的宽高指定大小或者为FILL_PARENT
1. MeasureSpec.AT_MOST -> 最大尺寸，控件的宽高为WRAP_CONTENT，控件大小一般随着控件的子空间或内容进行变化，此时控件尺寸只要不超过父控件允许的最大尺寸

##如何使用SurfaceView
1. 继承SurfaceView
2. 实现SurfaceHolder.Callback接口
surfaceCreated：Surface创建后调用，一般做一些初始化工作
surfaceChanged：Surface状态发生变化时调用（例如大小）
surfaceDestroyed：Surface销毁时调用，一般在这里结束绘制线程
3. SurfaceHolder：控制Surface的类，得到画布、提交画布、回调等
4. 绘制和逻辑
