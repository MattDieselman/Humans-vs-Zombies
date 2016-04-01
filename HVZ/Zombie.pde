//Zombie class
//Creates an inherited Zombie object from the Vehicle class
//Due to the abstract nature of the vehicle class, the Zombie
//  must implement the following methods:  
//  calcSteeringForces() and display()

class Zombie extends Vehicle {

  //---------------------------------------
  //Class fields
  //---------------------------------------

  //seeking target
  //set to null for now
  PVector target = null;
  Human targetHuman;
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
  Zombie(float x, float y, float r, float ms, float mf, float sd) {

    //call the super class' constructor and pass in necessary arguments
    super(x, y, r, ms, mf, sd);

    //instantiate steeringForce vector to (0, 0)
    steeringForce = new PVector(0, 0);

    //PShape initialization
    //draw the Zombie "pointing" toward 0 degrees
    fill(255);
    img = loadImage("Sprites/Zombie.png");
    /*body=createShape();
     body.beginShape();
     body.vertex(25, 0);
     body.vertex(-10, -10);
     body.vertex(0, 0);
     body.vertex(-10, 10);
     body.endShape(CLOSE);
     */
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
    //This Zombie's target (for now) is the mousev
    PVector pursueForce;
    if (humans.size()>0) { 
      pursueForce = Pursue(calcNearestHuman());
    } else { 
      pursueForce = Pursue(Wander());
    }
    PVector avoidForce = Avoid(objects);
    //add the above seeking force to this overall steering force

    steeringForce.add(PVector.mult(pursueForce, 2));
    steeringForce.add(PVector.mult(avoidForce, 15));
    //limit this Zombie's steering force to a maximum force
    if (position.x<100||position.x>width-100||position.y<100||position.y>height-100) {
      steeringForce.add(PVector.mult(seek(new PVector(width/2, height/2)), 1000));
    }
    steeringForce.limit(maxForce);

    //apply this steering force to the vehicle's acceleration
    applyForce(steeringForce);


    if (debugMode) {
      stroke(0, 255, 0);
      line(position.x, position.y, (steeringForce.x*5000)+position.x, (steeringForce.y*5000)+position.y);
      stroke(0);
    }
    //reset the steering force to 0
    steeringForce.mult(0);
  }

  PVector calcNearestHuman() {
    PVector closestPosition;
    if (humans.size()>0) {
      closestPosition = humans.get(0).position; //Default closestHuman'sPosition (set to 0th human to begin)
    } else {
      closestPosition = new PVector(random(0, width), random(0, height));
    }
    //Human closestHuman = testHuman;
    for (Human hum : humans) {
      PVector vectToObject = PVector.sub(hum.position, this.position);      //Get vector from this zombie to human
      PVector vectToClosest = PVector.sub(closestPosition, this.position);   //Bet vector from this zombie to CLOSEST human
      if (abs(vectToObject.magSq())<=abs(vectToClosest.magSq())) {           //If this human is closest than current Closest Human, reset the closest human to this human
        closestPosition=hum.position.copy();

        //println(targetHuman.futurePosition);
        targetHuman = hum;
      }
    }
    if (debugMode)line(position.x, position.y, closestPosition.x, closestPosition.y);
    return targetHuman.futurePosition;
  }

  //Method: display()
  //Purpose: Finds the angle this Zombie should be heading toward
  //         Draws this Zombie as a triangle pointing toward 0 degreed
  //         All Vehicles must implement display
  void display() {

    //calculate the direction of the current velocity - this is done for you
    float angle = velocity.heading();   

    //draw this vehicle's body PShape using proper translation and rotation
    pushMatrix();
    translate(position.x, position.y);
    rotate(angle);
    image(img, -16, -17);
    popMatrix();
  }

  //--------------------------------
  //Class methods
  //--------------------------------
}