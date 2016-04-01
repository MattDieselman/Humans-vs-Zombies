
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
void setup() {
  size(1080, 720);
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

void draw() {
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

void CheckCollision() {
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

void keyPressed() {
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

void mousePressed() {

  if (mouseButton ==LEFT) {
    zombies.add(new Zombie(mouseX, mouseY, 20, 2, .02f, 100));
  }
  if (mouseButton ==RIGHT) {
    humans.add(new Human(mouseX, mouseY, 20, 2, .02f, 100));
  }
}