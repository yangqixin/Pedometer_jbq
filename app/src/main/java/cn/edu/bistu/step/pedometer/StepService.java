package cn.edu.bistu.step.pedometer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class StepService implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mStepCount;
    private Sensor mStepDetector;
    private float mCount;//步行总数
    private float mDetector;//步行探测器
    private static final int sensorTypeC = Sensor.TYPE_STEP_COUNTER;
    private static final int sensorTypeD = Sensor.TYPE_STEP_DETECTOR;

    public StepService(Context context) {
        super();
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);//通过系统服务获取传感器
        //获取传感器类型
        mStepCount = mSensorManager.getDefaultSensor(sensorTypeC);//总步数
        mStepDetector = mSensorManager.getDefaultSensor(sensorTypeD);//单次有效数据
    }

    public void register(){
        //注册监听，灵敏度fast
        register(mStepCount, SensorManager.SENSOR_DELAY_FASTEST);
        register(mStepDetector, SensorManager.SENSOR_DELAY_FASTEST);
    }
    private void register(Sensor sensor, int rate) {
        mSensorManager.registerListener(this, sensor, rate);
    }

    public void unRegister(){
        //取消注册
        mSensorManager.unregisterListener(this,mStepCount);
        mSensorManager.unregisterListener(this,mStepDetector);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType()==sensorTypeC) {
            setStepCount(event.values[0]);
        }
        if (event.sensor.getType()==sensorTypeD) {
            if (event.values[0]==1.0) {
                mDetector++;
            }
        }
    }

    //设置、获取当前总步数
    public float getStepCount() {
        return mCount;
    }
    private void setStepCount(float count) {
        this.mCount = count;
    }

    //设置、获取当前单次有效步数
    public float getmDetector() {
        return mDetector;
    }
    public void setmDetector(float detector){
        this.mDetector = detector;
    }

}
