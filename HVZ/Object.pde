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