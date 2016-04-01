import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class HVZ extends PApplet {


//Beginning to code autonomous agents
//Will implement inheritance with a Vehicle class and a Zombie class


Zombie s;
Object[] objects;
ArrayList<Zombie> zombies;
ArrayList<Human> humans;
Boolean debugMode = false;
boolean breakBool = false;
Boolean tripyMode = false;
Boolean fleeMode = true;
public void setup() {
  
  height=720;
  width=1020;
  //s = new Zombie(width/2, height/2, 20, 4, .1f, 100);
  objects = new Object[5];
  zombies = new ArrayList<Zombie>();
  humans = new ArrayList<Human>();
  for (int i=0; i<objects.length; i++) {
    objects[i] = new Object();

    humans.add(new Human(random(150, width+1), random(150, height+1), 20, 2, .1f, 150));
  }
  zombies.add(new Zombie(random(100, 150), random(0, 150), 20, 1, .02f, 100));
}

public void draw() {
  println();
  if (!tripyMode)background(0, 55, 0);
  fill(0, 255, 0, 40);
  rect(100, 100, 880, 520);
  // Draw an ellipse at the mouse location
  //ellipse(mouseX, mouseY, 32, 32);
  for (int i=0; i<objects.length; i++) {
    objects[i].DrawObject();
  }
  for (Zombie zomb : zombies) {
    zomb.update();
    zomb.display();
    fill(0, 0, 0);
    if (debugMode) {
    }
  }
  for (int i=0; i<humans.size(); i++) {
    humans.get(i).update();
    humans.get(i).display();
    fill(0, 0, 0);
    if (debugMode)text(""+i, humans.get(i).position.x, humans.get(i).position.y);
    println("HUMAN "+i+" POSITION: "+humans.get(i).position);
  }
  CheckCollision();
  //println(debugMode);
  //update the Zombie - done for you
  //s.update();
  //s.display();
}

public void CheckCollision() {
  Zombie zToAdd = new Zombie(0, 0, 20, 4, .1f, 100);
  for (Zombie z : zombies) {
    for (Human h : humans) {
      if ((h.position.x<(z.position.x+30))&&(h.position.x>(z.position.x-30))&&(h.position.y<(z.position.y+30))&&(h.position.y>(z.position.y-30))) {
        zToAdd = (new Zombie(h.position.x, h.position.y, 20, 2, .02f, 100)); 
        humans.remove(h);
        breakBool=true;
        break;
      }
    }
    if (breakBool) {
      breakBool=false;
      zombies.add(zToAdd);
      break;
    }
  }
}

public void keyPressed() {
  if (key == ' ') {
    debugMode = !debugMode;
  }
  if (key == 't') {
    tripyMode = !tripyMode;
  }
  if (key == 'f') {
    fleeMode = !fleeMode;
  }
}

public void mousePressed() {

  if (mouseButton ==LEFT) {
    zombies.add(new Zombie(mouseX, mouseY, 20, 2, .02f, 100));
  }
  if (mouseButton ==RIGHT) {
    humans.add(new Human(mouseX, mouseY, 20, 2, .02f, 100));
  }
}
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
  public void calcSteeringForces() {

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
  public void display() {

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
class Object {
  PVector position;
  float radius;
  PImage image;
  public Object() {
    position = new PVector(random(150, width-149), random(150, height-149)); 
    int randomTreeSelection = (int)random(1, 29);
    switch(randomTreeSelection) {
    case 1:
      image = loadImage("trees/deadtree1.png");
      break;
    case 2:
      image = loadImage("trees/deadtree2.png");
      break;        
    case 3:
      image = loadImage("trees/deadtree3.png");
      break;        
    case 4:
      image = loadImage("trees/foliage1.png");
      break;        
    case 5:
      image = loadImage("trees/foliage2_0.png");
      break;        
    case 6:
      image = loadImage("trees/foliage3_0.png");
      break;        
    case 7:
      image = loadImage("trees/foliage4.png");
      break;        
    case 8:
      image = loadImage("trees/foliage5.png");
      break;        
    case 9:
      image = loadImage("trees/foliage6.png");
      break;        
    case 10:
      image = loadImage("trees/foliage7.png");
      break;        
    case 11:
      image = loadImage("trees/foliage8.png");
      break;        
    case 12:
      image = loadImage("trees/foliage9.png");
      break;        
    case 13:
      image = loadImage("trees/palmtree1.png");
      break;
    case 14:
      image = loadImage("trees/palmtree2.png");
      break;        
    case 15:
      image = loadImage("trees/palmtree3.png");
      break;        
    case 16:
      image = loadImage("trees/palmtree4.png");
      break;        
    case 17:
      image = loadImage("trees/palmtree5.png");
      break;        
    case 18:
      image = loadImage("trees/palmtree6.png");
      break;        
    case 19:
      image = loadImage("trees/tree1_4.png");
      break;        
    case 20:
      image = loadImage("trees/tree2_1.png");
      break;        
    case 21:
      image = loadImage("trees/tree3.png");
      break;
    case 22:
      image = loadImage("trees/tree4.png");
      break;    
    case 23:
      image = loadImage("trees/tree5.png");
      break;    
    case 24:
      image = loadImage("trees/tree6.png");
      break;    
    case 25:
      image = loadImage("trees/tree7.png");
      break;    
    case 26:
      image = loadImage("trees/tree8.png");
      break;    
    case 27:
      image = loadImage("trees/tree9.png");
      break;    
    case 28:
      image = loadImage("trees/tree-strange.png");
      break;
    }
    radius = random(50, 150);
  }

  public void DrawObject() {
    image(image, position.x, position.y);
  }
}
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
  public abstract void calcSteeringForces();
  public abstract void display();

  //--------------------------------
  //Class methods
  //--------------------------------

  //Method: update()
  //Purpose: Calculates the overall steering force within calcSteeringForces()
  //         Applies movement "formula" to move the position of this vehicle
  //         Zeroes-out acceleration 
  public void update() {
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
  public void applyForce(PVector force) {
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
  public PVector seek(PVector target) {

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
  public PVector Pursue(PVector target) {
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
    float change = .5f;//0.25f;
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
  public void calcSteeringForces() {

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

  public PVector calcNearestHuman() {
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
  public void display() {

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
  public void settings() {  size(1080, 720); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--present", "--window-color=#FF0000", "--stop-color=#07F515", "HVZ" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
