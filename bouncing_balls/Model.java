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
        int random = (int) (10+Math.random()*50);
        balls[0] = new Ball(width / 3, height * 0.9, 1.2, 1.6, 0.2,10 );
        int random2 = (int) (10+Math.random()*50);
        balls[1] = new Ball(2 * width / 3, height * 0.7, -0.6, 0.6, 0.3, 10);
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

            //Calculates the  velocity for both of the  balls
            double velocityball1 = Math.sqrt(b.vx* b.vx + b.vy*b.vy);
            double velocityball2 = Math.sqrt(otherBall.vx* otherBall.vx
                    + otherBall.vy * otherBall.vy);
            double[] velocitys = {velocityball1,velocityball2};

            //Calculate the  tehta angle for both balls
            double theta1 = Math.atan(b.vx/b.vy);
            double theta2 = Math.atan(otherBall.vx/otherBall.vy);
            double[] theta ={theta1,theta2};

            //Convert the  matrix both of the balls
            double[] v1 =  RectToPolar(velocitys[0],theta[0]);
            double[] v2 =  RectToPolar(velocitys[1], theta[1]);


            if(v1[0]<v2[0]) {
                double I = b.mass *v1[0] + otherBall.mass*v2[1];
                double R = -(v2[0]-v1[0]);
                double newvelocity = (I+otherBall.mass*R)/(b.mass+otherBall.mass);
                double newVelocity2 =(I-b.mass*R)/(otherBall.mass*b.mass);

                double[] newv1 = PolarToReact(newvelocity, theta[0]);
                double[] newv2 = PolarToReact(newVelocity2, theta[1]);


                b.set_velocity_x(newv1[0]);
                b.set_velocity_y(newv1[1]);


                otherBall.set_velocity_x(-newv2[0]);
                otherBall.set_velocity_y(-newv2[1]);


            }

        }
    }

    /*
        The  matrix equation for rotating the x-axis
        to get the Polar coordinates of the  matrix
     */
    public double[] RectToPolar(double velocity, double theta) {

        double x = velocity*Math.cos(10);
        double y = velocity*Math.sin(10);

        return new double[]{
                x*Math.cos(theta) + y*Math.sin(theta),
                -x*Math.sin(theta) + y*Math.cos(theta)
        };
    }
    /*
        The method which takes and rotates the values for the Polar coordinates
        and back to the React values.
     */
    public double[] PolarToReact(double velocity, double theta) {

        double x = velocity*Math.cos(10);
        double y = velocity*Math.sin(10);

        return new double[]{
            x*Math.cos(theta)- y*Math.sin(theta),
            x*Math.sin(theta)+ y*Math.cos(theta)
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

        Ball(double x, double y, double vx, double vy, double r, double mass) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.radius = r;
            this.mass = mass;
        }

        /**
         * Position, speed, and radius of the ball. You may wish to add other attributes.
         */
        double x, y, vx, vy, radius, mass;

        public void set_velocity_x( double x ){
            this.vx = x;
        }
        public void set_velocity_y(double y){
            this.vy = y;
        }


    }
}
