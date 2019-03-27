package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;

public class toggleExtension extends Command {

    public static boolean isExtended = true;

    public toggleExtension() {
        this.requires(Robot.extender);
        this.setTimeout(0.75d);
    }

    @Override
    protected boolean isFinished() {
        return this.isTimedOut();
    }

    protected void initialize() {
        if (Extend.isExtended) {
            Robot.extender.extend();
        } else {
            Robot.extender.retract();
        }
    }

    protected void execute() {
        // Do nothing for now
    }
}