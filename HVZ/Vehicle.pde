//Vehicle class
//Specific autonomous agents will inherit from this class 
//Abstract since there is no need for an actual Vehicle object
//Implements the stuff that each auto agent needs: movement, steering force calculations, and display

abstract class Vehicle {

  //--------------------------------
  //Class fields
  //--------------------------------
  //vectors for moving a vehicle
  PVector acceleration, velocity, position, futurePosition;

  //no longer need direction vector - will utilize forward and right
  //these orientation vectors provide a local point of view for the vehicle
  PVector forward, right;

  //floats to describe vehicle movement and size
  float speed, maxSpeed, maxForce, mass, radius, safeDistance, wanderChange;

  //--------------------------------
  //Constructor
  //Vehicle(x position, y position, radius, max speed, max force)
  //--------------------------------
  Vehicle(float x, float y, float r, float ms, float mf, float safeDistance_) {
    //Assign parameters to class fields
    position = new PVector(x, y);
    velocity = new PVector(1, 1);
    acceleration = new PVector(0, 0);
    forward = new PVector(0, 0);
    right = new PVector(0, 0);
    radius = r;
    maxSpeed=ms;
    maxForce=mf;
    mass=1;
    safeDistance = safeDistance_;
    futurePosition = new PVector(1, 1);

    wanderChange=0;
  }

  //--------------------------------
  //Abstract methods
  //--------------------------------
  //every sub-class Vehicle must use these functions
  abstract void calcSteeringForces();
  abstract void display();

  //--------------------------------
  //Class methods
  //--------------------------------

  //Method: update()
  //Purpose: Calculates the overall steering force within calcSteeringForces()
  //         Applies movement "formula" to move the position of this vehicle
  //         Zeroes-out acceleration 
  void update() {
    //calculate steering forces by calling calcSteeringForces()
    calcSteeringForces();
    //add acceleration to velocity, limit the velocity, and add velocity to position
    velocity.add(acceleration);
    velocity.limit(maxSpeed);
    position.add(velocity);
    //calculate forward and right vectors
    forward=velocity.copy().normalize();
    right=forward.copy().rotate(HALF_PI);
    //reset acceleration
    futurePosition = PVector.add(PVector.mult(this.velocity.copy(), 25), this.position.copy());

    if (debugMode) {
      stroke(0, 0, 255);
      line(position.x, position.y, (forward.x*50)+position.x, (forward.y*50)+position.y);
      stroke(255, 0, 0);
      line(position.x, position.y, (right.x*50)+position.x, (right.y*50)+position.y);
      stroke(255, 255, 0);
      fill(255, 255, 0);
      ellipse(futurePosition.x, futurePosition.y, 10, 10);
      stroke(0);
    }

    acceleration.mult(0);
  }


  //Method: applyForce(force vector)
  //Purpose: Divides the incoming force by the mass of this vehicle
  //         Adds the force to the acceleration vector
  void applyForce(PVector force) {
    acceleration.add(PVector.div(force, mass));
  }

  //Wander Method
  //public PVector Wander(){

  //}


  //--------------------------------
  //Steering Methods
  //--------------------------------

  //Method: seek(target's position vector)
  //Purpose: Calculates the steering force toward a target's position
  PVector seek(PVector target) {

    //write the code to seek a target!

    //first get desired velocity
    PVector desiredVelocity = PVector.sub(target, position);
    desiredVelocity.normalize();
    desiredVelocity.mult(maxSpeed);
    PVector steeringForce = PVector.sub(desiredVelocity, velocity);
    steeringForce.limit(maxForce);
    return steeringForce;
  }

  //Persue Method
  PVector Pursue(PVector target) {
    println(target);
    PVector desiredVelocity;
    //try {
    desiredVelocity = PVector.sub(target, position);
    println("PURSUING: "+desiredVelocity);
    // }
    // catch(NullPointerException npe) {
    //      println("PURSUING: "+velocity);
    //      return velocity;
    // }
    desiredVelocity.normalize();
    desiredVelocity.mult(maxSpeed);
    PVector steeringForce = PVector.sub(desiredVelocity, velocity);
    steeringForce.limit(maxForce);
    if (debugMode) {
      stroke(255, 255, 0);
      line(position.x, position.y, target.x, target.y);
    }
    return steeringForce;
  }

  //Method Flee
  //Purpose - Calcs the steering force away from target position
  public PVector Flee(ArrayList<Zombie> zombies) {

    for (Zombie zomb : zombies) {

      //Make new distance PVector
      PVector vectToObject = PVector.sub(zomb.position, position);
      //if the distance (squared to save processing power) is less than the safe distance
      if (vectToObject.magSq()<(pow(safeDistance, 2))) {
        PVector desiredVelocity = PVector.sub(zomb.position, position);
        desiredVelocity.mult(-1);
        return desiredVelocity;
      }
    }
    return new PVector(0, 0);
  }

  //Evade Method
  public PVector Evade(ArrayList<Zombie> zombies) {

    for (Zombie zomb : zombies) {

      //Make new distance PVector
      PVector vectToObject = PVector.sub(zomb.position, position);
      //if the distance (squared to save processing power) is less than the safe distance
      if (vectToObject.magSq()<(pow(safeDistance, 2))) {
        PVector desiredVelocity = PVector.sub(zomb.futurePosition, position);
        desiredVelocity.mult(-1);
        if (debugMode) {
          stroke(0, 255, 255);
          line(position.x, position.y, desiredVelocity.x+position.x, desiredVelocity.y+position.y);
        }
        return desiredVelocity;
      }
    }
    return PVector.sub(Wander(), position);
  }

  //Wander Method

  public PVector Wander() {
    float change = .5;//0.25f;
    wanderChange += random(-change, change);
    PVector circleLocation = velocity.copy();
    circleLocation.normalize();
    circleLocation.mult(50);
    circleLocation.add(position);

    PVector circleOffSet = new PVector(50*cos(wanderChange), 50*sin(wanderChange));
    PVector target = PVector.add(circleLocation, circleOffSet);
    if (debugMode)ellipse(target.x, target.y, 10, 10);

    return target;
  }

  public PVector Avoid(Object[] objects) {

    //For all Object obj in objects array
    for (Object obj : objects) {

      //Make new distance PVector
      PVector vectToObject = PVector.sub(obj.position, position);

      //Store projections in this variable
      float projectRight =0;

      //desired velocity defined
      PVector desiredVel = new PVector(0, 0);

      //if the distance (squared to save processing power) is less than the safe distance
      if (vectToObject.magSq()<(pow(safeDistance, 2))) {

        //if the "shadow" of the vector to the object on the foward vector is greater than 0 (it is infront)
        if (PVector.dot(vectToObject, forward)>0) {
          //get the projection on the right
          projectRight = PVector.dot(vectToObject, right);
          if (abs(projectRight)<radius+obj.radius) {
            if (projectRight>0) {
              desiredVel = this.right.copy().mult(-1).mult(maxSpeed);
            } else {
              desiredVel = this.right.copy().mult(maxSpeed);
            }
            return PVector.sub(desiredVel, velocity);
          }
        }
      }
    }
    return new PVector(0, 0);
  }
}