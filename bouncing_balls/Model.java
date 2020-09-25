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
        balls = new Ball[5];
        balls[0] = new Ball(width / 4, height * 0.5, 0.5, 0.5, 0.2, 10, Color.GREEN);
        balls[1] = new Ball(width / 2, height * 0.5, 1.6, 0.8, 0.4, 20, Color.MAGENTA);
        balls[2] = new Ball(2 * width / 3, height * 0.7, -0.6, 0.6, 0.3, 20, Color.BLUE);
        balls[3] = new Ball(2*width / 4, height * 2, 1.2, 1.6, 0.2, 10, Color.CYAN);
        balls[4] = new Ball(width / 3, height * 0.5, 1.2, 1.6, 0.2, 10, Color.ORANGE);
    }

    void step(double deltaT) {
        // TODO this method implements one step of simulation with a step deltaT
        for (int i = 0; i < balls.length; i++) {
            for (int j = i + 1; j < balls.length; j++) {
                if (hitOtherBall(balls[i], balls[j])) {
                    ballCollidedBall(balls[i], balls[j]);
                    while(hitOtherBall(balls[i], balls[j])){
                        moveBallAfterCollision(balls[i], deltaT);
                        moveBallAfterCollision(balls[j], deltaT);
                    }
                }
            }

        }

        for (Ball b : balls) {
            double[] ballCollsion = ballCollsionWall(b, deltaT);
            b.x = ballCollsion[0];
            b.y = ballCollsion[1];
        }
    }

    /***
     *  The new method that will check if there is a collision to a wall , while being affected by gravity.
     *  Here what was changed is that gravity was added and the  other is that we check the vectors are
     *  negative or positive when  going into the  different walls.
     * @param b
     * @param deltaT
     * @return
     */
    public double[] ballCollsionWall(Ball b, double deltaT) {


        // detect collision with the border and also checks the vector which work is inside the given locations
        if (b.x < b.radius && b.vx < 0 || b.x > areaWidth - b.radius && b.vx > 0) {
            b.vx *= -1; // change direction of ball
        }

        if (b.y < b.radius && b.vy < 0 || b.y > areaHeight - b.radius && b.vy > 0) {
            b.vy *= -1;
        }
        // compute new position according to the speed of the ball
        b.x += deltaT * b.vx;
        b.y += deltaT * b.vy;
        b.vy += GRAVITY * deltaT;
        return new double[]{b.x, b.y};
    }

    /**
     * The method which takes care of the whole ball that  will collided with
     * another ball and then sets the new vx and vy velocity to the  balls.
     * @param b
     * @param otherBall
     */
    public void ballCollidedBall(Ball b, Ball otherBall) {

            double[] ball1 = {b.vx, b.vy};


            double[] ball2 = {otherBall.vx, otherBall.vy};

            //The angle which would be sgima in the equation between the values
            double theta = Math.atan((otherBall.y - b.y) / (otherBall.x - b.x));

            double[] polarVelocity = RectToPolar(ball1);
            double[] polarVelocity2 = RectToPolar(ball2);

            polarVelocity[1] = polarVelocity[1]-theta;
            polarVelocity2[1] = polarVelocity2[1]-theta;


            double[][] Velocitys = VelcocityCalc(polarVelocity, polarVelocity2, b,
                    otherBall);
            double[] newPolarVelocity = Velocitys[0];
            double[] newPolarVelocity2 = Velocitys[1];

            newPolarVelocity[1] = newPolarVelocity[1]+theta;
            newPolarVelocity2[1] = newPolarVelocity2[1]+theta;


            double[] nv1 = PolarToRect(newPolarVelocity);
            double[] nv2 = PolarToRect(newPolarVelocity2);

            b.vx = nv1[0];
            b.vy = nv1[1];

            otherBall.vx = nv2[0];
            otherBall.vy = nv2[1];

    }


    /**
     * A speical case when there are more than 2 balls,
     * if the balls are to close. Then they will move into each other
     * in the case of ball1 and ball2 have been hit then they will intercept
     * ball3 in this case.
     * @param b
     * @param deltaT
     */
    public void moveBallAfterCollision(Ball b, double deltaT) {

        b.x += deltaT * b.vx/150;
        b.y += deltaT * b.vy/150;
    }

    /**
     * The method takes the polar coordinates of the velocity and then
     * converts it back to react coordinates to calculate the  new velocity.
     *  Which then take the x-value and converts it back to the polar coordinates from
     *  the React
     * @param veclocityPolar
     * @param velocity2Polar
     * @param b
     * @param otherball
     * @return new Velocity
     */
    public double[][] VelcocityCalc(double[] veclocityPolar, double[] velocity2Polar, Ball b, Ball otherball) {


        double[] v1 = PolarToRect(veclocityPolar);
        double[] v2 = PolarToRect(velocity2Polar);

        double I = b.mass * v1[0] + otherball.mass * v2[0];
        double R = v2[0] - v1[0];

        double newvelocity = (I + (otherball.mass * R)) / (otherball.mass + b.mass);
        double newvelocity2 = (I - (b.mass * R)) / (otherball.mass + b.mass);
        v1[0] = newvelocity;
        v2[0] = newvelocity2;
        double[]v1Polar =RectToPolar(v1);
        double[]v2Polar =RectToPolar(v2);

        return new double[][]{v1Polar, v2Polar};

    }

    /**
     * Method for taking the velocity from Rect Coordinates and converting them over to Polar Coordinates.
     * @param velocity
     * @return
     */
    public double[] RectToPolar(double[] velocity) {

        double r = Math.sqrt(velocity[0]*velocity[0]+ velocity[1]*velocity[1]);
        double sigma =0;
        if(r >0) {
            sigma = Math.atan(velocity[1] / velocity[0]);
            if(velocity[0]<0){
               sigma = sigma+Math.PI;
            }
        }

        return new double[]{
               r,sigma
        };
    }

    /***
     *  The method which converts to Polar coordinates to React Coordinates in this case.
     * @param Polar
     * @return
     */
    public double[] PolarToRect(double[] Polar) {

        double x = Math.cos(Polar[1])*Polar[0];
        double y = Math.sin(Polar[1])*Polar[0];

        return new double[]{
                x,y
        };
    }


    /**
     * Check if there is a collision between the balls  by taking the  distance the Square
     * and then see if the radius  is  within the  ball.
     */
    public boolean hitOtherBall(Ball b, Ball otherball) {
        double dx = b.x - otherball.x;
        double dy = b.y - otherball.y;

        double distFromSquare = (dx * dx) + (dy * dy);
        double SumRadius = b.radius + otherball.radius;

        double squaredRadius = (SumRadius * SumRadius);
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



    }
}
