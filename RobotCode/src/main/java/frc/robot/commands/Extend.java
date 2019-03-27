package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;

public class Extend extends Command {

    public static boolean isExtended = true;

    public Extend() {
        this.requires(Robot.extender);
        this.setTimeout(0.75d);
    }

    @Override
    protected boolean isFinished() {
        return this.isTimedOut();
    }

    protected void initialize() {
        Robot.extender.extend();
    }

    protected void execute() {
        // Do nothing for now
    }
}