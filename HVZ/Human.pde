//Zombie class
//Creates an inherited Zombie object from the Vehicle class
//Due to the abstract nature of the vehicle class, the Zombie
//  must implement the following methods:  
//  calcSteeringForces() and display()

class Human extends Vehicle {

  //---------------------------------------
  //Class fields
  //---------------------------------------

  //seeking target
  //set to null for now

  //PShape to draw this Zombie object
  PShape body;
  PImage img;

  //overall steering force for this Zombie accumulates the steering forces
  //  of which this will be applied to the vehicle's acceleration
  PVector steeringForce;

  float safeZone = 50;
  //---------------------------------------
  //Constructor
  //Zombie(x position, y position, radius, max speed, max force)
  //---------------------------------------
  public Human(float x, float y, float r, float ms, float mf, float sd) {

    //call the super class' constructor and pass in necessary arguments
    super(x, y, r, ms, mf, sd);

    //instantiate steeringForce vector to (0, 0)
    steeringForce = new PVector(0, 0);

    //PShape initialization
    //draw the Zombie "pointing" toward 0 degrees
    //body=createShape(RECT, -15, -15, 30, 30);
    img = loadImage("Sprites/Human.png");
  }


  //--------------------------------
  //Abstract class methods
  //--------------------------------

  //Method: calcSteeringForces()
  //Purpose: Based upon the specific steering behaviors this Zombie uses
  //         Calculates all of the resulting steering forces
  //         Applies each steering force to the acceleration
  //         Resets the steering force
  void calcSteeringForces() {

    //get the steering force returned from calling seek
    //This Zombie's target (for now) is the mouse
    PVector avoidForce = Avoid(objects);
    PVector evadeForce = Evade(zombies);
    //add the above seeking force to this overall steering force
    if (fleeMode)steeringForce.add(PVector.mult(evadeForce, 5));
    steeringForce.add(PVector.mult(avoidForce, 200));
    //limit this Zombie's steering force to a maximum force
    if (position.x<100||position.x>width-100||position.y<100||position.y>height-100) {
      steeringForce.add(PVector.mult(seek(new PVector(width/2, height/2)), 50000));
    }
    steeringForce.limit(maxForce);
    //apply this steering force to the vehicle's acceleration
    applyForce(steeringForce);
    if (debugMode) {
      stroke(0, 255, 0);
      line(position.x, position.y, (steeringForce.x*500)+position.x, (steeringForce.y*500)+position.y);
      stroke(0);
    }
    //reset the steering force to 0
    steeringForce.mult(0);
  }


  //Method: display()
  //Purpose: Finds the angle this Zombie should be heading toward
  //         Draws this Zombie as a triangle pointing toward 0 degreed
  //         All Vehicles must implement display
  void display() {

    //calculate the direction of the current velocity - this is done for you
    float angle = velocity.heading();   
    //if (debugMode) 

    //draw this vehicle's body PShape using proper translation and rotation
    pushMatrix();
    translate(position.x, position.y);
    rotate(angle);
    image(img, -15, -18);
    popMatrix();
  }
}