package bouncing_balls;

import jdk.jshell.spi.ExecutionControl;

import java.awt.*;

/**
 * The physics model.
 * <p>
 * This class is where you should implement your bouncing balls model.
 * <p>
 * The code has intentionally been kept as simple as possible, but if you wish, you can improve the design.
 *
 * @author Adam Grandén
 */
class Model {

    double areaWidth, areaHeight;
    double GRAVITY = -9.8;

    Ball[] balls;

    Model(double width, double height) {
        areaWidth = width;
        areaHeight = height;

        // Initialize the model with a few balls
        balls = new Ball[2];
        int random = (int) (10 + Math.random() * 50);
        balls[0] = new Ball(width / 3, height * 0.9, 1.2, 1.6, 0.2, 10, Color.GREEN);
        int random2 = (int) (10 + Math.random() * 50);
        balls[1] = new Ball(2 * width / 3, height * 0.7, -0.6, 0.6, 0.3, 10, Color.BLUE);
    }

    void step(double deltaT) {
        // TODO this method implements one step of simulation with a step deltaT
        for (Ball b : balls) {
            for (Ball otherBall : balls) {
                if (b != otherBall) {
                    ballCollidedBall(b, otherBall);
                }
            }
        }

        for (Ball b : balls) {
            double[] ballCollsion = ballCollsionWall(b, deltaT);
            b.x = ballCollsion[0];
            b.y = ballCollsion[1];
        }
    }

    /*

     */
    public double[] ballCollsionWall(Ball b, double deltaT) {
        // detect collision with the border
        if (b.x < b.radius || b.x > areaWidth - b.radius) {
            b.vx *= -1; // change direction of ball
        }
        if (b.y < b.radius || b.y > areaHeight - b.radius) {
            b.vy *= -1;
        }


        // compute new position according to the speed of the ball
        b.x += deltaT * b.vx;
        b.y += deltaT * b.vy;
        //b.y += GRAVITY *deltaT;


        return new double[]{b.x, b.y};
    }

    /*
        The method which checks if the ball collided with another ball. In which if it hit's
        the  velocity
     */
    public void ballCollidedBall(Ball b, Ball otherBall) {
        if (hitOtherBall(b, otherBall)) {


        }
    }

    /*
        The  matrix equation for rotating the x-axis
        to get the Polar coordinates of the  matrix
     */
    public double[] RectToPolar(double velocity, double theta) {

        return new double[]{
                x * Math.cos(theta) + y * Math.sin(theta),
                -x * Math.sin(theta) + y * Math.cos(theta)
        };
    }

    /*
        The method which takes and rotates the values for the Polar coordinates
        and back to the React values.
     */
    public double[] PolarToReact(double velocity, double theta) {

        return new double[]{
                x * Math.cos(theta) - y * Math.sin(theta),
                x * Math.sin(theta) + y * Math.cos(theta)
        };
    }


    /**
     * Check if there is a collision between the balls  by taking the  distance the Square
     * and then see if the radius  is  within the  ball.
     */
    public boolean hitOtherBall(Ball b, Ball otherball) {
        double dx = b.x - otherball.x;
        double yx = b.y - otherball.y;

        double distFromSquare = (dx * dx) + (yx * yx);
        double SumRadius = b.radius + otherball.radius;

        double squaredRadius = SumRadius * SumRadius;
        return distFromSquare <= squaredRadius;
    }


    /**
     * Simple inner class describing balls.
     */
    class Ball {

        Ball(double x, double y, double vx, double vy, double r, double mass, Color color) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.radius = r;
            this.mass = mass;
            this.color = color;
        }

        /**
         * Position, speed, and radius of the ball. You may wish to add other attributes.
         */
        double x, y, vx, vy, radius, mass;
        Color color;

        public void set_velocity_x(double x) {
            this.vx = x;
        }

        public void set_velocity_y(double y) {
            this.vy = y;
        }


    }
}
