/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

	private RobotMap robot = new RobotMap(RobotType.Chicago_2018);

	private Vision vision = new Vision();

	private UsbCamera visioncam;

	/**
	 * This function is run when the robot is first started up and should be used
	 * for any initialization code.
	 */
	@Override
	public void robotInit() {

		// Setup the camera for the driver
		UsbCamera drivercam = CameraServer.getInstance().startAutomaticCapture(0);
		drivercam.setResolution(480, 240);
		drivercam.setFPS(8);

		// Setup the camera used for vision
		visioncam = CameraServer.getInstance().startAutomaticCapture(1);
		visioncam.setFPS(6);
		visioncam.setBrightness(0);
		visioncam.setWhiteBalanceManual(100000);
		visioncam.setResolution(480, 240);

		// Setup the targeting vision system
		try {
			vision.generateTargetImage(visioncam);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void robotPeriodic() {

	}

	/**
	 * This function is called once each time the robot enters Disabled mode. You
	 * can use it to reset any subsystem information you want to clear when the
	 * robot is disabled.
	 */
	@Override
	public void disabledInit() {

	}

	@Override
	public void disabledPeriodic() {

	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable chooser
	 * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,
	 * remove all of the chooser code and uncomment the getString code to get the
	 * auto name from the text box below the Gyro
	 *
	 * <p>
	 * You can add additional auto modes by adding additional commands to the
	 * chooser code above (like the commented example) or additional comparisons to
	 * the switch structure below with additional strings & commands.
	 */
	@Override
	public void autonomousInit() {

	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {

	}

	@Override
	public void teleopInit() {

	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {

		// Calculate drive code, with turbo button
		double forward = robot.gamepad1.getStickButton(Hand.kLeft) ? robot.gamepad1.getY(Hand.kLeft)
				: robot.gamepad1.getY(Hand.kLeft) / 2,
				turn = robot.gamepad1.getStickButton(Hand.kLeft) ? robot.gamepad1.getX(Hand.kRight)
						: robot.gamepad1.getX(Hand.kRight) / 2;

		// Basic west coast drive code
		if (Math.abs(forward) > 0.05d || Math.abs(turn) > 0.05d) {
			robot.leftDrive1.set(ControlMode.PercentOutput, forward - turn);
			robot.rightDrive1.set(ControlMode.PercentOutput, forward + turn);
		} else {
			robot.leftDrive1.set(ControlMode.PercentOutput, 0);
			robot.rightDrive1.set(ControlMode.PercentOutput, 0);
		}

		SmartDashboard.putNumber("Left drive power", robot.leftDrive1.getMotorOutputPercent());
		SmartDashboard.putNumber("Right drive power", robot.rightDrive1.getMotorOutputPercent());

		// Try to get the center of the line
		try {
			SmartDashboard.putNumber("Center X", vision.findCenterX());
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (robot.gamepad1.getAButtonPressed()) {
			SmartDashboard.putBoolean("Vision tracking", !SmartDashboard.getBoolean("Vision tracking", false));
		}

		if (SmartDashboard.getBoolean("Vision tracking", false)) {
			vision.trackTarget(SmartDashboard.getNumber("Center X", 0), robot.leftDrive1, robot.rightDrive1, .25d);
		}

	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {

	}

}