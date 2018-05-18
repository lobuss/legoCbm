package example;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.ds.fswebcam.FsWebcamDriver;
import ev3dev.actuators.lego.motors.EV3LargeRegulatedMotor;
import ev3dev.sensors.Battery;
import ev3dev.sensors.ev3.EV3TouchSensor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.utility.Delay;
import org.zeroturnaround.zip.ZipUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class MyFirstRobot {

    //Robot Configuration
    private static final EV3TouchSensor touch1 = new EV3TouchSensor(SensorPort.S3);
    private static final EV3LargeRegulatedMotor motor = new EV3LargeRegulatedMotor(MotorPort.C);

    private static final String IMG_FOLDER = "capture";

    private static final int IMG_NUMBER = 24; // Nombre d'image que l'on veut prendre en photo

    private static final int DEGREE_TO_TURN = 360 / IMG_NUMBER;

    // set capture driver for fswebcam tool
    static {
        Webcam.setDriver(new FsWebcamDriver());
    }

    public static void main(String[] args) {
        System.out.println("Checking Battery Voltage: " + Battery.getInstance().getVoltage());

        //To Stop the motor in case of pkill java for example
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                System.out.println("Emergency Stop");
                motor.stop();
            }
        }));

        //Init motor
        motor.brake();
        motor.setSpeed(200);
        motor.resetTachoCount();

        // fait tourner le plateau + capture l 'image
        System.out.println("Go Forward with the motors. Start Position : " +  motor.getTachoCount());
        for(int i = 0; i < IMG_NUMBER; i++) {
            motor.rotateTo(motor.getTachoCount() + DEGREE_TO_TURN, true);
            System.out.println(String.format("Large Motor is moving: %s at speed %d", motor.isMoving(), motor.getSpeed()));
            Delay.msDelay(1000);
            captureFrame(IMG_FOLDER + "/capture_"+ i +".png");
        }

        //Delay.msDelay(5000);
        System.out.println("Stopped motors");

        // On arrete le moteur
        motor.stop();
        System.out.println("Go Forward with the motors. End Position : " +  motor.getTachoCount());
        System.out.println("Battery Voltage: " + Battery.getInstance().getVoltage());


        ZipUtil.pack(new File("/home/robot/" + IMG_FOLDER + "/"), new File("/home/robot/"+ IMG_FOLDER +".zip"));

        System.exit(0);

//        //mA.resetTachoCount();
//        mA.setSpeed(500);
//
//
//        final SampleProvider sp = touch1.getTouchMode();
//        int touchValue = 0;
//
//        //Control loop
//        final int iteration_threshold = 20;
//        for(int i = 0; i <= iteration_threshold; i++) {
//            System.out.println("brake motor");
//            mA.brake();
//            System.out.println("rotate motor");
//            //mA.rotate(degreesToTurn);
//            System.out.println("stop motor");
//            mA.stop();
//            System.out.println("forward motor");
//            //mA.forward();
//            System.out.println("fin motor");
//
//            float [] sample = new float[sp.sampleSize()];
//            sp.fetchSample(sample, 0);
//            touchValue = (int)sample[0];
//
//            System.out.println("Iteration: {}" + i);
//            System.out.println("Touch: {}" + touchValue);
//
//            if(touchValue == 1 ){
//                captureFrame("capture"+ i +".png");
//            }
//
//            Delay.msDelay(1000); // 1 second
//
//
//        }
//        mA.stop();



    }

    private static void captureFrame(String fileName) {
        Webcam webcam = Webcam.getDefault();
        webcam.setCustomViewSizes(new Dimension[] { WebcamResolution.HD720.getSize() }); // register custom size
        webcam.setViewSize(WebcamResolution.HD720.getSize()); // set size
        try {
            webcam.open();
            ImageIO.write(webcam.getImage(), "PNG", new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            webcam.close();
        }

    }
}
